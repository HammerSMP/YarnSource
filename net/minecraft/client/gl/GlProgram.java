/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlShader;

@Environment(value=EnvType.CLIENT)
public interface GlProgram {
    public int getProgramRef();

    public void markUniformsDirty();

    public GlShader getVertexShader();

    public GlShader getFragmentShader();
}

