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

## Milestone 1 status

**Complete.** The project, verified Create development dependencies, core registries,
and dedicated-server startup are now validated together. `./gradlew compileJava`,
`./gradlew build`, and `./gradlew runServer` all pass; the dedicated server loads
NeoForge 21.1.229, Create 6.0.10, and Luviteium 0.1.0 and reaches `Done`. The Create
runtime setup uses the documented non-transitive slim dependency plus Ponder, Flywheel,
and Registrate runtime/development dependencies. The 1.21.1 fluid-result field was also
corrected so startup loads the Luviteium mixing recipe without a parse error.

## Milestone 2 status

**Complete for the current data-driven scope.** Luviteium Ore is registered with an
iron-tool harvest gate, generates as size-one individual blocks three times per chunk
within the uniform Y 0–32 range, and targets both stone and deepslate ore replaceables.
The loot table provides Silk Touch ore drops and Fortune-aware raw-material drops.
Mineable/needs-iron tags, block/item models, English names, raw material registration,
and the specified green/raw/highlight palette are present. JSON validation and the full
Gradle build pass; the dedicated server also loads the world-generation data successfully.
The exact rarity tuning remains an intentionally easy-to-adjust data value and should be
rebalanced after a longer survival-world sampling pass.

## Milestone 3 status

**Complete for the current processing scope.** The Create heated-mixing recipe converts
one Raw Luviteium into exactly 144 mB of Molten Luviteium. NeoForge source/flowing fluid
registries and FluidType metadata are present, and the casting-table fluid capability is
available for Create pipe transfer. Client still/flowing textures and the green fluid tint
are registered separately on the client. JSON validation, the full Gradle build, and a
dedicated-server reload pass all succeed; the server reaches `Done` and loads the mixing
recipe without parse errors.

## Milestone 4 status

**Complete for the casting-table and blank-mold scope.** The Casting Table uses the
specified DBD/BKB/DBD recipe, keeps an exact-capacity molten-fluid tank selected by the
current mold, casts at 20 ticks per 144 mB with proportional timing, persists inventory,
fluid, and progress through NBT, and prevents mold extraction while fluid or progress is
active. The ten explicit mold shapes and output mappings are registered, and the DDD/III
blank-mold recipe is present. Item and fluid capabilities expose the table to Create
automation. Undamaged sample enforcement for specialized mold creation is intentionally
tracked under Milestone 5 because it requires a custom validation path beyond the native
Create deploying JSON contract.

## Milestone 5 status

**Complete for the current data-driven Deployer scope.** All ten mold recipes are
registered: the ingot mold uses an iron ingot, while tool and armor molds accept the
corresponding vanilla item tags. The deployer sample is consumed because the recipes
do not keep the held item. A `DeployerRecipeSearchEvent` listener rejects damaged
tools and armor when a blank mold is being formed, enforcing the full-durability
contract that native Create JSON ingredients cannot express. JSON validation, the full
Gradle build, and a dedicated-server startup/reload pass all succeed. Survival-world
Deployer interaction remains part of the final gameplay QA pass.

## Milestone 6 status

**Complete for the products and processing scope.** Cooled tool heads now craft into
functional Luviteium pickaxe, axe, shovel, hoe, and sword items using the vanilla
shapes. The tool tier centralizes the 1,250 durability, 7.5 mining speed, 4.5 attack
bonus, enchantability, and Luviteium-ingot repair values. Cast armor outputs are real
helmet, chestplate, leggings, and boots with an 18-point total defense target,
toughness, knockback resistance, and the same ingot repair ingredient. Hot ingots and
all hot tool heads have Create bulk-washing recipes; hot ingots produce panels via the
Mechanical Press after the cooled-ingot intermediate was removed from the product
family. JSON validation, the full Gradle build, and dedicated-server recipe loading
all pass; the server reaches `Done` with 2,841 recipes loaded.

## Milestone 7 status

**Complete for the current generator implementation scope.** The Biomass Generator is
now a Create directional kinetic block with a real generating block entity, one-slot
fuel inventory capped at 64 items, automatic restart, no redstone gate, 64 RPM output,
and 512 SU stress capacity. Fuel acceptance is governed by existing vanilla item tags
(see the documentation-accuracy pass below), which inherently excludes coal, charcoal,
lava, and bone meal. The inventory capability is exposed for Create Funnel/Chute
insertion. Full survival interaction and cumulative 64-stack timing checks remain
Milestone 8 QA.

## Milestone 8 status

