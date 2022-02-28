package net.piinut.voidophobia.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.blockEntity.AbstractVuxductBlockEntity;
import java.util.Random;

public class VuxLampBlock extends Block implements VuxConsumer{

    public static final BooleanProperty LIT = Properties.LIT;
    public static final double LIT_THRESHOLD = 5.0;

    public VuxLampBlock(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState().with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 2);
    }

    private boolean canLitUp(World world, BlockState state, BlockPos pos, Random random){
        double vux = consumeVux(world, state, pos, random);
        return vux >= LIT_THRESHOLD;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        world.setBlockState(pos, state.with(LIT, canLitUp(world, state, pos, random)));
        world.createAndScheduleBlockTick(pos, state.getBlock(), 2);
    }

    @Override
    public double consumeVux(World world, BlockState state, BlockPos pos, Random random) {
        if(world.isClient){
            return 0;
        }
        double vuxIn = 0;
        for(Direction direction : DIRECTIONS){
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();
            if(neighborBlock instanceof VuxProvider){
                vuxIn += ((VuxProvider)neighborBlock).getVux(world, neighborState, neighborPos, direction.getOpposite(), random);
            }else if(neighborBlock instanceof AbstractVuxductBlock){
                AbstractVuxductBlockEntity be = (AbstractVuxductBlockEntity) world.getBlockEntity(neighborPos);
                double tryConsumeVux = Math.min(LIT_THRESHOLD, be.getVuxOutput());
                be.removeVux(tryConsumeVux);
                vuxIn += tryConsumeVux;
            }
        }
        if(vuxIn >= LIT_THRESHOLD){
            world.setBlockState(pos, state.with(LIT, true));
        }else{
            world.setBlockState(pos, state.with(LIT, false));
        }
        return vuxIn;
    }
}
