package net.piinut.voidophobia.entity.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.piinut.voidophobia.entity.AbyssSpiderEntity;

public class AbyssSpiderEntityModel extends SpiderEntityModel<AbyssSpiderEntity> {

    private static final String BODY0 = "body0";
    /**
     * The key of the second model part of the body, whose value is {@value}.
     */
    private static final String BODY1 = "body1";
    /**
     * The key of the right middle front leg model part, whose value is {@value}.
     */
    private static final String RIGHT_MIDDLE_FRONT_LEG = "right_middle_front_leg";
    /**
     * The key of the left middle front leg model part, whose value is {@value}.
     */
    private static final String LEFT_MIDDLE_FRONT_LEG = "left_middle_front_leg";
    /**
     * The key of the right middle hind leg model part, whose value is {@value}.
     */
    private static final String RIGHT_MIDDLE_HIND_LEG = "right_middle_hind_leg";
    /**
     * The key of the left middle hind leg model part, whose value is {@value}.
     */
    private static final String LEFT_MIDDLE_HIND_LEG = "left_middle_hind_leg";


    public AbyssSpiderEntityModel(ModelPart root) {
        super(root);
    }

    public static TexturedModelData getTexturedModelData(){
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(40, 22).cuboid(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 6.0F), ModelTransform.pivot(0.0F, 16.0F, -6.0F));
        modelPartData2.addChild("left_fang", ModelPartBuilder.create().uv(0, 0).cuboid(-2.0F, 1.0F, -8.0F, 1.0F, 1.0F, 3.0F), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        modelPartData2.addChild("right_fang", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 1.0F, -8.0F, 1.0F, 1.0F, 3.0F), ModelTransform.pivot(0.0f, 0.0f, 0.0f));
        modelPartData.addChild(BODY0, ModelPartBuilder.create().uv(32, 34).cuboid(-3.0F, -1.0F, -5.0F, 6.0F, 4.0F, 8.0F), ModelTransform.pivot(0.0f, 15.0f, 0.0f));
        modelPartData.addChild(BODY1, ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -4.0F, -6.0F, 10.0F, 8.0F, 14.0F), ModelTransform.pivot(0.0f, 15.0f, 9.0f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(0, 26).cuboid(-17.0F, -1.0F, -1.0F, 18.0F, 2.0F, 2.0F);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(0, 22).cuboid(-1.0F, -1.0F, -1.0F, 18.0F, 2.0F, 2.0F);
        modelPartData.addChild(EntityModelPartNames.RIGHT_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.0F, 15.0F, 2.0F));
        modelPartData.addChild(EntityModelPartNames.LEFT_HIND_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 2.0f));
        modelPartData.addChild(RIGHT_MIDDLE_HIND_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 1.0f));
        modelPartData.addChild(LEFT_MIDDLE_HIND_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 1.0f));
        modelPartData.addChild(RIGHT_MIDDLE_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, 0.0f));
        modelPartData.addChild(LEFT_MIDDLE_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_FRONT_LEG, modelPartBuilder, ModelTransform.pivot(-4.0f, 15.0f, -1.0f));
        modelPartData.addChild(EntityModelPartNames.LEFT_FRONT_LEG, modelPartBuilder2, ModelTransform.pivot(4.0f, 15.0f, -1.0f));
        return TexturedModelData.of(modelData, 128, 64);
    }
}
