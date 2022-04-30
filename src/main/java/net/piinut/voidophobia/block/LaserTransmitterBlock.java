package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
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
import net.piinut.voidophobia.block.blockEntity.LaserTransmitterBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.item.LaserLensItem;
import net.piinut.voidophobia.util.VoxelShapeHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LaserTransmitterBlock extends BlockWithEntity implements VuxConsumer{

    private static final VoxelShape SHAPE1 = Block.createCuboidShape(2, 0, 2, 14, 2, 14);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(5, 3, 5, 6, 5, 6);
    private static final VoxelShape SHAPE3 = Block.createCuboidShape(10, 3, 5, 11, 5, 6);
    private static final VoxelShape SHAPE4 = Block.createCuboidShape(10, 3, 10, 11, 5, 11);
    private static final VoxelShape SHAPE5 = Block.createCuboidShape(5, 3, 10, 6, 5, 11);
    private static final VoxelShape SHAPE6 = Block.createCuboidShape(4, 2, 4, 12, 3, 12);
    private static final VoxelShape SHAPE7 = Block.createCuboidShape(6, 4, 5, 10, 5, 6);
    private static final VoxelShape SHAPE8 = Block.createCuboidShape(6, 4, 10, 10, 5, 11);
    private static final VoxelShape SHAPE9 = Block.createCuboidShape(5, 4, 6, 6, 5, 10);
    private static final VoxelShape SHAPE10 = Block.createCuboidShape(10, 4, 6, 11, 5, 10);

    private static final VoxelShape UP_SHAPE = VoxelShapes.union(SHAPE1, SHAPE2, SHAPE3, SHAPE4, SHAPE5, SHAPE6, SHAPE7, SHAPE8, SHAPE9, SHAPE10);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapeHelper.rotateX(UP_SHAPE);
    private static final VoxelShape DOWN_SHAPE = VoxelShapeHelper.rotateX(SOUTH_SHAPE);
    private static final VoxelShape WEST_SHAPE = VoxelShapeHelper.rotateY(SOUTH_SHAPE);
    private static final VoxelShape NORTH_SHAPE = VoxelShapeHelper.rotateY(WEST_SHAPE);
    private static final VoxelShape EAST_SHAPE = VoxelShapeHelper.rotateY(NORTH_SHAPE);

    public static final DirectionProperty FACING = FacingBlock.FACING;
    public static final BooleanProperty LIT = BooleanProperty.of("lit");

    public LaserTransmitterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP).with(LIT, Boolean.FALSE));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)){
            case UP -> {
                return UP_SHAPE;
            }
            case DOWN -> {
                return DOWN_SHAPE;
            }
            case NORTH -> {
                return NORTH_SHAPE;
            }
            case EAST -> {
                return EAST_SHAPE;
            }
            case SOUTH -> {
                return SOUTH_SHAPE;
            }
            default ->  {
                return WEST_SHAPE;
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction direction = ctx.getSide();
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(direction.getOpposite()));
        if (blockState.isOf(this) && blockState.get(FACING) == direction) {
            return this.getDefaultState().with(FACING, direction.getOpposite());
        }
        return this.getDefaultState().with(FACING, direction);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof LaserTransmitterBlockEntity laserTransmitterBlockEntity){
            ItemStack itemStack = laserTransmitterBlockEntity.getStack(0);
            ItemStack itemStack1 = player.getStackInHand(hand);
            if(world.isClient){
                return ActionResult.CONSUME;
            }
            if(itemStack.isEmpty()){
                if(itemStack1.getItem() instanceof LaserLensItem){
                    laserTransmitterBlockEntity.addItem(itemStack1);
                }
            }else{
                player.giveItemStack(laserTransmitterBlockEntity.getStack(0));
            }
            LaserTransmitterBlockEntity.updateBeam(world, pos, state, laserTransmitterBlockEntity);
            blockEntity.markDirty();
            return ActionResult.SUCCESS;
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof LaserTransmitterBlockEntity) {
                ItemScatterer.spawn(world, pos, (LaserTransmitterBlockEntity)blockEntity);
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserTransmitterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? checkType(type, ModBlockEntities.LASER_TRANSMITTER, (LaserTransmitterBlockEntity::clientTick)) : checkType(type, ModBlockEntities.LASER_TRANSMITTER, (LaserTransmitterBlockEntity::serverTick));
    }

    @Override
    public int consumeVux(World world, BlockState state, BlockPos pos, Random random) {
        if(world.isClient()){
            return 0;
        }
        LaserTransmitterBlockEntity blockEntity = (LaserTransmitterBlockEntity) world.getBlockEntity(pos);
        Direction direction = state.get(FACING).getOpposite();
        int vuxIn = 0;
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
        blockEntity.addVux(vuxIn);
        return vuxIn;
    }}
