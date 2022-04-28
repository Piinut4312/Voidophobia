package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.LaserDetectorBlock;
import net.piinut.voidophobia.block.LaserTransmitterBlock;
import net.piinut.voidophobia.block.ModBlocks;
import net.piinut.voidophobia.item.LaserLensItem;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.recipe.LaserActivatingRecipe;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class LaserTransmitterBlockEntity extends BlockEntity implements BasicInventory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private static final int DEFAULT_BEAM_LENGTH = 0;
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
        this.beamLength = 0;
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
    }

    private static int getProcessTime(World world, Inventory inventory) {
        return world.getRecipeManager().getFirstMatch(ModRecipeTypes.LASER_ACTIVATING, inventory, world).map(LaserActivatingRecipe::getTime).orElse(200);
    }

    private static boolean canAcceptRecipeOutputAsBlock(LaserActivatingRecipe recipe, ItemStack source) {
        if (source.isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        return !itemStack.isEmpty() && source.getItem() instanceof BlockItem && itemStack.getItem() instanceof BlockItem;
    }

    private static boolean canAcceptRecipeOutput(LaserActivatingRecipe recipe, ItemStack source){
        return !source.isEmpty() && recipe != null;
    }

    private static ItemStack craftRecipe(LaserActivatingRecipe recipe, ItemStack source, boolean isLaserworkTable) {
        if (recipe == null || (!isLaserworkTable && !LaserTransmitterBlockEntity.canAcceptRecipeOutputAsBlock(recipe, source))) {
            return ItemStack.EMPTY;
        }
        return LaserTransmitterBlockEntity.canAcceptRecipeOutput(recipe, source)? recipe.getOutput() : ItemStack.EMPTY;
    }
    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, LaserTransmitterBlockEntity blockEntity) {
        boolean bl = false;
        LaserTransmitterBlockEntity.updateBeam(world, blockPos, blockState, blockEntity);
        if(blockEntity.getBeamLength() > 0 && !blockEntity.getStack(0).isEmpty()){
            if(blockEntity.getStack(0).getItem() instanceof LaserLensItem laserLensItem){
                bl = true;
                if(blockEntity.vuxStored >= laserLensItem.getVuxConsumption()){
                    blockEntity.consumeVux(laserLensItem.getVuxConsumption());
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
                        if(!blockEntity.targetState.isAir()){
                            boolean isLaserworkTable = blockEntity.targetState.isOf(ModBlocks.LASERWORK_TABLE);
                            ItemStack itemStack;
                            LaserworkTableBlockEntity laserworkTableBlockEntity = null;
                            if(isLaserworkTable){
                                laserworkTableBlockEntity = (LaserworkTableBlockEntity) world.getBlockEntity(blockEntity.targetPos);
                                itemStack = laserworkTableBlockEntity.getStack(0);
                            }else{
                                itemStack = new ItemStack(blockEntity.targetState.getBlock().asItem());
                            }
                            SimpleInventory dummyInventory = new SimpleInventory(1);
                            dummyInventory.setStack(0, itemStack);
                            LaserActivatingRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.LASER_ACTIVATING, dummyInventory, world).orElse(null);
                            boolean canAcceptRecipe = isLaserworkTable? canAcceptRecipeOutput(recipe, itemStack) : canAcceptRecipeOutputAsBlock(recipe, itemStack);
                            if(canAcceptRecipe){
                                blockEntity.processTimeTotal = LaserTransmitterBlockEntity.getProcessTime(world, dummyInventory);
                                blockEntity.updateProgress();
                                if(blockEntity.processTime == blockEntity.processTimeTotal){
                                    ItemStack resultStack = LaserTransmitterBlockEntity.craftRecipe(recipe, itemStack, isLaserworkTable);
                                    if(!resultStack.isEmpty()){
                                        if(isLaserworkTable){
                                            laserworkTableBlockEntity.craftItem((ServerWorld) world, recipe.getOutput());
                                        }else{
                                            BlockState newState = Block.getBlockFromItem(resultStack.getItem()).getDefaultState();
                                            LaserTransmitterBlockEntity.updateAfterActivatingTarget(world, blockEntity, newState, blockPos, blockState);
                                        }
                                        blockEntity.processTime = 0;
                                    }
                                }
                            }
                        }
                    }else if(laserLensItem == ModItems.BURNING_LASER_LENS){
                        Direction dir = blockState.get(LaserTransmitterBlock.FACING);
                        Vec3i unitVec3i = dir.getVector();
                        Vec3d pos1 = new Vec3d(blockPos.getX()+0.5, blockPos.getY()+0.5, blockPos.getZ()+0.5);
                        Vec3d pos2 = new Vec3d(pos1.getX(), pos1.getY(), pos1.getZ());
                        if(unitVec3i.getX() != 0){
                            pos1 = pos1.add(0, -0.2, -0.2);
                            pos2 = pos2.add(unitVec3i.getX()* blockEntity.getBeamLength(), 0.2, 0.2);
                        }else if(unitVec3i.getY() != 0){
                            pos1 = pos1.add(-0.2, 0, -0.2);
                            pos2 = pos2.add(0.2, unitVec3i.getY() * blockEntity.getBeamLength(), 0.2);
                        }else if(unitVec3i.getZ() != 0){
                            pos1 = pos1.add(-0.2, -0.2, 0);
                            pos2 = pos2.add(0.2, 0.2, unitVec3i.getZ() * blockEntity.getBeamLength());
                        }
                        List<LivingEntity> affectedEntity = world.getEntitiesByClass(LivingEntity.class, new Box(pos1, pos2), livingEntity -> true);
                        for(LivingEntity livingEntity : affectedEntity){
                            livingEntity.setOnFireFor(5);
                        }
                    }
                }else{
                    blockEntity.beamLength = 0;
                    world.setBlockState(blockPos, blockState.with(LaserDetectorBlock.POWERED, false));
                }
            }
        }
        if(bl){
            blockEntity.markDirty();
        }
        ((ServerWorld) world).getChunkManager().markForUpdate(blockEntity.getPos());
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, LaserTransmitterBlockEntity blockEntity) {
        if(blockEntity.getBeamLength() > 0){
            ItemStack itemStack = blockEntity.getStack(0);
            if(!itemStack.isEmpty() && blockEntity.processTime > 0){
                Random random = world.getRandom();
                Vec3i dir = blockState.get(LaserTransmitterBlock.FACING).getVector();
                float x = blockEntity.targetPos.getX() + 0.4f * (1-dir.getX()) + random.nextFloat()*0.2f;
                float y = blockEntity.targetPos.getY() + 0.4f * (1-dir.getY()) + random.nextFloat()*0.2f;
                float z = blockEntity.targetPos.getZ() + 0.4f * (1-dir.getZ()) + random.nextFloat()*0.2f;
                if(itemStack.getItem() == ModItems.DESTRUCTION_LASER_LENS){
                    for(int i = 0; i < 6; i++){
                        world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockEntity.targetState), x, y, z, random.nextFloat()*0.2-0.1, random.nextFloat()*0.2-0.1, random.nextFloat()*0.2-0.1);
                    }
                }
            }
        }
    }

    private void consumeVux(float vux) {
        this.vuxStored -= vux;
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
        if(itemStack.isEmpty()){
            affectDistance = LaserTransmitterBlockEntity.DEFAULT_BEAM_LENGTH;
        }else if(itemStack.getItem() instanceof LaserLensItem){
            affectDistance = ((LaserLensItem) itemStack.getItem()).getAffectDistance();
        }

        for(i = 0; i < affectDistance; i++){
            pos.move(dir);
            BlockState state = world.getBlockState(pos);
            if(!(state.getOpacity(world, pos) < 15) || (state.isOf(ModBlocks.LASERWORK_TABLE) && blockState.get(LaserTransmitterBlock.FACING) == Direction.DOWN)){
                if(pos.getX() != blockEntity.targetPos.getX() || pos.getY() != blockEntity.targetPos.getY() || pos.getZ() != blockEntity.targetPos.getZ() || state.getBlock() != blockEntity.targetState.getBlock()){
                    blockEntity.processTime = 0;
                }
                blockEntity.targetPos = pos;
                blockEntity.targetState = state;
                break;
            }
        }

        blockEntity.setBeamLength(i);
        if(i > 0){
            world.setBlockState(blockPos, blockState.with(LaserTransmitterBlock.LIT, true));
        }else{
            world.setBlockState(blockPos, blockState.with(LaserTransmitterBlock.LIT, false));
        }
    }

    private void setBeamLength(int i) {
        if(this.beamLength != i){
            this.beamLength = i;
        }
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
        return Math.min(LaserTransmitterBlockEntity.MAX_VUX_CAPACITY - this.vuxStored, 1250);
    }

    public void addVux(double vuxIn) {
        this.vuxStored += Math.min(vuxIn, 1250);
        if(this.vuxStored > LaserTransmitterBlockEntity.MAX_VUX_CAPACITY){
            this.vuxStored = LaserTransmitterBlockEntity.MAX_VUX_CAPACITY;
        }
    }

}
