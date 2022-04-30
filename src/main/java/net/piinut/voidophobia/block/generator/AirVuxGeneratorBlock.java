package net.piinut.voidophobia.block.generator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.AirVuxGeneratorBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class AirVuxGeneratorBlock extends AbstractVuxGeneratorBlock{

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public AirVuxGeneratorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

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

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AirVuxGeneratorBlockEntity(pos, state);
    }

    @Override
    public int getVux(World world, BlockState state, BlockPos pos, Direction direction, Random random) {
        AirVuxGeneratorBlockEntity airVuxGeneratorBlockEntity = (AirVuxGeneratorBlockEntity) world.getBlockEntity(pos);
        return airVuxGeneratorBlockEntity.getVuxOutput();
    }

    @Override
    public void handleVuxConsumption(World world, BlockState state, BlockPos pos, int output) {
        AirVuxGeneratorBlockEntity blockEntity = (AirVuxGeneratorBlockEntity) world.getBlockEntity(pos);
        blockEntity.removeVux(output);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient()? checkType(type, ModBlockEntities.AIR_VUX_GENERATOR, (AirVuxGeneratorBlockEntity::clientTick)) : checkType(type, ModBlockEntities.AIR_VUX_GENERATOR, (AirVuxGeneratorBlockEntity::serverTick));
    }
}
