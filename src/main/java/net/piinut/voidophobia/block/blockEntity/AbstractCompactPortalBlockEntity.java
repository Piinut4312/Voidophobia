package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractCompactPortalBlockEntity extends BlockEntity implements BasicInventory, SidedInventory {

    protected IOMode mode;

    public AbstractCompactPortalBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public IOMode getMode(){
        return this.mode;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.mode = IOMode.valueOf(nbt.getString("mode"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("mode", this.getMode().toString());
    }


}
