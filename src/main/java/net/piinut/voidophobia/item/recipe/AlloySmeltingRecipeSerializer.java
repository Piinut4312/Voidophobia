package net.piinut.voidophobia.item.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


public class AlloySmeltingRecipeSerializer implements RecipeSerializer<AlloySmeltingRecipe> {

    public static final AlloySmeltingRecipeSerializer INSTANCE = new AlloySmeltingRecipeSerializer();

    @Override
    public AlloySmeltingRecipe read(Identifier id, JsonObject json) {
        AlloySmeltingJsonFormat recipeJson = new Gson().fromJson(json, AlloySmeltingJsonFormat.class);
        if (recipeJson.input1 == null || recipeJson.input2 == null || recipeJson.result == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        Ingredient input1 = Ingredient.fromJson(recipeJson.input1);
        int count1 = recipeJson.count1;
        Ingredient input2 = Ingredient.fromJson(recipeJson.input2);
        int count2 = recipeJson.count2;
        Item output = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.result))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.result));
        ItemStack outputStack = new ItemStack(output);
        float xp = recipeJson.experience;
        int cookTime = recipeJson.cookingTime;
        return new AlloySmeltingRecipe(input1, input2, count1, count2, outputStack, xp, cookTime, id);
    }

    @Override
    public AlloySmeltingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient1 = Ingredient.fromPacket(buf);
        int c1 = buf.readVarInt();
        Ingredient ingredient2 = Ingredient.fromPacket(buf);
        int c2 = buf.readVarInt();
        ItemStack output = buf.readItemStack();
        float f = buf.readFloat();
        int i = buf.readVarInt();
        return new AlloySmeltingRecipe(ingredient1, ingredient2, c1, c2, output, f, i, id);
    }

    @Override
    public void write(PacketByteBuf buf, AlloySmeltingRecipe recipe) {
        recipe.getFirstInput().write(buf);
        buf.writeVarInt(recipe.getCount1());
        recipe.getSecondInput().write(buf);
        buf.writeVarInt(recipe.getCount2());
        buf.writeItemStack(recipe.getOutput());
        buf.writeFloat(recipe.getExperience());
        buf.writeVarInt(recipe.getCookTime());
    }

}
