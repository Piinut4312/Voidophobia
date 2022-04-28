package net.piinut.voidophobia.block.blockEntity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import net.piinut.voidophobia.block.blockEntity.LaserworkTableBlockEntity;

public class LaserworkTableBlockEntityRenderer implements BlockEntityRenderer<LaserworkTableBlockEntity> {

    public LaserworkTableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx){

    }

    @Override
    public void render(LaserworkTableBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        ItemStack itemStack = entity.getStack(0);
        if(itemStack.getItem() instanceof BlockItem){
            matrices.translate(0.5, 1.1875, 0.5);
            matrices.scale(0.75f, 0.75f, 0.75f);
        }else{
            matrices.translate(0.5, 1.03125, 0.5);
            matrices.scale(0.5f, 0.5f, 0.5f);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        }
        renderItem(itemStack, light, overlay, matrices, vertexConsumers);
        matrices.pop();
    }

    private void renderItem(ItemStack itemStack, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers){
        MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);
    }
}
