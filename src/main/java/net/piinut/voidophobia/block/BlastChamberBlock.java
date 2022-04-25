package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.piinut.voidophobia.block.blockEntity.AlloyFurnaceBlockEntity;
import net.piinut.voidophobia.block.blockEntity.BlastChamberBlockEntity;
import net.piinut.voidophobia.block.blockEntity.LaserEngravingMachineBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Random;

public class BlastChamberBlock extends BlockWithEntity implements VuxProvider {

    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(1, 4, 1, 15, 14, 15);
    private static final VoxelShape SHAPE = VoxelShapes.union(SHAPE1, SHAPE2);

    public BlastChamberBlock(Settings settings) {
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
        return new BlastChamberBlockEntity(pos, state);
    }

    @Override
    public double getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random) {
        BlastChamberBlockEntity blockEntity = (BlastChamberBlockEntity) world.getBlockEntity(pos);
        return blockEntity.getVuxStored();
    }

    @Override
    public void handleVuxConsumption(World world, BlockState state, BlockPos pos, double input) {
        BlastChamberBlockEntity blockEntity = (BlastChamberBlockEntity) world.getBlockEntity(pos);
        blockEntity.removeVux(input);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? checkType(type, ModBlockEntities.BLAST_CHAMBER, (BlastChamberBlockEntity::clientTick)) : checkType(type, ModBlockEntities.BLAST_CHAMBER, (BlastChamberBlockEntity::serverTick));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof BlastChamberBlockEntity) {
                ItemScatterer.spawn(world, pos, (BlastChamberBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }
}
