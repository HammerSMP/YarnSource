/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;

@Environment(value=EnvType.CLIENT)
public class BlockModelRenderer {
    private final BlockColors colorMap;
    private static final ThreadLocal<BrightnessCache> brightnessCache = ThreadLocal.withInitial(() -> new BrightnessCache());

    public BlockModelRenderer(BlockColors arg) {
        this.colorMap = arg;
    }

    public boolean render(BlockRenderView arg, BakedModel arg2, BlockState arg3, BlockPos arg4, MatrixStack arg5, VertexConsumer arg6, boolean bl, Random random, long l, int i) {
        boolean bl2 = MinecraftClient.isAmbientOcclusionEnabled() && arg3.getLuminance() == 0 && arg2.useAmbientOcclusion();
        Vec3d lv = arg3.getModelOffset(arg, arg4);
        arg5.translate(lv.x, lv.y, lv.z);
        try {
            if (bl2) {
                return this.renderSmooth(arg, arg2, arg3, arg4, arg5, arg6, bl, random, l, i);
            }
            return this.renderFlat(arg, arg2, arg3, arg4, arg5, arg6, bl, random, l, i);
        }
        catch (Throwable throwable) {
            CrashReport lv2 = CrashReport.create(throwable, "Tesselating block model");
            CrashReportSection lv3 = lv2.addElement("Block model being tesselated");
            CrashReportSection.addBlockInfo(lv3, arg4, arg3);
            lv3.add("Using AO", bl2);
            throw new CrashException(lv2);
        }
    }

