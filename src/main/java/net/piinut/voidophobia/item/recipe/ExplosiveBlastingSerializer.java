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

public class ExplosiveBlastingSerializer implements RecipeSerializer<ExplosiveBlastingRecipe> {

    public static final ExplosiveBlastingSerializer INSTANCE = new ExplosiveBlastingSerializer();

    @Override
    public ExplosiveBlastingRecipe read(Identifier id, JsonObject json) {
        ExplosiveBlastingRecipeFormat recipeJson = new Gson().fromJson(json, ExplosiveBlastingRecipeFormat.class);
        if (recipeJson.ingredient == null || recipeJson.explosive == null || recipeJson.result == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        Ingredient ingredient = Ingredient.fromJson(recipeJson.ingredient);
        int count1 = recipeJson.inputCount;
        Ingredient explosive = Ingredient.fromJson(recipeJson.explosive);
        Item output = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.result))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.result));
        int count2 = recipeJson.outputCount;
        ItemStack outputStack = new ItemStack(output);
        float chance = recipeJson.chance;
        return new ExplosiveBlastingRecipe(ingredient, explosive, count1, count2, chance, outputStack, id);
    }

    @Override
    public ExplosiveBlastingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient1 = Ingredient.fromPacket(buf);
        int c1 = buf.readVarInt();
        Ingredient ingredient2 = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        int c2 = buf.readVarInt();
        float f = buf.readFloat();
        return new ExplosiveBlastingRecipe(ingredient1, ingredient2, c1, c2, f, output, id);
    }

    @Override
    public void write(PacketByteBuf buf, ExplosiveBlastingRecipe recipe) {
        recipe.getIngredient().write(buf);
        buf.writeVarInt(recipe.getInputCount());
        recipe.getExplosive().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeVarInt(recipe.getOutputCount());
        buf.writeFloat(recipe.getChance());
    }
}
