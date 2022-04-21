package net.piinut.voidophobia.item.tool;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.piinut.voidophobia.item.ModItems;

public class SilverToolMaterial implements ToolMaterial {

    public static final SilverToolMaterial INSTANCE = new SilverToolMaterial();

    @Override
    public int getDurability() {
        return 48;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 10.0f;
    }

    @Override
    public float getAttackDamage() {
        return 1.0f;
    }

    @Override
    public int getMiningLevel() {
        return MiningLevels.WOOD;
    }

    @Override
    public int getEnchantability() {
        return 28;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.ofItems(ModItems.SILVER_INGOT);
    }
}
