package net.piinut.voidophobia.block;

import net.minecraft.util.StringIdentifiable;

public enum ItemPipeNodeType implements StringIdentifiable {
    NONE("none"),
    TRANSFER("transfer"),
    INSERT("insert"),
    EXTRACT("extract");

    private final String name;

    ItemPipeNodeType(String name){
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}
