/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ClientChatListener;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.GameInfoChatListener;
import net.minecraft.client.gui.hud.BackgroundHelper;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatListenerHud;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.SubtitlesHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.MessageType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Arm;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.border.WorldBorder;

@Environment(value=EnvType.CLIENT)
public class InGameHud
extends DrawableHelper {
    private static final Identifier VIGNETTE_TEX = new Identifier("textures/misc/vignette.png");
    private static final Identifier WIDGETS_TEX = new Identifier("textures/gui/widgets.png");
    private static final Identifier PUMPKIN_BLUR = new Identifier("textures/misc/pumpkinblur.png");
    private final Random random = new Random();
    private final MinecraftClient client;
    private final ItemRenderer itemRenderer;
    private final ChatHud chatHud;
    private int ticks;
    @Nullable
    private Text overlayMessage;
    private int overlayRemaining;
    private boolean overlayTinted;
    public float vignetteDarkness = 1.0f;
    private int heldItemTooltipFade;
    private ItemStack currentStack = ItemStack.EMPTY;
    private final DebugHud debugHud;
    private final SubtitlesHud subtitlesHud;
    private final SpectatorHud spectatorHud;
    private final PlayerListHud playerListHud;
    private final BossBarHud bossBarHud;
    private int titleTotalTicks;
    @Nullable
    private Text title;
    @Nullable
    private Text subtitle;
    private int titleFadeInTicks;
    private int titleRemainTicks;
    private int titleFadeOutTicks;
    private int lastHealthValue;
    private int renderHealthValue;
    private long lastHealthCheckTime;
    private long heartJumpEndTick;
    private int scaledWidth;
    private int scaledHeight;
    private final Map<MessageType, List<ClientChatListener>> listeners = Maps.newHashMap();

    public InGameHud(MinecraftClient arg) {
        this.client = arg;
        this.itemRenderer = arg.getItemRenderer();
        this.debugHud = new DebugHud(arg);
        this.spectatorHud = new SpectatorHud(arg);
        this.chatHud = new ChatHud(arg);
        this.playerListHud = new PlayerListHud(arg, this);
        this.bossBarHud = new BossBarHud(arg);
        this.subtitlesHud = new SubtitlesHud(arg);
        for (MessageType lv : MessageType.values()) {
            this.listeners.put(lv, Lists.newArrayList());
        }
        NarratorManager lv2 = NarratorManager.INSTANCE;
        this.listeners.get((Object)MessageType.CHAT).add(new ChatListenerHud(arg));
        this.listeners.get((Object)MessageType.CHAT).add(lv2);
        this.listeners.get((Object)MessageType.SYSTEM).add(new ChatListenerHud(arg));
        this.listeners.get((Object)MessageType.SYSTEM).add(lv2);
        this.listeners.get((Object)MessageType.GAME_INFO).add(new GameInfoChatListener(arg));
        this.setDefaultTitleFade();
    }

    public void setDefaultTitleFade() {
        this.titleFadeInTicks = 10;
        this.titleRemainTicks = 70;
        this.titleFadeOutTicks = 20;
    }

    public void render(MatrixStack arg, float f) {
        float g;
        this.scaledWidth = this.client.getWindow().getScaledWidth();
        this.scaledHeight = this.client.getWindow().getScaledHeight();
        TextRenderer lv = this.getFontRenderer();
        RenderSystem.enableBlend();
        if (MinecraftClient.isFancyGraphicsEnabled()) {
            this.renderVignetteOverlay(this.client.getCameraEntity());
        } else {
            RenderSystem.enableDepthTest();
            RenderSystem.defaultBlendFunc();
        }
        ItemStack lv2 = this.client.player.inventory.getArmorStack(3);
        if (this.client.options.perspective == 0 && lv2.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
            this.renderPumpkinOverlay();
        }
        if (!this.client.player.hasStatusEffect(StatusEffects.NAUSEA) && (g = MathHelper.lerp(f, this.client.player.lastNauseaStrength, this.client.player.nextNauseaStrength)) > 0.0f) {
            this.renderPortalOverlay(g);
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            this.spectatorHud.render(arg, f);
        } else if (!this.client.options.hudHidden) {
            this.renderHotbar(f, arg);
        }
        if (!this.client.options.hudHidden) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            this.renderCrosshair(arg);
            RenderSystem.defaultBlendFunc();
            this.client.getProfiler().push("bossHealth");
            this.bossBarHud.render(arg);
            this.client.getProfiler().pop();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(GUI_ICONS_TEXTURE);
            if (this.client.interactionManager.hasStatusBars()) {
                this.renderStatusBars(arg);
            }
            this.renderMountHealth(arg);
            RenderSystem.disableBlend();
            int i = this.scaledWidth / 2 - 91;
            if (this.client.player.hasJumpingMount()) {
                this.renderMountJumpBar(arg, i);
            } else if (this.client.interactionManager.hasExperienceBar()) {
                this.renderExperienceBar(arg, i);
            }
            if (this.client.options.heldItemTooltips && this.client.interactionManager.getCurrentGameMode() != GameMode.SPECTATOR) {
                this.renderHeldItemTooltip(arg);
            } else if (this.client.player.isSpectator()) {
                this.spectatorHud.render(arg);
            }
        }
        if (this.client.player.getSleepTimer() > 0) {
            this.client.getProfiler().push("sleep");
            RenderSystem.disableDepthTest();
            RenderSystem.disableAlphaTest();
            float h = this.client.player.getSleepTimer();
            float j = h / 100.0f;
            if (j > 1.0f) {
                j = 1.0f - (h - 100.0f) / 10.0f;
            }
            int k = (int)(220.0f * j) << 24 | 0x101020;
            InGameHud.fill(arg, 0, 0, this.scaledWidth, this.scaledHeight, k);
            RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            this.client.getProfiler().pop();
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.client.isDemo()) {
            this.renderDemoTimer(arg);
        }
        this.renderStatusEffectOverlay(arg);
        if (this.client.options.debugEnabled) {
            this.debugHud.render(arg);
        }
        if (!this.client.options.hudHidden) {
            ScoreboardObjective lv6;
            int w;
            if (this.overlayMessage != null && this.overlayRemaining > 0) {
                this.client.getProfiler().push("overlayMessage");
                float l = (float)this.overlayRemaining - f;
                int m = (int)(l * 255.0f / 20.0f);
                if (m > 255) {
                    m = 255;
                }
                if (m > 8) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(this.scaledWidth / 2, this.scaledHeight - 68, 0.0f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    int n = 0xFFFFFF;
                    if (this.overlayTinted) {
                        n = MathHelper.hsvToRgb(l / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                    }
                    int o = m << 24 & 0xFF000000;
                    int p = lv.getWidth(this.overlayMessage);
                    this.drawTextBackground(arg, lv, -4, p, 0xFFFFFF | o);
                    lv.draw(arg, this.overlayMessage, (float)(-p / 2), -4.0f, n | o);
                    RenderSystem.disableBlend();
                    RenderSystem.popMatrix();
                }
                this.client.getProfiler().pop();
            }
            if (this.title != null && this.titleTotalTicks > 0) {
                this.client.getProfiler().push("titleAndSubtitle");
                float q = (float)this.titleTotalTicks - f;
                int r = 255;
                if (this.titleTotalTicks > this.titleFadeOutTicks + this.titleRemainTicks) {
                    float s = (float)(this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks) - q;
                    r = (int)(s * 255.0f / (float)this.titleFadeInTicks);
                }
                if (this.titleTotalTicks <= this.titleFadeOutTicks) {
                    r = (int)(q * 255.0f / (float)this.titleFadeOutTicks);
                }
                if ((r = MathHelper.clamp(r, 0, 255)) > 8) {
                    RenderSystem.pushMatrix();
                    RenderSystem.translatef(this.scaledWidth / 2, this.scaledHeight / 2, 0.0f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.pushMatrix();
                    RenderSystem.scalef(4.0f, 4.0f, 4.0f);
                    int t = r << 24 & 0xFF000000;
                    int u = lv.getWidth(this.title);
                    this.drawTextBackground(arg, lv, -10, u, 0xFFFFFF | t);
                    lv.drawWithShadow(arg, this.title, (float)(-u / 2), -10.0f, 0xFFFFFF | t);
                    RenderSystem.popMatrix();
                    if (this.subtitle != null) {
                        RenderSystem.pushMatrix();
                        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
                        int v = lv.getWidth(this.subtitle);
                        this.drawTextBackground(arg, lv, 5, v, 0xFFFFFF | t);
                        lv.drawWithShadow(arg, this.subtitle, (float)(-v / 2), 5.0f, 0xFFFFFF | t);
                        RenderSystem.popMatrix();
                    }
                    RenderSystem.disableBlend();
                    RenderSystem.popMatrix();
                }
                this.client.getProfiler().pop();
            }
            this.subtitlesHud.render(arg);
            Scoreboard lv3 = this.client.world.getScoreboard();
            ScoreboardObjective lv4 = null;
            Team lv5 = lv3.getPlayerTeam(this.client.player.getEntityName());
            if (lv5 != null && (w = lv5.getColor().getColorIndex()) >= 0) {
                lv4 = lv3.getObjectiveForSlot(3 + w);
            }
            ScoreboardObjective scoreboardObjective = lv6 = lv4 != null ? lv4 : lv3.getObjectiveForSlot(1);
            if (lv6 != null) {
                this.renderScoreboardSidebar(arg, lv6);
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableAlphaTest();
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, this.scaledHeight - 48, 0.0f);
            this.client.getProfiler().push("chat");
            this.chatHud.render(arg, this.ticks);
            this.client.getProfiler().pop();
            RenderSystem.popMatrix();
            lv6 = lv3.getObjectiveForSlot(0);
            if (this.client.options.keyPlayerList.isPressed() && (!this.client.isInSingleplayer() || this.client.player.networkHandler.getPlayerList().size() > 1 || lv6 != null)) {
                this.playerListHud.tick(true);
                this.playerListHud.render(arg, this.scaledWidth, lv3, lv6);
            } else {
                this.playerListHud.tick(false);
            }
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableAlphaTest();
    }

    private void drawTextBackground(MatrixStack arg, TextRenderer arg2, int i, int j, int k) {
        int l = this.client.options.getTextBackgroundColor(0.0f);
        if (l != 0) {
            int m = -j / 2;
            arg2.getClass();
            InGameHud.fill(arg, m - 2, i - 2, m + j + 2, i + 9 + 2, BackgroundHelper.ColorMixer.mixColor(l, k));
        }
    }

    private void renderCrosshair(MatrixStack arg) {
        GameOptions lv = this.client.options;
        if (lv.perspective != 0) {
            return;
        }
        if (this.client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR && !this.shouldRenderSpectatorCrosshair(this.client.crosshairTarget)) {
            return;
        }
        if (lv.debugEnabled && !lv.hudHidden && !this.client.player.getReducedDebugInfo() && !lv.reducedDebugInfo) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.scaledWidth / 2, this.scaledHeight / 2, this.getZOffset());
            Camera lv2 = this.client.gameRenderer.getCamera();
            RenderSystem.rotatef(lv2.getPitch(), -1.0f, 0.0f, 0.0f);
            RenderSystem.rotatef(lv2.getYaw(), 0.0f, 1.0f, 0.0f);
            RenderSystem.scalef(-1.0f, -1.0f, -1.0f);
            RenderSystem.renderCrosshair(10);
            RenderSystem.popMatrix();
        } else {
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
            int i = 15;
            this.drawTexture(arg, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 0, 0, 15, 15);
            if (this.client.options.attackIndicator == AttackIndicator.CROSSHAIR) {
                float f = this.client.player.getAttackCooldownProgress(0.0f);
                boolean bl = false;
                if (this.client.targetedEntity != null && this.client.targetedEntity instanceof LivingEntity && f >= 1.0f) {
                    bl = this.client.player.getAttackCooldownProgressPerTick() > 5.0f;
                    bl &= this.client.targetedEntity.isAlive();
                }
                int j = this.scaledHeight / 2 - 7 + 16;
                int k = this.scaledWidth / 2 - 8;
                if (bl) {
                    this.drawTexture(arg, k, j, 68, 94, 16, 16);
                } else if (f < 1.0f) {
                    int l = (int)(f * 17.0f);
                    this.drawTexture(arg, k, j, 36, 94, 16, 4);
                    this.drawTexture(arg, k, j, 52, 94, l, 4);
                }
            }
        }
    }

    private boolean shouldRenderSpectatorCrosshair(HitResult arg) {
        if (arg == null) {
            return false;
        }
        if (arg.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)arg).getEntity() instanceof NamedScreenHandlerFactory;
        }
        if (arg.getType() == HitResult.Type.BLOCK) {
            ClientWorld lv2 = this.client.world;
            BlockPos lv = ((BlockHitResult)arg).getBlockPos();
            return lv2.getBlockState(lv).createScreenHandlerFactory(lv2, lv) != null;
        }
        return false;
    }

    protected void renderStatusEffectOverlay(MatrixStack arg) {
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (collection.isEmpty()) {
            return;
        }
        RenderSystem.enableBlend();
        int i = 0;
        int j = 0;
        StatusEffectSpriteManager lv = this.client.getStatusEffectSpriteManager();
        ArrayList list = Lists.newArrayListWithExpectedSize((int)collection.size());
        this.client.getTextureManager().bindTexture(HandledScreen.BACKGROUND_TEXTURE);
        for (StatusEffectInstance lv2 : Ordering.natural().reverse().sortedCopy(collection)) {
            StatusEffect lv3 = lv2.getEffectType();
            if (!lv2.shouldShowIcon()) continue;
            int k = this.scaledWidth;
            int l = 1;
            if (this.client.isDemo()) {
                l += 15;
            }
            if (lv3.isBeneficial()) {
                k -= 25 * ++i;
            } else {
                k -= 25 * ++j;
                l += 26;
            }
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            float f = 1.0f;
            if (lv2.isAmbient()) {
                this.drawTexture(arg, k, l, 165, 166, 24, 24);
            } else {
                this.drawTexture(arg, k, l, 141, 166, 24, 24);
                if (lv2.getDuration() <= 200) {
                    int m = 10 - lv2.getDuration() / 20;
                    f = MathHelper.clamp((float)lv2.getDuration() / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + MathHelper.cos((float)lv2.getDuration() * (float)Math.PI / 5.0f) * MathHelper.clamp((float)m / 10.0f * 0.25f, 0.0f, 0.25f);
                }
            }
            Sprite lv4 = lv.getSprite(lv3);
            int n = k;
            int o = l;
            float g = f;
            list.add(() -> {
                this.client.getTextureManager().bindTexture(lv4.getAtlas().getId());
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, g);
                InGameHud.drawSprite(arg, n + 3, o + 3, this.getZOffset(), 18, 18, lv4);
            });
        }
        list.forEach(Runnable::run);
    }

    protected void renderHotbar(float f, MatrixStack arg) {
        float g;
        PlayerEntity lv = this.getCameraPlayer();
        if (lv == null) {
            return;
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.client.getTextureManager().bindTexture(WIDGETS_TEX);
        ItemStack lv2 = lv.getOffHandStack();
        Arm lv3 = lv.getMainArm().getOpposite();
        int i = this.scaledWidth / 2;
        int j = this.getZOffset();
        int k = 182;
        int l = 91;
        this.setZOffset(-90);
        this.drawTexture(arg, i - 91, this.scaledHeight - 22, 0, 0, 182, 22);
        this.drawTexture(arg, i - 91 - 1 + lv.inventory.selectedSlot * 20, this.scaledHeight - 22 - 1, 0, 22, 24, 22);
        if (!lv2.isEmpty()) {
            if (lv3 == Arm.LEFT) {
                this.drawTexture(arg, i - 91 - 29, this.scaledHeight - 23, 24, 22, 29, 24);
            } else {
                this.drawTexture(arg, i + 91, this.scaledHeight - 23, 53, 22, 29, 24);
            }
        }
        this.setZOffset(j);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (int m = 0; m < 9; ++m) {
            int n = i - 90 + m * 20 + 2;
            int o = this.scaledHeight - 16 - 3;
            this.renderHotbarItem(n, o, f, lv, lv.inventory.main.get(m));
        }
        if (!lv2.isEmpty()) {
            int p = this.scaledHeight - 16 - 3;
            if (lv3 == Arm.LEFT) {
                this.renderHotbarItem(i - 91 - 26, p, f, lv, lv2);
            } else {
                this.renderHotbarItem(i + 91 + 10, p, f, lv, lv2);
            }
        }
        if (this.client.options.attackIndicator == AttackIndicator.HOTBAR && (g = this.client.player.getAttackCooldownProgress(0.0f)) < 1.0f) {
            int q = this.scaledHeight - 20;
            int r = i + 91 + 6;
            if (lv3 == Arm.RIGHT) {
                r = i - 91 - 22;
            }
            this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
            int s = (int)(g * 19.0f);
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawTexture(arg, r, q, 0, 94, 18, 18);
            this.drawTexture(arg, r, q + 18 - s, 18, 112 - s, 18, s);
        }
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
    }

    public void renderMountJumpBar(MatrixStack arg, int i) {
        this.client.getProfiler().push("jumpBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        float f = this.client.player.method_3151();
        int j = 182;
        int k = (int)(f * 183.0f);
        int l = this.scaledHeight - 32 + 3;
        this.drawTexture(arg, i, l, 0, 84, 182, 5);
        if (k > 0) {
            this.drawTexture(arg, i, l, 0, 89, k, 5);
        }
        this.client.getProfiler().pop();
    }

    public void renderExperienceBar(MatrixStack arg, int i) {
        this.client.getProfiler().push("expBar");
        this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
        int j = this.client.player.getNextLevelExperience();
        if (j > 0) {
            int k = 182;
            int l = (int)(this.client.player.experienceProgress * 183.0f);
            int m = this.scaledHeight - 32 + 3;
            this.drawTexture(arg, i, m, 0, 64, 182, 5);
            if (l > 0) {
                this.drawTexture(arg, i, m, 0, 69, l, 5);
            }
        }
        this.client.getProfiler().pop();
        if (this.client.player.experienceLevel > 0) {
            this.client.getProfiler().push("expLevel");
            String string = "" + this.client.player.experienceLevel;
            int n = (this.scaledWidth - this.getFontRenderer().getWidth(string)) / 2;
            int o = this.scaledHeight - 31 - 4;
            this.getFontRenderer().draw(arg, string, (float)(n + 1), (float)o, 0);
            this.getFontRenderer().draw(arg, string, (float)(n - 1), (float)o, 0);
            this.getFontRenderer().draw(arg, string, (float)n, (float)(o + 1), 0);
            this.getFontRenderer().draw(arg, string, (float)n, (float)(o - 1), 0);
            this.getFontRenderer().draw(arg, string, (float)n, (float)o, 8453920);
            this.client.getProfiler().pop();
        }
    }

    public void renderHeldItemTooltip(MatrixStack arg) {
        this.client.getProfiler().push("selectedItemName");
        if (this.heldItemTooltipFade > 0 && !this.currentStack.isEmpty()) {
            int l;
            MutableText lv = new LiteralText("").append(this.currentStack.getName()).formatted(this.currentStack.getRarity().formatting);
            if (this.currentStack.hasCustomName()) {
                lv.formatted(Formatting.ITALIC);
            }
            int i = this.getFontRenderer().getWidth(lv);
            int j = (this.scaledWidth - i) / 2;
            int k = this.scaledHeight - 59;
            if (!this.client.interactionManager.hasStatusBars()) {
                k += 14;
            }
            if ((l = (int)((float)this.heldItemTooltipFade * 256.0f / 10.0f)) > 255) {
                l = 255;
            }
            if (l > 0) {
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.getFontRenderer().getClass();
                InGameHud.fill(arg, j - 2, k - 2, j + i + 2, k + 9 + 2, this.client.options.getTextBackgroundColor(0));
                this.getFontRenderer().drawWithShadow(arg, lv, (float)j, (float)k, 0xFFFFFF + (l << 24));
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
        this.client.getProfiler().pop();
    }

    public void renderDemoTimer(MatrixStack arg) {
        String string2;
        this.client.getProfiler().push("demo");
        if (this.client.world.getTime() >= 120500L) {
            String string = I18n.translate("demo.demoExpired", new Object[0]);
        } else {
            string2 = I18n.translate("demo.remainingTime", ChatUtil.ticksToString((int)(120500L - this.client.world.getTime())));
        }
        int i = this.getFontRenderer().getWidth(string2);
        this.getFontRenderer().drawWithShadow(arg, string2, (float)(this.scaledWidth - i - 10), 5.0f, 0xFFFFFF);
        this.client.getProfiler().pop();
    }

    private void renderScoreboardSidebar(MatrixStack arg2, ScoreboardObjective arg22) {
        int i;
        Scoreboard lv = arg22.getScoreboard();
        List<Object> collection = lv.getAllPlayerScores(arg22);
        List list = collection.stream().filter(arg -> arg.getPlayerName() != null && !arg.getPlayerName().startsWith("#")).collect(Collectors.toList());
        collection = list.size() > 15 ? Lists.newArrayList((Iterable)Iterables.skip(list, (int)(collection.size() - 15))) : list;
        ArrayList list2 = Lists.newArrayListWithCapacity((int)collection.size());
        Text lv2 = arg22.getDisplayName();
        int j = i = this.getFontRenderer().getWidth(lv2);
        int k = this.getFontRenderer().getWidth(": ");
        for (ScoreboardPlayerScore lv3 : collection) {
            Team lv4 = lv.getPlayerTeam(lv3.getPlayerName());
            MutableText lv5 = Team.modifyText(lv4, new LiteralText(lv3.getPlayerName()));
            list2.add(Pair.of((Object)lv3, (Object)lv5));
            j = Math.max(j, this.getFontRenderer().getWidth(lv5) + k + this.getFontRenderer().getWidth(Integer.toString(lv3.getScore())));
        }
        this.getFontRenderer().getClass();
        int l = collection.size() * 9;
        int m = this.scaledHeight / 2 + l / 3;
        int n = 3;
        int o = this.scaledWidth - j - 3;
        int p = 0;
        int q = this.client.options.getTextBackgroundColor(0.3f);
        int r = this.client.options.getTextBackgroundColor(0.4f);
        for (Pair pair : list2) {
            ScoreboardPlayerScore lv6 = (ScoreboardPlayerScore)pair.getFirst();
            Text lv7 = (Text)pair.getSecond();
            String string = (Object)((Object)Formatting.RED) + "" + lv6.getScore();
            int s = o;
            this.getFontRenderer().getClass();
            int t = m - ++p * 9;
            int u = this.scaledWidth - 3 + 2;
            this.getFontRenderer().getClass();
            InGameHud.fill(arg2, s - 2, t, u, t + 9, q);
            this.getFontRenderer().draw(arg2, lv7, (float)s, (float)t, -1);
            this.getFontRenderer().draw(arg2, string, (float)(u - this.getFontRenderer().getWidth(string)), (float)t, -1);
            if (p != collection.size()) continue;
            this.getFontRenderer().getClass();
            InGameHud.fill(arg2, s - 2, t - 9 - 1, u, t - 1, r);
            InGameHud.fill(arg2, s - 2, t - 1, u, t, q);
            this.getFontRenderer().getClass();
            this.getFontRenderer().draw(arg2, lv2, (float)(s + j / 2 - i / 2), (float)(t - 9), -1);
        }
    }

    private PlayerEntity getCameraPlayer() {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return null;
        }
        return (PlayerEntity)this.client.getCameraEntity();
    }

    private LivingEntity getRiddenEntity() {
        PlayerEntity lv = this.getCameraPlayer();
        if (lv != null) {
            Entity lv2 = lv.getVehicle();
            if (lv2 == null) {
                return null;
            }
            if (lv2 instanceof LivingEntity) {
                return (LivingEntity)lv2;
            }
        }
        return null;
    }

    private int getHeartCount(LivingEntity arg) {
        if (arg == null || !arg.isLiving()) {
            return 0;
        }
        float f = arg.getMaximumHealth();
        int i = (int)(f + 0.5f) / 2;
        if (i > 30) {
            i = 30;
        }
        return i;
    }

    private int getHeartRows(int i) {
        return (int)Math.ceil((double)i / 10.0);
    }

    private void renderStatusBars(MatrixStack arg) {
        PlayerEntity lv = this.getCameraPlayer();
        if (lv == null) {
            return;
        }
        int i = MathHelper.ceil(lv.getHealth());
        boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
        long l = Util.getMeasuringTimeMs();
        if (i < this.lastHealthValue && lv.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 20;
        } else if (i > this.lastHealthValue && lv.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 10;
        }
        if (l - this.lastHealthCheckTime > 1000L) {
            this.lastHealthValue = i;
            this.renderHealthValue = i;
            this.lastHealthCheckTime = l;
        }
        this.lastHealthValue = i;
        int j = this.renderHealthValue;
        this.random.setSeed(this.ticks * 312871);
        HungerManager lv2 = lv.getHungerManager();
        int k = lv2.getFoodLevel();
        int m = this.scaledWidth / 2 - 91;
        int n = this.scaledWidth / 2 + 91;
        int o = this.scaledHeight - 39;
        float f = (float)lv.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH);
        int p = MathHelper.ceil(lv.getAbsorptionAmount());
        int q = MathHelper.ceil((f + (float)p) / 2.0f / 10.0f);
        int r = Math.max(10 - (q - 2), 3);
        int s = o - (q - 1) * r - 10;
        int t = o - 10;
        int u = p;
        int v = lv.getArmor();
        int w = -1;
        if (lv.hasStatusEffect(StatusEffects.REGENERATION)) {
            w = this.ticks % MathHelper.ceil(f + 5.0f);
        }
        this.client.getProfiler().push("armor");
        for (int x = 0; x < 10; ++x) {
            if (v <= 0) continue;
            int y = m + x * 8;
            if (x * 2 + 1 < v) {
                this.drawTexture(arg, y, s, 34, 9, 9, 9);
            }
            if (x * 2 + 1 == v) {
                this.drawTexture(arg, y, s, 25, 9, 9, 9);
            }
            if (x * 2 + 1 <= v) continue;
            this.drawTexture(arg, y, s, 16, 9, 9, 9);
        }
        this.client.getProfiler().swap("health");
        for (int z = MathHelper.ceil((f + (float)p) / 2.0f) - 1; z >= 0; --z) {
            int aa = 16;
            if (lv.hasStatusEffect(StatusEffects.POISON)) {
                aa += 36;
            } else if (lv.hasStatusEffect(StatusEffects.WITHER)) {
                aa += 72;
            }
            int ab = 0;
            if (bl) {
                ab = 1;
            }
            int ac = MathHelper.ceil((float)(z + 1) / 10.0f) - 1;
            int ad = m + z % 10 * 8;
            int ae = o - ac * r;
            if (i <= 4) {
                ae += this.random.nextInt(2);
            }
            if (u <= 0 && z == w) {
                ae -= 2;
            }
            int af = 0;
            if (lv.world.getLevelProperties().isHardcore()) {
                af = 5;
            }
            this.drawTexture(arg, ad, ae, 16 + ab * 9, 9 * af, 9, 9);
            if (bl) {
                if (z * 2 + 1 < j) {
                    this.drawTexture(arg, ad, ae, aa + 54, 9 * af, 9, 9);
                }
                if (z * 2 + 1 == j) {
                    this.drawTexture(arg, ad, ae, aa + 63, 9 * af, 9, 9);
                }
            }
            if (u > 0) {
                if (u == p && p % 2 == 1) {
                    this.drawTexture(arg, ad, ae, aa + 153, 9 * af, 9, 9);
                    --u;
                    continue;
                }
                this.drawTexture(arg, ad, ae, aa + 144, 9 * af, 9, 9);
                u -= 2;
                continue;
            }
            if (z * 2 + 1 < i) {
                this.drawTexture(arg, ad, ae, aa + 36, 9 * af, 9, 9);
            }
            if (z * 2 + 1 != i) continue;
            this.drawTexture(arg, ad, ae, aa + 45, 9 * af, 9, 9);
        }
        LivingEntity lv3 = this.getRiddenEntity();
        int ag = this.getHeartCount(lv3);
        if (ag == 0) {
            this.client.getProfiler().swap("food");
            for (int ah = 0; ah < 10; ++ah) {
                int ai = o;
                int aj = 16;
                int ak = 0;
                if (lv.hasStatusEffect(StatusEffects.HUNGER)) {
                    aj += 36;
                    ak = 13;
                }
                if (lv.getHungerManager().getSaturationLevel() <= 0.0f && this.ticks % (k * 3 + 1) == 0) {
                    ai += this.random.nextInt(3) - 1;
                }
                int al = n - ah * 8 - 9;
                this.drawTexture(arg, al, ai, 16 + ak * 9, 27, 9, 9);
                if (ah * 2 + 1 < k) {
                    this.drawTexture(arg, al, ai, aj + 36, 27, 9, 9);
                }
                if (ah * 2 + 1 != k) continue;
                this.drawTexture(arg, al, ai, aj + 45, 27, 9, 9);
            }
            t -= 10;
        }
        this.client.getProfiler().swap("air");
        int am = lv.getAir();
        int an = lv.getMaxAir();
        if (lv.isSubmergedIn(FluidTags.WATER) || am < an) {
            int ao = this.getHeartRows(ag) - 1;
            t -= ao * 10;
            int ap = MathHelper.ceil((double)(am - 2) * 10.0 / (double)an);
            int aq = MathHelper.ceil((double)am * 10.0 / (double)an) - ap;
            for (int ar = 0; ar < ap + aq; ++ar) {
                if (ar < ap) {
                    this.drawTexture(arg, n - ar * 8 - 9, t, 16, 18, 9, 9);
                    continue;
                }
                this.drawTexture(arg, n - ar * 8 - 9, t, 25, 18, 9, 9);
            }
        }
        this.client.getProfiler().pop();
    }

    private void renderMountHealth(MatrixStack arg) {
        LivingEntity lv = this.getRiddenEntity();
        if (lv == null) {
            return;
        }
        int i = this.getHeartCount(lv);
        if (i == 0) {
            return;
        }
        int j = (int)Math.ceil(lv.getHealth());
        this.client.getProfiler().swap("mountHealth");
        int k = this.scaledHeight - 39;
        int l = this.scaledWidth / 2 + 91;
        int m = k;
        int n = 0;
        boolean bl = false;
        while (i > 0) {
            int o = Math.min(i, 10);
            i -= o;
            for (int p = 0; p < o; ++p) {
                int q = 52;
                int r = 0;
                int s = l - p * 8 - 9;
                this.drawTexture(arg, s, m, 52 + r * 9, 9, 9, 9);
                if (p * 2 + 1 + n < j) {
                    this.drawTexture(arg, s, m, 88, 9, 9, 9);
                }
                if (p * 2 + 1 + n != j) continue;
                this.drawTexture(arg, s, m, 97, 9, 9, 9);
            }
            m -= 10;
            n += 20;
        }
    }

    private void renderPumpkinOverlay() {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableAlphaTest();
        this.client.getTextureManager().bindTexture(PUMPKIN_BLUR);
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(7, VertexFormats.POSITION_TEXTURE);
        lv2.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        lv2.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        lv2.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        lv2.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        lv.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void updateVignetteDarkness(Entity arg) {
        if (arg == null) {
            return;
        }
        float f = MathHelper.clamp(1.0f - arg.getBrightnessAtEyes(), 0.0f, 1.0f);
        this.vignetteDarkness = (float)((double)this.vignetteDarkness + (double)(f - this.vignetteDarkness) * 0.01);
    }

    private void renderVignetteOverlay(Entity arg) {
        WorldBorder lv = this.client.world.getWorldBorder();
        float f = (float)lv.getDistanceInsideBorder(arg);
        double d = Math.min(lv.getShrinkingSpeed() * (double)lv.getWarningTime() * 1000.0, Math.abs(lv.getTargetSize() - lv.getSize()));
        double e = Math.max((double)lv.getWarningBlocks(), d);
        f = (double)f < e ? 1.0f - (float)((double)f / e) : 0.0f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        if (f > 0.0f) {
            RenderSystem.color4f(0.0f, f, f, 1.0f);
        } else {
            RenderSystem.color4f(this.vignetteDarkness, this.vignetteDarkness, this.vignetteDarkness, 1.0f);
        }
        this.client.getTextureManager().bindTexture(VIGNETTE_TEX);
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        lv3.begin(7, VertexFormats.POSITION_TEXTURE);
        lv3.vertex(0.0, this.scaledHeight, -90.0).texture(0.0f, 1.0f).next();
        lv3.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(1.0f, 1.0f).next();
        lv3.vertex(this.scaledWidth, 0.0, -90.0).texture(1.0f, 0.0f).next();
        lv3.vertex(0.0, 0.0, -90.0).texture(0.0f, 0.0f).next();
        lv2.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
    }

    private void renderPortalOverlay(float f) {
        if (f < 1.0f) {
            f *= f;
            f *= f;
            f = f * 0.8f + 0.2f;
        }
        RenderSystem.disableAlphaTest();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, f);
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        Sprite lv = this.client.getBlockRenderManager().getModels().getSprite(Blocks.NETHER_PORTAL.getDefaultState());
        float g = lv.getMinU();
        float h = lv.getMinV();
        float i = lv.getMaxU();
        float j = lv.getMaxV();
        Tessellator lv2 = Tessellator.getInstance();
        BufferBuilder lv3 = lv2.getBuffer();
        lv3.begin(7, VertexFormats.POSITION_TEXTURE);
        lv3.vertex(0.0, this.scaledHeight, -90.0).texture(g, j).next();
        lv3.vertex(this.scaledWidth, this.scaledHeight, -90.0).texture(i, j).next();
        lv3.vertex(this.scaledWidth, 0.0, -90.0).texture(i, h).next();
        lv3.vertex(0.0, 0.0, -90.0).texture(g, h).next();
        lv2.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbarItem(int i, int j, float f, PlayerEntity arg, ItemStack arg2) {
        if (arg2.isEmpty()) {
            return;
        }
        float g = (float)arg2.getCooldown() - f;
        if (g > 0.0f) {
            RenderSystem.pushMatrix();
            float h = 1.0f + g / 5.0f;
            RenderSystem.translatef(i + 8, j + 12, 0.0f);
            RenderSystem.scalef(1.0f / h, (h + 1.0f) / 2.0f, 1.0f);
            RenderSystem.translatef(-(i + 8), -(j + 12), 0.0f);
        }
        this.itemRenderer.method_27951(arg, arg2, i, j);
        if (g > 0.0f) {
            RenderSystem.popMatrix();
        }
        this.itemRenderer.renderGuiItemOverlay(this.client.textRenderer, arg2, i, j);
    }

    public void tick() {
        if (this.overlayRemaining > 0) {
            --this.overlayRemaining;
        }
        if (this.titleTotalTicks > 0) {
            --this.titleTotalTicks;
            if (this.titleTotalTicks <= 0) {
                this.title = null;
                this.subtitle = null;
            }
        }
        ++this.ticks;
        Entity lv = this.client.getCameraEntity();
        if (lv != null) {
            this.updateVignetteDarkness(lv);
        }
        if (this.client.player != null) {
            ItemStack lv2 = this.client.player.inventory.getMainHandStack();
            if (lv2.isEmpty()) {
                this.heldItemTooltipFade = 0;
            } else if (this.currentStack.isEmpty() || lv2.getItem() != this.currentStack.getItem() || !lv2.getName().equals(this.currentStack.getName())) {
                this.heldItemTooltipFade = 40;
            } else if (this.heldItemTooltipFade > 0) {
                --this.heldItemTooltipFade;
            }
            this.currentStack = lv2;
        }
    }

    public void setRecordPlayingOverlay(Text arg) {
        this.setOverlayMessage(new TranslatableText("record.nowPlaying", arg), true);
    }

    public void setOverlayMessage(Text arg, boolean bl) {
        this.overlayMessage = arg;
        this.overlayRemaining = 60;
        this.overlayTinted = bl;
    }

    public void setTitles(@Nullable Text arg, @Nullable Text arg2, int i, int j, int k) {
        if (arg == null && arg2 == null && i < 0 && j < 0 && k < 0) {
            this.title = null;
            this.subtitle = null;
            this.titleTotalTicks = 0;
            return;
        }
        if (arg != null) {
            this.title = arg;
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
            return;
        }
        if (arg2 != null) {
            this.subtitle = arg2;
            return;
        }
        if (i >= 0) {
            this.titleFadeInTicks = i;
        }
        if (j >= 0) {
            this.titleRemainTicks = j;
        }
        if (k >= 0) {
            this.titleFadeOutTicks = k;
        }
        if (this.titleTotalTicks > 0) {
            this.titleTotalTicks = this.titleFadeInTicks + this.titleRemainTicks + this.titleFadeOutTicks;
        }
    }

    public void addChatMessage(MessageType arg, Text arg2) {
        for (ClientChatListener lv : this.listeners.get((Object)arg)) {
            lv.onChatMessage(arg, arg2);
        }
    }

    public ChatHud getChatHud() {
        return this.chatHud;
    }

    public int getTicks() {
        return this.ticks;
    }

    public TextRenderer getFontRenderer() {
        return this.client.textRenderer;
    }

    public SpectatorHud getSpectatorHud() {
        return this.spectatorHud;
    }

    public PlayerListHud getPlayerListWidget() {
        return this.playerListHud;
    }

    public void clear() {
        this.playerListHud.clear();
        this.bossBarHud.clear();
        this.client.getToastManager().clear();
    }

    public BossBarHud getBossBarHud() {
        return this.bossBarHud;
    }

    public void resetDebugHudChunk() {
        this.debugHud.resetChunk();
    }
}

