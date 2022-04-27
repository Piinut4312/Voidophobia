package net.piinut.voidophobia.item.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class LaserActivatingRecipe implements Recipe<Inventory> {

    public Ingredient getSource() {
        return source;
    }

    public int getTime() {
        return time;
    }

    private final Ingredient source;
    private final ItemStack target;
    private final int time;
    private final Identifier id;

    public LaserActivatingRecipe(Ingredient ingredient, ItemStack result, int time, Identifier id) {
        this.source = ingredient;
        this.target = result;
        this.time = time;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack itemStack = inventory.getStack(0);
        return source.test(itemStack);
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
        return this.target;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.LASER_ACTIVATING;
    }
}
