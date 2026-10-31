# Mold contract (Milestone 1)

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
The first two deployer recipe files now exist and use Create 6.0.10's `create:deploying`
contract. Create's native JSON recipe format does not expose a durability predicate, so
the full-durability requirement is currently documented but not enforced by those data
recipes. A custom recipe condition/implementation remains required before this can be
marked complete.
