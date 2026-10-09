package com.hushayo.offlinelan;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OfflineLAN - allows non-premium (offline / cracked) accounts to join a LAN world.
 *
 * <p>Only the host needs this mod. When the host opens a singleplayer world to LAN
 * with Online Mode OFF, the integrated server skips Mojang session verification,
 * so offline-mode clients on the same network can join.</p>
 */
public class OfflineLanFabric implements ModInitializer {
    public static final String MOD_ID = "offlinelan";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Defaults to true (secure, vanilla behaviour). Host must deliberately turn it OFF.
    private static volatile boolean onlineMode = true;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("online-mode")
                    .requires(CmdPerm.op())
                    .then(Commands.literal("on").executes(ctx -> {
                        setOnlineMode(ctx.getSource().getServer(), true);
                        CmdPerm.say(ctx.getSource(), "Online Mode is now ON. Only premium accounts can join LAN.");
                        return 1;
                    }))
                    .then(Commands.literal("off").executes(ctx -> {
                        setOnlineMode(ctx.getSource().getServer(), false);
                        CmdPerm.say(ctx.getSource(), "Online Mode is now OFF. Offline/cracked accounts can join LAN.");
                        return 1;
                    }))
                    .then(Commands.literal("status").executes(ctx -> {
                        MinecraftServer server = ctx.getSource().getServer();
                        boolean effective = server != null ? server.usesAuthentication() : onlineMode;
                        CmdPerm.say(ctx.getSource(), "Online Mode is currently " + (effective ? "ON" : "OFF"));
                        return 1;
                    }))
                    .executes(ctx -> {
                        MinecraftServer server = ctx.getSource().getServer();
                        boolean effective = server != null ? server.usesAuthentication() : onlineMode;
                        CmdPerm.say(ctx.getSource(), "Online Mode is currently " + (effective ? "ON" : "OFF") + ". Use /online-mode on|off");
                        return 1;
                    });
            dispatcher.register(cmd);
        });
    }

    public static boolean isOnlineMode() {
        return onlineMode;
    }

    /**
     * Set online-mode and apply it to the given server, if present.
     * Passing a null server only updates the pending flag.
     */
    public static void setOnlineMode(MinecraftServer server, boolean enabled) {
        onlineMode = enabled;
        if (server != null) {
            try {
                server.setUsesAuthentication(onlineMode);
            } catch (Exception e) {
                LOGGER.error("[offlinelan] Failed to set usesAuthentication", e);
            }
        }
        LOGGER.info("[offlinelan] online-mode set to {}", enabled ? "ON (premium only)" : "OFF (allow offline/cracked LAN clients)");
    }
}
