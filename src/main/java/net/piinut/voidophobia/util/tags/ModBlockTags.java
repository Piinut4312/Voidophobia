package net.piinut.voidophobia.util.tags;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;

public class ModBlockTags {
    public static final Tag<Block> VUXDUCTS = TagFactory.BLOCK.create(new Identifier(Voidophobia.MODID, "vuxducts"));
}
