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
    public static RecipeType<VuxFilteringRecipe> VUX_FILTERING;
    public static RecipeType<VacuumCoatingRecipe> VACUUM_COATING;
    public static RecipeType<ExplosiveBlastingRecipe> EXPLOSIVE_BLASTING;
    public static RecipeType<LaserActivatingRecipe> LASER_ACTIVATING;

    public static Identifier ALLOY_SMELTING_ID = getId("alloy_smelting");
    public static Identifier VUX_FORMING_ID = getId("vux_forming");
    public static Identifier LASER_ENGRAVING_ID = getId("laser_engraving");
    public static Identifier VUX_FILTERING_ID = getId("vux_filtering");
    public static Identifier VACUUM_COATING_ID = getId("vacuum_coating");
    public static Identifier EXPLOSIVE_BLASTING_ID = getId("explosive_blasting");
    public static Identifier LASER_ACTIVATING_ID = getId("laser_activating");

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
        registerSerializer(VUX_FILTERING_ID, VuxFilteringRecipeSerializer.INSTANCE);
        VUX_FILTERING = registerType(VUX_FILTERING_ID);
        registerSerializer(VACUUM_COATING_ID, VacuumCoatingRecipeSerializer.INSTANCE);
        VACUUM_COATING = registerType(VACUUM_COATING_ID);
        registerSerializer(EXPLOSIVE_BLASTING_ID, ExplosiveBlastingSerializer.INSTANCE);
        EXPLOSIVE_BLASTING = registerType(EXPLOSIVE_BLASTING_ID);
        registerSerializer(LASER_ACTIVATING_ID, LaserActivatingRecipeSerializer.INSTANCE);
        LASER_ACTIVATING = registerType(LASER_ACTIVATING_ID);
    }

}
