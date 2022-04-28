package net.piinut.voidophobia.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class LaserLensItem extends Item {

    private int affectDistance;
    private int vuxConsumption;

    public LaserLensItem(int affectDistance, int vuxConsumption) {
        super(new FabricItemSettings().group(ModItems.VOIDOPHOBIA_DEFAULT_GROUP).maxCount(1));
        this.affectDistance = affectDistance;
        this.vuxConsumption = vuxConsumption;
    }

    public int getAffectDistance(){
        return this.affectDistance;
    }

    public int getVuxConsumption() {
        return vuxConsumption;
    }
}
