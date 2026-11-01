# Create: Luviteium

## Minecraft Mod Implementation Specification and AI Coding Prompt

This document is both the implementation specification and an actionable prompt for building **Create: Luviteium**, a Minecraft addon for **Minecraft 1.21.1 on NeoForge** with **Create** as a required dependency.

The implementation must preserve the design decisions below. Items explicitly marked **Unresolved** are intentional technical questions that must be verified during implementation rather than silently guessed.

**Later user-directed processing revision:** the current contract is
[docs/processing.md](docs/processing.md). All casts, including armor, leave the
machine hot and require Encased Fan + water cooling. Tools retain head + stick
assembly, using one cooled head (or blade) per tool. These requirements supersede
the original direct-armor outputs and multi-head vanilla-shaped assembly below.

---

## 1. Project definition

### Mod identity

- Mod name: **Create: Luviteium**
- Minecraft version: **1.21.1**
- Mod loader: **NeoForge**
- Required gameplay dependency: **Create**
- Do not invent or add dependencies that are not required by the verified target environment.
- The addon should feel like a natural Create progression and should use vanilla/Create-inspired presentation.

### Design direction

Luviteium is a rare, green-tinted material that sits between iron and diamond in practical equipment value, while remaining substantially easier to obtain than netherite. Its processing loop should reward Create automation without requiring elaborate custom machinery or elaborate custom 3D models.

Use vanilla- and Create-inspired models, block shapes, item silhouettes, and textures. Keep the asset scope deliberately modest: custom textures and simple model JSONs are expected; elaborate custom 3D geometry is not.

---

## 2. Visual identity

Use the following Luviteium palette consistently across items, blocks, fluids, particles, UI accents, and documentation:

| Use | Hex color |
|---|---|
| Main green | `#747C48` |
| Raw Luviteium | `#59613A` |
| Highlight / bright accent | `#A6AA70` |

The raw material should appear darker and earthier than processed Luviteium. Molten Luviteium may use a brighter, hotter interpretation of the same palette, but it must remain visually identifiable as Luviteium.

---

## 3. Ore generation and raw material

### Luviteium ore

- Generates in the Overworld.
- Vertical range: **Y 0 through Y 32**.
- Ore blocks are single rare blocks rather than large or common veins.
- Rarity target: **rarer than diamond, but more accessible than ancient debris/netherite**.
- Minimum tool: **iron pickaxe**.
- Drops raw Luviteium.
- Fortune must increase the raw-material yield according to normal Minecraft-style Fortune behavior.
- Silk Touch must drop the ore block itself.

The ore should be valuable enough to justify automation, but not so scarce that the Create processing chain becomes impractical in ordinary survival play.

### Ore-generation implementation

**Unresolved:** verify the exact NeoForge 1.21.1 world-generation and placed-feature APIs, then choose exact vein size, discard chance, distribution, and biome/stone targets. Do not invent exact tuning values until the target mappings and Create/NeoForge versions are confirmed.

Acceptance intent: the resulting distribution must clearly satisfy the qualitative rarity target and must be testable in a controlled world.

---

## 4. Processing chain

The core loop is:

```text
Luviteium Ore
  -> Raw Luviteium
  -> Create heated mixing
  -> Molten Luviteium
  -> Pipes
  -> Casting Table + selected reusable mold
  -> Hot cast product
  -> Cooling / washing where applicable
  -> Finished component or gear
```

### Heated Create mixing

- Use normal Create blaze-burner heat: **heated**, not superheated, unless testing proves a compatibility reason otherwise.
- Recipe: **1 Raw Luviteium -> 144 mB Molten Luviteium**.
- The recipe should be exposed through Create’s normal mixing/heat-processing system.
- The molten fluid must be pipe-compatible with Create fluid transport.

**Unresolved:** verify the exact Create recipe type, heat condition naming, serializer/data format, and Create version compatible with NeoForge 1.21.1.

### Fluid handling

- Molten Luviteium is intended to travel through Create pipes.
- A casting table receives the fluid and casts it into the selected mold.
- The casting table must have a clearly defined fluid-input direction or interface.

**Unresolved:** confirm the exact item/fluid output direction and the correct Create-compatible behavior for insertion, extraction, and automation. Support standard Create automation patterns where possible.

---

## 5. Casting table

### Crafting recipe

The casting table is crafted using this 3×3 pattern:

```text
D B D
B K B
D B D
```

Where:

- `D` = iron
- `B` = copper casing
- `K` = cauldron

Use the appropriate vanilla/Create item IDs for the verified target version. The table should be a simple, readable block with a Create-inspired appearance.

