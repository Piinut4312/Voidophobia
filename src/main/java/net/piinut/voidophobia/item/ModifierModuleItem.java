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
    private int buffs;
    private int debuffs;

    public ModifierModuleItem(Settings settings, String id, int buffs, int debuffs) {
        super(settings);
        this.id = id;
        this.buffs = buffs;
        this.debuffs = debuffs;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.usable_machines").formatted(Formatting.GOLD));
        tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.description").formatted(Formatting.BLUE));
        for(int i = 0; i < this.buffs; i++){
            tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.buff"+i).formatted(Formatting.GREEN));
        }
        for(int i = 0; i < this.debuffs; i++){
            tooltip.add(new TranslatableText("item.voidophobia." + id + "_modifier_module.tooltip.debuff"+i).formatted(Formatting.RED));
        }
    }
}