**Complete for automated QA and packaging.** A clean Gradle build, JSON validation,
dedicated-server startup, recipe loading, stale cooled-ingot reference audit, and jar
content inspection all pass. The final artifact is `build/libs/luviteium-0.1.0.jar`.
The remaining items are explicitly documented in `docs/qa-report.md` as manual client
or survival-world checks; they are not represented as automatically verified.

## Documentation-accuracy pass status

**Complete.** A dedicated audit pass compared every mod resource and implementation
against the specification and this document; all resulting discrepancies were fixed
in one sweep. The changes below are verified by `./gradlew build` and a dedicated
`runServer` startup reaching `Done`.

- Datapack directory names now use the 1.21.1 singular forms: `loot_table`,
  `tags/block`, and `tags/item` (formerly `loot_tables`, `tags/blocks`,
  `tags/items`). The ore loot table applies the Fortune/Silk Touch alternatives
  using the 1.21.1 `match_tool` predicate shape with `minecraft:enchantments`.
- Block loot tables were added for `casting_table`, `biomass_generator`, and
  `luviteium_block`; all previously survived only as ownerless blocks (no drops).
- The Casting Table can now be used by hand: right-clicking with a supported
  reusable mold inserts it into the empty mold slot, and right-clicking without a
  mold (or with an unsupported item) extracts the finished cast into the player
  inventory. `useItemOn`/`useWithoutItem` are implemented with the correct
  1.21.1 `ItemInteractionResult`/`InteractionResult` return types, and
  `onRemove` drops the mold, output, and stored molten fluid when the block is
  broken.
- The Casting Table output slot now respects the item's max stack size, and the
  tank capacity is re-applied as `requiredAmount()` after world reload so the
  exact-capacity invariant holds across NBT reloads.
- New recipes close the intended product loop: a Create splashing (fan washing)
  recipe cools a Hot Luviteium Ingot into a Luviteium Ingot, the Mechanical
  Press panel recipe consumes the cooled ingot (not the hot one), a nine-ingot
  shaped recipe crafts the Luviteium Block, and the block unpacks back into
  nine ingots with a shapeless recipe.
- The Luviteium pickaxe, axe, shovel, and hoe now extend the real vanilla
  `PickaxeItem`, `AxeItem`, `ShovelItem`, and `HoeItem` classes instead of
  generic `DiggerItem` stubs, restoring axe stripping, shovel pathing, and hoe
  tilling behavior with the correct effective tool rules.
- Armor defense is the 18-point total target: helmet 3, chestplate 7,
  leggings 5, boots 3, with enchantability 18.
- Biomass Generator fuel governance is now purely vanilla-tag driven: the
  whitelist and burn values come from existing 1.21.1 item tags
  (`logs_that_burn` 150, `planks` 38, `saplings` 75, `leaves` 15, `flowers` 15,
  `villager_plantable_seeds` 15). Nether stems are excluded via
  `logs_that_burn`, stripped logs/wood burn at the unified log value because
  `logs_that_burn` already contains them, and plant items without any vanilla
  item tag (sticks, moss, hay, dried kelp, cactus, bamboo, sugar cane, vines,
  melon/pumpkin seeds, wheat/beetroot produce) are no longer accepted. The
  generator tooltip text reflects the tag-driven fuel list.
- Defensive armor layer textures (`luviteium_layer_1.png` and
  `luviteium_layer_2.png`) were generated by recoloring the vanilla diamond
  armor layers into the Luviteium green palette, so worn armor renders in-world.
- A Luviteium creative-mode tab was added with the full item list, and client
  tooltips explain cooled-item hints, mold usage, and accepted generator fuels.

## Molten bucket and tool-icon pass

**Complete.** Adds the missing player-facing forms of the material.

- The five tool item icons (pickaxe, axe, shovel, hoe, sword) previously
  pointed at their head-only component textures, so a finished tool rendered
  as a floating head with no handle. Each tool model now uses a dedicated
  full-tool texture generated in the existing Luviteium item palette, keeping
  the vanilla wooden handle.
- A new `luviteium_bucket` item ("Molten Luviteium Bucket") carries one
  ingot charge of 144 mB. Right-clicking a Casting Table with it pours the
  charge into the tank (up to the exact capacity of the selected mold), and
  right-clicking with an empty `minecraft:bucket` draws one charge back out.
  The bucket swaps with the empty bucket and falls back to dropping the
  result when the inventory is full. It is registered in the creative tab
  with English name and two explanatory tooltip lines.
- The mold family itself required no change: the blank mold recipe, all ten
  reusable molds, their textures/models, and the ten `create:deploying`
  recipes were re-verified present and consistent with `docs/molds.md`.
