/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

@Environment(value=EnvType.CLIENT)
public class FluidRenderer {
    private final Sprite[] lavaSprites = new Sprite[2];
    private final Sprite[] waterSprites = new Sprite[2];
    private Sprite waterOverlaySprite;

    protected void onResourceReload() {
        this.lavaSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getSprite();
        this.lavaSprites[1] = ModelLoader.LAVA_FLOW.getSprite();
        this.waterSprites[0] = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getSprite();
        this.waterSprites[1] = ModelLoader.WATER_FLOW.getSprite();
        this.waterOverlaySprite = ModelLoader.WATER_OVERLAY.getSprite();
    }

    private static boolean isSameFluid(BlockView arg, BlockPos arg2, Direction arg3, FluidState arg4) {
        BlockPos lv = arg2.offset(arg3);
        FluidState lv2 = arg.getFluidState(lv);
        return lv2.getFluid().matchesType(arg4.getFluid());
    }

    private static boolean isSideCovered(BlockView arg, BlockPos arg2, Direction arg3, float f) {
        BlockPos lv = arg2.offset(arg3);
        BlockState lv2 = arg.getBlockState(lv);
        if (lv2.isOpaque()) {
            VoxelShape lv3 = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, f, 1.0);
            VoxelShape lv4 = lv2.getCullingShape(arg, lv);
            return VoxelShapes.isSideCovered(lv3, lv4, arg3);
        }
        return false;
    }

    public boolean render(BlockRenderView arg, BlockPos arg2, VertexConsumer arg3, FluidState arg4) {
        float t;
        boolean bl7;
        boolean bl = arg4.matches(FluidTags.LAVA);
        Sprite[] lvs = bl ? this.lavaSprites : this.waterSprites;
        int i = bl ? 0xFFFFFF : BiomeColors.getWaterColor(arg, arg2);
        float f = (float)(i >> 16 & 0xFF) / 255.0f;
        float g = (float)(i >> 8 & 0xFF) / 255.0f;
        float h = (float)(i & 0xFF) / 255.0f;
        boolean bl2 = !FluidRenderer.isSameFluid(arg, arg2, Direction.UP, arg4);
        boolean bl3 = !FluidRenderer.isSameFluid(arg, arg2, Direction.DOWN, arg4) && !FluidRenderer.isSideCovered(arg, arg2, Direction.DOWN, 0.8888889f);
        boolean bl4 = !FluidRenderer.isSameFluid(arg, arg2, Direction.NORTH, arg4);
        boolean bl5 = !FluidRenderer.isSameFluid(arg, arg2, Direction.SOUTH, arg4);
        boolean bl6 = !FluidRenderer.isSameFluid(arg, arg2, Direction.WEST, arg4);
        boolean bl8 = bl7 = !FluidRenderer.isSameFluid(arg, arg2, Direction.EAST, arg4);
        if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
            return false;
        }
        boolean bl82 = false;
        float j = arg.getBrightness(Direction.DOWN, true);
        float k = arg.getBrightness(Direction.UP, true);
        float l = arg.getBrightness(Direction.NORTH, true);
        float m = arg.getBrightness(Direction.WEST, true);
        float n = this.getNorthWestCornerFluidHeight(arg, arg2, arg4.getFluid());
        float o = this.getNorthWestCornerFluidHeight(arg, arg2.south(), arg4.getFluid());
        float p = this.getNorthWestCornerFluidHeight(arg, arg2.east().south(), arg4.getFluid());
        float q = this.getNorthWestCornerFluidHeight(arg, arg2.east(), arg4.getFluid());
        double d = arg2.getX() & 0xF;
        double e = arg2.getY() & 0xF;
        double r = arg2.getZ() & 0xF;
        float s = 0.001f;
        float f2 = t = bl3 ? 0.001f : 0.0f;
        if (bl2 && !FluidRenderer.isSideCovered(arg, arg2, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
            float an;
            float am;
            float al;
            float ak;
            float aj;
            float ai;
            float ah;
            float ag;
            bl82 = true;
            n -= 0.001f;
            o -= 0.001f;
            p -= 0.001f;
            q -= 0.001f;
            Vec3d lv = arg4.getVelocity(arg, arg2);
            if (lv.x == 0.0 && lv.z == 0.0) {
                Sprite lv2 = lvs[0];
                float u = lv2.getFrameU(0.0);
                float v = lv2.getFrameV(0.0);
                float w = u;
                float x = lv2.getFrameV(16.0);
                float y = lv2.getFrameU(16.0);
                float z = x;
                float aa = y;
                float ab = v;
            } else {
                Sprite lv3 = lvs[1];
                float ac = (float)MathHelper.atan2(lv.z, lv.x) - 1.5707964f;
                float ad = MathHelper.sin(ac) * 0.25f;
                float ae = MathHelper.cos(ac) * 0.25f;
                float af = 8.0f;
                ag = lv3.getFrameU(8.0f + (-ae - ad) * 16.0f);
                ah = lv3.getFrameV(8.0f + (-ae + ad) * 16.0f);
                ai = lv3.getFrameU(8.0f + (-ae + ad) * 16.0f);
                aj = lv3.getFrameV(8.0f + (ae + ad) * 16.0f);
                ak = lv3.getFrameU(8.0f + (ae + ad) * 16.0f);
                al = lv3.getFrameV(8.0f + (ae - ad) * 16.0f);
                am = lv3.getFrameU(8.0f + (ae - ad) * 16.0f);
                an = lv3.getFrameV(8.0f + (-ae - ad) * 16.0f);
            }
            void ao = (ag + ai + ak + am) / 4.0f;
            void ap = (ah + aj + al + an) / 4.0f;
            float aq = (float)lvs[0].getWidth() / (lvs[0].getMaxU() - lvs[0].getMinU());
            float ar = (float)lvs[0].getHeight() / (lvs[0].getMaxV() - lvs[0].getMinV());
            float as = 4.0f / Math.max(ar, aq);
            ag = MathHelper.lerp(as, ag, (float)ao);
            ai = MathHelper.lerp(as, ai, (float)ao);
            ak = MathHelper.lerp(as, ak, (float)ao);
            am = MathHelper.lerp(as, am, (float)ao);
            ah = MathHelper.lerp(as, ah, (float)ap);
            aj = MathHelper.lerp(as, aj, (float)ap);
            al = MathHelper.lerp(as, al, (float)ap);
            an = MathHelper.lerp(as, an, (float)ap);
            int at = this.getLight(arg, arg2);
            float au = k * f;
            float av = k * g;
            float aw = k * h;
            this.vertex(arg3, d + 0.0, e + (double)n, r + 0.0, au, av, aw, ag, ah, at);
            this.vertex(arg3, d + 0.0, e + (double)o, r + 1.0, au, av, aw, ai, aj, at);
            this.vertex(arg3, d + 1.0, e + (double)p, r + 1.0, au, av, aw, ak, al, at);
            this.vertex(arg3, d + 1.0, e + (double)q, r + 0.0, au, av, aw, am, an, at);
            if (arg4.method_15756(arg, arg2.up())) {
                this.vertex(arg3, d + 0.0, e + (double)n, r + 0.0, au, av, aw, ag, ah, at);
                this.vertex(arg3, d + 1.0, e + (double)q, r + 0.0, au, av, aw, am, an, at);
                this.vertex(arg3, d + 1.0, e + (double)p, r + 1.0, au, av, aw, ak, al, at);
                this.vertex(arg3, d + 0.0, e + (double)o, r + 1.0, au, av, aw, ai, aj, at);
            }
        }
        if (bl3) {
            float ax = lvs[0].getMinU();
            float ay = lvs[0].getMaxU();
            float az = lvs[0].getMinV();
            float ba = lvs[0].getMaxV();
            int bb = this.getLight(arg, arg2.down());
            float bc = j * f;
            float bd = j * g;
            float be = j * h;
            this.vertex(arg3, d, e + (double)t, r + 1.0, bc, bd, be, ax, ba, bb);
            this.vertex(arg3, d, e + (double)t, r, bc, bd, be, ax, az, bb);
            this.vertex(arg3, d + 1.0, e + (double)t, r, bc, bd, be, ay, az, bb);
            this.vertex(arg3, d + 1.0, e + (double)t, r + 1.0, bc, bd, be, ay, ba, bb);
            bl82 = true;
        }
        for (int bf = 0; bf < 4; ++bf) {
            Block lv10;
            boolean bl12;
            Direction lv7;
            double ce;
            double cd;
            double cc;
            double cb;
            float ca;
            float bz;
            if (bf == 0) {
                float bg = n;
                float bh = q;
                double bi = d;
                double bj = d + 1.0;
                double bk = r + (double)0.001f;
                double bm = r + (double)0.001f;
                Direction lv4 = Direction.NORTH;
                boolean bl9 = bl4;
            } else if (bf == 1) {
                float bn = p;
                float bo = o;
                double bp = d + 1.0;
                double bq = d;
                double br = r + 1.0 - (double)0.001f;
                double bs = r + 1.0 - (double)0.001f;
                Direction lv5 = Direction.SOUTH;
                boolean bl10 = bl5;
            } else if (bf == 2) {
                float bt = o;
                float bu = n;
                double bv = d + (double)0.001f;
                double bw = d + (double)0.001f;
                double bx = r + 1.0;
                double by = r;
                Direction lv6 = Direction.WEST;
                boolean bl11 = bl6;
            } else {
                bz = q;
                ca = p;
                cb = d + 1.0 - (double)0.001f;
                cc = d + 1.0 - (double)0.001f;
                cd = r;
                ce = r + 1.0;
                lv7 = Direction.EAST;
                bl12 = bl7;
            }
            if (!bl12 || FluidRenderer.isSideCovered(arg, arg2, lv7, Math.max(bz, ca))) continue;
            bl82 = true;
            BlockPos lv8 = arg2.offset(lv7);
            Sprite lv9 = lvs[1];
            if (!bl && ((lv10 = arg.getBlockState(lv8).getBlock()) == Blocks.GLASS || lv10 instanceof StainedGlassBlock)) {
                lv9 = this.waterOverlaySprite;
            }
            float cf = lv9.getFrameU(0.0);
            float cg = lv9.getFrameU(8.0);
            float ch = lv9.getFrameV((1.0f - bz) * 16.0f * 0.5f);
            float ci = lv9.getFrameV((1.0f - ca) * 16.0f * 0.5f);
            float cj = lv9.getFrameV(8.0);
            int ck = this.getLight(arg, lv8);
            float cl = bf < 2 ? l : m;
            float cm = k * cl * f;
            float cn = k * cl * g;
            float co = k * cl * h;
            this.vertex(arg3, cb, e + (double)bz, cd, cm, cn, co, cf, ch, ck);
            this.vertex(arg3, cc, e + (double)ca, ce, cm, cn, co, cg, ci, ck);
            this.vertex(arg3, cc, e + (double)t, ce, cm, cn, co, cg, cj, ck);
            this.vertex(arg3, cb, e + (double)t, cd, cm, cn, co, cf, cj, ck);
            if (lv9 == this.waterOverlaySprite) continue;
            this.vertex(arg3, cb, e + (double)t, cd, cm, cn, co, cf, cj, ck);
            this.vertex(arg3, cc, e + (double)t, ce, cm, cn, co, cg, cj, ck);
            this.vertex(arg3, cc, e + (double)ca, ce, cm, cn, co, cg, ci, ck);
            this.vertex(arg3, cb, e + (double)bz, cd, cm, cn, co, cf, ch, ck);
        }
        return bl82;
    }

    private void vertex(VertexConsumer arg, double d, double e, double f, float g, float h, float i, float j, float k, int l) {
        arg.vertex(d, e, f).color(g, h, i, 1.0f).texture(j, k).light(l).normal(0.0f, 1.0f, 0.0f).next();
    }

    private int getLight(BlockRenderView arg, BlockPos arg2) {
        int i = WorldRenderer.getLightmapCoordinates(arg, arg2);
        int j = WorldRenderer.getLightmapCoordinates(arg, arg2.up());
        int k = i & 0xFF;
        int l = j & 0xFF;
        int m = i >> 16 & 0xFF;
        int n = j >> 16 & 0xFF;
        return (k > l ? k : l) | (m > n ? m : n) << 16;
    }

    private float getNorthWestCornerFluidHeight(BlockView arg, BlockPos arg2, Fluid arg3) {
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < 4; ++j) {
            BlockPos lv = arg2.add(-(j & 1), 0, -(j >> 1 & 1));
            if (arg.getFluidState(lv.up()).getFluid().matchesType(arg3)) {
                return 1.0f;
            }
            FluidState lv2 = arg.getFluidState(lv);
            if (lv2.getFluid().matchesType(arg3)) {
                float g = lv2.getHeight(arg, lv);
                if (g >= 0.8f) {
                    f += g * 10.0f;
                    i += 10;
                    continue;
                }
                f += g;
                ++i;
                continue;
            }
            if (arg.getBlockState(lv).getMaterial().isSolid()) continue;
            ++i;
        }
        return f / (float)i;
    }
}

