# Mold contract (Milestones 4–5)

The first implementation keeps the mold family deliberately finite and explicit:

| Mold | Undamaged deployer sample | Molten amount | Intended output |
|---|---|---:|---|
| Ingot Mold | iron ingot | 144 mB | Luviteium ingot |
| Pickaxe Head Mold | any vanilla pickaxe | 288 mB | hot Luviteium pickaxe head |
| Axe Head Mold | any vanilla axe | 288 mB | hot Luviteium axe head |
| Shovel Head Mold | any vanilla shovel | 288 mB | hot Luviteium shovel head |
| Hoe Head Mold | any vanilla hoe | 288 mB | hot Luviteium hoe head |
| Sword Blade Mold | any vanilla sword | 288 mB | hot Luviteium sword blade |
| Helmet Mold | any vanilla helmet | 432 mB | Luviteium helmet |
| Chestplate Mold | any vanilla chestplate | 576 mB | Luviteium chestplate |
| Leggings Mold | any vanilla leggings | 720 mB | Luviteium leggings |
| Boots Mold | any vanilla boots | 432 mB | Luviteium boots |

The sample is consumed and must have full durability. The specialized mold is reusable.
All ten deployer recipe files use Create 6.0.10's `create:deploying` contract. Tool and
armor samples are matched through vanilla item tags; a `DeployerRecipeSearchEvent`
listener rejects a damaged sample when the depot input is a blank mold, because native
Create JSON ingredients do not expose a durability predicate.
