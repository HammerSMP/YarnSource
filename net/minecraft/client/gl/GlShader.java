/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.gl;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlProgram;
import net.minecraft.client.texture.TextureUtil;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class GlShader {
    private final Type shaderType;
    private final String name;
    private final int shaderRef;
    private int refCount;

    private GlShader(Type arg, int i, String string) {
        this.shaderType = arg;
        this.shaderRef = i;
        this.name = string;
    }

    public void attachTo(GlProgram arg) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        ++this.refCount;
        GlStateManager.attachShader(arg.getProgramRef(), this.shaderRef);
    }

    public void release() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        --this.refCount;
        if (this.refCount <= 0) {
            GlStateManager.deleteShader(this.shaderRef);
            this.shaderType.getLoadedShaders().remove(this.name);
        }
    }

    public String getName() {
        return this.name;
    }

    public static GlShader createFromResource(Type arg, String string, InputStream inputStream) throws IOException {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        String string2 = TextureUtil.readAllToString(inputStream);
        if (string2 == null) {
            throw new IOException("Could not load program " + arg.getName());
        }
        int i = GlStateManager.createShader(arg.getGlType());
        GlStateManager.shaderSource(i, string2);
        GlStateManager.compileShader(i);
        if (GlStateManager.getShader(i, 35713) == 0) {
            String string3 = StringUtils.trim((String)GlStateManager.getShaderInfoLog(i, 32768));
            throw new IOException("Couldn't compile " + arg.getName() + " program: " + string3);
        }
        GlShader lv = new GlShader(arg, i, string);
        arg.getLoadedShaders().put(string, lv);
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        VERTEX("vertex", ".vsh", 35633),
        FRAGMENT("fragment", ".fsh", 35632);

        private final String name;
        private final String fileExtension;
        private final int glType;
        private final Map<String, GlShader> loadedShaders = Maps.newHashMap();

        private Type(String string2, String string3, int j) {
            this.name = string2;
            this.fileExtension = string3;
            this.glType = j;
        }

        public String getName() {
            return this.name;
        }

        public String getFileExtension() {
            return this.fileExtension;
        }

        private int getGlType() {
            return this.glType;
        }

        public Map<String, GlShader> getLoadedShaders() {
            return this.loadedShaders;
        }
    }
}

