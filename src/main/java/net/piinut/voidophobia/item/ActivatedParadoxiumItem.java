package net.piinut.voidophobia.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ActivatedParadoxiumItem extends Item {
    public ActivatedParadoxiumItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
}