### Casting behavior

- Casting speed: **20 ticks per 144 mB**.
- Capacity must be exactly the amount required by the selected mold; no overfilling should be required.
- The selected mold must not change merely because fluid is present.
- The casting table must not consume fluid until it has a valid mold and sufficient capacity for the selected operation.
- A casting operation should produce a hot product where cooling is required by the product rules below.

### Supported mold amounts and proportional timing

All amounts below are per finished product. Casting time scales proportionally from 20 ticks per 144 mB.

| Product | Molten Luviteium | Casting time |
|---|---:|---:|
| Ingot | 144 mB | 20 ticks |
| Tool head | 288 mB | 40 ticks |
| Armor component / complete armor piece, as defined by its mold | 432 mB | 60 ticks |
| Larger component | 576 mB | 80 ticks |
| Larger component | 720 mB | 100 ticks |
| Larger component | 1008 mB | 140 ticks |
| Armor piece or other large product | 1152 mB | 160 ticks |

The exact mapping of each specialized mold to these listed amounts must be implemented consistently and documented in recipes. Do not create additional unapproved amounts without a design reason and documentation.

**Unresolved:** confirm whether the casting table should use an internal progress state, a scheduled server tick process, or an existing Create casting abstraction. Ensure timing remains deterministic and does not depend on client frame rate.

---

## 6. Mold system

### Blank mold

Crafting pattern:

```text
D D D
I I I
_ _ _
```

Where:

- `D` = diamond
- `I` = iron
- `_` = empty slot

The result is a reusable **Blank Mold**.

### Specialized molds

Specialized molds are created with a **Create Deployer** operating on a **Depot**. The Deployer uses an undamaged sample item or piece of gear to imprint a blank mold.

Rules:

- The sample item is consumed.
- The resulting specialized mold is reusable indefinitely.
- The sample must be undamaged.
- Specialized molds are made from applicable vanilla gear samples, including:
  - stone gear where applicable;
  - iron gear where applicable;
  - gold gear where applicable;
  - diamond gear where applicable;
  - netherite gear where applicable;
  - ingot samples.
- The system should support the ten reusable mold types intended by the design. The final list must be explicitly enumerated in the implementation and recipe data.

The mold system should make the source item obvious through its name, icon, tooltip, or model while remaining visually simple.

**Unresolved:** verify the exact Create Deployer-on-Depot recipe integration API and supported recipe conditions for consuming an undamaged input. Confirm whether durability is available as a native recipe condition or requires a custom condition/processing implementation.

**Unresolved:** finalize and document the exact ten mold types and the exact source-item-to-mold mapping. Do not silently infer extra variants from every vanilla item.

---

## 7. Products and cooling

### General product rules

- Ingot casting uses 144 mB.
- Tool-head casting uses 288 mB per head.
- Armor and larger products use the approved amounts in the casting table above.
- Tool heads are cast hot, then cooled before being used in crafting.
- Armor is cast as a complete finished armor piece.
- Ingot-based products that need a cool state must be cooled before normal crafting or use.

### Cooling and bulk washing

- Hot products should have a clear cooling step.
- Support Create bulk washing using a fan and water for the relevant hot-product cooling or washing recipes.
- The washing setup must be automatable with normal Create components.
- Recipes should avoid requiring a custom machine when a standard Create bulk-processing recipe is appropriate.

**Unresolved:** determine which products are cooled through bulk washing, which use a direct cooling state transition, and what exact Create bulk-washing recipe format is valid for the selected Create version. The final behavior must be internally consistent and discoverable through recipe viewing data if a recipe-viewer integration is present, without adding an unapproved dependency.

### Tool crafting

- Cooled Luviteium tool heads are combined with sticks using normal shaped crafting.
- Tool recipes should follow the corresponding vanilla tool shape.
- The intended tools include at least pickaxe, axe, shovel, hoe, and sword if the corresponding mold set includes them.

### Armor crafting

- Armor is cast as complete pieces.
- Do not require an additional armor-assembly crafting step.
- Armor pieces should follow the normal helmet, chestplate, leggings, and boots slot model.

### Panels

- Luviteium panels are made from cooled Luviteium ingots using a Create Mechanical Press.
- The press recipe should be simple and consistent with Create’s normal pressing conventions.

---

## 8. Luviteium gear balance

Luviteium equipment should sit between iron and diamond, but closer to diamond in practical performance.

Provisional values:

- Pickaxe durability: **1250**
- Pickaxe mining speed: **7.5**
- Pickaxe attack damage: **4.5**
- Armor value: **18 total**, unless the verified per-piece balancing model requires distributing this total across the four armor pieces.
- Repair material: Luviteium ingots in an anvil.

