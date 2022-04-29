package net.piinut.voidophobia.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.piinut.voidophobia.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerArmorEffectMixin {

    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract PlayerAbilities getAbilities();

    @Inject(method = "tick", at = @At(value = "RETURN"))
    private void tick(CallbackInfo ci){
        ItemStack helmet = this.getEquippedStack(EquipmentSlot.HEAD);
        ItemStack chestplate = this.getEquippedStack(EquipmentSlot.CHEST);
        ItemStack leggings = this.getEquippedStack(EquipmentSlot.LEGS);
        ItemStack boots = this.getEquippedStack(EquipmentSlot.FEET);
        boolean matchHelmet = helmet.isOf(ModItems.ETHER_ALLOY_HELMET);
        boolean matchChestplate = chestplate.isOf(ModItems.ETHER_ALLOY_CHESTPLATE);
        boolean matchLeggings = leggings.isOf(ModItems.ETHER_ALLOY_LEGGINGS);
        boolean matchBoots = boots.isOf(ModItems.ETHER_ALLOY_BOOTS);
        if(matchHelmet){
            ((PlayerEntity)(Object)this).addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, true));
        }
        if(matchChestplate){
            ((PlayerEntity)(Object)this).addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 0, false, false, true));
        }
        if(matchLeggings){
            ((PlayerEntity)(Object)this).addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 200, 1, false, false, true));
        }
        if(matchBoots){
            ((PlayerEntity)(Object)this).addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 200, 0, false, false, true));
        }
        if(matchHelmet && matchChestplate && matchLeggings && matchBoots){
            this.getAbilities().allowFlying = true;
        }
    }

}
