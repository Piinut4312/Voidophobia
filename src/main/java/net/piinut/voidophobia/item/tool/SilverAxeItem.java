package net.piinut.voidophobia.item.tool;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.piinut.voidophobia.entity.AbyssSpiderEntity;
import net.piinut.voidophobia.entity.EnsorcelledEntity;

public class SilverAxeItem extends AxeItem {

    public SilverAxeItem(float attackDamage, float attackSpeed, Settings settings) {
        super(SilverToolMaterial.INSTANCE, attackDamage, attackSpeed, settings);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if(target instanceof AbyssSpiderEntity || target instanceof EnsorcelledEntity){
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100));
        }
        return super.postHit(stack, target, attacker);
    }
}
