package net.piinut.voidophobia.util.tags;

import net.fabricmc.fabric.api.tag.TagFactory;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;

public class ModItemTags {

    public static final Tag<Item> VUX_FORMING_MACHINE_MODIFIERS = TagFactory.ITEM.create(new Identifier(Voidophobia.MODID, "vux_forming_machine_modifiers"));
    public static final Tag<Item> VUX_FILTER_MACHINE_MODIFIERS = TagFactory.ITEM.create(new Identifier(Voidophobia.MODID, "vux_filter_machine_modifiers"));
}
