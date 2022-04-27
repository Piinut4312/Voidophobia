package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class LaserworkTableBlock extends BlockWithEntity {

    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, 10, 0, 16, 16, 16);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(0, 0, 12, 4, 10, 16);
    private static final VoxelShape SHAPE3 = Block.createCuboidShape(0, 0, 0, 4, 10, 4);
    private static final VoxelShape SHAPE4 = Block.createCuboidShape(12, 0, 0, 16, 10, 4);
    private static final VoxelShape SHAPE5 = Block.createCuboidShape(12, 0, 12, 16, 10, 16);

    private static final VoxelShape SHAPE = VoxelShapes.union(SHAPE1, SHAPE2, SHAPE3, SHAPE4, SHAPE5);

    public LaserworkTableBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}
