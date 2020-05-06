/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.item.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapBannerMarker;
import net.minecraft.item.map.MapFrameMarker;
import net.minecraft.item.map.MapIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

public class MapState
extends PersistentState {
    public int xCenter;
    public int zCenter;
    public DimensionType dimension;
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

    public void init(int i, int j, int k, boolean bl, boolean bl2, DimensionType arg) {
        this.scale = (byte)k;
        this.calculateCenter(i, j, this.scale);
        this.dimension = arg;
        this.showIcons = bl;
        this.unlimitedTracking = bl2;
        this.markDirty();
    }

    public void calculateCenter(double d, double e, int i) {
        int j = 128 * (1 << i);
        int k = MathHelper.floor((d + 64.0) / (double)j);
        int l = MathHelper.floor((e + 64.0) / (double)j);
        this.xCenter = k * j + j / 2 - 64;
        this.zCenter = l * j + j / 2 - 64;
    }

    @Override
    public void fromTag(CompoundTag arg) {
        int i = arg.getInt("dimension");
        DimensionType lv = DimensionType.byRawId(i);
        if (lv == null) {
            throw new IllegalArgumentException("Invalid map dimension: " + i);
        }
        this.dimension = lv;
        this.xCenter = arg.getInt("xCenter");
        this.zCenter = arg.getInt("zCenter");
        this.scale = (byte)MathHelper.clamp(arg.getByte("scale"), 0, 4);
        this.showIcons = !arg.contains("trackingPosition", 1) || arg.getBoolean("trackingPosition");
        this.unlimitedTracking = arg.getBoolean("unlimitedTracking");
        this.locked = arg.getBoolean("locked");
        this.colors = arg.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }
        ListTag lv2 = arg.getList("banners", 10);
        for (int j = 0; j < lv2.size(); ++j) {
            MapBannerMarker lv3 = MapBannerMarker.fromNbt(lv2.getCompound(j));
            this.banners.put(lv3.getKey(), lv3);
            this.addIcon(lv3.getIconType(), null, lv3.getKey(), lv3.getPos().getX(), lv3.getPos().getZ(), 180.0, lv3.getName());
        }
        ListTag lv4 = arg.getList("frames", 10);
        for (int k = 0; k < lv4.size(); ++k) {
            MapFrameMarker lv5 = MapFrameMarker.fromTag(lv4.getCompound(k));
            this.frames.put(lv5.getKey(), lv5);
            this.addIcon(MapIcon.Type.FRAME, null, "frame-" + lv5.getEntityId(), lv5.getPos().getX(), lv5.getPos().getZ(), lv5.getRotation(), null);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        arg.putInt("dimension", this.dimension.getRawId());
        arg.putInt("xCenter", this.xCenter);
        arg.putInt("zCenter", this.zCenter);
        arg.putByte("scale", this.scale);
        arg.putByteArray("colors", this.colors);
        arg.putBoolean("trackingPosition", this.showIcons);
        arg.putBoolean("unlimitedTracking", this.unlimitedTracking);
        arg.putBoolean("locked", this.locked);
        ListTag lv = new ListTag();
        for (MapBannerMarker lv2 : this.banners.values()) {
            lv.add(lv2.getNbt());
        }
        arg.put("banners", lv);
        ListTag lv3 = new ListTag();
        for (MapFrameMarker lv4 : this.frames.values()) {
            lv3.add(lv4.toTag());
        }
        arg.put("frames", lv3);
        return arg;
    }

    public void copyFrom(MapState arg) {
        this.locked = true;
        this.xCenter = arg.xCenter;
        this.zCenter = arg.zCenter;
        this.banners.putAll(arg.banners);
        this.icons.putAll(arg.icons);
        System.arraycopy(arg.colors, 0, this.colors, 0, arg.colors.length);
        this.markDirty();
    }

    public void update(PlayerEntity arg, ItemStack arg2) {
        CompoundTag lv7;
        if (!this.updateTrackersByPlayer.containsKey(arg)) {
            PlayerUpdateTracker lv = new PlayerUpdateTracker(arg);
            this.updateTrackersByPlayer.put(arg, lv);
            this.updateTrackers.add(lv);
        }
        if (!arg.inventory.contains(arg2)) {
            this.icons.remove(arg.getName().getString());
        }
        for (int i = 0; i < this.updateTrackers.size(); ++i) {
            PlayerUpdateTracker lv2 = this.updateTrackers.get(i);
            String string = lv2.player.getName().getString();
            if (lv2.player.removed || !lv2.player.inventory.contains(arg2) && !arg2.isInFrame()) {
                this.updateTrackersByPlayer.remove(lv2.player);
                this.updateTrackers.remove(lv2);
                this.icons.remove(string);
                continue;
            }
            if (arg2.isInFrame() || lv2.player.dimension != this.dimension || !this.showIcons) continue;
            this.addIcon(MapIcon.Type.PLAYER, lv2.player.world, string, lv2.player.getX(), lv2.player.getZ(), lv2.player.yaw, null);
        }
        if (arg2.isInFrame() && this.showIcons) {
            ItemFrameEntity lv3 = arg2.getFrame();
            BlockPos lv4 = lv3.getDecorationBlockPos();
            MapFrameMarker lv5 = this.frames.get(MapFrameMarker.getKey(lv4));
            if (lv5 != null && lv3.getEntityId() != lv5.getEntityId() && this.frames.containsKey(lv5.getKey())) {
                this.icons.remove("frame-" + lv5.getEntityId());
            }
            MapFrameMarker lv6 = new MapFrameMarker(lv4, lv3.getHorizontalFacing().getHorizontal() * 90, lv3.getEntityId());
            this.addIcon(MapIcon.Type.FRAME, arg.world, "frame-" + lv3.getEntityId(), lv4.getX(), lv4.getZ(), lv3.getHorizontalFacing().getHorizontal() * 90, null);
            this.frames.put(lv6.getKey(), lv6);
        }
        if ((lv7 = arg2.getTag()) != null && lv7.contains("Decorations", 9)) {
            ListTag lv8 = lv7.getList("Decorations", 10);
            for (int j = 0; j < lv8.size(); ++j) {
                CompoundTag lv9 = lv8.getCompound(j);
                if (this.icons.containsKey(lv9.getString("id"))) continue;
                this.addIcon(MapIcon.Type.byId(lv9.getByte("type")), arg.world, lv9.getString("id"), lv9.getDouble("x"), lv9.getDouble("z"), lv9.getDouble("rot"), null);
            }
        }
    }

    public static void addDecorationsTag(ItemStack arg, BlockPos arg2, String string, MapIcon.Type arg3) {
        ListTag lv2;
        if (arg.hasTag() && arg.getTag().contains("Decorations", 9)) {
            ListTag lv = arg.getTag().getList("Decorations", 10);
        } else {
            lv2 = new ListTag();
            arg.putSubTag("Decorations", lv2);
        }
        CompoundTag lv3 = new CompoundTag();
        lv3.putByte("type", arg3.getId());
        lv3.putString("id", string);
        lv3.putDouble("x", arg2.getX());
        lv3.putDouble("z", arg2.getZ());
        lv3.putDouble("rot", 180.0);
        lv2.add(lv3);
        if (arg3.hasTintColor()) {
            CompoundTag lv4 = arg.getOrCreateSubTag("display");
            lv4.putInt("MapColor", arg3.getTintColor());
        }
    }

    /*
     * WARNING - void declaration
     */
    private void addIcon(MapIcon.Type arg, @Nullable IWorld arg2, String string, double d, double e, double f, @Nullable Text arg3) {
        void o;
        int i = 1 << this.scale;
        float g = (float)(d - (double)this.xCenter) / (float)i;
        float h = (float)(e - (double)this.zCenter) / (float)i;
        byte b = (byte)((double)(g * 2.0f) + 0.5);
        byte c = (byte)((double)(h * 2.0f) + 0.5);
        int j = 63;
        if (g >= -63.0f && h >= -63.0f && g <= 63.0f && h <= 63.0f) {
            byte k = (byte)((f += f < 0.0 ? -8.0 : 8.0) * 16.0 / 360.0);
            if (this.dimension == DimensionType.THE_NETHER && arg2 != null) {
                int l = (int)(arg2.getLevelProperties().getTimeOfDay() / 10L);
                k = (byte)(l * l * 34187121 + l * 121 >> 15 & 0xF);
            }
        } else if (arg == MapIcon.Type.PLAYER) {
            int m = 320;
            if (Math.abs(g) < 320.0f && Math.abs(h) < 320.0f) {
                arg = MapIcon.Type.PLAYER_OFF_MAP;
            } else if (this.unlimitedTracking) {
                arg = MapIcon.Type.PLAYER_OFF_LIMITS;
            } else {
                this.icons.remove(string);
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
            this.icons.remove(string);
            return;
        }
        this.icons.put(string, new MapIcon(arg, b, c, (byte)o, arg3));
    }

    @Nullable
    public Packet<?> getPlayerMarkerPacket(ItemStack arg, BlockView arg2, PlayerEntity arg3) {
        PlayerUpdateTracker lv = this.updateTrackersByPlayer.get(arg3);
        if (lv == null) {
            return null;
        }
        return lv.getPacket(arg);
    }

    public void markDirty(int i, int j) {
        this.markDirty();
        for (PlayerUpdateTracker lv : this.updateTrackers) {
            lv.markDirty(i, j);
        }
    }

    public PlayerUpdateTracker getPlayerSyncData(PlayerEntity arg) {
        PlayerUpdateTracker lv = this.updateTrackersByPlayer.get(arg);
        if (lv == null) {
            lv = new PlayerUpdateTracker(arg);
            this.updateTrackersByPlayer.put(arg, lv);
            this.updateTrackers.add(lv);
        }
        return lv;
    }

    public void addBanner(IWorld arg, BlockPos arg2) {
        float f = (float)arg2.getX() + 0.5f;
        float g = (float)arg2.getZ() + 0.5f;
        int i = 1 << this.scale;
        float h = (f - (float)this.xCenter) / (float)i;
        float j = (g - (float)this.zCenter) / (float)i;
        int k = 63;
        boolean bl = false;
        if (h >= -63.0f && j >= -63.0f && h <= 63.0f && j <= 63.0f) {
            MapBannerMarker lv = MapBannerMarker.fromWorldBlock(arg, arg2);
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
                this.addIcon(lv.getIconType(), arg, lv.getKey(), f, g, 180.0, lv.getName());
                bl = true;
            }
            if (bl) {
                this.markDirty();
            }
        }
    }

    public void removeBanner(BlockView arg, int i, int j) {
        Iterator<MapBannerMarker> iterator = this.banners.values().iterator();
        while (iterator.hasNext()) {
            MapBannerMarker lv2;
            MapBannerMarker lv = iterator.next();
            if (lv.getPos().getX() != i || lv.getPos().getZ() != j || lv.equals(lv2 = MapBannerMarker.fromWorldBlock(arg, lv.getPos()))) continue;
            iterator.remove();
            this.icons.remove(lv.getKey());
        }
    }

    public void removeFrame(BlockPos arg, int i) {
        this.icons.remove("frame-" + i);
        this.frames.remove(MapFrameMarker.getKey(arg));
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
        public Packet<?> getPacket(ItemStack arg) {
            if (this.dirty) {
                this.dirty = false;
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(arg), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, this.startX, this.startZ, this.endX + 1 - this.startX, this.endZ + 1 - this.startZ);
            }
            if (this.emptyPacketsRequested++ % 5 == 0) {
                return new MapUpdateS2CPacket(FilledMapItem.getMapId(arg), MapState.this.scale, MapState.this.showIcons, MapState.this.locked, MapState.this.icons.values(), MapState.this.colors, 0, 0, 0, 0);
            }
            return null;
        }

        public void markDirty(int i, int j) {
            if (this.dirty) {
                this.startX = Math.min(this.startX, i);
                this.startZ = Math.min(this.startZ, j);
                this.endX = Math.max(this.endX, i);
                this.endZ = Math.max(this.endZ, j);
            } else {
                this.dirty = true;
                this.startX = i;
                this.startZ = j;
                this.endX = i;
                this.endZ = j;
            }
        }
    }
}