These values are provisional implementation targets and must be kept easy to adjust through the material/tool registration layer.

**Unresolved:** determine exact per-piece armor values, toughness, knockback resistance, enchantability, attack values for every tool, and whether the NeoForge 1.21.1 item-component/equipment APIs require a different registration pattern.

---

## 9. Biomass generator

The Biomass Generator is a Create-oriented power source that burns approved organic fuel items.

### Crafting recipe

Pattern:

```text
D P D
S M S
D B P
```

Where:

- `D` = iron
- `P` = Luviteium panel
- `S` = shaft
- `M` = mechanical bearing
- `B` = blaze rod

### Generator behavior

- Output: **64 RPM**
- Capacity: **512 SU**
- Fuel inventory: exactly **one slot** with a stack limit of 64.
- Burns whenever valid fuel is present, regardless of current load.
- No redstone control.
- Automatically restarts when valid fuel remains or is inserted.
- Fuel must be inserted through a standard **Create Funnel or Chute**.
- Do not require direct belt insertion as the primary input method.

### Fuel values

The following values are burn durations per item. These are design targets and must be converted to the correct tick representation for the verified implementation.

| Fuel | Burn duration |
|---|---:|
| Logs | 7.5 seconds per log; 64 logs = 8 minutes |
| Stripped logs / stripped wood | 6 seconds |
| Planks | 1.875 seconds |
| Leaves | 0.75 seconds |
| Saplings | 3.75 seconds |
| Sticks | 0.9375 seconds |
| Seeds | 0.75 seconds |
| Wheat, beetroot, carrot, potato | 1.5 seconds |
| Cactus, bamboo | 1.875 seconds |
| Sugarcane | 1.5 seconds |
| Vines, flowers, moss | 0.75 seconds |
| Moss block | 6 seconds |
| Dried kelp block | 15 seconds |
| Hay bale | 13.5 seconds |
| Normal wood | 7.5 seconds |

Explicitly excluded fuels:

- coal;
- charcoal;
- lava;
- bone meal.

**Unresolved:** determine the exact tick rounding policy for fractional durations. Preserve the intended values as closely as possible, document the rounding rule, and test cumulative behavior for a full stack. Also verify how the generator should expose its SU capacity and rotation output through Create’s current kinetic APIs.

**Unresolved:** confirm the exact funnel/chute insertion hook and whether the generator needs a custom inventory capability, sided insertion rules, or a Create-specific transport behavior.

---

## 10. Implementation constraints

- Target only Minecraft 1.21.1 NeoForge and the verified compatible Create build.
- Do not add dependencies merely for convenience.
- Prefer data-driven recipes, tags, loot tables, and world-generation data where the APIs support them.
- Keep gameplay values centralized and easy to rebalance.
- Keep client and server registration separated correctly.
- Avoid client-only classes being loaded on a dedicated server.
- Provide meaningful registry names and stable resource locations.
- Add tooltips or in-game descriptions where they clarify hot/cool states, mold behavior, or generator fuel rules.
- Use standard Minecraft and Create conventions for sounds, particles, block interaction, inventory behavior, and recipe discovery wherever feasible.
- Provide localization entries for every player-facing item, block, fluid, mold, state, and message.

---

## 11. Suggested project structure

Adapt package names to the selected mod ID, but keep responsibilities separated approximately as follows:

```text
src/main/java/<mod-package>/
  CreateLuviteium.java
  registry/
    ModBlocks.java
    ModItems.java
    ModFluids.java
    ModEntitiesOrBlockEntities.java
    ModCreativeTabs.java
  content/
    CastingTableBlock.java
    CastingTableBlockEntity.java
    BiomassGeneratorBlock.java
    BiomassGeneratorBlockEntity.java
    MoldItem.java
    HotProductItem.java
  recipe/
    ModRecipeTypes.java
    ModRecipeSerializers.java
    DeployerMoldRecipe.java
    CastingRecipe.java
  world/
    LuviteiumOreGeneration.java
  compat/
    CreateCompat.java
  client/
    ClientSetup.java
    ModelAndRenderSetup.java
  data/
    ModTags.java

src/main/resources/
  META-INF/neoforge.mods.toml
  assets/<mod-id>/
    blockstates/
    models/block/
    models/item/
    textures/block/
    textures/item/
    textures/fluid/
    lang/en_us.json
    sounds.json (only if needed)
  data/<mod-id>/
    recipes/
    loot_tables/
    tags/
    worldgen/
  data/create/
    recipes/ (only when the verified Create data contract requires this namespace)
```

