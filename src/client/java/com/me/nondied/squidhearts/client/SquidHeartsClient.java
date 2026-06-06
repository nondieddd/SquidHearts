package com.me.nondied.squidhearts.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.text.Text;

public class SquidHeartsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("squidhearts")
                    .then(ClientCommandManager.literal("experience")
                            .then(ClientCommandManager.argument("level", IntegerArgumentType.integer(0))
                                    .executes(context -> {
                                        int level = IntegerArgumentType.getInteger(context, "level");
                                        SquidHeartsClientState.visualExperience = level;
                                        context.getSource().sendFeedback(Text.literal("§aExperiencia visual cambiada a: " + level));
                                        return 1;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("SCG2")
                            .executes(context -> {
                                // Verde raro / Vanilla
                                SquidHeartsClientState.expColor = 0x80FF20;
                                context.getSource().sendFeedback(Text.literal("§aColor de experiencia cambiado a SCG2 (Verde)"));
                                return 1;
                            })
                    )
                    .then(ClientCommandManager.literal("SCG3")
                            .executes(context -> {
                                // Cian claro
                                SquidHeartsClientState.expColor = 0x55FFFF;
                                context.getSource().sendFeedback(Text.literal("§bColor de experiencia cambiado a SCG3 (Cian)"));
                                return 1;
                            })
                    )
                    .then(ClientCommandManager.literal("SCG4")
                            .executes(context -> {
                                // Blanco
                                SquidHeartsClientState.expColor = 0xFFFFFF;
                                context.getSource().sendFeedback(Text.literal("§fColor de experiencia cambiado a SCG4 (Blanco)"));
                                return 1;
                            })
                    )
                    .then(ClientCommandManager.literal("reset")
                            .executes(context -> {
                                SquidHeartsClientState.visualExperience = -1;
                                SquidHeartsClientState.expColor = 0x80FF20; // Resetea al verde
                                context.getSource().sendFeedback(Text.literal("§eExperiencia visual y color reseteados a la normalidad."));
                                return 1;
                            })
                    )
            );
        });
    }
}