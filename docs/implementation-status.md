# Implementation status

## Verified baseline

- Minecraft: 1.21.1
- NeoForge: 21.1.229 (selected baseline; requires Java 21)
- Create: 6.0.10; development Maven artifact currently pinned to `6.0.10-280`
- Required runtime dependency range: `[6.0.10,6.1.0)`

These choices are based on the NeoForged 1.21.1 setup documentation and Create's
NeoForge 1.21.1 dependency guide. The local environment does not currently provide
`java` or `gradle`, so the baseline still needs a real Gradle sync before API names
are treated as compile-verified.

## Completed in this pass

- Gradle/NeoForge project skeleton and Create Maven repositories.
- Mod metadata with required NeoForge, Minecraft, and Create dependencies.
- Basic block and item registries for ore, block, casting-table/generator placeholders,
  raw material, ingot, panel, blank mold, and ten explicit mold shapes.
- Initial English localization, model/blockstate stubs, blank mold/casting-table/generator
  shaped recipes, and Fortune ore loot table.
- Explicit mold mapping in `docs/molds.md`.
- First-pass data-driven ore placement from Y 0 through Y 32, with single-block
  veins, stone/deepslate replacement, iron-tool gating, Fortune raw drops, and
  Silk Touch ore drops.

## Not yet implemented

Block entities, fluids, Create heated mixing, Create Deployer/Depot recipes, casting
progress/state persistence, gear/material registration, textures, cooling/
washing, Mechanical Press integration, and kinetic Biomass Generator behavior remain
Milestones 2–7. The four registered machine blocks are intentionally placeholders and
must not be presented as functional until those systems are implemented and tested.
