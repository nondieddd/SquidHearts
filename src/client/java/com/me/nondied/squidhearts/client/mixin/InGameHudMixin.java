package com.me.nondied.squidhearts.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    public abstract TextRenderer getTextRenderer();

    @ModifyArgs(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHealthBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/entity/player/PlayerEntity;IIIIFIIIZ)V"
            )
    )
    private void centerHealthBar(Args args) {
        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();
        args.set(2, width / 2 - 40);
        args.set(3, height - 33);
    }

    @Inject(
            method = "renderHealthBar",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideAbsorptionBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean renderingAbsorption, CallbackInfo ci) {
        if (renderingAbsorption) {
            ci.cancel();
        }
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true, require = 0)
    private void hideFoodBar(DrawContext context, PlayerEntity player, int top, int right, CallbackInfo ci) {
        ci.cancel();
    }

    @Redirect(
            method = "renderStatusBars",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void modifyStatusSprites(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        String path = texture.getPath();

        if (path.contains("food") || path.contains("hunger") || path.contains("beef") || path.contains("meat")) {
            return;
        }

        if (path.contains("absorption")) {
            return;
        }

        if (path.contains("heart/container")) {
            instance.drawGuiTexture(Identifier.of("minecraft", "hud/heart/0"), x, y, width, height);
            instance.drawGuiTexture(Identifier.of("minecraft", "hud/heart/container"), x, y, width, height);
            return;
        }

        if (path.contains("bubble")) {
            instance.drawGuiTexture(texture, x, y - 10, width, height);
            return;
        }

        instance.drawGuiTexture(texture, x, y, width, height);
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"), cancellable = true)
    private void hideExpBar(DrawContext context, int x, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "renderExperienceLevel", at = @At("HEAD"), cancellable = true)
    private void centerExpLevel(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        ci.cancel();

        MinecraftClient client = MinecraftClient.getInstance();
        int level = client.player != null ? client.player.experienceLevel : 0;

        if (level > 0) {
            client.getProfiler().push("expLevel");

            String levelString = String.valueOf(level);
            int screenWidth = context.getScaledWindowWidth();
            int screenHeight = context.getScaledWindowHeight();

            int textX = (screenWidth - this.getTextRenderer().getWidth(levelString)) / 2;
            int textY = screenHeight - 43;

            context.drawText(this.getTextRenderer(), levelString, textX + 1, textY, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX - 1, textY, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX, textY + 1, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX, textY - 1, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX, textY, 0xFFFFFF, false);

            client.getProfiler().pop();
        }
    }

    @Redirect(
            method = "renderHotbar",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"
            )
    )
    private void fixHotbarSelectionOffset(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
        if (texture.getPath().contains("hotbar_selection")) {
            instance.drawGuiTexture(texture, x + -1, y, width, height);
        } else {
            instance.drawGuiTexture(texture, x, y, width, height);
        }
    }
}