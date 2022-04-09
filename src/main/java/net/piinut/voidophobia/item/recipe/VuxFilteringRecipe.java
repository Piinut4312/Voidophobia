package net.piinut.voidophobia.item.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.VuxFilterItem;

import java.util.Objects;

public class VuxFilteringRecipe implements Recipe<Inventory> {

    public Ingredient getIngredient() {
        return ingredient;
    }

    public String getFilter() {
        return filter;
    }

    public ItemStack getResult() {
        return result;
    }

    public int getCount() {
        return count;
    }

    private final Ingredient ingredient;
    private final String filter;
    private final ItemStack result;
    private int count;
    private final Identifier id;

    public VuxFilteringRecipe(Ingredient ingredient, String filter, ItemStack result, int count, Identifier id){
        this.ingredient = ingredient;
        this.filter = filter;
        this.result = result;
        this.count = count;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        ItemStack itemStack = inventory.getStack(0);
        ItemStack itemStack1 = inventory.getStack(1);
        if(!itemStack1.isOf(ModItems.VUX_FILTER)){
            return false;
        }
        NbtCompound nbt = itemStack1.getNbt();
        return ingredient.test(itemStack) && Objects.equals(nbt.getString(VuxFilterItem.TYPE_KEY), filter);
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
        itemStack.setCount(count);
        return itemStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return VuxFilteringRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.VUX_FILTERING;
    }
}
