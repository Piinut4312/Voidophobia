package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.BasicVuxductBlockEntity;
import net.piinut.voidophobia.block.blockEntity.CompactNetherPortalBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CompactNetherPortalBlock extends AbstractCompactPortalBlock {

    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(0, 12, 0, 16,16, 16);
    private static final VoxelShape SHAPE3 = Block.createCuboidShape(0, 4, 0, 4, 12, 4);
    private static final VoxelShape SHAPE4 = Block.createCuboidShape(12, 4, 0, 16, 12, 4);
    private static final VoxelShape SHAPE5 = Block.createCuboidShape(12, 4, 12, 16, 12, 16);
    private static final VoxelShape SHAPE6 = Block.createCuboidShape(0, 4, 12, 4, 12, 16);
    private static final VoxelShape SHAPE7 = Block.createCuboidShape(4, 4, 1, 12, 12, 3);
    private static final VoxelShape SHAPE8 = Block.createCuboidShape(13, 4, 4, 15, 12, 12);
    private static final VoxelShape SHAPE9 = Block.createCuboidShape(4, 4, 13, 12, 12, 15);
    private static final VoxelShape SHAPE10 = Block.createCuboidShape(1, 4, 4, 3, 12, 12);
    private static final VoxelShape SHAPE11 = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
    private static final VoxelShape OUTLINE_SHAPE = VoxelShapes.union(SHAPE1, SHAPE2, SHAPE3, SHAPE4, SHAPE5, SHAPE6, SHAPE7, SHAPE8, SHAPE9, SHAPE10, SHAPE11);
    private static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public CompactNetherPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return OUTLINE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (random.nextInt(20) == 0) {
            world.playSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5f, random.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int i = 0; i < 4; ++i) {
            double d;
            double e = (double)pos.getY() + random.nextDouble();
            double f;
            double g;
            double h = ((double)random.nextFloat() - 0.5) * 0.5;
            double j;
            int k = random.nextInt(2) * 2 - 1;
            f = (double)pos.getZ() + 0.5 + 0.25 * (double)k;
            j = random.nextFloat() * 2.0f * (float)k;
            d = (double)pos.getX() + 0.5 + 0.25 * (double)k;
            g = random.nextFloat() * 2.0f * (float)k;
            world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CompactNetherPortalBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? null : checkType(type, ModBlockEntities.COMPACT_NETHER_PORTAL, (CompactNetherPortalBlockEntity::serverTick));
    }
}
