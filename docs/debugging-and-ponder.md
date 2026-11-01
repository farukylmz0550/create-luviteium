# Runtime debugging and Create Ponder

This records the initial debugging pass. The later unified hot-cast production
chain is described in [`processing.md`](processing.md). The current ten-test suite
also includes the [animation sync regression](animations.md).

## Fixes

- **Generator stress capacity:** Create multiplies the source's capacity by RPM.
  Returning 512 therefore supplied 32,768 SU at 64 RPM. The generator now returns
  8 SU/RPM for an actual network capacity of **512 SU**, with zero self-load.
- **Generator fuel handling:** the capability reports its real 64-item slot limit.
  Refuelling no longer disconnects/reconnects the kinetic network each time an item
  burns out. Fuel state is marked dirty while ticking; queued fuel drops when the
  generator is removed. Existing vanilla fuel tags and burn durations are retained.
- **Casting persistence:** progress is marked dirty as it advances and pauses when
  output is blocked or fluid is insufficient. Saved progress resumes after NBT load.
- **Bucket conservation:** pouring/scooping simulates a full 144 mB charge before
  changing either inventory or fluid. Partial space/fluid no longer causes loss or
  duplication. Creative interaction retains the held bucket/mold.
- **Manual mold removal:** sneak-right-click empty-handed extracts an idle mold;
  a filled or partly processed mold remains locked.
- **Gear:** vanilla item attribute components are explicitly registered. Pickaxe
  total attack damage is 4.5, including the player's base 1 damage; the shared tier
  bonus is 2.5. Armor has durability (vanilla type base × 30) and cannot stack.
  Vanilla tool/armor category tags make the gear eligible for the corresponding
  enchantments. Ingot repair is retained.

## Tutorials

Hover over a supported item and hold Create's Ponder key (default **W**).

| Scene | Entry items | Demonstrated flow |
| --- | --- | --- |
| Casting | Casting Table, Raw Luviteium, Molten Luviteium Bucket, Ingot Mold | Insert reusable mold; tank/pump/pipe input; amounts and timings; hot ingot → washed ingot → pressed panel; remove idle mold |
| Mold imprinting | Blank Mold, Pickaxe Head Mold, hot/cooled Pickaxe Head, Luviteium Pickaxe | Depot and powered Deployer; undamaged sample consumed; fan washing before tool crafting |
| Biomass | Biomass Generator | Barrel/Chute input, shaft axis, 64-item slot, 64 RPM / 512 SU, accepted fuels, stop/restart |

All three have English and Turkish captions and keyframes. They register only on
the client. `tools/qa/generate_scene_resources.py` reproducibly generates our own
NBT floor template and English scene strings using Python's standard library.
The unseen upper corner block extends Ponder's actual placement bounds: its bounds
come from placed blocks, not the template's `size` field. It is never shown.
Machine/item PNG textures remain placeholders; this pass did not replace them.

Ponder is an animated explanation. Its scripted fluid/item transitions are not
evidence that the corresponding server automation recipes have been tested.

## Reproducible validation

```sh
./gradlew build runGameTestServer
./gradlew runClient -PponderSmokeTest
```

Validated with Java 21, Minecraft 1.21.1, NeoForge 21.1.229, Create 6.0.10-280
and Ponder 1.0.82. The build succeeds and **all six required GameTests pass**:

1. Exact tank capacity, filled-mold lock, 9-tick NBT save/load, no output at tick
   19, output and unlock at tick 20.
2. Blocked output preserves fluid and progress, then finishes the remaining time.
3. Rejected partial bucket transfers change nothing; complete charge round-trip.
4. Pickaxe durability/attack, enchantment tags, unstackable damageable armor and
   ingot repair.
5. Actual Create network capacity and connected shaft speed; a 64-leaf stack burns
   for approximately 960 game ticks, stops, and automatically restarts with new fuel.
6. A real Chute feeds the generator; breaking it drops the three queued logs after
   one log begins burning.

The opt-in desktop client test creates a uniquely named flat world inside
`runs/client/saves/`, compiles and plays each scene twice, asserts each machine
exists and playback finishes, opens Ponder UI, captures nine screenshots, and
exits. It reports `LUVITEIUM_PONDER_ALL_PASS`; ordinary startup never invokes it.
Screenshots are locally generated evidence, ignored by Git:

- `runs/client/screenshots/ponder-casting_table.png`
- `runs/client/screenshots/ponder-blank_mold.png`
- `runs/client/screenshots/ponder-biomass_generator.png`
- `runs/client/screenshots/ponder-biomass_generator-turning.png`
- `runs/client/screenshots/ponder-biomass_generator-stopped.png`
- `runs/client/screenshots/ponder-biomass_generator-stopped-later.png`
- `runs/client/screenshots/ponder-processing-mixing.png`
- `runs/client/screenshots/ponder-processing-hot-cast.png`
- `runs/client/screenshots/ponder-processing-cooling.png`

Screenshots were visually inspected for machines, text and missing textures. The
client test exercises English UI rendering; Turkish caption keys have resource
parity, but Turkish layout has not had a separate screenshot pass.
GameTest advances game ticks faster than real time; it is not a wall-clock fuel
benchmark. The older `tools/qa/run_qa.py` RCON harness was not run in this pass and
is not the basis of these results.

## Remaining gameplay checks

Survival progression and ore rarity, physical pipe/Funnel integration, actual
Deployer/Press/fan recipe execution, real chunk unload/reload and multiplayer
interaction remain separate QA work. NBT round-trip and Ponder playback do not
replace those checks. The Casting Table still has no dedicated GUI, and breaking
it discards stored molten fluid while dropping mold/output items.

The distributable is `build/libs/luviteium-0.1.0.jar`.

## Shaft connection visibility

The Biomass Generator model now has a raised brass mounting plate, a smaller
light-metal collar and a dark 4×4 shaft center on each end of its kinetic axis. Both connectors rotate
with the existing six facing variants, matching `hasShaftTowards`. The item model
inherits the same geometry. The existing original 16×16 textures supply the
surfaces; no replacement PNGs or borrowed artwork were added. The collars project
two model pixels out from the casing and meet the center of a connected shaft.
The socket housings are static. The generator now also renders Create's own
`AllPartialModels.SHAFT_HALF` at both ends, protruding four pixels from the casing
so the ends remain visible beyond the two-pixel collars. These are runtime
references to Create's models/textures; no vendor files are copied into the mod.
The client-only `BiomassGeneratorRenderer` uses Create's normal kinetic rotation
transform and synchronized `getSpeed()`. Powered ends turn with the network;
at zero speed they stay visible and stationary, including an overstressed network.
There is no burn-time-only animation that could continue while the network stops.
The renderer uses the regular block-entity path without a separate Flywheel visual,
so there is only one owner drawing the generator's shaft ends.
The later [placeholder texture refresh](textures.md) supplies dedicated collar,
brass and dark-iron textures instead of sampling the Blank Mold icon. Resource
references and model bounds were checked; `build runClient -PponderSmokeTest`
passes and the generator's Ponder screenshot was visually inspected.
The client smoke test checks renderer registration, 64 RPM moving snapshots with
different angles, and two zero-RPM snapshots with identical angles. This verifies
the visible/stationary behavior without hiding the shaft when fuel runs out.
