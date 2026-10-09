package com.hushayo.offlinelan;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStoppedEvent;

/**
 * Client side: adds an Online Mode ON/OFF toggle button to the Share-to-LAN screen.
 *
 * <p>Plain class, wired up from the main mod guarded by
 * {@code FMLEnvironment.dist == Dist.CLIENT} instead of annotations, so this
 * compiles and runs on every Forge version (Forge's {@code @Mod} has no
 * {@code dist()} element). The class is never loaded on dedicated servers.</p>
 */
public class OfflineLanForgeClient {
    private static Button toggleButton;

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(OfflineLanForgeClient::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(OfflineLanForgeClient::onScreenInit);
        MinecraftForge.EVENT_BUS.addListener(OfflineLanForgeClient::onScreenRender);
    }

    private static Component label() {
        return Component.literal("Online Mode: " + (OfflineLanForge.isOnlineMode() ? "ON" : "OFF"));
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

    private static void onScreenRender(ScreenEvent.Render.Post event) {
        if (toggleButton != null && event.getScreen() instanceof ShareToLanScreen) {
            toggleButton.setMessage(label());
        }
    }
}
