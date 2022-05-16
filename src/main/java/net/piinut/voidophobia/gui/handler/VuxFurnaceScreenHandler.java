package net.piinut.voidophobia.gui.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.piinut.voidophobia.gui.screen.slot.VuxFurnaceOutputSlot;

public class VuxFurnaceScreenHandler extends ScreenHandler {

    private Inventory inventory;
    private PropertyDelegate propertyDelegate;

    public VuxFurnaceScreenHandler(int syncId, PlayerInventory playerInventory){
        this(syncId, new SimpleInventory(2), playerInventory, new ArrayPropertyDelegate(6));
    }

    public VuxFurnaceScreenHandler(int syncId, Inventory inventory, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.VUX_FURNACE, syncId);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        this.propertyDelegate = propertyDelegate;

        this.addSlot(new Slot(inventory, 0, 43, 37));
        this.addSlot(new VuxFurnaceOutputSlot(playerInventory.player, inventory, 1, 103, 37));

        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }

        this.addProperties(propertyDelegate);
    }

    public int getCookProgress() {
        int i = this.propertyDelegate.get(0);
        int j = this.propertyDelegate.get(1);
        if (j == 0 || i == 0) {
            return 0;
        }
        return i * 24 / j;
    }

    public int getVuxStorage() {
        float i = this.propertyDelegate.get(2);
        float j = this.propertyDelegate.get(3);
        if (i == 0 || j == 0) {
            return 0;
        }
        return (int) (i * 56 / j);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.getSlot(index);
        if(slot != null && slot.hasStack()){
            ItemStack itemStack = slot.getStack();
            newStack = itemStack.copy();
            if(index < this.inventory.size()){
                if(!this.insertItem(itemStack, this.inventory.size(), this.slots.size(), true)){
                    return ItemStack.EMPTY;
                }
            }else if(!this.insertItem(itemStack, 0, this.inventory.size(), false)){
                return ItemStack.EMPTY;
            }

            if(itemStack.isEmpty()){
                slot.setStack(ItemStack.EMPTY);
            }else{
                slot.markDirty();
            }

        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public int getVuxStored() {
        return this.propertyDelegate.get(2);
    }

    public boolean shouldRenderFlame(){
        return this.propertyDelegate.get(2) >= 20;
    }
}
