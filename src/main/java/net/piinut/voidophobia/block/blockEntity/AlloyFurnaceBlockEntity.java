package net.piinut.voidophobia.block.blockEntity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.piinut.voidophobia.Voidophobia;
import net.piinut.voidophobia.block.AlloyFurnaceBlock;
import net.piinut.voidophobia.gui.handler.AlloyFurnaceScreenHandler;
import net.piinut.voidophobia.item.ModItems;
import net.piinut.voidophobia.item.recipe.AlloySmeltingRecipe;
import net.piinut.voidophobia.item.recipe.ModRecipeTypes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AlloyFurnaceBlockEntity extends BlockEntity implements SidedInventory, BasicInventory, NamedScreenHandlerFactory, RecipeInputProvider {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private static final int[] TOP_SLOTS = new int[]{0, 1};
    private static final int[] BOTTOM_SLOTS = new int[]{3, 2};
    private static final int[] SIDE_SLOTS = new int[]{2};
    int burnTime;
    int fuelTime;
    int cookTime;
    int cookTimeTotal;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0 -> {
                    return AlloyFurnaceBlockEntity.this.burnTime;
                }
                case 1 -> {
                    return AlloyFurnaceBlockEntity.this.fuelTime;
                }
                case 2 -> {
                    return AlloyFurnaceBlockEntity.this.cookTime;
                }
                case 3 -> {
                    return AlloyFurnaceBlockEntity.this.cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> AlloyFurnaceBlockEntity.this.burnTime = value;
                case 1 -> AlloyFurnaceBlockEntity.this.fuelTime = value;
                case 2 -> AlloyFurnaceBlockEntity.this.cookTime = value;
                case 3 -> AlloyFurnaceBlockEntity.this.cookTimeTotal = value;
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };

    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();

    public AlloyFurnaceBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.ALLOY_FURNACE ,blockPos, blockState);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.burnTime = nbt.getShort("BurnTime");
        this.cookTime = nbt.getShort("CookTime");
        this.cookTimeTotal = nbt.getShort("CookTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(2));
        this.recipesUsed.forEach((identifier, count) -> nbt.putInt(identifier.toString(), count));
        nbt.put("RecipesUsed", nbt);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putShort("BurnTime", (short)this.burnTime);
        nbt.putShort("CookTime", (short)this.cookTime);
        nbt.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        NbtCompound nbtCompound = nbt.getCompound("RecipesUsed");
        for (String string : nbtCompound.getKeys()) {
            this.recipesUsed.put(new Identifier(Voidophobia.MODID, string), nbtCompound.getInt(string));
        }
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        LinkedHashMap<Item, Integer> map = Maps.newLinkedHashMap();
        AlloyFurnaceBlockEntity.addFuel(map, Items.LAVA_BUCKET, 10000);
        AlloyFurnaceBlockEntity.addFuel(map, Blocks.COAL_BLOCK, 8000);
        AlloyFurnaceBlockEntity.addFuel(map, Items.BLAZE_ROD, 1200);
        AlloyFurnaceBlockEntity.addFuel(map, ModItems.INFERNIUM, 3600);
        return map;
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, ItemConvertible item, int fuelTime) {
        Item item2 = item.asItem();
        fuelTimes.put(item2, fuelTime);
    }

    private int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item item = fuel.getItem();
        return AlloyFurnaceBlockEntity.createFuelTimeMap().getOrDefault(item, 0);
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
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        if (dir == Direction.DOWN && slot == 2) {
            return stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.BUCKET);
        }
        return false;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    public void dropExperienceForRecipesUsed(ServerPlayerEntity player) {
        this.getRecipesUsedAndDropExperience(player.getWorld(), player.getPos());
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> getRecipesUsedAndDropExperience(ServerWorld world, Vec3d pos) {
        ArrayList<Recipe<?>> list = Lists.newArrayList();
        for (Object2IntMap.Entry entry : this.recipesUsed.object2IntEntrySet()) {
            world.getRecipeManager().get((Identifier)entry.getKey()).ifPresent(recipe -> {
                list.add(recipe);
                AlloyFurnaceBlockEntity.dropExperience(world, pos, entry.getIntValue(), ((AlloySmeltingRecipe)recipe).getExperience());
            });
        }
        return list;
    }
    
    private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience) {
        int i = MathHelper.floor((float)multiplier * experience);
        float f = MathHelper.fractionalPart((float)multiplier * experience);
        if (f != 0.0f && Math.random() < (double)f) {
            ++i;
        }
        ExperienceOrbEntity.spawn(world, pos, i);
    }

    private static boolean canAcceptRecipeOutput(@Nullable AlloySmeltingRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (recipe == null || !recipe.canCraft(slots.get(0), slots.get(1))) {
            return false;
        }
        ItemStack itemStack = recipe.getOutput();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack itemStack2 = slots.get(3);
        if (itemStack2.isEmpty()) {
            return true;
        }
        if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) {
            return false;
        }
        return itemStack2.getCount() + itemStack.getCount() <= count;
    }

    private static void craftRecipe(@Nullable AlloySmeltingRecipe recipe, DefaultedList<ItemStack> slots, int count) {
        if (!AlloyFurnaceBlockEntity.canAcceptRecipeOutput(recipe, slots, count)) {
            return;
        }
        ItemStack itemStack = slots.get(0);
        ItemStack itemStack1 = slots.get(1);
        ItemStack itemStack2 = recipe.getOutput();
        ItemStack itemStack3 = slots.get(3);
        if (itemStack3.isEmpty()) {
            slots.set(3, itemStack2.copy());
        } else if (itemStack3.isOf(itemStack2.getItem())) {
            itemStack3.increment(itemStack2.getCount());
        }
        itemStack.decrement(recipe.getCountForInput(itemStack));
        itemStack1.decrement(recipe.getCountForInput(itemStack1));
    }

    private static int getCookTime(World world, BasicInventory inventory) {
        return world.getRecipeManager().getFirstMatch(ModRecipeTypes.ALLOY_SMELTING, inventory, world).map(AlloySmeltingRecipe::getCookTime).orElse(200);
    }

    public static void tick(World world, BlockPos pos, BlockState state, AlloyFurnaceBlockEntity blockEntity) {

        boolean bl = blockEntity.isBurning();
        boolean bl2 = false;
        if (blockEntity.isBurning()) {
            --blockEntity.burnTime;
        }
        ItemStack itemStack = blockEntity.inventory.get(2);
        if (blockEntity.isBurning() || !itemStack.isEmpty() && !blockEntity.inventory.get(0).isEmpty()) {
            AlloySmeltingRecipe recipe = world.getRecipeManager().getFirstMatch(ModRecipeTypes.ALLOY_SMELTING, blockEntity, world).orElse(null);
            int i = blockEntity.getMaxCountPerStack();
            if (!blockEntity.isBurning() && AlloyFurnaceBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                blockEntity.fuelTime = blockEntity.burnTime = blockEntity.getFuelTime(itemStack);
                if (blockEntity.isBurning()) {
                    bl2 = true;
                    if (!itemStack.isEmpty()) {
                        Item item = itemStack.getItem();
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            Item item2 = item.getRecipeRemainder();
                            blockEntity.inventory.set(2, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                        }
                    }
                }
            }
            if (blockEntity.isBurning() && AlloyFurnaceBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, i)) {
                ++blockEntity.cookTime;
                if (blockEntity.cookTime == blockEntity.cookTimeTotal) {
                    blockEntity.cookTime = 0;
                    blockEntity.cookTimeTotal = AlloyFurnaceBlockEntity.getCookTime(world, blockEntity);
                    AlloyFurnaceBlockEntity.craftRecipe(recipe, blockEntity.inventory, i);
                    bl2 = true;
                }
            } else {
                blockEntity.cookTime = 0;
            }
        } else if (blockEntity.cookTime > 0) {
            blockEntity.cookTime = MathHelper.clamp(blockEntity.cookTime - 2, 0, blockEntity.cookTimeTotal);
        }
        if (bl != blockEntity.isBurning()) {
            bl2 = true;
            state = state.with(AlloyFurnaceBlock.LIT, blockEntity.isBurning());
            world.setBlockState(pos, state, Block.NOTIFY_ALL);
        }
        if (bl2) {
            AlloyFurnaceBlockEntity.markDirty(world, pos, state);
        }
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack itemStack = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack, itemStack);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if ((slot == 0 || slot == 1) && !bl) {
            this.cookTimeTotal = AlloyFurnaceBlockEntity.getCookTime(this.world, this);
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Alloy Furnace");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlloyFurnaceScreenHandler(syncId, inv, this, this.propertyDelegate);
    }

    @Override
    public void provideRecipeInputs(RecipeMatcher finder) {
        for (ItemStack itemStack : this.inventory) {
            finder.addInput(itemStack);
        }
    }
}
