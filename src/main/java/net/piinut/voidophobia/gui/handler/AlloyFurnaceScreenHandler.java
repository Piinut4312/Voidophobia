package net.piinut.voidophobia.gui.handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import net.piinut.voidophobia.gui.screen.slot.AlloyFurnaceOutputSlot;

public class AlloyFurnaceScreenHandler extends AbstractRecipeScreenHandler<Inventory> {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private final World world;

    public AlloyFurnaceScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(4), new ArrayPropertyDelegate(4));
    }

    public AlloyFurnaceScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ModScreenHandlers.ALLOY_FURNACE, syncId);
        checkSize(inventory, 4);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        this.world = playerInventory.player.world;
        inventory.onOpen(playerInventory.player);

        this.addSlot(new Slot(inventory, 0, 42, 17));
        this.addSlot(new Slot(inventory, 1, 70, 17));
        this.addSlot(new Slot(inventory, 2, 56, 53));
        this.addSlot(new AlloyFurnaceOutputSlot(playerInventory.player, inventory, 3, 116, 35));

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
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
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
    public void populateRecipeFinder(RecipeMatcher finder) {
        if (this.inventory instanceof RecipeInputProvider) {
            ((RecipeInputProvider) this.inventory).provideRecipeInputs(finder);
        }
    }

    @Override
    public void clearCraftingSlots() {
        this.getSlot(0).setStack(ItemStack.EMPTY);
        this.getSlot(1).setStack(ItemStack.EMPTY);
        this.getSlot(2).setStack(ItemStack.EMPTY);
    }

    @Override
    public boolean matches(Recipe<? super Inventory> recipe) {
        return recipe.matches(this.inventory, this.world);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 3;
    }

    @Override
    public int getCraftingWidth() {
        return 1;
    }

    @Override
    public int getCraftingHeight() {
        return 1;
    }

    @Override
    public int getCraftingSlotCount() {
        return 4;
    }

    public int getCookProgress() {
        int i = this.propertyDelegate.get(2);
        int j = this.propertyDelegate.get(3);
        if (j == 0 || i == 0) {
            return 0;
        }
        return i * 24 / j;
    }

    public int getFuelProgress() {
        int i = this.propertyDelegate.get(1);
        if (i == 0) {
            i = 200;
        }
        return this.propertyDelegate.get(0) * 13 / i;
    }

    public boolean isBurning() {
        return this.propertyDelegate.get(0) > 0;
    }

    @Override
    public RecipeBookCategory getCategory() {
        return null;
    }

    @Override
    public boolean canInsertIntoSlot(int index) {
        return index != 3;
    }
}
