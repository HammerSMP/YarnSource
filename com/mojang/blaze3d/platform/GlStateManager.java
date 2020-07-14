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
    public static void alphaFunc(int func, float ref) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (func != GlStateManager.ALPHA_TEST.func || ref != GlStateManager.ALPHA_TEST.ref) {
            GlStateManager.ALPHA_TEST.func = func;
            GlStateManager.ALPHA_TEST.ref = ref;
            GL11.glAlphaFunc((int)func, (float)ref);
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
    public static void enableLight(int light) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        LIGHT_ENABLE[light].enable();
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
    public static void colorMaterial(int face, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (face != GlStateManager.COLOR_MATERIAL.face || mode != GlStateManager.COLOR_MATERIAL.mode) {
            GlStateManager.COLOR_MATERIAL.face = face;
            GlStateManager.COLOR_MATERIAL.mode = mode;
            GL11.glColorMaterial((int)face, (int)mode);
        }
    }

    @Deprecated
    public static void light(int light, int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightfv((int)light, (int)pname, (FloatBuffer)params);
    }

    @Deprecated
    public static void lightModel(int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLightModelfv((int)pname, (FloatBuffer)params);
    }

    @Deprecated
    public static void normal3f(float nx, float ny, float nz) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormal3f((float)nx, (float)ny, (float)nz);
    }

    public static void disableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.capState.disable();
    }

    public static void enableDepthTest() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GlStateManager.DEPTH.capState.enable();
    }

    public static void depthFunc(int func) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (func != GlStateManager.DEPTH.func) {
            GlStateManager.DEPTH.func = func;
            GL11.glDepthFunc((int)func);
        }
    }

    public static void depthMask(boolean mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (mask != GlStateManager.DEPTH.mask) {
            GlStateManager.DEPTH.mask = mask;
            GL11.glDepthMask((boolean)mask);
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

    public static void blendFunc(int srcFactor, int dstFactor) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (srcFactor != GlStateManager.BLEND.srcFactorRGB || dstFactor != GlStateManager.BLEND.dstFactorRGB) {
            GlStateManager.BLEND.srcFactorRGB = srcFactor;
            GlStateManager.BLEND.dstFactorRGB = dstFactor;
            GL11.glBlendFunc((int)srcFactor, (int)dstFactor);
        }
    }

    public static void blendFuncSeparate(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (srcFactorRGB != GlStateManager.BLEND.srcFactorRGB || dstFactorRGB != GlStateManager.BLEND.dstFactorRGB || srcFactorAlpha != GlStateManager.BLEND.srcFactorAlpha || dstFactorAlpha != GlStateManager.BLEND.dstFactorAlpha) {
            GlStateManager.BLEND.srcFactorRGB = srcFactorRGB;
            GlStateManager.BLEND.dstFactorRGB = dstFactorRGB;
            GlStateManager.BLEND.srcFactorAlpha = srcFactorAlpha;
            GlStateManager.BLEND.dstFactorAlpha = dstFactorAlpha;
            GlStateManager.blendFuncSeparateUntracked(srcFactorRGB, dstFactorRGB, srcFactorAlpha, dstFactorAlpha);
        }
    }

    public static void blendColor(float red, float green, float blue, float alpha) {
        GL14.glBlendColor((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void blendEquation(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendEquation((int)mode);
    }

    public static String initFramebufferSupport(GLCapabilities capabilities) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        field_25251 = capabilities.OpenGL30 ? class_5343.field_25253 : (capabilities.GL_EXT_framebuffer_blit ? class_5343.field_25254 : class_5343.field_25255);
        if (capabilities.OpenGL30) {
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
        if (capabilities.GL_ARB_framebuffer_object) {
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
        if (capabilities.GL_EXT_framebuffer_object) {
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

    public static int getProgram(int program, int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgrami((int)program, (int)pname);
    }

    public static void attachShader(int program, int shader) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glAttachShader((int)program, (int)shader);
    }

    public static void deleteShader(int shader) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteShader((int)shader);
    }

    public static int createShader(int type) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateShader((int)type);
    }

    public static void shaderSource(int shader, CharSequence source) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glShaderSource((int)shader, (CharSequence)source);
    }

    public static void compileShader(int shader) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glCompileShader((int)shader);
    }

    public static int getShader(int shader, int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderi((int)shader, (int)pname);
    }

    public static void useProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUseProgram((int)program);
    }

    public static int createProgram() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glCreateProgram();
    }

    public static void deleteProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glDeleteProgram((int)program);
    }

    public static void linkProgram(int program) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glLinkProgram((int)program);
    }

    public static int getUniformLocation(int program, CharSequence name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetUniformLocation((int)program, (CharSequence)name);
    }

    public static void uniform1(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1iv((int)location, (IntBuffer)value);
    }

    public static void uniform1(int location, int value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1i((int)location, (int)value);
    }

    public static void uniform1(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform1fv((int)location, (FloatBuffer)value);
    }

    public static void uniform2(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2iv((int)location, (IntBuffer)value);
    }

    public static void uniform2(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform2fv((int)location, (FloatBuffer)value);
    }

    public static void uniform3(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3iv((int)location, (IntBuffer)value);
    }

    public static void uniform3(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform3fv((int)location, (FloatBuffer)value);
    }

    public static void uniform4(int location, IntBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4iv((int)location, (IntBuffer)value);
    }

    public static void uniform4(int location, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniform4fv((int)location, (FloatBuffer)value);
    }

    public static void uniformMatrix2(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix2fv((int)location, (boolean)transpose, (FloatBuffer)value);
    }

    public static void uniformMatrix3(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix3fv((int)location, (boolean)transpose, (FloatBuffer)value);
    }

    public static void uniformMatrix4(int location, boolean transpose, FloatBuffer value) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glUniformMatrix4fv((int)location, (boolean)transpose, (FloatBuffer)value);
    }

    public static int getAttribLocation(int program, CharSequence name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetAttribLocation((int)program, (CharSequence)name);
    }

    public static int genBuffers() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL15.glGenBuffers();
    }

    public static void bindBuffers(int target, int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBindBuffer((int)target, (int)buffer);
    }

    public static void bufferData(int target, ByteBuffer data, int usage) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL15.glBufferData((int)target, (ByteBuffer)data, (int)usage);
    }

    public static void deleteBuffers(int buffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL15.glDeleteBuffers((int)buffer);
    }

    public static void copyTexSubImage2d(int i, int j, int k, int l, int m, int n, int o, int p) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL20.glCopyTexSubImage2D((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p);
    }

    public static void bindFramebuffer(int target, int framebuffer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glBindFramebuffer((int)target, (int)framebuffer);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glBindFramebuffer((int)target, (int)framebuffer);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glBindFramebufferEXT((int)target, (int)framebuffer);
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
            case field_25253: {
                GL30.glBlitFramebuffer((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p, (int)q, (int)r);
                break;
            }
            case field_25254: {
                EXTFramebufferBlit.glBlitFramebufferEXT((int)i, (int)j, (int)k, (int)l, (int)m, (int)n, (int)o, (int)p, (int)q, (int)r);
                break;
            }
        }
    }

    public static void deleteFramebuffers(int framebuffers) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glDeleteFramebuffers((int)framebuffers);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glDeleteFramebuffers((int)framebuffers);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glDeleteFramebuffersEXT((int)framebuffers);
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

    public static int checkFramebufferStatus(int target) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                return GL30.glCheckFramebufferStatus((int)target);
            }
            case ARB: {
                return ARBFramebufferObject.glCheckFramebufferStatus((int)target);
            }
            case EXT: {
                return EXTFramebufferObject.glCheckFramebufferStatusEXT((int)target);
            }
        }
        return -1;
    }

    public static void framebufferTexture2D(int target, int attachment, int textureTarget, int texture, int level) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        switch (fboMode) {
            case BASE: {
                GL30.glFramebufferTexture2D((int)target, (int)attachment, (int)textureTarget, (int)texture, (int)level);
                break;
            }
            case ARB: {
                ARBFramebufferObject.glFramebufferTexture2D((int)target, (int)attachment, (int)textureTarget, (int)texture, (int)level);
                break;
            }
            case EXT: {
                EXTFramebufferObject.glFramebufferTexture2DEXT((int)target, (int)attachment, (int)textureTarget, (int)texture, (int)level);
            }
        }
    }

    @Deprecated
    public static int getActiveBoundTexture() {
        return GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture;
    }

    public static void activeTextureUntracked(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glActiveTexture((int)texture);
    }

    @Deprecated
    public static void clientActiveTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glClientActiveTexture((int)texture);
    }

    @Deprecated
    public static void multiTexCoords2f(int texture, float s, float t) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL13.glMultiTexCoord2f((int)texture, (float)s, (float)t);
    }

    public static void blendFuncSeparateUntracked(int srcFactorRGB, int dstFactorRGB, int srcFactorAlpha, int dstFactorAlpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL14.glBlendFuncSeparate((int)srcFactorRGB, (int)dstFactorRGB, (int)srcFactorAlpha, (int)dstFactorAlpha);
    }

    public static String getShaderInfoLog(int shader, int maxLength) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetShaderInfoLog((int)shader, (int)maxLength);
    }

    public static String getProgramInfoLog(int program, int maxLength) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL20.glGetProgramInfoLog((int)program, (int)maxLength);
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

    public static void setupOverlayColor(int texture, int size) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.activeTexture(33985);
        GlStateManager.enableTexture();
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = 1.0f / (float)(size - 1);
        GlStateManager.scalef(f, f, f);
        GlStateManager.matrixMode(5888);
        GlStateManager.bindTexture(texture);
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

    private static void combineColor(int combineColor, int source0Color) {
        GlStateManager.texEnv(8960, 34161, combineColor);
        GlStateManager.texEnv(8960, 34176, source0Color);
        GlStateManager.texEnv(8960, 34192, 768);
    }

    private static void combineColor(int combineColor, int source0Color, int source1Color, int source2Color) {
        GlStateManager.texEnv(8960, 34161, combineColor);
        GlStateManager.texEnv(8960, 34176, source0Color);
        GlStateManager.texEnv(8960, 34192, 768);
        GlStateManager.texEnv(8960, 34177, source1Color);
        GlStateManager.texEnv(8960, 34193, 768);
        GlStateManager.texEnv(8960, 34178, source2Color);
        GlStateManager.texEnv(8960, 34194, 770);
    }

    private static void combineAlpha(int combineAlpha, int source0Alpha) {
        GlStateManager.texEnv(8960, 34162, combineAlpha);
        GlStateManager.texEnv(8960, 34184, source0Alpha);
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

    private static FloatBuffer getBuffer(float a, float b, float c, float d) {
        colorBuffer.clear();
        colorBuffer.put(a).put(b).put(c).put(d);
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
    public static void fogMode(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (mode != GlStateManager.FOG.mode) {
            GlStateManager.FOG.mode = mode;
            GlStateManager.fogi(2917, mode);
        }
    }

    @Deprecated
    public static void fogDensity(float density) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (density != GlStateManager.FOG.density) {
            GlStateManager.FOG.density = density;
            GL11.glFogf((int)2914, (float)density);
        }
    }

    @Deprecated
    public static void fogStart(float start) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (start != GlStateManager.FOG.start) {
            GlStateManager.FOG.start = start;
            GL11.glFogf((int)2915, (float)start);
        }
    }

    @Deprecated
    public static void fogEnd(float end) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (end != GlStateManager.FOG.end) {
            GlStateManager.FOG.end = end;
            GL11.glFogf((int)2916, (float)end);
        }
    }

    @Deprecated
    public static void fog(int pname, float[] params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogfv((int)pname, (float[])params);
    }

    @Deprecated
    public static void fogi(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glFogi((int)pname, (int)param);
    }

    public static void enableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.capState.enable();
    }

    public static void disableCull() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.CULL.capState.disable();
    }

    public static void polygonMode(int face, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPolygonMode((int)face, (int)mode);
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

    public static void polygonOffset(float factor, float units) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (factor != GlStateManager.POLY_OFFSET.factor || units != GlStateManager.POLY_OFFSET.units) {
            GlStateManager.POLY_OFFSET.factor = factor;
            GlStateManager.POLY_OFFSET.units = units;
            GL11.glPolygonOffset((float)factor, (float)units);
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

    public static void logicOp(int op) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (op != GlStateManager.COLOR_LOGIC.op) {
            GlStateManager.COLOR_LOGIC.op = op;
            GL11.glLogicOp((int)op);
        }
    }

    @Deprecated
    public static void enableTexGen(TexCoord coord) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getGenCoordState((TexCoord)coord).capState.enable();
    }

    @Deprecated
    public static void disableTexGen(TexCoord coord) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GlStateManager.getGenCoordState((TexCoord)coord).capState.disable();
    }

    @Deprecated
    public static void texGenMode(TexCoord coord, int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        TexGenCoordState lv = GlStateManager.getGenCoordState(coord);
        if (mode != lv.mode) {
            lv.mode = mode;
            GL11.glTexGeni((int)lv.coord, (int)9472, (int)mode);
        }
    }

    @Deprecated
    public static void texGenParam(TexCoord coord, int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexGenfv((int)GlStateManager.getGenCoordState((TexCoord)coord).coord, (int)pname, (FloatBuffer)params);
    }

    @Deprecated
    private static TexGenCoordState getGenCoordState(TexCoord coord) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        switch (coord) {
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

    public static void activeTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (activeTexture != texture - 33984) {
            activeTexture = texture - 33984;
            GlStateManager.activeTextureUntracked(texture);
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
    public static void texEnv(int target, int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexEnvi((int)target, (int)pname, (int)param);
    }

    public static void texParameter(int target, int pname, float param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameterf((int)target, (int)pname, (float)param);
    }

    public static void texParameter(int target, int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexParameteri((int)target, (int)pname, (int)param);
    }

    public static int getTexLevelParameter(int target, int level, int pname) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        return GL11.glGetTexLevelParameteri((int)target, (int)level, (int)pname);
    }

    public static int genTextures() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGenTextures();
    }

    public static void method_30498(int[] is) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glGenTextures((int[])is);
    }

    public static void deleteTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glDeleteTextures((int)texture);
        for (Texture2DState lv : TEXTURES) {
            if (lv.boundTexture != texture) continue;
            lv.boundTexture = -1;
        }
    }

    public static void method_30499(int[] is) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        for (Texture2DState lv : TEXTURES) {
            for (int i : is) {
                if (lv.boundTexture != i) continue;
                lv.boundTexture = -1;
            }
        }
        GL11.glDeleteTextures((int[])is);
    }

    public static void bindTexture(int texture) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (texture != GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture) {
            GlStateManager.TEXTURES[GlStateManager.activeTexture].boundTexture = texture;
            GL11.glBindTexture((int)3553, (int)texture);
        }
    }

    public static void texImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, @Nullable IntBuffer pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexImage2D((int)target, (int)level, (int)internalFormat, (int)width, (int)height, (int)border, (int)format, (int)type, (IntBuffer)pixels);
    }

    public static void texSubImage2D(int target, int level, int offsetX, int offsetY, int width, int height, int format, int type, long pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glTexSubImage2D((int)target, (int)level, (int)offsetX, (int)offsetY, (int)width, (int)height, (int)format, (int)type, (long)pixels);
    }

    public static void getTexImage(int target, int level, int format, int type, long pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetTexImage((int)target, (int)level, (int)format, (int)type, (long)pixels);
    }

    @Deprecated
    public static void shadeModel(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        if (mode != modelShadeMode) {
            modelShadeMode = mode;
            GL11.glShadeModel((int)mode);
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

    public static void viewport(int x, int y, int width, int height) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        Viewport.INSTANCE.x = x;
        Viewport.INSTANCE.y = y;
        Viewport.INSTANCE.width = width;
        Viewport.INSTANCE.height = height;
        GL11.glViewport((int)x, (int)y, (int)width, (int)height);
    }

    public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (red != GlStateManager.COLOR_MASK.red || green != GlStateManager.COLOR_MASK.green || blue != GlStateManager.COLOR_MASK.blue || alpha != GlStateManager.COLOR_MASK.alpha) {
            GlStateManager.COLOR_MASK.red = red;
            GlStateManager.COLOR_MASK.green = green;
            GlStateManager.COLOR_MASK.blue = blue;
            GlStateManager.COLOR_MASK.alpha = alpha;
            GL11.glColorMask((boolean)red, (boolean)green, (boolean)blue, (boolean)alpha);
        }
    }

    public static void stencilFunc(int func, int ref, int mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (func != GlStateManager.STENCIL.subState.func || func != GlStateManager.STENCIL.subState.ref || func != GlStateManager.STENCIL.subState.mask) {
            GlStateManager.STENCIL.subState.func = func;
            GlStateManager.STENCIL.subState.ref = ref;
            GlStateManager.STENCIL.subState.mask = mask;
            GL11.glStencilFunc((int)func, (int)ref, (int)mask);
        }
    }

    public static void stencilMask(int mask) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (mask != GlStateManager.STENCIL.mask) {
            GlStateManager.STENCIL.mask = mask;
            GL11.glStencilMask((int)mask);
        }
    }

    public static void stencilOp(int sfail, int dpfail, int dppass) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (sfail != GlStateManager.STENCIL.sfail || dpfail != GlStateManager.STENCIL.dpfail || dppass != GlStateManager.STENCIL.dppass) {
            GlStateManager.STENCIL.sfail = sfail;
            GlStateManager.STENCIL.dpfail = dpfail;
            GlStateManager.STENCIL.dppass = dppass;
            GL11.glStencilOp((int)sfail, (int)dpfail, (int)dppass);
        }
    }

    public static void clearDepth(double depth) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearDepth((double)depth);
    }

    public static void clearColor(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClearColor((float)red, (float)green, (float)blue, (float)alpha);
    }

    public static void clearStencil(int stencil) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glClearStencil((int)stencil);
    }

    public static void clear(int mask, boolean getError) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glClear((int)mask);
        if (getError) {
            GlStateManager.getError();
        }
    }

    @Deprecated
    public static void matrixMode(int mode) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glMatrixMode((int)mode);
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
    public static void getFloat(int pname, FloatBuffer params) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glGetFloatv((int)pname, (FloatBuffer)params);
    }

    @Deprecated
    public static void ortho(double l, double r, double b, double t, double n, double f) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glOrtho((double)l, (double)r, (double)b, (double)t, (double)n, (double)f);
    }

    @Deprecated
    public static void rotatef(float angle, float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glRotatef((float)angle, (float)x, (float)y, (float)z);
    }

    @Deprecated
    public static void scalef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScalef((float)x, (float)y, (float)z);
    }

    @Deprecated
    public static void scaled(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glScaled((double)x, (double)y, (double)z);
    }

    @Deprecated
    public static void translatef(float x, float y, float z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslatef((float)x, (float)y, (float)z);
    }

    @Deprecated
    public static void translated(double x, double y, double z) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTranslated((double)x, (double)y, (double)z);
    }

    @Deprecated
    public static void multMatrix(FloatBuffer matrix) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glMultMatrixf((FloatBuffer)matrix);
    }

    @Deprecated
    public static void multMatrix(Matrix4f matrix) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        matrix.writeToBuffer(MATRIX_BUFFER);
        MATRIX_BUFFER.rewind();
        GlStateManager.multMatrix(MATRIX_BUFFER);
    }

    @Deprecated
    public static void color4f(float red, float green, float blue, float alpha) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        if (red != GlStateManager.COLOR.red || green != GlStateManager.COLOR.green || blue != GlStateManager.COLOR.blue || alpha != GlStateManager.COLOR.alpha) {
            GlStateManager.COLOR.red = red;
            GlStateManager.COLOR.green = green;
            GlStateManager.COLOR.blue = blue;
            GlStateManager.COLOR.alpha = alpha;
            GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
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
    public static void normalPointer(int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glNormalPointer((int)type, (int)stride, (long)pointer);
    }

    @Deprecated
    public static void texCoordPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glTexCoordPointer((int)size, (int)type, (int)stride, (long)pointer);
    }

    @Deprecated
    public static void vertexPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glVertexPointer((int)size, (int)type, (int)stride, (long)pointer);
    }

    @Deprecated
    public static void colorPointer(int size, int type, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glColorPointer((int)size, (int)type, (int)stride, (long)pointer);
    }

    public static void vertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glVertexAttribPointer((int)index, (int)size, (int)type, (boolean)normalized, (int)stride, (long)pointer);
    }

    @Deprecated
    public static void enableClientState(int cap) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glEnableClientState((int)cap);
    }

    @Deprecated
    public static void disableClientState(int cap) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDisableClientState((int)cap);
    }

    public static void enableVertexAttribArray(int index) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray((int)index);
    }

    public static void method_22607(int index) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL20.glEnableVertexAttribArray((int)index);
    }

    public static void drawArrays(int mode, int first, int count) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glDrawArrays((int)mode, (int)first, (int)count);
    }

    public static void lineWidth(float width) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glLineWidth((float)width);
    }

    public static void pixelStore(int pname, int param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        GL11.glPixelStorei((int)pname, (int)param);
    }

    public static void pixelTransfer(int pname, float param) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glPixelTransferf((int)pname, (float)param);
    }

    public static void readPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        GL11.glReadPixels((int)x, (int)y, (int)width, (int)height, (int)format, (int)type, (ByteBuffer)pixels);
    }

    public static int getError() {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetError();
    }

    public static String getString(int name) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThread);
        return GL11.glGetString((int)name);
    }

    public static int getInteger(int pname) {
        RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
        return GL11.glGetInteger((int)pname);
    }

    public static boolean supportsGl30() {
        return field_25251 != class_5343.field_25255;
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

        public CapabilityTracker(int cap) {
            this.cap = cap;
        }

        public void disable() {
            this.setState(false);
        }

        public void enable() {
            this.setState(true);
        }

        public void setState(boolean state) {
            RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
            if (state != this.state) {
                this.state = state;
                if (state) {
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

        public Color4(float red, float green, float blue, float alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
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

        public TexGenCoordState(int coord, int cap) {
            this.coord = coord;
            this.capState = new CapabilityTracker(cap);
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
        field_25253,
        field_25254,
        field_25255;

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

