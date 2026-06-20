package com.aman.ainpc;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(AINPC.MODID)
public class AINPC {

    public static final String MODID = "ainpc";

    public AINPC() {
        RegisterCommandsEvent.BUS.addListener(this::onRegisterCommands);
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        NPCCommand.register(event.getDispatcher());
    }
}