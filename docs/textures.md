# Placeholder textures

**This entire set remains placeholder artwork. The user explicitly plans to
replace it later. Do not mark the artwork complete or treat this refresh as final.**

## Current refresh

The selected style is Create-like dark metal, thin brass hardware and limited
green accents. Cool Luviteium items are forest green; hot casts are orange.

- 39 item PNGs are 16×16 with binary transparent backgrounds. Finished sword and
  shovel are diagonal, component heads have no wooden handles, and ingot/panel
  have distinct bar/sheet silhouettes.
- All 11 mold icons use a dark rectangular casting plate with a different recess;
  the blank mold is no longer identical to the ingot mold.
- Four hot armor PNGs replace the previous runtime tint of the cold armor icons.
  Their item models now point directly at those hot PNGs; the tint handler was
  removed to avoid coloring the new art twice.
- Casting Machine front, side, top and bottom each have a different opaque 16×16
  face. The top shows the mold bed; the front shows a pipe inlet and small gauge.
- Biomass Generator has separate front socket, side vents, feed hatch, underside
  and service-face PNGs. Dedicated shaft-socket, brass and dark-iron textures keep
  its collar geometry independent of unrelated item pixels. Its animated shaft
  still references Create's own model and is unchanged by this art pass.
- Ore, storage block and still/flowing molten metal also have new 16×16 PNGs.
  Molten color is in the PNGs; the fluid tint is now white to avoid double tinting.
- Worn armor retains the existing placeholder 64×32 layer UV layout. It is not
  a 16×16 inventory sprite and was not regenerated in this refresh.

## Source and reproducibility

Two new original atlases were produced using the built-in **imagegen** tool.
No vanilla/Create texture PNG was fetched or copied for this refresh.

| Source | Exact prompt | Grid |
| --- | --- | --- |
| [`machines-source.png`](../art/texture-refresh/machines-source.png) | [`machines-prompt.txt`](../art/texture-refresh/machines-prompt.txt) | 4×4, 16 used cells |
| [`items-source.png`](../art/texture-refresh/items-source.png) | [`items-prompt.txt`](../art/texture-refresh/items-prompt.txt) | 8×5, 39 used cells; last swatch unused |

The requested atlas dimensions differ from the generator's actual output sizes
(1254×1254 and 1586×992). The extraction script calculates cell bounds from actual
dimensions, crops cells, downsamples with nearest-neighbor to 16×16 and normalizes
item alpha to 0/255. It does not draw or procedurally replace the generated artwork.

```sh
python3 tools/art/extract_textures.py
```

Requires ImageMagick (`magick`). The script writes the runtime files under
`src/main/resources/assets/luviteium/textures/block/` and `textures/item/`.
The source PNGs and prompts are retained under `art/texture-refresh/`, outside the
runtime resource directory, so the large atlases are not included in the mod JAR.

## Replacing placeholders later

Replace the individual PNGs at their current paths. Keep 16×16 resolution for
inventory and block textures; preserve transparent item backgrounds and opaque
block faces. Keep armor-layer UV dimensions/layout intact. Existing registry IDs
and gameplay recipes do not depend on the artwork. If replacing a PNG manually,
do not rerun atlas extraction unless you also intend to restore the generated set.

## Validation

All 39 item sprites have 16×16 dimensions, fully transparent outer background and
binary alpha, and no pair is pixel-identical. Block faces are opaque and 16×16;
all local model texture references resolve. Nearest-neighbor contact sheets were
visually inspected for silhouette, head/handle separation and item distinctions.
The clean build and Ponder client smoke test verify resource loading and the
machine/fluid appearances in-game. This is placeholder QA, not final art approval.
