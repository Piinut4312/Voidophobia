package net.piinut.voidophobia.item.recipe;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class LaserEngravingRecipeSerializer implements RecipeSerializer<LaserEngravingRecipe> {

    public static final LaserEngravingRecipeSerializer INSTANCE = new LaserEngravingRecipeSerializer();

    @Override
    public LaserEngravingRecipe read(Identifier id, JsonObject json) {
        LaserEngravingJsonFormat recipeJson = new Gson().fromJson(json, LaserEngravingJsonFormat.class);
        if (recipeJson.modifier == null || recipeJson.type_name == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        Ingredient modifier = Ingredient.fromJson(recipeJson.modifier);
        String type = recipeJson.type_name;
        return new LaserEngravingRecipe(modifier, type, id);
    }

    @Override
    public LaserEngravingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient modifier = Ingredient.fromPacket(buf);
        String type = buf.readString();
        return new LaserEngravingRecipe(modifier, type, id);
    }

    @Override
    public void write(PacketByteBuf buf, LaserEngravingRecipe recipe) {
        recipe.getModifier().write(buf);
        buf.writeString(recipe.getOutputType());
    }
}
