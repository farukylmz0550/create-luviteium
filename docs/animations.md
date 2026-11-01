# Machine animation

All textures remain placeholders. These effects add feedback to the existing
processing and kinetic systems without changing costs, recipes or timing.

## Casting Machine

- The inserted mold is visible on the upper casting bed.
- Molten metal gradually spreads across the mold as fluid arrives. Client-side
  interpolation smooths updates instead of changing the actual fluid quantity.
- While casting can advance, the mold has a small vibration and sparse smoke.
  Insufficient fluid or a blocked output stops that working effect.
- A completed product rises briefly, then rests above the mold until collected.
  A short smoke puff marks completion. Automated extraction can still happen
  immediately; a brief render-only product image completes the animation.
- No item entities are created by the renderer or completion effect. The only
  real output is the existing inventory slot, so animation cannot duplicate casts.

The block entity now sends an initial chunk update tag and standard block-entity
update packets containing mold, fluid, output and progress. Inventory/fluid changes
are coalesced into the next server tick; active progress syncs every four ticks.
A vanilla block event starts the short completion animation. Visual fields are
transient and never used for server-side processing.

## Biomass Generator

- Both shaft ends have a small Create cogwheel driven by the same synchronized
  kinetic speed as the shaft. The wheels remain visible at rest.
- The wheels and shafts stop on zero actual speed, including an overstressed
  network. Their animation is not driven by the mere presence of fuel.
- Sparse smoke appears only while the generator is burning fuel. Fuel still burns
  while an overloaded network is stopped, so combustion smoke can continue then.
- The wheels are decorative shaft-mounted hardware, not extra connection points
  or additional stress generators. Existing six-direction shaft connectivity stays
  unchanged.

Effects use normal world lighting, vanilla smoke particles and existing Create
models. There is no full-bright glow, new sound loop or additional dependency.

## Validation

`./gradlew build runGameTestServer` runs ten required tests. The new animation
sync test checks initial partial-fill synchronization, progress and completion
updates, blocked-output detection, and that 40 client animation ticks cannot
advance production or create an output. The nine previous production/gear/kinetic
tests continue to cover the gameplay behavior.

`./gradlew runClient -PponderSmokeTest` verifies both renderers, visible molten
metal, a visible completed product during its ejection lift, and moving/stopped shaft angles. Ponder now
demonstrates gradual fluid input and per-tick casting instead of a duplicate item
entity standing in for the output. Screenshots are saved under
`runs/client/screenshots/` for visual inspection.

Validation on 2026-11-01: all ten GameTests passed; all three Ponder scenes passed
playback and replay, and both machine renderers passed the real client checks.
Powered shaft angles changed between captures; both stopped captures held the
same angle. The completed hot cast was captured during its upward motion.

These automated checks cover serialization and the client renderer. They are not
a separate multi-user multiplayer session or a particle-density performance test.
