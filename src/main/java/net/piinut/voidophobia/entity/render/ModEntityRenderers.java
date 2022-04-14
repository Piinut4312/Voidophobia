package net.piinut.voidophobia.entity.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.entity.ModEntities;
import net.piinut.voidophobia.entity.model.AbyssSpiderEntityModel;

public class ModEntityRenderers {

    public static final EntityModelLayer ABYSS_SPIDER_MODEL_LAYER = new EntityModelLayer(new Identifier(Voidophobia.MODID, "abyss_spider"), "main");

    public static void registerAll(){
        EntityRendererRegistry.register(ModEntities.ABYSS_SPIDER, AbyssSpiderEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ABYSS_SPIDER_MODEL_LAYER, AbyssSpiderEntityModel::getTexturedModelData);
    }

}
