package com.aman.ainpc;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class NPCInteractionHandler {

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.EntityInteract event) {

        if (event.getTarget() instanceof AINPCEntity npc) {

            ChatListener.talkingToNPC = true;
            ChatListener.currentNPC = npc;

            ChatListener.freezeNPC();

            event.getEntity().displayClientMessage(
                    Component.literal("Talking to AI NPC..."),
                    false
            );
        }
    }
}