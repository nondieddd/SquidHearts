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

                                        // Mensaje opcional en el chat (solo tú lo ves)
                                        context.getSource().sendFeedback(Text.literal("§aExperiencia visual cambiada a: " + level));
                                        return 1;
                                    })
                            )
                    )
                    .then(ClientCommandManager.literal("reset")
                            .executes(context -> {
                                SquidHeartsClientState.visualExperience = -1;
                                context.getSource().sendFeedback(Text.literal("§eExperiencia visual reseteada a la real."));
                                return 1;
                            })
                    )
            );
        });
    }
}