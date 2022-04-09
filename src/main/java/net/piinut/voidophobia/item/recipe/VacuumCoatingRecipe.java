package net.piinut.voidophobia.item.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class VacuumCoatingRecipe implements Recipe<Inventory> {

    private final Ingredient firstInput;
    private final Ingredient secondInput;
    private final ItemStack outputStack;
    private final Identifier id;
    private final int processTime;

    public VacuumCoatingRecipe(Ingredient firstInput, Ingredient secondInput, ItemStack outputStack, Identifier id, int processTime) {
        this.firstInput = firstInput;
        this.secondInput = secondInput;
        this.outputStack = outputStack;
        this.id = id;
        this.processTime = processTime;
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
        return itemStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.VACUUM_COATING;
    }

    public Ingredient getFirstInput() {
        return firstInput;
    }

    public Ingredient getSecondInput() {
        return secondInput;
    }

    public int getProcessTime() {
        return processTime;
    }
}
