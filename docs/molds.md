# Mold contract (Milestones 4–5)

The first implementation keeps the mold family deliberately finite and explicit:

| Mold | Undamaged deployer sample | Molten amount | Intended output |
|---|---|---:|---|
| Ingot Mold | iron ingot | 144 mB | hot Luviteium ingot |
| Pickaxe Head Mold | any vanilla pickaxe | 288 mB | hot Luviteium pickaxe head |
| Axe Head Mold | any vanilla axe | 288 mB | hot Luviteium axe head |
| Shovel Head Mold | any vanilla shovel | 288 mB | hot Luviteium shovel head |
| Hoe Head Mold | any vanilla hoe | 288 mB | hot Luviteium hoe head |
| Sword Blade Mold | any vanilla sword | 288 mB | hot Luviteium sword blade |
| Helmet Mold | any vanilla helmet | 720 mB | hot Luviteium helmet |
| Chestplate Mold | any vanilla chestplate | 1152 mB | hot Luviteium chestplate |
| Leggings Mold | any vanilla leggings | 1008 mB | hot Luviteium leggings |
| Boots Mold | any vanilla boots | 576 mB | hot Luviteium boots |

The sample is consumed and must have full durability. Blank Molds stack to 64;
all ten specialized molds are single-stack reusable items.
Every output must be cooled by an Encased Fan blowing through water. Washed armor
is ready to wear; one washed tool head/blade is assembled with sticks. See
[`processing.md`](processing.md) for the complete current chain.
All ten deployer recipe files use Create 6.0.10's `create:deploying` contract. Tool and
armor samples are matched through vanilla item tags; a `DeployerRecipeSearchEvent`
listener rejects a damaged sample when the depot input is a blank mold, because native
Create JSON ingredients do not expose a durability predicate.
