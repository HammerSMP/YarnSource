/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.ARBDebugOutput
 *  org.lwjgl.opengl.GL
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GLCapabilities
 *  org.lwjgl.opengl.GLDebugMessageARBCallback
 *  org.lwjgl.opengl.GLDebugMessageARBCallbackI
 *  org.lwjgl.opengl.GLDebugMessageCallback
 *  org.lwjgl.opengl.GLDebugMessageCallbackI
 *  org.lwjgl.opengl.KHRDebug
 */
package net.minecraft.client.gl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.Untracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallbackI;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.opengl.KHRDebug;

@Environment(value=EnvType.CLIENT)
public class GlDebug {
    private static final Logger LOGGER = LogManager.getLogger();
    protected static final ByteBuffer byteBuffer = GlAllocationUtils.allocateByteBuffer(64);
    protected static final FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    protected static final IntBuffer intBuffer = byteBuffer.asIntBuffer();
    private static final Joiner NEWLINE_JOINER = Joiner.on((char)'\n');
    private static final Joiner SEMICOLON_JOINER = Joiner.on((String)"; ");
    private static final Map<Integer, String> CONSTANTS = Maps.newHashMap();
    private static final List<Integer> KHR_VERBOSITY_LEVELS = ImmutableList.of((Object)37190, (Object)37191, (Object)37192, (Object)33387);
    private static final List<Integer> ARB_VERBOSITY_LEVELS = ImmutableList.of((Object)37190, (Object)37191, (Object)37192);
    private static final Map<String, List<String>> field_4923;

    private static String unknown(int opcode) {
        return "Unknown (0x" + Integer.toHexString(opcode).toUpperCase() + ")";
    }

    private static String getSource(int opcode) {
        switch (opcode) {
            case 33350: {
                return "API";
            }
            case 33351: {
                return "WINDOW SYSTEM";
            }
            case 33352: {
                return "SHADER COMPILER";
            }
            case 33353: {
                return "THIRD PARTY";
            }
            case 33354: {
                return "APPLICATION";
            }
            case 33355: {
                return "OTHER";
            }
        }
        return GlDebug.unknown(opcode);
    }

    private static String getType(int opcode) {
        switch (opcode) {
            case 33356: {
                return "ERROR";
            }
            case 33357: {
                return "DEPRECATED BEHAVIOR";
            }
            case 33358: {
                return "UNDEFINED BEHAVIOR";
            }
            case 33359: {
                return "PORTABILITY";
            }
            case 33360: {
                return "PERFORMANCE";
            }
            case 33361: {
                return "OTHER";
            }
            case 33384: {
                return "MARKER";
            }
        }
        return GlDebug.unknown(opcode);
    }

    private static String getSeverity(int opcode) {
        switch (opcode) {
            case 37190: {
                return "HIGH";
            }
            case 37191: {
                return "MEDIUM";
            }
            case 37192: {
                return "LOW";
            }
            case 33387: {
                return "NOTIFICATION";
            }
        }
        return GlDebug.unknown(opcode);
    }

    private static void info(int source, int type, int id, int severity, int m, long n, long o) {
        LOGGER.info("OpenGL debug message, id={}, source={}, type={}, severity={}, message={}", (Object)id, (Object)GlDebug.getSource(source), (Object)GlDebug.getType(type), (Object)GlDebug.getSeverity(severity), (Object)GLDebugMessageCallback.getMessage((int)m, (long)n));
    }

    private static void registerConstant(int constant, String description) {
        CONSTANTS.merge(constant, description, (string, string2) -> string + "/" + string2);
    }

    public static void enableDebug(int verbosity, boolean sync) {
        RenderSystem.assertThread(RenderSystem::isInInitPhase);
        if (verbosity <= 0) {
            return;
        }
        GLCapabilities gLCapabilities = GL.getCapabilities();
        if (gLCapabilities.GL_KHR_debug) {
            GL11.glEnable((int)37600);
            if (sync) {
                GL11.glEnable((int)33346);
            }
            for (int j = 0; j < KHR_VERBOSITY_LEVELS.size(); ++j) {
                boolean bl2 = j < verbosity;
                KHRDebug.glDebugMessageControl((int)4352, (int)4352, (int)KHR_VERBOSITY_LEVELS.get(j), (int[])null, (boolean)bl2);
            }
            KHRDebug.glDebugMessageCallback((GLDebugMessageCallbackI)((GLDebugMessageCallbackI)GLX.make(GLDebugMessageCallback.create(GlDebug::info), Untracker::untrack)), (long)0L);
        } else if (gLCapabilities.GL_ARB_debug_output) {
            if (sync) {
                GL11.glEnable((int)33346);
            }
            for (int k = 0; k < ARB_VERBOSITY_LEVELS.size(); ++k) {
                boolean bl3 = k < verbosity;
                ARBDebugOutput.glDebugMessageControlARB((int)4352, (int)4352, (int)ARB_VERBOSITY_LEVELS.get(k), (int[])null, (boolean)bl3);
            }
            ARBDebugOutput.glDebugMessageCallbackARB((GLDebugMessageARBCallbackI)((GLDebugMessageARBCallbackI)GLX.make(GLDebugMessageARBCallback.create(GlDebug::info), Untracker::untrack)), (long)0L);
        }
    }

