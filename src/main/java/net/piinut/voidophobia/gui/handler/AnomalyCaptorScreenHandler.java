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

public class AnomalyCaptorScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    public AnomalyCaptorScreenHandler(int syncId, PlayerInventory playerInventory){
        this(syncId, new SimpleInventory(9), playerInventory, new ArrayPropertyDelegate(2));
    }

    public AnomalyCaptorScreenHandler(int syncId, Inventory inventory, PlayerInventory playerInventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.ANOMALY_CAPTOR, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);

        for(int m = 0; m < 2; ++m){
            for(int l = 0; l < 3; ++l){
                this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 28 + m * 18){

                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        for (int m = 0; m < 3; ++m) {
            this.addSlot(new Slot(inventory, m + 6, 134, 18 + m * 18){

                @Override
                public int getMaxItemCount(ItemStack stack) {
                    return 1;
                }
            });
        }

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

    public float getCooldownTime(){
        int i = this.propertyDelegate.get(1) - this.propertyDelegate.get(0);
        return (float) (i/20.0);
    }

    public int getCooldownProgress() {
        int i = this.propertyDelegate.get(0);
        if (i == 0) {
            return 0;
        }
        return i * 56 / this.propertyDelegate.get(1);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 9) {
                if (!this.insertItem(itemStack2, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index < 45 && !this.insertItem(itemStack2, 6, 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }
            slot.markDirty();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTakeItem(player, itemStack2);
            this.sendContentUpdates();
        }
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

}