This is a suggestion, not a license to create unsupported integration layers. Use the actual NeoForge and Create APIs after inspecting the target mappings and example-compatible source.

---

## 12. Milestones

### Milestone 1: Project and registries

- Set up the 1.21.1 NeoForge project.
- Verify the exact Create dependency and mappings.
- Register the mod, creative tab if used, raw material, ingot, panel, fluid, ore, casting table, generator, blank mold, and specialized molds.
- Confirm dedicated-server startup.

### Milestone 2: Ore and basic materials

- Implement ore generation in the Overworld at Y 0–32.
- Implement iron-pickaxe harvest requirement, Silk Touch, and Fortune behavior.
- Add textures, models, loot tables, tags, and English localization.

### Milestone 3: Fluid processing

- Add Raw Luviteium to Molten Luviteium heated mixing at 1:144 mB.
- Verify heated Create integration and fluid transport.
- Add fluid visuals and resource data.

### Milestone 4: Casting table and molds

- Implement the casting table recipe and block entity.
- Implement exact-capacity casting and 20 ticks per 144 mB.
- Add mold selection persistence and protection against unintended mold changes.
- Implement blank mold crafting.

### Milestone 5: Deployer mold creation

- Implement Deployer-on-Depot mold recipes.
- Consume only undamaged samples.
- Confirm reusable output molds.
- Add all ten explicitly documented mold types.

### Milestone 6: Products and processing

- Add ingots, hot products, tool heads, complete armor pieces, panels, and cooling/washing recipes.
- Add tool and armor crafting.
- Add anvil repair behavior.
- Add Mechanical Press panel production.

### Milestone 7: Biomass Generator

- Implement the recipe, one-slot inventory, fuel whitelist, burn timing, auto-restart, no-redstone behavior, and Create kinetic output.
- Verify Funnel/Chute insertion.
- Test 64-item fuel stacks and fractional-duration rounding.

### Milestone 8: Balance, QA, and packaging

- Test survival progression and automation.
- Test dedicated-server behavior.
- Test invalid inputs, empty molds, insufficient fluid, overfilling, interruption, chunk unload/reload, hopper/Create insertion, and fuel replacement.
- Confirm no unwanted dependencies.
- Package the mod and document verified versions.

---

## 13. Acceptance criteria

The implementation is acceptable only when all of the following are true:

1. It launches on the verified Minecraft 1.21.1 NeoForge and Create versions.
2. It does not require invented or undocumented dependencies.
3. Luviteium ore generates in the Overworld from Y 0 through Y 32 as rare individual blocks.
4. The ore requires an iron pickaxe, supports Fortune raw drops, and supports Silk Touch ore drops.
5. The color palette uses `#747C48`, `#59613A`, and `#A6AA70` coherently.
6. Heated Create mixing converts 1 raw item into exactly 144 mB molten fluid.
7. Molten fluid can be transported with Create pipes.
8. The casting table recipe matches the specified DBD/BKB/DBD pattern.
9. Casting uses exactly the selected mold capacity and takes 20 ticks per 144 mB.
10. Fluid presence cannot unexpectedly change the selected mold.
11. The blank mold recipe matches three diamonds over three iron ingots with an empty bottom row.
12. Specialized molds are made through Create Deployer-on-Depot processing, consume an undamaged sample, and are reusable.
13. The intended ten mold types are explicitly documented and implemented.
14. Tool heads cool before tool crafting, and armor is cast complete.
15. Relevant hot products support the intended Create fan-and-water bulk-washing workflow.
16. Luviteium panels are made with a Create Mechanical Press.
17. Luviteium gear is between iron and diamond and uses the provisional pickaxe and armor targets unless a documented compatibility adjustment is required.
18. Luviteium ingots repair Luviteium gear in an anvil.
19. The Biomass Generator matches the specified recipe, 64 RPM, 512 SU capacity, one 64-stack fuel slot, automatic restart, and no redstone control.
20. Fuel enters through a standard Create Funnel or Chute.
21. The generator accepts only the approved biomass fuels and rejects coal, charcoal, lava, and bonemeal.
22. Burn durations are documented, rounded consistently, and tested.
23. Models and textures are vanilla/Create-inspired and do not depend on elaborate custom 3D models.
24. All player-facing content has English localization and useful names/tooltips.
25. Dedicated-server and save/reload tests pass.

---

## 14. Explicit unresolved technical checklist

Before calling the implementation complete, resolve and record:

