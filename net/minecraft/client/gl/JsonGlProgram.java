/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.io.IOUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.GlBlendState;
import net.minecraft.client.gl.GlProgram;
import net.minecraft.client.gl.GlProgramManager;
import net.minecraft.client.gl.GlShader;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderParseException;
import net.minecraft.client.gl.Uniform;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class JsonGlProgram
implements GlProgram,
AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Uniform dummyUniform = new Uniform();
    private static JsonGlProgram activeProgram;
    private static int activeProgramRef;
    private final Map<String, IntSupplier> samplerBinds = Maps.newHashMap();
    private final List<String> samplerNames = Lists.newArrayList();
    private final List<Integer> samplerShaderLocs = Lists.newArrayList();
    private final List<GlUniform> uniformData = Lists.newArrayList();
    private final List<Integer> uniformLocs = Lists.newArrayList();
    private final Map<String, GlUniform> uniformByName = Maps.newHashMap();
    private final int programRef;
    private final String name;
    private boolean uniformStateDirty;
    private final GlBlendState blendState;
    private final List<Integer> attribLocs;
    private final List<String> attribNames;
    private final GlShader vertexShader;
    private final GlShader fragmentShader;

    public JsonGlProgram(ResourceManager arg, String string) throws IOException {
        Identifier lv = new Identifier("shaders/program/" + string + ".json");
        this.name = string;
        Resource lv2 = null;
        try {
            JsonArray jsonArray3;
            JsonArray jsonArray2;
            lv2 = arg.getResource(lv);
            JsonObject jsonObject = JsonHelper.deserialize(new InputStreamReader(lv2.getInputStream(), StandardCharsets.UTF_8));
            String string2 = JsonHelper.getString(jsonObject, "vertex");
            String string3 = JsonHelper.getString(jsonObject, "fragment");
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "samplers", null);
            if (jsonArray != null) {
                int i = 0;
                for (Object jsonElement : jsonArray) {
                    try {
                        this.addSampler((JsonElement)jsonElement);
                    }
                    catch (Exception exception) {
                        ShaderParseException lv3 = ShaderParseException.wrap(exception);
                        lv3.addFaultyElement("samplers[" + i + "]");
                        throw lv3;
                    }
                    ++i;
                }
            }
            if ((jsonArray2 = JsonHelper.getArray(jsonObject, "attributes", null)) != null) {
                int j = 0;
                this.attribLocs = Lists.newArrayListWithCapacity((int)jsonArray2.size());
                this.attribNames = Lists.newArrayListWithCapacity((int)jsonArray2.size());
                for (Iterator jsonElement2 : jsonArray2) {
                    try {
                        this.attribNames.add(JsonHelper.asString((JsonElement)jsonElement2, "attribute"));
                    }
                    catch (Exception exception2) {
                        ShaderParseException lv4 = ShaderParseException.wrap(exception2);
                        lv4.addFaultyElement("attributes[" + j + "]");
                        throw lv4;
                    }
                    ++j;
                }
            } else {
                this.attribLocs = null;
                this.attribNames = null;
            }
            if ((jsonArray3 = JsonHelper.getArray(jsonObject, "uniforms", null)) != null) {
                int k = 0;
                for (JsonElement jsonElement3 : jsonArray3) {
                    try {
                        this.addUniform(jsonElement3);
                    }
                    catch (Exception exception3) {
                        ShaderParseException lv5 = ShaderParseException.wrap(exception3);
                        lv5.addFaultyElement("uniforms[" + k + "]");
                        throw lv5;
                    }
                    ++k;
                }
            }
            this.blendState = JsonGlProgram.deserializeBlendState(JsonHelper.getObject(jsonObject, "blend", null));
            this.vertexShader = JsonGlProgram.getShader(arg, GlShader.Type.VERTEX, string2);
            this.fragmentShader = JsonGlProgram.getShader(arg, GlShader.Type.FRAGMENT, string3);
            this.programRef = GlProgramManager.createProgram();
            GlProgramManager.linkProgram(this);
            this.finalizeUniformsAndSamplers();
            if (this.attribNames != null) {
                for (String string4 : this.attribNames) {
                    int l = GlUniform.getAttribLocation(this.programRef, string4);
                    this.attribLocs.add(l);
                }
            }
        }
        catch (Exception exception4) {
            ShaderParseException lv6 = ShaderParseException.wrap(exception4);
            lv6.addFaultyFile(lv.getPath());
            throw lv6;
        }
        finally {
            IOUtils.closeQuietly((Closeable)lv2);
        }
        this.markUniformsDirty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static GlShader getShader(ResourceManager arg, GlShader.Type arg2, String string) throws IOException {
        GlShader lv = arg2.getLoadedShaders().get(string);
        if (lv == null) {
            Identifier lv2 = new Identifier("shaders/program/" + string + arg2.getFileExtension());
            Resource lv3 = arg.getResource(lv2);
            try {
                lv = GlShader.createFromResource(arg2, string, lv3.getInputStream());
            }
            finally {
                IOUtils.closeQuietly((Closeable)lv3);
            }
        }
        return lv;
    }

    public static GlBlendState deserializeBlendState(JsonObject jsonObject) {
        if (jsonObject == null) {
            return new GlBlendState();
        }
        int i = 32774;
        int j = 1;
        int k = 0;
        int l = 1;
        int m = 0;
        boolean bl = true;
        boolean bl2 = false;
        if (JsonHelper.hasString(jsonObject, "func") && (i = GlBlendState.getFuncFromString(jsonObject.get("func").getAsString())) != 32774) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "srcrgb") && (j = GlBlendState.getComponentFromString(jsonObject.get("srcrgb").getAsString())) != 1) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "dstrgb") && (k = GlBlendState.getComponentFromString(jsonObject.get("dstrgb").getAsString())) != 0) {
            bl = false;
        }
        if (JsonHelper.hasString(jsonObject, "srcalpha")) {
            l = GlBlendState.getComponentFromString(jsonObject.get("srcalpha").getAsString());
            if (l != 1) {
                bl = false;
            }
            bl2 = true;
        }
        if (JsonHelper.hasString(jsonObject, "dstalpha")) {
            m = GlBlendState.getComponentFromString(jsonObject.get("dstalpha").getAsString());
            if (m != 0) {
                bl = false;
            }
            bl2 = true;
        }
        if (bl) {
            return new GlBlendState();
        }
        if (bl2) {
            return new GlBlendState(j, k, l, m, i);
        }
        return new GlBlendState(j, k, i);
    }

    @Override
    public void close() {
        for (GlUniform lv : this.uniformData) {
            lv.close();
        }
        GlProgramManager.deleteProgram(this);
    }

    public void disable() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlProgramManager.useProgram(0);
        activeProgramRef = -1;
        activeProgram = null;
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            if (this.samplerBinds.get(this.samplerNames.get(i)) == null) continue;
            GlStateManager.activeTexture(33984 + i);
            GlStateManager.disableTexture();
            GlStateManager.bindTexture(0);
        }
    }

    public void enable() {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        this.uniformStateDirty = false;
        activeProgram = this;
        this.blendState.enable();
        if (this.programRef != activeProgramRef) {
            GlProgramManager.useProgram(this.programRef);
            activeProgramRef = this.programRef;
        }
        for (int i = 0; i < this.samplerShaderLocs.size(); ++i) {
            String string = this.samplerNames.get(i);
            IntSupplier intSupplier = this.samplerBinds.get(string);
            if (intSupplier == null) continue;
            RenderSystem.activeTexture(33984 + i);
            RenderSystem.enableTexture();
            int j = intSupplier.getAsInt();
            if (j == -1) continue;
            RenderSystem.bindTexture(j);
            GlUniform.uniform1(this.samplerShaderLocs.get(i), i);
        }
        for (GlUniform lv : this.uniformData) {
            lv.upload();
        }
    }

    @Override
    public void markUniformsDirty() {
        this.uniformStateDirty = true;
    }

    @Nullable
    public GlUniform getUniformByName(String string) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return this.uniformByName.get(string);
    }

    public Uniform getUniformByNameOrDummy(String string) {
        RenderSystem.assertThread(RenderSystem::isOnGameThread);
        GlUniform lv = this.getUniformByName(string);
        return lv == null ? dummyUniform : lv;
    }

    private void finalizeUniformsAndSamplers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        IntArrayList intList = new IntArrayList();
        for (int i = 0; i < this.samplerNames.size(); ++i) {
            String string = this.samplerNames.get(i);
            int j = GlUniform.getUniformLocation(this.programRef, string);
            if (j == -1) {
                LOGGER.warn("Shader {} could not find sampler named {} in the specified shader program.", (Object)this.name, (Object)string);
                this.samplerBinds.remove(string);
                intList.add(i);
                continue;
            }
            this.samplerShaderLocs.add(j);
        }
        for (int k = intList.size() - 1; k >= 0; --k) {
            this.samplerNames.remove(intList.getInt(k));
        }
        for (GlUniform lv : this.uniformData) {
            String string2 = lv.getName();
            int l = GlUniform.getUniformLocation(this.programRef, string2);
            if (l == -1) {
                LOGGER.warn("Could not find uniform named {} in the specified shader program.", (Object)string2);
                continue;
            }
            this.uniformLocs.add(l);
            lv.setLoc(l);
            this.uniformByName.put(string2, lv);
        }
    }

    private void addSampler(JsonElement jsonElement) {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "sampler");
        String string = JsonHelper.getString(jsonObject, "name");
        if (!JsonHelper.hasString(jsonObject, "file")) {
            this.samplerBinds.put(string, null);
            this.samplerNames.add(string);
            return;
        }
        this.samplerNames.add(string);
    }

    public void bindSampler(String string, IntSupplier intSupplier) {
        if (this.samplerBinds.containsKey(string)) {
            this.samplerBinds.remove(string);
        }
        this.samplerBinds.put(string, intSupplier);
        this.markUniformsDirty();
    }

    private void addUniform(JsonElement jsonElement) throws ShaderParseException {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "uniform");
        String string = JsonHelper.getString(jsonObject, "name");
        int i = GlUniform.getTypeIndex(JsonHelper.getString(jsonObject, "type"));
        int j = JsonHelper.getInt(jsonObject, "count");
        float[] fs = new float[Math.max(j, 16)];
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
        if (jsonArray.size() != j && jsonArray.size() > 1) {
            throw new ShaderParseException("Invalid amount of values specified (expected " + j + ", found " + jsonArray.size() + ")");
        }
        int k = 0;
        for (JsonElement jsonElement2 : jsonArray) {
            try {
                fs[k] = JsonHelper.asFloat(jsonElement2, "value");
            }
            catch (Exception exception) {
                ShaderParseException lv = ShaderParseException.wrap(exception);
                lv.addFaultyElement("values[" + k + "]");
                throw lv;
            }
            ++k;
        }
        if (j > 1 && jsonArray.size() == 1) {
            while (k < j) {
                fs[k] = fs[0];
                ++k;
            }
        }
        int l = j > 1 && j <= 4 && i < 8 ? j - 1 : 0;
        GlUniform lv2 = new GlUniform(string, i + l, j, this);
        if (i <= 3) {
            lv2.set((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
        } else if (i <= 7) {
            lv2.setForDataType(fs[0], fs[1], fs[2], fs[3]);
        } else {
            lv2.set(fs);
        }
        this.uniformData.add(lv2);
    }

    @Override
    public GlShader getVertexShader() {
        return this.vertexShader;
    }

    @Override
    public GlShader getFragmentShader() {
        return this.fragmentShader;
    }

    @Override
    public int getProgramRef() {
        return this.programRef;
    }

    static {
        activeProgramRef = -1;
    }
}

