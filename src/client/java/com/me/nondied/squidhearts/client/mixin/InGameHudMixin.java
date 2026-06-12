package com.me.nondied.squidhearts.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.bar.Bar;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import net.minecraft.util.profiler.Profilers;

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
            method = "renderMainHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/bar/Bar;renderBar(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V"
            )
    )
    private void hideExpBar(Bar bar, DrawContext context, RenderTickCounter tickCounter) {
        System.out.println("[SQUIDHEARTS] hideExpBar called, bar class = " + bar.getClass().getSimpleName());
    }

    @Redirect(
            method = "renderMainHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/bar/Bar;drawExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;I)V"
            )
    )
    private void centerExpLevel(DrawContext context, TextRenderer textRenderer, int level) {
        System.out.println("[SQUIDHEARTS] centerExpLevel called, level = " + level);

        if (com.me.nondied.squidhearts.client.SquidHeartsClientState.visualExperience != -1) {
            level = com.me.nondied.squidhearts.client.SquidHeartsClientState.visualExperience;
        }

        if (level > 0) {
            Profilers.get().push("expLevel");

            String levelString = String.valueOf(level);
            int screenWidth = context.getScaledWindowWidth();
            int screenHeight = context.getScaledWindowHeight();

            int textX = (screenWidth - this.getTextRenderer().getWidth(levelString)) / 2;
            int textY = screenHeight - 43;
            int color = com.me.nondied.squidhearts.client.SquidHeartsClientState.expColor;
            context.drawText(this.getTextRenderer(), levelString, textX + 1, textY, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX - 1, textY, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX, textY + 1, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX, textY - 1, 0x000000, false);
            context.drawText(this.getTextRenderer(), levelString, textX, textY, color, false);

            Profilers.get().pop();
        }
    }
}