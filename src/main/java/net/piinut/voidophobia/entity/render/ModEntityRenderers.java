package net.piinut.voidophobia.entity.render;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.entity.ModEntities;
import net.piinut.voidophobia.entity.model.AbyssSpiderEntityModel;
import net.piinut.voidophobia.entity.model.EnsorcelledEntityModel;

public class ModEntityRenderers {

    public static final EntityModelLayer ABYSS_SPIDER_MODEL_LAYER = new EntityModelLayer(new Identifier(Voidophobia.MODID, "abyss_spider"), "main");
    public static final EntityModelLayer ENSORCELLED_MODEL_LAYER = new EntityModelLayer(new Identifier(Voidophobia.MODID, "ensorcelled"), "main");

    public static void registerAll(){
        EntityRendererRegistry.register(ModEntities.ABYSS_SPIDER, AbyssSpiderEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.ENSORCELLED, EnsorcelledEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(ABYSS_SPIDER_MODEL_LAYER, AbyssSpiderEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ENSORCELLED_MODEL_LAYER, EnsorcelledEntityModel::getTexturedModelData);
    }

}
