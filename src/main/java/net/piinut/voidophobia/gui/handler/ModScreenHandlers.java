package net.piinut.voidophobia.gui.handler;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.piinut.voidophobia.Voidophobia;

public class ModScreenHandlers {

    public static ScreenHandlerType<AlloyFurnaceScreenHandler> ALLOY_FURNACE;
    public static ScreenHandlerType<VuxFormingMachineScreenHandler> VUX_FORMING_MACHINE;
    public static ScreenHandlerType<LaserEngravingMachineScreenHandler> LASER_ENGRAVING_MACHINE;
    public static ScreenHandlerType<VuxFilterMachineScreenHandler> VUX_FILTER_MACHINE;
    public static ScreenHandlerType<VacuumCoaterScreenHandler> VACUUM_COATER;
    public static ScreenHandlerType<BasicItemPipeScreenHandler> BASIC_ITEM_PIPE;
    public static ScreenHandlerType<AnomalyCaptorScreenHandler> ANOMALY_CAPTOR;
    public static ScreenHandlerType<VuxFurnaceScreenHandler> VUX_FURNACE;

    private static Identifier getId(String id){
        return new Identifier(Voidophobia.MODID, id);
    }

    public static void registerAll(){
        ALLOY_FURNACE = ScreenHandlerRegistry.registerSimple(getId("alloy_furnace"), AlloyFurnaceScreenHandler::new);
        VUX_FORMING_MACHINE = ScreenHandlerRegistry.registerSimple(getId("vux_forming_machine"), VuxFormingMachineScreenHandler::new);
        LASER_ENGRAVING_MACHINE = ScreenHandlerRegistry.registerSimple(getId("laser_engraving_machine"), LaserEngravingMachineScreenHandler::new);
        VUX_FILTER_MACHINE = ScreenHandlerRegistry.registerSimple(getId("vux_filter_machine"), VuxFilterMachineScreenHandler::new);
        VACUUM_COATER = ScreenHandlerRegistry.registerSimple(getId("vacuum_coater"), VacuumCoaterScreenHandler::new);
        BASIC_ITEM_PIPE = ScreenHandlerRegistry.registerSimple(getId("basic_item_pipe"), BasicItemPipeScreenHandler::new);
        ANOMALY_CAPTOR = ScreenHandlerRegistry.registerSimple(getId("anomaly_captor"), AnomalyCaptorScreenHandler::new);
        VUX_FURNACE = ScreenHandlerRegistry.registerSimple(getId("vux_furnace"), VuxFurnaceScreenHandler::new);
    }

}
