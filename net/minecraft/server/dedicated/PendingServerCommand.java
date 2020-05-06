/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.dedicated;

import net.minecraft.server.command.ServerCommandSource;

public class PendingServerCommand {
    public final String command;
    public final ServerCommandSource source;

    public PendingServerCommand(String string, ServerCommandSource arg) {
        this.command = string;
        this.source = arg;
    }
}

