package net.piinut.voidophobia.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModifierModuleItem extends Item {

    private String id;
    private boolean hasDebuff;

    public ModifierModuleItem(Settings settings, String id, boolean hasDebuff) {
        super(settings);
        this.id = id;
        this.hasDebuff = hasDebuff;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.usable_machines").formatted(Formatting.GOLD));
        tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.description").formatted(Formatting.BLUE));
        tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.buff").formatted(Formatting.GREEN));
        if(hasDebuff){
            tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.debuff").formatted(Formatting.RED));
        }
    }
}
