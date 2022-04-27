package net.piinut.voidophobia.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class LaserLensItem extends Item {

    private int affectDistance;

    public LaserLensItem(int affectDistance) {
        super(new FabricItemSettings().group(ModItems.VOIDOPHOBIA_DEFAULT_GROUP).maxCount(1));
        this.affectDistance = affectDistance;
    }

    public int getAffectDistance(){
        return this.affectDistance;
    }
}
