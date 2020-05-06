/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.scoreboard;

import net.minecraft.world.PersistentState;

public class ScoreboardSynchronizer
implements Runnable {
    private final PersistentState compound;

    public ScoreboardSynchronizer(PersistentState arg) {
        this.compound = arg;
    }

    @Override
    public void run() {
        this.compound.markDirty();
    }
}

