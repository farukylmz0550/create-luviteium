package com.create.luviteium.content;

import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/** Curios-compatible permanent charm. */
public class CharmItem extends Item implements ICurioItem {
    public CharmItem() {
        super(new Item.Properties().stacksTo(1));
    }
}
