# Milestone 8 QA report

Current production revision: **nine GameTests pass**; heated Basin conversion,
all ten hot casts and washing outputs, and one-head tool assembly are covered in
[`processing.md`](processing.md). This supersedes the old direct-armor behavior.

For the latest runtime regression and Ponder results, see
[`debugging-and-ponder.md`](debugging-and-ponder.md). The sections below retain
earlier validation history; old recipe counts and artifact sizes are not current.

## Verified automatically

- Clean `./gradlew clean build`: passed.
- All resource JSON files validated with `jq`: passed.
- Dedicated development server: passed; NeoForge 21.1.229, Create 6.0.10, and
  Luviteium 0.1.0 load successfully and reach `Done`.
- Recipe load: 2,841 recipes loaded without a Luviteium parse error.
- Final distributable: `build/libs/luviteium-0.1.0.jar` (80 KB).
- Jar inspection: Biomass Generator classes, panel assets, casting/mixing/deploying,
  washing, pressing, and tool recipes are packaged.
- Removed-item audit: no `cooled_luviteium_ingot` or
  `LUVITEIUM_INGOT_COOLED` references remain in source, resources, or docs.
- Git worktree and resource diff checks: clean after the final commit.

## Documentation-accuracy pass verification

- `./gradlew build`: passed after all accuracy fixes.
- Dedicated development server restarted fresh and reached `Done` with
  Luviteium 0.1.0 and Create 6.0.10 loaded; no missing-model or missing-data
  errors appear after the `loot_table`/`tags` directory renames.
- Resource JSON re-validated with `jq` after the ore predicate rewrite and the
  three new block loot tables, splashing, pressing, and block recipes.
- 1.21.1 directory-contract audit: no `loot_tables`, `tags/blocks`, or
  `tags/items` paths remain under `data/`.
- Tool-item audit: pickaxe, axe, shovel, and hoe instances extend
  `PickaxeItem`, `AxeItem`, `ShovelItem`, and `HoeItem` respectively.
- Interaction audit: Casting Table `useItemOn` returns
  `net.minecraft.world.ItemInteractionResult` and `useWithoutItem` returns
  `net.minecraft.world.InteractionResult`, matching the 1.21.1
  `BlockBehaviour` signatures.

## Molten bucket and tool-icon verification

- `./gradlew build`: passed; jar contains the five tool textures, the bucket
  texture, and the bucket item model.
- Model/texture reference audit: every `layer0` in `assets/luviteium/models`
  resolves to an existing PNG, and all blockstate model references resolve.
- Item model count matches registration count (39 item models for 39
  registered items).
- Dedicated server startup after the change: reached `Done` with no Luviteium
  errors.
- RCON `/summon` item-id check: `luviteium:luviteium_bucket` resolves to
  "Molten Luviteium Bucket"; the control id `luviteium:does_not_exist` is
  rejected with `Tried to load invalid item`, proving the check is real.
- Mold contract re-audit: `blank_mold.json` plus all ten
  `recipe/deploying/*.json` files present, matching the ten rows in
  `docs/molds.md`.

## Harvest-gate verification (1.21.1 tool rules)

- Found and fixed a silent gameplay bug: the ore's iron-pickaxe requirement
  lived in `data/luviteium/tags/block/needs_iron_tool.json`, which defines
  `#luviteium:needs_iron_tool`. Vanilla harvest checks only read
  `#minecraft:*` tags, so the ore and the Luviteium block were in no vanilla
  mining tag at all and dropped nothing in survival with any tool.
- 1.21.1 gate model confirmed from bytecode (`Tier.createToolProperties`):
  each pickaxe's `minecraft:tool` component applies
  `deniesDrops(#minecraft:incorrect_for_<tier>_tool)` first, then
  `minesAndDrops(#minecraft:mineable/pickaxe)`. Vanilla 1.21.1 ships no
  `needs_*_tool` datapack files and no tool reads those keys, so gating via
  `needs_iron_tool` is a no-op in this version.
- Fix in place: `data/minecraft/tags/block/mineable/pickaxe.json`,
  `incorrect_for_wooden_tool.json`, and `incorrect_for_stone_tool.json`
  each contain `luviteium:luviteium_ore` and `luviteium:luviteium_block`;
  the dead mod-namespace block tags are deleted. Wooden and stone pickaxes
  match the earlier `deniesDrops` rule (no drops); iron and better match the
  allow rule and harvest normally, matching the spec's iron-pickaxe minimum.
- Harness assertion `HG1` re-audits the three vanilla-namespace files and the
  absence of `needs_iron_tool` on every run.

## Implementation decisions recorded

- Fuel governance update from the accuracy pass: the biomass whitelist and burn
  values are now sourced exclusively from existing vanilla 1.21.1 item tags
  (`logs_that_burn` 150, `planks` 38, `saplings` 75, `leaves` 15, `flowers` 15,
  `villager_plantable_seeds` 15). Hard-coded `Items.X` checks and the
  registry-id stripped-wood detection were removed; stripped logs/wood burn at
  the unified 150-tick log value, and untagged plant items (sticks, moss, hay,
  dried kelp, cactus, bamboo, sugar cane, vines, melon/pumpkin seeds,
  wheat/beetroot produce) are no longer fuels. Other mods opt into the
  generator simply by matching the same vanilla tags.
- Current fractional fuel durations are rounded to the nearest whole tick:
  1.875 s → 38 ticks and 0.75 s → 15 ticks.
- Product flow update from the accuracy pass: cooled-ingot crafting via fan
  splashing was reintroduced (hot ingot → Luviteium Ingot), while panel
  pressing consumes the cooled ingot. The earlier direct hot-ingot pressing is
  superseded; tool heads still use the existing washing recipes before tool
  crafting.
- Breaking the Casting Table drops its mold and output; stored molten fluid is
  discarded, not returned as an item. Block loot tables exist for the table,
  generator and storage block. The latest runtime fix also drops queued generator
  fuel when the generator is removed.
- Biomass fuel insertion uses the generator's NeoForge item capability, which is the
  standard inventory path used by Create Funnel/Chute transport.

## Manual gameplay checks remaining

These require a client or an interactive survival test and are not claimed as
automatically verified here:

- survival progression and actual ore rarity sampling;
- physical Create pipe, Funnel, Depot, Deployer, and Mechanical Press behavior;
- real chunk unload/reload and broader inventory automation (NBT round-trip,
  interrupted casting, Chute feeding, a 64-leaf fuel stack and a connected shaft
  now have passing GameTests);
- final texture art replacement (armor already renders with the dedicated
  `luviteium_layer_1/2` textures; item and machine art remains
  placeholder-quality).
