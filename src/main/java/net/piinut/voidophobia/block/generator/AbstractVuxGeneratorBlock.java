package net.piinut.voidophobia.block.generator;

import net.minecraft.block.BlockWithEntity;
import net.piinut.voidophobia.block.VuxProvider;

public abstract class AbstractVuxGeneratorBlock extends BlockWithEntity implements VuxProvider {

    public AbstractVuxGeneratorBlock(Settings settings) {
        super(settings);
    }
}
