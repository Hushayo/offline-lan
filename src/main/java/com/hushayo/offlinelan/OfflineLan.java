package com.hushayo.offlinelan;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;

/**
 * OfflineLAN - allows non-premium (offline / cracked) accounts to join a LAN world.
 *
 * <p>Only the host needs this mod. When the host opens a singleplayer world to LAN
 * with Online Mode OFF, the integrated server skips Mojang session verification,
 * so offline-mode clients on the same network can join.</p>
 */
@Mod(OfflineLan.MOD_ID)
public class OfflineLan {
    public static final String MOD_ID = "offlinelan";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Defaults to true (secure, vanilla behaviour). Host must deliberately turn it OFF.
    private static volatile boolean onlineMode = true;

    public OfflineLan(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.addListener(OfflineLan::onServerStarted);
        NeoForge.EVENT_BUS.addListener(OfflineLan::onServerStopped);
        NeoForge.EVENT_BUS.addListener(OfflineLan::onRegisterCommands);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        // Reset to secure default on every fresh server start for safety.
        onlineMode = true;
        applyTo(event.getServer());
        LOGGER.info("[offlinelan] Server started, online-mode={} (use Share-to-LAN screen or /online-mode to change)", onlineMode ? "ON" : "OFF");
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        onlineMode = true;
        OfflineLanClient.clearButton();
    }

    public static boolean isOnlineMode() {
        return onlineMode;
    }

    /**
     * Set online-mode and apply it to the given server, if present.
     * Passing a null server only updates the pending flag (applied on next server start / screen open).
     */
    public static void setOnlineMode(MinecraftServer server, boolean enabled) {
        onlineMode = enabled;
        if (server != null) {
            applyTo(server);
        }
        LOGGER.info("[offlinelan] online-mode set to {}", enabled ? "ON (premium only)" : "OFF (allow offline/cracked LAN clients)");
    }

    private static void applyTo(MinecraftServer server) {
        try {
            server.setUsesAuthentication(onlineMode);
        } catch (Exception e) {
            LOGGER.error("[offlinelan] Failed to set usesAuthentication", e);
        }
    }

    private static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("online-mode")
                .requires(src -> src.hasPermission(Commands.LEVEL_ADMINS))
                .then(Commands.literal("on").executes(ctx -> {
                    setOnlineMode(ctx.getSource().getServer(), true);
                    ctx.getSource().sendSuccess(() -> Component.literal("Online Mode is now ON. Only premium accounts can join LAN."), false);
                    return 1;
                }))
                .then(Commands.literal("off").executes(ctx -> {
                    setOnlineMode(ctx.getSource().getServer(), false);
                    ctx.getSource().sendSuccess(() -> Component.literal("Online Mode is now OFF. Offline/cracked accounts can join LAN."), false);
                    return 1;
                }))
                .then(Commands.literal("status").executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    boolean effective = server != null ? server.usesAuthentication() : onlineMode;
                    ctx.getSource().sendSuccess(() -> Component.literal("Online Mode is currently " + (effective ? "ON" : "OFF")), false);
                    return 1;
                }))
                .executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    boolean effective = server != null ? server.usesAuthentication() : onlineMode;
                    ctx.getSource().sendSuccess(() -> Component.literal("Online Mode is currently " + (effective ? "ON" : "OFF") + ". Use /online-mode on|off"), false);
                    return 1;
                });
        event.getDispatcher().register(cmd);
    }
}
