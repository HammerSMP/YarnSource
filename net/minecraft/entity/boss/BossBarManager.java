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
    public CommandBossBar get(Identifier arg) {
        return this.commandBossBars.get(arg);
    }

    public CommandBossBar add(Identifier arg, Text arg2) {
        CommandBossBar lv = new CommandBossBar(arg, arg2);
        this.commandBossBars.put(arg, lv);
        return lv;
    }

    public void remove(CommandBossBar arg) {
        this.commandBossBars.remove(arg.getId());
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

    public void fromTag(CompoundTag arg) {
        for (String string : arg.getKeys()) {
            Identifier lv = new Identifier(string);
            this.commandBossBars.put(lv, CommandBossBar.fromTag(arg.getCompound(string), lv));
        }
    }

    public void onPlayerConnect(ServerPlayerEntity arg) {
        for (CommandBossBar lv : this.commandBossBars.values()) {
            lv.onPlayerConnect(arg);
        }
    }

    public void onPlayerDisconnenct(ServerPlayerEntity arg) {
        for (CommandBossBar lv : this.commandBossBars.values()) {
            lv.onPlayerDisconnect(arg);
        }
    }
}

