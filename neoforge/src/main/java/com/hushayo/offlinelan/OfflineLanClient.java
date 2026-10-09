package com.hushayo.offlinelan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * Client side: adds an Online Mode ON/OFF toggle button to the Share-to-LAN screen.
 *
 * <p>Plain class, wired up from the main mod guarded by
 * {@code FMLEnvironment.dist == Dist.CLIENT} instead of annotations, so this
 * compiles and runs on every NeoForge version (old {@code @Mod} layouts had
 * neither {@code dist()} nor a top-level {@code EventBusSubscriber}). The class
 * is never loaded on dedicated servers.</p>
 */
public class OfflineLanClient {
    private static Button toggleButton;

    public static void init() {
        NeoForge.EVENT_BUS.addListener(OfflineLanClient::onServerStopped);
        NeoForge.EVENT_BUS.addListener(OfflineLanClient::onScreenInit);
        NeoForge.EVENT_BUS.addListener(OfflineLanClient::onScreenRender);
    }

    private static Component label() {
        return Component.literal("Online Mode: " + (OfflineLan.isOnlineMode() ? "ON" : "OFF"));
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        toggleButton = null;
    }

    private static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof ShareToLanScreen screen)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        MinecraftServer server = mc.getSingleplayerServer();
        if (server == null) {
            return;
        }

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

    private static void onScreenRender(ScreenEvent.Render.Post event) {
        if (toggleButton != null && event.getScreen() instanceof ShareToLanScreen) {
            toggleButton.setMessage(label());
        }
    }
}
