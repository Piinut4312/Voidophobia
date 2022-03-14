package net.piinut.voidophobia.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VuxFilterItem extends Item {

    public static final String TYPE_KEY = "Type";
    public static final String BLANK = "blank";

    public VuxFilterItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack itemStack = new ItemStack(this);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString(TYPE_KEY, BLANK);
        return itemStack;
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        super.onCraft(stack, world, player);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putString(TYPE_KEY, BLANK);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        super.appendStacks(group, stacks);
        if(group == ItemGroup.SEARCH || group == ModItems.VOIDOPHOBIA_DEFAULT_GROUP){
            ItemStack itemStack = new ItemStack(this);
            NbtCompound nbt = itemStack.getOrCreateNbt();
            nbt.putString(TYPE_KEY, BLANK);
            stacks.add(itemStack);
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        if(!stack.hasNbt()){
            return false;
        }
        return stack.getNbt().getString(TYPE_KEY) != BLANK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        NbtCompound nbt = stack.getOrCreateNbt();
        String type = nbt.getString(TYPE_KEY);
        if(type == ""){
            nbt.putString(TYPE_KEY, BLANK);
        }
        tooltip.add(new TranslatableText("vux_filter.type."+type));
    }
}
