package net.piinut.voidophobia.block.blockEntity.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.piinut.voidophobia.block.LaserTransmitterBlock;
import net.piinut.voidophobia.block.ModBlocks;
import net.piinut.voidophobia.block.blockEntity.LaserTransmitterBlockEntity;

public class LaserTransmitterBlockEntityRenderer implements BlockEntityRenderer<LaserTransmitterBlockEntity> {

    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

    public LaserTransmitterBlockEntityRenderer(BlockEntityRendererFactory.Context ctx){

    }

    @Override
    public void render(LaserTransmitterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        BlockState blockState = entity.getWorld().getBlockState(entity.getPos());
        Direction dir = Direction.UP;
        if(blockState.getBlock() == ModBlocks.LASER_TRANSMITTER){
            dir = blockState.get(LaserTransmitterBlock.FACING);
        }
        matrices.translate(0.5, 0.5, 0.5);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.multiply(dir.getRotationQuaternion());
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion(90.0f));
        matrices.translate(0, 0, 0.5);
        ItemStack itemStack = entity.getStack(0);
        MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers, 0);
        matrices.pop();
        if(entity.getBeamLength() > 0){
            renderBeam(matrices, vertexConsumers, tickDelta, entity.getWorld().getTime(), -0.3f, entity.getBeamLength()+0.8f, new float[]{185.0f /255, 80.0f/255, 216.0f/255}, dir);
        }
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, float yOffset, float maxY, float[] color, Direction dir) {
        LaserTransmitterBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0f, worldTime, yOffset, maxY, color, 0.1f, 0.1f, dir);
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, float yOffset, float maxY, float[] color, float innerRadius, float outerRadius, Direction dir) {
        float i = yOffset + maxY;
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(dir.getRotationQuaternion());
        float f = (float)Math.floorMod(worldTime, 40) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2f - (float)MathHelper.floor(g * 0.1f));
        float j = color[0];
        float k = color[1];
        float l = color[2];
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f * 2.25f - 45.0f));
        float m;
        float n = innerRadius;
        float o = innerRadius;
        float p;
        float q = -innerRadius;
        float r;
        float s;
        float t = -innerRadius;
        float w = -1.0f + h;
        float x = maxY * heightScale * (0.5f / innerRadius) + w;
        LaserTransmitterBlockEntityRenderer.renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), j, k, l, 1.0f, yOffset, i, 0.0f, n, o, 0.0f, q, 0.0f, 0.0f, t, 0.0f, 1.0f, x, w);
        matrices.pop();
        m = -outerRadius;
        n = -outerRadius;
        o = outerRadius;
        p = -outerRadius;
        q = -outerRadius;
        r = outerRadius;
        s = outerRadius;
        t = outerRadius;
        w = -1.0f + h;
        x = maxY * heightScale + w;
        LaserTransmitterBlockEntityRenderer.renderBeamLayer(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), j, k, l, 0.125f, yOffset, i, m, n, o, p, q, r, s, t, 0.0f, 1.0f, x, w);
        matrices.pop();
    }

    private static void renderBeamLayer(MatrixStack matrices, VertexConsumer vertices, float red, float green, float blue, float alpha, float yOffset, float height, float x1, float z1, float x2, float z2, float x3, float z3, float x4, float z4, float u1, float u2, float v1, float v2) {
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        LaserTransmitterBlockEntityRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x1, z1, x2, z2, u1, u2, v1, v2);
        LaserTransmitterBlockEntityRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x4, z4, x3, z3, u1, u2, v1, v2);
        LaserTransmitterBlockEntityRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x2, z2, x4, z4, u1, u2, v1, v2);
        LaserTransmitterBlockEntityRenderer.renderBeamFace(matrix4f, matrix3f, vertices, red, green, blue, alpha, yOffset, height, x3, z3, x1, z1, u1, u2, v1, v2);
    }

    private static void renderBeamFace(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, float yOffset, float height, float x1, float z1, float x2, float z2, float u1, float u2, float v1, float v2) {
        LaserTransmitterBlockEntityRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x1, z1, u2, v1);
        LaserTransmitterBlockEntityRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x1, z1, u2, v2);
        LaserTransmitterBlockEntityRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, yOffset, x2, z2, u1, v2);
        LaserTransmitterBlockEntityRenderer.renderBeamVertex(positionMatrix, normalMatrix, vertices, red, green, blue, alpha, height, x2, z2, u1, v1);
    }

    private static void renderBeamVertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertices, float red, float green, float blue, float alpha, float y, float x, float z, float u, float v) {
        vertices.vertex(positionMatrix, x, y, z).color(red, green, blue, alpha).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(LightmapTextureManager.MAX_LIGHT_COORDINATE).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }
}
