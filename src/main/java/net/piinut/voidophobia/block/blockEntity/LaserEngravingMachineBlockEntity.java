package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.text.TranslatableText;
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

import java.util.Objects;
import java.util.Random;

public class LaserEngravingMachineBlockEntity extends AbstractVuxContainerBlockEntity implements BasicInventory, NamedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private int processTime;
    private int processTimeTotal;
    private int vuxConsumptionRate;
    public static final int DEFAULT_TOTAL_PROCESS_TIME = 100;
    public static final int DEFAULT_VUX_CAPACITY = 16000;
    public static final int DEFAULT_VUX_TRANSFER_RATE = 300;
    private static final int DEFAULT_VUX_CONSUMPTION_RATE = 60;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return LaserEngravingMachineBlockEntity.this.processTime;
                }
                case 1 -> {
                    return LaserEngravingMachineBlockEntity.this.processTimeTotal;
                }
                case 2 -> {
                    return LaserEngravingMachineBlockEntity.this.getVuxStored();
                }
                case 3 -> {
                    return LaserEngravingMachineBlockEntity.this.getVuxCapacity();
                }
                case 4 -> {
                    return LaserEngravingMachineBlockEntity.this.getVuxTransferRate();
                }
                default -> {
                    return 0;
                }
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> LaserEngravingMachineBlockEntity.this.processTime = value;
                case 1 -> LaserEngravingMachineBlockEntity.this.processTimeTotal = value;
                case 2 -> LaserEngravingMachineBlockEntity.this.setVuxStored(value);
                case 3 -> LaserEngravingMachineBlockEntity.this.setVuxCapacity(value);
                case 4 -> LaserEngravingMachineBlockEntity.this.setVuxTransferRate(value);
            }
        }

        @Override
        public int size() {
            return 5;
        }
    };

    public LaserEngravingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_ENGRAVING_MACHINE, pos, state, DEFAULT_VUX_CAPACITY, DEFAULT_VUX_TRANSFER_RATE);
        this.vuxConsumptionRate = DEFAULT_VUX_CONSUMPTION_RATE;
        this.processTimeTotal = DEFAULT_TOTAL_PROCESS_TIME;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory);
        this.processTime = nbt.getInt("ProcessTime");
        this.processTimeTotal = nbt.getInt("ProcessTimeTotal");
        this.vuxConsumptionRate = nbt.getInt("VuxConsumptionRate");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("ProcessTime", this.processTime);
        nbt.putInt("ProcessTimeTotal", this.processTimeTotal);
        nbt.putInt("VuxConsumptionRate", this.vuxConsumptionRate);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    public double requestVuxConsume() {
        if(this.getVuxStored() >= this.getVuxCapacity()){
            return 0;
        }
        return Math.min(this.getVuxCapacity() - this.getVuxStored(), this.getVuxTransferRate());
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.voidophobia.laser_engraving");
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
                if (blockEntity.isProcessing() && blockEntity.canAcceptRecipeOutput(recipe) && blockEntity.getVuxStored() >= blockEntity.vuxConsumptionRate) {
                    if(blockEntity.processTime < blockEntity.processTimeTotal - 40 && blockEntity.processTime%40 == 0){
                        world.playSound(null, pos, ModSounds.BLOCK_LASER_ENGRAVING_MACHINE_PROCESSING, SoundCategory.BLOCKS, 0.2f, 1f);
                    }
                    if(blockEntity.processTime == blockEntity.processTimeTotal - 40){
                        world.playSound(null, pos, ModSounds.BLOCK_LASER_ENGRAVING_MACHINE_FINISH, SoundCategory.BLOCKS, 0.2f, 1f);
                    }
                    world.setBlockState(pos, state.with(LaserEngravingMachineBlock.LIT, true));
                    ++blockEntity.processTime;
                    blockEntity.removeVux(blockEntity.vuxConsumptionRate);
                    if (blockEntity.processTime >= blockEntity.processTimeTotal) {
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
                blockEntity.processTime = MathHelper.clamp(blockEntity.processTime - 2, 0, blockEntity.processTimeTotal);
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

        return Objects.equals(itemStack2.getNbt().getString(VuxFilterItem.TYPE_KEY), VuxFilterItem.BLANK);
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
        return this.processTime < this.processTimeTotal;
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
        return createNbt();
    }

}
