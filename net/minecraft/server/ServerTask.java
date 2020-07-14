/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server;

public class ServerTask
implements Runnable {
    private final int creationTicks;
    private final Runnable runnable;

    public ServerTask(int creationTicks, Runnable runnable) {
        this.creationTicks = creationTicks;
        this.runnable = runnable;
    }

    public int getCreationTicks() {
        return this.creationTicks;
    }

    @Override
    public void run() {
        this.runnable.run();
    }
}

