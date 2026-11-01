#!/usr/bin/env python3
"""Minimal RCON client for Minecraft (Source engine protocol)."""
import socket
import struct

SERVERDATA_AUTH = 3
SERVERDATA_EXECCOMMAND = 2
SERVERDATA_AUTH_RESPONSE = 2


class RconError(Exception):
    pass


class Rcon:
    def __init__(self, host="127.0.0.1", port=25575, password="", timeout=120):
        self.sock = socket.create_connection((host, port), timeout=timeout)
        self.sock.settimeout(timeout)
        self.request_id = 0
        self._auth(password)

    def _send(self, ptype, body=b""):
        self.request_id += 1
        payload = struct.pack("<ii", self.request_id, ptype) + body + b"\x00\x00"
        self.sock.sendall(struct.pack("<i", len(payload)) + payload)

    def _recv_packet(self):
        length_raw = self._read_exact(4)
        (length,) = struct.unpack("<i", length_raw)
        data = self._read_exact(length)
        rid, ptype = struct.unpack("<ii", data[:8])
        body = data[8:-2]
        return rid, ptype, body.decode("utf-8", "replace")

    def _read_exact(self, size):
        buf = b""
        while len(buf) < size:
            chunk = self.sock.recv(size - len(buf))
            if not chunk:
                raise RconError("connection closed")
            buf += chunk
        return buf

    def _auth(self, password):
        self._send(SERVERDATA_AUTH, password.encode())
        # First packet may be an empty COMMAND value; read until auth response.
        while True:
            rid, ptype, _ = self._recv_packet()
            if ptype == SERVERDATA_AUTH_RESPONSE:
                if rid == -1:
                    raise RconError("authentication failed")
                return

    def command(self, text):
        self._send(SERVERDATA_EXECCOMMAND, text.encode())
        while True:
            rid, ptype, body = self._recv_packet()
            if ptype == SERVERDATA_AUTH_RESPONSE:
                continue
            return body

    def close(self):
        try:
            self.sock.close()
        except OSError:
            pass
