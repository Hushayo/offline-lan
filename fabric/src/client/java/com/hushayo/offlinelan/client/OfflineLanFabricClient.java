package com.hushayo.offlinelan.client;

import com.hushayo.offlinelan.OfflineLanFabric;
import com.hushayo.offlinelan.client.mixin.ScreenAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

/**
 * Client side: adds an Online Mode ON/OFF toggle button to the
 * "Open to LAN" screen (ShareToLanScreen on 1.20.2+, OpenToLanScreen before).
 */
public class OfflineLanFabricClient implements ClientModInitializer {
    private static Button toggleButton;

    private static Component label() {
        return Component.literal("Online Mode: " + (OfflineLanFabric.isOnlineMode() ? "ON" : "OFF"));
    }

    private static boolean isLanScreen(Screen screen) {
        String name = screen.getClass().getSimpleName();
        return name.equals("ShareToLanScreen") || name.equals("OpenToLanScreen");
    }

    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!isLanScreen(screen)) {
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            MinecraftServer server = mc.getSingleplayerServer();
            if (server == null) {
                return;
            }

            // Re-apply the flag so the button state is authoritative.
            OfflineLanFabric.setOnlineMode(server, OfflineLanFabric.isOnlineMode());

            int x = scaledWidth / 2 - 100;
            int y = scaledHeight - 60;

            toggleButton = Button.builder(label(), btn -> {
                MinecraftServer s = Minecraft.getInstance().getSingleplayerServer();
                OfflineLanFabric.setOnlineMode(s, !OfflineLanFabric.isOnlineMode());
                btn.setMessage(label());
            }).bounds(x, y, 200, 20).build();

            ((ScreenAccessor) screen).offlinelan$addWidget(toggleButton);
        });

        // Keep the label fresh (e.g. after /online-mode) and drop the stale
        // button reference once the LAN screen is closed.
        ScreenEvents.AFTER_RENDER.register((Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) -> {
            if (toggleButton == null) {
                return;
            }
            if (isLanScreen(screen)) {
                toggleButton.setMessage(label());
            } else {
                toggleButton = null;
            }
        });
    }
}
