# Luviteium processing

This is the current production contract. It supersedes the original specification's
directly usable armor casts and the older multi-head tool assembly recipes.

## One production chain

1. Put **Raw Luviteium** in a **Basin**, heat it with a fueled **Blaze Burner**,
   and power the **Mechanical Mixer** above it. One raw item produces **144 mB
   of Molten Luviteium**. The output is fluid, not an ingot or bucket item.
2. Put the reusable product mold into the **Casting Machine**. A powered
   Mechanical Pump and Fluid Pipes can transfer molten metal directly from the
   Basin; a separate intermediate tank is optional.
3. The machine takes only the amount required by its mold and waits if the charge
   is incomplete. Once full, it automatically casts **one hot product**, drains
   that charge and keeps the mold. A blocked output pauses casting.
4. Put the hot product on a Depot or belt in the airflow of a powered **Encased
   Fan blowing through water**. Create's washing process returns the normal,
   cooled item. Merely putting an item in water is not the cooling recipe.

Every mold now follows this chain, including armor. Hot armor is a normal item,
not wearable armor; only the washed result can be equipped. No separate
`cooled_luviteium_ingot` item is introduced.

## Mold amounts and assembly

| Mold | Molten metal | Casting time | Hot output → washed output |
| --- | ---: | ---: | --- |
| Ingot | 144 mB | 20 ticks | Hot ingot → ingot |
| Pickaxe / axe / shovel / hoe head | 288 mB | 40 ticks | Hot head → head |
| Sword blade | 288 mB | 40 ticks | Hot blade → blade |
| Helmet | 432 mB | 60 ticks | Hot helmet → wearable helmet |
| Chestplate | 576 mB | 80 ticks | Hot chestplate → wearable chestplate |
| Leggings | 720 mB | 100 ticks | Hot leggings → wearable leggings |
| Boots | 432 mB | 60 ticks | Hot boots → wearable boots |

As requested, tool heads still need a handle after cooling. Assemble **one cooled
head above two sticks** in a vertical column for pickaxe, axe, shovel or hoe.
For a sword, put **one cooled blade above one stick**. Hot heads cannot substitute
for cooled ones. Older recipes incorrectly required multiple complete heads;
those extra heads are no longer needed.

Panel pressing (ordinary ingot → panel) and storage block packing/unpacking remain
downstream uses of cooled metal, not additional steps needed to cool a cast.
Mold imprinting is unchanged: a Deployer consumes an undamaged sample over a Depot
holding a Blank Mold, and the resulting mold can be reused indefinitely.

## Player-facing changes and compatibility

- The machine is displayed as **Casting Machine** / **Kalıp Makinesi**. Its
  `luviteium:casting_table` registry ID stays the same, preserving placed blocks,
  inventory items and recipes in existing worlds.
- Raw ore, mold, machine and hot-item tooltips describe the next processing step.
- The casting Ponder scene now shows the heated Basin and Mixer, direct piping,
  automatic hot casting and water/fan cooling in order. English and Turkish
  captions are updated; hot armor and armor molds also link to this scene.
- Four hot armor item IDs and four washing recipes were added. Existing finished
  armor stays usable; new armor casts require cooling.
- Hot armor now has dedicated 16×16 orange placeholder PNGs from the later
  [texture refresh](textures.md). There is no extra runtime warm tint or emissive
  renderer. The user will replace this art later.

## Verification

`./gradlew build runGameTestServer` passes all **nine** required GameTests, including
the six earlier machine/gear regressions and three production tests:

- Create's actual Basin recipe matcher rejects an unheated Basin. With heat, the
  Basin processing code consumes one raw item and exposes exactly 144 mB of molten
  metal through its fluid capability.
- All ten molds reject incomplete charges, accept only the missing fluid, respect
  casting time, produce the correct hot item, reuse the mold, and wash into the
  correct cooled item through Create's actual splashing recipe implementation.
- All five tool recipes accept one cooled head/blade and the correct number of
  sticks; hot components fail the recipe match.

`./gradlew runClient -PponderSmokeTest` plays/replays all three scenes and captures
the updated mixing, hot-cast and cooling stages in `runs/client/screenshots/`.
This is a visual check, not a physical automation integration test. The tests above
exercise Create's recipe implementations directly; a complete survival line with
powered Mixer, pumps, belts and Fan still needs a separate end-to-end gameplay pass.

New verified builds are copied to the user's CurseForge `deneme/mods` directory
and desktop, as recorded in the README.
