/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class BossBarHud
extends DrawableHelper {
    private static final Identifier BAR_TEX = new Identifier("textures/gui/bars.png");
    private final MinecraftClient client;
    private final Map<UUID, ClientBossBar> bossBars = Maps.newLinkedHashMap();

    public BossBarHud(MinecraftClient arg) {
        this.client = arg;
    }

    public void render(MatrixStack arg) {
        if (this.bossBars.isEmpty()) {
            return;
        }
        int i = this.client.getWindow().getScaledWidth();
        int j = 12;
        for (ClientBossBar lv : this.bossBars.values()) {
            int k = i / 2 - 91;
            int l = j;
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(BAR_TEX);
            this.renderBossBar(arg, k, l, lv);
            Text lv2 = lv.getName();
            int m = this.client.textRenderer.getWidth(lv2);
            int n = i / 2 - m / 2;
            int o = l - 9;
            this.client.textRenderer.drawWithShadow(arg, lv2, (float)n, (float)o, 0xFFFFFF);
            this.client.textRenderer.getClass();
            if ((j += 10 + 9) < this.client.getWindow().getScaledHeight() / 3) continue;
            break;
        }
    }

    private void renderBossBar(MatrixStack arg, int i, int j, BossBar arg2) {
        int k;
        this.drawTexture(arg, i, j, 0, arg2.getColor().ordinal() * 5 * 2, 182, 5);
        if (arg2.getOverlay() != BossBar.Style.PROGRESS) {
            this.drawTexture(arg, i, j, 0, 80 + (arg2.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
        if ((k = (int)(arg2.getPercent() * 183.0f)) > 0) {
            this.drawTexture(arg, i, j, 0, arg2.getColor().ordinal() * 5 * 2 + 5, k, 5);
            if (arg2.getOverlay() != BossBar.Style.PROGRESS) {
                this.drawTexture(arg, i, j, 0, 80 + (arg2.getOverlay().ordinal() - 1) * 5 * 2 + 5, k, 5);
            }
        }
    }

    public void handlePacket(BossBarS2CPacket arg) {
        if (arg.getType() == BossBarS2CPacket.Type.ADD) {
            this.bossBars.put(arg.getUuid(), new ClientBossBar(arg));
        } else if (arg.getType() == BossBarS2CPacket.Type.REMOVE) {
            this.bossBars.remove(arg.getUuid());
        } else {
            this.bossBars.get(arg.getUuid()).handlePacket(arg);
        }
    }

    public void clear() {
        this.bossBars.clear();
    }

    public boolean shouldPlayDragonMusic() {
        if (!this.bossBars.isEmpty()) {
            for (BossBar bossBar : this.bossBars.values()) {
                if (!bossBar.hasDragonMusic()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldDarkenSky() {
        if (!this.bossBars.isEmpty()) {
            for (BossBar bossBar : this.bossBars.values()) {
                if (!bossBar.getDarkenSky()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldThickenFog() {
        if (!this.bossBars.isEmpty()) {
            for (BossBar bossBar : this.bossBars.values()) {
                if (!bossBar.getThickenFog()) continue;
                return true;
            }
        }
        return false;
    }
}

