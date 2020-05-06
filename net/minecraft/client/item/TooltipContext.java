/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public interface TooltipContext {
    public boolean isAdvanced();

    @Environment(value=EnvType.CLIENT)
    public static enum Default implements TooltipContext
    {
        NORMAL(false),
        ADVANCED(true);

        private final boolean advanced;

        private Default(boolean bl) {
            this.advanced = bl;
        }

        @Override
        public boolean isAdvanced() {
            return this.advanced;
        }
    }
}

