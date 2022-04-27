package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.piinut.voidophobia.block.LaserEngravingMachineBlock;
import net.piinut.voidophobia.gui.handler.LaserEngravingMachineScreenHandler;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.VuxFilterItem;
import net.piinut.voidophobia.item.recipe.LaserEngravingRecipe;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import net.piinut.voidophobia.sound.ModSounds;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class LaserEngravingMachineBlockEntity extends BlockEntity implements BasicInventory, NamedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    int processTime;
    public static final int TOTAL_PROCESS_TIME = 100;
    float vuxStored;
    public static final float MAX_VUX_CAPACITY = 16000;
    private static final float VUX_CONSUME_PER_TICK = 40;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return LaserEngravingMachineBlockEntity.this.processTime;
                }
                case 1 -> {
                    return (int) LaserEngravingMachineBlockEntity.this.vuxStored;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> LaserEngravingMachineBlockEntity.this.processTime = value;
                case 1 -> LaserEngravingMachineBlockEntity.this.vuxStored = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    public LaserEngravingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_ENGRAVING_MACHINE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory);
        this.processTime = nbt.getInt("ProcessTime");
        this.vuxStored = nbt.getFloat("VuxStored");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putFloat("VuxStored", this.vuxStored);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public double requestVuxConsume() {
        if(this.vuxStored >= LaserEngravingMachineBlockEntity.MAX_VUX_CAPACITY){
            return 0;
        }
        return Math.min(LaserEngravingMachineBlockEntity.MAX_VUX_CAPACITY - this.vuxStored, 500);
    }

    public void addVux(double vuxIn) {
        this.vuxStored += Math.min(vuxIn, 500);
        if(this.vuxStored > LaserEngravingMachineBlockEntity.MAX_VUX_CAPACITY){
            this.vuxStored = LaserEngravingMachineBlockEntity.MAX_VUX_CAPACITY;
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Laser Engraving");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new LaserEngravingMachineScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, LaserEngravingMachineBlockEntity blockEntity){

        if(!world.isClient){
            boolean bl = blockEntity.isProcessing();
            boolean bl2 = false;
            if (blockEntity.isProcessing() && !blockEntity.inventory.get(0).isEmpty() && !blockEntity.inventory.get(1).isEmpty()) {
                LaserEngravingRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.LASER_ENGRAVING, blockEntity, world).orElse(null);
                if (!blockEntity.isProcessing() && blockEntity.canAcceptRecipeOutput(recipe)) {
                    if (blockEntity.isProcessing()) {
                        bl2 = true;
                    }
                }
                if (blockEntity.isProcessing() && blockEntity.canAcceptRecipeOutput(recipe) && blockEntity.vuxStored >= LaserEngravingMachineBlockEntity.VUX_CONSUME_PER_TICK) {
                    if(blockEntity.processTime < LaserEngravingMachineBlockEntity.TOTAL_PROCESS_TIME - 40 && blockEntity.processTime%40 == 0){
                        world.playSound(null, pos, ModSounds.BLOCK_LASER_ENGRAVING_MACHINE_PROCESSING, SoundCategory.BLOCKS, 0.2f, 1f);
                    }
                    if(blockEntity.processTime == LaserEngravingMachineBlockEntity.TOTAL_PROCESS_TIME - 40){
                        world.playSound(null, pos, ModSounds.BLOCK_LASER_ENGRAVING_MACHINE_FINISH, SoundCategory.BLOCKS, 0.2f, 1f);
                    }
                    world.setBlockState(pos, state.with(LaserEngravingMachineBlock.LIT, true));
                    ++blockEntity.processTime;
                    blockEntity.vuxStored -= LaserEngravingMachineBlockEntity.VUX_CONSUME_PER_TICK;
                    if (blockEntity.processTime == LaserEngravingMachineBlockEntity.TOTAL_PROCESS_TIME) {
                        blockEntity.processTime = 0;
                        blockEntity.craftRecipe(recipe);
                        world.setBlockState(pos, state.with(LaserEngravingMachineBlock.LIT, false));
                        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
                        bl2 = true;
                    }
                } else {
                    blockEntity.processTime = 0;
                }
            } else if (blockEntity.processTime > 0) {
                blockEntity.processTime = MathHelper.clamp(blockEntity.processTime - 2, 0, LaserEngravingMachineBlockEntity.TOTAL_PROCESS_TIME);
            }
            if (bl != blockEntity.isProcessing()) {
                bl2 = true;
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
            }
            if (bl2) {
                blockEntity.markDirty();
            }
            ((ServerWorld) world).getChunkManager().markForUpdate(blockEntity.getPos());
        }
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, LaserEngravingMachineBlockEntity blockEntity){
        if(world.isClient){
            Random random = world.getRandom();
            if(blockEntity.hasLaserBeam()){
                double x = pos.getX()+0.5;
                double y = pos.getY()+0.21;
                double z = pos.getZ()+0.5;
                for(int i = 0; i < 2; i++){
                    double dx = random.nextDouble()*0.12-0.06;
                    double dz = random.nextDouble()*0.12-0.06;
                    world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(ModItems.VUX_FILTER)), x+dx, y, z+dz, 0, 0.01, 0);
                }
            }
        }
    }

    private boolean canAcceptRecipeOutput(LaserEngravingRecipe recipe) {
        if (this.getStack(0).isEmpty() || this.getStack(1).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = this.getStack(1);

        if(!itemStack2.isOf(ModItems.VUX_FILTER)){
            return false;
        }

        return itemStack2.getNbt().getString(VuxFilterItem.TYPE_KEY) == VuxFilterItem.BLANK;
    }

    private void craftRecipe(LaserEngravingRecipe recipe) {
        if (recipe == null || !this.canAcceptRecipeOutput(recipe)) {
            return;
        }
        ItemStack itemStack = this.getStack(1);
        String type = recipe.getOutputType();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString(VuxFilterItem.TYPE_KEY, type);
        this.setStack(1, itemStack.copy());
    }

    private boolean isProcessing() {
        return this.processTime < TOTAL_PROCESS_TIME;
    }

    public boolean hasLaserBeam(){
        return this.processTime > 0 && this.isProcessing();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, inventory);
        nbtCompound.putInt("ProcessTime", processTime);
        return nbtCompound;
    }

}
