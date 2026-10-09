package com.hushayo.offlinelan;

import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * OP check + message helper for /online-mode, legacy-1.19 variant (Forge 1.19.x).
 *
 * <p>1.19.x has {@code CommandSourceStack.hasPermission(int)} but its
 * {@code sendSuccess} takes a {@code Component} directly (the
 * {@code Supplier} overload only exists on 1.20+). Only used for Forge 1.19.x
 * jobs (selected in build.gradle via {@code -Pcmdperm=legacy19}).</p>
 */
public final class CmdPerm {
    private CmdPerm() {
    }

    public static Predicate<CommandSourceStack> op() {
        return src -> src.hasPermission(3);
    }

    public static void say(CommandSourceStack src, String text) {
        src.sendSuccess(Component.literal(text), false);
    }
}
