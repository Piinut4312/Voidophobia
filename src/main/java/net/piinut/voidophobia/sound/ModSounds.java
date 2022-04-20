package net.piinut.voidophobia.sound;


import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.piinut.voidophobia.Voidophobia;


public class ModSounds {

    public static Identifier BLOCK_LASER_ENGRAVING_MACHINE_PROCESSING_ID = getId("block.laser_engraving_machine.processing");
    public static Identifier BLOCK_LASER_ENGRAVING_MACHINE_FINISH_ID = getId("block.laser_engraving_machine.finish");
    public static SoundEvent BLOCK_LASER_ENGRAVING_MACHINE_PROCESSING = new SoundEvent(BLOCK_LASER_ENGRAVING_MACHINE_PROCESSING_ID);
    public static SoundEvent BLOCK_LASER_ENGRAVING_MACHINE_FINISH = new SoundEvent(BLOCK_LASER_ENGRAVING_MACHINE_FINISH_ID);

    private static Identifier getId(String id){
        return new Identifier(Voidophobia.MODID, id);
    }

    public static void registerAll(){
        register(BLOCK_LASER_ENGRAVING_MACHINE_PROCESSING_ID, BLOCK_LASER_ENGRAVING_MACHINE_PROCESSING);
        register(BLOCK_LASER_ENGRAVING_MACHINE_FINISH_ID, BLOCK_LASER_ENGRAVING_MACHINE_FINISH);
    }

    private static void register(Identifier id, SoundEvent soundEvent){
        Registry.register(Registry.SOUND_EVENT, id, soundEvent);
    }

}
