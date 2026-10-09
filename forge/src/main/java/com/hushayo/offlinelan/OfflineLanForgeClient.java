package com.hushayo.offlinelan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = OfflineLanForge.MOD_ID, dist = Dist.CLIENT)
@Mod.EventBusSubscriber(modid = OfflineLanForge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OfflineLanForgeClient {
    private static Button toggleButton;

    private static Component label() {
        return Component.literal("Online Mode: " + (OfflineLanForge.isOnlineMode() ? "ON" : "OFF"));
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        toggleButton = null;
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof ShareToLanScreen screen)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        MinecraftServer server = mc.getSingleplayerServer();
        if (server == null) {
            return;
        }

        // Re-apply the flag so the button state is authoritative.
        OfflineLanForge.setOnlineMode(server, OfflineLanForge.isOnlineMode());

        int x = screen.width / 2 - 100;
        int y = screen.height - 60;

        toggleButton = Button.builder(label(), btn -> {
            MinecraftServer s = Minecraft.getInstance().getSingleplayerServer();
            OfflineLanForge.setOnlineMode(s, !OfflineLanForge.isOnlineMode());
            btn.setMessage(label());
        }).bounds(x, y, 200, 20).build();

        event.addListener(toggleButton);
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (toggleButton != null && event.getScreen() instanceof ShareToLanScreen) {
            toggleButton.setMessage(label());
        }
    }
}
