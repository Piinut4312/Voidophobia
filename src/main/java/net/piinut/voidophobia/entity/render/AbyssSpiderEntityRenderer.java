package net.piinut.voidophobia.entity.render;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.entity.AbyssSpiderEntity;
import net.piinut.voidophobia.entity.model.AbyssSpiderEntityModel;

public class AbyssSpiderEntityRenderer extends MobEntityRenderer<AbyssSpiderEntity, AbyssSpiderEntityModel> {

    private static final Identifier TEXTURE = new Identifier(Voidophobia.MODID, "textures/entity/abyss_spider.png");

    public AbyssSpiderEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AbyssSpiderEntityModel(context.getPart(ModEntityRenderers.ABYSS_SPIDER_MODEL_LAYER)), 0.75F);
    }

    @Override
    protected float getLyingAngle(AbyssSpiderEntity spiderEntity) {
        return 180.0f;
    }

    @Override
    public Identifier getTexture(AbyssSpiderEntity entity) {
        return TEXTURE;
    }
}
