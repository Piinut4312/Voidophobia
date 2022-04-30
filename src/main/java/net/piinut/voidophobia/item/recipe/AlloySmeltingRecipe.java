package net.piinut.voidophobia.item.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class AlloySmeltingRecipe implements Recipe<Inventory> {

    private final Ingredient firstInput;
    private final Ingredient secondInput;

    public int getCount1() {
        return count1;
    }

    public int getCount2() {
        return count2;
    }

    private final int count1;
    private final int count2;
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

    public AlloySmeltingRecipe(Ingredient input1, Ingredient input2, int count1, int count2, ItemStack output, float xp, int cookTime, Identifier id){
        this.firstInput = input1;
        this.secondInput = input2;
        this.count1 = count1;
        this.count2 = count2;
        this.outputStack = output;
        this.id = id;
        this.experience = xp;
        this.cookTime = cookTime;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack itemStack1 = inventory.getStack(0);
        ItemStack itemStack2 = inventory.getStack(1);
        int c1 = itemStack1.getCount();
        int c2 = itemStack2.getCount();
        boolean b11 = firstInput.test(itemStack1);
        boolean b12 = firstInput.test(itemStack2);
        boolean b21 = secondInput.test(itemStack1);
        boolean b22 = secondInput.test(itemStack2);
        boolean b3 = (c1 >= count1 && c2 >= count2);
        boolean b4 = (c1 >= count2 && c2 >= count1);
        return (b11 && b22 && b3) || (b12 && b21 && b4);
    }

    public boolean canCraft(ItemStack itemStack1, ItemStack itemStack2){
        int c1 = itemStack1.getCount();
        int c2 = itemStack2.getCount();
        boolean b11 = firstInput.test(itemStack1);
        boolean b12 = firstInput.test(itemStack2);
        boolean b21 = secondInput.test(itemStack1);
        boolean b22 = secondInput.test(itemStack2);
        boolean b3 = (c1 >= count1 && c2 >= count2);
        boolean b4 = (c1 >= count2 && c2 >= count1);
        return (b11 && b22 && b3) || (b12 && b21 && b4);
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

    public int getCountForInput(ItemStack itemStack){
        if(getFirstInput().test(itemStack)){
            return count1;
        }
        if(getSecondInput().test(itemStack)){
            return count2;
        }
        return 0;
    }

}
