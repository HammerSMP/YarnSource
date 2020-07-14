/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.PostProcessShader;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Matrix4f;
import org.apache.commons.io.IOUtils;

@Environment(value=EnvType.CLIENT)
public class ShaderEffect
implements AutoCloseable {
    private final Framebuffer mainTarget;
    private final ResourceManager resourceManager;
    private final String name;
    private final List<PostProcessShader> passes = Lists.newArrayList();
    private final Map<String, Framebuffer> targetsByName = Maps.newHashMap();
    private final List<Framebuffer> defaultSizedTargets = Lists.newArrayList();
    private Matrix4f projectionMatrix;
    private int width;
    private int height;
    private float time;
    private float lastTickDelta;

    public ShaderEffect(TextureManager arg, ResourceManager arg2, Framebuffer framebuffer, Identifier location) throws IOException, JsonSyntaxException {
        this.resourceManager = arg2;
        this.mainTarget = framebuffer;
        this.time = 0.0f;
        this.lastTickDelta = 0.0f;
        this.width = framebuffer.viewportWidth;
        this.height = framebuffer.viewportHeight;
        this.name = location.toString();
        this.setupProjectionMatrix();
        this.parseEffect(arg, location);
    }

    private void parseEffect(TextureManager arg, Identifier location) throws IOException, JsonSyntaxException {
        Resource lv;
        block11: {
            lv = null;
            try {
                lv = this.resourceManager.getResource(location);
                JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(lv.getInputStream(), StandardCharsets.UTF_8));
                if (JsonHelper.hasArray(jsonObject, "targets")) {
                    JsonArray jsonArray = jsonObject.getAsJsonArray("targets");
                    int i = 0;
                    for (JsonElement jsonElement : jsonArray) {
                        try {
                            this.parseTarget(jsonElement);
                        }
                        catch (Exception exception) {
                            ShaderParseException lv2 = ShaderParseException.wrap(exception);
                            lv2.addFaultyElement("targets[" + i + "]");
                            throw lv2;
                        }
                        ++i;
                    }
                }
                if (!JsonHelper.hasArray(jsonObject, "passes")) break block11;
                JsonArray jsonArray2 = jsonObject.getAsJsonArray("passes");
                int j = 0;
                for (JsonElement jsonElement2 : jsonArray2) {
                    try {
                        this.parsePass(arg, jsonElement2);
                    }
                    catch (Exception exception2) {
                        ShaderParseException lv3 = ShaderParseException.wrap(exception2);
                        lv3.addFaultyElement("passes[" + j + "]");
                        throw lv3;
                    }
                    ++j;
                }
            }
            catch (Exception exception3) {
                try {
                    ShaderParseException lv4 = ShaderParseException.wrap(exception3);
                    lv4.addFaultyFile(location.getPath());
                    throw lv4;
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(lv);
                    throw throwable;
                }
            }
        }
        IOUtils.closeQuietly((Closeable)lv);
    }

    private void parseTarget(JsonElement jsonTarget) throws ShaderParseException {
        if (JsonHelper.isString(jsonTarget)) {
            this.addTarget(jsonTarget.getAsString(), this.width, this.height);
        } else {
            JsonObject jsonObject = JsonHelper.asObject(jsonTarget, "target");
            String string = JsonHelper.getString(jsonObject, "name");
            int i = JsonHelper.getInt(jsonObject, "width", this.width);
            int j = JsonHelper.getInt(jsonObject, "height", this.height);
            if (this.targetsByName.containsKey(string)) {
                throw new ShaderParseException(string + " is already defined");
            }
            this.addTarget(string, i, j);
        }
    }

    private void parsePass(TextureManager arg, JsonElement jsonPass) throws IOException {
        JsonArray jsonArray2;
        JsonObject jsonObject;
        block21: {
            jsonObject = JsonHelper.asObject(jsonPass, "pass");
            String string = JsonHelper.getString(jsonObject, "name");
            String string2 = JsonHelper.getString(jsonObject, "intarget");
            String string3 = JsonHelper.getString(jsonObject, "outtarget");
            Framebuffer lv = this.getTarget(string2);
            Framebuffer lv2 = this.getTarget(string3);
            if (lv == null) {
                throw new ShaderParseException("Input target '" + string2 + "' does not exist");
            }
            if (lv2 == null) {
                throw new ShaderParseException("Output target '" + string3 + "' does not exist");
            }
            PostProcessShader lv3 = this.addPass(string, lv, lv2);
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "auxtargets", null);
            if (jsonArray == null) break block21;
            int i = 0;
            for (JsonElement jsonElement2 : jsonArray) {
                block20: {
                    try {
                        Framebuffer lv4;
                        boolean bl2;
                        String string4;
                        block22: {
                            String string7;
                            JsonObject jsonObject2 = JsonHelper.asObject(jsonElement2, "auxtarget");
                            string4 = JsonHelper.getString(jsonObject2, "name");
                            String string5 = JsonHelper.getString(jsonObject2, "id");
                            if (string5.endsWith(":depth")) {
                                boolean bl = true;
                                String string6 = string5.substring(0, string5.lastIndexOf(58));
                            } else {
                                bl2 = false;
                                string7 = string5;
                            }
                            lv4 = this.getTarget(string7);
                            if (lv4 != null) break block22;
                            if (bl2) {
                                throw new ShaderParseException("Render target '" + string7 + "' can't be used as depth buffer");
                            }
                            Identifier lv5 = new Identifier("textures/effect/" + string7 + ".png");
                            Resource lv6 = null;
                            try {
                                lv6 = this.resourceManager.getResource(lv5);
                            }
                            catch (FileNotFoundException fileNotFoundException) {
                                try {
                                    throw new ShaderParseException("Render target or texture '" + string7 + "' does not exist");
                                }
                                catch (Throwable throwable) {
                                    IOUtils.closeQuietly(lv6);
                                    throw throwable;
                                }
                            }
                            IOUtils.closeQuietly((Closeable)lv6);
                            arg.bindTexture(lv5);
                            AbstractTexture lv7 = arg.getTexture(lv5);
                            int j = JsonHelper.getInt(jsonObject2, "width");
                            int k = JsonHelper.getInt(jsonObject2, "height");
                            boolean bl3 = JsonHelper.getBoolean(jsonObject2, "bilinear");
                            if (bl3) {
                                RenderSystem.texParameter(3553, 10241, 9729);
                                RenderSystem.texParameter(3553, 10240, 9729);
                            } else {
                                RenderSystem.texParameter(3553, 10241, 9728);
                                RenderSystem.texParameter(3553, 10240, 9728);
                            }
                            lv3.addAuxTarget(string4, lv7::getGlId, j, k);
                            break block20;
                        }
                        if (bl2) {
                            lv3.addAuxTarget(string4, lv4::method_30278, lv4.textureWidth, lv4.textureHeight);
                        } else {
                            lv3.addAuxTarget(string4, lv4::method_30277, lv4.textureWidth, lv4.textureHeight);
                        }
                    }
                    catch (Exception exception) {
                        ShaderParseException lv8 = ShaderParseException.wrap(exception);
                        lv8.addFaultyElement("auxtargets[" + i + "]");
                        throw lv8;
                    }
                }
                ++i;
            }
        }
        if ((jsonArray2 = JsonHelper.getArray(jsonObject, "uniforms", null)) != null) {
            int l = 0;
            for (JsonElement jsonElement3 : jsonArray2) {
                try {
                    this.parseUniform(jsonElement3);
                }
                catch (Exception exception2) {
                    ShaderParseException lv9 = ShaderParseException.wrap(exception2);
                    lv9.addFaultyElement("uniforms[" + l + "]");
                    throw lv9;
                }
                ++l;
            }
        }
    }

    private void parseUniform(JsonElement jsonUniform) throws ShaderParseException {
        JsonObject jsonObject = JsonHelper.asObject(jsonUniform, "uniform");
        String string = JsonHelper.getString(jsonObject, "name");
        GlUniform lv = this.passes.get(this.passes.size() - 1).getProgram().getUniformByName(string);
        if (lv == null) {
            throw new ShaderParseException("Uniform '" + string + "' does not exist");
        }
        float[] fs = new float[4];
        int i = 0;
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
        for (JsonElement jsonElement2 : jsonArray) {
            try {
                fs[i] = JsonHelper.asFloat(jsonElement2, "value");
            }
            catch (Exception exception) {
                ShaderParseException lv2 = ShaderParseException.wrap(exception);
                lv2.addFaultyElement("values[" + i + "]");
                throw lv2;
            }
            ++i;
        }
        switch (i) {
            case 0: {
                break;
            }
            case 1: {
                lv.set(fs[0]);
                break;
            }
            case 2: {
                lv.set(fs[0], fs[1]);
                break;
            }
            case 3: {
                lv.set(fs[0], fs[1], fs[2]);
                break;
            }
            case 4: {
                lv.set(fs[0], fs[1], fs[2], fs[3]);
            }
        }
    }

    public Framebuffer getSecondaryTarget(String name) {
        return this.targetsByName.get(name);
    }

    public void addTarget(String name, int width, int height) {
        Framebuffer lv = new Framebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
        lv.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.targetsByName.put(name, lv);
        if (width == this.width && height == this.height) {
            this.defaultSizedTargets.add(lv);
        }
    }

    @Override
    public void close() {
        for (Framebuffer lv : this.targetsByName.values()) {
            lv.delete();
        }
        for (PostProcessShader lv2 : this.passes) {
            lv2.close();
        }
        this.passes.clear();
    }

    public PostProcessShader addPass(String programName, Framebuffer source, Framebuffer dest) throws IOException {
        PostProcessShader lv = new PostProcessShader(this.resourceManager, programName, source, dest);
        this.passes.add(this.passes.size(), lv);
        return lv;
    }

    private void setupProjectionMatrix() {
        this.projectionMatrix = Matrix4f.projectionMatrix(this.mainTarget.textureWidth, this.mainTarget.textureHeight, 0.1f, 1000.0f);
    }

    public void setupDimensions(int targetsWidth, int targetsHeight) {
        this.width = this.mainTarget.textureWidth;
        this.height = this.mainTarget.textureHeight;
        this.setupProjectionMatrix();
        for (PostProcessShader lv : this.passes) {
            lv.setProjectionMatrix(this.projectionMatrix);
        }
        for (Framebuffer lv2 : this.defaultSizedTargets) {
            lv2.resize(targetsWidth, targetsHeight, MinecraftClient.IS_SYSTEM_MAC);
        }
    }

    public void render(float tickDelta) {
        if (tickDelta < this.lastTickDelta) {
            this.time += 1.0f - this.lastTickDelta;
            this.time += tickDelta;
        } else {
            this.time += tickDelta - this.lastTickDelta;
        }
        this.lastTickDelta = tickDelta;
        while (this.time > 20.0f) {
            this.time -= 20.0f;
        }
        for (PostProcessShader lv : this.passes) {
            lv.render(this.time / 20.0f);
        }
    }

    public final String getName() {
        return this.name;
    }

    private Framebuffer getTarget(String name) {
        if (name == null) {
            return null;
        }
        if (name.equals("minecraft:main")) {
            return this.mainTarget;
        }
        return this.targetsByName.get(name);
    }
}

