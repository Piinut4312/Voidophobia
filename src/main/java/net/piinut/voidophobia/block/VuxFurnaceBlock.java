package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.AbstractVuxductBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.block.blockEntity.VuxFurnaceBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class VuxFurnaceBlock extends BlockWithEntity implements VuxConsumer {

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty ON = BooleanProperty.of("on");

    public VuxFurnaceBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(ON, Boolean.FALSE));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(ON, false);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING).add(ON);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VuxFurnaceBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VuxFurnaceBlockEntity) {
                ItemScatterer.spawn(world, pos, (VuxFurnaceBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 2);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.consumeVux(world, state, pos, random);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 2);
    }

    @Override
    public int consumeVux(World world, BlockState state, BlockPos pos, Random random) {
        if(world.isClient()){
            return 0;
        }
        VuxFurnaceBlockEntity blockEntity = (VuxFurnaceBlockEntity) world.getBlockEntity(pos);
        int vuxIn = 0;
        for(Direction direction : DIRECTIONS){
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();
            if(neighborBlock instanceof VuxProvider){
                vuxIn += ((VuxProvider)neighborBlock).getVux(world, neighborState, neighborPos, direction.getOpposite(), random);
                ((VuxProvider)neighborBlock).handleVuxConsumption(world, neighborState, neighborPos, vuxIn);
            }else if(neighborBlock instanceof AbstractVuxductBlock){
                AbstractVuxductBlockEntity be = (AbstractVuxductBlockEntity) world.getBlockEntity(neighborPos);
                int tryConsumeVux = Math.min(blockEntity.requestVuxConsume(), be.getVuxOutput());
                be.removeVux(tryConsumeVux);
                vuxIn += tryConsumeVux;
            }
        }
        blockEntity.addVux(vuxIn);
        return vuxIn;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
            if (screenHandlerFactory != null) {
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? null : checkType(type, ModBlockEntities.VUX_FURNACE, (VuxFurnaceBlockEntity::tick));
    }

}
