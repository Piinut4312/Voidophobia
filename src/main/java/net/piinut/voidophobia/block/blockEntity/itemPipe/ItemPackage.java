package net.piinut.voidophobia.block.blockEntity.itemPipe;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ItemPackage {

    private ItemStack itemStack;
    private int cooldown;
    private BlockPos destinationPos;

    public ItemPackage(ItemStack itemStack, BlockPos pos, int cooldown){
        this.itemStack = itemStack;
        this.cooldown = cooldown;
        this.destinationPos = pos;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getCooldown() {
        return cooldown;
    }

    public BlockPos getDestinationPos() {
        return destinationPos;
    }

    public void updateCooldown(){
        --this.cooldown;
        if(this.cooldown < 0){
            this.cooldown = 0;
        }
    }

}
