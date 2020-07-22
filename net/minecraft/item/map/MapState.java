/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapFrameMarker;
import net.minecraft.item.map.MapIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.BlockView;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MapState
extends PersistentState {
    private static final Logger field_25019 = LogManager.getLogger();
    public int xCenter;
    public int zCenter;
    public RegistryKey<World> dimension;
    public boolean showIcons;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors = new byte[16384];
    public boolean locked;
    public final List<PlayerUpdateTracker> updateTrackers = Lists.newArrayList();
    private final Map<PlayerEntity, PlayerUpdateTracker> updateTrackersByPlayer = Maps.newHashMap();
    private final Map<String, MapBannerMarker> banners = Maps.newHashMap();
    public final Map<String, MapIcon> icons = Maps.newLinkedHashMap();
    private final Map<String, MapFrameMarker> frames = Maps.newHashMap();

    public MapState(String string) {
        super(string);
    }

    public void init(int x, int z, int scale, boolean showIcons, boolean unlimitedTracking, RegistryKey<World> dimension) {
        this.scale = (byte)scale;
        this.calculateCenter(x, z, this.scale);
        this.dimension = dimension;
        this.showIcons = showIcons;
        this.unlimitedTracking = unlimitedTracking;
        this.markDirty();
    }

    public void calculateCenter(double x, double z, int scale) {
        int j = 128 * (1 << scale);
        int k = MathHelper.floor((x + 64.0) / (double)j);
        int l = MathHelper.floor((z + 64.0) / (double)j);
        this.xCenter = k * j + j / 2 - 64;
        this.zCenter = l * j + j / 2 - 64;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        this.dimension = (RegistryKey)DimensionType.method_28521(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)tag.get("dimension"))).resultOrPartial(((Logger)field_25019)::error).orElseThrow(() -> new IllegalArgumentException("Invalid map dimension: " + tag.get("dimension")));
        this.xCenter = tag.getInt("xCenter");
        this.zCenter = tag.getInt("zCenter");
        this.scale = (byte)MathHelper.clamp(tag.getByte("scale"), 0, 4);
        this.showIcons = !tag.contains("trackingPosition", 1) || tag.getBoolean("trackingPosition");
        this.unlimitedTracking = tag.getBoolean("unlimitedTracking");
        this.locked = tag.getBoolean("locked");
        this.colors = tag.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        ListTag lv = tag.getList("banners", 10);
        for (int i = 0; i < lv.size(); ++i) {
            MapBannerMarker lv2 = MapBannerMarker.fromNbt(lv.getCompound(i));
            this.banners.put(lv2.getKey(), lv2);
            this.addIcon(lv2.getIconType(), null, lv2.getKey(), lv2.getPos().getX(), lv2.getPos().getZ(), 180.0, lv2.getName());
        }
        ListTag lv3 = tag.getList("frames", 10);
        for (int j = 0; j < lv3.size(); ++j) {
            MapFrameMarker lv4 = MapFrameMarker.fromTag(lv3.getCompound(j));
            this.frames.put(lv4.getKey(), lv4);
            this.addIcon(MapIcon.Type.FRAME, null, "frame-" + lv4.getEntityId(), lv4.getPos().getX(), lv4.getPos().getZ(), lv4.getRotation(), null);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Identifier.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.dimension.getValue()).resultOrPartial(((Logger)field_25019)::error).ifPresent(arg2 -> tag.put("dimension", (Tag)arg2));
        tag.putInt("xCenter", this.xCenter);
        tag.putInt("zCenter", this.zCenter);
        tag.putByte("scale", this.scale);
        tag.putByteArray("colors", this.colors);
        tag.putBoolean("trackingPosition", this.showIcons);
        tag.putBoolean("unlimitedTracking", this.unlimitedTracking);
        tag.putBoolean("locked", this.locked);
        ListTag lv = new ListTag();
        for (MapBannerMarker lv2 : this.banners.values()) {
            lv.add(lv2.getNbt());
        }
        tag.put("banners", lv);
        ListTag lv3 = new ListTag();
        for (MapFrameMarker lv4 : this.frames.values()) {
            lv3.add(lv4.toTag());
        }
        tag.put("frames", lv3);
        return tag;
    }

    public void copyFrom(MapState state) {
        this.locked = true;
        this.xCenter = state.xCenter;
        this.zCenter = state.zCenter;
        this.banners.putAll(state.banners);
        this.icons.putAll(state.icons);
        System.arraycopy(state.colors, 0, this.colors, 0, state.colors.length);
        this.markDirty();
    }

    public void update(PlayerEntity player, ItemStack stack) {
        CompoundTag lv7;
        if (!this.updateTrackersByPlayer.containsKey(player)) {
            PlayerUpdateTracker lv = new PlayerUpdateTracker(player);
            this.updateTrackersByPlayer.put(player, lv);
            this.updateTrackers.add(lv);
        }
        if (!player.inventory.contains(stack)) {
            this.icons.remove(player.getName().getString());
        }
        for (int i = 0; i < this.updateTrackers.size(); ++i) {
            PlayerUpdateTracker lv2 = this.updateTrackers.get(i);
            String string = lv2.player.getName().getString();
            if (lv2.player.removed || !lv2.player.inventory.contains(stack) && !stack.isInFrame()) {
                this.updateTrackersByPlayer.remove(lv2.player);
                this.updateTrackers.remove(lv2);
                this.icons.remove(string);
                continue;
            }
            if (stack.isInFrame() || lv2.player.world.getRegistryKey() != this.dimension || !this.showIcons) continue;
            this.addIcon(MapIcon.Type.PLAYER, lv2.player.world, string, lv2.player.getX(), lv2.player.getZ(), lv2.player.yaw, null);
        }
        if (stack.isInFrame() && this.showIcons) {
            ItemFrameEntity lv3 = stack.getFrame();
            BlockPos lv4 = lv3.getDecorationBlockPos();
            MapFrameMarker lv5 = this.frames.get(MapFrameMarker.getKey(lv4));
            if (lv5 != null && lv3.getEntityId() != lv5.getEntityId() && this.frames.containsKey(lv5.getKey())) {
                this.icons.remove("frame-" + lv5.getEntityId());
            }
            MapFrameMarker lv6 = new MapFrameMarker(lv4, lv3.getHorizontalFacing().getHorizontal() * 90, lv3.getEntityId());
            this.addIcon(MapIcon.Type.FRAME, player.world, "frame-" + lv3.getEntityId(), lv4.getX(), lv4.getZ(), lv3.getHorizontalFacing().getHorizontal() * 90, null);
            this.frames.put(lv6.getKey(), lv6);
        }
        if ((lv7 = stack.getTag()) != null && lv7.contains("Decorations", 9)) {
            ListTag lv8 = lv7.getList("Decorations", 10);
            for (int j = 0; j < lv8.size(); ++j) {
                CompoundTag lv9 = lv8.getCompound(j);
                if (this.icons.containsKey(lv9.getString("id"))) continue;
                this.addIcon(MapIcon.Type.byId(lv9.getByte("type")), player.world, lv9.getString("id"), lv9.getDouble("x"), lv9.getDouble("z"), lv9.getDouble("rot"), null);
            }
        }
    }

    public static void addDecorationsTag(ItemStack stack, BlockPos pos, String id, MapIcon.Type type) {
        ListTag lv2;
        if (stack.hasTag() && stack.getTag().contains("Decorations", 9)) {
            ListTag lv = stack.getTag().getList("Decorations", 10);
        } else {
            lv2 = new ListTag();
            stack.putSubTag("Decorations", lv2);
        }
        CompoundTag lv3 = new CompoundTag();
        lv3.putByte("type", type.getId());
        lv3.putString("id", id);
        lv3.putDouble("x", pos.getX());
        lv3.putDouble("z", pos.getZ());
        lv3.putDouble("rot", 180.0);
        lv2.add(lv3);
        if (type.hasTintColor()) {
            CompoundTag lv4 = stack.getOrCreateSubTag("display");
            lv4.putInt("MapColor", type.getTintColor());
        }
    }

    /*
     * WARNING - void declaration
     */
    private void addIcon(MapIcon.Type type, @Nullable WorldAccess world, String key, double x, double z, double rotation, @Nullable Text text) {
        void o;
        int i = 1 << this.scale;
        float g = (float)(x - (double)this.xCenter) / (float)i;
        float h = (float)(z - (double)this.zCenter) / (float)i;
        byte b = (byte)((double)(g * 2.0f) + 0.5);
        byte c = (byte)((double)(h * 2.0f) + 0.5);
        int j = 63;
        if (g >= -63.0f && h >= -63.0f && g <= 63.0f && h <= 63.0f) {
            byte k = (byte)((rotation += rotation < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension == World.NETHER && world != null) {
                int l = (int)(world.getLevelProperties().getTimeOfDay() / 10L);
                k = (byte)(l * l * 34187121 + l * 121 >> 15 & 0xF);
            }
        } else if (type == MapIcon.Type.PLAYER) {
            int m = 320;
            if (Math.abs(g) < 320.0f && Math.abs(h) < 320.0f) {
                type = MapIcon.Type.PLAYER_OFF_MAP;
            } else if (this.unlimitedTracking) {
                type = MapIcon.Type.PLAYER_OFF_LIMITS;
            } else {
                this.icons.remove(key);
                return;
            }
            boolean n = false;
            if (g <= -63.0f) {
                b = -128;
            }
            if (h <= -63.0f) {
                c = -128;
            }
            if (g >= 63.0f) {
                b = 127;
            }
            if (h >= 63.0f) {
                c = 127;
            }
        } else {
            this.icons.remove(key);
            return;
        }
        this.icons.put(key, new MapIcon(type, b, c, (byte)o, text));
    }

    @Nullable
    public Packet<?> getPlayerMarkerPacket(ItemStack map, BlockView world, PlayerEntity pos) {
        PlayerUpdateTracker lv = this.updateTrackersByPlayer.get(pos);
        if (lv == null) {
            return null;
        }
        return lv.getPacket(map);
    }

    public void markDirty(int x, int z) {
        this.markDirty();
        for (PlayerUpdateTracker lv : this.updateTrackers) {
            lv.markDirty(x, z);
        }
    }

    public PlayerUpdateTracker getPlayerSyncData(PlayerEntity player) {
        PlayerUpdateTracker lv = this.updateTrackersByPlayer.get(player);
        if (lv == null) {
            lv = new PlayerUpdateTracker(player);
            this.updateTrackersByPlayer.put(player, lv);
            this.updateTrackers.add(lv);
        }
        return lv;
    }

    public void addBanner(WorldAccess world, BlockPos pos) {
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getZ() + 0.5;
        int i = 1 << this.scale;
        double f = (d - (double)this.xCenter) / (double)i;
        double g = (e - (double)this.zCenter) / (double)i;
        int j = 63;
        boolean bl = false;
        if (f >= -63.0 && g >= -63.0 && f <= 63.0 && g <= 63.0) {
            MapBannerMarker lv = MapBannerMarker.fromWorldBlock(world, pos);
            if (lv == null) {
                return;
            }
            boolean bl2 = true;
            if (this.banners.containsKey(lv.getKey()) && this.banners.get(lv.getKey()).equals(lv)) {
                this.banners.remove(lv.getKey());
                this.icons.remove(lv.getKey());
                bl2 = false;
                bl = true;
            }
            if (bl2) {
                this.banners.put(lv.getKey(), lv);
                this.addIcon(lv.getIconType(), world, lv.getKey(), d, e, 180.0, lv.getName());
                bl = true;
            }
            if (bl) {
                this.markDirty();
            }
        }
    }

    public void removeBanner(BlockView world, int x, int z) {
        Iterator<MapBannerMarker> iterator = this.banners.values().iterator();
        while (iterator.hasNext()) {
            MapBannerMarker lv2;
            MapBannerMarker lv = iterator.next();
            if (lv.getPos().getX() != x || lv.getPos().getZ() != z || lv.equals(lv2 = MapBannerMarker.fromWorldBlock(world, lv.getPos()))) continue;
            iterator.remove();
            this.icons.remove(lv.getKey());
        }
    }

    public void removeFrame(BlockPos pos, int id) {
        this.icons.remove("frame-" + id);
        this.frames.remove(MapFrameMarker.getKey(pos));
    }

    public class PlayerUpdateTracker {
        public final PlayerEntity player;
        private boolean dirty = true;
        private int startX;
        private int startZ;
        private int endX = 127;
        private int endZ = 127;
        private int emptyPacketsRequested;
        public int field_131;

        public PlayerUpdateTracker(PlayerEntity arg2) {
            this.player = arg2;
        }

        @Nullable
        public Packet<?> getPacket(ItemStack stack) {
            if (this.dirty) {
                this.dirty = false;
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(stack), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, this.startX, this.startZ, this.endX + 1 - this.startX, this.endZ + 1 - this.startZ);
            }
            if (this.emptyPacketsRequested++ % 5 == 0) {
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(stack), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, 0, 0, 0, 0);
            }
            return null;
        }

        public void markDirty(int x, int z) {
            if (this.dirty) {
                this.startX = Math.min(this.startX, x);
                this.startZ = Math.min(this.startZ, z);
                this.endX = Math.max(this.endX, x);
                this.endZ = Math.max(this.endZ, z);
            } else {
                this.dirty = true;
                this.startX = x;
                this.startZ = z;
                this.endX = x;
                this.endZ = z;
            }
        }
    }
}

