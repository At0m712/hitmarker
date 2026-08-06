    package atom.hitmarker.sounds;

    import atom.hitmarker.HitMarker;
    import net.minecraft.core.Registry;
    import net.minecraft.core.registries.BuiltInRegistries;
    import net.minecraft.resources.Identifier;
    import net.minecraft.sounds.SoundEvent;

    public class ModSounds {

        public static final SoundEvent HIT_SOUND_1 = registerSoundEvent("hit_sound_1");
        public static final SoundEvent HIT_SOUND_2 = registerSoundEvent("hit_sound_2");

        private static SoundEvent registerSoundEvent(String name) {
            Identifier id = HitMarker.id(name);
            return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
        }

        public static void registerSounds(){
            HitMarker.LOGGER.info("Registering Mod Sounds for " + HitMarker.MOD_ID);
        }
    }