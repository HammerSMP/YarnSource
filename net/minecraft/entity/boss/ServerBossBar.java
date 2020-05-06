/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 */
package net.minecraft.entity.boss;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ServerBossBar
extends BossBar {
    private final Set<ServerPlayerEntity> players = Sets.newHashSet();
    private final Set<ServerPlayerEntity> unmodifiablePlayers = Collections.unmodifiableSet(this.players);
    private boolean visible = true;

    public ServerBossBar(Text arg, BossBar.Color arg2, BossBar.Style arg3) {
        super(MathHelper.randomUuid(), arg, arg2, arg3);
    }

    @Override
    public void setPercent(float f) {
        if (f != this.percent) {
            super.setPercent(f);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_PCT);
        }
    }

    @Override
    public void setColor(BossBar.Color arg) {
        if (arg != this.color) {
            super.setColor(arg);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_STYLE);
        }
    }

    @Override
    public void setOverlay(BossBar.Style arg) {
        if (arg != this.style) {
            super.setOverlay(arg);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_STYLE);
        }
    }

    @Override
    public BossBar setDarkenSky(boolean bl) {
        if (bl != this.darkenSky) {
            super.setDarkenSky(bl);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_PROPERTIES);
        }
        return this;
    }

    @Override
    public BossBar setDragonMusic(boolean bl) {
        if (bl != this.dragonMusic) {
            super.setDragonMusic(bl);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_PROPERTIES);
        }
        return this;
    }

    @Override
    public BossBar setThickenFog(boolean bl) {
        if (bl != this.thickenFog) {
            super.setThickenFog(bl);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_PROPERTIES);
        }
        return this;
    }

    @Override
    public void setName(Text arg) {
        if (!Objects.equal((Object)arg, (Object)this.name)) {
            super.setName(arg);
            this.sendPacket(BossBarS2CPacket.Type.UPDATE_NAME);
        }
    }

    private void sendPacket(BossBarS2CPacket.Type arg) {
        if (this.visible) {
            BossBarS2CPacket lv = new BossBarS2CPacket(arg, this);
            for (ServerPlayerEntity lv2 : this.players) {
                lv2.networkHandler.sendPacket(lv);
            }
        }
    }

    public void addPlayer(ServerPlayerEntity arg) {
        if (this.players.add(arg) && this.visible) {
            arg.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.ADD, this));
        }
    }

    public void removePlayer(ServerPlayerEntity arg) {
        if (this.players.remove(arg) && this.visible) {
            arg.networkHandler.sendPacket(new BossBarS2CPacket(BossBarS2CPacket.Type.REMOVE, this));
        }
    }

    public void clearPlayers() {
        if (!this.players.isEmpty()) {
            for (ServerPlayerEntity lv : Lists.newArrayList(this.players)) {
                this.removePlayer(lv);
            }
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean bl) {
        if (bl != this.visible) {
            this.visible = bl;
            for (ServerPlayerEntity lv : this.players) {
                lv.networkHandler.sendPacket(new BossBarS2CPacket(bl ? BossBarS2CPacket.Type.ADD : BossBarS2CPacket.Type.REMOVE, this));
            }
        }
    }

    public Collection<ServerPlayerEntity> getPlayers() {
        return this.unmodifiablePlayers;
    }
}

