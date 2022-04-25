package net.piinut.voidophobia.item.potion;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;

public class ModPotions {

    public static final Potion POISON_OF_ABYSS = new Potion("poison_of_abyss", new StatusEffectInstance(StatusEffects.BLINDNESS, 400, 1), new StatusEffectInstance(StatusEffects.POISON, 900));
    public static final Potion BLINDNESS = new Potion("blindness", new StatusEffectInstance(StatusEffects.BLINDNESS, 600, 1));
    public static void registerAll(){
        Registry.register(Registry.POTION, new Identifier(Voidophobia.MODID, "poison_of_abyss"), POISON_OF_ABYSS);
        Registry.register(Registry.POTION, new Identifier(Voidophobia.MODID, "blindness"), BLINDNESS);
    }


}
