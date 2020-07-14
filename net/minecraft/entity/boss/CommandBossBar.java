/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package net.minecraft.entity.boss;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class CommandBossBar
extends ServerBossBar {
    private final Identifier id;
    private final Set<UUID> playerUuids = Sets.newHashSet();
    private int value;
    private int maxValue = 100;

    public CommandBossBar(Identifier id, Text displayName) {
        super(displayName, BossBar.Color.WHITE, BossBar.Style.PROGRESS);
        this.id = id;
        this.setPercent(0.0f);
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public void addPlayer(ServerPlayerEntity player) {
        super.addPlayer(player);
        this.playerUuids.add(player.getUuid());
    }

    public void addPlayer(UUID uuid) {
        this.playerUuids.add(uuid);
    }

    @Override
    public void removePlayer(ServerPlayerEntity player) {
        super.removePlayer(player);
        this.playerUuids.remove(player.getUuid());
    }

    @Override
    public void clearPlayers() {
        super.clearPlayers();
        this.playerUuids.clear();
    }

    public int getValue() {
        return this.value;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public void setValue(int value) {
        this.value = value;
        this.setPercent(MathHelper.clamp((float)value / (float)this.maxValue, 0.0f, 1.0f));
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        this.setPercent(MathHelper.clamp((float)this.value / (float)maxValue, 0.0f, 1.0f));
    }

    public final Text toHoverableText() {
        return Texts.bracketed(this.getName()).styled(style -> style.withColor(this.getColor().getTextFormat()).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.getId().toString()))).withInsertion(this.getId().toString()));
    }

    public boolean addPlayers(Collection<ServerPlayerEntity> players) {
        HashSet set = Sets.newHashSet();
        HashSet set2 = Sets.newHashSet();
        for (UUID uUID : this.playerUuids) {
            boolean bl = false;
            for (ServerPlayerEntity lv : players) {
                if (!lv.getUuid().equals(uUID)) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            set.add(uUID);
        }
        for (ServerPlayerEntity lv2 : players) {
            boolean bl2 = false;
            for (UUID uUID2 : this.playerUuids) {
                if (!lv2.getUuid().equals(uUID2)) continue;
                bl2 = true;
                break;
            }
            if (bl2) continue;
            set2.add(lv2);
        }
        for (UUID uUID3 : set) {
            for (ServerPlayerEntity lv3 : this.getPlayers()) {
                if (!lv3.getUuid().equals(uUID3)) continue;
                this.removePlayer(lv3);
                break;
            }
            this.playerUuids.remove(uUID3);
        }
        for (ServerPlayerEntity lv4 : set2) {
            this.addPlayer(lv4);
        }
        return !set.isEmpty() || !set2.isEmpty();
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", Text.Serializer.toJson(this.name));
        lv.putBoolean("Visible", this.isVisible());
        lv.putInt("Value", this.value);
        lv.putInt("Max", this.maxValue);
        lv.putString("Color", this.getColor().getName());
        lv.putString("Overlay", this.getOverlay().getName());
        lv.putBoolean("DarkenScreen", this.getDarkenSky());
        lv.putBoolean("PlayBossMusic", this.hasDragonMusic());
        lv.putBoolean("CreateWorldFog", this.getThickenFog());
        ListTag lv2 = new ListTag();
        for (UUID uUID : this.playerUuids) {
            lv2.add(NbtHelper.fromUuid(uUID));
        }
        lv.put("Players", lv2);
        return lv;
    }

    public static CommandBossBar fromTag(CompoundTag tag, Identifier id) {
        CommandBossBar lv = new CommandBossBar(id, Text.Serializer.fromJson(tag.getString("Name")));
        lv.setVisible(tag.getBoolean("Visible"));
        lv.setValue(tag.getInt("Value"));
        lv.setMaxValue(tag.getInt("Max"));
        lv.setColor(BossBar.Color.byName(tag.getString("Color")));
        lv.setOverlay(BossBar.Style.byName(tag.getString("Overlay")));
        lv.setDarkenSky(tag.getBoolean("DarkenScreen"));
        lv.setDragonMusic(tag.getBoolean("PlayBossMusic"));
        lv.setThickenFog(tag.getBoolean("CreateWorldFog"));
        ListTag lv2 = tag.getList("Players", 11);
        for (int i = 0; i < lv2.size(); ++i) {
            lv.addPlayer(NbtHelper.toUuid(lv2.get(i)));
        }
        return lv;
    }

    public void onPlayerConnect(ServerPlayerEntity player) {
        if (this.playerUuids.contains(player.getUuid())) {
            this.addPlayer(player);
        }
    }

    public void onPlayerDisconnect(ServerPlayerEntity player) {
        super.removePlayer(player);
    }
}

