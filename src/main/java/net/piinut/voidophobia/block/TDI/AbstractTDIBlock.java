package net.piinut.voidophobia.block.TDI;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.piinut.voidophobia.util.VoxelShapeHelper;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTDIBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    private static final VoxelShape SHAPE1 = Block.createCuboidShape(2, 0, 2, 14, 2, 14);
    private static final VoxelShape SHAPE2 = Block.createCuboidShape(6, 0, 0, 10, 4, 2);
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(SHAPE1, SHAPE2);
    private static final VoxelShape EAST_SHAPE = VoxelShapeHelper.rotateY(NORTH_SHAPE);
    private static final VoxelShape SOUTH_SHAPE = VoxelShapeHelper.rotateY(EAST_SHAPE);
    private static final VoxelShape WEST_SHAPE = VoxelShapeHelper.rotateY(SOUTH_SHAPE);

    public AbstractTDIBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch(state.get(FACING)){
            case WEST -> {return WEST_SHAPE;}
            case EAST -> {return EAST_SHAPE;}
            case SOUTH -> {return SOUTH_SHAPE;}
            default -> {return NORTH_SHAPE;}
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        return state.with(FACING, ctx.getPlayerFacing().getOpposite());
    }
}
