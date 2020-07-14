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
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
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

    private static boolean isSameFluid(BlockView world, BlockPos pos, Direction side, FluidState state) {
        BlockPos lv = pos.offset(side);
        FluidState lv2 = world.getFluidState(lv);
        return lv2.getFluid().matchesType(state.getFluid());
    }

    private static boolean method_29710(BlockView arg, Direction arg2, float f, BlockPos arg3, BlockState arg4) {
        if (arg4.isOpaque()) {
            VoxelShape lv = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, f, 1.0);
            VoxelShape lv2 = arg4.getCullingShape(arg, arg3);
            return VoxelShapes.isSideCovered(lv, lv2, arg2);
        }
        return false;
    }

    private static boolean isSideCovered(BlockView world, BlockPos pos, Direction direction, float maxDeviation) {
        BlockPos lv = pos.offset(direction);
        BlockState lv2 = world.getBlockState(lv);
        return FluidRenderer.method_29710(world, direction, maxDeviation, lv, lv2);
    }

    private static boolean method_29709(BlockView arg, BlockPos arg2, BlockState arg3, Direction arg4) {
        return FluidRenderer.method_29710(arg, arg4.getOpposite(), 1.0f, arg2, arg3);
    }

    public static boolean method_29708(BlockRenderView arg, BlockPos arg2, FluidState arg3, BlockState arg4, Direction arg5) {
        return !FluidRenderer.method_29709(arg, arg2, arg4, arg5) && !FluidRenderer.isSameFluid(arg, arg2, arg5, arg3);
    }

    public boolean render(BlockRenderView world, BlockPos pos, VertexConsumer vertexConsumer, FluidState state) {
        float t;
        boolean bl = state.isIn(FluidTags.LAVA);
        Sprite[] lvs = bl ? this.lavaSprites : this.waterSprites;
        BlockState lv = world.getBlockState(pos);
        int i = bl ? 0xFFFFFF : BiomeColors.getWaterColor(world, pos);
        float f = (float)(i >> 16 & 0xFF) / 255.0f;
        float g = (float)(i >> 8 & 0xFF) / 255.0f;
        float h = (float)(i & 0xFF) / 255.0f;
        boolean bl2 = !FluidRenderer.isSameFluid(world, pos, Direction.UP, state);
        boolean bl3 = FluidRenderer.method_29708(world, pos, state, lv, Direction.DOWN) && !FluidRenderer.isSideCovered(world, pos, Direction.DOWN, 0.8888889f);
        boolean bl4 = FluidRenderer.method_29708(world, pos, state, lv, Direction.NORTH);
        boolean bl5 = FluidRenderer.method_29708(world, pos, state, lv, Direction.SOUTH);
        boolean bl6 = FluidRenderer.method_29708(world, pos, state, lv, Direction.WEST);
        boolean bl7 = FluidRenderer.method_29708(world, pos, state, lv, Direction.EAST);
        if (!(bl2 || bl3 || bl7 || bl6 || bl4 || bl5)) {
            return false;
        }
        boolean bl8 = false;
        float j = world.getBrightness(Direction.DOWN, true);
        float k = world.getBrightness(Direction.UP, true);
        float l = world.getBrightness(Direction.NORTH, true);
        float m = world.getBrightness(Direction.WEST, true);
        float n = this.getNorthWestCornerFluidHeight(world, pos, state.getFluid());
        float o = this.getNorthWestCornerFluidHeight(world, pos.south(), state.getFluid());
        float p = this.getNorthWestCornerFluidHeight(world, pos.east().south(), state.getFluid());
        float q = this.getNorthWestCornerFluidHeight(world, pos.east(), state.getFluid());
        double d = pos.getX() & 0xF;
        double e = pos.getY() & 0xF;
        double r = pos.getZ() & 0xF;
        float s = 0.001f;
        float f2 = t = bl3 ? 0.001f : 0.0f;
        if (bl2 && !FluidRenderer.isSideCovered(world, pos, Direction.UP, Math.min(Math.min(n, o), Math.min(p, q)))) {
            float an;
            float am;
            float al;
            float ak;
            float aj;
            float ai;
            float ah;
            float ag;
            bl8 = true;
            n -= 0.001f;
            o -= 0.001f;
            p -= 0.001f;
            q -= 0.001f;
            Vec3d lv2 = state.getVelocity(world, pos);
            if (lv2.x == 0.0 && lv2.z == 0.0) {
                Sprite lv3 = lvs[0];
                float u = lv3.getFrameU(0.0);
                float v = lv3.getFrameV(0.0);
                float w = u;
                float x = lv3.getFrameV(16.0);
                float y = lv3.getFrameU(16.0);
                float z = x;
                float aa = y;
                float ab = v;
            } else {
                Sprite lv4 = lvs[1];
                float ac = (float)MathHelper.atan2(lv2.z, lv2.x) - 1.5707964f;
                float ad = MathHelper.sin(ac) * 0.25f;
                float ae = MathHelper.cos(ac) * 0.25f;
                float af = 8.0f;
                ag = lv4.getFrameU(8.0f + (-ae - ad) * 16.0f);
                ah = lv4.getFrameV(8.0f + (-ae + ad) * 16.0f);
                ai = lv4.getFrameU(8.0f + (-ae + ad) * 16.0f);
                aj = lv4.getFrameV(8.0f + (ae + ad) * 16.0f);
                ak = lv4.getFrameU(8.0f + (ae + ad) * 16.0f);
                al = lv4.getFrameV(8.0f + (ae - ad) * 16.0f);
                am = lv4.getFrameU(8.0f + (ae - ad) * 16.0f);
                an = lv4.getFrameV(8.0f + (-ae - ad) * 16.0f);
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
            int at = this.getLight(world, pos);
            float au = k * f;
            float av = k * g;
            float aw = k * h;
            this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, au, av, aw, ag, ah, at);
            this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, au, av, aw, ai, aj, at);
            this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, au, av, aw, ak, al, at);
            this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, au, av, aw, am, an, at);
            if (state.method_15756(world, pos.up())) {
                this.vertex(vertexConsumer, d + 0.0, e + (double)n, r + 0.0, au, av, aw, ag, ah, at);
                this.vertex(vertexConsumer, d + 1.0, e + (double)q, r + 0.0, au, av, aw, am, an, at);
                this.vertex(vertexConsumer, d + 1.0, e + (double)p, r + 1.0, au, av, aw, ak, al, at);
                this.vertex(vertexConsumer, d + 0.0, e + (double)o, r + 1.0, au, av, aw, ai, aj, at);
            }
        }
        if (bl3) {
            float ax = lvs[0].getMinU();
            float ay = lvs[0].getMaxU();
            float az = lvs[0].getMinV();
            float ba = lvs[0].getMaxV();
            int bb = this.getLight(world, pos.down());
            float bc = j * f;
            float bd = j * g;
            float be = j * h;
            this.vertex(vertexConsumer, d, e + (double)t, r + 1.0, bc, bd, be, ax, ba, bb);
            this.vertex(vertexConsumer, d, e + (double)t, r, bc, bd, be, ax, az, bb);
            this.vertex(vertexConsumer, d + 1.0, e + (double)t, r, bc, bd, be, ay, az, bb);
            this.vertex(vertexConsumer, d + 1.0, e + (double)t, r + 1.0, bc, bd, be, ay, ba, bb);
            bl8 = true;
        }
        for (int bf = 0; bf < 4; ++bf) {
            Block lv11;
            boolean bl12;
            Direction lv8;
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
                Direction lv5 = Direction.NORTH;
                boolean bl9 = bl4;
            } else if (bf == 1) {
                float bn = p;
                float bo = o;
                double bp = d + 1.0;
                double bq = d;
                double br = r + 1.0 - (double)0.001f;
                double bs = r + 1.0 - (double)0.001f;
                Direction lv6 = Direction.SOUTH;
                boolean bl10 = bl5;
            } else if (bf == 2) {
                float bt = o;
                float bu = n;
                double bv = d + (double)0.001f;
                double bw = d + (double)0.001f;
                double bx = r + 1.0;
                double by = r;
                Direction lv7 = Direction.WEST;
                boolean bl11 = bl6;
            } else {
                bz = q;
                ca = p;
                cb = d + 1.0 - (double)0.001f;
                cc = d + 1.0 - (double)0.001f;
                cd = r;
                ce = r + 1.0;
                lv8 = Direction.EAST;
                bl12 = bl7;
            }
            if (!bl12 || FluidRenderer.isSideCovered(world, pos, lv8, Math.max(bz, ca))) continue;
            bl8 = true;
            BlockPos lv9 = pos.offset(lv8);
            Sprite lv10 = lvs[1];
            if (!bl && ((lv11 = world.getBlockState(lv9).getBlock()) instanceof TransparentBlock || lv11 instanceof LeavesBlock)) {
                lv10 = this.waterOverlaySprite;
            }
            float cf = lv10.getFrameU(0.0);
            float cg = lv10.getFrameU(8.0);
            float ch = lv10.getFrameV((1.0f - bz) * 16.0f * 0.5f);
            float ci = lv10.getFrameV((1.0f - ca) * 16.0f * 0.5f);
            float cj = lv10.getFrameV(8.0);
            int ck = this.getLight(world, lv9);
            float cl = bf < 2 ? l : m;
            float cm = k * cl * f;
            float cn = k * cl * g;
            float co = k * cl * h;
            this.vertex(vertexConsumer, cb, e + (double)bz, cd, cm, cn, co, cf, ch, ck);
            this.vertex(vertexConsumer, cc, e + (double)ca, ce, cm, cn, co, cg, ci, ck);
            this.vertex(vertexConsumer, cc, e + (double)t, ce, cm, cn, co, cg, cj, ck);
            this.vertex(vertexConsumer, cb, e + (double)t, cd, cm, cn, co, cf, cj, ck);
            if (lv10 == this.waterOverlaySprite) continue;
            this.vertex(vertexConsumer, cb, e + (double)t, cd, cm, cn, co, cf, cj, ck);
            this.vertex(vertexConsumer, cc, e + (double)t, ce, cm, cn, co, cg, cj, ck);
            this.vertex(vertexConsumer, cc, e + (double)ca, ce, cm, cn, co, cg, ci, ck);
            this.vertex(vertexConsumer, cb, e + (double)bz, cd, cm, cn, co, cf, ch, ck);
        }
        return bl8;
    }

    private void vertex(VertexConsumer vertexConsumer, double x, double y, double z, float red, float green, float blue, float u, float v, int light) {
        vertexConsumer.vertex(x, y, z).color(red, green, blue, 1.0f).texture(u, v).light(light).normal(0.0f, 1.0f, 0.0f).next();
    }

    private int getLight(BlockRenderView world, BlockPos pos) {
        int i = WorldRenderer.getLightmapCoordinates(world, pos);
        int j = WorldRenderer.getLightmapCoordinates(world, pos.up());
        int k = i & 0xFF;
        int l = j & 0xFF;
        int m = i >> 16 & 0xFF;
        int n = j >> 16 & 0xFF;
        return (k > l ? k : l) | (m > n ? m : n) << 16;
    }

    private float getNorthWestCornerFluidHeight(BlockView world, BlockPos pos, Fluid fluid) {
        int i = 0;
        float f = 0.0f;
        for (int j = 0; j < 4; ++j) {
            BlockPos lv = pos.add(-(j & 1), 0, -(j >> 1 & 1));
            if (world.getFluidState(lv.up()).getFluid().matchesType(fluid)) {
                return 1.0f;
            }
            FluidState lv2 = world.getFluidState(lv);
            if (lv2.getFluid().matchesType(fluid)) {
                float g = lv2.getHeight(world, lv);
                if (g >= 0.8f) {
                    f += g * 10.0f;
                    i += 10;
                    continue;
                }
                f += g;
                ++i;
                continue;
            }
            if (world.getBlockState(lv).getMaterial().isSolid()) continue;
            ++i;
        }
        return f / (float)i;
    }
}

