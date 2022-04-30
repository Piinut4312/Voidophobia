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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.AbstractVuxductBlockEntity;
import net.piinut.voidophobia.block.blockEntity.LaserEngravingMachineBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.util.VoxelShapeHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LaserEngravingMachineBlock extends BlockWithEntity implements VuxConsumer{

    private static final VoxelShape SHAPE1 = Block.createCuboidShape(0, 0, 0, 16, 3, 16);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(7, 3, 13, 9, 16, 15);
    private static final VoxelShape SHAPE3 = Block.createCuboidShape(1, 3, 4, 3, 16, 6);
    private static final VoxelShape SHAPE4 = Block.createCuboidShape(13, 3, 4, 15, 16, 6);
    private static final VoxelShape SHAPE5 = Block.createCuboidShape(3, 14, 3, 13, 16, 13);
    private static final VoxelShape SHAPE6 = Block.createCuboidShape(7, 10, 7, 9, 14, 9);

    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(SHAPE1, SHAPE2, SHAPE3, SHAPE4, SHAPE5, SHAPE6);
    private static final VoxelShape EAST_SHAPE = VoxelShapeHelper.rotateY(NORTH_SHAPE);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapeHelper.rotateY(EAST_SHAPE);
    private static final VoxelShape WEST_SHAPE = VoxelShapeHelper.rotateY(SOUTH_SHAPE);

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    public LaserEngravingMachineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(LIT, Boolean.FALSE));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserEngravingMachineBlockEntity(pos, state);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite()).with(LIT, false);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LaserEngravingMachineBlockEntity) {
                ItemScatterer.spawn(world, pos, (LaserEngravingMachineBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
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
        builder.add(FACING).add(LIT);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 1);
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

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        this.consumeVux(world, state, pos, random);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 1);
    }

    @Override
    public int consumeVux(World world, BlockState state, BlockPos pos, Random random) {
        if(world.isClient()){
            return 0;
        }
        LaserEngravingMachineBlockEntity blockEntity = (LaserEngravingMachineBlockEntity) world.getBlockEntity(pos);
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? checkType(type, ModBlockEntities.LASER_ENGRAVING_MACHINE, (LaserEngravingMachineBlockEntity::clientTick)) : checkType(type, ModBlockEntities.LASER_ENGRAVING_MACHINE, (LaserEngravingMachineBlockEntity::serverTick));
    }


}
