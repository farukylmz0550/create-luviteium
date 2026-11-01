#!/usr/bin/env python3
"""Resample imagegen colors onto the original armor UV/alpha; requires Pillow.

No pixels or UV islands are drawn. Generated boundary drift is handled by sampling
the nearest opaque source pixel. Original runtime coverage is the authoritative
mask; generated transparent padding cannot punch holes in an armor surface.
"""
from pathlib import Path
import json
import hashlib

from PIL import Image

ROOT = Path(__file__).resolve().parents[2]
ART = ROOT / 'art/armor-refresh'
DEST = ROOT / 'src/main/resources/assets/luviteium/textures/models/armor'


def sha256(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def prepare(layer):
    original_path = ART / f'input-layer-{layer}.png'
    source_path = ART / f'layer-{layer}-source.png'
    original = Image.open(original_path).convert('RGBA')
    source = Image.open(source_path).convert('RGBA')
    assert original.size == (64, 32)
    assert source.width == 2 * source.height
    result = source.resize(original.size, Image.Resampling.NEAREST)
    mask = original.getchannel('A')
    assert set(mask.get_flattened_data()) == {0, 255}, 'Expected binary source alpha'
    sampled = result.load()
    source_pixels = source.load()
    corrected = 0
    max_distance = 0.0
    # Nearest-color edge extension is deliberately limited to one logical pixel.
    limit = int(source.width / 64) + 1
    for y in range(32):
        for x in range(64):
            if mask.getpixel((x, y)) == 0:
                sampled[x, y] = (0, 0, 0, 0)
                continue
            r, g, b, a = sampled[x, y]
            if a < 240 or b > max(r, g):
                cx = min(source.width - 1, int((x + .5) * source.width / 64))
                cy = min(source.height - 1, int((y + .5) * source.height / 32))
                candidates = []
                for sy in range(max(0, cy-limit), min(source.height, cy+limit+1)):
                    for sx in range(max(0, cx-limit), min(source.width, cx+limit+1)):
                        rgba = source_pixels[sx, sy]
                        # Ignore occasional blue/cyan generator fringe pixels.
                        if rgba[3] >= 240 and rgba[2] <= max(rgba[0], rgba[1]):
                            candidates.append(((sx-cx)**2 + (sy-cy)**2, sx, sy))
                if not candidates:
                    raise ValueError(f'Layer {layer}: missing generated color at UV {(x,y)}')
                distance, sx, sy = min(candidates)
                max_distance = max(max_distance, distance ** .5 / (source.width / 64))
                assert max_distance <= 1.5, 'Generated UV drift exceeds local edge extension'
                sampled[x, y] = source_pixels[sx, sy]
                corrected += 1
    result.putalpha(mask)
    assert result.getchannel('A').tobytes() == mask.tobytes()
    visible = [rgba for rgba in result.get_flattened_data() if rgba[3]]
    assert all(max(r,g,b) > 20 for r,g,b,a in visible), 'Unexpected black surface'
    assert all(b <= max(r,g) for r,g,b,a in visible), 'Unexpected blue accent'
    path = DEST / f'luviteium_layer_{layer}.png'
    result.save(path)
    result.resize((1024,512), Image.Resampling.NEAREST).save(ART / f'preview-layer-{layer}.png')
    return dict(layer=layer, source_size=list(source.size), runtime_size=list(result.size),
                visible_pixels=len(visible), alpha_identical=True,
                edge_samples_corrected=corrected, max_edge_distance_in_uv_pixels=round(max_distance,3),
                input_sha256=sha256(original_path), source_sha256=sha256(source_path),
                runtime_sha256=sha256(path))


if __name__ == '__main__':
    report = [prepare(n) for n in (1,2)]
    (ART / 'validation.json').write_text(json.dumps(report, indent=2) + '\n')
    print(json.dumps(report, indent=2))
