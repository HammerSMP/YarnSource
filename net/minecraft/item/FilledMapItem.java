/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.LinkedHashMultiset
 *  com.google.common.collect.Multiset
 *  com.google.common.collect.Multisets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.class_5321;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;

public class FilledMapItem
extends NetworkSyncedItem {
    public FilledMapItem(Item.Settings arg) {
        super(arg);
    }

    public static ItemStack createMap(World arg, int i, int j, byte b, boolean bl, boolean bl2) {
        ItemStack lv = new ItemStack(Items.FILLED_MAP);
        FilledMapItem.createMapState(lv, arg, i, j, b, bl, bl2, arg.method_27983());
        return lv;
    }

    @Nullable
    public static MapState getMapState(ItemStack arg, World arg2) {
        return arg2.getMapState(FilledMapItem.getMapName(FilledMapItem.getMapId(arg)));
    }

    @Nullable
    public static MapState getOrCreateMapState(ItemStack arg, World arg2) {
        MapState lv = FilledMapItem.getMapState(arg, arg2);
        if (lv == null && arg2 instanceof ServerWorld) {
            lv = FilledMapItem.createMapState(arg, arg2, arg2.getLevelProperties().getSpawnX(), arg2.getLevelProperties().getSpawnZ(), 3, false, false, arg2.method_27983());
        }
        return lv;
    }

    public static int getMapId(ItemStack arg) {
        CompoundTag lv = arg.getTag();
        return lv != null && lv.contains("map", 99) ? lv.getInt("map") : 0;
    }

    private static MapState createMapState(ItemStack arg, World arg2, int i, int j, int k, boolean bl, boolean bl2, class_5321<DimensionType> arg3) {
        int l = arg2.getNextMapId();
        MapState lv = new MapState(FilledMapItem.getMapName(l));
        lv.init(i, j, k, bl, bl2, arg3);
        arg2.putMapState(lv);
        arg.getOrCreateTag().putInt("map", l);
        return lv;
    }

    public static String getMapName(int i) {
        return "map_" + i;
    }

    public void updateColors(World arg, Entity arg2, MapState arg3) {
        if (arg.method_27983() != arg3.dimension || !(arg2 instanceof PlayerEntity)) {
            return;
        }
        int i = 1 << arg3.scale;
        int j = arg3.xCenter;
        int k = arg3.zCenter;
        int l = MathHelper.floor(arg2.getX() - (double)j) / i + 64;
        int m = MathHelper.floor(arg2.getZ() - (double)k) / i + 64;
        int n = 128 / i;
        if (arg.getDimension().method_27998()) {
            n /= 2;
        }
        MapState.PlayerUpdateTracker lv = arg3.getPlayerSyncData((PlayerEntity)arg2);
        ++lv.field_131;
        boolean bl = false;
        for (int o = l - n + 1; o < l + n; ++o) {
            if ((o & 0xF) != (lv.field_131 & 0xF) && !bl) continue;
            bl = false;
            double d = 0.0;
            for (int p = m - n - 1; p < m + n; ++p) {
                byte c;
                byte b;
                MaterialColor lv9;
                if (o < 0 || p < -1 || o >= 128 || p >= 128) continue;
                int q = o - l;
                int r = p - m;
                boolean bl2 = q * q + r * r > (n - 2) * (n - 2);
                int s = (j / i + o - 64) * i;
                int t = (k / i + p - 64) * i;
                LinkedHashMultiset multiset = LinkedHashMultiset.create();
                WorldChunk lv2 = arg.getWorldChunk(new BlockPos(s, 0, t));
                if (lv2.isEmpty()) continue;
                ChunkPos lv3 = lv2.getPos();
                int u = s & 0xF;
                int v = t & 0xF;
                int w = 0;
                double e = 0.0;
                if (arg.getDimension().method_27998()) {
                    int x = s + t * 231871;
                    if (((x = x * x * 31287121 + x * 11) >> 20 & 1) == 0) {
                        multiset.add((Object)Blocks.DIRT.getDefaultState().getTopMaterialColor(arg, BlockPos.ORIGIN), 10);
                    } else {
                        multiset.add((Object)Blocks.STONE.getDefaultState().getTopMaterialColor(arg, BlockPos.ORIGIN), 100);
                    }
                    e = 100.0;
                } else {
                    BlockPos.Mutable lv4 = new BlockPos.Mutable();
                    BlockPos.Mutable lv5 = new BlockPos.Mutable();
                    for (int y = 0; y < i; ++y) {
                        for (int z = 0; z < i; ++z) {
                            BlockState lv8;
                            int aa = lv2.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, y + u, z + v) + 1;
                            if (aa > 1) {
                                BlockState lv6;
                                do {
                                    lv4.set(lv3.getStartX() + y + u, --aa, lv3.getStartZ() + z + v);
                                } while ((lv6 = lv2.getBlockState(lv4)).getTopMaterialColor(arg, lv4) == MaterialColor.AIR && aa > 0);
                                if (aa > 0 && !lv6.getFluidState().isEmpty()) {
                                    BlockState lv7;
                                    int ab = aa - 1;
                                    lv5.set(lv4);
                                    do {
                                        lv5.setY(ab--);
                                        lv7 = lv2.getBlockState(lv5);
                                        ++w;
                                    } while (ab > 0 && !lv7.getFluidState().isEmpty());
                                    lv6 = this.getFluidStateIfVisible(arg, lv6, lv4);
                                }
                            } else {
                                lv8 = Blocks.BEDROCK.getDefaultState();
                            }
                            arg3.removeBanner(arg, lv3.getStartX() + y + u, lv3.getStartZ() + z + v);
                            e += (double)aa / (double)(i * i);
                            multiset.add((Object)lv8.getTopMaterialColor(arg, lv4));
                        }
                    }
                }
                w /= i * i;
                double f = (e - d) * 4.0 / (double)(i + 4) + ((double)(o + p & 1) - 0.5) * 0.4;
                int ac = 1;
                if (f > 0.6) {
                    ac = 2;
                }
                if (f < -0.6) {
                    ac = 0;
                }
                if ((lv9 = (MaterialColor)Iterables.getFirst((Iterable)Multisets.copyHighestCountFirst((Multiset)multiset), (Object)MaterialColor.AIR)) == MaterialColor.WATER) {
                    f = (double)w * 0.1 + (double)(o + p & 1) * 0.2;
                    ac = 1;
                    if (f < 0.5) {
                        ac = 2;
                    }
                    if (f > 0.9) {
                        ac = 0;
                    }
                }
                d = e;
                if (p < 0 || q * q + r * r >= n * n || bl2 && (o + p & 1) == 0 || (b = arg3.colors[o + p * 128]) == (c = (byte)(lv9.id * 4 + ac))) continue;
                arg3.colors[o + p * 128] = c;
                arg3.markDirty(o, p);
                bl = true;
            }
        }
    }

    private BlockState getFluidStateIfVisible(World arg, BlockState arg2, BlockPos arg3) {
        FluidState lv = arg2.getFluidState();
        if (!lv.isEmpty() && !arg2.isSideSolidFullSquare(arg, arg3, Direction.UP)) {
            return lv.getBlockState();
        }
        return arg2;
    }

    private static boolean hasPositiveDepth(Biome[] args, int i, int j, int k) {
        return args[j * i + k * i * 128 * i].getDepth() >= 0.0f;
    }

    public static void fillExplorationMap(ServerWorld arg, ItemStack arg2) {
        MapState lv = FilledMapItem.getOrCreateMapState(arg2, arg);
        if (lv == null) {
            return;
        }
        if (arg.method_27983() != lv.dimension) {
            return;
        }
        int i = 1 << lv.scale;
        int j = lv.xCenter;
        int k = lv.zCenter;
        Biome[] lvs = new Biome[128 * i * 128 * i];
        for (int l = 0; l < 128 * i; ++l) {
            for (int m = 0; m < 128 * i; ++m) {
                lvs[l * 128 * i + m] = arg.getBiome(new BlockPos((j / i - 64) * i + m, 0, (k / i - 64) * i + l));
            }
        }
        for (int n = 0; n < 128; ++n) {
            for (int o = 0; o < 128; ++o) {
                if (n <= 0 || o <= 0 || n >= 127 || o >= 127) continue;
                Biome lv2 = lvs[n * i + o * i * 128 * i];
                int p = 8;
                if (FilledMapItem.hasPositiveDepth(lvs, i, n - 1, o - 1)) {
                    --p;
                }
                if (FilledMapItem.hasPositiveDepth(lvs, i, n - 1, o + 1)) {
                    --p;
                }
                if (FilledMapItem.hasPositiveDepth(lvs, i, n - 1, o)) {
                    --p;
                }
                if (FilledMapItem.hasPositiveDepth(lvs, i, n + 1, o - 1)) {
                    --p;
                }
                if (FilledMapItem.hasPositiveDepth(lvs, i, n + 1, o + 1)) {
                    --p;
                }
                if (FilledMapItem.hasPositiveDepth(lvs, i, n + 1, o)) {
                    --p;
                }
                if (FilledMapItem.hasPositiveDepth(lvs, i, n, o - 1)) {
                    --p;
                }
                if (FilledMapItem.hasPositiveDepth(lvs, i, n, o + 1)) {
                    --p;
                }
                int q = 3;
                MaterialColor lv3 = MaterialColor.AIR;
                if (lv2.getDepth() < 0.0f) {
                    lv3 = MaterialColor.ORANGE;
                    if (p > 7 && o % 2 == 0) {
                        q = (n + (int)(MathHelper.sin((float)o + 0.0f) * 7.0f)) / 8 % 5;
                        if (q == 3) {
                            q = 1;
                        } else if (q == 4) {
                            q = 0;
                        }
                    } else if (p > 7) {
                        lv3 = MaterialColor.AIR;
                    } else if (p > 5) {
                        q = 1;
                    } else if (p > 3) {
                        q = 0;
                    } else if (p > 1) {
                        q = 0;
                    }
                } else if (p > 0) {
                    lv3 = MaterialColor.BROWN;
                    q = p > 3 ? 1 : 3;
                }
                if (lv3 == MaterialColor.AIR) continue;
                lv.colors[n + o * 128] = (byte)(lv3.id * 4 + q);
                lv.markDirty(n, o);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack arg, World arg2, Entity arg3, int i, boolean bl) {
        if (arg2.isClient) {
            return;
        }
        MapState lv = FilledMapItem.getOrCreateMapState(arg, arg2);
        if (lv == null) {
            return;
        }
        if (arg3 instanceof PlayerEntity) {
            PlayerEntity lv2 = (PlayerEntity)arg3;
            lv.update(lv2, arg);
        }
        if (!lv.locked && (bl || arg3 instanceof PlayerEntity && ((PlayerEntity)arg3).getOffHandStack() == arg)) {
            this.updateColors(arg2, arg3, lv);
        }
    }

    @Override
    @Nullable
    public Packet<?> createSyncPacket(ItemStack arg, World arg2, PlayerEntity arg3) {
        return FilledMapItem.getOrCreateMapState(arg, arg2).getPlayerMarkerPacket(arg, arg2, arg3);
    }

    @Override
    public void onCraft(ItemStack arg, World arg2, PlayerEntity arg3) {
        CompoundTag lv = arg.getTag();
        if (lv != null && lv.contains("map_scale_direction", 99)) {
            FilledMapItem.scale(arg, arg2, lv.getInt("map_scale_direction"));
            lv.remove("map_scale_direction");
        }
    }

    protected static void scale(ItemStack arg, World arg2, int i) {
        MapState lv = FilledMapItem.getOrCreateMapState(arg, arg2);
        if (lv != null) {
            FilledMapItem.createMapState(arg, arg2, lv.xCenter, lv.zCenter, MathHelper.clamp(lv.scale + i, 0, 4), lv.showIcons, lv.unlimitedTracking, lv.dimension);
        }
    }

    @Nullable
    public static ItemStack copyMap(World arg, ItemStack arg2) {
        MapState lv = FilledMapItem.getOrCreateMapState(arg2, arg);
        if (lv != null) {
            ItemStack lv2 = arg2.copy();
            MapState lv3 = FilledMapItem.createMapState(lv2, arg, 0, 0, lv.scale, lv.showIcons, lv.unlimitedTracking, lv.dimension);
            lv3.copyFrom(lv);
            return lv2;
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        MapState lv;
        MapState mapState = lv = arg2 == null ? null : FilledMapItem.getOrCreateMapState(arg, arg2);
        if (lv != null && lv.locked) {
            list.add(new TranslatableText("filled_map.locked", FilledMapItem.getMapId(arg)).formatted(Formatting.GRAY));
        }
        if (arg3.isAdvanced()) {
            if (lv != null) {
                list.add(new TranslatableText("filled_map.id", FilledMapItem.getMapId(arg)).formatted(Formatting.GRAY));
                list.add(new TranslatableText("filled_map.scale", 1 << lv.scale).formatted(Formatting.GRAY));
                list.add(new TranslatableText("filled_map.level", lv.scale, 4).formatted(Formatting.GRAY));
            } else {
                list.add(new TranslatableText("filled_map.unknown").formatted(Formatting.GRAY));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static int getMapColor(ItemStack arg) {
        CompoundTag lv = arg.getSubTag("display");
        if (lv != null && lv.contains("MapColor", 99)) {
            int i = lv.getInt("MapColor");
            return 0xFF000000 | i & 0xFFFFFF;
        }
        return -12173266;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        BlockState lv = arg.getWorld().getBlockState(arg.getBlockPos());
        if (lv.isIn(BlockTags.BANNERS)) {
            if (!arg.world.isClient) {
                MapState lv2 = FilledMapItem.getOrCreateMapState(arg.getStack(), arg.getWorld());
                lv2.addBanner(arg.getWorld(), arg.getBlockPos());
            }
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(arg);
    }
}

