package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.LaserTransmitterBlock;
import net.piinut.voidophobia.block.ModBlocks;
import net.piinut.voidophobia.item.LaserLensItem;
import net.piinut.voidophobia.item.ModItems;
import org.jetbrains.annotations.Nullable;

public class LaserTransmitterBlockEntity extends BlockEntity implements BasicInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private static final int DEFAULT_BEAM_LENGTH = 20;
    private static final float vuxConsumePerTick = 800;
    private static final float MAX_VUX_CAPACITY = 60000;
    private int processTime;
    private int processTimeTotal;
    private float vuxStored;
    private int beamLength;
    private BlockPos targetPos;
    private BlockState targetState;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            if(index == 0){
                return (int) LaserTransmitterBlockEntity.this.vuxStored;
            }else if(index == 1){
                return LaserTransmitterBlockEntity.this.processTime;
            }else if(index == 2){
                return LaserTransmitterBlockEntity.this.processTimeTotal;
            }else if(index == 3){
                return LaserTransmitterBlockEntity.this.beamLength;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if(index == 0){
                LaserTransmitterBlockEntity.this.vuxStored = value;
            }else if(index == 1){
                LaserTransmitterBlockEntity.this.processTime = value;
            }else if(index == 2){
                LaserTransmitterBlockEntity.this.processTimeTotal = value;
            }else if(index == 3){
                LaserTransmitterBlockEntity.this.beamLength = value;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public LaserTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_TRANSMITTER, pos, state);
        this.beamLength = DEFAULT_BEAM_LENGTH;
        this.processTime = 0;
        this.processTimeTotal = 0;
        this.vuxStored = 0;
        this.targetPos = BlockPos.ORIGIN;
        this.targetState = Blocks.AIR.getDefaultState();
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory);
        this.vuxStored = nbt.getFloat("VuxStored");
        this.processTime = nbt.getInt("ProcessTime");
        this.processTimeTotal = nbt.getInt("ProcessTimeTotal");
        this.beamLength = nbt.getInt("BeamLength");
        this.targetPos = NbtHelper.toBlockPos(nbt.getCompound("TargetPos"));
        this.targetState = NbtHelper.toBlockState(nbt.getCompound("TargetState"));
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putFloat("VuxStored", this.vuxStored);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putInt("ProcessTimeTotal", this.processTimeTotal);
        nbt.putInt("BeamLength", this.beamLength);
        nbt.put("TargetPos", NbtHelper.fromBlockPos(this.targetPos));
        nbt.put("TargetState", NbtHelper.fromBlockState(this.targetState));
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    private void updateProgress(){
        ++this.processTime;
        if(this.processTime > this.processTimeTotal){
            this.processTime = this.processTimeTotal;
        }
    }

    private static void updateAfterActivatingTarget(World world, LaserTransmitterBlockEntity blockEntity, BlockState newState, BlockPos blockPos, BlockState blockState){
        world.setBlockState(blockEntity.targetPos, newState, Block.NOTIFY_ALL);
        LaserTransmitterBlockEntity.updateBeam(world, blockPos, blockState, blockEntity);
        blockEntity.processTime = 0;
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, LaserTransmitterBlockEntity blockEntity) {
        boolean bl = false;
        if(!world.isClient){
            LaserTransmitterBlockEntity.updateBeam(world, blockPos, blockState, blockEntity);
            if(blockEntity.getBeamLength() > 0 && blockEntity.vuxStored >= LaserTransmitterBlockEntity.vuxConsumePerTick){
                blockEntity.consumeVux();
                bl = true;
                if(!blockEntity.getStack(0).isEmpty() && blockEntity.getStack(0).getItem() instanceof LaserLensItem laserLensItem){
                    if(laserLensItem == ModItems.DESTRUCTION_LASER_LENS){
                        blockEntity.processTimeTotal = (int) (20*world.getBlockState(blockEntity.targetPos).getHardness(world, blockEntity.targetPos));
                        if(blockEntity.processTimeTotal > 0){
                            blockEntity.updateProgress();
                            if(blockEntity.processTime == blockEntity.processTimeTotal){
                                world.breakBlock(blockEntity.targetPos, true);
                                LaserTransmitterBlockEntity.updateBeam(world, blockPos, blockState, blockEntity);
                                blockEntity.processTime = 0;
                            }
                        }
                    }else if(laserLensItem == ModItems.ACTIVATION_LASER_LENS){
                        if(blockEntity.targetState.isOf(ModBlocks.END_SAND)){
                            blockEntity.processTimeTotal = 1600;
                            blockEntity.updateProgress();
                            if(blockEntity.processTime == blockEntity.processTimeTotal){
                                BlockState newState = ModBlocks.BLOCK_OF_STARDUST.getDefaultState();
                                LaserTransmitterBlockEntity.updateAfterActivatingTarget(world, blockEntity, newState, blockPos, blockState);
                            }
                        }else if(blockEntity.targetState.isOf(Blocks.NETHERRACK)){
                            blockEntity.processTimeTotal = 3200;
                            blockEntity.updateProgress();
                            if(blockEntity.processTime == blockEntity.processTimeTotal){
                                BlockState newState = Blocks.GLOWSTONE.getDefaultState();
                                LaserTransmitterBlockEntity.updateAfterActivatingTarget(world, blockEntity, newState, blockPos, blockState);
                            }
                        }else if(blockEntity.targetState.isOf(Blocks.CARVED_PUMPKIN)){
                            blockEntity.processTimeTotal = 400;
                            blockEntity.updateProgress();
                            if(blockEntity.processTime == blockEntity.processTimeTotal){
                                BlockState newState = Blocks.JACK_O_LANTERN.getDefaultState();
                                LaserTransmitterBlockEntity.updateAfterActivatingTarget(world, blockEntity, newState, blockPos, blockState);
                            }
                        }
                    }
                }
            }
            if(bl){
                blockEntity.markDirty();
            }
            ((ServerWorld) world).getChunkManager().markForUpdate(blockEntity.getPos());
        }
    }

    private void consumeVux() {
        this.vuxStored -= LaserTransmitterBlockEntity.vuxConsumePerTick;
        if(this.vuxStored < 0){
            this.vuxStored = 0;
        }
    }

    public static void updateBeam(World world, BlockPos blockPos, BlockState blockState, LaserTransmitterBlockEntity blockEntity) {

        BlockPos.Mutable pos = blockPos.mutableCopy();
        Vec3i dir = blockState.get(LaserTransmitterBlock.FACING).getVector();
        ItemStack itemStack = blockEntity.inventory.get(0);
        int affectDistance = 0;
        int i;
        if(blockEntity.vuxStored >= LaserTransmitterBlockEntity.vuxConsumePerTick){
            if(itemStack.isEmpty()){
                affectDistance = LaserTransmitterBlockEntity.DEFAULT_BEAM_LENGTH;
            }else if(itemStack.getItem() instanceof LaserLensItem){
                affectDistance = ((LaserLensItem) itemStack.getItem()).getAffectDistance();
            }
        }
        for(i = 0; i < affectDistance; i++){
            pos.move(dir);
            BlockState state = world.getBlockState(pos);
            if(!(state.getOpacity(world, pos) < 15)){
                if(pos.getX() != blockEntity.targetPos.getX() || pos.getY() != blockEntity.targetPos.getY() || pos.getZ() != blockEntity.targetPos.getZ()){
                    blockEntity.processTime = 0;
                }
                blockEntity.targetPos = pos;
                blockEntity.targetState = state;
                break;
            }
        }

        blockEntity.setBeamLength(i);
    }

    private void setBeamLength(int i) {
        if(this.beamLength != i){
            this.beamLength = i;
        }
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, LaserTransmitterBlockEntity blockEntity) {
    }

    public void addItem(ItemStack itemStack1) {
        this.inventory.set(0, itemStack1.split(1));
        this.markDirty();
    }

    public int getBeamLength() {
        return this.beamLength;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public double requestVuxConsume() {
        if(this.vuxStored >= LaserTransmitterBlockEntity.MAX_VUX_CAPACITY){
            return 0;
        }
        return Math.min(LaserTransmitterBlockEntity.MAX_VUX_CAPACITY - this.vuxStored, 1800);
    }

    public void addVux(double vuxIn) {
        this.vuxStored += Math.min(vuxIn, 1800);
        if(this.vuxStored > LaserTransmitterBlockEntity.MAX_VUX_CAPACITY){
            this.vuxStored = LaserTransmitterBlockEntity.MAX_VUX_CAPACITY;
        }
    }
}
