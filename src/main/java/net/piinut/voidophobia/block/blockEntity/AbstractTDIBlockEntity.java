package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.piinut.voidophobia.block.AbstractCompactPortalBlock;

public class AbstractTDIBlockEntity extends BlockEntity {

    protected boolean paired;
    protected boolean found_portal;
    protected TDIDimensionType paired_dim;
    protected BlockPos paired_pos;

    private static final String PAIRED = "Paired";
    private static final String PAIRED_POS = "PairedPortalPosition";
    private static final String FOUND_PORTAL = "FoundPortal";
    private static final String PAIRED_DIM = "PairedDimension";

    public AbstractTDIBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.paired = false;
        this.found_portal = false;
        this.paired_pos = pos;
        this.paired_dim = TDIDimensionType.OVERWORLD;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.paired = nbt.getBoolean(PAIRED);
        this.found_portal = nbt.getBoolean(FOUND_PORTAL);
        this.paired_pos = NbtHelper.toBlockPos(nbt.getCompound(PAIRED_POS));
        this.paired_dim = TDIDimensionType.valueOf(nbt.getString(PAIRED_DIM));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putBoolean(PAIRED, this.paired);
        nbt.putBoolean(FOUND_PORTAL, this.found_portal);
        nbt.put(PAIRED_POS, NbtHelper.fromBlockPos(this.paired_pos));
        nbt.putString(PAIRED_DIM, this.paired_dim.toString());
    }

    public TDIDimensionType getLocalDimension(){
        if(this.world.getDimension().hasEnderDragonFight()){
            return TDIDimensionType.END;
        }else if(this.world.getDimension().isPiglinSafe()){
            return TDIDimensionType.NETHER;
        }
        return TDIDimensionType.OVERWORLD;
    }

    public boolean updatePortalLink(){
        BlockPos belowPos = this.getPos().down();
        BlockState belowState = this.world.getBlockState(belowPos);
        this.found_portal = belowState.getBlock() instanceof AbstractCompactPortalBlock;
        this.markDirty();
        return this.found_portal;
    }

    public void pairing(BlockPos pos, TDIDimensionType dim){
        if(updatePortalLink()){
            this.paired_pos = pos;
            this.paired_dim = dim;
            this.paired = true;
            this.updatePortalLink();
            this.markDirty();
        }
    }

}