    public boolean renderSmooth(BlockRenderView arg, BakedModel arg2, BlockState arg3, BlockPos arg4, MatrixStack arg5, VertexConsumer arg6, boolean bl, Random random, long l, int i) {
        boolean bl2 = false;
        float[] fs = new float[Direction.values().length * 2];
        BitSet bitSet = new BitSet(3);
        AmbientOcclusionCalculator lv = new AmbientOcclusionCalculator();
        for (Direction lv2 : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = arg2.getQuads(arg3, lv2, random);
            if (list.isEmpty() || bl && !Block.shouldDrawSide(arg3, arg, arg4, lv2)) continue;
            this.renderQuadsSmooth(arg, arg3, arg4, arg5, arg6, list, fs, bitSet, lv, i);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = arg2.getQuads(arg3, null, random);
        if (!list2.isEmpty()) {
            this.renderQuadsSmooth(arg, arg3, arg4, arg5, arg6, list2, fs, bitSet, lv, i);
            bl2 = true;
        }
        return bl2;
    }

    public boolean renderFlat(BlockRenderView arg, BakedModel arg2, BlockState arg3, BlockPos arg4, MatrixStack arg5, VertexConsumer arg6, boolean bl, Random random, long l, int i) {
        boolean bl2 = false;
        BitSet bitSet = new BitSet(3);
        for (Direction lv : Direction.values()) {
            random.setSeed(l);
            List<BakedQuad> list = arg2.getQuads(arg3, lv, random);
            if (list.isEmpty() || bl && !Block.shouldDrawSide(arg3, arg, arg4, lv)) continue;
            int j = WorldRenderer.getLightmapCoordinates(arg, arg3, arg4.offset(lv));
            this.renderQuadsFlat(arg, arg3, arg4, j, i, false, arg5, arg6, list, bitSet);
            bl2 = true;
        }
        random.setSeed(l);
        List<BakedQuad> list2 = arg2.getQuads(arg3, null, random);
        if (!list2.isEmpty()) {
            this.renderQuadsFlat(arg, arg3, arg4, -1, i, true, arg5, arg6, list2, bitSet);
            bl2 = true;
        }
        return bl2;
    }

    private void renderQuadsSmooth(BlockRenderView arg, BlockState arg2, BlockPos arg3, MatrixStack arg4, VertexConsumer arg5, List<BakedQuad> list, float[] fs, BitSet bitSet, AmbientOcclusionCalculator arg6, int i) {
        for (BakedQuad lv : list) {
            this.getQuadDimensions(arg, arg2, arg3, lv.getVertexData(), lv.getFace(), fs, bitSet);
            arg6.apply(arg, arg2, arg3, lv.getFace(), fs, bitSet, lv.hasShade());
            this.renderQuad(arg, arg2, arg3, arg5, arg4.peek(), lv, arg6.brightness[0], arg6.brightness[1], arg6.brightness[2], arg6.brightness[3], arg6.light[0], arg6.light[1], arg6.light[2], arg6.light[3], i);
        }
    }

    private void renderQuad(BlockRenderView arg, BlockState arg2, BlockPos arg3, VertexConsumer arg4, MatrixStack.Entry arg5, BakedQuad arg6, float f, float g, float h, float i, int j, int k, int l, int m, int n) {
        float u;
        float t;
        float s;
        if (arg6.hasColor()) {
            int o = this.colorMap.getColor(arg2, arg, arg3, arg6.getColorIndex());
            float p = (float)(o >> 16 & 0xFF) / 255.0f;
            float q = (float)(o >> 8 & 0xFF) / 255.0f;
            float r = (float)(o & 0xFF) / 255.0f;
        } else {
            s = 1.0f;
            t = 1.0f;
            u = 1.0f;
        }
        arg4.quad(arg5, arg6, new float[]{f, g, h, i}, s, t, u, new int[]{j, k, l, m}, n, true);
    }

    private void getQuadDimensions(BlockRenderView arg, BlockState arg2, BlockPos arg3, int[] is, Direction arg4, @Nullable float[] fs, BitSet bitSet) {
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (int l = 0; l < 4; ++l) {
            float m = Float.intBitsToFloat(is[l * 8]);
            float n = Float.intBitsToFloat(is[l * 8 + 1]);
            float o = Float.intBitsToFloat(is[l * 8 + 2]);
            f = Math.min(f, m);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, m);
            j = Math.max(j, n);
            k = Math.max(k, o);
        }
        if (fs != null) {
            fs[Direction.WEST.getId()] = f;
            fs[Direction.EAST.getId()] = i;
            fs[Direction.DOWN.getId()] = g;
            fs[Direction.UP.getId()] = j;
            fs[Direction.NORTH.getId()] = h;
            fs[Direction.SOUTH.getId()] = k;
            int p = Direction.values().length;
            fs[Direction.WEST.getId() + p] = 1.0f - f;
            fs[Direction.EAST.getId() + p] = 1.0f - i;
            fs[Direction.DOWN.getId() + p] = 1.0f - g;
            fs[Direction.UP.getId() + p] = 1.0f - j;
            fs[Direction.NORTH.getId() + p] = 1.0f - h;
            fs[Direction.SOUTH.getId() + p] = 1.0f - k;
        }
        float q = 1.0E-4f;
        float r = 0.9999f;
        switch (arg4) {
            case DOWN: {
                bitSet.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, g == j && (g < 1.0E-4f || arg2.isFullCube(arg, arg3)));
                break;
            }
            case UP: {
                bitSet.set(1, f >= 1.0E-4f || h >= 1.0E-4f || i <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, g == j && (j > 0.9999f || arg2.isFullCube(arg, arg3)));
                break;
            }
            case NORTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, h == k && (h < 1.0E-4f || arg2.isFullCube(arg, arg3)));
                break;
            }
            case SOUTH: {
                bitSet.set(1, f >= 1.0E-4f || g >= 1.0E-4f || i <= 0.9999f || j <= 0.9999f);
                bitSet.set(0, h == k && (k > 0.9999f || arg2.isFullCube(arg, arg3)));
                break;
            }
            case WEST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, f == i && (f < 1.0E-4f || arg2.isFullCube(arg, arg3)));
                break;
            }
            case EAST: {
                bitSet.set(1, g >= 1.0E-4f || h >= 1.0E-4f || j <= 0.9999f || k <= 0.9999f);
                bitSet.set(0, f == i && (i > 0.9999f || arg2.isFullCube(arg, arg3)));
            }
        }
    }

    private void renderQuadsFlat(BlockRenderView arg, BlockState arg2, BlockPos arg3, int i, int j, boolean bl, MatrixStack arg4, VertexConsumer arg5, List<BakedQuad> list, BitSet bitSet) {
        for (BakedQuad lv : list) {
            if (bl) {
                this.getQuadDimensions(arg, arg2, arg3, lv.getVertexData(), lv.getFace(), null, bitSet);
                BlockPos lv2 = bitSet.get(0) ? arg3.offset(lv.getFace()) : arg3;
                i = WorldRenderer.getLightmapCoordinates(arg, arg2, lv2);
            }
            float f = arg.getBrightness(lv.getFace(), lv.hasShade());
            this.renderQuad(arg, arg2, arg3, arg5, arg4.peek(), lv, f, f, f, f, i, i, i, i, j);
        }
    }

    public void render(MatrixStack.Entry arg, VertexConsumer arg2, @Nullable BlockState arg3, BakedModel arg4, float f, float g, float h, int i, int j) {
        Random random = new Random();
        long l = 42L;
        for (Direction lv : Direction.values()) {
            random.setSeed(42L);
            BlockModelRenderer.renderQuad(arg, arg2, f, g, h, arg4.getQuads(arg3, lv, random), i, j);
        }
        random.setSeed(42L);
        BlockModelRenderer.renderQuad(arg, arg2, f, g, h, arg4.getQuads(arg3, null, random), i, j);
    }

    private static void renderQuad(MatrixStack.Entry arg, VertexConsumer arg2, float f, float g, float h, List<BakedQuad> list, int i, int j) {
        for (BakedQuad lv : list) {
            float p;
            float o;
            float n;
            if (lv.hasColor()) {
                float k = MathHelper.clamp(f, 0.0f, 1.0f);
                float l = MathHelper.clamp(g, 0.0f, 1.0f);
                float m = MathHelper.clamp(h, 0.0f, 1.0f);
            } else {
                n = 1.0f;
                o = 1.0f;
                p = 1.0f;
            }
            arg2.quad(arg, lv, n, o, p, i, j);
        }
    }

    public static void enableBrightnessCache() {
        brightnessCache.get().enable();
    }

    public static void disableBrightnessCache() {
        brightnessCache.get().disable();
    }

    @Environment(value=EnvType.CLIENT)
    public static enum NeighborData {
        DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.WEST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.EAST, NeighborOrientation.SOUTH}),
        UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0f, true, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.SOUTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.EAST, NeighborOrientation.NORTH, NeighborOrientation.EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_EAST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.NORTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.WEST, NeighborOrientation.SOUTH, NeighborOrientation.WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_WEST, NeighborOrientation.SOUTH}),
        NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST}),
        SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_UP, NeighborOrientation.WEST, NeighborOrientation.UP, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_WEST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.WEST, NeighborOrientation.DOWN, NeighborOrientation.WEST}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_DOWN, NeighborOrientation.EAST, NeighborOrientation.DOWN, NeighborOrientation.EAST}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_EAST, NeighborOrientation.FLIP_UP, NeighborOrientation.EAST, NeighborOrientation.UP, NeighborOrientation.EAST}),
        WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.UP, NeighborOrientation.NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.DOWN, NeighborOrientation.SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH}),
        EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6f, true, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.DOWN, NeighborOrientation.SOUTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_DOWN, NeighborOrientation.NORTH, NeighborOrientation.FLIP_DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.FLIP_NORTH, NeighborOrientation.DOWN, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.NORTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.FLIP_NORTH, NeighborOrientation.UP, NeighborOrientation.NORTH}, new NeighborOrientation[]{NeighborOrientation.FLIP_UP, NeighborOrientation.SOUTH, NeighborOrientation.FLIP_UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.FLIP_SOUTH, NeighborOrientation.UP, NeighborOrientation.SOUTH});

        private final Direction[] faces;
        private final boolean nonCubicWeight;
        private final NeighborOrientation[] field_4192;
        private final NeighborOrientation[] field_4185;
        private final NeighborOrientation[] field_4180;
        private final NeighborOrientation[] field_4188;
        private static final NeighborData[] field_4190;

        private NeighborData(Direction[] args, float f, boolean bl, NeighborOrientation[] args2, NeighborOrientation[] args3, NeighborOrientation[] args4, NeighborOrientation[] args5) {
            this.faces = args;
            this.nonCubicWeight = bl;
            this.field_4192 = args2;
            this.field_4185 = args3;
            this.field_4180 = args4;
            this.field_4188 = args5;
        }

        public static NeighborData getData(Direction arg) {
            return field_4190[arg.getId()];
        }

        static {
            field_4190 = Util.make(new NeighborData[6], args -> {
                args[Direction.DOWN.getId()] = DOWN;
                args[Direction.UP.getId()] = UP;
                args[Direction.NORTH.getId()] = NORTH;
                args[Direction.SOUTH.getId()] = SOUTH;
                args[Direction.WEST.getId()] = WEST;
                args[Direction.EAST.getId()] = EAST;
            });
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum NeighborOrientation {
        DOWN(Direction.DOWN, false),
        UP(Direction.UP, false),
        NORTH(Direction.NORTH, false),
        SOUTH(Direction.SOUTH, false),
        WEST(Direction.WEST, false),
        EAST(Direction.EAST, false),
        FLIP_DOWN(Direction.DOWN, true),
        FLIP_UP(Direction.UP, true),
        FLIP_NORTH(Direction.NORTH, true),
        FLIP_SOUTH(Direction.SOUTH, true),
        FLIP_WEST(Direction.WEST, true),
        FLIP_EAST(Direction.EAST, true);

        private final int shape;

        private NeighborOrientation(Direction arg, boolean bl) {
            this.shape = arg.getId() + (bl ? Direction.values().length : 0);
        }
    }

    @Environment(value=EnvType.CLIENT)
    class AmbientOcclusionCalculator {
        private final float[] brightness = new float[4];
        private final int[] light = new int[4];

        public void apply(BlockRenderView arg, BlockState arg2, BlockPos arg3, Direction arg4, float[] fs, BitSet bitSet, boolean bl) {
            int ac;
            float ab;
            int y;
            float x;
            int u;
            float t;
            int q;
            float p;
            boolean bl5;
            BlockPos lv = bitSet.get(0) ? arg3.offset(arg4) : arg3;
            NeighborData lv2 = NeighborData.getData(arg4);
            BlockPos.Mutable lv3 = new BlockPos.Mutable();
            BrightnessCache lv4 = (BrightnessCache)brightnessCache.get();
            lv3.set(lv, lv2.faces[0]);
            BlockState lv5 = arg.getBlockState(lv3);
            int i = lv4.getInt(lv5, arg, lv3);
            float f = lv4.getFloat(lv5, arg, lv3);
            lv3.set(lv, lv2.faces[1]);
            BlockState lv6 = arg.getBlockState(lv3);
            int j = lv4.getInt(lv6, arg, lv3);
            float g = lv4.getFloat(lv6, arg, lv3);
            lv3.set(lv, lv2.faces[2]);
            BlockState lv7 = arg.getBlockState(lv3);
            int k = lv4.getInt(lv7, arg, lv3);
            float h = lv4.getFloat(lv7, arg, lv3);
            lv3.set(lv, lv2.faces[3]);
            BlockState lv8 = arg.getBlockState(lv3);
            int l = lv4.getInt(lv8, arg, lv3);
            float m = lv4.getFloat(lv8, arg, lv3);
            lv3.set(lv, lv2.faces[0]).move(arg4);
            boolean bl2 = arg.getBlockState(lv3).getOpacity(arg, lv3) == 0;
            lv3.set(lv, lv2.faces[1]).move(arg4);
            boolean bl3 = arg.getBlockState(lv3).getOpacity(arg, lv3) == 0;
            lv3.set(lv, lv2.faces[2]).move(arg4);
            boolean bl4 = arg.getBlockState(lv3).getOpacity(arg, lv3) == 0;
            lv3.set(lv, lv2.faces[3]).move(arg4);
            boolean bl6 = bl5 = arg.getBlockState(lv3).getOpacity(arg, lv3) == 0;
            if (bl4 || bl2) {
                lv3.set(lv, lv2.faces[0]).move(lv2.faces[2]);
                BlockState lv9 = arg.getBlockState(lv3);
                float n = lv4.getFloat(lv9, arg, lv3);
                int o = lv4.getInt(lv9, arg, lv3);
            } else {
                p = f;
                q = i;
            }
            if (bl5 || bl2) {
                lv3.set(lv, lv2.faces[0]).move(lv2.faces[3]);
                BlockState lv10 = arg.getBlockState(lv3);
                float r = lv4.getFloat(lv10, arg, lv3);
                int s = lv4.getInt(lv10, arg, lv3);
            } else {
                t = f;
                u = i;
            }
            if (bl4 || bl3) {
                lv3.set(lv, lv2.faces[1]).move(lv2.faces[2]);
                BlockState lv11 = arg.getBlockState(lv3);
                float v = lv4.getFloat(lv11, arg, lv3);
                int w = lv4.getInt(lv11, arg, lv3);
            } else {
                x = f;
                y = i;
            }
            if (bl5 || bl3) {
                lv3.set(lv, lv2.faces[1]).move(lv2.faces[3]);
                BlockState lv12 = arg.getBlockState(lv3);
                float z = lv4.getFloat(lv12, arg, lv3);
                int aa = lv4.getInt(lv12, arg, lv3);
            } else {
                ab = f;
                ac = i;
            }
            int ad = lv4.getInt(arg2, arg, arg3);
            lv3.set(arg3, arg4);
            BlockState lv13 = arg.getBlockState(lv3);
            if (bitSet.get(0) || !lv13.isOpaqueFullCube(arg, lv3)) {
                ad = lv4.getInt(lv13, arg, lv3);
            }
            float ae = bitSet.get(0) ? lv4.getFloat(arg.getBlockState(lv), arg, lv) : lv4.getFloat(arg.getBlockState(arg3), arg, arg3);
            Translation lv14 = Translation.getTranslations(arg4);
            if (!bitSet.get(1) || !lv2.nonCubicWeight) {
                float af = (m + f + t + ae) * 0.25f;
                float ag = (h + f + p + ae) * 0.25f;
                float ah = (h + g + x + ae) * 0.25f;
                float ai = (m + g + ab + ae) * 0.25f;
                this.light[((Translation)lv14).firstCorner] = this.getAmbientOcclusionBrightness(l, i, u, ad);
                this.light[((Translation)lv14).secondCorner] = this.getAmbientOcclusionBrightness(k, i, q, ad);
                this.light[((Translation)lv14).thirdCorner] = this.getAmbientOcclusionBrightness(k, j, y, ad);
                this.light[((Translation)lv14).fourthCorner] = this.getAmbientOcclusionBrightness(l, j, ac, ad);
                this.brightness[((Translation)lv14).firstCorner] = af;
                this.brightness[((Translation)lv14).secondCorner] = ag;
                this.brightness[((Translation)lv14).thirdCorner] = ah;
                this.brightness[((Translation)lv14).fourthCorner] = ai;
            } else {
                float aj = (m + f + t + ae) * 0.25f;
                float ak = (h + f + p + ae) * 0.25f;
                float al = (h + g + x + ae) * 0.25f;
                float am = (m + g + ab + ae) * 0.25f;
                float an = fs[lv2.field_4192[0].shape] * fs[lv2.field_4192[1].shape];
                float ao = fs[lv2.field_4192[2].shape] * fs[lv2.field_4192[3].shape];
                float ap = fs[lv2.field_4192[4].shape] * fs[lv2.field_4192[5].shape];
                float aq = fs[lv2.field_4192[6].shape] * fs[lv2.field_4192[7].shape];
                float ar = fs[lv2.field_4185[0].shape] * fs[lv2.field_4185[1].shape];
                float as = fs[lv2.field_4185[2].shape] * fs[lv2.field_4185[3].shape];
                float at = fs[lv2.field_4185[4].shape] * fs[lv2.field_4185[5].shape];
                float au = fs[lv2.field_4185[6].shape] * fs[lv2.field_4185[7].shape];
                float av = fs[lv2.field_4180[0].shape] * fs[lv2.field_4180[1].shape];
                float aw = fs[lv2.field_4180[2].shape] * fs[lv2.field_4180[3].shape];
                float ax = fs[lv2.field_4180[4].shape] * fs[lv2.field_4180[5].shape];
                float ay = fs[lv2.field_4180[6].shape] * fs[lv2.field_4180[7].shape];
                float az = fs[lv2.field_4188[0].shape] * fs[lv2.field_4188[1].shape];
                float ba = fs[lv2.field_4188[2].shape] * fs[lv2.field_4188[3].shape];
                float bb = fs[lv2.field_4188[4].shape] * fs[lv2.field_4188[5].shape];
                float bc = fs[lv2.field_4188[6].shape] * fs[lv2.field_4188[7].shape];
                this.brightness[((Translation)lv14).firstCorner] = aj * an + ak * ao + al * ap + am * aq;
                this.brightness[((Translation)lv14).secondCorner] = aj * ar + ak * as + al * at + am * au;
                this.brightness[((Translation)lv14).thirdCorner] = aj * av + ak * aw + al * ax + am * ay;
                this.brightness[((Translation)lv14).fourthCorner] = aj * az + ak * ba + al * bb + am * bc;
                int bd = this.getAmbientOcclusionBrightness(l, i, u, ad);
                int be = this.getAmbientOcclusionBrightness(k, i, q, ad);
                int bf = this.getAmbientOcclusionBrightness(k, j, y, ad);
                int bg = this.getAmbientOcclusionBrightness(l, j, ac, ad);
                this.light[((Translation)lv14).firstCorner] = this.getBrightness(bd, be, bf, bg, an, ao, ap, aq);
                this.light[((Translation)lv14).secondCorner] = this.getBrightness(bd, be, bf, bg, ar, as, at, au);
                this.light[((Translation)lv14).thirdCorner] = this.getBrightness(bd, be, bf, bg, av, aw, ax, ay);
                this.light[((Translation)lv14).fourthCorner] = this.getBrightness(bd, be, bf, bg, az, ba, bb, bc);
            }
            float bh = arg.getBrightness(arg4, bl);
            int bi = 0;
            while (bi < this.brightness.length) {
                int n = bi++;
                this.brightness[n] = this.brightness[n] * bh;
            }
        }

        private int getAmbientOcclusionBrightness(int i, int j, int k, int l) {
            if (i == 0) {
                i = l;
            }
            if (j == 0) {
                j = l;
            }
            if (k == 0) {
                k = l;
            }
            return i + j + k + l >> 2 & 0xFF00FF;
        }

        private int getBrightness(int i, int j, int k, int l, float f, float g, float h, float m) {
            int n = (int)((float)(i >> 16 & 0xFF) * f + (float)(j >> 16 & 0xFF) * g + (float)(k >> 16 & 0xFF) * h + (float)(l >> 16 & 0xFF) * m) & 0xFF;
            int o = (int)((float)(i & 0xFF) * f + (float)(j & 0xFF) * g + (float)(k & 0xFF) * h + (float)(l & 0xFF) * m) & 0xFF;
            return n << 16 | o;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class BrightnessCache {
        private boolean enabled;
        private final Long2IntLinkedOpenHashMap intCache = Util.make(() -> {
            Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int i) {
                }
            };
            long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return long2IntLinkedOpenHashMap;
        });
        private final Long2FloatLinkedOpenHashMap floatCache = Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25f){

                protected void rehash(int i) {
                }
            };
            long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
            return long2FloatLinkedOpenHashMap;
        });

        private BrightnessCache() {
        }

        public void enable() {
            this.enabled = true;
        }

        public void disable() {
            this.enabled = false;
            this.intCache.clear();
            this.floatCache.clear();
        }

        public int getInt(BlockState arg, BlockRenderView arg2, BlockPos arg3) {
            int i;
            long l = arg3.asLong();
            if (this.enabled && (i = this.intCache.get(l)) != Integer.MAX_VALUE) {
                return i;
            }
            int j = WorldRenderer.getLightmapCoordinates(arg2, arg, arg3);
            if (this.enabled) {
                if (this.intCache.size() == 100) {
                    this.intCache.removeFirstInt();
                }
                this.intCache.put(l, j);
            }
            return j;
        }

        public float getFloat(BlockState arg, BlockRenderView arg2, BlockPos arg3) {
            float f;
            long l = arg3.asLong();
            if (this.enabled && !Float.isNaN(f = this.floatCache.get(l))) {
                return f;
            }
            float g = arg.getAmbientOcclusionLightLevel(arg2, arg3);
            if (this.enabled) {
                if (this.floatCache.size() == 100) {
                    this.floatCache.removeFirstFloat();
                }
                this.floatCache.put(l, g);
            }
            return g;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Translation {
        DOWN(0, 1, 2, 3),
        UP(2, 3, 0, 1),
        NORTH(3, 0, 1, 2),
        SOUTH(0, 1, 2, 3),
        WEST(3, 0, 1, 2),
        EAST(1, 2, 3, 0);

        private final int firstCorner;
        private final int secondCorner;
        private final int thirdCorner;
        private final int fourthCorner;
        private static final Translation[] VALUES;

        private Translation(int j, int k, int l, int m) {
            this.firstCorner = j;
            this.secondCorner = k;
            this.thirdCorner = l;
            this.fourthCorner = m;
        }

        public static Translation getTranslations(Direction arg) {
            return VALUES[arg.getId()];
        }

        static {
            VALUES = Util.make(new Translation[6], args -> {
                args[Direction.DOWN.getId()] = DOWN;
                args[Direction.UP.getId()] = UP;
                args[Direction.NORTH.getId()] = NORTH;
                args[Direction.SOUTH.getId()] = SOUTH;
                args[Direction.WEST.getId()] = WEST;
                args[Direction.EAST.getId()] = EAST;
            });
        }
    }
}

