/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.util;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;

@Environment(value=EnvType.CLIENT)
public class TextCollector {
    private boolean needsFakeRoot = true;
    @Nullable
    private MutableText root;

    public void add(MutableText arg) {
        if (this.root == null) {
            this.root = arg;
        } else {
            if (this.needsFakeRoot) {
                this.root = new LiteralText("").append(this.root);
                this.needsFakeRoot = false;
            }
            this.root.append(arg);
        }
    }

    @Nullable
    public MutableText getRawCombined() {
        return this.root;
    }

    public MutableText getCombined() {
        return this.root != null ? this.root : new LiteralText("");
    }
}