- exact NeoForge 1.21.1 and Create version compatibility;
- exact Create recipe serializers and heat condition names;
- exact Create Deployer/Depot recipe integration;
- how to enforce an undamaged source item;
- exact ten specialized mold types and all source mappings;
- exact ore placement tuning and rarity validation;
- exact casting-table item/fluid input and output directions;
- exact persistence behavior when a casting operation is interrupted or the chunk unloads;
- exact hot-product cooling and bulk-washing recipe semantics;
- exact fuel tick rounding policy;
- exact Create kinetic API for 64 RPM and 512 SU;
- exact Funnel/Chute insertion behavior;
- exact per-piece armor distribution and remaining gear stats;
- exact asset/model requirements for fluid, hot, cooled, and mold states.

Do not hide these decisions in arbitrary constants. Put verified decisions in code, data, or a short implementation note so they can be reviewed and changed.

---

## 15. Ready-to-use AI coding prompt

You are implementing a Minecraft mod addon named **Create: Luviteium**.

Build it for **Minecraft 1.21.1 on NeoForge**, with the verified compatible **Create** version as the required gameplay dependency. First inspect the project’s actual mappings, NeoForge version, Create API, and recipe/data contracts. Do not invent dependencies, APIs, registry names, or compatibility assumptions. If a requirement below conflicts with the verified API, preserve the gameplay intent, implement the closest compatible behavior, and document the difference in an unresolved/compatibility note.

Implement the complete Luviteium progression described in this document:

- rare single-block Overworld Luviteium ore at Y 0–32;
- iron-pickaxe mining requirement;
- Fortune raw drops and Silk Touch ore drops;
- palette `#747C48`, raw `#59613A`, highlights `#A6AA70`;
- heated Create mixing of 1 raw Luviteium into 144 mB molten Luviteium;
- Create-pipe-compatible molten fluid;
- casting table crafted with DBD/BKB/DBD, where D is iron, B is copper casing, and K is cauldron;
- exact-capacity reusable molds;
- casting at 20 ticks per 144 mB with proportional timing;
- no unintended mold changes while fluid is present;
- blank mold crafted from a top row of three diamonds, middle row of three iron, and empty bottom row;
- reusable specialized molds made by Create Deployer on Depot using undamaged stone, iron, gold, diamond, netherite where applicable, and ingot samples; samples are consumed;
- the explicitly documented set of ten mold types;
- hot tool heads that must cool before stick-based tool crafting;
- complete armor cast directly;
- Create fan-and-water bulk washing for relevant cooling/washing recipes;
- Mechanical Press production of cooled Luviteium panels;
- Luviteium tools and armor balanced between iron and diamond, closer to diamond, with provisional pickaxe durability 1250, speed 7.5, attack 4.5, total armor 18, and anvil repair using Luviteium ingots;
- Biomass Generator crafted with DPD/SMS/DBP, where D is iron, P is Luviteium panel, S is shaft, M is mechanical bearing, and B is blaze rod;
- generator output 64 RPM and 512 SU capacity;
- one 64-stack fuel slot;
- burning whenever fuel exists regardless of load;
- no redstone control and automatic restart;
- standard Create Funnel/Chute fuel insertion;
- the complete approved biomass fuel table and exclusions;
- consistent, documented rounding for fractional fuel durations;
- vanilla/Create-inspired simple visuals without elaborate custom 3D models.

Use data-driven recipes and tags where appropriate. Keep registration, content logic, recipe logic, world generation, Create compatibility, and client setup separated. Ensure the dedicated server never loads client-only classes. Add models, blockstates, textures, loot tables, tags, English localization, and useful tooltips. Make all balance values easy to adjust.

Implement in milestones: project/registries, ore/materials, fluid processing, casting/molds, Deployer integration, products/cooling, Biomass Generator, then QA and packaging. After each milestone, compile or run the narrowest relevant test. At the end, run a clean build and targeted gameplay/integration tests for ore drops, fluid amounts, casting timing, mold persistence, interrupted casting, Deployer sample consumption, cooling/washing, panel pressing, generator fuel behavior, Funnel/Chute insertion, save/reload, and dedicated-server startup.

Before declaring success, report:

1. verified Minecraft, NeoForge, and Create versions;
2. every implemented registry and recipe family;
3. the exact ten mold types and source mappings;
4. any compatibility deviations from this specification;
5. ore-generation tuning and the validation method;
6. fuel tick rounding behavior;
7. casting input/output directions;
8. the final file/package structure;
9. build and test results;
10. any remaining unresolved technical details.

Do not claim completion if a required behavior is only stubbed, unregistered, client-only, or untested.
