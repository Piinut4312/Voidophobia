package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.item.ItemStack;

public interface LaserEngravingInventory extends BasicInventory{

    @Override
    default void setStack(int slot, ItemStack stack) {
        BasicInventory.super.setStack(slot, stack);
        this.markDirty();
    }

    @Override
    default ItemStack removeStack(int slot, int count) {
        this.markDirty();
        return BasicInventory.super.removeStack(slot, count);
    }

}
