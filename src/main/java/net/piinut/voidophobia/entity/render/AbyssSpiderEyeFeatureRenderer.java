package net.piinut.voidophobia.entity.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.EyesFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.entity.AbyssSpiderEntity;
import net.piinut.voidophobia.entity.model.AbyssSpiderEntityModel;

public class AbyssSpiderEyeFeatureRenderer extends EyesFeatureRenderer<AbyssSpiderEntity, AbyssSpiderEntityModel> {

    private static final Identifier TEXTURE = new Identifier(Voidophobia.MODID, "textures/entity/abyss_spider_eyes.png");
    private static final RenderLayer SKIN = RenderLayer.getEyes(TEXTURE);

    public AbyssSpiderEyeFeatureRenderer(FeatureRendererContext<AbyssSpiderEntity, AbyssSpiderEntityModel> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public RenderLayer getEyesTexture() {
        return SKIN;
    }
}
