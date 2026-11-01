# Worn armor placeholder color refresh

**All artwork here and both runtime armor layers remain placeholders.**

This pass replaces the original flat olive color treatment with warm dark iron,
subdued forest green and aged-brass accents. It preserves the existing vanilla
64×32 armor UV coverage and changes only the two worn-layer PNGs.

## Inputs and generation

Original project-owned inputs are `input-layer-1.png` and `input-layer-2.png`.
`reference-layer-1.png` and `reference-layer-2.png` are exact nearest-neighbor
16× enlargements used as edit targets. Both original images and enlarged
references were inspected before generation.

The built-in **imagegen** tool was used in edit mode with
`transparent_background: true`; no CLI/API fallback was used. Each generated
image is 1774×887 despite the prompt requesting the reference's 1024×512 size.
No vanilla/Create source images were downloaded for this pass.

| Saved generated source | Exact prompt | References, in order | Disposition |
| --- | --- | --- | --- |
| `layer-1-source.png` | `layer-1-prompt.txt` | `reference-layer-1.png` | Accepted |
| `layer-2-rejected-source.png` | `layer-2-prompt.txt` | `reference-layer-2.png`, `layer-1-source.png` | Rejected: copied layer-1 UV islands |
| `layer-2-source.png` | `layer-2-retry-prompt.txt` | `reference-layer-2.png` | Accepted |

Original tool output identifiers:

- Layer 1: `exec-04e23a85-f0f4-4b06-b04c-0c41b8c04a2d.png`
- Rejected layer 2: `exec-c7f276f6-0486-4cd7-bd1c-bb503fb57970.png`
- Accepted layer 2: `exec-0fa7dcf4-ead2-4adf-a8e6-735e04d71a57.png`

All generated sources are copied into this directory, so reproduction does not
depend on machine-local output paths.

## Preparation and verification

Run `python3 tools/art/prepare_armor.py` from the repository root; requires Pillow.
The script samples generated colors using nearest-neighbor resizing to 64×32.
Where generated edges are slightly inset or contain blue fringe, it samples the
nearest opaque, non-blue source pixel in a small neighborhood. It never invents
colors, draws UV islands, or copies old olive colors into a missing region.
Original alpha is then restored exactly, including every transparent hole.

- Layer 1: 632 occupied pixels; 51 edge/fringe samples corrected. Maximum sampling
  displacement is 0.613 of one runtime pixel.
- Layer 2: 280 occupied pixels; no edge/fringe corrections needed.
- Both: RGBA 64×32; original alpha bytes identical; no blue-dominant pixels or
  opaque near-black fill introduced; no emissive resource or renderer changes.
- `validation.json` records dimensions, coverage and SHA-256 of inputs, sources
  and final runtime PNGs.
- `preview-layer-1.png` and `preview-layer-2.png` are 16× nearest-neighbor
  enlargements of the final files, inspected visually for palette and coverage.

The script preserves UV boundaries/coverage, not the old exact RGB values or
every old shading value. The generator supplied the new metal shading and trim.
After the asset checks, all four armor pieces were inspected in the real client
from four angles. See the [in-game preview](../../docs/images/armor-preview.png).
The temporary development preview screen is not included in the release JAR.

Runtime destinations:

- `src/main/resources/assets/luviteium/textures/models/armor/luviteium_layer_1.png`
- `src/main/resources/assets/luviteium/textures/models/armor/luviteium_layer_2.png`
