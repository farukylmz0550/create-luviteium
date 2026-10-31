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

## Implementation decisions recorded

- Fractional fuel durations are rounded to the nearest whole tick: 1.875 s → 38
  ticks, 0.9375 s → 19 ticks, and 0.75 s → 15 ticks.
- The separate cooled-ingot item is not part of the final product set. Hot ingots
  are pressed directly into panels; hot tool heads still use the existing washing
  recipes before tool crafting.
- Biomass fuel insertion uses the generator's NeoForge item capability, which is the
  standard inventory path used by Create Funnel/Chute transport.

## Manual gameplay checks remaining

These require a client or an interactive survival test and are not claimed as
automatically verified here:

- survival progression and actual ore rarity sampling;
- physical Create pipe, Funnel/Chute, Depot, Deployer, and Mechanical Press behavior;
- casting interruption, chunk unload/reload, and inventory automation;
- cumulative timing of a full 64-item fuel stack and visible kinetic output;
- armor/tool visuals and final texture replacement.
