/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.item.map;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.map.MapIcon;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class MapBannerMarker {
    private final BlockPos pos;
    private final DyeColor color;
    @Nullable
    private final Text name;

    public MapBannerMarker(BlockPos arg, DyeColor arg2, @Nullable Text arg3) {
        this.pos = arg;
        this.color = arg2;
        this.name = arg3;
    }

    public static MapBannerMarker fromNbt(CompoundTag arg) {
        BlockPos lv = NbtHelper.toBlockPos(arg.getCompound("Pos"));
        DyeColor lv2 = DyeColor.byName(arg.getString("Color"), DyeColor.WHITE);
        MutableText lv3 = arg.contains("Name") ? Text.Serializer.fromJson(arg.getString("Name")) : null;
        return new MapBannerMarker(lv, lv2, lv3);
    }

    @Nullable
    public static MapBannerMarker fromWorldBlock(BlockView arg, BlockPos arg2) {
        BlockEntity lv = arg.getBlockEntity(arg2);
        if (lv instanceof BannerBlockEntity) {
            BannerBlockEntity lv2 = (BannerBlockEntity)lv;
            DyeColor lv3 = lv2.getColorForState(() -> arg.getBlockState(arg2));
            Text lv4 = lv2.hasCustomName() ? lv2.getCustomName() : null;
            return new MapBannerMarker(arg2, lv3, lv4);
        }
        return null;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public MapIcon.Type getIconType() {
        switch (this.color) {
            case WHITE: {
                return MapIcon.Type.BANNER_WHITE;
            }
            case ORANGE: {
                return MapIcon.Type.BANNER_ORANGE;
            }
            case MAGENTA: {
                return MapIcon.Type.BANNER_MAGENTA;
            }
            case LIGHT_BLUE: {
                return MapIcon.Type.BANNER_LIGHT_BLUE;
            }
            case YELLOW: {
                return MapIcon.Type.BANNER_YELLOW;
            }
            case LIME: {
                return MapIcon.Type.BANNER_LIME;
            }
            case PINK: {
                return MapIcon.Type.BANNER_PINK;
            }
            case GRAY: {
                return MapIcon.Type.BANNER_GRAY;
            }
            case LIGHT_GRAY: {
                return MapIcon.Type.BANNER_LIGHT_GRAY;
            }
            case CYAN: {
                return MapIcon.Type.BANNER_CYAN;
            }
            case PURPLE: {
                return MapIcon.Type.BANNER_PURPLE;
            }
            case BLUE: {
                return MapIcon.Type.BANNER_BLUE;
            }
            case BROWN: {
                return MapIcon.Type.BANNER_BROWN;
            }
            case GREEN: {
                return MapIcon.Type.BANNER_GREEN;
            }
            case RED: {
                return MapIcon.Type.BANNER_RED;
            }
        }
        return MapIcon.Type.BANNER_BLACK;
    }

    @Nullable
    public Text getName() {
        return this.name;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MapBannerMarker lv = (MapBannerMarker)object;
        return Objects.equals(this.pos, lv.pos) && this.color == lv.color && Objects.equals(this.name, lv.name);
    }

    public int hashCode() {
        return Objects.hash(this.pos, this.color, this.name);
    }

    public CompoundTag getNbt() {
        CompoundTag lv = new CompoundTag();
        lv.put("Pos", NbtHelper.fromBlockPos(this.pos));
        lv.putString("Color", this.color.getName());
        if (this.name != null) {
            lv.putString("Name", Text.Serializer.toJson(this.name));
        }
        return lv;
    }

    public String getKey() {
        return "banner-" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ();
    }
}

