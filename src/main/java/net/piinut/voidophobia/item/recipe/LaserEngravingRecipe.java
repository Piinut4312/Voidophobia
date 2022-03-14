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

public class LaserEngravingRecipe implements Recipe<Inventory> {

    public Ingredient getModifier() {
        return modifier;
    }

    public String getOutputType(){
        return type;
    }

    private final Ingredient modifier;
    private final String type;
    private final Identifier id;

    public LaserEngravingRecipe(Ingredient modifier, String type, Identifier id){
        this.modifier = modifier;
        this.type = type;
        this.id = id;
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        return modifier.test(inventory.getStack(0));
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
        ItemStack itemStack = new ItemStack(ModItems.VUX_FILTER);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString(VuxFilterItem.TYPE_KEY, type);
        return itemStack;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LaserEngravingRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.LASER_ENGRAVING;
    }
}
