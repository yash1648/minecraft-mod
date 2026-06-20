package com.aman.ainpc;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = AINPC.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // === AI Server ===
    private static final ForgeConfigSpec.ConfigValue<String> AI_SERVER_URL = BUILDER
            .comment("URL of the AI backend server")
            .define("aiServerUrl", "http://127.0.0.1:5000/chat");

    // === NPC Behavior ===
    private static final ForgeConfigSpec.ConfigValue<String> NPC_NAME = BUILDER
            .comment("Name displayed above the NPC")
            .define("npcName", "AI NPC");

    private static final ForgeConfigSpec.IntValue NPC_OP_LEVEL = BUILDER
            .comment("Required OP level to use /spawnnpc (0-4)")
            .defineInRange("npcOpLevel", 2, 0, 4);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static String aiServerUrl;
    public static String npcName;
    public static int npcOpLevel;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        aiServerUrl = AI_SERVER_URL.get();
        npcName = NPC_NAME.get();
        npcOpLevel = NPC_OP_LEVEL.get();
    }
}
