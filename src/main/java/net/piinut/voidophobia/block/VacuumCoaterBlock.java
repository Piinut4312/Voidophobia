package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.AbstractVuxductBlockEntity;
import net.piinut.voidophobia.block.blockEntity.VacuumCoaterBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.util.VoxelShapeHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class VacuumCoaterBlock extends BlockWithEntity implements VuxConsumer {

    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, 0, 0, 16, 4, 16);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(5, 4, 3, 13, 16, 11);
    private static final VoxelShape SHAPE3 = Block.createCuboidShape(1, 4, 2, 5, 12, 6);
    private static final VoxelShape SHAPE4 = Block.createCuboidShape(1, 4, 7, 5, 12, 11);
    private static final VoxelShape SHAPE5 = Block.createCuboidShape(0, 4, 12, 16, 14, 16);
    private static final VoxelShape SHAPE6 = Block.createCuboidShape(5, 9, 2, 13, 10, 3);
    private static final VoxelShape SHAPE7 = Block.createCuboidShape(13, 9, 3, 14, 10, 12);
    private static final VoxelShape SHAPE8 = Block.createCuboidShape(13, 4, 2, 14, 10, 3);
    private static final VoxelShape SHAPE9 = Block.createCuboidShape(2, 12, 4, 3, 13, 5);
    private static final VoxelShape SHAPE10 = Block.createCuboidShape(2, 12, 9, 3, 13, 10);
    private static final VoxelShape SHAPE11 = Block.createCuboidShape(2, 13, 4, 3, 14, 10);
    private static final VoxelShape SHAPE12 = Block.createCuboidShape(2, 9, 11, 3, 10, 12);
    private static final VoxelShape SHAPE13 = Block.createCuboidShape(2, 7, 11, 3, 8, 12);
    private static final VoxelShape SHAPE14 = Block.createCuboidShape(2, 5, 11, 3, 6, 12);

    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(SHAPE1, SHAPE2, SHAPE3, SHAPE4, SHAPE5, SHAPE6, SHAPE7, SHAPE8, SHAPE9, SHAPE10, SHAPE11, SHAPE12, SHAPE13, SHAPE14);
    private static final VoxelShape EAST_SHAPE = VoxelShapeHelper.rotateY(NORTH_SHAPE);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapeHelper.rotateY(EAST_SHAPE);
    private static final VoxelShape WEST_SHAPE = VoxelShapeHelper.rotateY(SOUTH_SHAPE);

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    
    public VacuumCoaterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)){
            case EAST -> {
                return EAST_SHAPE;
            }
            case SOUTH -> {
                return SOUTH_SHAPE;
            }
            case WEST -> {
                return WEST_SHAPE;
            }
            default -> {
                return NORTH_SHAPE;
            }
        }
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VacuumCoaterBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VacuumCoaterBlockEntity) {
                ItemScatterer.spawn(world, pos, (VacuumCoaterBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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
        builder.add(FACING);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 1);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.consumeVux(world, state, pos, random);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 1);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? null : checkType(type, ModBlockEntities.VACUUM_COATER, (VacuumCoaterBlockEntity::tick));
    }

    @Override
    public int consumeVux(World world, BlockState state, BlockPos pos, Random random) {
        if(world.isClient()){
            return 0;
        }
        VacuumCoaterBlockEntity blockEntity = (VacuumCoaterBlockEntity) world.getBlockEntity(pos);
        int vuxIn = 0;
        for(Direction direction : DIRECTIONS){
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();
            if(neighborBlock instanceof VuxProvider){
                vuxIn += ((VuxProvider)neighborBlock).getVux(world, neighborState, neighborPos, direction.getOpposite(), random);
            }else if(neighborBlock instanceof AbstractVuxductBlock){
                AbstractVuxductBlockEntity be = (AbstractVuxductBlockEntity) world.getBlockEntity(neighborPos);
                int tryConsumeVux = (int) Math.min(blockEntity.requestVuxConsume(), be.getVuxOutput());
                be.removeVux(tryConsumeVux);
                vuxIn += tryConsumeVux;
            }
        }
        blockEntity.addVux(vuxIn);
        return vuxIn;
    }
}
