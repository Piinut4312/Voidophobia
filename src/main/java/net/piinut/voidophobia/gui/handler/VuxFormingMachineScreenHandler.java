package net.piinut.voidophobia.gui.handler;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.VuxFormingMachineBlockEntity;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import net.piinut.voidophobia.item.recipe.VuxFormingRecipe;

import java.util.List;

public class VuxFormingMachineScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final World world;
    private ItemStack inputStack = ItemStack.EMPTY;
    private List<VuxFormingRecipe> availableRecipes = Lists.newArrayList();
    Runnable contentsChangedListener = () -> {};

    public VuxFormingMachineScreenHandler(int syncId, PlayerInventory playerInventory){
        this(syncId, playerInventory, new SimpleInventory(2), new ArrayPropertyDelegate(5));
    }

    public VuxFormingMachineScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.VUX_FORMING_MACHINE, syncId);
        this.world = playerInventory.player.getWorld();
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 20, 15){

            @Override
            public void markDirty() {
                super.markDirty();
                VuxFormingMachineScreenHandler.this.onContentChanged(inventory);
                VuxFormingMachineScreenHandler.this.contentsChangedListener.run();
            }
        });
        this.addSlot(new Slot(inventory, 1, 20, 53));

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

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        int newId = id;
        if (this.isInBounds(id)) {
            if(this.propertyDelegate.get(0) == id){
                newId = -1;
            }
            this.propertyDelegate.set(0, newId);
        }
        return true;
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    @Override
    public boolean canInsertIntoSlot(Slot slot) {
        return slot.id == 0;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (index == 1) {
                item.onCraft(itemStack2, player.world, player);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickTransfer(itemStack2, itemStack);
            } else if (index == 0 ? !this.insertItem(itemStack2, 2, 38, false) : (this.world.getRecipeManager().getFirstMatch(ModRecipeTypes.VUX_FORMING, new SimpleInventory(itemStack2), this.world).isPresent() ? !this.insertItem(itemStack2, 0, 1, false) : (index >= 2 && index < 29 ? !this.insertItem(itemStack2, 29, 38, false) : index >= 29 && index < 38 && !this.insertItem(itemStack2, 2, 29, false)))) {
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

    @Override
    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.getSlot(0).getStack();
        if (!itemStack.isOf(this.inputStack.getItem())) {
            this.inputStack = itemStack.copy();
            this.updateInput(inventory, itemStack);
        }
    }

    private void updateInput(Inventory input, ItemStack stack) {
        this.availableRecipes.clear();
        this.propertyDelegate.set(0, -1);
        if (!stack.isEmpty()) {
            this.availableRecipes = this.world.getRecipeManager().getAllMatches(ModRecipeTypes.VUX_FORMING, input, this.world);
        }
    }

    public int getProcessProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            return 0;
        }
        return i * 18 / VuxFormingMachineBlockEntity.TOTAL_PROCESS_TIME;
    }

    public int getVuxStorage(){
        float i = this.propertyDelegate.get(2);
        if(i == 0){
            return 0;
        }
        return (int) (i * 56 / VuxFormingMachineBlockEntity.DEFAULT_VUX_CAPACITY);
    }

    public List<VuxFormingRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public int getSelectedRecipe() {
        return this.propertyDelegate.get(0);
    }

    public boolean canCraft() {
        return !this.inventory.getStack(0).isEmpty() && !this.availableRecipes.isEmpty();
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

}
