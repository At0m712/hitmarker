package atom.hitmarker.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import atom.hitmarker.HitMarkerClient;
import atom.hitmarker.ModConfig;


@Mixin(Gui.class)
public abstract class GuiMixin {

    public final Minecraft minecraft = Minecraft.getInstance();

    private static final ResourceLocation HIT_SPRITE_1 = ResourceLocation.fromNamespaceAndPath("hitmarker", "crosshair/hit1");
    private static final ResourceLocation HIT_SPRITE_2 = ResourceLocation.fromNamespaceAndPath("hitmarker", "crosshair/hit2");
    private static final ResourceLocation HIT_SPRITE_3 = ResourceLocation.fromNamespaceAndPath("hitmarker", "crosshair/hit3");
    private static final ResourceLocation HIT_SPRITE_4 = ResourceLocation.fromNamespaceAndPath("hitmarker", "crosshair/hit4");
    private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");

    @Redirect(
            method = "renderCrosshair",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIII)V")
    )
    private void redirectCrosshairBlit(GuiGraphics instance, RenderPipeline renderPipeline, ResourceLocation sprite, int x, int y, int width, int height) {

        instance.blitSprite(renderPipeline, sprite, x, y, width, height);

        if (sprite.equals(CROSSHAIR_SPRITE)) {
            if (HitMarkerClient.projectileHitTimer > 0.0F) {

                ResourceLocation spriteToUse = HIT_SPRITE_1;
                if (ModConfig.crosshairStyle == 2) spriteToUse = HIT_SPRITE_2;
                if (ModConfig.crosshairStyle == 3) spriteToUse = HIT_SPRITE_3;
                if (ModConfig.crosshairStyle == 4) spriteToUse = HIT_SPRITE_4;

                int color = HitMarkerClient.isKillIndicator ? 0xFFFF0000 : 0xFFFFFFFF;

                instance.blitSprite(RenderPipelines.GUI_TEXTURED, spriteToUse, x, y, width, height, color);
            }
        }
    }
}