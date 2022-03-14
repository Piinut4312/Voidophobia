package net.piinut.voidophobia.item.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;

public class ModRecipeTypes {

    public static RecipeType<AlloySmeltingRecipe> ALLOY_SMELTING;
    public static RecipeType<VuxFormingRecipe> VUX_FORMING;
    public static RecipeType<LaserEngravingRecipe> LASER_ENGRAVING;

    public static Identifier ALLOY_SMELTING_ID = getId("alloy_smelting");
    public static Identifier VUX_FORMING_ID = getId("vux_forming");
    public static Identifier LASER_ENGRAVING_ID = getId("laser_engraving");

    private static Identifier getId(String id){
        return new Identifier(Voidophobia.MODID, id);
    }

    private static void registerSerializer(Identifier id, RecipeSerializer<?> serializer){
        Registry.register(Registry.RECIPE_SERIALIZER, id, serializer);
    }

    private static <T extends Recipe<?>> RecipeType<T> registerType(Identifier id) {
        return Registry.register(Registry.RECIPE_TYPE, id, new RecipeType<T>(){

            public String toString() {
                return id.getPath();
            }
        });
    }

    public static void registerAll(){
        registerSerializer(ALLOY_SMELTING_ID, AlloySmeltingRecipeSerializer.INSTANCE);
        ALLOY_SMELTING = registerType(ALLOY_SMELTING_ID);
        registerSerializer(VUX_FORMING_ID, VuxFormingRecipeSerializer.INSTANCE);
        VUX_FORMING = registerType(VUX_FORMING_ID);
        registerSerializer(LASER_ENGRAVING_ID, LaserEngravingRecipeSerializer.INSTANCE);
        LASER_ENGRAVING = registerType(LASER_ENGRAVING_ID);
    }

}
