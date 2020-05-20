/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.realmsclient.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public interface Errable {
    public void error(Text var1);

    default public void error(String string) {
        this.error(new LiteralText(string));
    }
}

