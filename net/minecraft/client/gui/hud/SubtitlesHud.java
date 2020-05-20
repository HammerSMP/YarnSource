/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstanceListener;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class SubtitlesHud
extends DrawableHelper
implements SoundInstanceListener {
    private final MinecraftClient client;
    private final List<SubtitleEntry> entries = Lists.newArrayList();
    private boolean enabled;

    public SubtitlesHud(MinecraftClient arg) {
        this.client = arg;
    }

    public void render(MatrixStack arg) {
        if (!this.enabled && this.client.options.showSubtitles) {
            this.client.getSoundManager().registerListener(this);
            this.enabled = true;
        } else if (this.enabled && !this.client.options.showSubtitles) {
            this.client.getSoundManager().unregisterListener(this);
            this.enabled = false;
        }
        if (!this.enabled || this.entries.isEmpty()) {
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Vec3d lv = new Vec3d(this.client.player.getX(), this.client.player.getEyeY(), this.client.player.getZ());
        Vec3d lv2 = new Vec3d(0.0, 0.0, -1.0).rotateX(-this.client.player.pitch * ((float)Math.PI / 180)).rotateY(-this.client.player.yaw * ((float)Math.PI / 180));
        Vec3d lv3 = new Vec3d(0.0, 1.0, 0.0).rotateX(-this.client.player.pitch * ((float)Math.PI / 180)).rotateY(-this.client.player.yaw * ((float)Math.PI / 180));
        Vec3d lv4 = lv2.crossProduct(lv3);
        int i = 0;
        int j = 0;
        Iterator<SubtitleEntry> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            SubtitleEntry lv5 = iterator.next();
            if (lv5.getTime() + 3000L <= Util.getMeasuringTimeMs()) {
                iterator.remove();
                continue;
            }
            j = Math.max(j, this.client.textRenderer.getWidth(lv5.getText()));
        }
        j += this.client.textRenderer.getWidth("<") + this.client.textRenderer.getWidth(" ") + this.client.textRenderer.getWidth(">") + this.client.textRenderer.getWidth(" ");
        for (SubtitleEntry lv6 : this.entries) {
            int k = 255;
            Text lv7 = lv6.getText();
            Vec3d lv8 = lv6.getPosition().subtract(lv).normalize();
            double d = -lv4.dotProduct(lv8);
            double e = -lv2.dotProduct(lv8);
            boolean bl = e > 0.5;
            int l = j / 2;
            this.client.textRenderer.getClass();
            int m = 9;
            int n = m / 2;
            float f = 1.0f;
            int o = this.client.textRenderer.getWidth(lv7);
            int p = MathHelper.floor(MathHelper.clampedLerp(255.0, 75.0, (float)(Util.getMeasuringTimeMs() - lv6.getTime()) / 3000.0f));
            int q = p << 16 | p << 8 | p;
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)this.client.getWindow().getScaledWidth() - (float)l * 1.0f - 2.0f, (float)(this.client.getWindow().getScaledHeight() - 30) - (float)(i * (m + 1)) * 1.0f, 0.0f);
            RenderSystem.scalef(1.0f, 1.0f, 1.0f);
            SubtitlesHud.fill(arg, -l - 1, -n - 1, l + 1, n + 1, this.client.options.getTextBackgroundColor(0.8f));
            RenderSystem.enableBlend();
            if (!bl) {
                if (d > 0.0) {
                    this.client.textRenderer.draw(arg, ">", (float)(l - this.client.textRenderer.getWidth(">")), (float)(-n), q + -16777216);
                } else if (d < 0.0) {
                    this.client.textRenderer.draw(arg, "<", (float)(-l), (float)(-n), q + -16777216);
                }
            }
            this.client.textRenderer.draw(arg, lv7, (float)(-o / 2), (float)(-n), q + -16777216);
            RenderSystem.popMatrix();
            ++i;
        }
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    @Override
    public void onSoundPlayed(SoundInstance arg, WeightedSoundSet arg2) {
        if (arg2.getSubtitle() == null) {
            return;
        }
        Text lv = arg2.getSubtitle();
        if (!this.entries.isEmpty()) {
            for (SubtitleEntry lv2 : this.entries) {
                if (!lv2.getText().equals(lv)) continue;
                lv2.reset(new Vec3d(arg.getX(), arg.getY(), arg.getZ()));
                return;
            }
        }
        this.entries.add(new SubtitleEntry(lv, new Vec3d(arg.getX(), arg.getY(), arg.getZ())));
    }

    @Environment(value=EnvType.CLIENT)
    public class SubtitleEntry {
        private final Text text;
        private long time;
        private Vec3d pos;

        public SubtitleEntry(Text arg2, Vec3d arg3) {
            this.text = arg2;
            this.pos = arg3;
            this.time = Util.getMeasuringTimeMs();
        }

        public Text getText() {
            return this.text;
        }

        public long getTime() {
            return this.time;
        }

        public Vec3d getPosition() {
            return this.pos;
        }

        public void reset(Vec3d arg) {
            this.pos = arg;
            this.time = Util.getMeasuringTimeMs();
        }
    }
}

