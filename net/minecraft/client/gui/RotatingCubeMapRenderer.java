/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RotatingCubeMapRenderer {
    private final MinecraftClient client;
    private final CubeMapRenderer cubeMap;
    private float time;

    public RotatingCubeMapRenderer(CubeMapRenderer arg) {
        this.cubeMap = arg;
        this.client = MinecraftClient.getInstance();
    }

    public void render(float f, float g) {
        this.time += f;
        this.cubeMap.draw(this.client, MathHelper.sin(this.time * 0.001f) * 5.0f + 25.0f, -this.time * 0.1f, g);
    }
}

