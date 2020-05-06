/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  it.unimi.dsi.fastutil.ints.IntSets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.BlankGlyph;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.RenderableGlyph;

@Environment(value=EnvType.CLIENT)
public class BlankFont
implements Font {
    @Override
    @Nullable
    public RenderableGlyph getGlyph(int i) {
        return BlankGlyph.INSTANCE;
    }

    @Override
    public IntSet method_27442() {
        return IntSets.EMPTY_SET;
    }
}

