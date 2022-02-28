package net.piinut.voidophobia.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AbstractVuxductBlock extends BlockWithEntity {

    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty[] DIRECTIONS = {DOWN, UP, NORTH, SOUTH, WEST, EAST};

    private static final VoxelShape BASE_SHAPE = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
    private static final VoxelShape NORTH_SHAPE = createPipeShape();
    private static final VoxelShape EAST_SHAPE = rotateY(NORTH_SHAPE);
    private static final VoxelShape SOUTH_SHAPE = rotateY(EAST_SHAPE);
    private static final VoxelShape WEST_SHAPE = rotateY(SOUTH_SHAPE);
    private static final VoxelShape UP_SHAPE = rotateX(NORTH_SHAPE);
    private static final VoxelShape DOWN_SHAPE = rotateX(rotateX(UP_SHAPE));
    private static final VoxelShape[] SHAPES = {DOWN_SHAPE, UP_SHAPE, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE};

    public AbstractVuxductBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(NORTH, false).with(SOUTH, false).with(EAST, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }

    private static Box rotateX(Box box){
        Vec3d pMin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d pMax = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d c = new Vec3d(0, 0.5, 0.5);
        Vec3d diffMin = c.subtract(pMin);
        Vec3d diffMax = c.subtract(pMax);
        Vec3d newMin = c.add(-diffMin.x, diffMin.z, -diffMin.y);
        Vec3d newMax = c.add(-diffMax.x, diffMax.z, -diffMax.y);
        return new Box(newMin, newMax);
    }

    private static Box rotateY(Box box){
        Vec3d pMin = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d pMax = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d c = new Vec3d(0.5, 0, 0.5);
        Vec3d diffMin = c.subtract(pMin);
        Vec3d diffMax = c.subtract(pMax);
        Vec3d newMin = c.add(diffMin.z, -diffMin.y, -diffMin.x);
        Vec3d newMax = c.add(diffMax.z, -diffMax.y, -diffMax.x);
        return new Box(newMin, newMax);
    }

    private static VoxelShape rotateX(VoxelShape voxelShape){
        List<Box> boxes = voxelShape.getBoundingBoxes();
        VoxelShape voxelShape1 = VoxelShapes.empty();
        for(Box box : boxes){
            voxelShape1 = VoxelShapes.union(voxelShape1, VoxelShapes.cuboid(rotateX(box)));
        }
        return voxelShape1;
    }

    private static VoxelShape rotateY(VoxelShape voxelShape){
        List<Box> boxes = voxelShape.getBoundingBoxes();
        VoxelShape voxelShape1 = VoxelShapes.empty();
        for(Box box : boxes){
            voxelShape1 = VoxelShapes.union(voxelShape1, VoxelShapes.cuboid(rotateY(box)));
        }
        return voxelShape1;
    }

    private static VoxelShape createPipeShape(){
        VoxelShape voxelShape1 = Block.createCuboidShape(7, 6, 1, 9, 7, 6);
        VoxelShape voxelShape2 = Block.createCuboidShape(7, 9, 1, 9, 10, 6);
        VoxelShape voxelShape3 = Block.createCuboidShape(6, 7, 1, 7, 9, 6);
        VoxelShape voxelShape4 = Block.createCuboidShape(9, 7, 1, 10, 9, 6);
        VoxelShape voxelShape5 = Block.createCuboidShape(6, 6, 0, 10, 7, 1);
        VoxelShape voxelShape6 = Block.createCuboidShape(6, 9, 0, 10, 10, 1);
        VoxelShape voxelShape7 = Block.createCuboidShape(6, 7, 0, 7, 9, 1);
        VoxelShape voxelShape8 = Block.createCuboidShape(9, 7, 0, 10, 9, 1);
        return VoxelShapes.union(voxelShape1, voxelShape2, voxelShape3, voxelShape4, voxelShape5, voxelShape6,voxelShape7, voxelShape8);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DOWN, UP, NORTH, SOUTH, WEST, EAST);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape voxelShape = BASE_SHAPE;
        for(int i = 0; i < 6; i++){
            if(state.get(DIRECTIONS[i])){
                voxelShape = VoxelShapes.union(voxelShape, SHAPES[i]);
            }
        }
        return voxelShape;
    }

    private static boolean canConnectTo(BlockState blockState){
        return blockState.getBlock() instanceof AbstractVuxductBlock || blockState.getBlock() instanceof VuxProvider || blockState.getBlock() instanceof VuxConsumer;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        return state.with(DIRECTIONS[direction.getId()], canConnectTo(neighborState));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos blockPos = ctx.getBlockPos();
        BlockPos northPos = blockPos.north();
        BlockPos southPos = blockPos.south();
        BlockPos eastPos = blockPos.east();
        BlockPos westPos = blockPos.west();
        BlockPos upPos = blockPos.up();
        BlockPos downPos = blockPos.down();
        BlockState northState = world.getBlockState(northPos);
        BlockState southState = world.getBlockState(southPos);
        BlockState eastState = world.getBlockState(eastPos);
        BlockState westState = world.getBlockState(westPos);
        BlockState upState = world.getBlockState(upPos);
        BlockState downState = world.getBlockState(downPos);
        return super.getPlacementState(ctx)
                .with(NORTH, canConnectTo(northState)).with(SOUTH, canConnectTo(southState))
                .with(EAST, canConnectTo(eastState)).with(WEST, canConnectTo(westState))
                .with(UP, canConnectTo(upState)).with(DOWN, canConnectTo(downState));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
