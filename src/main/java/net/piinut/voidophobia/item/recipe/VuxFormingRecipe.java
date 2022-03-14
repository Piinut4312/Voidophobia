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

    public int getCount() {
        return count;
    }

    private final Ingredient input;
    private final ItemStack output;
    private final int count;
    private final Identifier id;

    public VuxFormingRecipe(Ingredient input, ItemStack output, int count, Identifier id) {
        this.input = input;
        this.output = output;
        this.count = count;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack itemStack = inventory.getStack(0);
        return input.test(itemStack);
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
        itemStack.setCount(count);
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
