package net.piinut.voidophobia.gui.handler;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;

public class ModScreenHandlers {

    public static ScreenHandlerType<AlloyFurnaceScreenHandler> ALLOY_FURNACE;
    public static ScreenHandlerType<VuxFormingMachineScreenHandler> VUX_FORMING_MACHINE;
    public static ScreenHandlerType<LaserEngravingMachineScreenHandler> LASER_ENGRAVING_MACHINE;

    private static Identifier getId(String id){
        return new Identifier(Voidophobia.MODID, id);
    }

    public static void registerAll(){
        ALLOY_FURNACE = ScreenHandlerRegistry.registerSimple(getId("alloy_furnace"), AlloyFurnaceScreenHandler::new);
        VUX_FORMING_MACHINE = ScreenHandlerRegistry.registerSimple(getId("vux_forming_machine"), VuxFormingMachineScreenHandler::new);
        LASER_ENGRAVING_MACHINE = ScreenHandlerRegistry.registerSimple(getId("laser_engraving_machine"), LaserEngravingMachineScreenHandler::new);
    }

}
