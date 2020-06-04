/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.lwjgl.opengl.ARBFramebufferObject
 *  org.lwjgl.opengl.EXTFramebufferBlit
 *  org.lwjgl.opengl.EXTFramebufferObject
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL13
 *  org.lwjgl.opengl.GL14
 *  org.lwjgl.opengl.GL15
 *  org.lwjgl.opengl.GL20
 *  org.lwjgl.opengl.GL30
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.system.MemoryUtil
 */
package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.platform.FramebufferInfo;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.Untracker;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferBlit;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

@Environment(value=EnvType.CLIENT)
public class GlStateManager {
    private static final FloatBuffer MATRIX_BUFFER = GLX.make(MemoryUtil.memAllocFloat((int)16), floatBuffer -> Untracker.untrack(MemoryUtil.memAddress((FloatBuffer)floatBuffer)));
    private static final AlphaTestState ALPHA_TEST = new AlphaTestState();
    private static final CapabilityTracker LIGHTING = new CapabilityTracker(2896);
    private static final CapabilityTracker[] LIGHT_ENABLE = (CapabilityTracker[])IntStream.range(0, 8).mapToObj(i -> new CapabilityTracker(16384 + i)).toArray(CapabilityTracker[]::new);
    private static final ColorMaterialState COLOR_MATERIAL = new ColorMaterialState();
    private static final BlendFuncState BLEND = new BlendFuncState();
    private static final DepthTestState DEPTH = new DepthTestState();
    private static final FogState FOG = new FogState();
    private static final CullFaceState CULL = new CullFaceState();
    private static final PolygonOffsetState POLY_OFFSET = new PolygonOffsetState();
    private static final LogicOpState COLOR_LOGIC = new LogicOpState();
    private static final TexGenState TEX_GEN = new TexGenState();
    private static final ClearState CLEAR = new ClearState();
    private static final StencilState STENCIL = new StencilState();
    private static final FloatBuffer colorBuffer = GlAllocationUtils.allocateFloatBuffer(4);
    private static int activeTexture;
    private static final Texture2DState[] TEXTURES;
    private static int modelShadeMode;
    private static final CapabilityTracker RESCALE_NORMAL;
    private static final ColorMask COLOR_MASK;
    private static final Color4 COLOR;
    private static FBOMode fboMode;
    private static class_5343 field_25251;

