package com.hushayo.offlinelan;

import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * OP check + message helper for /online-mode, legacy variant (Minecraft 1.20 - 1.21.10).
 *
 * <p>Uses the numeric permission level. This exact API
 * ({@code CommandSourceStack.hasPermission(int)}) was removed in 1.21.11 by the
 * permission overhaul; see the {@code cmdperm-modern} variant. Only one variant
 * is ever on the compile classpath (selected in build.gradle via
 * {@code -Pcmdperm=...}).</p>
 */
public final class CmdPerm {
    private CmdPerm() {
    }

    public static Predicate<CommandSourceStack> op() {
        return src -> src.hasPermission(3);
    }

    public static void say(CommandSourceStack src, String text) {
        src.sendSuccess(() -> Component.literal(text), false);
    }
}
