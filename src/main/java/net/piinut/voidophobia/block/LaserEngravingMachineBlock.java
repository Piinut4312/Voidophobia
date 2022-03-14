package net.piinut.voidophobia.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.blockEntity.AbstractVuxductBlockEntity;
import net.piinut.voidophobia.block.blockEntity.LaserEngravingMachineBlockEntity;
import net.piinut.voidophobia.block.blockEntity.ModBlockEntities;
import net.piinut.voidophobia.block.blockEntity.VuxFormingMachineBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LaserEngravingMachineBlock extends BlockWithEntity implements VuxConsumer{

    public LaserEngravingMachineBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserEngravingMachineBlockEntity(pos, state);
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
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.createAndScheduleBlockTick(pos, state.getBlock(), 2);
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
        world.createAndScheduleBlockTick(pos, state.getBlock(), 2);
    }

    @Override
    public double consumeVux(World world, BlockState state, BlockPos pos, Random random) {
        if(world.isClient()){
            return 0;
        }
        LaserEngravingMachineBlockEntity blockEntity = (LaserEngravingMachineBlockEntity) world.getBlockEntity(pos);
        double vuxIn = 0;
        for(Direction direction : DIRECTIONS){
            BlockPos neighborPos = pos.offset(direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            Block neighborBlock = neighborState.getBlock();
            if(neighborBlock instanceof VuxProvider){
                vuxIn += ((VuxProvider)neighborBlock).getVux(world, neighborState, neighborPos, direction.getOpposite(), random);
            }else if(neighborBlock instanceof AbstractVuxductBlock){
                AbstractVuxductBlockEntity be = (AbstractVuxductBlockEntity) world.getBlockEntity(neighborPos);
                double tryConsumeVux = Math.min(blockEntity.requestVuxConsume(), be.getVuxOutput());
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
        return world.isClient()? null : checkType(type, ModBlockEntities.LASER_ENGRAVING_MACHINE, (LaserEngravingMachineBlockEntity::tick));
    }
}
