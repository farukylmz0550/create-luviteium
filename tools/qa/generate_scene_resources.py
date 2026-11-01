#!/usr/bin/env python3
"""Rebuild original Ponder/GameTest templates and English scene keys, no dependencies."""
import gzip
import json
import re
import struct
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
RES = ROOT / 'src/main/resources'


def string(value):
    encoded = value.encode('utf-8')
    return struct.pack('>H', len(encoded)) + encoded


def named(kind, name, value):
    return bytes([kind]) + string(name) + value


def integer(value):
    return struct.pack('>i', value)


def sequence(kind, values):
    return bytes([kind]) + integer(len(values)) + b''.join(values)


def template(path, width, height, floor):
    blocks = []
    if floor:
        for x in range(width):
            for z in range(width):
                blocks.append(named(9, 'pos', sequence(3, list(map(integer, [x, 0, z]))))
                              + named(3, 'state', integer(0)) + b'\0')
        # Ponder derives bounds from placed blocks, not the NBT size field.
        # This never-shown corner keeps runtime machine placement inside bounds.
        blocks.append(named(9, 'pos', sequence(3, list(map(integer, [width - 1, height - 1, width - 1]))))
                      + named(3, 'state', integer(0)) + b'\0')
    palette = named(8, 'Name', string('minecraft:smooth_stone')) + b'\0'
    root = named(3, 'DataVersion', integer(3955))
    root += named(9, 'size', sequence(3, list(map(integer, [width, height, width]))))
    root += named(9, 'palette', sequence(10, [palette]))
    root += named(9, 'blocks', sequence(10, blocks))
    root += named(9, 'entities', sequence(10, [])) + b'\0'
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_bytes(gzip.compress(named(10, '', root), mtime=0))


def main():
    template(RES / 'assets/luviteium/ponder/workshop.nbt', 7, 5, True)
    template(RES / 'data/luviteium/structure/empty.nbt', 7, 5, False)
    source = (ROOT / 'src/main/java/com/create/luviteium/client/ponder/LuviteiumScenes.java').read_text()
    lang_path = RES / 'assets/luviteium/lang/en_us.json'
    lang = json.loads(lang_path.read_text())
    for scene in re.split(r'public static void ', source)[1:]:
        title = re.search(r'scene.title\("([^"]+)", "([^"]+)"\)', scene)
        key = 'luviteium.ponder.' + title[1]
        lang[key + '.header'] = title[2]
        for index, text in enumerate(re.findall(r'\.text\("([^"]+)"\)', scene), 1):
            lang[f'{key}.text_{index}'] = text
    lang_path.write_text(json.dumps(lang, ensure_ascii=False, indent=2) + '\n')
    print('Generated Ponder template, GameTest template and English Ponder keys.')


if __name__ == '__main__':
    main()
