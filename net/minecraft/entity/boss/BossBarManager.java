/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.boss;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BossBarManager {
    private final Map<Identifier, CommandBossBar> commandBossBars = Maps.newHashMap();

    @Nullable
    public CommandBossBar get(Identifier id) {
        return this.commandBossBars.get(id);
    }

    public CommandBossBar add(Identifier id, Text displayName) {
        CommandBossBar lv = new CommandBossBar(id, displayName);
        this.commandBossBars.put(id, lv);
        return lv;
    }

    public void remove(CommandBossBar bossBar) {
        this.commandBossBars.remove(bossBar.getId());
    }

    public Collection<Identifier> getIds() {
        return this.commandBossBars.keySet();
    }

    public Collection<CommandBossBar> getAll() {
        return this.commandBossBars.values();
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        for (CommandBossBar lv2 : this.commandBossBars.values()) {
            lv.put(lv2.getId().toString(), lv2.toTag());
        }
        return lv;
    }

    public void fromTag(CompoundTag tag) {
        for (String string : tag.getKeys()) {
            Identifier lv = new Identifier(string);
            this.commandBossBars.put(lv, CommandBossBar.fromTag(tag.getCompound(string), lv));
        }
    }

    public void onPlayerConnect(ServerPlayerEntity player) {
        for (CommandBossBar lv : this.commandBossBars.values()) {
            lv.onPlayerConnect(player);
        }
    }

    public void onPlayerDisconnect(ServerPlayerEntity player) {
        for (CommandBossBar lv : this.commandBossBars.values()) {
            lv.onPlayerDisconnect(player);
        }
    }
}

