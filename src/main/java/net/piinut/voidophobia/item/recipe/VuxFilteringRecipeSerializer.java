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

public class VuxFilteringRecipeSerializer implements RecipeSerializer<VuxFilteringRecipe> {

    public static final VuxFilteringRecipeSerializer INSTANCE = new VuxFilteringRecipeSerializer();

    @Override
    public VuxFilteringRecipe read(Identifier id, JsonObject json) {
        VuxFilteringJsonFormat recipeJson = new Gson().fromJson(json, VuxFilteringJsonFormat.class);
        if (recipeJson.ingredient == null || recipeJson.result == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        Ingredient ingredient = Ingredient.fromJson(recipeJson.ingredient);
        Item result = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.result))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.result));
        String filter = recipeJson.filter;
        int count = recipeJson.count;
        return new VuxFilteringRecipe(ingredient, filter, new ItemStack(result, count), count, id);
    }

    @Override
    public VuxFilteringRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient = Ingredient.fromPacket(buf);
        ItemStack result = buf.readItemStack();
        String filter = buf.readString();
        int count = buf.readVarInt();
        return new VuxFilteringRecipe(ingredient, filter, result, count, id);
    }

    @Override
    public void write(PacketByteBuf buf, VuxFilteringRecipe recipe) {
        recipe.getIngredient().write(buf);
        buf.writeItemStack(recipe.getResult());
        buf.writeString(recipe.getFilter());
        buf.writeVarInt(recipe.getCount());
    }
}
