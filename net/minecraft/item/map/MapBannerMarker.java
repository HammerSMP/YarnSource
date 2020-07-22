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

    public MapBannerMarker(BlockPos pos, DyeColor dyeColor, @Nullable Text name) {
        this.pos = pos;
        this.color = dyeColor;
        this.name = name;
    }

    public static MapBannerMarker fromNbt(CompoundTag tag) {
        BlockPos lv = NbtHelper.toBlockPos(tag.getCompound("Pos"));
        DyeColor lv2 = DyeColor.byName(tag.getString("Color"), DyeColor.WHITE);
        MutableText lv3 = tag.contains("Name") ? Text.Serializer.fromJson(tag.getString("Name")) : null;
        return new MapBannerMarker(lv, lv2, lv3);
    }

    @Nullable
    public static MapBannerMarker fromWorldBlock(BlockView blockView, BlockPos blockPos) {
        BlockEntity lv = blockView.getBlockEntity(blockPos);
        if (lv instanceof BannerBlockEntity) {
            BannerBlockEntity lv2 = (BannerBlockEntity)lv;
            DyeColor lv3 = lv2.getColorForState(() -> blockView.getBlockState(blockPos));
            Text lv4 = lv2.hasCustomName() ? lv2.getCustomName() : null;
            return new MapBannerMarker(blockPos, lv3, lv4);
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

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MapBannerMarker lv = (MapBannerMarker)o;
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

