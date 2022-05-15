package net.piinut.voidophobia.gui.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BasicItemPipeScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    public BasicItemPipeScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, new SimpleInventory(6), playerInventory);
    }

    public BasicItemPipeScreenHandler(int syncId, Inventory inventory, PlayerInventory playerInventory) {
        super(ModScreenHandlers.BASIC_ITEM_PIPE, syncId);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 72, 58));
        this.addSlot(new Slot(inventory, 1, 50, 36));
        this.addSlot(new Slot(inventory, 2, 72, 14));
        this.addSlot(new Slot(inventory, 3, 94, 36));
        this.addSlot(new Slot(inventory, 4, 72, 36));
        this.addSlot(new Slot(inventory, 5, 116, 36));

        for (int m = 0; m < 3; ++m) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (int m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }



    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index < 6 ? !this.insertItem(itemStack2, 6, 42, true) : index < 42 && !this.insertItem(itemStack2, 0, 6, false)) {
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
}
