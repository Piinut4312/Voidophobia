package net.piinut.voidophobia.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.piinut.voidophobia.item.ModItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerInvulnerableToMixin {

    @Shadow public abstract boolean damage(DamageSource source, float amount);

    @Inject(method = "isInvulnerableTo(Lnet/minecraft/entity/damage/DamageSource;)Z", at = @At(value = "HEAD"), cancellable = true)

    private void isInvulnerableTo(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir){
        if(damageSource == DamageSource.OUT_OF_WORLD){
            ItemStack helmet = ((PlayerEntity)(Object)this).getEquippedStack(EquipmentSlot.HEAD);
            ItemStack chestplate = ((PlayerEntity)(Object)this).getEquippedStack(EquipmentSlot.CHEST);
            ItemStack leggings = ((PlayerEntity)(Object)this).getEquippedStack(EquipmentSlot.LEGS);
            ItemStack boots = ((PlayerEntity)(Object)this).getEquippedStack(EquipmentSlot.FEET);
            boolean matchHelmet = helmet.isOf(ModItems.ETHER_ALLOY_HELMET);
            boolean matchChestplate = chestplate.isOf(ModItems.ETHER_ALLOY_CHESTPLATE);
            boolean matchLeggings = leggings.isOf(ModItems.ETHER_ALLOY_LEGGINGS);
            boolean matchBoots = boots.isOf(ModItems.ETHER_ALLOY_BOOTS);
            if(matchHelmet && matchChestplate && matchLeggings && matchBoots){
                cir.setReturnValue(true);
            }
        }

    }

}
