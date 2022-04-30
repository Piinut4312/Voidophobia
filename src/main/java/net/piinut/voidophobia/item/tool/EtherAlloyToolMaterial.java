package net.piinut.voidophobia.item.tool;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.piinut.voidophobia.item.ModItems;

public class EtherAlloyToolMaterial implements ToolMaterial {

    public static final EtherAlloyToolMaterial INSTANCE = new EtherAlloyToolMaterial();

    @Override
    public int getDurability() {
        return 3561;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 12.0f;
    }

    @Override
    public float getAttackDamage() {
        return 6.5f;
    }

    @Override
    public int getMiningLevel() {
        return MiningLevels.NETHERITE;
    }

    @Override
    public int getEnchantability() {
        return 26;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.ETHER_ALLOY_INGOT);
    }
}
