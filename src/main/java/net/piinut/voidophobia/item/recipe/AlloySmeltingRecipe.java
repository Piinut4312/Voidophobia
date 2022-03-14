package net.piinut.voidophobia.item.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AlloySmeltingRecipe implements Recipe<Inventory> {

    private final Ingredient firstInput;
    private final Ingredient secondInput;
    private final ItemStack outputStack;
    private final Identifier id;
    private final float experience;
    private final int cookTime;

    public Ingredient getFirstInput() {
        return firstInput;
    }

    public Ingredient getSecondInput() {
        return secondInput;
    }

    public float getExperience() {
        return experience;
    }

    public int getCookTime() {
        return cookTime;
    }

    public AlloySmeltingRecipe(Ingredient input1, Ingredient input2, ItemStack output, float xp, int cookTime, Identifier id){
        this.firstInput = input1;
        this.secondInput = input2;
        this.outputStack = output;
        this.id = id;
        this.experience = xp;
        this.cookTime = cookTime;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack itemStack1 = inventory.getStack(0);
        ItemStack itemStack2 = inventory.getStack(1);
        return (firstInput.test(itemStack1) && secondInput.test(itemStack2)) || (firstInput.test(itemStack2) && secondInput.test(itemStack1));
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        ItemStack itemStack = outputStack.copy();
        itemStack.setCount(2);
        return itemStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return AlloySmeltingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ALLOY_SMELTING;
    }

}
