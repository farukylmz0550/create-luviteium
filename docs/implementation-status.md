# Implementation status

## Verified baseline

- Minecraft: 1.21.1
- NeoForge: 21.1.229 (selected baseline; requires Java 21)
- Create: 6.0.10; development Maven artifact currently pinned to `6.0.10-280`
- Required runtime dependency range: `[6.0.10,6.1.0)`
- Gradle: 8.10.2 via the committed Gradle Wrapper

These choices are based on the NeoForged 1.21.1 setup documentation and Create's
NeoForge 1.21.1 dependency guide. The environment now has Temurin Java 21.0.13;
the committed wrapper performs the Gradle sync without a globally installed Gradle.

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
- Corrected the NeoForge 1.21.1 userdev DSL, mod constructor event-bus API, and
  block-item generic types against the actual mappings. Both `./gradlew compileJava`
  and `./gradlew build` pass.
- Registered source and flowing Molten Luviteium fluids with NeoForge `FluidType`
  metadata and client still/flowing textures.
- Added the verified Create 6.0.10 heated mixing recipe: 1 Raw Luviteium to 144 mB
  Molten Luviteium. Recipe resources use the 1.21.1 singular `recipe` directory.
- Added the first functional Casting Table slice: a persistent block entity with a
  144 mB exact-capacity tank, reusable ingot mold slot, hot ingot output slot,
  deterministic 20-tick casting, NBT save/load, and NeoForge fluid/item handler
  capabilities for Create pipe and transport automation.
- Added Ingot Mold and Pickaxe Head Mold Create Deployer recipes, plus dynamic casting
  amounts/timing for 144 mB/20 ticks and 288 mB/40 ticks respectively.
- Added the remaining eight Deployer mold recipes and explicit casting amount mapping:
  tool heads at 288 mB, helmet/boots at 432 mB, chestplate at 576 mB, and leggings at
  720 mB. Placeholder hot-head/armor output items and basic palette assets are present.
- Added Create fan washing recipes that convert hot ingots and tool heads into cooled parts,
  plus a Mechanical Press recipe that converts one cooled ingot into one Luviteium Panel.
  The cooled items currently reuse the first-pass palette models; dedicated polished textures
  remain a later visual pass.
- Replaced the first-pass placeholder art with recolored 1.21.1 vanilla-style 16x16 item
  silhouettes for tools, armor, ingots, raw material, panels, and molds. Cool Luviteium uses
  a green palette; hot variants retain a distinct orange heat palette.

## Not yet implemented

The Casting Table currently implements all ten mold paths as registered outputs but has
no player GUI; undamaged-sample enforcement, actual tool/armor attributes, hot armor
cooling, and casting-table visual polish remain. Create Depot recipes, gear/material
registration, dedicated item/block textures, and kinetic Biomass Generator behavior remain
Milestones 5–7. The Biomass Generator is still a placeholder and must not be presented as
functional until implemented and tested.