    static {
        GlDebug.registerConstant(256, "GL11.GL_ACCUM");
        GlDebug.registerConstant(257, "GL11.GL_LOAD");
        GlDebug.registerConstant(258, "GL11.GL_RETURN");
        GlDebug.registerConstant(259, "GL11.GL_MULT");
        GlDebug.registerConstant(260, "GL11.GL_ADD");
        GlDebug.registerConstant(512, "GL11.GL_NEVER");
        GlDebug.registerConstant(513, "GL11.GL_LESS");
        GlDebug.registerConstant(514, "GL11.GL_EQUAL");
        GlDebug.registerConstant(515, "GL11.GL_LEQUAL");
        GlDebug.registerConstant(516, "GL11.GL_GREATER");
        GlDebug.registerConstant(517, "GL11.GL_NOTEQUAL");
        GlDebug.registerConstant(518, "GL11.GL_GEQUAL");
        GlDebug.registerConstant(519, "GL11.GL_ALWAYS");
        GlDebug.registerConstant(0, "GL11.GL_POINTS");
        GlDebug.registerConstant(1, "GL11.GL_LINES");
        GlDebug.registerConstant(2, "GL11.GL_LINE_LOOP");
        GlDebug.registerConstant(3, "GL11.GL_LINE_STRIP");
        GlDebug.registerConstant(4, "GL11.GL_TRIANGLES");
        GlDebug.registerConstant(5, "GL11.GL_TRIANGLE_STRIP");
        GlDebug.registerConstant(6, "GL11.GL_TRIANGLE_FAN");
        GlDebug.registerConstant(7, "GL11.GL_QUADS");
        GlDebug.registerConstant(8, "GL11.GL_QUAD_STRIP");
        GlDebug.registerConstant(9, "GL11.GL_POLYGON");
        GlDebug.registerConstant(0, "GL11.GL_ZERO");
        GlDebug.registerConstant(1, "GL11.GL_ONE");
        GlDebug.registerConstant(768, "GL11.GL_SRC_COLOR");
        GlDebug.registerConstant(769, "GL11.GL_ONE_MINUS_SRC_COLOR");
        GlDebug.registerConstant(770, "GL11.GL_SRC_ALPHA");
        GlDebug.registerConstant(771, "GL11.GL_ONE_MINUS_SRC_ALPHA");
        GlDebug.registerConstant(772, "GL11.GL_DST_ALPHA");
        GlDebug.registerConstant(773, "GL11.GL_ONE_MINUS_DST_ALPHA");
        GlDebug.registerConstant(774, "GL11.GL_DST_COLOR");
        GlDebug.registerConstant(775, "GL11.GL_ONE_MINUS_DST_COLOR");
        GlDebug.registerConstant(776, "GL11.GL_SRC_ALPHA_SATURATE");
        GlDebug.registerConstant(32769, "GL14.GL_CONSTANT_COLOR");
        GlDebug.registerConstant(32770, "GL14.GL_ONE_MINUS_CONSTANT_COLOR");
        GlDebug.registerConstant(32771, "GL14.GL_CONSTANT_ALPHA");
        GlDebug.registerConstant(32772, "GL14.GL_ONE_MINUS_CONSTANT_ALPHA");
        GlDebug.registerConstant(1, "GL11.GL_TRUE");
        GlDebug.registerConstant(0, "GL11.GL_FALSE");
        GlDebug.registerConstant(12288, "GL11.GL_CLIP_PLANE0");
        GlDebug.registerConstant(12289, "GL11.GL_CLIP_PLANE1");
        GlDebug.registerConstant(12290, "GL11.GL_CLIP_PLANE2");
        GlDebug.registerConstant(12291, "GL11.GL_CLIP_PLANE3");
        GlDebug.registerConstant(12292, "GL11.GL_CLIP_PLANE4");
        GlDebug.registerConstant(12293, "GL11.GL_CLIP_PLANE5");
        GlDebug.registerConstant(5120, "GL11.GL_BYTE");
        GlDebug.registerConstant(5121, "GL11.GL_UNSIGNED_BYTE");
        GlDebug.registerConstant(5122, "GL11.GL_SHORT");
        GlDebug.registerConstant(5123, "GL11.GL_UNSIGNED_SHORT");
        GlDebug.registerConstant(5124, "GL11.GL_INT");
        GlDebug.registerConstant(5125, "GL11.GL_UNSIGNED_INT");
        GlDebug.registerConstant(5126, "GL11.GL_FLOAT");
        GlDebug.registerConstant(5127, "GL11.GL_2_BYTES");
        GlDebug.registerConstant(5128, "GL11.GL_3_BYTES");
        GlDebug.registerConstant(5129, "GL11.GL_4_BYTES");
        GlDebug.registerConstant(5130, "GL11.GL_DOUBLE");
        GlDebug.registerConstant(0, "GL11.GL_NONE");
        GlDebug.registerConstant(1024, "GL11.GL_FRONT_LEFT");
        GlDebug.registerConstant(1025, "GL11.GL_FRONT_RIGHT");
        GlDebug.registerConstant(1026, "GL11.GL_BACK_LEFT");
        GlDebug.registerConstant(1027, "GL11.GL_BACK_RIGHT");
        GlDebug.registerConstant(1028, "GL11.GL_FRONT");
        GlDebug.registerConstant(1029, "GL11.GL_BACK");
        GlDebug.registerConstant(1030, "GL11.GL_LEFT");
        GlDebug.registerConstant(1031, "GL11.GL_RIGHT");
        GlDebug.registerConstant(1032, "GL11.GL_FRONT_AND_BACK");
        GlDebug.registerConstant(1033, "GL11.GL_AUX0");
        GlDebug.registerConstant(1034, "GL11.GL_AUX1");
        GlDebug.registerConstant(1035, "GL11.GL_AUX2");
        GlDebug.registerConstant(1036, "GL11.GL_AUX3");
        GlDebug.registerConstant(0, "GL11.GL_NO_ERROR");
        GlDebug.registerConstant(1280, "GL11.GL_INVALID_ENUM");
        GlDebug.registerConstant(1281, "GL11.GL_INVALID_VALUE");
        GlDebug.registerConstant(1282, "GL11.GL_INVALID_OPERATION");
        GlDebug.registerConstant(1283, "GL11.GL_STACK_OVERFLOW");
        GlDebug.registerConstant(1284, "GL11.GL_STACK_UNDERFLOW");
        GlDebug.registerConstant(1285, "GL11.GL_OUT_OF_MEMORY");
        GlDebug.registerConstant(1536, "GL11.GL_2D");
        GlDebug.registerConstant(1537, "GL11.GL_3D");
        GlDebug.registerConstant(1538, "GL11.GL_3D_COLOR");
        GlDebug.registerConstant(1539, "GL11.GL_3D_COLOR_TEXTURE");
        GlDebug.registerConstant(1540, "GL11.GL_4D_COLOR_TEXTURE");
        GlDebug.registerConstant(1792, "GL11.GL_PASS_THROUGH_TOKEN");
        GlDebug.registerConstant(1793, "GL11.GL_POINT_TOKEN");
        GlDebug.registerConstant(1794, "GL11.GL_LINE_TOKEN");
        GlDebug.registerConstant(1795, "GL11.GL_POLYGON_TOKEN");
        GlDebug.registerConstant(1796, "GL11.GL_BITMAP_TOKEN");
        GlDebug.registerConstant(1797, "GL11.GL_DRAW_PIXEL_TOKEN");
        GlDebug.registerConstant(1798, "GL11.GL_COPY_PIXEL_TOKEN");
        GlDebug.registerConstant(1799, "GL11.GL_LINE_RESET_TOKEN");
        GlDebug.registerConstant(2048, "GL11.GL_EXP");
        GlDebug.registerConstant(2049, "GL11.GL_EXP2");
        GlDebug.registerConstant(2304, "GL11.GL_CW");
        GlDebug.registerConstant(2305, "GL11.GL_CCW");
        GlDebug.registerConstant(2560, "GL11.GL_COEFF");
        GlDebug.registerConstant(2561, "GL11.GL_ORDER");
        GlDebug.registerConstant(2562, "GL11.GL_DOMAIN");
        GlDebug.registerConstant(2816, "GL11.GL_CURRENT_COLOR");
        GlDebug.registerConstant(2817, "GL11.GL_CURRENT_INDEX");
        GlDebug.registerConstant(2818, "GL11.GL_CURRENT_NORMAL");
        GlDebug.registerConstant(2819, "GL11.GL_CURRENT_TEXTURE_COORDS");
        GlDebug.registerConstant(2820, "GL11.GL_CURRENT_RASTER_COLOR");
        GlDebug.registerConstant(2821, "GL11.GL_CURRENT_RASTER_INDEX");
        GlDebug.registerConstant(2822, "GL11.GL_CURRENT_RASTER_TEXTURE_COORDS");
        GlDebug.registerConstant(2823, "GL11.GL_CURRENT_RASTER_POSITION");
        GlDebug.registerConstant(2824, "GL11.GL_CURRENT_RASTER_POSITION_VALID");
        GlDebug.registerConstant(2825, "GL11.GL_CURRENT_RASTER_DISTANCE");
        GlDebug.registerConstant(2832, "GL11.GL_POINT_SMOOTH");
        GlDebug.registerConstant(2833, "GL11.GL_POINT_SIZE");
        GlDebug.registerConstant(2834, "GL11.GL_POINT_SIZE_RANGE");
        GlDebug.registerConstant(2835, "GL11.GL_POINT_SIZE_GRANULARITY");
        GlDebug.registerConstant(2848, "GL11.GL_LINE_SMOOTH");
        GlDebug.registerConstant(2849, "GL11.GL_LINE_WIDTH");
        GlDebug.registerConstant(2850, "GL11.GL_LINE_WIDTH_RANGE");
        GlDebug.registerConstant(2851, "GL11.GL_LINE_WIDTH_GRANULARITY");
        GlDebug.registerConstant(2852, "GL11.GL_LINE_STIPPLE");
        GlDebug.registerConstant(2853, "GL11.GL_LINE_STIPPLE_PATTERN");
        GlDebug.registerConstant(2854, "GL11.GL_LINE_STIPPLE_REPEAT");
        GlDebug.registerConstant(2864, "GL11.GL_LIST_MODE");
        GlDebug.registerConstant(2865, "GL11.GL_MAX_LIST_NESTING");
        GlDebug.registerConstant(2866, "GL11.GL_LIST_BASE");
        GlDebug.registerConstant(2867, "GL11.GL_LIST_INDEX");
        GlDebug.registerConstant(2880, "GL11.GL_POLYGON_MODE");
        GlDebug.registerConstant(2881, "GL11.GL_POLYGON_SMOOTH");
        GlDebug.registerConstant(2882, "GL11.GL_POLYGON_STIPPLE");
        GlDebug.registerConstant(2883, "GL11.GL_EDGE_FLAG");
        GlDebug.registerConstant(2884, "GL11.GL_CULL_FACE");
        GlDebug.registerConstant(2885, "GL11.GL_CULL_FACE_MODE");
        GlDebug.registerConstant(2886, "GL11.GL_FRONT_FACE");
        GlDebug.registerConstant(2896, "GL11.GL_LIGHTING");
        GlDebug.registerConstant(2897, "GL11.GL_LIGHT_MODEL_LOCAL_VIEWER");
        GlDebug.registerConstant(2898, "GL11.GL_LIGHT_MODEL_TWO_SIDE");
        GlDebug.registerConstant(2899, "GL11.GL_LIGHT_MODEL_AMBIENT");
        GlDebug.registerConstant(2900, "GL11.GL_SHADE_MODEL");
        GlDebug.registerConstant(2901, "GL11.GL_COLOR_MATERIAL_FACE");
        GlDebug.registerConstant(2902, "GL11.GL_COLOR_MATERIAL_PARAMETER");
        GlDebug.registerConstant(2903, "GL11.GL_COLOR_MATERIAL");
        GlDebug.registerConstant(2912, "GL11.GL_FOG");
        GlDebug.registerConstant(2913, "GL11.GL_FOG_INDEX");
        GlDebug.registerConstant(2914, "GL11.GL_FOG_DENSITY");
        GlDebug.registerConstant(2915, "GL11.GL_FOG_START");
        GlDebug.registerConstant(2916, "GL11.GL_FOG_END");
        GlDebug.registerConstant(2917, "GL11.GL_FOG_MODE");
        GlDebug.registerConstant(2918, "GL11.GL_FOG_COLOR");
        GlDebug.registerConstant(2928, "GL11.GL_DEPTH_RANGE");
        GlDebug.registerConstant(2929, "GL11.GL_DEPTH_TEST");
        GlDebug.registerConstant(2930, "GL11.GL_DEPTH_WRITEMASK");
        GlDebug.registerConstant(2931, "GL11.GL_DEPTH_CLEAR_VALUE");
        GlDebug.registerConstant(2932, "GL11.GL_DEPTH_FUNC");
        GlDebug.registerConstant(2944, "GL11.GL_ACCUM_CLEAR_VALUE");
        GlDebug.registerConstant(2960, "GL11.GL_STENCIL_TEST");
        GlDebug.registerConstant(2961, "GL11.GL_STENCIL_CLEAR_VALUE");
        GlDebug.registerConstant(2962, "GL11.GL_STENCIL_FUNC");
        GlDebug.registerConstant(2963, "GL11.GL_STENCIL_VALUE_MASK");
        GlDebug.registerConstant(2964, "GL11.GL_STENCIL_FAIL");
        GlDebug.registerConstant(2965, "GL11.GL_STENCIL_PASS_DEPTH_FAIL");
        GlDebug.registerConstant(2966, "GL11.GL_STENCIL_PASS_DEPTH_PASS");
        GlDebug.registerConstant(2967, "GL11.GL_STENCIL_REF");
        GlDebug.registerConstant(2968, "GL11.GL_STENCIL_WRITEMASK");
        GlDebug.registerConstant(2976, "GL11.GL_MATRIX_MODE");
        GlDebug.registerConstant(2977, "GL11.GL_NORMALIZE");
        GlDebug.registerConstant(2978, "GL11.GL_VIEWPORT");
        GlDebug.registerConstant(2979, "GL11.GL_MODELVIEW_STACK_DEPTH");
        GlDebug.registerConstant(2980, "GL11.GL_PROJECTION_STACK_DEPTH");
        GlDebug.registerConstant(2981, "GL11.GL_TEXTURE_STACK_DEPTH");
        GlDebug.registerConstant(2982, "GL11.GL_MODELVIEW_MATRIX");
        GlDebug.registerConstant(2983, "GL11.GL_PROJECTION_MATRIX");
        GlDebug.registerConstant(2984, "GL11.GL_TEXTURE_MATRIX");
        GlDebug.registerConstant(2992, "GL11.GL_ATTRIB_STACK_DEPTH");
        GlDebug.registerConstant(2993, "GL11.GL_CLIENT_ATTRIB_STACK_DEPTH");
        GlDebug.registerConstant(3008, "GL11.GL_ALPHA_TEST");
        GlDebug.registerConstant(3009, "GL11.GL_ALPHA_TEST_FUNC");
        GlDebug.registerConstant(3010, "GL11.GL_ALPHA_TEST_REF");
        GlDebug.registerConstant(3024, "GL11.GL_DITHER");
        GlDebug.registerConstant(3040, "GL11.GL_BLEND_DST");
        GlDebug.registerConstant(3041, "GL11.GL_BLEND_SRC");
        GlDebug.registerConstant(3042, "GL11.GL_BLEND");
        GlDebug.registerConstant(3056, "GL11.GL_LOGIC_OP_MODE");
        GlDebug.registerConstant(3057, "GL11.GL_INDEX_LOGIC_OP");
        GlDebug.registerConstant(3058, "GL11.GL_COLOR_LOGIC_OP");
        GlDebug.registerConstant(3072, "GL11.GL_AUX_BUFFERS");
        GlDebug.registerConstant(3073, "GL11.GL_DRAW_BUFFER");
        GlDebug.registerConstant(3074, "GL11.GL_READ_BUFFER");
        GlDebug.registerConstant(3088, "GL11.GL_SCISSOR_BOX");
        GlDebug.registerConstant(3089, "GL11.GL_SCISSOR_TEST");
        GlDebug.registerConstant(3104, "GL11.GL_INDEX_CLEAR_VALUE");
        GlDebug.registerConstant(3105, "GL11.GL_INDEX_WRITEMASK");
        GlDebug.registerConstant(3106, "GL11.GL_COLOR_CLEAR_VALUE");
        GlDebug.registerConstant(3107, "GL11.GL_COLOR_WRITEMASK");
        GlDebug.registerConstant(3120, "GL11.GL_INDEX_MODE");
        GlDebug.registerConstant(3121, "GL11.GL_RGBA_MODE");
        GlDebug.registerConstant(3122, "GL11.GL_DOUBLEBUFFER");
        GlDebug.registerConstant(3123, "GL11.GL_STEREO");
        GlDebug.registerConstant(3136, "GL11.GL_RENDER_MODE");
        GlDebug.registerConstant(3152, "GL11.GL_PERSPECTIVE_CORRECTION_HINT");
        GlDebug.registerConstant(3153, "GL11.GL_POINT_SMOOTH_HINT");
        GlDebug.registerConstant(3154, "GL11.GL_LINE_SMOOTH_HINT");
        GlDebug.registerConstant(3155, "GL11.GL_POLYGON_SMOOTH_HINT");
        GlDebug.registerConstant(3156, "GL11.GL_FOG_HINT");
        GlDebug.registerConstant(3168, "GL11.GL_TEXTURE_GEN_S");
        GlDebug.registerConstant(3169, "GL11.GL_TEXTURE_GEN_T");
        GlDebug.registerConstant(3170, "GL11.GL_TEXTURE_GEN_R");
        GlDebug.registerConstant(3171, "GL11.GL_TEXTURE_GEN_Q");
        GlDebug.registerConstant(3184, "GL11.GL_PIXEL_MAP_I_TO_I");
        GlDebug.registerConstant(3185, "GL11.GL_PIXEL_MAP_S_TO_S");
        GlDebug.registerConstant(3186, "GL11.GL_PIXEL_MAP_I_TO_R");
        GlDebug.registerConstant(3187, "GL11.GL_PIXEL_MAP_I_TO_G");
        GlDebug.registerConstant(3188, "GL11.GL_PIXEL_MAP_I_TO_B");
        GlDebug.registerConstant(3189, "GL11.GL_PIXEL_MAP_I_TO_A");
        GlDebug.registerConstant(3190, "GL11.GL_PIXEL_MAP_R_TO_R");
        GlDebug.registerConstant(3191, "GL11.GL_PIXEL_MAP_G_TO_G");
        GlDebug.registerConstant(3192, "GL11.GL_PIXEL_MAP_B_TO_B");
        GlDebug.registerConstant(3193, "GL11.GL_PIXEL_MAP_A_TO_A");
        GlDebug.registerConstant(3248, "GL11.GL_PIXEL_MAP_I_TO_I_SIZE");
        GlDebug.registerConstant(3249, "GL11.GL_PIXEL_MAP_S_TO_S_SIZE");
        GlDebug.registerConstant(3250, "GL11.GL_PIXEL_MAP_I_TO_R_SIZE");
        GlDebug.registerConstant(3251, "GL11.GL_PIXEL_MAP_I_TO_G_SIZE");
        GlDebug.registerConstant(3252, "GL11.GL_PIXEL_MAP_I_TO_B_SIZE");
        GlDebug.registerConstant(3253, "GL11.GL_PIXEL_MAP_I_TO_A_SIZE");
        GlDebug.registerConstant(3254, "GL11.GL_PIXEL_MAP_R_TO_R_SIZE");
        GlDebug.registerConstant(3255, "GL11.GL_PIXEL_MAP_G_TO_G_SIZE");
        GlDebug.registerConstant(3256, "GL11.GL_PIXEL_MAP_B_TO_B_SIZE");
        GlDebug.registerConstant(3257, "GL11.GL_PIXEL_MAP_A_TO_A_SIZE");
        GlDebug.registerConstant(3312, "GL11.GL_UNPACK_SWAP_BYTES");
        GlDebug.registerConstant(3313, "GL11.GL_UNPACK_LSB_FIRST");
        GlDebug.registerConstant(3314, "GL11.GL_UNPACK_ROW_LENGTH");
        GlDebug.registerConstant(3315, "GL11.GL_UNPACK_SKIP_ROWS");
        GlDebug.registerConstant(3316, "GL11.GL_UNPACK_SKIP_PIXELS");
        GlDebug.registerConstant(3317, "GL11.GL_UNPACK_ALIGNMENT");
        GlDebug.registerConstant(3328, "GL11.GL_PACK_SWAP_BYTES");
        GlDebug.registerConstant(3329, "GL11.GL_PACK_LSB_FIRST");
        GlDebug.registerConstant(3330, "GL11.GL_PACK_ROW_LENGTH");
        GlDebug.registerConstant(3331, "GL11.GL_PACK_SKIP_ROWS");
        GlDebug.registerConstant(3332, "GL11.GL_PACK_SKIP_PIXELS");
        GlDebug.registerConstant(3333, "GL11.GL_PACK_ALIGNMENT");
        GlDebug.registerConstant(3344, "GL11.GL_MAP_COLOR");
        GlDebug.registerConstant(3345, "GL11.GL_MAP_STENCIL");
        GlDebug.registerConstant(3346, "GL11.GL_INDEX_SHIFT");
        GlDebug.registerConstant(3347, "GL11.GL_INDEX_OFFSET");
        GlDebug.registerConstant(3348, "GL11.GL_RED_SCALE");
        GlDebug.registerConstant(3349, "GL11.GL_RED_BIAS");
        GlDebug.registerConstant(3350, "GL11.GL_ZOOM_X");
        GlDebug.registerConstant(3351, "GL11.GL_ZOOM_Y");
        GlDebug.registerConstant(3352, "GL11.GL_GREEN_SCALE");
        GlDebug.registerConstant(3353, "GL11.GL_GREEN_BIAS");
        GlDebug.registerConstant(3354, "GL11.GL_BLUE_SCALE");
        GlDebug.registerConstant(3355, "GL11.GL_BLUE_BIAS");
        GlDebug.registerConstant(3356, "GL11.GL_ALPHA_SCALE");
        GlDebug.registerConstant(3357, "GL11.GL_ALPHA_BIAS");
        GlDebug.registerConstant(3358, "GL11.GL_DEPTH_SCALE");
        GlDebug.registerConstant(3359, "GL11.GL_DEPTH_BIAS");
        GlDebug.registerConstant(3376, "GL11.GL_MAX_EVAL_ORDER");
        GlDebug.registerConstant(3377, "GL11.GL_MAX_LIGHTS");
        GlDebug.registerConstant(3378, "GL11.GL_MAX_CLIP_PLANES");
        GlDebug.registerConstant(3379, "GL11.GL_MAX_TEXTURE_SIZE");
        GlDebug.registerConstant(3380, "GL11.GL_MAX_PIXEL_MAP_TABLE");
        GlDebug.registerConstant(3381, "GL11.GL_MAX_ATTRIB_STACK_DEPTH");
        GlDebug.registerConstant(3382, "GL11.GL_MAX_MODELVIEW_STACK_DEPTH");
        GlDebug.registerConstant(3383, "GL11.GL_MAX_NAME_STACK_DEPTH");
        GlDebug.registerConstant(3384, "GL11.GL_MAX_PROJECTION_STACK_DEPTH");
        GlDebug.registerConstant(3385, "GL11.GL_MAX_TEXTURE_STACK_DEPTH");
        GlDebug.registerConstant(3386, "GL11.GL_MAX_VIEWPORT_DIMS");
        GlDebug.registerConstant(3387, "GL11.GL_MAX_CLIENT_ATTRIB_STACK_DEPTH");
        GlDebug.registerConstant(3408, "GL11.GL_SUBPIXEL_BITS");
        GlDebug.registerConstant(3409, "GL11.GL_INDEX_BITS");
        GlDebug.registerConstant(3410, "GL11.GL_RED_BITS");
        GlDebug.registerConstant(3411, "GL11.GL_GREEN_BITS");
        GlDebug.registerConstant(3412, "GL11.GL_BLUE_BITS");
        GlDebug.registerConstant(3413, "GL11.GL_ALPHA_BITS");
        GlDebug.registerConstant(3414, "GL11.GL_DEPTH_BITS");
        GlDebug.registerConstant(3415, "GL11.GL_STENCIL_BITS");
        GlDebug.registerConstant(3416, "GL11.GL_ACCUM_RED_BITS");
        GlDebug.registerConstant(3417, "GL11.GL_ACCUM_GREEN_BITS");
        GlDebug.registerConstant(3418, "GL11.GL_ACCUM_BLUE_BITS");
        GlDebug.registerConstant(3419, "GL11.GL_ACCUM_ALPHA_BITS");
        GlDebug.registerConstant(3440, "GL11.GL_NAME_STACK_DEPTH");
        GlDebug.registerConstant(3456, "GL11.GL_AUTO_NORMAL");
        GlDebug.registerConstant(3472, "GL11.GL_MAP1_COLOR_4");
        GlDebug.registerConstant(3473, "GL11.GL_MAP1_INDEX");
        GlDebug.registerConstant(3474, "GL11.GL_MAP1_NORMAL");
        GlDebug.registerConstant(3475, "GL11.GL_MAP1_TEXTURE_COORD_1");
        GlDebug.registerConstant(3476, "GL11.GL_MAP1_TEXTURE_COORD_2");
        GlDebug.registerConstant(3477, "GL11.GL_MAP1_TEXTURE_COORD_3");
        GlDebug.registerConstant(3478, "GL11.GL_MAP1_TEXTURE_COORD_4");
        GlDebug.registerConstant(3479, "GL11.GL_MAP1_VERTEX_3");
        GlDebug.registerConstant(3480, "GL11.GL_MAP1_VERTEX_4");
        GlDebug.registerConstant(3504, "GL11.GL_MAP2_COLOR_4");
        GlDebug.registerConstant(3505, "GL11.GL_MAP2_INDEX");
        GlDebug.registerConstant(3506, "GL11.GL_MAP2_NORMAL");
        GlDebug.registerConstant(3507, "GL11.GL_MAP2_TEXTURE_COORD_1");
        GlDebug.registerConstant(3508, "GL11.GL_MAP2_TEXTURE_COORD_2");
        GlDebug.registerConstant(3509, "GL11.GL_MAP2_TEXTURE_COORD_3");
        GlDebug.registerConstant(3510, "GL11.GL_MAP2_TEXTURE_COORD_4");
        GlDebug.registerConstant(3511, "GL11.GL_MAP2_VERTEX_3");
        GlDebug.registerConstant(3512, "GL11.GL_MAP2_VERTEX_4");
        GlDebug.registerConstant(3536, "GL11.GL_MAP1_GRID_DOMAIN");
        GlDebug.registerConstant(3537, "GL11.GL_MAP1_GRID_SEGMENTS");
        GlDebug.registerConstant(3538, "GL11.GL_MAP2_GRID_DOMAIN");
        GlDebug.registerConstant(3539, "GL11.GL_MAP2_GRID_SEGMENTS");
        GlDebug.registerConstant(3552, "GL11.GL_TEXTURE_1D");
        GlDebug.registerConstant(3553, "GL11.GL_TEXTURE_2D");
        GlDebug.registerConstant(3568, "GL11.GL_FEEDBACK_BUFFER_POINTER");
        GlDebug.registerConstant(3569, "GL11.GL_FEEDBACK_BUFFER_SIZE");
        GlDebug.registerConstant(3570, "GL11.GL_FEEDBACK_BUFFER_TYPE");
        GlDebug.registerConstant(3571, "GL11.GL_SELECTION_BUFFER_POINTER");
        GlDebug.registerConstant(3572, "GL11.GL_SELECTION_BUFFER_SIZE");
        GlDebug.registerConstant(4096, "GL11.GL_TEXTURE_WIDTH");
        GlDebug.registerConstant(4097, "GL11.GL_TEXTURE_HEIGHT");
        GlDebug.registerConstant(4099, "GL11.GL_TEXTURE_INTERNAL_FORMAT");
        GlDebug.registerConstant(4100, "GL11.GL_TEXTURE_BORDER_COLOR");
        GlDebug.registerConstant(4101, "GL11.GL_TEXTURE_BORDER");
        GlDebug.registerConstant(4352, "GL11.GL_DONT_CARE");
        GlDebug.registerConstant(4353, "GL11.GL_FASTEST");
        GlDebug.registerConstant(4354, "GL11.GL_NICEST");
        GlDebug.registerConstant(16384, "GL11.GL_LIGHT0");
        GlDebug.registerConstant(16385, "GL11.GL_LIGHT1");
        GlDebug.registerConstant(16386, "GL11.GL_LIGHT2");
        GlDebug.registerConstant(16387, "GL11.GL_LIGHT3");
        GlDebug.registerConstant(16388, "GL11.GL_LIGHT4");
        GlDebug.registerConstant(16389, "GL11.GL_LIGHT5");
        GlDebug.registerConstant(16390, "GL11.GL_LIGHT6");
        GlDebug.registerConstant(16391, "GL11.GL_LIGHT7");
        GlDebug.registerConstant(4608, "GL11.GL_AMBIENT");
        GlDebug.registerConstant(4609, "GL11.GL_DIFFUSE");
        GlDebug.registerConstant(4610, "GL11.GL_SPECULAR");
        GlDebug.registerConstant(4611, "GL11.GL_POSITION");
        GlDebug.registerConstant(4612, "GL11.GL_SPOT_DIRECTION");
        GlDebug.registerConstant(4613, "GL11.GL_SPOT_EXPONENT");
        GlDebug.registerConstant(4614, "GL11.GL_SPOT_CUTOFF");
        GlDebug.registerConstant(4615, "GL11.GL_CONSTANT_ATTENUATION");
        GlDebug.registerConstant(4616, "GL11.GL_LINEAR_ATTENUATION");
        GlDebug.registerConstant(4617, "GL11.GL_QUADRATIC_ATTENUATION");
        GlDebug.registerConstant(4864, "GL11.GL_COMPILE");
        GlDebug.registerConstant(4865, "GL11.GL_COMPILE_AND_EXECUTE");
        GlDebug.registerConstant(5376, "GL11.GL_CLEAR");
        GlDebug.registerConstant(5377, "GL11.GL_AND");
        GlDebug.registerConstant(5378, "GL11.GL_AND_REVERSE");
        GlDebug.registerConstant(5379, "GL11.GL_COPY");
        GlDebug.registerConstant(5380, "GL11.GL_AND_INVERTED");
        GlDebug.registerConstant(5381, "GL11.GL_NOOP");
        GlDebug.registerConstant(5382, "GL11.GL_XOR");
        GlDebug.registerConstant(5383, "GL11.GL_OR");
        GlDebug.registerConstant(5384, "GL11.GL_NOR");
        GlDebug.registerConstant(5385, "GL11.GL_EQUIV");
        GlDebug.registerConstant(5386, "GL11.GL_INVERT");
        GlDebug.registerConstant(5387, "GL11.GL_OR_REVERSE");
        GlDebug.registerConstant(5388, "GL11.GL_COPY_INVERTED");
        GlDebug.registerConstant(5389, "GL11.GL_OR_INVERTED");
        GlDebug.registerConstant(5390, "GL11.GL_NAND");
        GlDebug.registerConstant(5391, "GL11.GL_SET");
        GlDebug.registerConstant(5632, "GL11.GL_EMISSION");
        GlDebug.registerConstant(5633, "GL11.GL_SHININESS");
        GlDebug.registerConstant(5634, "GL11.GL_AMBIENT_AND_DIFFUSE");
        GlDebug.registerConstant(5635, "GL11.GL_COLOR_INDEXES");
        GlDebug.registerConstant(5888, "GL11.GL_MODELVIEW");
        GlDebug.registerConstant(5889, "GL11.GL_PROJECTION");
        GlDebug.registerConstant(5890, "GL11.GL_TEXTURE");
        GlDebug.registerConstant(6144, "GL11.GL_COLOR");
        GlDebug.registerConstant(6145, "GL11.GL_DEPTH");
        GlDebug.registerConstant(6146, "GL11.GL_STENCIL");
        GlDebug.registerConstant(6400, "GL11.GL_COLOR_INDEX");
        GlDebug.registerConstant(6401, "GL11.GL_STENCIL_INDEX");
        GlDebug.registerConstant(6402, "GL11.GL_DEPTH_COMPONENT");
        GlDebug.registerConstant(6403, "GL11.GL_RED");
        GlDebug.registerConstant(6404, "GL11.GL_GREEN");
        GlDebug.registerConstant(6405, "GL11.GL_BLUE");
        GlDebug.registerConstant(6406, "GL11.GL_ALPHA");
        GlDebug.registerConstant(6407, "GL11.GL_RGB");
        GlDebug.registerConstant(6408, "GL11.GL_RGBA");
        GlDebug.registerConstant(6409, "GL11.GL_LUMINANCE");
        GlDebug.registerConstant(6410, "GL11.GL_LUMINANCE_ALPHA");
        GlDebug.registerConstant(6656, "GL11.GL_BITMAP");
        GlDebug.registerConstant(6912, "GL11.GL_POINT");
        GlDebug.registerConstant(6913, "GL11.GL_LINE");
        GlDebug.registerConstant(6914, "GL11.GL_FILL");
        GlDebug.registerConstant(7168, "GL11.GL_RENDER");
        GlDebug.registerConstant(7169, "GL11.GL_FEEDBACK");
        GlDebug.registerConstant(7170, "GL11.GL_SELECT");
        GlDebug.registerConstant(7424, "GL11.GL_FLAT");
        GlDebug.registerConstant(7425, "GL11.GL_SMOOTH");
        GlDebug.registerConstant(7680, "GL11.GL_KEEP");
        GlDebug.registerConstant(7681, "GL11.GL_REPLACE");
        GlDebug.registerConstant(7682, "GL11.GL_INCR");
        GlDebug.registerConstant(7683, "GL11.GL_DECR");
        GlDebug.registerConstant(7936, "GL11.GL_VENDOR");
        GlDebug.registerConstant(7937, "GL11.GL_RENDERER");
        GlDebug.registerConstant(7938, "GL11.GL_VERSION");
        GlDebug.registerConstant(7939, "GL11.GL_EXTENSIONS");
        GlDebug.registerConstant(8192, "GL11.GL_S");
        GlDebug.registerConstant(8193, "GL11.GL_T");
        GlDebug.registerConstant(8194, "GL11.GL_R");
        GlDebug.registerConstant(8195, "GL11.GL_Q");
        GlDebug.registerConstant(8448, "GL11.GL_MODULATE");
        GlDebug.registerConstant(8449, "GL11.GL_DECAL");
        GlDebug.registerConstant(8704, "GL11.GL_TEXTURE_ENV_MODE");
        GlDebug.registerConstant(8705, "GL11.GL_TEXTURE_ENV_COLOR");
        GlDebug.registerConstant(8960, "GL11.GL_TEXTURE_ENV");
        GlDebug.registerConstant(9216, "GL11.GL_EYE_LINEAR");
        GlDebug.registerConstant(9217, "GL11.GL_OBJECT_LINEAR");
        GlDebug.registerConstant(9218, "GL11.GL_SPHERE_MAP");
        GlDebug.registerConstant(9472, "GL11.GL_TEXTURE_GEN_MODE");
        GlDebug.registerConstant(9473, "GL11.GL_OBJECT_PLANE");
        GlDebug.registerConstant(9474, "GL11.GL_EYE_PLANE");
        GlDebug.registerConstant(9728, "GL11.GL_NEAREST");
        GlDebug.registerConstant(9729, "GL11.GL_LINEAR");
        GlDebug.registerConstant(9984, "GL11.GL_NEAREST_MIPMAP_NEAREST");
        GlDebug.registerConstant(9985, "GL11.GL_LINEAR_MIPMAP_NEAREST");
        GlDebug.registerConstant(9986, "GL11.GL_NEAREST_MIPMAP_LINEAR");
        GlDebug.registerConstant(9987, "GL11.GL_LINEAR_MIPMAP_LINEAR");
        GlDebug.registerConstant(10240, "GL11.GL_TEXTURE_MAG_FILTER");
        GlDebug.registerConstant(10241, "GL11.GL_TEXTURE_MIN_FILTER");
        GlDebug.registerConstant(10242, "GL11.GL_TEXTURE_WRAP_S");
        GlDebug.registerConstant(10243, "GL11.GL_TEXTURE_WRAP_T");
        GlDebug.registerConstant(10496, "GL11.GL_CLAMP");
        GlDebug.registerConstant(10497, "GL11.GL_REPEAT");
        GlDebug.registerConstant(-1, "GL11.GL_ALL_CLIENT_ATTRIB_BITS");
        GlDebug.registerConstant(32824, "GL11.GL_POLYGON_OFFSET_FACTOR");
        GlDebug.registerConstant(10752, "GL11.GL_POLYGON_OFFSET_UNITS");
        GlDebug.registerConstant(10753, "GL11.GL_POLYGON_OFFSET_POINT");
        GlDebug.registerConstant(10754, "GL11.GL_POLYGON_OFFSET_LINE");
        GlDebug.registerConstant(32823, "GL11.GL_POLYGON_OFFSET_FILL");
        GlDebug.registerConstant(32827, "GL11.GL_ALPHA4");
        GlDebug.registerConstant(32828, "GL11.GL_ALPHA8");
        GlDebug.registerConstant(32829, "GL11.GL_ALPHA12");
        GlDebug.registerConstant(32830, "GL11.GL_ALPHA16");
        GlDebug.registerConstant(32831, "GL11.GL_LUMINANCE4");
        GlDebug.registerConstant(32832, "GL11.GL_LUMINANCE8");
        GlDebug.registerConstant(32833, "GL11.GL_LUMINANCE12");
        GlDebug.registerConstant(32834, "GL11.GL_LUMINANCE16");
        GlDebug.registerConstant(32835, "GL11.GL_LUMINANCE4_ALPHA4");
        GlDebug.registerConstant(32836, "GL11.GL_LUMINANCE6_ALPHA2");
        GlDebug.registerConstant(32837, "GL11.GL_LUMINANCE8_ALPHA8");
        GlDebug.registerConstant(32838, "GL11.GL_LUMINANCE12_ALPHA4");
        GlDebug.registerConstant(32839, "GL11.GL_LUMINANCE12_ALPHA12");
        GlDebug.registerConstant(32840, "GL11.GL_LUMINANCE16_ALPHA16");
        GlDebug.registerConstant(32841, "GL11.GL_INTENSITY");
        GlDebug.registerConstant(32842, "GL11.GL_INTENSITY4");
        GlDebug.registerConstant(32843, "GL11.GL_INTENSITY8");
        GlDebug.registerConstant(32844, "GL11.GL_INTENSITY12");
        GlDebug.registerConstant(32845, "GL11.GL_INTENSITY16");
        GlDebug.registerConstant(10768, "GL11.GL_R3_G3_B2");
        GlDebug.registerConstant(32847, "GL11.GL_RGB4");
        GlDebug.registerConstant(32848, "GL11.GL_RGB5");
        GlDebug.registerConstant(32849, "GL11.GL_RGB8");
        GlDebug.registerConstant(32850, "GL11.GL_RGB10");
        GlDebug.registerConstant(32851, "GL11.GL_RGB12");
        GlDebug.registerConstant(32852, "GL11.GL_RGB16");
        GlDebug.registerConstant(32853, "GL11.GL_RGBA2");
        GlDebug.registerConstant(32854, "GL11.GL_RGBA4");
        GlDebug.registerConstant(32855, "GL11.GL_RGB5_A1");
        GlDebug.registerConstant(32856, "GL11.GL_RGBA8");
        GlDebug.registerConstant(32857, "GL11.GL_RGB10_A2");
        GlDebug.registerConstant(32858, "GL11.GL_RGBA12");
        GlDebug.registerConstant(32859, "GL11.GL_RGBA16");
        GlDebug.registerConstant(32860, "GL11.GL_TEXTURE_RED_SIZE");
        GlDebug.registerConstant(32861, "GL11.GL_TEXTURE_GREEN_SIZE");
        GlDebug.registerConstant(32862, "GL11.GL_TEXTURE_BLUE_SIZE");
        GlDebug.registerConstant(32863, "GL11.GL_TEXTURE_ALPHA_SIZE");
        GlDebug.registerConstant(32864, "GL11.GL_TEXTURE_LUMINANCE_SIZE");
        GlDebug.registerConstant(32865, "GL11.GL_TEXTURE_INTENSITY_SIZE");
        GlDebug.registerConstant(32867, "GL11.GL_PROXY_TEXTURE_1D");
        GlDebug.registerConstant(32868, "GL11.GL_PROXY_TEXTURE_2D");
        GlDebug.registerConstant(32870, "GL11.GL_TEXTURE_PRIORITY");
        GlDebug.registerConstant(32871, "GL11.GL_TEXTURE_RESIDENT");
        GlDebug.registerConstant(32872, "GL11.GL_TEXTURE_BINDING_1D");
        GlDebug.registerConstant(32873, "GL11.GL_TEXTURE_BINDING_2D");
        GlDebug.registerConstant(32884, "GL11.GL_VERTEX_ARRAY");
        GlDebug.registerConstant(32885, "GL11.GL_NORMAL_ARRAY");
        GlDebug.registerConstant(32886, "GL11.GL_COLOR_ARRAY");
        GlDebug.registerConstant(32887, "GL11.GL_INDEX_ARRAY");
        GlDebug.registerConstant(32888, "GL11.GL_TEXTURE_COORD_ARRAY");
        GlDebug.registerConstant(32889, "GL11.GL_EDGE_FLAG_ARRAY");
        GlDebug.registerConstant(32890, "GL11.GL_VERTEX_ARRAY_SIZE");
        GlDebug.registerConstant(32891, "GL11.GL_VERTEX_ARRAY_TYPE");
        GlDebug.registerConstant(32892, "GL11.GL_VERTEX_ARRAY_STRIDE");
        GlDebug.registerConstant(32894, "GL11.GL_NORMAL_ARRAY_TYPE");
        GlDebug.registerConstant(32895, "GL11.GL_NORMAL_ARRAY_STRIDE");
        GlDebug.registerConstant(32897, "GL11.GL_COLOR_ARRAY_SIZE");
        GlDebug.registerConstant(32898, "GL11.GL_COLOR_ARRAY_TYPE");
        GlDebug.registerConstant(32899, "GL11.GL_COLOR_ARRAY_STRIDE");
        GlDebug.registerConstant(32901, "GL11.GL_INDEX_ARRAY_TYPE");
        GlDebug.registerConstant(32902, "GL11.GL_INDEX_ARRAY_STRIDE");
        GlDebug.registerConstant(32904, "GL11.GL_TEXTURE_COORD_ARRAY_SIZE");
        GlDebug.registerConstant(32905, "GL11.GL_TEXTURE_COORD_ARRAY_TYPE");
        GlDebug.registerConstant(32906, "GL11.GL_TEXTURE_COORD_ARRAY_STRIDE");
        GlDebug.registerConstant(32908, "GL11.GL_EDGE_FLAG_ARRAY_STRIDE");
        GlDebug.registerConstant(32910, "GL11.GL_VERTEX_ARRAY_POINTER");
        GlDebug.registerConstant(32911, "GL11.GL_NORMAL_ARRAY_POINTER");
        GlDebug.registerConstant(32912, "GL11.GL_COLOR_ARRAY_POINTER");
        GlDebug.registerConstant(32913, "GL11.GL_INDEX_ARRAY_POINTER");
        GlDebug.registerConstant(32914, "GL11.GL_TEXTURE_COORD_ARRAY_POINTER");
        GlDebug.registerConstant(32915, "GL11.GL_EDGE_FLAG_ARRAY_POINTER");
        GlDebug.registerConstant(10784, "GL11.GL_V2F");
        GlDebug.registerConstant(10785, "GL11.GL_V3F");
        GlDebug.registerConstant(10786, "GL11.GL_C4UB_V2F");
        GlDebug.registerConstant(10787, "GL11.GL_C4UB_V3F");
        GlDebug.registerConstant(10788, "GL11.GL_C3F_V3F");
        GlDebug.registerConstant(10789, "GL11.GL_N3F_V3F");
        GlDebug.registerConstant(10790, "GL11.GL_C4F_N3F_V3F");
        GlDebug.registerConstant(10791, "GL11.GL_T2F_V3F");
        GlDebug.registerConstant(10792, "GL11.GL_T4F_V4F");
        GlDebug.registerConstant(10793, "GL11.GL_T2F_C4UB_V3F");
        GlDebug.registerConstant(10794, "GL11.GL_T2F_C3F_V3F");
        GlDebug.registerConstant(10795, "GL11.GL_T2F_N3F_V3F");
        GlDebug.registerConstant(10796, "GL11.GL_T2F_C4F_N3F_V3F");
        GlDebug.registerConstant(10797, "GL11.GL_T4F_C4F_N3F_V4F");
        GlDebug.registerConstant(3057, "GL11.GL_LOGIC_OP");
        GlDebug.registerConstant(4099, "GL11.GL_TEXTURE_COMPONENTS");
        GlDebug.registerConstant(32874, "GL12.GL_TEXTURE_BINDING_3D");
        GlDebug.registerConstant(32875, "GL12.GL_PACK_SKIP_IMAGES");
        GlDebug.registerConstant(32876, "GL12.GL_PACK_IMAGE_HEIGHT");
        GlDebug.registerConstant(32877, "GL12.GL_UNPACK_SKIP_IMAGES");
        GlDebug.registerConstant(32878, "GL12.GL_UNPACK_IMAGE_HEIGHT");
        GlDebug.registerConstant(32879, "GL12.GL_TEXTURE_3D");
        GlDebug.registerConstant(32880, "GL12.GL_PROXY_TEXTURE_3D");
        GlDebug.registerConstant(32881, "GL12.GL_TEXTURE_DEPTH");
        GlDebug.registerConstant(32882, "GL12.GL_TEXTURE_WRAP_R");
        GlDebug.registerConstant(32883, "GL12.GL_MAX_3D_TEXTURE_SIZE");
        GlDebug.registerConstant(32992, "GL12.GL_BGR");
        GlDebug.registerConstant(32993, "GL12.GL_BGRA");
        GlDebug.registerConstant(32818, "GL12.GL_UNSIGNED_BYTE_3_3_2");
        GlDebug.registerConstant(33634, "GL12.GL_UNSIGNED_BYTE_2_3_3_REV");
        GlDebug.registerConstant(33635, "GL12.GL_UNSIGNED_SHORT_5_6_5");
        GlDebug.registerConstant(33636, "GL12.GL_UNSIGNED_SHORT_5_6_5_REV");
        GlDebug.registerConstant(32819, "GL12.GL_UNSIGNED_SHORT_4_4_4_4");
        GlDebug.registerConstant(33637, "GL12.GL_UNSIGNED_SHORT_4_4_4_4_REV");
        GlDebug.registerConstant(32820, "GL12.GL_UNSIGNED_SHORT_5_5_5_1");
        GlDebug.registerConstant(33638, "GL12.GL_UNSIGNED_SHORT_1_5_5_5_REV");
        GlDebug.registerConstant(32821, "GL12.GL_UNSIGNED_INT_8_8_8_8");
        GlDebug.registerConstant(33639, "GL12.GL_UNSIGNED_INT_8_8_8_8_REV");
        GlDebug.registerConstant(32822, "GL12.GL_UNSIGNED_INT_10_10_10_2");
        GlDebug.registerConstant(33640, "GL12.GL_UNSIGNED_INT_2_10_10_10_REV");
        GlDebug.registerConstant(32826, "GL12.GL_RESCALE_NORMAL");
        GlDebug.registerConstant(33272, "GL12.GL_LIGHT_MODEL_COLOR_CONTROL");
        GlDebug.registerConstant(33273, "GL12.GL_SINGLE_COLOR");
        GlDebug.registerConstant(33274, "GL12.GL_SEPARATE_SPECULAR_COLOR");
        GlDebug.registerConstant(33071, "GL12.GL_CLAMP_TO_EDGE");
        GlDebug.registerConstant(33082, "GL12.GL_TEXTURE_MIN_LOD");
        GlDebug.registerConstant(33083, "GL12.GL_TEXTURE_MAX_LOD");
        GlDebug.registerConstant(33084, "GL12.GL_TEXTURE_BASE_LEVEL");
        GlDebug.registerConstant(33085, "GL12.GL_TEXTURE_MAX_LEVEL");
        GlDebug.registerConstant(33000, "GL12.GL_MAX_ELEMENTS_VERTICES");
        GlDebug.registerConstant(33001, "GL12.GL_MAX_ELEMENTS_INDICES");
        GlDebug.registerConstant(33901, "GL12.GL_ALIASED_POINT_SIZE_RANGE");
        GlDebug.registerConstant(33902, "GL12.GL_ALIASED_LINE_WIDTH_RANGE");
        GlDebug.registerConstant(33984, "GL13.GL_TEXTURE0");
        GlDebug.registerConstant(33985, "GL13.GL_TEXTURE1");
        GlDebug.registerConstant(33986, "GL13.GL_TEXTURE2");
        GlDebug.registerConstant(33987, "GL13.GL_TEXTURE3");
        GlDebug.registerConstant(33988, "GL13.GL_TEXTURE4");
        GlDebug.registerConstant(33989, "GL13.GL_TEXTURE5");
        GlDebug.registerConstant(33990, "GL13.GL_TEXTURE6");
        GlDebug.registerConstant(33991, "GL13.GL_TEXTURE7");
        GlDebug.registerConstant(33992, "GL13.GL_TEXTURE8");
        GlDebug.registerConstant(33993, "GL13.GL_TEXTURE9");
        GlDebug.registerConstant(33994, "GL13.GL_TEXTURE10");
        GlDebug.registerConstant(33995, "GL13.GL_TEXTURE11");
        GlDebug.registerConstant(33996, "GL13.GL_TEXTURE12");
        GlDebug.registerConstant(33997, "GL13.GL_TEXTURE13");
        GlDebug.registerConstant(33998, "GL13.GL_TEXTURE14");
        GlDebug.registerConstant(33999, "GL13.GL_TEXTURE15");
        GlDebug.registerConstant(34000, "GL13.GL_TEXTURE16");
        GlDebug.registerConstant(34001, "GL13.GL_TEXTURE17");
        GlDebug.registerConstant(34002, "GL13.GL_TEXTURE18");
        GlDebug.registerConstant(34003, "GL13.GL_TEXTURE19");
        GlDebug.registerConstant(34004, "GL13.GL_TEXTURE20");
        GlDebug.registerConstant(34005, "GL13.GL_TEXTURE21");
        GlDebug.registerConstant(34006, "GL13.GL_TEXTURE22");
        GlDebug.registerConstant(34007, "GL13.GL_TEXTURE23");
        GlDebug.registerConstant(34008, "GL13.GL_TEXTURE24");
        GlDebug.registerConstant(34009, "GL13.GL_TEXTURE25");
        GlDebug.registerConstant(34010, "GL13.GL_TEXTURE26");
        GlDebug.registerConstant(34011, "GL13.GL_TEXTURE27");
        GlDebug.registerConstant(34012, "GL13.GL_TEXTURE28");
        GlDebug.registerConstant(34013, "GL13.GL_TEXTURE29");
        GlDebug.registerConstant(34014, "GL13.GL_TEXTURE30");
        GlDebug.registerConstant(34015, "GL13.GL_TEXTURE31");
        GlDebug.registerConstant(34016, "GL13.GL_ACTIVE_TEXTURE");
        GlDebug.registerConstant(34017, "GL13.GL_CLIENT_ACTIVE_TEXTURE");
        GlDebug.registerConstant(34018, "GL13.GL_MAX_TEXTURE_UNITS");
        GlDebug.registerConstant(34065, "GL13.GL_NORMAL_MAP");
        GlDebug.registerConstant(34066, "GL13.GL_REFLECTION_MAP");
        GlDebug.registerConstant(34067, "GL13.GL_TEXTURE_CUBE_MAP");
        GlDebug.registerConstant(34068, "GL13.GL_TEXTURE_BINDING_CUBE_MAP");
        GlDebug.registerConstant(34069, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X");
        GlDebug.registerConstant(34070, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_X");
        GlDebug.registerConstant(34071, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Y");
        GlDebug.registerConstant(34072, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y");
        GlDebug.registerConstant(34073, "GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_Z");
        GlDebug.registerConstant(34074, "GL13.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z");
        GlDebug.registerConstant(34075, "GL13.GL_PROXY_TEXTURE_CUBE_MAP");
        GlDebug.registerConstant(34076, "GL13.GL_MAX_CUBE_MAP_TEXTURE_SIZE");
        GlDebug.registerConstant(34025, "GL13.GL_COMPRESSED_ALPHA");
        GlDebug.registerConstant(34026, "GL13.GL_COMPRESSED_LUMINANCE");
        GlDebug.registerConstant(34027, "GL13.GL_COMPRESSED_LUMINANCE_ALPHA");
        GlDebug.registerConstant(34028, "GL13.GL_COMPRESSED_INTENSITY");
        GlDebug.registerConstant(34029, "GL13.GL_COMPRESSED_RGB");
        GlDebug.registerConstant(34030, "GL13.GL_COMPRESSED_RGBA");
        GlDebug.registerConstant(34031, "GL13.GL_TEXTURE_COMPRESSION_HINT");
        GlDebug.registerConstant(34464, "GL13.GL_TEXTURE_COMPRESSED_IMAGE_SIZE");
        GlDebug.registerConstant(34465, "GL13.GL_TEXTURE_COMPRESSED");
        GlDebug.registerConstant(34466, "GL13.GL_NUM_COMPRESSED_TEXTURE_FORMATS");
        GlDebug.registerConstant(34467, "GL13.GL_COMPRESSED_TEXTURE_FORMATS");
        GlDebug.registerConstant(32925, "GL13.GL_MULTISAMPLE");
        GlDebug.registerConstant(32926, "GL13.GL_SAMPLE_ALPHA_TO_COVERAGE");
        GlDebug.registerConstant(32927, "GL13.GL_SAMPLE_ALPHA_TO_ONE");
        GlDebug.registerConstant(32928, "GL13.GL_SAMPLE_COVERAGE");
        GlDebug.registerConstant(32936, "GL13.GL_SAMPLE_BUFFERS");
        GlDebug.registerConstant(32937, "GL13.GL_SAMPLES");
        GlDebug.registerConstant(32938, "GL13.GL_SAMPLE_COVERAGE_VALUE");
        GlDebug.registerConstant(32939, "GL13.GL_SAMPLE_COVERAGE_INVERT");
        GlDebug.registerConstant(34019, "GL13.GL_TRANSPOSE_MODELVIEW_MATRIX");
        GlDebug.registerConstant(34020, "GL13.GL_TRANSPOSE_PROJECTION_MATRIX");
        GlDebug.registerConstant(34021, "GL13.GL_TRANSPOSE_TEXTURE_MATRIX");
        GlDebug.registerConstant(34022, "GL13.GL_TRANSPOSE_COLOR_MATRIX");
        GlDebug.registerConstant(34160, "GL13.GL_COMBINE");
        GlDebug.registerConstant(34161, "GL13.GL_COMBINE_RGB");
        GlDebug.registerConstant(34162, "GL13.GL_COMBINE_ALPHA");
        GlDebug.registerConstant(34176, "GL13.GL_SOURCE0_RGB");
        GlDebug.registerConstant(34177, "GL13.GL_SOURCE1_RGB");
        GlDebug.registerConstant(34178, "GL13.GL_SOURCE2_RGB");
        GlDebug.registerConstant(34184, "GL13.GL_SOURCE0_ALPHA");
        GlDebug.registerConstant(34185, "GL13.GL_SOURCE1_ALPHA");
        GlDebug.registerConstant(34186, "GL13.GL_SOURCE2_ALPHA");
        GlDebug.registerConstant(34192, "GL13.GL_OPERAND0_RGB");
        GlDebug.registerConstant(34193, "GL13.GL_OPERAND1_RGB");
        GlDebug.registerConstant(34194, "GL13.GL_OPERAND2_RGB");
        GlDebug.registerConstant(34200, "GL13.GL_OPERAND0_ALPHA");
        GlDebug.registerConstant(34201, "GL13.GL_OPERAND1_ALPHA");
        GlDebug.registerConstant(34202, "GL13.GL_OPERAND2_ALPHA");
        GlDebug.registerConstant(34163, "GL13.GL_RGB_SCALE");
        GlDebug.registerConstant(34164, "GL13.GL_ADD_SIGNED");
        GlDebug.registerConstant(34165, "GL13.GL_INTERPOLATE");
        GlDebug.registerConstant(34023, "GL13.GL_SUBTRACT");
        GlDebug.registerConstant(34166, "GL13.GL_CONSTANT");
        GlDebug.registerConstant(34167, "GL13.GL_PRIMARY_COLOR");
        GlDebug.registerConstant(34168, "GL13.GL_PREVIOUS");
        GlDebug.registerConstant(34478, "GL13.GL_DOT3_RGB");
        GlDebug.registerConstant(34479, "GL13.GL_DOT3_RGBA");
        GlDebug.registerConstant(33069, "GL13.GL_CLAMP_TO_BORDER");
        GlDebug.registerConstant(33169, "GL14.GL_GENERATE_MIPMAP");
        GlDebug.registerConstant(33170, "GL14.GL_GENERATE_MIPMAP_HINT");
        GlDebug.registerConstant(33189, "GL14.GL_DEPTH_COMPONENT16");
        GlDebug.registerConstant(33190, "GL14.GL_DEPTH_COMPONENT24");
        GlDebug.registerConstant(33191, "GL14.GL_DEPTH_COMPONENT32");
        GlDebug.registerConstant(34890, "GL14.GL_TEXTURE_DEPTH_SIZE");
        GlDebug.registerConstant(34891, "GL14.GL_DEPTH_TEXTURE_MODE");
        GlDebug.registerConstant(34892, "GL14.GL_TEXTURE_COMPARE_MODE");
        GlDebug.registerConstant(34893, "GL14.GL_TEXTURE_COMPARE_FUNC");
        GlDebug.registerConstant(34894, "GL14.GL_COMPARE_R_TO_TEXTURE");
        GlDebug.registerConstant(33872, "GL14.GL_FOG_COORDINATE_SOURCE");
        GlDebug.registerConstant(33873, "GL14.GL_FOG_COORDINATE");
        GlDebug.registerConstant(33874, "GL14.GL_FRAGMENT_DEPTH");
        GlDebug.registerConstant(33875, "GL14.GL_CURRENT_FOG_COORDINATE");
        GlDebug.registerConstant(33876, "GL14.GL_FOG_COORDINATE_ARRAY_TYPE");
        GlDebug.registerConstant(33877, "GL14.GL_FOG_COORDINATE_ARRAY_STRIDE");
        GlDebug.registerConstant(33878, "GL14.GL_FOG_COORDINATE_ARRAY_POINTER");
        GlDebug.registerConstant(33879, "GL14.GL_FOG_COORDINATE_ARRAY");
        GlDebug.registerConstant(33062, "GL14.GL_POINT_SIZE_MIN");
        GlDebug.registerConstant(33063, "GL14.GL_POINT_SIZE_MAX");
        GlDebug.registerConstant(33064, "GL14.GL_POINT_FADE_THRESHOLD_SIZE");
        GlDebug.registerConstant(33065, "GL14.GL_POINT_DISTANCE_ATTENUATION");
        GlDebug.registerConstant(33880, "GL14.GL_COLOR_SUM");
        GlDebug.registerConstant(33881, "GL14.GL_CURRENT_SECONDARY_COLOR");
        GlDebug.registerConstant(33882, "GL14.GL_SECONDARY_COLOR_ARRAY_SIZE");
        GlDebug.registerConstant(33883, "GL14.GL_SECONDARY_COLOR_ARRAY_TYPE");
        GlDebug.registerConstant(33884, "GL14.GL_SECONDARY_COLOR_ARRAY_STRIDE");
        GlDebug.registerConstant(33885, "GL14.GL_SECONDARY_COLOR_ARRAY_POINTER");
        GlDebug.registerConstant(33886, "GL14.GL_SECONDARY_COLOR_ARRAY");
        GlDebug.registerConstant(32968, "GL14.GL_BLEND_DST_RGB");
        GlDebug.registerConstant(32969, "GL14.GL_BLEND_SRC_RGB");
        GlDebug.registerConstant(32970, "GL14.GL_BLEND_DST_ALPHA");
        GlDebug.registerConstant(32971, "GL14.GL_BLEND_SRC_ALPHA");
        GlDebug.registerConstant(34055, "GL14.GL_INCR_WRAP");
        GlDebug.registerConstant(34056, "GL14.GL_DECR_WRAP");
        GlDebug.registerConstant(34048, "GL14.GL_TEXTURE_FILTER_CONTROL");
        GlDebug.registerConstant(34049, "GL14.GL_TEXTURE_LOD_BIAS");
        GlDebug.registerConstant(34045, "GL14.GL_MAX_TEXTURE_LOD_BIAS");
        GlDebug.registerConstant(33648, "GL14.GL_MIRRORED_REPEAT");
        GlDebug.registerConstant(32773, "ARBImaging.GL_BLEND_COLOR");
        GlDebug.registerConstant(32777, "ARBImaging.GL_BLEND_EQUATION");
        GlDebug.registerConstant(32774, "GL14.GL_FUNC_ADD");
        GlDebug.registerConstant(32778, "GL14.GL_FUNC_SUBTRACT");
        GlDebug.registerConstant(32779, "GL14.GL_FUNC_REVERSE_SUBTRACT");
        GlDebug.registerConstant(32775, "GL14.GL_MIN");
        GlDebug.registerConstant(32776, "GL14.GL_MAX");
        GlDebug.registerConstant(34962, "GL15.GL_ARRAY_BUFFER");
        GlDebug.registerConstant(34963, "GL15.GL_ELEMENT_ARRAY_BUFFER");
        GlDebug.registerConstant(34964, "GL15.GL_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34965, "GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34966, "GL15.GL_VERTEX_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34967, "GL15.GL_NORMAL_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34968, "GL15.GL_COLOR_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34969, "GL15.GL_INDEX_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34970, "GL15.GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34971, "GL15.GL_EDGE_FLAG_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34972, "GL15.GL_SECONDARY_COLOR_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34973, "GL15.GL_FOG_COORDINATE_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34974, "GL15.GL_WEIGHT_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(34975, "GL15.GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING");
        GlDebug.registerConstant(35040, "GL15.GL_STREAM_DRAW");
        GlDebug.registerConstant(35041, "GL15.GL_STREAM_READ");
        GlDebug.registerConstant(35042, "GL15.GL_STREAM_COPY");
        GlDebug.registerConstant(35044, "GL15.GL_STATIC_DRAW");
        GlDebug.registerConstant(35045, "GL15.GL_STATIC_READ");
        GlDebug.registerConstant(35046, "GL15.GL_STATIC_COPY");
        GlDebug.registerConstant(35048, "GL15.GL_DYNAMIC_DRAW");
        GlDebug.registerConstant(35049, "GL15.GL_DYNAMIC_READ");
        GlDebug.registerConstant(35050, "GL15.GL_DYNAMIC_COPY");
        GlDebug.registerConstant(35000, "GL15.GL_READ_ONLY");
        GlDebug.registerConstant(35001, "GL15.GL_WRITE_ONLY");
        GlDebug.registerConstant(35002, "GL15.GL_READ_WRITE");
        GlDebug.registerConstant(34660, "GL15.GL_BUFFER_SIZE");
        GlDebug.registerConstant(34661, "GL15.GL_BUFFER_USAGE");
        GlDebug.registerConstant(35003, "GL15.GL_BUFFER_ACCESS");
        GlDebug.registerConstant(35004, "GL15.GL_BUFFER_MAPPED");
        GlDebug.registerConstant(35005, "GL15.GL_BUFFER_MAP_POINTER");
        GlDebug.registerConstant(34138, "NVFogDistance.GL_FOG_DISTANCE_MODE_NV");
        GlDebug.registerConstant(34139, "NVFogDistance.GL_EYE_RADIAL_NV");
        GlDebug.registerConstant(34140, "NVFogDistance.GL_EYE_PLANE_ABSOLUTE_NV");
        field_4923 = Maps.newHashMap();
    }
}

