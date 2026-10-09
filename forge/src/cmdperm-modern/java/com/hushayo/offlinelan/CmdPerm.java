package com.hushayo.offlinelan;

import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * OP check for /online-mode, modern variant (Minecraft 1.21.11+).
 *
 * <p>The 1.21.11 permission overhaul removed
 * {@code CommandSourceStack.hasPermission(int)} and turned
 * {@code Commands.LEVEL_*} into {@code PermissionCheck} objects, so the legacy
 * numeric check no longer compiles. Only one variant is ever on the compile
 * classpath (selected in build.gradle via {@code -Pcmdperm=modern}).</p>
 */
public final class CmdPerm {
    private CmdPerm() {
    }

    public static Predicate<CommandSourceStack> op() {
        return Commands.hasPermission(Commands.LEVEL_ADMINS);
    }
}
