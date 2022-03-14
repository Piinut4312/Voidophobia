package net.piinut.voidophobia.world.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record GodelCrystalPileFeatureConfig(IntProvider height, BlockStateProvider block) implements FeatureConfig {
    public static final Codec<GodelCrystalPileFeatureConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            IntProvider.VALUE_CODEC.fieldOf("height").forGetter(GodelCrystalPileFeatureConfig::height),
            BlockStateProvider.TYPE_CODEC.fieldOf("block").forGetter(GodelCrystalPileFeatureConfig::block)
    ).apply(instance, instance.stable(GodelCrystalPileFeatureConfig::new)));
}