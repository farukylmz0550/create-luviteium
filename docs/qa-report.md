# Milestone 8 QA report

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
- Fractional fuel durations are rounded to the nearest whole tick: 1.875 s → 38
  ticks, 0.9375 s → 19 ticks, and 0.75 s → 15 ticks.
- Product flow update from the accuracy pass: cooled-ingot crafting via fan
  splashing was reintroduced (hot ingot → Luviteium Ingot), while panel
  pressing consumes the cooled ingot. The earlier direct hot-ingot pressing is
  superseded; tool heads still use the existing washing recipes before tool
  crafting.
- Slots removed by hand, world drops, and block removal all preserve contents
  because the Casting Table drops its mold, output, and drains molten fluid in
  `onRemove`, and host loot tables (`casting_table`, `biomass_generator`,
  `luviteium_block`) were added in the same pass.
- Biomass fuel insertion uses the generator's NeoForge item capability, which is the
  standard inventory path used by Create Funnel/Chute transport.

## Manual gameplay checks remaining

These require a client or an interactive survival test and are not claimed as
automatically verified here:

- survival progression and actual ore rarity sampling;
- physical Create pipe, Funnel/Chute, Depot, Deployer, and Mechanical Press behavior;
- casting interruption, chunk unload/reload, and inventory automation;
- cumulative timing of a full 64-item fuel stack and visible kinetic output;
- final texture art replacement (armor already renders with the dedicated
  `luviteium_layer_1/2` textures; item and machine art remains
  placeholder-quality).
