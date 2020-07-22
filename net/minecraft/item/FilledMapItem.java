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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;

public class FilledMapItem
extends NetworkSyncedItem {
    public FilledMapItem(Item.Settings arg) {
        super(arg);
    }

    public static ItemStack createMap(World world, int x, int z, byte scale, boolean showIcons, boolean unlimitedTracking) {
        ItemStack lv = new ItemStack(Items.FILLED_MAP);
        FilledMapItem.createMapState(lv, world, x, z, scale, showIcons, unlimitedTracking, world.getRegistryKey());
        return lv;
    }

    @Nullable
    public static MapState getMapState(ItemStack stack, World world) {
        return world.getMapState(FilledMapItem.getMapName(FilledMapItem.getMapId(stack)));
    }

    @Nullable
    public static MapState getOrCreateMapState(ItemStack map, World world) {
        MapState lv = FilledMapItem.getMapState(map, world);
        if (lv == null && world instanceof ServerWorld) {
            lv = FilledMapItem.createMapState(map, world, world.getLevelProperties().getSpawnX(), world.getLevelProperties().getSpawnZ(), 3, false, false, world.getRegistryKey());
        }
        return lv;
    }

    public static int getMapId(ItemStack stack) {
        CompoundTag lv = stack.getTag();
        return lv != null && lv.contains("map", 99) ? lv.getInt("map") : 0;
    }

    private static MapState createMapState(ItemStack stack, World world, int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension) {
        int l = world.getNextMapId();
        MapState lv = new MapState(FilledMapItem.getMapName(l));
        lv.init(x, z, scale, showIcons, unlimitedTracking, dimension);
        world.putMapState(lv);
        stack.getOrCreateTag().putInt("map", l);
        return lv;
    }

    public static String getMapName(int mapId) {
        return "map_" + mapId;
    }

    public void updateColors(World world, Entity entity, MapState state) {
        if (world.getRegistryKey() != state.dimension || !(entity instanceof PlayerEntity)) {
            return;
        }
        int i = 1 << state.scale;
        int j = state.xCenter;
        int k = state.zCenter;
        int l = MathHelper.floor(entity.getX() - (double)j) / i + 64;
        int m = MathHelper.floor(entity.getZ() - (double)k) / i + 64;
        int n = 128 / i;
        if (world.getDimension().hasCeiling()) {
            n /= 2;
        }
        MapState.PlayerUpdateTracker lv = state.getPlayerSyncData((PlayerEntity)entity);
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
                WorldChunk lv2 = world.getWorldChunk(new BlockPos(s, 0, t));
                if (lv2.isEmpty()) continue;
                ChunkPos lv3 = lv2.getPos();
                int u = s & 0xF;
                int v = t & 0xF;
                int w = 0;
                double e = 0.0;
                if (world.getDimension().hasCeiling()) {
                    int x = s + t * 231871;
                    if (((x = x * x * 31287121 + x * 11) >> 20 & 1) == 0) {
                        multiset.add((Object)Blocks.DIRT.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 10);
                    } else {
                        multiset.add((Object)Blocks.STONE.getDefaultState().getTopMaterialColor(world, BlockPos.ORIGIN), 100);
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
                                } while ((lv6 = lv2.getBlockState(lv4)).getTopMaterialColor(world, lv4) == MaterialColor.CLEAR && aa > 0);
                                if (aa > 0 && !lv6.getFluidState().isEmpty()) {
                                    BlockState lv7;
                                    int ab = aa - 1;
                                    lv5.set(lv4);
                                    do {
                                        lv5.setY(ab--);
                                        lv7 = lv2.getBlockState(lv5);
                                        ++w;
                                    } while (ab > 0 && !lv7.getFluidState().isEmpty());
                                    lv6 = this.getFluidStateIfVisible(world, lv6, lv4);
                                }
                            } else {
                                lv8 = Blocks.BEDROCK.getDefaultState();
                            }
                            state.removeBanner(world, lv3.getStartX() + y + u, lv3.getStartZ() + z + v);
                            e += (double)aa / (double)(i * i);
                            multiset.add((Object)lv8.getTopMaterialColor(world, lv4));
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
                if ((lv9 = (MaterialColor)Iterables.getFirst((Iterable)Multisets.copyHighestCountFirst((Multiset)multiset), (Object)MaterialColor.CLEAR)) == MaterialColor.WATER) {
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
                if (p < 0 || q * q + r * r >= n * n || bl2 && (o + p & 1) == 0 || (b = state.colors[o + p * 128]) == (c = (byte)(lv9.id * 4 + ac))) continue;
                state.colors[o + p * 128] = c;
                state.markDirty(o, p);
                bl = true;
            }
        }
    }

    private BlockState getFluidStateIfVisible(World world, BlockState state, BlockPos pos) {
        FluidState lv = state.getFluidState();
        if (!lv.isEmpty() && !state.isSideSolidFullSquare(world, pos, Direction.UP)) {
            return lv.getBlockState();
        }
        return state;
    }

    private static boolean hasPositiveDepth(Biome[] biomes, int scale, int x, int z) {
        return biomes[x * scale + z * scale * 128 * scale].getDepth() >= 0.0f;
    }

    public static void fillExplorationMap(ServerWorld arg, ItemStack map) {
        MapState lv = FilledMapItem.getOrCreateMapState(map, arg);
        if (lv == null) {
            return;
        }
        if (arg.getRegistryKey() != lv.dimension) {
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
                MaterialColor lv3 = MaterialColor.CLEAR;
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
                        lv3 = MaterialColor.CLEAR;
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
                if (lv3 == MaterialColor.CLEAR) continue;
                lv.colors[n + o * 128] = (byte)(lv3.id * 4 + q);
                lv.markDirty(n, o);
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient) {
            return;
        }
        MapState lv = FilledMapItem.getOrCreateMapState(stack, world);
        if (lv == null) {
            return;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity lv2 = (PlayerEntity)entity;
            lv.update(lv2, stack);
        }
        if (!lv.locked && (selected || entity instanceof PlayerEntity && ((PlayerEntity)entity).getOffHandStack() == stack)) {
            this.updateColors(world, entity, lv);
        }
    }

    @Override
    @Nullable
    public Packet<?> createSyncPacket(ItemStack stack, World world, PlayerEntity player) {
        return FilledMapItem.getOrCreateMapState(stack, world).getPlayerMarkerPacket(stack, world, player);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        CompoundTag lv = stack.getTag();
        if (lv != null && lv.contains("map_scale_direction", 99)) {
            FilledMapItem.scale(stack, world, lv.getInt("map_scale_direction"));
            lv.remove("map_scale_direction");
        } else if (lv != null && lv.contains("map_to_lock", 1) && lv.getBoolean("map_to_lock")) {
            FilledMapItem.copyMap(world, stack);
            lv.remove("map_to_lock");
        }
    }

    protected static void scale(ItemStack map, World world, int amount) {
        MapState lv = FilledMapItem.getOrCreateMapState(map, world);
        if (lv != null) {
            FilledMapItem.createMapState(map, world, lv.xCenter, lv.zCenter, MathHelper.clamp(lv.scale + amount, 0, 4), lv.showIcons, lv.unlimitedTracking, lv.dimension);
        }
    }

    public static void copyMap(World world, ItemStack stack) {
        MapState lv = FilledMapItem.getOrCreateMapState(stack, world);
        if (lv != null) {
            MapState lv2 = FilledMapItem.createMapState(stack, world, 0, 0, lv.scale, lv.showIcons, lv.unlimitedTracking, lv.dimension);
            lv2.copyFrom(lv);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        MapState lv;
        MapState mapState = lv = world == null ? null : FilledMapItem.getOrCreateMapState(stack, world);
        if (lv != null && lv.locked) {
            tooltip.add(new TranslatableText("filled_map.locked", FilledMapItem.getMapId(stack)).formatted(Formatting.GRAY));
        }
        if (context.isAdvanced()) {
            if (lv != null) {
                tooltip.add(new TranslatableText("filled_map.id", FilledMapItem.getMapId(stack)).formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("filled_map.scale", 1 << lv.scale).formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("filled_map.level", lv.scale, 4).formatted(Formatting.GRAY));
            } else {
                tooltip.add(new TranslatableText("filled_map.unknown").formatted(Formatting.GRAY));
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static int getMapColor(ItemStack stack) {
        CompoundTag lv = stack.getSubTag("display");
        if (lv != null && lv.contains("MapColor", 99)) {
            int i = lv.getInt("MapColor");
            return 0xFF000000 | i & 0xFFFFFF;
        }
        return -12173266;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState lv = context.getWorld().getBlockState(context.getBlockPos());
        if (lv.isIn(BlockTags.BANNERS)) {
            if (!context.getWorld().isClient) {
                MapState lv2 = FilledMapItem.getOrCreateMapState(context.getStack(), context.getWorld());
                lv2.addBanner(context.getWorld(), context.getBlockPos());
            }
            return ActionResult.success(context.getWorld().isClient);
        }
        return super.useOnBlock(context);
    }
}

