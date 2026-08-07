package atom.hitmarker;

import atom.hitmarker.sounds.ModSounds;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

public class HitMarkerClient implements ClientModInitializer {

    private final Minecraft minecraft = Minecraft.getInstance();
    public static float projectileHitTimer = 0;
    public static boolean isKillIndicator = false;
    public static LivingEntity lastHitEntity = null;

    public static boolean killSoundPlayed = false;
    public static boolean hitSoundPlayed = false;
    public static int ticksSinceHit = 0;

    @Override
    public void onInitializeClient() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("HitMarker")


                    .then(ClientCommands.literal("Sound")
                            .then(ClientCommands.argument("enabled", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean isEnabled = BoolArgumentType.getBool(context, "enabled");
                                        ModConfig.playSound = isEnabled;

                                        context.getSource().sendFeedback(Component.literal("§aHitMarker Sound : " + (isEnabled ? "Enable" : "Disable")));
                                        return 1;
                                    })
                            )
                    )


                    .then(ClientCommands.literal("Style")
                            .then(ClientCommands.argument("type", IntegerArgumentType.integer(1, 3))
                                    .executes(context -> {
                                        int style = IntegerArgumentType.getInteger(context, "type");
                                        ModConfig.crosshairStyle = style;

                                        context.getSource().sendFeedback(Component.literal("§aHitMarker Style on : " + style));
                                        return 1;
                                    })
                            )
                    )
            );
        });

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("votre_mod_id", "projectile_hit_timer"),
                (graphics, tickCounter) -> {
                    // tickCounter est désormais une instance de DeltaTracker
                    float deltaTime = tickCounter.getGameTimeDeltaPartialTick(false) * 0.05f;

                    if (projectileHitTimer > 0) {
                        projectileHitTimer -= deltaTime;
                    } else {
                        lastHitEntity = null;
                    }
                }
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (projectileHitTimer > 0 && lastHitEntity != null) {
                ticksSinceHit++;

                boolean isDead = lastHitEntity.isDeadOrDying() || lastHitEntity.getHealth() <= 0.0F;

                if (isDead) {
                    isKillIndicator = true;

                    if (!killSoundPlayed && client.player != null) {
                        if (ModConfig.playSound){
                            client.player.playSound(ModSounds.HIT_SOUND_2, 1.0F, 1.0F);
                        }
                        killSoundPlayed = true;
                        hitSoundPlayed = true;
                    }
                } else {

                    if (!hitSoundPlayed && ticksSinceHit > 1) {
                        if (client.player != null) {
                            if (ModConfig.playSound){
                            client.player.playSound(ModSounds.HIT_SOUND_1, 1.0F, 1.0F);
                            }
                        }
                        hitSoundPlayed = true;
                    }
                }
            }
        });
    }

    public static void projectileHit(LivingEntity entity) {
        boolean isNewHit = (projectileHitTimer <= 0.1f || lastHitEntity != entity);

        if (isNewHit) {
            projectileHitTimer = 0.5f;
            isKillIndicator = false;
            lastHitEntity = entity;

            killSoundPlayed = false;
            hitSoundPlayed = false;
            ticksSinceHit = 0;
        }
    }
}