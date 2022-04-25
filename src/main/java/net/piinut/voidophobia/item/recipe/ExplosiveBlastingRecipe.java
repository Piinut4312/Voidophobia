package net.piinut.voidophobia.item.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ExplosiveBlastingRecipe implements Recipe<Inventory> {

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Ingredient getExplosive() {
        return explosive;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public float getChance() {
        return chance;
    }

    private final Ingredient ingredient;
    private final Ingredient explosive;
    private final int inputCount;
    private final int outputCount;
    private final float chance;
    private final ItemStack result;
    private final Identifier id;

    public ExplosiveBlastingRecipe(Ingredient ingredient, Ingredient explosive, int inputCount, int outputCount, float chance, ItemStack result, Identifier id) {
        this.ingredient = ingredient;
        this.explosive = explosive;
        this.inputCount = inputCount;
        this.outputCount = outputCount;
        this.chance = chance;
        this.result = result;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        int count1 = inventory.getStack(0).getCount();
        return ingredient.test(inventory.getStack(0)) && explosive.test(inventory.getStack(1)) && count1 >= this.inputCount;
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
        ItemStack itemStack = result.copy();
        itemStack.setCount(outputCount);
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
        return ModRecipeTypes.EXPLOSIVE_BLASTING;
    }
}
