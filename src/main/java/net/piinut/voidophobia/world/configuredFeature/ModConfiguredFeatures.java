package net.piinut.voidophobia.world.configuredFeature;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.*;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures {

    private static ConfiguredFeature<?, ?> CHROME_ORE_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.CHROME_ORE.getDefaultState())
                    , OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_CHROME_ORE.getDefaultState()))
                    , 8));

    private static ConfiguredFeature<?, ?> NICKEL_ORE_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.NICKEL_ORE.getDefaultState())
                    , OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_NICKEL_ORE.getDefaultState()))
                    , 8));

    public static PlacedFeature CHROME_ORE_PLACED_FEATURE = CHROME_ORE_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(6),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(28)));

    public static PlacedFeature NICKEL_ORE_PLACED_FEATURE = NICKEL_ORE_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(8),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-28), YOffset.fixed(60)));

    private static void registerConfiguredFeature(String id, ConfiguredFeature configuredFeature){
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Voidophobia.MODID, id), configuredFeature);
    }

    private static void registerPlacedFeature(String id, PlacedFeature placedFeature){
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(Voidophobia.MODID, id), placedFeature);
    }


    public static void registerAll(){
        registerConfiguredFeature("chrome_ore", CHROME_ORE_CONFIGURED_FEATURE);
        registerConfiguredFeature("nickel_ore", NICKEL_ORE_CONFIGURED_FEATURE);
        registerPlacedFeature("chrome_ore", CHROME_ORE_PLACED_FEATURE);
        registerPlacedFeature("nickel_ore", NICKEL_ORE_PLACED_FEATURE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "chrome_ore")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "nickel_ore")));
    }

}
