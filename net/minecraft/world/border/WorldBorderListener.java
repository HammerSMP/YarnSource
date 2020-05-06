/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.border;

import net.minecraft.world.border.WorldBorder;

public interface WorldBorderListener {
    public void onSizeChange(WorldBorder var1, double var2);

    public void onInterpolateSize(WorldBorder var1, double var2, double var4, long var6);

    public void onCenterChanged(WorldBorder var1, double var2, double var4);

    public void onWarningTimeChanged(WorldBorder var1, int var2);

    public void onWarningBlocksChanged(WorldBorder var1, int var2);

    public void onDamagePerBlockChanged(WorldBorder var1, double var2);

    public void onSafeZoneChanged(WorldBorder var1, double var2);

    public static class WorldBorderSyncer
    implements WorldBorderListener {
        private final WorldBorder border;

        public WorldBorderSyncer(WorldBorder arg) {
            this.border = arg;
        }

        @Override
        public void onSizeChange(WorldBorder arg, double d) {
            this.border.setSize(d);
        }

        @Override
        public void onInterpolateSize(WorldBorder arg, double d, double e, long l) {
            this.border.interpolateSize(d, e, l);
        }

        @Override
        public void onCenterChanged(WorldBorder arg, double d, double e) {
            this.border.setCenter(d, e);
        }

        @Override
        public void onWarningTimeChanged(WorldBorder arg, int i) {
            this.border.setWarningTime(i);
        }

        @Override
        public void onWarningBlocksChanged(WorldBorder arg, int i) {
            this.border.setWarningBlocks(i);
        }

        @Override
        public void onDamagePerBlockChanged(WorldBorder arg, double d) {
            this.border.setDamagePerBlock(d);
        }

        @Override
        public void onSafeZoneChanged(WorldBorder arg, double d) {
            this.border.setBuffer(d);
        }
    }
}

