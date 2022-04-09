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

public class VacuumCoatingRecipeSerializer implements RecipeSerializer<VacuumCoatingRecipe> {

    public static final VacuumCoatingRecipeSerializer INSTANCE = new VacuumCoatingRecipeSerializer();

    @Override
    public VacuumCoatingRecipe read(Identifier id, JsonObject json) {
        VacuumCoatingJsonFormat recipeJson = new Gson().fromJson(json, VacuumCoatingJsonFormat.class);
        if (recipeJson.input1 == null || recipeJson.input2 == null || recipeJson.result == null) {
            throw new JsonSyntaxException("A required attribute is missing!");
        }
        Ingredient input1 = Ingredient.fromJson(recipeJson.input1);
        Ingredient input2 = Ingredient.fromJson(recipeJson.input2);
        Item output = Registry.ITEM.getOrEmpty(new Identifier(recipeJson.result))
                .orElseThrow(() -> new JsonSyntaxException("No such item " + recipeJson.result));
        ItemStack outputStack = new ItemStack(output);
        int processTime = recipeJson.processTime;
        return new VacuumCoatingRecipe(input1, input2, outputStack, id, processTime);
    }

    @Override
    public VacuumCoatingRecipe read(Identifier id, PacketByteBuf buf) {
        Ingredient ingredient1 = Ingredient.fromPacket(buf);
        Ingredient ingredient2 = Ingredient.fromPacket(buf);
        ItemStack output = buf.readItemStack();
        int i = buf.readVarInt();
        return new VacuumCoatingRecipe(ingredient1, ingredient2, output, id, i);
    }

    @Override
    public void write(PacketByteBuf buf, VacuumCoatingRecipe recipe) {
        recipe.getFirstInput().write(buf);
        recipe.getSecondInput().write(buf);
        buf.writeItemStack(recipe.getOutput());
        buf.writeVarInt(recipe.getProcessTime());
    }
}
