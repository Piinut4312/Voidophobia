package net.piinut.voidophobia.world.configuredFeature;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Blocks;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.UniformIntProvider;
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

    public static final RuleTest BEDROCK = new BlockMatchRuleTest(Blocks.BEDROCK);

    private static ConfiguredFeature<?, ?> SLIGHTLY_CRACKED_BEDROCK_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(OreFeatureConfig.createTarget(BEDROCK, ModBlocks.SLIGHTLY_CRACKED_BEDROCK.getDefaultState())), 6));

    private static ConfiguredFeature<?, ?> CRACKED_BEDROCK_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(OreFeatureConfig.createTarget(BEDROCK, ModBlocks.CRACKED_BEDROCK.getDefaultState())), 5));

    private static ConfiguredFeature<?, ?> GODEL_CRYSTAL_OVERWORLD_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(OreFeatureConfig.createTarget(OreConfiguredFeatures.BASE_STONE_OVERWORLD, ModBlocks.GODEL_CRYSTAL_BLOCK.getDefaultState())), 4));

    private static ConfiguredFeature<?, ?> GODEL_CRYSTAL_NETHER_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(OreFeatureConfig.createTarget(OreConfiguredFeatures.BASE_STONE_NETHER, ModBlocks.GODEL_CRYSTAL_BLOCK.getDefaultState())), 6));

    private static ConfiguredFeature<?, ?> CHROME_ORE_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.CHROME_ORE.getDefaultState())
                    , OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_CHROME_ORE.getDefaultState()))
                    , 9));

    private static ConfiguredFeature<?, ?> NICKEL_ORE_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.NICKEL_ORE.getDefaultState())
                    , OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_NICKEL_ORE.getDefaultState()))
                    , 9));

    private static ConfiguredFeature<?, ?> SILVER_ORE_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.SILVER_ORE.getDefaultState())
                    , OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_SILVER_ORE.getDefaultState()))
                    , 9));

    private static ConfiguredFeature<?, ?> SILVER_ORE_BURIED_CONFIGURED_FEATURE = Feature.ORE.configure(
            new OreFeatureConfig(List.of(
                    OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES, ModBlocks.SILVER_ORE.getDefaultState())
                    , OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.DEEPSLATE_SILVER_ORE.getDefaultState()))
                    , 9, 0.5f));

    public static PlacedFeature SLIGHTLY_CRACKED_BEDROCK_PLACED_FEATURE = SLIGHTLY_CRACKED_BEDROCK_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(8),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-60)));

    public static PlacedFeature CRACKED_BEDROCK_PLACED_FEATURE = CRACKED_BEDROCK_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(6),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-60)));

    public static PlacedFeature GODEL_CRYSTAL_OVERWORLD_PLACED_FEATURE = GODEL_CRYSTAL_OVERWORLD_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(4),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-48)));

    public static PlacedFeature GODEL_CRYSTAL_NETHER_PLACED_FEATURE = GODEL_CRYSTAL_NETHER_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(6),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-48)));

    public static PlacedFeature CHROME_ORE_PLACED_FEATURE = CHROME_ORE_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(6),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(28)));

    public static PlacedFeature NICKEL_ORE_PLACED_FEATURE = NICKEL_ORE_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(8),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-28), YOffset.fixed(64)));

    public static PlacedFeature SILVER_ORE_PLACED_FEATURE = SILVER_ORE_BURIED_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(4),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.trapezoid(YOffset.fixed(-64), YOffset.fixed(32)));

    public static PlacedFeature SILVER_ORE_LOWER_PLACED_FEATURE = SILVER_ORE_BURIED_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(UniformIntProvider.create(0, 1)),
            SquarePlacementModifier.of(),
            HeightRangePlacementModifier.uniform(YOffset.fixed(-64), YOffset.fixed(-48)));

    private static void registerConfiguredFeature(String id, ConfiguredFeature configuredFeature){
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, new Identifier(Voidophobia.MODID, id), configuredFeature);
    }

    private static void registerPlacedFeature(String id, PlacedFeature placedFeature){
        Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(Voidophobia.MODID, id), placedFeature);
    }

    public static void registerAll(){
        registerConfiguredFeature("chrome_ore", CHROME_ORE_CONFIGURED_FEATURE);
        registerConfiguredFeature("nickel_ore", NICKEL_ORE_CONFIGURED_FEATURE);
        registerConfiguredFeature("silver_ore", SILVER_ORE_CONFIGURED_FEATURE);
        registerConfiguredFeature("silver_ore_buried", SILVER_ORE_BURIED_CONFIGURED_FEATURE);
        registerConfiguredFeature("slightly_bedrock", SLIGHTLY_CRACKED_BEDROCK_CONFIGURED_FEATURE);
        registerConfiguredFeature("godel_crystal_overworld", GODEL_CRYSTAL_OVERWORLD_CONFIGURED_FEATURE);
        registerConfiguredFeature("godel_crystal_nether", GODEL_CRYSTAL_NETHER_CONFIGURED_FEATURE);
        registerConfiguredFeature("cracked_bedrock", CRACKED_BEDROCK_CONFIGURED_FEATURE);
        registerPlacedFeature("chrome_ore", CHROME_ORE_PLACED_FEATURE);
        registerPlacedFeature("nickel_ore", NICKEL_ORE_PLACED_FEATURE);
        registerPlacedFeature("silver_ore", SILVER_ORE_PLACED_FEATURE);
        registerPlacedFeature("silver_ore_lower", SILVER_ORE_LOWER_PLACED_FEATURE);
        registerPlacedFeature("slightly_cracked_bedrock", SLIGHTLY_CRACKED_BEDROCK_PLACED_FEATURE);
        registerPlacedFeature("godel_crystal_overworld", GODEL_CRYSTAL_OVERWORLD_PLACED_FEATURE);
        registerPlacedFeature("godel_crystal_nether", GODEL_CRYSTAL_NETHER_PLACED_FEATURE);
        registerPlacedFeature("cracked_bedrock", CRACKED_BEDROCK_PLACED_FEATURE);
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "chrome_ore")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "nickel_ore")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "silver_ore")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "silver_ore_lower")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "slightly_cracked_bedrock")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "cracked_bedrock")));
        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "godel_crystal_overworld")));
        BiomeModifications.addFeature(BiomeSelectors.foundInTheNether(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY, new Identifier(Voidophobia.MODID, "godel_crystal_nether")));
    }

}
