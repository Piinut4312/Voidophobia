package net.piinut.voidophobia;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.SpawnSettings;
import net.piinut.voidophobia.block.ModBlocks;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.block.blockEntity.renderer.LaserEngravingMachineBlockEntityRenderer;
import net.piinut.voidophobia.block.blockEntity.renderer.ReinforcedPistonBlockEntityRenderer;
import net.piinut.voidophobia.entity.ModEntities;
import net.piinut.voidophobia.entity.render.ModEntityRenderers;
import net.piinut.voidophobia.gui.screen.ModScreens;

public class VoidophobiaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerBlockEntityRenderers();
    }

    private void registerBlockEntityRenderers(){
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMPACT_NETHER_PORTAL, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.REINFORCED_GLASS, RenderLayer.getTranslucent());
        BlockEntityRendererRegistry.register(ModBlockEntities.REINFORCED_PISTON, ReinforcedPistonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.LASER_ENGRAVING_MACHINE, LaserEngravingMachineBlockEntityRenderer::new);
        ModScreens.registerAll();
        ModEntityRenderers.registerAll();
        BiomeModifications.addSpawn(BiomeSelectors.foundInOverworld(), SpawnGroup.MONSTER, ModEntities.ENSORCELLED, 3, 1, 2);
    }

}
