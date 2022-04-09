package net.piinut.voidophobia.gui.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.VuxFilterMachineBlockEntity;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;

public class VuxFilterMachineScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final World world;

    public VuxFilterMachineScreenHandler(int syncId, PlayerInventory playerInventory){
        this(syncId, playerInventory, new SimpleInventory(3), new ArrayPropertyDelegate(2));
    }

    public VuxFilterMachineScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.VUX_FILTER_MACHINE, syncId);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.getWorld();
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 27, 38));
        this.addSlot(new Slot(inventory, 1, 67, 38){
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.VUX_FILTER);
            }
        });
        this.addSlot(new Slot(inventory, 2, 107, 38){
            @Override
            public boolean canInsert(ItemStack stack) {
                return false;
            }
        });

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

    public int getProcessProgress() {
        int i = this.propertyDelegate.get(0);
        if (i == 0) {
            return 0;
        }
        return i * 60 / VuxFilterMachineBlockEntity.TOTAL_PROCESS_TIME;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public int getVuxStorage() {
        float i = this.propertyDelegate.get(1);
        if (i == 0) {
            return 0;
        }
        return (int) (i * 56 / VuxFilterMachineBlockEntity.MAX_VUX_CAPACITY);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (index == 2) {
                item.onCraft(itemStack2, player.world, player);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index == 0 ? !this.insertItem(itemStack2, 2, 38, false) : (this.world.getRecipeManager().getFirstMatch(ModRecipeTypes.VUX_FILTERING, new SimpleInventory(itemStack2), this.world).isPresent() ? !this.insertItem(itemStack2, 0, 1, false) : (index >= 2 && index < 29 ? !this.insertItem(itemStack2, 29, 38, false) : index >= 29 && index < 38 && !this.insertItem(itemStack2, 2, 29, false)))) {
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