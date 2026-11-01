#!/usr/bin/env python3
"""Sample luviteium ore density from the generated world region files.

Reads runs/server/region/*.mca (zlib-compressed NBT chunks), counts
`luviteium:luviteium_ore` blocks inside the forceloaded sample batches
written by tools/qa/run_qa.py, and prints per-chunk density plus a Y
histogram. No third-party dependencies.

Run after the server has stopped (region files must not be written to).
"""
import glob
import os
import struct
import sys
import zlib

ORE = "luviteium:luviteium_ore"
REGION_DIR = "runs/server/world/region"

# Forceload sample batches from run_qa.py: chunk ranges (inclusive).
SAMPLE_CHUNK_RANGES = [(-8, 7, -8, 7), (-8, 7, 8, 15),
                       (8, 15, -8, 7), (8, 15, 8, 15)]

# Spec expectation: 3 attempts/chunk, 0.8 air-exposure discard -> 0.6/chunk.
EXPECTED_PER_CHUNK = 3 * 0.2


# --------------------------------------------------------------- mini NBT
class Reader:
    def __init__(self, buf):
        self.b = buf
        self.i = 0

    def take(self, fmt):
        size = struct.calcsize(">" + fmt)
        vals = struct.unpack_from(">" + fmt, self.b, self.i)
        self.i += size
        return vals if len(vals) > 1 else vals[0]

    def payload(self, tag):
        if tag == 1:
            return self.take("b")
        if tag == 2:
            return self.take("h")
        if tag == 3:
            return self.take("i")
        if tag == 4:
            return self.take("q")
        if tag == 5:
            return self.take("f")
        if tag == 6:
            return self.take("d")
        if tag == 7:
            n = self.take("i")
            v = self.b[self.i:self.i + n]
            self.i += n
            return v
        if tag == 8:
            return self.take("%ds" % self.take("h")).decode("utf-8", "replace")
        if tag == 9:
            et, n = self.take("bi")
            return [self.payload(et) for _ in range(n)]
        if tag == 10:
            out = {}
            while True:
                t = self.take("b")
                if t == 0:
                    return out
                # Read the key first, then the value: never rely on the
                # evaluation order of `out[key] = value` (differs by Python).
                key = self.take("%ds" % self.take("h")).decode("utf-8", "replace")
                out[key] = self.payload(t)
        if tag == 11:
            return [self.take("i") for _ in range(self.take("i"))]
        if tag == 12:
            return [self.take("q") for _ in range(self.take("i"))]
        raise ValueError("unknown tag %d at %d" % (tag, self.i))


def parse_nbt(buf):
    r = Reader(buf)
    tag = r.take("b")
    r.take("%ds" % r.take("h"))  # root name
    return r.payload(tag)


# ------------------------------------------------------------------ mca
def iter_chunks(path):
    with open(path, "rb") as f:
        data = f.read()
    broken = 0
    for slot in range(1024):
        loc = struct.unpack_from(">I", data, slot * 4)[0]
        offset = (loc >> 8) * 4096
        if offset == 0:
            continue
        length = struct.unpack_from(">i", data, offset)[0]
        comp = data[offset + 4]
        raw = data[offset + 5:offset + 4 + length]
        try:
            payload = zlib.decompress(raw) if comp == 2 else raw
            yield parse_nbt(payload)
        except Exception:  # torn write or unsupported compression
            broken += 1
    if broken:
        print(f"WARNING: skipped {broken} unreadable chunk(s) in {path}")


def section_ore_positions(section):
    """Return [(x, y, z)] of ore blocks inside one section (local coords)."""
    states = section.get("block_states")
    if not states:
        return []
    palette = states["palette"]
    names = [p["Name"] for p in palette]
    if ORE not in names:
        return []
    idx = names.index(ORE)
    data = states.get("data")
    if data is None:
        return [(x, y, z) for y in range(16)
                for z in range(16) for x in range(16)] if idx == 0 else []
    bits = max(4, (len(palette) - 1).bit_length())
    per_long = 64 // bits
    mask = (1 << bits) - 1
    out = []
    for i in range(4096):
        lv = data[i // per_long]
        val = (lv >> ((i % per_long) * bits)) & mask
        if val == idx:
            out.append((i & 15, (i >> 4) & 15, (i >> 8) & 15))
    return out


def chunk_ore_positions(chunk):
    out = []
    for sec in chunk.get("sections", []):
        sec_y = sec.get("Y", 0)
        for x, y, z in section_ore_positions(sec):
            out.append((x, sec_y * 16 + y, z))
    return out


def in_sample(cx, cz):
    return any(x0 <= cx <= x1 and z0 <= cz <= z1
               for x0, x1, z0, z1 in SAMPLE_CHUNK_RANGES)


def main():
    if not os.path.isdir(REGION_DIR):
        print("no region dir at", REGION_DIR)
        return 1
    total_ore = 0
    chunks_seen = 0
    chunks_with_ore = 0
    y_hist = {}
    for path in sorted(glob.glob(os.path.join(REGION_DIR, "*.mca"))):
        base = os.path.basename(path)[2:-4]  # r.X.Z
        rx, rz = (int(v) for v in base.split("."))
        for chunk in iter_chunks(path):
            cx, cz = chunk.get("xPos"), chunk.get("zPos")
            if cx is None or not in_sample(cx, cz):
                continue
            chunks_seen += 1
            ores = chunk_ore_positions(chunk)
            if ores:
                chunks_with_ore += 1
            total_ore += len(ores)
            for _, wy, _ in ores:
                band = "%02d-%02d" % (wy // 16 * 16, wy // 16 * 16 + 15)
                y_hist[band] = y_hist.get(band, 0) + 1
    if chunks_seen == 0:
        print("no sampled chunks found; run tools/qa/run_qa.py first")
        return 1
    per_chunk = total_ore / chunks_seen
    coverage = chunks_with_ore / chunks_seen
    print(f"sampled chunks      : {chunks_seen}")
    print(f"total ore blocks     : {total_ore}")
    print(f"chunks containing ore: {chunks_with_ore} ({coverage:.1%})")
    print(f"ore per chunk        : {per_chunk:.3f} "
          f"(spec expectation ~{EXPECTED_PER_CHUNK:.2f})")
    print("Y histogram:")
    for band in sorted(y_hist):
        print(f"  Y {band}: {y_hist[band]}")
    # Progression gate sanity: ore must be below diamond tier value.
    return 0


if __name__ == "__main__":
    sys.exit(main())
