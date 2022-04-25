package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.AbstractVuxductBlock;
import net.piinut.voidophobia.block.VuxProvider;

public abstract class AbstractVuxductBlockEntity extends BlockEntity {

    protected double vuxCapacity;
    protected double vuxContaining;
    protected double maxVuxRate;
    protected double netVuxFlow;
    private static final String VUX = "vux";
    private static final String CAP = "capacity";
    private static final String RATE = "rate";
    private static final String FLOW = "flow";

    public AbstractVuxductBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, double vuxCapacity, double maxVuxRate) {
        super(type, pos, state);
        this.vuxCapacity = vuxCapacity;
        this.vuxContaining = 0;
        this.maxVuxRate = maxVuxRate;
        this.netVuxFlow = 0;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putDouble(VUX, this.vuxContaining);
        nbt.putDouble(CAP, this.vuxCapacity);
        nbt.putDouble(RATE, this.maxVuxRate);
        nbt.putDouble(FLOW, this.netVuxFlow);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.vuxContaining = nbt.getDouble(VUX);
        this.vuxCapacity = nbt.getDouble(CAP);
        this.maxVuxRate = nbt.getDouble(RATE);
        this.netVuxFlow = nbt.getDouble(FLOW);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public double getVuxContaining(){
        return this.vuxContaining;
    }

    public double getVuxOutput(){
        return Math.min(this.maxVuxRate, this.vuxContaining);
    }

    public void addVux(double vux){
        this.netVuxFlow += vux;
    }

    public void removeVux(double vux){
        this.netVuxFlow -= vux;
    }

    public void updateVux(){
        this.vuxContaining += this.netVuxFlow;
        if(this.vuxContaining > this.vuxCapacity){
            this.vuxContaining = this.vuxCapacity;
        }
        if(this.vuxContaining < 0){
            this.vuxContaining = 0;
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, AbstractVuxductBlockEntity be){
        BlockState blockState;
        Direction direction;
        Block block;
        BlockPos blockPos;
        for(int i = 0; i < 6; i++){
            if(!state.get(AbstractVuxductBlock.DIRECTIONS[i])) continue;
            direction = Direction.byId(i);
            blockPos = pos.offset(direction);
            blockState = world.getBlockState(blockPos);
            block = blockState.getBlock();
            if(block instanceof VuxProvider){
                double input = ((VuxProvider) blockState.getBlock()).getVux(world, blockState, blockPos, direction.getOpposite(), world.getRandom());
                input = Math.min(input, be.maxVuxRate);
                be.addVux(input);
                ((VuxProvider) blockState.getBlock()).handleVuxConsumption(world, blockState, blockPos, input);
            }else if(block instanceof AbstractVuxductBlock){
                AbstractVuxductBlockEntity neighborBE = (AbstractVuxductBlockEntity) world.getBlockEntity(blockPos);
                double neighborVux = neighborBE.getVuxContaining();
                double vuxDiff = (be.vuxContaining-neighborVux)*0.125;
                double adjustedVux = Math.min(vuxDiff, be.maxVuxRate);
                if(adjustedVux > 0){
                    be.removeVux(adjustedVux);
                    neighborBE.addVux(adjustedVux);
                }
            }
        }

        be.updateVux();
        be.netVuxFlow = 0;
        be.markDirty();
    }

}
