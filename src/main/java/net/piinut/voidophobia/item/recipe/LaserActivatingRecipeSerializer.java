package net.piinut.voidophobia.item.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;

public class LaserActivatingRecipeSerializer implements RecipeSerializer<LaserActivatingRecipe> {

    public static final LaserActivatingRecipeSerializer INSTANCE = new LaserActivatingRecipeSerializer();

    @Override
    public LaserActivatingRecipe read(Identifier id, JsonObject json) {
        LaserActivatingRecipeFormat recipeJson = new Gson().fromJson(json, LaserActivatingRecipeFormat.class);
        if (recipeJson.source == null || recipeJson.target == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        Ingredient source = Ingredient.fromJson(recipeJson.source);
        ItemStack target = ShapedRecipe.outputFromJson(recipeJson.target);
        int time = recipeJson.time;
        return new LaserActivatingRecipe(source, target, time, id);
    }

    @Override
    public LaserActivatingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient source = Ingredient.fromPacket(buf);
        ItemStack target = buf.readItemStack();
        int time = buf.readVarInt();
        return new LaserActivatingRecipe(source, target, time, id);
    }

    @Override
    public void write(PacketByteBuf buf, LaserActivatingRecipe recipe) {
        recipe.getSource().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeVarInt(recipe.getTime());
    }
}
