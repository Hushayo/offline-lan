package com.hushayo.offlinelan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = OfflineLan.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = OfflineLan.MOD_ID, value = Dist.CLIENT)
public class OfflineLanClient {
    private static Button toggleButton;

    public OfflineLanClient(ModContainer container) {
    }

    static void clearButton() {
        toggleButton = null;
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        toggleButton = null;
    }

    private static Component label() {
        return Component.literal("Online Mode: " + (OfflineLan.isOnlineMode() ? "ON" : "OFF"));
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

        // Keep the flag in sync with the live server (e.g. after /online-mode was used).
        // Re-apply the flag so the button state is authoritative.
        OfflineLan.setOnlineMode(server, OfflineLan.isOnlineMode());

        int x = screen.width / 2 - 100;
        int y = screen.height - 60;

        toggleButton = Button.builder(label(), btn -> {
            MinecraftServer s = Minecraft.getInstance().getSingleplayerServer();
            OfflineLan.setOnlineMode(s, !OfflineLan.isOnlineMode());
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
