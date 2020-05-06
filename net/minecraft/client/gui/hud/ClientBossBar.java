/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ClientBossBar
extends BossBar {
    protected float healthLatest;
    protected long timeHealthSet;

    public ClientBossBar(BossBarS2CPacket arg) {
        super(arg.getUuid(), arg.getName(), arg.getColor(), arg.getOverlay());
        this.healthLatest = arg.getPercent();
        this.percent = arg.getPercent();
        this.timeHealthSet = Util.getMeasuringTimeMs();
        this.setDarkenSky(arg.shouldDarkenSky());
        this.setDragonMusic(arg.hasDragonMusic());
        this.setThickenFog(arg.shouldThickenFog());
    }

    @Override
    public void setPercent(float f) {
        this.percent = this.getPercent();
        this.healthLatest = f;
        this.timeHealthSet = Util.getMeasuringTimeMs();
    }

    @Override
    public float getPercent() {
        long l = Util.getMeasuringTimeMs() - this.timeHealthSet;
        float f = MathHelper.clamp((float)l / 100.0f, 0.0f, 1.0f);
        return MathHelper.lerp(f, this.percent, this.healthLatest);
    }

    public void handlePacket(BossBarS2CPacket arg) {
        switch (arg.getType()) {
            case UPDATE_NAME: {
                this.setName(arg.getName());
                break;
            }
            case UPDATE_PCT: {
                this.setPercent(arg.getPercent());
                break;
            }
            case UPDATE_STYLE: {
                this.setColor(arg.getColor());
                this.setOverlay(arg.getOverlay());
                break;
            }
            case UPDATE_PROPERTIES: {
                this.setDarkenSky(arg.shouldDarkenSky());
                this.setDragonMusic(arg.hasDragonMusic());
            }
        }
    }
}

