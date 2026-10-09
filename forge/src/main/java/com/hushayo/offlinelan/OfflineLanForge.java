package com.hushayo.offlinelan;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.api.distmarker.Dist;
import org.slf4j.Logger;

/**
 * OfflineLAN - allows non-premium (offline / cracked) accounts to join a LAN world.
 *
 * <p>Only the host needs this mod. When the host opens a singleplayer world to LAN
 * with Online Mode OFF, the integrated server skips Mojang session verification,
 * so offline-mode clients on the same network can join.</p>
 */
@Mod(OfflineLanForge.MOD_ID)
public class OfflineLanForge {
    public static final String MOD_ID = "offlinelan";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Defaults to true (secure, vanilla behaviour). Host must deliberately turn it OFF.
    private static volatile boolean onlineMode = true;

    @SuppressWarnings("unused")
    public OfflineLanForge(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.addListener(OfflineLanForge::onServerStarted);
        MinecraftForge.EVENT_BUS.addListener(OfflineLanForge::onServerStopped);
        MinecraftForge.EVENT_BUS.addListener(OfflineLanForge::onRegisterCommands);
        // Client-only screen hook. Guarded (not annotated) so the common code
        // stays compatible with every Forge version.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            OfflineLanForgeClient.init();
        }
    }

    private static void onServerStarted(ServerStartedEvent event) {
        // Reset to secure default on every fresh server start for safety.
        onlineMode = true;
        applyTo(event.getServer());
        LOGGER.info("[offlinelan] Server started, online-mode={} (use Share-to-LAN screen or /online-mode to change)", onlineMode ? "ON" : "OFF");
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        onlineMode = true;
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
        event.getDispatcher().register(cmd);
    }
}
