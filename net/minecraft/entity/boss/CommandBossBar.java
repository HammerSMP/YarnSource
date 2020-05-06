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

    public CommandBossBar(Identifier arg, Text arg2) {
        super(arg2, BossBar.Color.WHITE, BossBar.Style.PROGRESS);
        this.id = arg;
        this.setPercent(0.0f);
    }

    public Identifier getId() {
        return this.id;
    }

    @Override
    public void addPlayer(ServerPlayerEntity arg) {
        super.addPlayer(arg);
        this.playerUuids.add(arg.getUuid());
    }

    public void addPlayer(UUID uUID) {
        this.playerUuids.add(uUID);
    }

    @Override
    public void removePlayer(ServerPlayerEntity arg) {
        super.removePlayer(arg);
        this.playerUuids.remove(arg.getUuid());
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

    public void setValue(int i) {
        this.value = i;
        this.setPercent(MathHelper.clamp((float)i / (float)this.maxValue, 0.0f, 1.0f));
    }

    public void setMaxValue(int i) {
        this.maxValue = i;
        this.setPercent(MathHelper.clamp((float)this.value / (float)i, 0.0f, 1.0f));
    }

    public final Text toHoverableText() {
        return Texts.bracketed(this.getName()).styled(arg -> arg.withColor(this.getColor().getTextFormat()).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.getId().toString()))).withInsertion(this.getId().toString()));
    }

    public boolean addPlayers(Collection<ServerPlayerEntity> collection) {
        HashSet set = Sets.newHashSet();
        HashSet set2 = Sets.newHashSet();
        for (UUID uUID : this.playerUuids) {
            boolean bl = false;
            for (ServerPlayerEntity lv : collection) {
                if (!lv.getUuid().equals(uUID)) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            set.add(uUID);
        }
        for (ServerPlayerEntity lv2 : collection) {
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
            lv2.add(NbtHelper.fromUuidNew(uUID));
        }
        lv.put("Players", lv2);
        return lv;
    }

    public static CommandBossBar fromTag(CompoundTag arg, Identifier arg2) {
        CommandBossBar lv = new CommandBossBar(arg2, Text.Serializer.fromJson(arg.getString("Name")));
        lv.setVisible(arg.getBoolean("Visible"));
        lv.setValue(arg.getInt("Value"));
        lv.setMaxValue(arg.getInt("Max"));
        lv.setColor(BossBar.Color.byName(arg.getString("Color")));
        lv.setOverlay(BossBar.Style.byName(arg.getString("Overlay")));
        lv.setDarkenSky(arg.getBoolean("DarkenScreen"));
        lv.setDragonMusic(arg.getBoolean("PlayBossMusic"));
        lv.setThickenFog(arg.getBoolean("CreateWorldFog"));
        ListTag lv2 = arg.getList("Players", 11);
        for (int i = 0; i < lv2.size(); ++i) {
            lv.addPlayer(NbtHelper.toUuidNew(lv2.get(i)));
        }
        return lv;
    }

    public void onPlayerConnect(ServerPlayerEntity arg) {
        if (this.playerUuids.contains(arg.getUuid())) {
            this.addPlayer(arg);
        }
    }

    public void onPlayerDisconnect(ServerPlayerEntity arg) {
        super.removePlayer(arg);
    }
}

