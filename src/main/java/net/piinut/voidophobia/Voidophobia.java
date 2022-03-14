package net.piinut.voidophobia;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.piinut.voidophobia.block.ModBlocks;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.gui.handler.ModScreenHandlers;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import net.piinut.voidophobia.world.configuredFeature.ModConfiguredFeatures;
import net.piinut.voidophobia.world.feture.ModFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Voidophobia implements ModInitializer {

    public static final String MODID = "voidophobia";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        LOGGER.info("Voidophobia initialized.");
        ModBlocks.registerAll();
        ModItems.registerAll();
        ModBlockEntities.registerAll();
        ModFeatures.registerAll();
        ModConfiguredFeatures.registerAll();
        ModScreenHandlers.registerAll();
        ModRecipeTypes.registerAll();
    }
}
