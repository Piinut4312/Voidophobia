package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public abstract class AbstractVuxGeneratorBlockEntity extends BlockEntity {

    private int vuxStored;
    private int vuxGenRate;
    private int vuxCapacity;
    private int vuxOutputRate;

    public AbstractVuxGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.vuxStored = 0;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.vuxStored = nbt.getInt("VuxStored");
        this.vuxGenRate = nbt.getInt("VuxGenerationRate");
        this.vuxCapacity = nbt.getInt("VuxCapacity");
        this.vuxOutputRate = nbt.getInt("VuxOutputRate");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("VuxStored", this.vuxStored);
        nbt.putInt("VuxGenerationRate", this.vuxGenRate);
        nbt.putInt("VuxCapacity", this.vuxCapacity);
        nbt.putInt("VuxOutputRate", this.vuxOutputRate);
    }

    public int getVuxStored(){
        return this.vuxStored;
    }

    public int getVuxGenRate(){
        return this.vuxGenRate;
    }

    public int getVuxCapacity(){
        return this.vuxCapacity;
    }

    public int getVuxOutputRate(){
        return this.vuxOutputRate;
    }

    public void setVuxStored(int value){
        this.vuxStored = Math.max(0, Math.min(value, getVuxCapacity()));
    }

    public void setVuxGenRate(int value){
        this.vuxGenRate = Math.max(value, 0);
    }

    public void setVuxCapacity(int value){
        this.vuxCapacity = Math.max(value, 0);
    }

    public void setVuxOutputRate(int value){
        this.vuxOutputRate = Math.max(value, 0);
    }

    public void addVux(int value){
        this.vuxStored = MathHelper.clamp(this.vuxStored+value, 0, this.vuxCapacity);
    }

    public void removeVux(int value){
        this.addVux(-value);
    }

    public abstract int getVuxOutput();
    public abstract void generateVux();

}
