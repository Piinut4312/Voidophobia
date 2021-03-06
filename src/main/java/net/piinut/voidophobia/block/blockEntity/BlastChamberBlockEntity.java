package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.piinut.voidophobia.item.recipe.ExplosiveBlastingRecipe;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlastChamberBlockEntity extends AbstractVuxContainerBlockEntity implements BasicInventory, SidedInventory {

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2};
    private static final int[] SIDE_SLOTS = new int[]{1};
    public DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int coolDown = MAX_COOLDOWN;
    public static final int MAX_COOLDOWN = 200;
    public static final int DEFAULT_VUX_CAPACITY = 40000;
    public static final int DEFAULT_VUX_TRANSFER_RATE = 4000;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch(index){
                case 0 -> {
                    return BlastChamberBlockEntity.this.getVuxStored();
                }
                case 1 -> {
                    return BlastChamberBlockEntity.this.getVuxCapacity();
                }
                case 2 -> {
                    return BlastChamberBlockEntity.this.getVuxTransferRate();
                }
                case 3 -> {
                    return BlastChamberBlockEntity.this.coolDown;
                }
                default -> {
                    return 0;
                }
            }
        }

        @Override
        public void set(int index, int value) {
            switch (index){
                case 0 -> BlastChamberBlockEntity.this.setVuxStored(value);
                case 1 -> BlastChamberBlockEntity.this.setVuxCapacity(value);
                case 2 -> BlastChamberBlockEntity.this.setVuxTransferRate(value);
                case 3 -> BlastChamberBlockEntity.this.coolDown = value;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    public BlastChamberBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLAST_CHAMBER, pos, state, DEFAULT_VUX_CAPACITY, DEFAULT_VUX_TRANSFER_RATE);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventory.clear();
        Inventories.readNbt(nbt, this.inventory);
        this.coolDown = nbt.getInt("CoolDown");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("CoolDown", this.coolDown);
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return switch (slot) {
            case 0 -> dir == Direction.UP;
            case 1 -> dir != Direction.DOWN && dir != Direction.UP;
            default -> false;
        };
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot == 2;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    private static boolean canAcceptRecipeOutput(ExplosiveBlastingRecipe recipe, DefaultedList<ItemStack> slots) {
        if (slots.get(0).isEmpty() || slots.get(1).isEmpty() || recipe == null || slots.get(0).getCount() < recipe.getInputCount()) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack1 = slots.get(2);
        if(itemStack1.isEmpty()){
            return true;
        }
        if (!itemStack1.isItemEqualIgnoreDamage(itemStack)) {
            return false;
        }

        return itemStack1.getCount() + recipe.getOutputCount() < itemStack1.getMaxCount();
    }

    private static void craftRecipe(Random random, ExplosiveBlastingRecipe recipe, DefaultedList<ItemStack> slots) {
        ItemStack itemStack1 = slots.get(1);
        if (recipe == null || !BlastChamberBlockEntity.canAcceptRecipeOutput(recipe, slots)) {
            itemStack1.decrement(1);
            return;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack2 = recipe.getOutput();
        int count = BlastChamberBlockEntity.getMaxBatch(itemStack1.getItem());
        while(count > 0 && slots.get(2).getCount() < recipe.getOutput().getMaxCount()){
            for(int i = 0; i < recipe.getOutputCount(); i++){
                ItemStack itemStack3 = slots.get(2);
                if(random.nextFloat() < recipe.getChance()){
                    if(itemStack3.isEmpty()){
                        itemStack2.setCount(1);
                        slots.set(2, itemStack2.copy());
                    }else if(itemStack3.isOf(itemStack2.getItem())){
                        itemStack3.increment(1);
                    }
                }
            }
            itemStack.decrement(recipe.getInputCount());
            count--;
        }
        itemStack1.decrement(1);
    }

    private static int getMaxBatch(Item item) {
        if(item == Items.TNT){
            return 8;
        }
        if(item == Items.END_CRYSTAL){
            return 32;
        }
        return 0;
    }

    public static void clientTick(World world, BlockPos blockPos, BlockState blockState, BlastChamberBlockEntity blockEntity) {
        if(world.isClient){
            Random random = world.getRandom();
            if(blockEntity.coolDown <= 0){
                double x = blockPos.getX()+0.5;
                double y = blockPos.getY()+0.5;
                double z = blockPos.getZ()+0.5;
                for(int i = 0; i < 2; i++){
                    double dx = random.nextDouble()*0.12-0.06;
                    double dz = random.nextDouble()*0.12-0.06;
                    world.addParticle(ParticleTypes.EXPLOSION, x+dx, y, z+dz, 0.1, 0.1, 0.1);
                }
            }
        }
    }

    public static void serverTick(World world, BlockPos blockPos, BlockState blockState, BlastChamberBlockEntity blockEntity) {
        if(!world.isClient){
            boolean bl2 = false;
            if (!blockEntity.inventory.get(0).isEmpty() && !blockEntity.inventory.get(1).isEmpty()) {
                ExplosiveBlastingRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.EXPLOSIVE_BLASTING, blockEntity, world).orElse(null);
                if (BlastChamberBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory)) {
                    bl2 = true;
                    if (blockEntity.coolDown == 0) {
                        blockEntity.coolDown = BlastChamberBlockEntity.getCooldown(blockEntity.inventory.get(1));
                        BlastChamberBlockEntity.craftRecipe(world.getRandom(), recipe, blockEntity.inventory);
                        world.playSound(null, blockPos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 0.5f, 1f);
                        blockEntity.addVux(1000);
                    }
                    if (blockEntity.coolDown > 0 && blockEntity.getStack(2).getCount() < blockEntity.getStack(2).getMaxCount()) {
                        blockEntity.coolDown = MathHelper.clamp(blockEntity.coolDown - 2, 0, BlastChamberBlockEntity.MAX_COOLDOWN);
                    }
                }
            }
            if (bl2) {
                world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL);
                BlastChamberBlockEntity.markDirty(world, blockPos, blockState);
            }
            ((ServerWorld) world).getChunkManager().markForUpdate(blockEntity.getPos());
        }
    }

    private static int getCooldown(ItemStack itemStack) {
        if(itemStack.isOf(Items.TNT)){
            return 200;
        }
        if(itemStack.isOf(Items.END_CRYSTAL)){
            return 80;
        }
        return MAX_COOLDOWN;
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

    public PropertyDelegate getPropertyDelegate() {
        return this.propertyDelegate;
    }


}
