package net.piinut.voidophobia.gui.screen;

import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.piinut.voidophobia.gui.handler.ModScreenHandlers;

public class ModScreens {

    public static void registerAll(){
        ScreenRegistry.register(ModScreenHandlers.ALLOY_FURNACE, AlloyFurnaceScreen::new);
        ScreenRegistry.register(ModScreenHandlers.VUX_FORMING_MACHINE, VuxFormingMachineScreen::new);
        ScreenRegistry.register(ModScreenHandlers.LASER_ENGRAVING_MACHINE, LaserEngravingMachineScreen::new);
        ScreenRegistry.register(ModScreenHandlers.VUX_FILTER_MACHINE, VuxFilterMachineScreen::new);
        ScreenRegistry.register(ModScreenHandlers.VACUUM_COATER, VacuumCoaterScreen::new);
        ScreenRegistry.register(ModScreenHandlers.BASIC_ITEM_PIPE, BasicItemPipeScreen::new);
        ScreenRegistry.register(ModScreenHandlers.ANOMALY_CAPTOR, AnomalyCaptorScreen::new);
    }

}
