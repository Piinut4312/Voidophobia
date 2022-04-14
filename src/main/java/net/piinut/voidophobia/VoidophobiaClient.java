package net.piinut.voidophobia;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.piinut.voidophobia.block.ModBlocks;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.block.blockEntity.renderer.LaserEngravingMachineBlockEntityRenderer;
import net.piinut.voidophobia.block.blockEntity.renderer.ReinforcedPistonBlockEntityRenderer;
import net.piinut.voidophobia.entity.render.ModEntityRenderers;
import net.piinut.voidophobia.gui.screen.ModScreens;

public class VoidophobiaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerBlockEntityRenderers();
    }

    private void registerBlockEntityRenderers(){
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.COMPACT_NETHER_PORTAL, RenderLayer.getTranslucent());
        BlockEntityRendererRegistry.register(ModBlockEntities.REINFORCED_PISTON, ReinforcedPistonBlockEntityRenderer::new);
        BlockEntityRendererRegistry.register(ModBlockEntities.LASER_ENGRAVING_MACHINE, LaserEngravingMachineBlockEntityRenderer::new);
        ModScreens.registerAll();
        ModEntityRenderers.registerAll();
    }

}
