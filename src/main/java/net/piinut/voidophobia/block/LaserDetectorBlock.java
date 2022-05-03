package net.piinut.voidophobia.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.LaserTransmitterBlockEntity;

import java.util.Random;

public class LaserDetectorBlock extends Block {

    public static final BooleanProperty POWERED = Properties.POWERED;

    public LaserDetectorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return state.getWeakRedstonePower(world, pos, direction);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        if (state.get(POWERED)) {
            return 15;
        }
        return 0;
    }

    private void updateNeighbors(World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        for(Direction dir : DIRECTIONS){
            world.updateNeighborsAlways(pos.offset(dir), this);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        boolean shouldPower = false;
        for(Direction dir : DIRECTIONS){
            BlockPos.Mutable testPos = pos.mutableCopy();
            Vec3i vec = dir.getVector();
            for(int i = 0; i < 32; i++){
                testPos.move(vec);
                BlockState blockState = world.getBlockState(testPos);
                if(blockState.isOf(ModBlocks.LASER_TRANSMITTER)){
                    LaserTransmitterBlockEntity blockEntity = (LaserTransmitterBlockEntity) world.getBlockEntity(testPos);
                    if(blockEntity.getBeamLength() >= i && blockState.get(LaserTransmitterBlock.FACING) == dir.getOpposite()){
                        shouldPower = true;
                    }
                    break;
                }else if(!(blockState.getOpacity(world, pos) < 15)){
                    break;
                }
            }
        }
        world.createAndScheduleBlockTick(pos, this, 2);
        world.setBlockState(pos, state.with(POWERED, shouldPower));
        this.updateNeighbors(world, pos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, this, 2);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        if (state.get(POWERED)) {
            this.updateNeighbors(world, pos);
        }
        super.onStateReplaced(state, world, pos, newState, false);
    }
}
