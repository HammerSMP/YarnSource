/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MusicDiscItem
extends Item {
    private static final Map<SoundEvent, MusicDiscItem> MUSIC_DISCS = Maps.newHashMap();
    private final int comparatorOutput;
    private final SoundEvent sound;

    protected MusicDiscItem(int i, SoundEvent arg, Item.Settings arg2) {
        super(arg2);
        this.comparatorOutput = i;
        this.sound = arg;
        MUSIC_DISCS.put(this.sound, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        BlockPos lv2;
        World lv = arg.getWorld();
        BlockState lv3 = lv.getBlockState(lv2 = arg.getBlockPos());
        if (!lv3.isOf(Blocks.JUKEBOX) || lv3.get(JukeboxBlock.HAS_RECORD).booleanValue()) {
            return ActionResult.PASS;
        }
        ItemStack lv4 = arg.getStack();
        if (!lv.isClient) {
            ((JukeboxBlock)Blocks.JUKEBOX).setRecord(lv, lv2, lv3, lv4);
            lv.syncWorldEvent(null, 1010, lv2, Item.getRawId(this));
            lv4.decrement(1);
            PlayerEntity lv5 = arg.getPlayer();
            if (lv5 != null) {
                lv5.incrementStat(Stats.PLAY_RECORD);
            }
        }
        return ActionResult.success(lv.isClient);
    }

    public int getComparatorOutput() {
        return this.comparatorOutput;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
        list.add(this.getDescription().formatted(Formatting.GRAY));
    }

    @Environment(value=EnvType.CLIENT)
    public MutableText getDescription() {
        return new TranslatableText(this.getTranslationKey() + ".desc");
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static MusicDiscItem bySound(SoundEvent arg) {
        return MUSIC_DISCS.get(arg);
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }
}

