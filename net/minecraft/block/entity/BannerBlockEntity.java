/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;

public class BannerBlockEntity
extends BlockEntity
implements Nameable {
    @Nullable
    private Text customName;
    @Nullable
    private DyeColor baseColor = DyeColor.WHITE;
    @Nullable
    private ListTag patternListTag;
    private boolean patternListTagRead;
    @Nullable
    private List<Pair<BannerPattern, DyeColor>> patterns;

    public BannerBlockEntity() {
        super(BlockEntityType.BANNER);
    }

    public BannerBlockEntity(DyeColor arg) {
        this();
        this.baseColor = arg;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static ListTag getPatternListTag(ItemStack arg) {
        ListTag lv = null;
        CompoundTag lv2 = arg.getSubTag("BlockEntityTag");
        if (lv2 != null && lv2.contains("Patterns", 9)) {
            lv = lv2.getList("Patterns", 10).copy();
        }
        return lv;
    }

    @Environment(value=EnvType.CLIENT)
    public void readFrom(ItemStack arg, DyeColor arg2) {
        this.patternListTag = BannerBlockEntity.getPatternListTag(arg);
        this.baseColor = arg2;
        this.patterns = null;
        this.patternListTagRead = true;
        this.customName = arg.hasCustomName() ? arg.getName() : null;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableText("block.minecraft.banner");
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(Text arg) {
        this.customName = arg;
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        if (this.patternListTag != null) {
            arg.put("Patterns", this.patternListTag);
        }
        if (this.customName != null) {
            arg.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return arg;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        if (arg2.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(arg2.getString("CustomName"));
        }
        this.baseColor = this.hasWorld() ? ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor() : null;
        this.patternListTag = arg2.getList("Patterns", 10);
        this.patterns = null;
        this.patternListTagRead = true;
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 6, this.toInitialChunkDataTag());
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }

    public static int getPatternCount(ItemStack arg) {
        CompoundTag lv = arg.getSubTag("BlockEntityTag");
        if (lv != null && lv.contains("Patterns")) {
            return lv.getList("Patterns", 10).size();
        }
        return 0;
    }

    @Environment(value=EnvType.CLIENT)
    public List<Pair<BannerPattern, DyeColor>> getPatterns() {
        if (this.patterns == null && this.patternListTagRead) {
            this.patterns = BannerBlockEntity.method_24280(this.getColorForState(this::getCachedState), this.patternListTag);
        }
        return this.patterns;
    }

    @Environment(value=EnvType.CLIENT)
    public static List<Pair<BannerPattern, DyeColor>> method_24280(DyeColor arg, @Nullable ListTag arg2) {
        ArrayList list = Lists.newArrayList();
        list.add(Pair.of((Object)((Object)BannerPattern.BASE), (Object)arg));
        if (arg2 != null) {
            for (int i = 0; i < arg2.size(); ++i) {
                CompoundTag lv = arg2.getCompound(i);
                BannerPattern lv2 = BannerPattern.byId(lv.getString("Pattern"));
                if (lv2 == null) continue;
                int j = lv.getInt("Color");
                list.add(Pair.of((Object)((Object)lv2), (Object)DyeColor.byId(j)));
            }
        }
        return list;
    }

    public static void loadFromItemStack(ItemStack arg) {
        CompoundTag lv = arg.getSubTag("BlockEntityTag");
        if (lv == null || !lv.contains("Patterns", 9)) {
            return;
        }
        ListTag lv2 = lv.getList("Patterns", 10);
        if (lv2.isEmpty()) {
            return;
        }
        lv2.remove(lv2.size() - 1);
        if (lv2.isEmpty()) {
            arg.removeSubTag("BlockEntityTag");
        }
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockState arg) {
        ItemStack lv = new ItemStack(BannerBlock.getForColor(this.getColorForState(() -> arg)));
        if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
            lv.getOrCreateSubTag("BlockEntityTag").put("Patterns", this.patternListTag.copy());
        }
        if (this.customName != null) {
            lv.setCustomName(this.customName);
        }
        return lv;
    }

    public DyeColor getColorForState(Supplier<BlockState> supplier) {
        if (this.baseColor == null) {
            this.baseColor = ((AbstractBannerBlock)supplier.get().getBlock()).getColor();
        }
        return this.baseColor;
    }
}