    @Deprecated
    public static void pushLightingAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib((int)8256);
    }

    @Deprecated
    public static void pushTextureAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushAttrib((int)270336);
    }

    @Deprecated
    public static void popAttributes() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopAttrib();
    }

    @Deprecated
    public static void disableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.ALPHA_TEST.capState.disable();
    }

    @Deprecated
    public static void enableAlphaTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.ALPHA_TEST.capState.enable();
    }

    @Deprecated
    public static void alphaFunc(int i, float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (i != GlStateManager.ALPHA_TEST.func || f != GlStateManager.ALPHA_TEST.ref) {
            GlStateManager.ALPHA_TEST.func = i;
            GlStateManager.ALPHA_TEST.ref = f;
            GL11.glAlphaFunc((int)i, (float)f);
        }
    }

    @Deprecated
    public static void enableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.enable();
    }

    @Deprecated
    public static void disableLighting() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHTING.disable();
    }

    @Deprecated
    public static void enableLight(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHT_ENABLE[i].enable();
    }

    @Deprecated
    public static void enableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.capState.enable();
    }

    @Deprecated
    public static void disableColorMaterial() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_MATERIAL.capState.disable();
    }

    @Deprecated
    public static void colorMaterial(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.COLOR_MATERIAL.face || j != GlStateManager.COLOR_MATERIAL.mode) {
            GlStateManager.COLOR_MATERIAL.face = i;
            GlStateManager.COLOR_MATERIAL.mode = j;
            GL11.glColorMaterial((int)i, (int)j);
        }
    }

    @Deprecated
    public static void light(int i, int j, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightfv((int)i, (int)j, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void lightModel(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightModelfv((int)i, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void normal3f(float f, float g, float h) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormal3f((float)f, (float)g, (float)h);
    }

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.capState.disable();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.capState.enable();
    }

    public static void depthFunc(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (i != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = i;
            GL11.glDepthFunc((int)i);
        }
    }

    public static void depthMask(boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (bl != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = bl;
            GL11.glDepthMask((boolean)bl);
        }
    }

    public static void disableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.BLEND.capState.disable();
    }

    public static void enableBlend() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.BLEND.capState.enable();
    }

    public static void blendFunc(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.BLEND.srcFactorRGB || j != GlStateManager.BLEND.dstFactorRGB) {
            GlStateManager.BLEND.srcFactorRGB = i;
            GlStateManager.BLEND.dstFactorRGB = j;
            GL11.glBlendFunc((int)i, (int)j);
        }
    }

    public static void blendFuncSeparate(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.BLEND.srcFactorRGB || j != GlStateManager.BLEND.dstFactorRGB || k != GlStateManager.BLEND.srcFactorAlpha || l != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRGB = i;
            GlStateManager.BLEND.dstFactorRGB = j;
            GlStateManager.BLEND.srcFactorAlpha = k;
            GlStateManager.BLEND.dstFactorAlpha = l;
            GlStateManager.blendFuncSeparateUntracked(i, j, k, l);
        }
    }

    public static void blendColor(float f, float g, float h, float i) {
        GL14.glBlendColor((float)f, (float)g, (float)h, (float)i);
    }

    public static void blendEquation(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendEquation((int)i);
    }

    public static String initFramebufferSupport(GLCapabilities gLCapabilities) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        field_25251 = gLCapabilities.OpenGL30 ? class_5343.BASE : (gLCapabilities.GL_EXT_framebuffer_blit ? class_5343.EXT : class_5343.NONE);
        if (gLCapabilities.OpenGL30) {
            fboMode = FBOMode.BASE;
            FramebufferInfo.FRAME_BUFFER = 36160;
            FramebufferInfo.RENDER_BUFFER = 36161;
            FramebufferInfo.COLOR_ATTACHMENT = 36064;
            FramebufferInfo.DEPTH_ATTACHMENT = 36096;
            FramebufferInfo.FRAME_BUFFER_COMPLETE = 36053;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "OpenGL 3.0";
        }
        if (gLCapabilities.GL_ARB_framebuffer_object) {
            fboMode = FBOMode.ARB;
            FramebufferInfo.FRAME_BUFFER = 36160;
            FramebufferInfo.RENDER_BUFFER = 36161;
            FramebufferInfo.COLOR_ATTACHMENT = 36064;
            FramebufferInfo.DEPTH_ATTACHMENT = 36096;
            FramebufferInfo.FRAME_BUFFER_COMPLETE = 36053;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "ARB_framebuffer_object extension";
        }
        if (gLCapabilities.GL_EXT_framebuffer_object) {
            fboMode = FBOMode.EXT;
            FramebufferInfo.FRAME_BUFFER = 36160;
            FramebufferInfo.RENDER_BUFFER = 36161;
            FramebufferInfo.COLOR_ATTACHMENT = 36064;
            FramebufferInfo.DEPTH_ATTACHMENT = 36096;
            FramebufferInfo.FRAME_BUFFER_COMPLETE = 36053;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT = 36055;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_ATTACHMENT = 36054;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER = 36059;
            FramebufferInfo.FRAME_BUFFER_INCOMPLETE_READ_BUFFER = 36060;
            return "EXT_framebuffer_object extension";
        }
        throw new IllegalStateException("Could not initialize framebuffer support.");
    }

    public static int getProgram(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgrami((int)i, (int)j);
    }

    public static void attachShader(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glAttachShader((int)i, (int)j);
    }

    public static void deleteShader(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteShader((int)i);
    }

    public static int createShader(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateShader((int)i);
    }

    public static void shaderSource(int i, CharSequence charSequence) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glShaderSource((int)i, (CharSequence)charSequence);
    }

    public static void compileShader(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glCompileShader((int)i);
    }

    public static int getShader(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderi((int)i, (int)j);
    }

    public static void useProgram(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUseProgram((int)i);
    }

    public static int createProgram() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateProgram();
    }

    public static void deleteProgram(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteProgram((int)i);
    }

    public static void linkProgram(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glLinkProgram((int)i);
    }

    public static int getUniformLocation(int i, CharSequence charSequence) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetUniformLocation((int)i, (CharSequence)charSequence);
    }

    public static void uniform1(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1iv((int)i, (IntBuffer)intBuffer);
    }

    public static void uniform1(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1i((int)i, (int)j);
    }

    public static void uniform1(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1fv((int)i, (FloatBuffer)floatBuffer);
    }

    public static void uniform2(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2iv((int)i, (IntBuffer)intBuffer);
    }

    public static void uniform2(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2fv((int)i, (FloatBuffer)floatBuffer);
    }

    public static void uniform3(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3iv((int)i, (IntBuffer)intBuffer);
    }

    public static void uniform3(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3fv((int)i, (FloatBuffer)floatBuffer);
    }

    public static void uniform4(int i, IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4iv((int)i, (IntBuffer)intBuffer);
    }

    public static void uniform4(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4fv((int)i, (FloatBuffer)floatBuffer);
    }

    public static void uniformMatrix2(int i, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix2fv((int)i, (boolean)bl, (FloatBuffer)floatBuffer);
    }

    public static void uniformMatrix3(int i, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix3fv((int)i, (boolean)bl, (FloatBuffer)floatBuffer);
    }

    public static void uniformMatrix4(int i, boolean bl, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix4fv((int)i, (boolean)bl, (FloatBuffer)floatBuffer);
    }

    public static int getAttribLocation(int i, CharSequence charSequence) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetAttribLocation((int)i, (CharSequence)charSequence);
    }

    public static int genBuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL15.glGenBuffers();
    }

    public static void bindBuffers(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBindBuffer((int)i, (int)j);
    }

    public static void bufferData(int i, ByteBuffer byteBuffer, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBufferData((int)i, (ByteBuffer)byteBuffer, (int)j);
    }

    public static void deleteBuffers(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL15.glDeleteBuffers((int)i);
    }

    public static void copyTexSubImage2d(int i, int j, int k, int l, int m, int n, int o, int p) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glCopyTexSubImage2D((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p);
    }

    public static void bindFramebuffer(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glBindFramebuffer((int)i, (int)j);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindFramebuffer((int)i, (int)j);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindFramebufferEXT((int)i, (int)j);
            }
        }
    }

    public static int getFramebufferDepthAttachment() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                if (GL30.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36048) != 5890) break;
                return GL30.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36049);
            }
            case ARB: {
                if (ARBFramebufferObject.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36048) != 5890) break;
                return ARBFramebufferObject.glGetFramebufferAttachmentParameteri((int)36160, (int)36096, (int)36049);
            }
            case EXT: {
                if (EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT((int)36160, (int)36096, (int)36048) != 5890) break;
                return EXTFramebufferObject.glGetFramebufferAttachmentParameteriEXT((int)36160, (int)36096, (int)36049);
            }
        }
        return 0;
    }

    public static void blitFramebuffer(int i, int j, int k, int l, int m, int n, int o, int p, int q, int r) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (field_25251) {
            case BASE: {
                GL30.glBlitFramebuffer((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p, (int)q, (int)r);
                break;
            }
            case EXT: {
                EXTFramebufferBlit.glBlitFramebufferEXT((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p, (int)q, (int)r);
                break;
            }
        }
    }

    public static void deleteFramebuffers(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glDeleteFramebuffers((int)i);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteFramebuffers((int)i);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)i);
            }
        }
    }

    public static int genFramebuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glGenFramebuffers();
            }
            case ARB: {
                return ARBFramebufferObject.glGenFramebuffers();
            }
            case EXT: {
                return EXTFramebufferObject.glGenFramebuffersEXT();
            }
        }
        return -1;
    }

    public static int checkFramebufferStatus(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glCheckFramebufferStatus((int)i);
            }
            case ARB: {
                return ARBFramebufferObject.glCheckFramebufferStatus((int)i);
            }
            case EXT: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT((int)i);
            }
        }
        return -1;
    }

    public static void framebufferTexture2D(int i, int j, int k, int l, int m) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glFramebufferTexture2D((int)i, (int)j, (int)k, (int)l, (int)m);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferTexture2D((int)i, (int)j, (int)k, (int)l, (int)m);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)i, (int)j, (int)k, (int)l, (int)m);
            }
        }
    }

    @Deprecated
    public static int getActiveBoundTexture() {
        return GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture;
    }

    public static void activeTextureUntracked(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glActiveTexture((int)i);
    }

    @Deprecated
    public static void clientActiveTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glClientActiveTexture((int)i);
    }

    @Deprecated
    public static void multiTexCoords2f(int i, float f, float g) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glMultiTexCoord2f((int)i, (float)f, (float)g);
    }

    public static void blendFuncSeparateUntracked(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendFuncSeparate((int)i, (int)j, (int)k, (int)l);
    }

    public static String getShaderInfoLog(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderInfoLog((int)i, (int)j);
    }

    public static String getProgramInfoLog(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgramInfoLog((int)i, (int)j);
    }

    public static void setupOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.texEnv(8960, 8704, 34160);
        GlStateManager.combineColor(7681, 34168);
    }

    public static void teardownOutline() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.texEnv(8960, 8704, 8448);
        GlStateManager.combineColor(8448, 5890, 34168, 34166);
    }

    public static void setupOverlayColor(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.activeTexture(33985);
        GlStateManager.enableTexture();
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = 1.0f / (float)(j - 1);
        GlStateManager.scalef(f, f, f);
        GlStateManager.matrixMode(5888);
        GlStateManager.bindTexture(i);
        GlStateManager.texParameter(3553, 10241, 9728);
        GlStateManager.texParameter(3553, 10240, 9728);
        GlStateManager.texParameter(3553, 10242, 10496);
        GlStateManager.texParameter(3553, 10243, 10496);
        GlStateManager.texEnv(8960, 8704, 34160);
        GlStateManager.combineColor(34165, 34168, 5890, 5890);
        GlStateManager.combineAlpha(7681, 34168);
        GlStateManager.activeTexture(33984);
    }

    public static void teardownOverlayColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.activeTexture(33985);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(33984);
    }

    private static void combineColor(int i, int j) {
        GlStateManager.texEnv(8960, 34161, i);
        GlStateManager.texEnv(8960, 34176, j);
        GlStateManager.texEnv(8960, 34192, 768);
    }

    private static void combineColor(int i, int j, int k, int l) {
        GlStateManager.texEnv(8960, 34161, i);
        GlStateManager.texEnv(8960, 34176, j);
        GlStateManager.texEnv(8960, 34192, 768);
        GlStateManager.texEnv(8960, 34177, k);
        GlStateManager.texEnv(8960, 34193, 768);
        GlStateManager.texEnv(8960, 34178, l);
        GlStateManager.texEnv(8960, 34194, 770);
    }

    private static void combineAlpha(int i, int j) {
        GlStateManager.texEnv(8960, 34162, i);
        GlStateManager.texEnv(8960, 34184, j);
        GlStateManager.texEnv(8960, 34200, 770);
    }

    public static void setupLevelDiffuseLighting(Vector3f arg, Vector3f arg2, Matrix4f arg3) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.enableLight(0);
        GlStateManager.enableLight(1);
        Vector4f lv = new Vector4f(arg);
        lv.transform(arg3);
        GlStateManager.light(16384, 4611, GlStateManager.getBuffer(lv.getX(), lv.getY(), lv.getZ(), 0.0f));
        float f = 0.6f;
        GlStateManager.light(16384, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16384, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16384, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        Vector4f lv2 = new Vector4f(arg2);
        lv2.transform(arg3);
        GlStateManager.light(16385, 4611, GlStateManager.getBuffer(lv2.getX(), lv2.getY(), lv2.getZ(), 0.0f));
        GlStateManager.light(16385, 4609, GlStateManager.getBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        GlStateManager.light(16385, 4608, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.light(16385, 4610, GlStateManager.getBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        GlStateManager.shadeModel(7424);
        float g = 0.4f;
        GlStateManager.lightModel(2899, GlStateManager.getBuffer(0.4f, 0.4f, 0.4f, 1.0f));
        GlStateManager.popMatrix();
    }

    public static void setupGuiFlatDiffuseLighting(Vector3f arg, Vector3f arg2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f lv = new Matrix4f();
        lv.loadIdentity();
        lv.multiply(Matrix4f.scale(1.0f, -1.0f, 1.0f));
        lv.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-22.5f));
        lv.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(135.0f));
        GlStateManager.setupLevelDiffuseLighting(arg, arg2, lv);
    }

    public static void setupGui3dDiffuseLighting(Vector3f arg, Vector3f arg2) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        Matrix4f lv = new Matrix4f();
        lv.loadIdentity();
        lv.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(62.0f));
        lv.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(185.5f));
        lv.multiply(Matrix4f.scale(1.0f, -1.0f, 1.0f));
        lv.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-22.5f));
        lv.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(135.0f));
        GlStateManager.setupLevelDiffuseLighting(arg, arg2, lv);
    }

    private static FloatBuffer getBuffer(float f, float g, float h, float i) {
        colorBuffer.clear();
        colorBuffer.put(f).put(g).put(h).put(i);
        colorBuffer.flip();
        return colorBuffer;
    }

    public static void setupEndPortalTexGen() {
        GlStateManager.texGenMode(TexCoord.S, 9216);
        GlStateManager.texGenMode(TexCoord.T, 9216);
        GlStateManager.texGenMode(TexCoord.R, 9216);
        GlStateManager.texGenParam(TexCoord.S, 9474, GlStateManager.getBuffer(1.0f, 0.0f, 0.0f, 0.0f));
        GlStateManager.texGenParam(TexCoord.T, 9474, GlStateManager.getBuffer(0.0f, 1.0f, 0.0f, 0.0f));
        GlStateManager.texGenParam(TexCoord.R, 9474, GlStateManager.getBuffer(0.0f, 0.0f, 1.0f, 0.0f));
        GlStateManager.enableTexGen(TexCoord.S);
        GlStateManager.enableTexGen(TexCoord.T);
        GlStateManager.enableTexGen(TexCoord.R);
    }

    public static void clearTexGen() {
        GlStateManager.disableTexGen(TexCoord.S);
        GlStateManager.disableTexGen(TexCoord.T);
        GlStateManager.disableTexGen(TexCoord.R);
    }

    public static void mulTextureByProjModelView() {
        GlStateManager.getFloat(2983, MATRIX_BUFFER);
        GlStateManager.multMatrix(MATRIX_BUFFER);
        GlStateManager.getFloat(2982, MATRIX_BUFFER);
        GlStateManager.multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void enableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.FOG.capState.enable();
    }

    @Deprecated
    public static void disableFog() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.FOG.capState.disable();
    }

    @Deprecated
    public static void fogMode(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.FOG.mode) {
            GlStateManager.FOG.mode = i;
            GlStateManager.fogi(2917, i);
        }
    }

    @Deprecated
    public static void fogDensity(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.FOG.density) {
            GlStateManager.FOG.density = f;
            GL11.glFogf((int)2914, (float)f);
        }
    }

    @Deprecated
    public static void fogStart(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.FOG.start) {
            GlStateManager.FOG.start = f;
            GL11.glFogf((int)2915, (float)f);
        }
    }

    @Deprecated
    public static void fogEnd(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.FOG.end) {
            GlStateManager.FOG.end = f;
            GL11.glFogf((int)2916, (float)f);
        }
    }

    @Deprecated
    public static void fog(int i, float[] fs) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogfv((int)i, (float[])fs);
    }

    @Deprecated
    public static void fogi(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogi((int)i, (int)j);
    }

    public static void enableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.capState.enable();
    }

    public static void disableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.capState.disable();
    }

    public static void polygonMode(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPolygonMode((int)i, (int)j);
    }

    public static void enablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capFill.enable();
    }

    public static void disablePolygonOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capFill.disable();
    }

    public static void enableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capLine.enable();
    }

    public static void disableLineOffset() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.POLY_OFFSET.capLine.disable();
    }

    public static void polygonOffset(float f, float g) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.POLY_OFFSET.factor || g != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = f;
            GlStateManager.POLY_OFFSET.units = g;
            GL11.glPolygonOffset((float)f, (float)g);
        }
    }

    public static void enableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.capState.enable();
    }

    public static void disableColorLogicOp() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR_LOGIC.capState.disable();
    }

    public static void logicOp(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = i;
            GL11.glLogicOp((int)i);
        }
    }

    @Deprecated
    public static void enableTexGen(TexCoord arg) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getGenCoordState((TexCoord)arg).capState.enable();
    }

    @Deprecated
    public static void disableTexGen(TexCoord arg) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getGenCoordState((TexCoord)arg).capState.disable();
    }

    @Deprecated
    public static void texGenMode(TexCoord arg, int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        TexGenCoordState lv = GlStateManager.getGenCoordState(arg);
        if (i != lv.mode) {
            lv.mode = i;
            GL11.glTexGeni((int)lv.coord, (int)9472, (int)i);
        }
    }

    @Deprecated
    public static void texGenParam(TexCoord arg, int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexGenfv((int)GlStateManager.getGenCoordState((TexCoord)arg).coord, (int)i, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    private static TexGenCoordState getGenCoordState(TexCoord arg) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        switch (arg) {
            case S: {
                return GlStateManager.TEX_GEN.s;
            }
            case T: {
                return GlStateManager.TEX_GEN.t;
            }
            case R: {
                return GlStateManager.TEX_GEN.r;
            }
            case Q: {
                return GlStateManager.TEX_GEN.q;
            }
        }
        return GlStateManager.TEX_GEN.s;
    }

    public static void activeTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (activeTexture != i - 33984) {
            activeTexture = i - 33984;
            GlStateManager.activeTextureUntracked(i);
        }
    }

    public static void enableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.enable();
    }

    public static void disableTexture() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.TEXTURES[GlStateManager.activeTexture].capState.disable();
    }

    @Deprecated
    public static void texEnv(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexEnvi((int)i, (int)j, (int)k);
    }

    public static void texParameter(int i, int j, float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameterf((int)i, (int)j, (float)f);
    }

    public static void texParameter(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameteri((int)i, (int)j, (int)k);
    }

    public static int getTexLevelParameter(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GL11.glGetTexLevelParameteri((int)i, (int)j, (int)k);
    }

    public static int genTextures() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGenTextures();
    }

    public static void deleteTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glDeleteTextures((int)i);
        for (Texture2DState lv : TEXTURES) {
            if (lv.boundTexture != i) continue;
            lv.boundTexture = -1;
        }
    }

    public static void bindTexture(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (i != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = i;
            GL11.glBindTexture((int)3553, (int)i);
        }
    }

    public static void texImage2D(int i, int j, int k, int l, int m, int n, int o, int p, @Nullable IntBuffer intBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexImage2D((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p, (IntBuffer)intBuffer);
    }

    public static void texSubImage2D(int i, int j, int k, int l, int m, int n, int o, int p, long q) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexSubImage2D((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p, (long)q);
    }

    public static void getTexImage(int i, int j, int k, int l, long m) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetTexImage((int)i, (int)j, (int)k, (int)l, (long)m);
    }

    @Deprecated
    public static void shadeModel(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (i != modelShadeMode) {
            modelShadeMode = i;
            GL11.glShadeModel((int)i);
        }
    }

    @Deprecated
    public static void enableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.enable();
    }

    @Deprecated
    public static void disableRescaleNormal() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        RESCALE_NORMAL.disable();
    }

    public static void viewport(int i, int j, int k, int l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        Viewport.INSTANCE.x = i;
        Viewport.INSTANCE.y = j;
        Viewport.INSTANCE.width = k;
        Viewport.INSTANCE.height = l;
        GL11.glViewport((int)i, (int)j, (int)k, (int)l);
    }

    public static void colorMask(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (bl != GlStateManager.COLOR_MASK.red || bl2 != GlStateManager.COLOR_MASK.green || bl3 != GlStateManager.COLOR_MASK.blue || bl4 != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = bl;
            GlStateManager.COLOR_MASK.green = bl2;
            GlStateManager.COLOR_MASK.blue = bl3;
            GlStateManager.COLOR_MASK.alpha = bl4;
            GL11.glColorMask((boolean)bl, (boolean)bl2, (boolean)bl3, (boolean)bl4);
        }
    }

    public static void stencilFunc(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.STENCIL.subState.func || i != GlStateManager.STENCIL.subState.ref || i != GlStateManager.STENCIL.subState.mask) {
            GlStateManager.STENCIL.subState.func = i;
            GlStateManager.STENCIL.subState.ref = j;
            GlStateManager.STENCIL.subState.mask = k;
            GL11.glStencilFunc((int)i, (int)j, (int)k);
        }
    }

    public static void stencilMask(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.STENCIL.mask) {
            GlStateManager.STENCIL.mask = i;
            GL11.glStencilMask((int)i);
        }
    }

    public static void stencilOp(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.STENCIL.sfail || j != GlStateManager.STENCIL.dpfail || k != GlStateManager.STENCIL.dppass) {
            GlStateManager.STENCIL.sfail = i;
            GlStateManager.STENCIL.dpfail = j;
            GlStateManager.STENCIL.dppass = k;
            GL11.glStencilOp((int)i, (int)j, (int)k);
        }
    }

    public static void clearDepth(double d) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (d != GlStateManager.CLEAR.clearDepth) {
            GlStateManager.CLEAR.clearDepth = d;
            GL11.glClearDepth((double)d);
        }
    }

    public static void clearColor(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (f != GlStateManager.CLEAR.clearColor.red || g != GlStateManager.CLEAR.clearColor.green || h != GlStateManager.CLEAR.clearColor.blue || i != GlStateManager.CLEAR.clearColor.alpha) {
            GlStateManager.CLEAR.clearColor.red = f;
            GlStateManager.CLEAR.clearColor.green = g;
            GlStateManager.CLEAR.clearColor.blue = h;
            GlStateManager.CLEAR.clearColor.alpha = i;
            GL11.glClearColor((float)f, (float)g, (float)h, (float)i);
        }
    }

    public static void clearStencil(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (i != GlStateManager.CLEAR.clearStencil) {
            GlStateManager.CLEAR.clearStencil = i;
            GL11.glClearStencil((int)i);
        }
    }

    public static void clear(int i, boolean bl) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClear((int)i);
        if (bl) {
            GlStateManager.getError();
        }
    }

    @Deprecated
    public static void matrixMode(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glMatrixMode((int)i);
    }

    @Deprecated
    public static void loadIdentity() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glLoadIdentity();
    }

    @Deprecated
    public static void pushMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPushMatrix();
    }

    @Deprecated
    public static void popMatrix() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPopMatrix();
    }

    @Deprecated
    public static void getFloat(int i, FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetFloatv((int)i, (FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void ortho(double d, double e, double f, double g, double h, double i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glOrtho((double)d, (double)e, (double)f, (double)g, (double)h, (double)i);
    }

    @Deprecated
    public static void rotatef(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glRotatef((float)f, (float)g, (float)h, (float)i);
    }

    @Deprecated
    public static void scalef(float f, float g, float h) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScalef((float)f, (float)g, (float)h);
    }

    @Deprecated
    public static void scaled(double d, double e, double f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScaled((double)d, (double)e, (double)f);
    }

    @Deprecated
    public static void translatef(float f, float g, float h) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslatef((float)f, (float)g, (float)h);
    }

    @Deprecated
    public static void translated(double d, double e, double f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslated((double)d, (double)e, (double)f);
    }

    @Deprecated
    public static void multMatrix(FloatBuffer floatBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glMultMatrixf((FloatBuffer)floatBuffer);
    }

    @Deprecated
    public static void multMatrix(Matrix4f arg) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        arg.writeToBuffer(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        GlStateManager.multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void color4f(float f, float g, float h, float i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (f != GlStateManager.COLOR.red || g != GlStateManager.COLOR.green || h != GlStateManager.COLOR.blue || i != GlStateManager.COLOR.alpha) {
            GlStateManager.COLOR.red = f;
            GlStateManager.COLOR.green = g;
            GlStateManager.COLOR.blue = h;
            GlStateManager.COLOR.alpha = i;
            GL11.glColor4f((float)f, (float)g, (float)h, (float)i);
        }
    }

    @Deprecated
    public static void clearCurrentColor() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.COLOR.red = -1.0f;
        GlStateManager.COLOR.green = -1.0f;
        GlStateManager.COLOR.blue = -1.0f;
        GlStateManager.COLOR.alpha = -1.0f;
    }

    @Deprecated
    public static void normalPointer(int i, int j, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormalPointer((int)i, (int)j, (long)l);
    }

    @Deprecated
    public static void texCoordPointer(int i, int j, int k, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexCoordPointer((int)i, (int)j, (int)k, (long)l);
    }

    @Deprecated
    public static void vertexPointer(int i, int j, int k, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glVertexPointer((int)i, (int)j, (int)k, (long)l);
    }

    @Deprecated
    public static void colorPointer(int i, int j, int k, long l) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glColorPointer((int)i, (int)j, (int)k, (long)l);
    }

    public static void vertexAttribPointer(int i, int j, int k, boolean bl, int l, long m) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glVertexAttribPointer((int)i, (int)j, (int)k, (boolean)bl, (int)l, (long)m);
    }

    @Deprecated
    public static void enableClientState(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glEnableClientState((int)i);
    }

    @Deprecated
    public static void disableClientState(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDisableClientState((int)i);
    }

    public static void enableVertexAttribArray(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray((int)i);
    }

    public static void method_22607(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray((int)i);
    }

    public static void drawArrays(int i, int j, int k) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDrawArrays((int)i, (int)j, (int)k);
    }

    public static void lineWidth(float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLineWidth((float)f);
    }

    public static void pixelStore(int i, int j) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glPixelStorei((int)i, (int)j);
    }

    public static void pixelTransfer(int i, float f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelTransferf((int)i, (float)f);
    }

    public static void readPixels(int i, int j, int k, int l, int m, int n, ByteBuffer byteBuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glReadPixels((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (ByteBuffer)byteBuffer);
    }

    public static int getError() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetError();
    }

    public static String getString(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetString((int)i);
    }

    public static int getInteger(int i) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGetInteger((int)i);
    }

    public static boolean supportsGl30() {
        return field_25251 != class_5343.NONE;
    }

    static {
        TEXTURES = (Texture2DState[])IntStream.range(0, 12).mapToObj(i -> new Texture2DState()).toArray(Texture2DState[]::new);
        modelShadeMode = 7425;
        RESCALE_NORMAL = new CapabilityTracker(32826);
        COLOR_MASK = new ColorMask();
        COLOR = new Color4();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum DstFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_COLOR(768),
        ZERO(0);

        public final int field_22528;

        private DstFactor(int j) {
            this.field_22528 = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum SrcFactor {
        CONSTANT_ALPHA(32771),
        CONSTANT_COLOR(32769),
        DST_ALPHA(772),
        DST_COLOR(774),
        ONE(1),
        ONE_MINUS_CONSTANT_ALPHA(32772),
        ONE_MINUS_CONSTANT_COLOR(32770),
        ONE_MINUS_DST_ALPHA(773),
        ONE_MINUS_DST_COLOR(775),
        ONE_MINUS_SRC_ALPHA(771),
        ONE_MINUS_SRC_COLOR(769),
        SRC_ALPHA(770),
        SRC_ALPHA_SATURATE(776),
        SRC_COLOR(768),
        ZERO(0);

        public final int field_22545;

        private SrcFactor(int j) {
            this.field_22545 = j;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CapabilityTracker {
        private final int cap;
        private boolean state;

        public CapabilityTracker(int i) {
            this.cap = i;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean bl) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            if (bl != this.state) {
                this.state = bl;
                if (bl) {
                    GL11.glEnable((int)this.cap);
                } else {
                    GL11.glDisable((int)this.cap);
                }
            }
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class Color4 {
        public float red = 1.0f;
        public float green = 1.0f;
        public float blue = 1.0f;
        public float alpha = 1.0f;

        public Color4() {
            this(1.0f, 1.0f, 1.0f, 1.0f);
        }

        public Color4(float f, float g, float h, float i) {
            this.red = f;
            this.green = g;
            this.blue = h;
            this.alpha = i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ColorMask {
        public boolean red = true;
        public boolean green = true;
        public boolean blue = true;
        public boolean alpha = true;

        private ColorMask() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public static enum TexCoord {
        S,
        T,
        R,
        Q;

    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class TexGenCoordState {
        public final CapabilityTracker capState;
        public final int coord;
        public int mode = -1;

        public TexGenCoordState(int i, int j) {
            this.coord = i;
            this.capState = new CapabilityTracker(j);
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class TexGenState {
        public final TexGenCoordState s = new TexGenCoordState(8192, 3168);
        public final TexGenCoordState t = new TexGenCoordState(8193, 3169);
        public final TexGenCoordState r = new TexGenCoordState(8194, 3170);
        public final TexGenCoordState q = new TexGenCoordState(8195, 3171);

        private TexGenState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilState {
        public final StencilSubState subState = new StencilSubState();
        public int mask = -1;
        public int sfail = 7680;
        public int dpfail = 7680;
        public int dppass = 7680;

        private StencilState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class StencilSubState {
        public int func = 519;
        public int ref;
        public int mask = -1;

        private StencilSubState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class ClearState {
        public double clearDepth = 1.0;
        public final Color4 clearColor = new Color4(0.0f, 0.0f, 0.0f, 0.0f);
        public int clearStencil;

        private ClearState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class LogicOpState {
        public final CapabilityTracker capState = new CapabilityTracker(3058);
        public int op = 5379;

        private LogicOpState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class PolygonOffsetState {
        public final CapabilityTracker capFill = new CapabilityTracker(32823);
        public final CapabilityTracker capLine = new CapabilityTracker(10754);
        public float factor;
        public float units;

        private PolygonOffsetState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CullFaceState {
        public final CapabilityTracker capState = new CapabilityTracker(2884);
        public int mode = 1029;

        private CullFaceState() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class FogState {
        public final CapabilityTracker capState = new CapabilityTracker(2912);
        public int mode = 2048;
        public float density = 1.0f;
        public float start;
        public float end = 1.0f;

        private FogState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class DepthTestState {
        public final CapabilityTracker capState = new CapabilityTracker(2929);
        public boolean mask = true;
        public int func = 513;

        private DepthTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BlendFuncState {
        public final CapabilityTracker capState = new CapabilityTracker(3042);
        public int srcFactorRGB = 1;
        public int dstFactorRGB = 0;
        public int srcFactorAlpha = 1;
        public int dstFactorAlpha = 0;

        private BlendFuncState() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class ColorMaterialState {
        public final CapabilityTracker capState = new CapabilityTracker(2903);
        public int face = 1032;
        public int mode = 5634;

        private ColorMaterialState() {
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    static class AlphaTestState {
        public final CapabilityTracker capState = new CapabilityTracker(3008);
        public int func = 519;
        public float ref = -1.0f;

        private AlphaTestState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Texture2DState {
        public final CapabilityTracker capState = new CapabilityTracker(3553);
        public int boundTexture;

        private Texture2DState() {
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum class_5343 {
        BASE,
        EXT,
        NONE;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum FBOMode {
        BASE,
        ARB,
        EXT;

    }

    @Environment(value=EnvType.CLIENT)
    public static enum Viewport {
        INSTANCE;

        protected int x;
        protected int y;
        protected int width;
        protected int height;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum LogicOp {
        AND(5377),
        AND_INVERTED(5380),
        AND_REVERSE(5378),
        CLEAR(5376),
        COPY(5379),
        COPY_INVERTED(5388),
        EQUIV(5385),
        INVERT(5386),
        NAND(5390),
        NOOP(5381),
        NOR(5384),
        OR(5383),
        OR_INVERTED(5389),
        OR_REVERSE(5387),
        SET(5391),
        XOR(5382);

        public final int value;

        private LogicOp(int j) {
            this.value = j;
        }
    }

    @Deprecated
    @Environment(value=EnvType.CLIENT)
    public static enum FogMode {
        LINEAR(9729),
        EXP(2048),
        EXP2(2049);

        public final int value;

        private FogMode(int j) {
            this.value = j;
        }
    }
}

