package com.aman.ainpc;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.minecraft.world.entity.EntityType;

public class NPCCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("spawnnpc")
                        .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.byId(Config.npcOpLevel))))
                        .executes(context -> {
                            ServerLevel level = context.getSource().getLevel();

                            AINPCEntity npc = new AINPCEntity(
                                    EntityType.VILLAGER,
                                    level
                            );

                            npc.setCustomName(net.minecraft.network.chat.Component.literal(Config.npcName));
                            npc.setCustomNameVisible(true);

                            npc.setPos(
                                    context.getSource().getPosition().x,
                                    context.getSource().getPosition().y,
                                    context.getSource().getPosition().z
                            );

                            level.addFreshEntity(npc);

                            context.getSource().sendSuccess(
                                    () -> net.minecraft.network.chat.Component.literal(Config.npcName + " spawned"),
                                    true
                            );

                            return 1;
                        })
        );
    }
}