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

public class VuxFormingRecipeSerializer implements RecipeSerializer<VuxFormingRecipe> {

    public static final VuxFormingRecipeSerializer INSTANCE = new VuxFormingRecipeSerializer();

    @Override
    public VuxFormingRecipe read(Identifier id, JsonObject json) {
        VuxFormingJsonFormat recipeJson = new Gson().fromJson(json,VuxFormingJsonFormat.class);
        if (recipeJson.input == null || recipeJson.output == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        Ingredient input = Ingredient.fromJson(recipeJson.input);
        Item output = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.output))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.output));
        ItemStack outputStack = new ItemStack(output);
        int count = recipeJson.count;
        return new VuxFormingRecipe(input, outputStack, count, id);
    }

    @Override
    public VuxFormingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient input = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        int count = buf.readVarInt();
        return new VuxFormingRecipe(input, output, count, id);
    }

    @Override
    public void write(PacketByteBuf buf, VuxFormingRecipe recipe) {
        recipe.getInput().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeVarInt(recipe.getCount());
    }
}
