#!/usr/bin/env python3
"""Slice generated atlases into game PNGs. Requires ImageMagick; never redraws art."""
from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'art/texture-refresh'
DEST = ROOT / 'src/main/resources/assets/luviteium/textures'

MACHINES = [
    'casting_table_front', 'casting_table_side', 'casting_table_top', 'casting_table_bottom',
    'biomass_generator_front', 'biomass_generator_side', 'biomass_generator_top', 'biomass_generator_bottom',
    'biomass_generator_pipe', 'shaft_socket', 'machine_brass', 'machine_dark_iron',
    'luviteium_block', 'luviteium_ore', 'molten_luviteium_still', 'molten_luviteium_flowing',
]
ITEMS = [
    'raw_luviteium', 'luviteium_ingot', 'luviteium_panel', 'luviteium_bucket',
    'luviteium_pickaxe', 'luviteium_axe', 'luviteium_shovel', 'luviteium_hoe',
    'luviteium_sword', 'luviteium_pickaxe_head', 'luviteium_axe_head', 'luviteium_shovel_head',
    'luviteium_hoe_head', 'luviteium_sword_blade', 'luviteium_helmet', 'luviteium_chestplate',
    'luviteium_leggings', 'luviteium_boots', 'hot_luviteium_ingot', 'hot_luviteium_pickaxe_head',
    'hot_luviteium_axe_head', 'hot_luviteium_shovel_head', 'hot_luviteium_hoe_head', 'hot_luviteium_sword_blade',
    'hot_luviteium_helmet', 'hot_luviteium_chestplate', 'hot_luviteium_leggings', 'hot_luviteium_boots',
    'blank_mold', 'ingot_mold', 'pickaxe_head_mold', 'axe_head_mold',
    'shovel_head_mold', 'hoe_head_mold', 'sword_blade_mold', 'helmet_mold',
    'chestplate_mold', 'leggings_mold', 'boots_mold',
]


def extract(file, columns, rows, names, directory, transparent):
    source = SOURCE / file
    width, height = map(int, subprocess.check_output(
        ['magick', 'identify', '-format', '%w %h', str(source)], text=True).split())
    for index, name in enumerate(names):
        col, row = index % columns, index // columns
        left, right = round(col * width / columns), round((col + 1) * width / columns)
        top, bottom = round(row * height / rows), round((row + 1) * height / rows)
        destination = DEST / directory / (name + '.png')
        args = ['magick', str(source), '-crop', f'{right-left}x{bottom-top}+{left}+{top}',
                '+repage', '-filter', 'point', '-resize', '16x16!', '-strip']
        if transparent:
            # Binary coverage avoids semi-transparent halos at Minecraft inventory scale.
            args += ['-alpha', 'on', '-channel', 'A', '-threshold', '50%', '+channel']
        else:
            args += ['-alpha', 'off']
        subprocess.run(args + ['PNG32:' + str(destination)], check=True)
    print(f'Extracted {len(names)} {directory} textures at 16x16.')


if __name__ == '__main__':
    extract('machines-source.png', 4, 4, MACHINES, 'block', False)
    extract('items-source.png', 8, 5, ITEMS, 'item', True)
