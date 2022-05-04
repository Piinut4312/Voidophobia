package net.piinut.voidophobia.block.blockEntity.renderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import net.piinut.voidophobia.block.blockEntity.BlastChamberBlockEntity;

public class BlastChamberBlockEntityRenderer implements BlockEntityRenderer<BlastChamberBlockEntity> {

    public BlastChamberBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    private void renderItem(ItemStack itemStack, ModelTransformation.Mode mode, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed){
        MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, mode, light, overlay, matrices, vertexConsumers, 0);
    }

    private void renderItemWithPosAndScale(ItemStack itemStack, double x, double y, double z, float scx, float scy, float scz, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumer){
        matrices.push();
        matrices.translate(x, y, z);
        matrices.scale(scx, scy, scz);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
        renderItem(itemStack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumer, 0);
        matrices.pop();
    }

    @Override
    public void render(BlastChamberBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack itemStack = entity.getStack(0);
        ItemStack itemStack1 = entity.getStack(1);
        int coolDown = entity.getPropertyDelegate().get(3);
        final float exp = 80f ;
        final float startSize = 0.3f;
        final float endSize = 0.7f;
        float explosiveSize = (float) (startSize + ((endSize - startSize)/(exp - 1))*(Math.pow(exp, 1 - coolDown/200.0f)-1));

        double offset = 0.2;
        double theta1 = Math.PI/3;

        if(!itemStack1.isEmpty()){
            renderItemWithPosAndScale(itemStack1, 0.5, 0.25, 0.5, explosiveSize, explosiveSize, explosiveSize, light, overlay, matrices, vertexConsumers);
            renderItemWithPosAndScale(itemStack, 0.5+offset*Math.cos(theta1), 0.25, 0.5+offset*Math.sin(theta1), 0.4f, 0.4f, 0.4f, light, overlay, matrices, vertexConsumers);
        }else{
            renderItemWithPosAndScale(itemStack, 0.5, 0.25, 0.5, 0.4f, 0.4f, 0.4f, light, overlay, matrices, vertexConsumers);
        }

    }
}
