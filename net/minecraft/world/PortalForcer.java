/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class PortalForcer {
    private final ServerWorld world;
    private final Random random;

    public PortalForcer(ServerWorld arg) {
        this.world = arg;
        this.random = new Random(arg.getSeed());
    }

    public boolean usePortal(Entity arg, float f) {
        Vec3d lv = arg.getLastNetherPortalDirectionVector();
        Direction lv2 = arg.getLastNetherPortalDirection();
        BlockPattern.TeleportTarget lv3 = this.getPortal(arg.getBlockPos(), arg.getVelocity(), lv2, lv.x, lv.y, arg instanceof PlayerEntity);
        if (lv3 == null) {
            return false;
        }
        Vec3d lv4 = lv3.pos;
        Vec3d lv5 = lv3.velocity;
        arg.setVelocity(lv5);
        arg.yaw = f + (float)lv3.yaw;
        arg.refreshPositionAfterTeleport(lv4.x, lv4.y, lv4.z);
        return true;
    }

    @Nullable
    public BlockPattern.TeleportTarget getPortal(BlockPos arg4, Vec3d arg22, Direction arg32, double d, double e, boolean bl) {
        PointOfInterestStorage lv = this.world.getPointOfInterestStorage();
        lv.preloadChunks(this.world, arg4, 128);
        List list = lv.getInSquare(arg -> arg == PointOfInterestType.NETHER_PORTAL, arg4, 128, PointOfInterestStorage.OccupationStatus.ANY).collect(Collectors.toList());
        Optional<PointOfInterest> optional = list.stream().min(Comparator.comparingDouble(arg2 -> arg2.getPos().getSquaredDistance(arg4)).thenComparingInt(arg -> arg.getPos().getY()));
        return optional.map(arg3 -> {
            BlockPos lv = arg3.getPos();
            this.world.getChunkManager().addTicket(ChunkTicketType.PORTAL, new ChunkPos(lv), 3, lv);
            BlockPattern.Result lv2 = NetherPortalBlock.findPortal(this.world, lv);
            return lv2.getTeleportTarget(arg32, lv, e, arg22, d);
        }).orElse(null);
    }

    public boolean createPortal(Entity arg) {
        int i = 16;
        double d = -1.0;
        int j = MathHelper.floor(arg.getX());
        int k = MathHelper.floor(arg.getY());
        int l = MathHelper.floor(arg.getZ());
        int m = j;
        int n = k;
        int o = l;
        int p = 0;
        int q = this.random.nextInt(4);
        BlockPos.Mutable lv = new BlockPos.Mutable();
        for (int r = j - 16; r <= j + 16; ++r) {
            double e = (double)r + 0.5 - arg.getX();
            for (int s = l - 16; s <= l + 16; ++s) {
                double f = (double)s + 0.5 - arg.getZ();
                block2: for (int t = this.world.getDimensionHeight() - 1; t >= 0; --t) {
                    if (!this.world.isAir(lv.set(r, t, s))) continue;
                    while (t > 0 && this.world.isAir(lv.set(r, t - 1, s))) {
                        --t;
                    }
                    for (int u = q; u < q + 4; ++u) {
                        int v = u % 2;
                        int w = 1 - v;
                        if (u % 4 >= 2) {
                            v = -v;
                            w = -w;
                        }
                        for (int x = 0; x < 3; ++x) {
                            for (int y = 0; y < 4; ++y) {
                                for (int z = -1; z < 4; ++z) {
                                    int aa = r + (y - 1) * v + x * w;
                                    int ab = t + z;
                                    int ac = s + (y - 1) * w - x * v;
                                    lv.set(aa, ab, ac);
                                    if (z < 0 && !this.world.getBlockState(lv).getMaterial().isSolid() || z >= 0 && !this.world.isAir(lv)) continue block2;
                                }
                            }
                        }
                        double g = (double)t + 0.5 - arg.getY();
                        double h = e * e + g * g + f * f;
                        if (!(d < 0.0) && !(h < d)) continue;
                        d = h;
                        m = r;
                        n = t;
                        o = s;
                        p = u % 4;
                    }
                }
            }
        }
        if (d < 0.0) {
            for (int ad = j - 16; ad <= j + 16; ++ad) {
                double ae = (double)ad + 0.5 - arg.getX();
                for (int af = l - 16; af <= l + 16; ++af) {
                    double ag = (double)af + 0.5 - arg.getZ();
                    block10: for (int ah = this.world.getDimensionHeight() - 1; ah >= 0; --ah) {
                        if (!this.world.isAir(lv.set(ad, ah, af))) continue;
                        while (ah > 0 && this.world.isAir(lv.set(ad, ah - 1, af))) {
                            --ah;
                        }
                        for (int ai = q; ai < q + 2; ++ai) {
                            int aj = ai % 2;
                            int ak = 1 - aj;
                            for (int al = 0; al < 4; ++al) {
                                for (int am = -1; am < 4; ++am) {
                                    int an = ad + (al - 1) * aj;
                                    int ao = ah + am;
                                    int ap = af + (al - 1) * ak;
                                    lv.set(an, ao, ap);
                                    if (am < 0 && !this.world.getBlockState(lv).getMaterial().isSolid() || am >= 0 && !this.world.isAir(lv)) continue block10;
                                }
                            }
                            double aq = (double)ah + 0.5 - arg.getY();
                            double ar = ae * ae + aq * aq + ag * ag;
                            if (!(d < 0.0) && !(ar < d)) continue;
                            d = ar;
                            m = ad;
                            n = ah;
                            o = af;
                            p = ai % 2;
                        }
                    }
                }
            }
        }
        int as = p;
        int at = m;
        int au = n;
        int av = o;
        int aw = as % 2;
        int ax = 1 - aw;
        if (as % 4 >= 2) {
            aw = -aw;
            ax = -ax;
        }
        if (d < 0.0) {
            au = n = MathHelper.clamp(n, 70, this.world.getDimensionHeight() - 10);
            for (int ay = -1; ay <= 1; ++ay) {
                for (int az = 1; az < 3; ++az) {
                    for (int ba = -1; ba < 3; ++ba) {
                        int bb = at + (az - 1) * aw + ay * ax;
                        int bc = au + ba;
                        int bd = av + (az - 1) * ax - ay * aw;
                        boolean bl = ba < 0;
                        lv.set(bb, bc, bd);
                        this.world.setBlockState(lv, bl ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
        for (int be = -1; be < 3; ++be) {
            for (int bf = -1; bf < 4; ++bf) {
                if (be != -1 && be != 2 && bf != -1 && bf != 3) continue;
                lv.set(at + be * aw, au + bf, av + be * ax);
                this.world.setBlockState(lv, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
        }
        BlockState lv2 = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, aw == 0 ? Direction.Axis.Z : Direction.Axis.X);
        for (int bg = 0; bg < 2; ++bg) {
            for (int bh = 0; bh < 3; ++bh) {
                lv.set(at + bg * aw, au + bh, av + bg * ax);
                this.world.setBlockState(lv, lv2, 18);
            }
        }
        return true;
    }
}

