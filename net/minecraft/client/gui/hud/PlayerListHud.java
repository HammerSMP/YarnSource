/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Ordering
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.StringRenderable;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class PlayerListHud
extends DrawableHelper {
    private static final Ordering<PlayerListEntry> ENTRY_ORDERING = Ordering.from((Comparator)new EntryOrderComparator());
    private final MinecraftClient client;
    private final InGameHud inGameHud;
    private Text footer;
    private Text header;
    private long showTime;
    private boolean visible;

    public PlayerListHud(MinecraftClient arg, InGameHud arg2) {
        this.client = arg;
        this.inGameHud = arg2;
    }

    public Text getPlayerName(PlayerListEntry arg) {
        if (arg.getDisplayName() != null) {
            return this.method_27538(arg, arg.getDisplayName().shallowCopy());
        }
        return this.method_27538(arg, Team.modifyText(arg.getScoreboardTeam(), new LiteralText(arg.getProfile().getName())));
    }

    private Text method_27538(PlayerListEntry arg, MutableText arg2) {
        return arg.getGameMode() == GameMode.SPECTATOR ? arg2.formatted(Formatting.ITALIC) : arg2;
    }

    public void tick(boolean bl) {
        if (bl && !this.visible) {
            this.showTime = Util.getMeasuringTimeMs();
        }
        this.visible = bl;
    }

    public void render(MatrixStack arg, int i, Scoreboard arg2, @Nullable ScoreboardObjective arg3) {
        int r;
        boolean bl;
        int m;
        ClientPlayNetworkHandler lv = this.client.player.networkHandler;
        List list = ENTRY_ORDERING.sortedCopy(lv.getPlayerList());
        int j = 0;
        int k = 0;
        for (PlayerListEntry lv2 : list) {
            int l = this.client.textRenderer.getWidth(this.getPlayerName(lv2));
            j = Math.max(j, l);
            if (arg3 == null || arg3.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) continue;
            l = this.client.textRenderer.getWidth(" " + arg2.getPlayerScore(lv2.getProfile().getName(), arg3).getScore());
            k = Math.max(k, l);
        }
        list = list.subList(0, Math.min(list.size(), 80));
        int n = m = list.size();
        int o = 1;
        while (n > 20) {
            n = (m + ++o - 1) / o;
        }
        boolean bl2 = bl = this.client.isInSingleplayer() || this.client.getNetworkHandler().getConnection().isEncrypted();
        if (arg3 != null) {
            if (arg3.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
                int p = 90;
            } else {
                int q = k;
            }
        } else {
            r = 0;
        }
        int s = Math.min(o * ((bl ? 9 : 0) + j + r + 13), i - 50) / o;
        int t = i / 2 - (s * o + (o - 1) * 5) / 2;
        int u = 10;
        int v = s * o + (o - 1) * 5;
        List<StringRenderable> list2 = null;
        if (this.header != null) {
            list2 = this.client.textRenderer.wrapLines(this.header, i - 50);
            for (StringRenderable stringRenderable : list2) {
                v = Math.max(v, this.client.textRenderer.getWidth(stringRenderable));
            }
        }
        List<StringRenderable> list3 = null;
        if (this.footer != null) {
            list3 = this.client.textRenderer.wrapLines(this.footer, i - 50);
            for (StringRenderable lv4 : list3) {
                v = Math.max(v, this.client.textRenderer.getWidth(lv4));
            }
        }
        if (list2 != null) {
            this.client.textRenderer.getClass();
            PlayerListHud.fill(arg, i / 2 - v / 2 - 1, u - 1, i / 2 + v / 2 + 1, u + list2.size() * 9, Integer.MIN_VALUE);
            for (StringRenderable lv5 : list2) {
                int w = this.client.textRenderer.getWidth(lv5);
                this.client.textRenderer.drawWithShadow(arg, lv5, (float)(i / 2 - w / 2), (float)u, -1);
                this.client.textRenderer.getClass();
                u += 9;
            }
            ++u;
        }
        PlayerListHud.fill(arg, i / 2 - v / 2 - 1, u - 1, i / 2 + v / 2 + 1, u + n * 9, Integer.MIN_VALUE);
        int n2 = this.client.options.getTextBackgroundColor(0x20FFFFFF);
        for (int y = 0; y < m; ++y) {
            int ah;
            int ai;
            int z = y / n;
            int aa = y % n;
            int ab = t + z * s + z * 5;
            int ac = u + aa * 9;
            PlayerListHud.fill(arg, ab, ac, ab + s, ac + 8, n2);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            if (y >= list.size()) continue;
            PlayerListEntry lv6 = (PlayerListEntry)list.get(y);
            GameProfile gameProfile = lv6.getProfile();
            if (bl) {
                PlayerEntity lv7 = this.client.world.getPlayerByUuid(gameProfile.getId());
                boolean bl22 = lv7 != null && lv7.isPartVisible(PlayerModelPart.CAPE) && ("Dinnerbone".equals(gameProfile.getName()) || "Grumm".equals(gameProfile.getName()));
                this.client.getTextureManager().bindTexture(lv6.getSkinTexture());
                int ad = 8 + (bl22 ? 8 : 0);
                int ae = 8 * (bl22 ? -1 : 1);
                DrawableHelper.drawTexture(arg, ab, ac, 8, 8, 8.0f, ad, 8, ae, 64, 64);
                if (lv7 != null && lv7.isPartVisible(PlayerModelPart.HAT)) {
                    int af = 8 + (bl22 ? 8 : 0);
                    int ag = 8 * (bl22 ? -1 : 1);
                    DrawableHelper.drawTexture(arg, ab, ac, 8, 8, 40.0f, af, 8, ag, 64, 64);
                }
                ab += 9;
            }
            this.client.textRenderer.drawWithShadow(arg, this.getPlayerName(lv6), (float)ab, (float)ac, lv6.getGameMode() == GameMode.SPECTATOR ? -1862270977 : -1);
            if (arg3 != null && lv6.getGameMode() != GameMode.SPECTATOR && (ai = (ah = ab + j + 1) + r) - ah > 5) {
                this.renderScoreboardObjective(arg3, ac, gameProfile.getName(), ah, ai, lv6, arg);
            }
            this.renderLatencyIcon(arg, s, ab - (bl ? 9 : 0), ac, lv6);
        }
        if (list3 != null) {
            this.client.textRenderer.getClass();
            PlayerListHud.fill(arg, i / 2 - v / 2 - 1, (u += n * 9 + 1) - 1, i / 2 + v / 2 + 1, u + list3.size() * 9, Integer.MIN_VALUE);
            for (StringRenderable lv8 : list3) {
                int aj = this.client.textRenderer.getWidth(lv8);
                this.client.textRenderer.drawWithShadow(arg, lv8, (float)(i / 2 - aj / 2), (float)u, -1);
                this.client.textRenderer.getClass();
                u += 9;
            }
        }
    }

    protected void renderLatencyIcon(MatrixStack arg, int i, int j, int k, PlayerListEntry arg2) {
        int r;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
        boolean l = false;
        if (arg2.getLatency() < 0) {
            int m = 5;
        } else if (arg2.getLatency() < 150) {
            boolean n = false;
        } else if (arg2.getLatency() < 300) {
            boolean o = true;
        } else if (arg2.getLatency() < 600) {
            int p = 2;
        } else if (arg2.getLatency() < 1000) {
            int q = 3;
        } else {
            r = 4;
        }
        this.setZOffset(this.getZOffset() + 100);
        this.drawTexture(arg, j + i - 11, k, 0, 176 + r * 8, 10, 8);
        this.setZOffset(this.getZOffset() - 100);
    }

    private void renderScoreboardObjective(ScoreboardObjective arg, int i, String string, int j, int k, PlayerListEntry arg2, MatrixStack arg3) {
        int l = arg.getScoreboard().getPlayerScore(string, arg).getScore();
        if (arg.getRenderType() == ScoreboardCriterion.RenderType.HEARTS) {
            boolean bl;
            this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
            long m = Util.getMeasuringTimeMs();
            if (this.showTime == arg2.method_2976()) {
                if (l < arg2.method_2973()) {
                    arg2.method_2978(m);
                    arg2.method_2975(this.inGameHud.getTicks() + 20);
                } else if (l > arg2.method_2973()) {
                    arg2.method_2978(m);
                    arg2.method_2975(this.inGameHud.getTicks() + 10);
                }
            }
            if (m - arg2.method_2974() > 1000L || this.showTime != arg2.method_2976()) {
                arg2.method_2972(l);
                arg2.method_2965(l);
                arg2.method_2978(m);
            }
            arg2.method_2964(this.showTime);
            arg2.method_2972(l);
            int n = MathHelper.ceil((float)Math.max(l, arg2.method_2960()) / 2.0f);
            int o = Math.max(MathHelper.ceil(l / 2), Math.max(MathHelper.ceil(arg2.method_2960() / 2), 10));
            boolean bl2 = bl = arg2.method_2961() > (long)this.inGameHud.getTicks() && (arg2.method_2961() - (long)this.inGameHud.getTicks()) / 3L % 2L == 1L;
            if (n > 0) {
                int p = MathHelper.floor(Math.min((float)(k - j - 4) / (float)o, 9.0f));
                if (p > 3) {
                    for (int q = n; q < o; ++q) {
                        this.drawTexture(arg3, j + q * p, i, bl ? 25 : 16, 0, 9, 9);
                    }
                    for (int r = 0; r < n; ++r) {
                        this.drawTexture(arg3, j + r * p, i, bl ? 25 : 16, 0, 9, 9);
                        if (bl) {
                            if (r * 2 + 1 < arg2.method_2960()) {
                                this.drawTexture(arg3, j + r * p, i, 70, 0, 9, 9);
                            }
                            if (r * 2 + 1 == arg2.method_2960()) {
                                this.drawTexture(arg3, j + r * p, i, 79, 0, 9, 9);
                            }
                        }
                        if (r * 2 + 1 < l) {
                            this.drawTexture(arg3, j + r * p, i, r >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (r * 2 + 1 != l) continue;
                        this.drawTexture(arg3, j + r * p, i, r >= 10 ? 169 : 61, 0, 9, 9);
                    }
                } else {
                    float f = MathHelper.clamp((float)l / 20.0f, 0.0f, 1.0f);
                    int s = (int)((1.0f - f) * 255.0f) << 16 | (int)(f * 255.0f) << 8;
                    String string2 = "" + (float)l / 2.0f;
                    if (k - this.client.textRenderer.getWidth(string2 + "hp") >= j) {
                        string2 = string2 + "hp";
                    }
                    this.client.textRenderer.drawWithShadow(arg3, string2, (float)((k + j) / 2 - this.client.textRenderer.getWidth(string2) / 2), (float)i, s);
                }
            }
        } else {
            String string3 = (Object)((Object)Formatting.YELLOW) + "" + l;
            this.client.textRenderer.drawWithShadow(arg3, string3, (float)(k - this.client.textRenderer.getWidth(string3)), (float)i, 0xFFFFFF);
        }
    }

    public void setFooter(@Nullable Text arg) {
        this.footer = arg;
    }

    public void setHeader(@Nullable Text arg) {
        this.header = arg;
    }

    public void clear() {
        this.header = null;
        this.footer = null;
    }

    @Environment(value=EnvType.CLIENT)
    static class EntryOrderComparator
    implements Comparator<PlayerListEntry> {
        private EntryOrderComparator() {
        }

        @Override
        public int compare(PlayerListEntry arg, PlayerListEntry arg2) {
            Team lv = arg.getScoreboardTeam();
            Team lv2 = arg2.getScoreboardTeam();
            return ComparisonChain.start().compareTrueFirst(arg.getGameMode() != GameMode.SPECTATOR, arg2.getGameMode() != GameMode.SPECTATOR).compare((Comparable)((Object)(lv != null ? lv.getName() : "")), (Comparable)((Object)(lv2 != null ? lv2.getName() : ""))).compare((Object)arg.getProfile().getName(), (Object)arg2.getProfile().getName(), String::compareToIgnoreCase).result();
        }

        @Override
        public /* synthetic */ int compare(Object object, Object object2) {
            return this.compare((PlayerListEntry)object, (PlayerListEntry)object2);
        }
    }
}

