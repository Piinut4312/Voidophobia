package net.piinut.voidophobia.block.blockEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.piinut.voidophobia.gui.handler.LaserEngravingMachineScreenHandler;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.VuxFilterItem;
import net.piinut.voidophobia.item.recipe.LaserEngravingRecipe;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import org.jetbrains.annotations.Nullable;

public class LaserEngravingMachineBlockEntity extends BlockEntity implements BasicInventory, NamedScreenHandlerFactory {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    int processTime;
    public static final int TOTAL_PROCESS_TIME = 100;
    float vuxStored;
    public static final float MAX_VUX_CAPACITY = 16000;
    private static final float VUX_CONSUME_PER_TICK = 40;
    private static final int TOTAL_VUX_CONSUME_PER_ITEM = (int) (TOTAL_PROCESS_TIME * VUX_CONSUME_PER_TICK);
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
        return inventory;
    }

    public double requestVuxConsume() {
        if(this.vuxStored >= LaserEngravingMachineBlockEntity.MAX_VUX_CAPACITY){
            return 0;
        }
        return Math.min(LaserEngravingMachineBlockEntity.MAX_VUX_CAPACITY - this.vuxStored, 200);
    }

    public void addVux(double vuxIn) {
        this.vuxStored += Math.min(vuxIn, 200);
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

    public static void tick(World world, BlockPos pos, BlockState state, LaserEngravingMachineBlockEntity blockEntity){
        boolean bl = blockEntity.isProcessing();
        boolean bl2 = false;
        if (blockEntity.isProcessing() && !blockEntity.inventory.get(0).isEmpty() && !blockEntity.inventory.get(1).isEmpty()) {
            LaserEngravingRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.LASER_ENGRAVING, blockEntity, world).orElse(null);
            if (!blockEntity.isProcessing() && LaserEngravingMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory)) {
                if (blockEntity.isProcessing()) {
                    bl2 = true;
                }
            }
            if (blockEntity.isProcessing() && LaserEngravingMachineBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory) && blockEntity.vuxStored >= LaserEngravingMachineBlockEntity.VUX_CONSUME_PER_TICK) {
                ++blockEntity.processTime;
                blockEntity.vuxStored -= LaserEngravingMachineBlockEntity.VUX_CONSUME_PER_TICK;
                if (blockEntity.processTime == LaserEngravingMachineBlockEntity.TOTAL_PROCESS_TIME) {
                    blockEntity.processTime = 0;
                    LaserEngravingMachineBlockEntity.craftRecipe(recipe, blockEntity.inventory);
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
            LaserEngravingMachineBlockEntity.markDirty(world, pos, state);
        }
    }

    private static boolean canAcceptRecipeOutput(LaserEngravingRecipe recipe, DefaultedList<ItemStack> slots) {
        if (slots.get(0).isEmpty() || slots.get(1).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.get(1);

        if(!itemStack2.isOf(ModItems.VUX_FILTER)){
            return false;
        }

        return itemStack2.getNbt().getString(VuxFilterItem.TYPE_KEY) == VuxFilterItem.BLANK;
    }

    private static void craftRecipe(LaserEngravingRecipe recipe, DefaultedList<ItemStack> slots) {
        if (recipe == null || !LaserEngravingMachineBlockEntity.canAcceptRecipeOutput(recipe, slots)) {
            return;
        }
        ItemStack itemStack = slots.get(1);
        String type = recipe.getOutputType();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString(VuxFilterItem.TYPE_KEY, type);
        slots.set(1, itemStack.copy());
    }

    private boolean isProcessing() {
        return this.processTime < TOTAL_PROCESS_TIME;
    }

}
