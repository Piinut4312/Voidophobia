package net.piinut.voidophobia.item.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class VuxFormingRecipe implements Recipe<Inventory> {

    public Ingredient getInput() {
        return input;
    }

    public int getOutputCount() {
        return outputCount;
    }

    private final Ingredient input;
    private final ItemStack output;

    public int getInputCount() {
        return inputCount;
    }

    private final int inputCount;
    private final int outputCount;
    private final Identifier id;

    public VuxFormingRecipe(Ingredient input, ItemStack output, int inputCount, int outputCount, Identifier id) {
        this.input = input;
        this.output = output;
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack itemStack = inventory.getStack(0);
        return input.test(itemStack) && itemStack.getCount() >= inputCount;
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
        ItemStack itemStack = output.copy();
        itemStack.setCount(outputCount);
        return itemStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return VuxFormingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.VUX_FORMING;
    }
}
