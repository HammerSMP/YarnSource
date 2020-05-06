/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server;

public class ServerTask
implements Runnable {
    private final int creationTicks;
    private final Runnable runnable;

    public ServerTask(int i, Runnable runnable) {
        this.creationTicks = i;
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

