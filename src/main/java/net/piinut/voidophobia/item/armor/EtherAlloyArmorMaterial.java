package net.piinut.voidophobia.item.armor;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.piinut.voidophobia.item.ModItems;

public class EtherAlloyArmorMaterial implements ArmorMaterial {

    public static final EtherAlloyArmorMaterial INSTANCE = new EtherAlloyArmorMaterial();

    private static final int[] BASE_DURABILITY = new int[] {13, 15, 16, 11};
    private static final int[] PROTECTION_VALUES = new int[] {5, 8, 10, 5};

    @Override
    public int getDurability(EquipmentSlot slot) {
        return BASE_DURABILITY[slot.getEntitySlotId()] * 81;
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return PROTECTION_VALUES[slot.getEntitySlotId()];
    }

    @Override
    public int getEnchantability() {
        return 24;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_NETHERITE;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.ETHER_ALLOY_INGOT);
    }

    @Override
    public String getName() {
        return "ether_alloy";
    }

    @Override
    public float getToughness() {
        return 5.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.5f;
    }
}
