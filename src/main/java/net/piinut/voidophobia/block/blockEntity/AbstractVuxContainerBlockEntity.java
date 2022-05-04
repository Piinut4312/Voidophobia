package net.piinut.voidophobia.block.blockEntity;


import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractVuxContainerBlockEntity extends BlockEntity implements VuxContainer{

    private int vuxStored;
    private int vuxCapacity;
    private int vuxTransferRate;

    public AbstractVuxContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int vuxCapacity, int vuxTransferRate) {
        super(type, pos, state);
        this.vuxCapacity = vuxCapacity;
        this.vuxTransferRate = vuxTransferRate;
        this.vuxStored = 0;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.vuxStored = nbt.getInt("VuxStored");
        this.vuxCapacity = nbt.getInt("VuxCapacity");
        this.vuxTransferRate = nbt.getInt("VuxTransferRate");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("VuxStored", this.vuxStored);
        nbt.putInt("VuxCapacity", this.vuxCapacity);
        nbt.putInt("VuxTransferRate", this.vuxTransferRate);
    }

    @Override
    public int getVuxStored() {
        return this.vuxStored;
    }

    @Override
    public int getVuxCapacity() {
        return this.vuxCapacity;
    }

    @Override
    public int getVuxTransferRate() {
        return this.vuxTransferRate;
    }

    @Override
    public void setVuxStored(int value) {
        this.vuxStored = value;
    }

    @Override
    public void addVux(int value) {
        this.setVuxStored(MathHelper.clamp(this.vuxStored + Math.min(value, this.vuxTransferRate), 0, this.vuxCapacity));
    }

    @Override
    public void removeVux(int value) {
        this.setVuxStored(MathHelper.clamp(this.vuxStored - Math.min(value, this.vuxTransferRate), 0, this.vuxCapacity));
    }

    @Override
    public void setVuxCapacity(int value) {
        this.vuxCapacity = Math.max(0, value);
    }

    @Override
    public void setVuxTransferRate(int value) {
        this.vuxTransferRate = value;
    }
}
