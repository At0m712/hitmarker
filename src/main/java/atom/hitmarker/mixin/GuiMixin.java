package atom.hitmarker.mixin;

import atom.hitmarker.HitMarkerClient;
import atom.hitmarker.ModConfig;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

    import atom.hitmarker.HitMarkerClient;

@Mixin(Gui.class)
public abstract class GuiMixin {

    public final Minecraft minecraft = Minecraft.getInstance();

    private static final ResourceLocation HIT_SPRITE_1 = ResourceLocation.fromNamespaceAndPath("hitmarker", "crosshair/hit1");
    private static final ResourceLocation HIT_SPRITE_2 = ResourceLocation.fromNamespaceAndPath("hitmarker", "crosshair/hit2");
    private static final ResourceLocation HIT_SPRITE_3 = ResourceLocation.fromNamespaceAndPath("hitmarker", "crosshair/hit3");
    private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");

    @Redirect(
            method = "renderCrosshair",

            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V")
    )
    private void redirectCrosshairBlit(GuiGraphics instance, ResourceLocation sprite, int x, int y, int width, int height) {

        instance.blitSprite(sprite, x, y, width, height);

        if (sprite.equals(CROSSHAIR_SPRITE)) {
            if (HitMarkerClient.projectileHitTimer > 0.0F) {

                RenderSystem.defaultBlendFunc();

                if (HitMarkerClient.isKillIndicator) {
                    instance.setColor(1.0F, 0.0F, 0.0F, 1.0F); // Rouge
                }

                ResourceLocation spriteToUse = HIT_SPRITE_1;
                if (ModConfig.crosshairStyle == 2) spriteToUse = HIT_SPRITE_2;
                if (ModConfig.crosshairStyle == 3) spriteToUse = HIT_SPRITE_3;

                instance.blitSprite(spriteToUse, x, y, width, height);

                instance.setColor(1.0F, 1.0F, 1.0F, 1.0F);

                RenderSystem.blendFuncSeparate(
                        GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                        GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                        GlStateManager.SourceFactor.ONE,
                        GlStateManager.DestFactor.ZERO
                );
            }
        }
    }
}