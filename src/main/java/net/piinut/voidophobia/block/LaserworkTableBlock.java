package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.LaserworkTableBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
        return new LaserworkTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? checkType(type, ModBlockEntities.LASERWORK_TABLE, (LaserworkTableBlockEntity::clientTick)) : checkType(type, ModBlockEntities.LASERWORK_TABLE, (LaserworkTableBlockEntity::serverTick));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LaserworkTableBlockEntity) {
                ItemScatterer.spawn(world, pos, (LaserworkTableBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack itemStack = player.getStackInHand(hand);
        LaserworkTableBlockEntity laserworkTableBlockEntity;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LaserworkTableBlockEntity) {
            laserworkTableBlockEntity = (LaserworkTableBlockEntity) blockEntity;
            if (!world.isClient) {
                if(!itemStack.isEmpty() && laserworkTableBlockEntity.addItem(player.getAbilities().creativeMode ? itemStack.copy() : itemStack)){
                    return ActionResult.SUCCESS;
                } else if (!laserworkTableBlockEntity.getStack(1).isEmpty()) {
                    ItemStack itemStack1 = laserworkTableBlockEntity.getStack(1).copy();
                    laserworkTableBlockEntity.removeStack(1);
                    player.giveItemStack(itemStack1);
                    return ActionResult.SUCCESS;
                } else if (!laserworkTableBlockEntity.getStack(0).isEmpty()) {
                    ItemStack itemStack1 = laserworkTableBlockEntity.getStack(0).copy();
                    laserworkTableBlockEntity.removeStack(0);
                    player.giveItemStack(itemStack1);
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }
}
