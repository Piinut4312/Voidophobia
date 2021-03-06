package net.piinut.voidophobia.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.piinut.voidophobia.block.blockEntity.AbstractItemPipeBlockEntity;
import net.piinut.voidophobia.util.VoxelShapeHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class AbstractItemPipeBlock extends BlockWithEntity {

    public static final EnumProperty<ItemPipeNodeType> CONNECTION_NORTH = EnumProperty.of("north", ItemPipeNodeType.class);
    public static final EnumProperty<ItemPipeNodeType> CONNECTION_EAST = EnumProperty.of("east", ItemPipeNodeType.class);
    public static final EnumProperty<ItemPipeNodeType> CONNECTION_SOUTH = EnumProperty.of("south", ItemPipeNodeType.class);
    public static final EnumProperty<ItemPipeNodeType> CONNECTION_WEST = EnumProperty.of("west", ItemPipeNodeType.class);
    public static final EnumProperty<ItemPipeNodeType> CONNECTION_UP = EnumProperty.of("up", ItemPipeNodeType.class);
    public static final EnumProperty<ItemPipeNodeType> CONNECTION_DOWN = EnumProperty.of("down", ItemPipeNodeType.class);
    public static final Map<Direction, EnumProperty<ItemPipeNodeType>> DIRECTION_ENUM_PROPERTY_MAP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, CONNECTION_NORTH, Direction.EAST, CONNECTION_EAST, Direction.SOUTH, CONNECTION_SOUTH, Direction.WEST, CONNECTION_WEST, Direction.UP, CONNECTION_UP, Direction.DOWN, CONNECTION_DOWN));

    public static final VoxelShape BASE_SHAPE = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
    public static final VoxelShape PIPE_NORTH_TRANSFER_SHAPE = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    public static final VoxelShape PIPE_NORTH_IO_SHAPE = Block.createCuboidShape(4, 4, 0, 12, 12, 5);
    public static final VoxelShape PIPE_EAST_TRANSFER_SHAPE = VoxelShapeHelper.rotateY(PIPE_NORTH_TRANSFER_SHAPE);
    public static final VoxelShape PIPE_EAST_IO_SHAPE = VoxelShapeHelper.rotateY(PIPE_NORTH_IO_SHAPE);
    public static final VoxelShape PIPE_SOUTH_TRANSFER_SHAPE = VoxelShapeHelper.rotateY(PIPE_EAST_TRANSFER_SHAPE);
    public static final VoxelShape PIPE_SOUTH_IO_SHAPE = VoxelShapeHelper.rotateY(PIPE_EAST_IO_SHAPE);
    public static final VoxelShape PIPE_WEST_TRANSFER_SHAPE = VoxelShapeHelper.rotateY(PIPE_SOUTH_TRANSFER_SHAPE);
    public static final VoxelShape PIPE_WEST_IO_SHAPE = VoxelShapeHelper.rotateY(PIPE_SOUTH_IO_SHAPE);
    public static final VoxelShape PIPE_UP_TRANSFER_SHAPE = VoxelShapeHelper.rotateX(PIPE_NORTH_TRANSFER_SHAPE);
    public static final VoxelShape PIPE_UP_IO_SHAPE = VoxelShapeHelper.rotateX(PIPE_NORTH_IO_SHAPE);
    public static final VoxelShape PIPE_DOWN_TRANSFER_SHAPE = VoxelShapeHelper.rotateX(VoxelShapeHelper.rotateX(PIPE_UP_TRANSFER_SHAPE));
    public static final VoxelShape PIPE_DOWN_IO_SHAPE = VoxelShapeHelper.rotateX(VoxelShapeHelper.rotateX(PIPE_UP_IO_SHAPE));

    private static final Map<Direction, VoxelShape> PIPE_TRANSFER_SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, PIPE_NORTH_TRANSFER_SHAPE, Direction.EAST, PIPE_EAST_TRANSFER_SHAPE, Direction.SOUTH, PIPE_SOUTH_TRANSFER_SHAPE, Direction.WEST, PIPE_WEST_TRANSFER_SHAPE, Direction.UP, PIPE_UP_TRANSFER_SHAPE, Direction.DOWN, PIPE_DOWN_TRANSFER_SHAPE));
    private static final Map<Direction, VoxelShape> PIPE_IO_SHAPES = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, PIPE_NORTH_IO_SHAPE, Direction.EAST, PIPE_EAST_IO_SHAPE, Direction.SOUTH, PIPE_SOUTH_IO_SHAPE, Direction.WEST, PIPE_WEST_IO_SHAPE, Direction.UP, PIPE_UP_IO_SHAPE, Direction.DOWN, PIPE_DOWN_IO_SHAPE));

    public AbstractItemPipeBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(CONNECTION_NORTH, ItemPipeNodeType.NONE).with(CONNECTION_EAST, ItemPipeNodeType.NONE).with(CONNECTION_SOUTH, ItemPipeNodeType.NONE).with(CONNECTION_WEST, ItemPipeNodeType.NONE).with(CONNECTION_UP, ItemPipeNodeType.NONE).with(CONNECTION_DOWN, ItemPipeNodeType.NONE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONNECTION_NORTH, CONNECTION_EAST, CONNECTION_SOUTH, CONNECTION_WEST, CONNECTION_UP, CONNECTION_DOWN);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = BASE_SHAPE;
        for(Direction direction : DIRECTIONS){
            ItemPipeNodeType type = state.get(DIRECTION_ENUM_PROPERTY_MAP.get(direction));
            if(type == ItemPipeNodeType.TRANSFER){
                shape = VoxelShapes.union(shape, PIPE_TRANSFER_SHAPES.get(direction));
            }
            if(type == ItemPipeNodeType.INSERT || type == ItemPipeNodeType.EXTRACT){
                shape = VoxelShapes.union(shape, PIPE_IO_SHAPES.get(direction));
            }
        }
        return shape;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockState state = super.getPlacementState(ctx);
        for(Direction direction : DIRECTIONS){
            BlockPos neighborPos = ctx.getBlockPos().offset(direction, 1);
            BlockState neighborState = world.getBlockState(neighborPos);
            if(neighborState.getBlock() instanceof AbstractItemPipeBlock){
                state = state.with(DIRECTION_ENUM_PROPERTY_MAP.get(direction), ItemPipeNodeType.TRANSFER);
            }else{
                state = state.with(DIRECTION_ENUM_PROPERTY_MAP.get(direction), ItemPipeNodeType.NONE);
            }
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(DIRECTION_ENUM_PROPERTY_MAP.get(direction), getConnectionType(state, neighborState, direction));
    }

    protected void updateNeighbors(World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        updateNeighbors(world, pos);
        AbstractItemPipeBlockEntity blockEntity = (AbstractItemPipeBlockEntity) world.getBlockEntity(pos);
        if(blockEntity != null && !world.isClient()){
            blockEntity.network.updateNetwork(world);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        AbstractItemPipeBlockEntity blockEntity = (AbstractItemPipeBlockEntity) world.getBlockEntity(pos);
        if(blockEntity != null && !world.isClient()){
            blockEntity.network.updateNetwork(world);
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractItemPipeBlockEntity) {
                ItemScatterer.spawn(world, pos, ((AbstractItemPipeBlockEntity)blockEntity).inventory);
                ItemScatterer.spawn(world, pos, ((AbstractItemPipeBlockEntity)blockEntity).socketInventory);
                world.updateComparators(pos,this);
            }
            updateNeighbors(world, pos);
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    public static ItemPipeNodeType getConnectionType(BlockState state, BlockState neighbor, Direction direction) {
        ItemPipeNodeType nodeType = getNodeTypeForDirection(state, direction);
        if(neighbor.getBlock() instanceof AbstractItemPipeBlock){
            return ItemPipeNodeType.TRANSFER;
        }else if(nodeType == ItemPipeNodeType.EXTRACT || nodeType == ItemPipeNodeType.INSERT){
            return nodeType;
        }
        return ItemPipeNodeType.NONE;
    }

    public static ItemPipeNodeType getNodeTypeForDirection(BlockState state, Direction direction){
        return state.get(DIRECTION_ENUM_PROPERTY_MAP.get(direction));
    }

}
