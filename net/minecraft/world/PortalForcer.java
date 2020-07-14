/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world;

import java.util.Comparator;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.class_5459;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class PortalForcer {
    private final ServerWorld world;

    public PortalForcer(ServerWorld world) {
        this.world = world;
    }

    public Optional<class_5459.class_5460> method_30483(BlockPos arg3, boolean bl) {
        PointOfInterestStorage lv = this.world.getPointOfInterestStorage();
        int i = bl ? 16 : 128;
        lv.preloadChunks(this.world, arg3, i);
        Optional<PointOfInterest> optional = lv.getInSquare(arg -> arg == PointOfInterestType.NETHER_PORTAL, arg3, i, PointOfInterestStorage.OccupationStatus.ANY).min(Comparator.comparingDouble(arg2 -> arg2.getPos().getSquaredDistance(arg3)).thenComparingInt(arg -> arg.getPos().getY()));
        return optional.map(arg -> {
            BlockPos lv = arg.getPos();
            this.world.getChunkManager().addTicket(ChunkTicketType.field_19280, new ChunkPos(lv), 3, lv);
            BlockState lv2 = this.world.getBlockState(lv);
            return class_5459.method_30574(lv, lv2.get(Properties.HORIZONTAL_AXIS), 21, Direction.Axis.Y, 21, arg2 -> this.world.getBlockState((BlockPos)arg2) == lv2);
        });
    }

    public Optional<class_5459.class_5460> method_30482(BlockPos arg, Direction.Axis arg2) {
        Direction lv = Direction.get(Direction.AxisDirection.POSITIVE, arg2);
        double d = -1.0;
        BlockPos lv2 = null;
        double e = -1.0;
        BlockPos lv3 = null;
        WorldBorder lv4 = this.world.getWorldBorder();
        int i = this.world.getDimensionHeight() - 1;
        BlockPos.Mutable lv5 = arg.mutableCopy();
        for (BlockPos.Mutable lv6 : BlockPos.method_30512(arg, 16, Direction.EAST, Direction.SOUTH)) {
            int j = Math.min(i, this.world.getTopY(Heightmap.Type.MOTION_BLOCKING, lv6.getX(), lv6.getZ()));
            boolean k = true;
            if (!lv4.contains(lv6) || !lv4.contains(lv6.move(lv, 1))) continue;
            lv6.move(lv.getOpposite(), 1);
            for (int l = j; l >= 0; --l) {
                int n;
                lv6.setY(l);
                if (!this.world.isAir(lv6)) continue;
                int m = l;
                while (l > 0 && this.world.isAir(lv6.move(Direction.DOWN))) {
                    --l;
                }
                if (l + 4 > i || (n = m - l) > 0 && n < 3) continue;
                lv6.setY(l);
                if (!this.method_30481(lv6, lv5, lv, 0)) continue;
                double f = arg.getSquaredDistance(lv6);
                if (this.method_30481(lv6, lv5, lv, -1) && this.method_30481(lv6, lv5, lv, 1) && (d == -1.0 || d > f)) {
                    d = f;
                    lv2 = lv6.toImmutable();
                }
                if (d != -1.0 || e != -1.0 && !(e > f)) continue;
                e = f;
                lv3 = lv6.toImmutable();
            }
        }
        if (d == -1.0 && e != -1.0) {
            lv2 = lv3;
            d = e;
        }
        if (d == -1.0) {
            lv2 = new BlockPos(arg.getX(), MathHelper.clamp(arg.getY(), 70, this.world.getDimensionHeight() - 10), arg.getZ()).toImmutable();
            Direction lv7 = lv.rotateYClockwise();
            if (!lv4.contains(lv2)) {
                return Optional.empty();
            }
            for (int o = -1; o < 2; ++o) {
                for (int p = 0; p < 2; ++p) {
                    for (int q = -1; q < 3; ++q) {
                        BlockState lv8 = q < 0 ? Blocks.OBSIDIAN.getDefaultState() : Blocks.AIR.getDefaultState();
                        lv5.set(lv2, p * lv.getOffsetX() + o * lv7.getOffsetX(), q, p * lv.getOffsetZ() + o * lv7.getOffsetZ());
                        this.world.setBlockState(lv5, lv8);
                    }
                }
            }
        }
        for (int r = -1; r < 3; ++r) {
            for (int s = -1; s < 4; ++s) {
                if (r != -1 && r != 2 && s != -1 && s != 3) continue;
                lv5.set(lv2, r * lv.getOffsetX(), s, r * lv.getOffsetZ());
                this.world.setBlockState(lv5, Blocks.OBSIDIAN.getDefaultState(), 3);
            }
        }
        BlockState lv9 = (BlockState)Blocks.NETHER_PORTAL.getDefaultState().with(NetherPortalBlock.AXIS, arg2);
        for (int t = 0; t < 2; ++t) {
            for (int u = 0; u < 3; ++u) {
                lv5.set(lv2, t * lv.getOffsetX(), u, t * lv.getOffsetZ());
                this.world.setBlockState(lv5, lv9, 18);
            }
        }
        return Optional.of(new class_5459.class_5460(lv2.toImmutable(), 2, 3));
    }

    private boolean method_30481(BlockPos arg, BlockPos.Mutable arg2, Direction arg3, int i) {
        Direction lv = arg3.rotateYClockwise();
        for (int j = -1; j < 3; ++j) {
            for (int k = -1; k < 4; ++k) {
                arg2.set(arg, arg3.getOffsetX() * j + lv.getOffsetX() * i, k, arg3.getOffsetZ() * j + lv.getOffsetZ() * i);
                if (k < 0 && !this.world.getBlockState(arg2).getMaterial().isSolid()) {
                    return false;
                }
                if (k < 0 || this.world.isAir(arg2)) continue;
                return false;
            }
        }
        return true;
    }
}