- Verified with a clean `./gradlew build`, a dedicated-server startup
  reaching `Done`, and an RCON `/summon` check where
  `luviteium:luviteium_bucket` resolves to "Molten Luviteium Bucket" while
  an intentionally invalid item id is rejected with a registry error.

## Harvest-gate namespace fix (1.21.1 tool rules)

**Complete.** The ore's iron-pickaxe requirement was silently dead and is now
wired through the model 1.21.1 actually uses.

- The old `data/luviteium/tags/block/needs_iron_tool.json` and
  `data/luviteium/tags/block/mineable/pickaxe.json` files defined
  `#luviteium:needs_iron_tool` and `#luviteium:mineable/pickaxe`. Vanilla
  harvest checks never read tags in the mod's namespace, so the ore and the
  storage block were in *no* vanilla mining tag: they had no pickaxe mining
  speed and dropped nothing in survival with every tool, including a
  netherite pickaxe.
- Verified from the 1.21.1 bytecode: `ItemStack.isCorrectToolForDrops` ->
  `Item.isCorrectToolForDrops` -> `Tool.isCorrectForDrops`, and
  `Tier.createToolProperties` builds exactly two rules in order:
  `Rule.deniesDrops(tier.getIncorrectBlocksForDrops())` then
  `Rule.minesAndDrops(miningBlocks, speed)`. The `needs_*_tool` tag keys
  still exist in `BlockTags` but vanilla 1.21.1 ships no datapack files for
  them and no vanilla tool references them, so gating through
  `needs_iron_tool` has no effect.
- The fix moves both blocks into the vanilla namespace:
  `data/minecraft/tags/block/mineable/pickaxe.json` (mining speed and the
  allow-drops rule for every pickaxe tier) plus
  `data/minecraft/tags/block/incorrect_for_wooden_tool.json` and
  `incorrect_for_stone_tool.json` (the earlier `deniesDrops` rules that make
  wooden and stone pickaxes drop nothing). Iron and better pickaxes match
  only the second rule and harvest normally, which is exactly the spec's
  "Minimum tool: iron pickaxe" requirement.
- The dead `luviteium`-namespace block tag files were deleted. The
  `data/luviteium/tags/item/ingots/luviteium.json` item tag stays: it is a
  mod-namespace convenience tag, not a vanilla gate.
- The QA harness now audits this statically (`HG1`): the three vanilla-namespace
  tag files must exist with the ore id, and no `needs_iron_tool` file may
  remain in any namespace.

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
- Added Create fan washing recipes that convert hot tool heads into cooled parts, plus a
  Mechanical Press recipe that converts one hot ingot into one Luviteium Panel. The separate
  cooled-ingot item was removed during Milestone 7.
- Replaced the first-pass placeholder art with recolored 1.21.1 vanilla-style 16x16 item
  silhouettes for tools, armor, ingots, raw material, panels, and molds. Cool Luviteium uses
  a green palette; hot variants retain a distinct orange heat palette.
- Preserved transparent alpha backgrounds during recoloring and separated the cast tool-head
  icons from their vanilla handles; pickaxe, axe, shovel, and hoe head outputs now show only
  the cast head shape.
- Rebuilt the recolored PNGs with explicit vanilla alpha masks after palette tuning, avoiding
  accidental grayscale flattening and keeping the final green material shaded rather than
  emissive-looking.
- Corrected Create's 1.21.1 fluid-result field in the heated mixing recipe and verified that
  the NeoForge dedicated development server reaches the `Done` state with Luviteium loaded.
- Replaced the temporary referenced/vendor art with original 16x16 pixel textures authored for
  this mod. The machine art borrows Create's visual language—brass trim, dark casing, tank
  windows, fan hub, and pipe ports—without copying Create or vanilla PNG files. The Casting
  Table's north face is visually tank-like for fluid piping, while its existing fluid
  capability remains the actual automation connection point.
- Split the machine surfaces into explicit front, side, bottom, and top PNG assets. Casting
  Table now has a dedicated front panel in addition to its side, top, and bottom textures;
  Biomass Generator keeps distinct front fan, side casing, pipe-port, top, and bottom faces.

## Not yet implemented

The Casting Table implements all ten mold paths as registered outputs with
hand-based mold insertion and output extraction, but still has no player GUI;
casting-table visual polish and hot armor cooling remain. Create Depot recipes
and gear/material registration also remain later passes. The Biomass Generator
has its functional kinetic and fuel implementation; its machine assets remain
documented placeholders until the final texture pass.
