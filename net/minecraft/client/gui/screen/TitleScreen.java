/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.Runnables
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen;

import com.google.common.util.concurrent.Runnables;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.options.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.realms.RealmsBridge;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class TitleScreen
extends Screen {
    private static final Logger field_23775 = LogManager.getLogger();
    public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
    private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
    private static final Identifier ACCESSIBILITY_ICON_TEXTURE = new Identifier("textures/gui/accessibility.png");
    private final boolean isMinceraft;
    @Nullable
    private String splashText;
    private ButtonWidget buttonResetDemo;
    private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
    private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
    private boolean realmsNotificationsInitialized;
    private Screen realmsNotificationGui;
    private int copyrightTextWidth;
    private int copyrightTextX;
    private final RotatingCubeMapRenderer backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
    private final boolean doBackgroundFade;
    private long backgroundFadeStart;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean bl) {
        super(new TranslatableText("narrator.screen.title"));
        this.doBackgroundFade = bl;
        this.isMinceraft = (double)new Random().nextFloat() < 1.0E-4;
    }

    private boolean areRealmsNotificationsEnabled() {
        return this.client.options.realmsNotifications && this.realmsNotificationGui != null;
    }

    @Override
    public void tick() {
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.tick();
        }
    }

    public static CompletableFuture<Void> loadTexturesAsync(TextureManager arg, Executor executor) {
        return CompletableFuture.allOf(arg.loadTextureAsync(MINECRAFT_TITLE_TEXTURE, executor), arg.loadTextureAsync(EDITION_TITLE_TEXTURE, executor), arg.loadTextureAsync(PANORAMA_OVERLAY, executor), PANORAMA_CUBE_MAP.loadTexturesAsync(arg, executor));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (this.splashText == null) {
            this.splashText = this.client.getSplashTextLoader().get();
        }
        this.copyrightTextWidth = this.textRenderer.getWidth("Copyright Mojang AB. Do not distribute!");
        this.copyrightTextX = this.width - this.copyrightTextWidth - 2;
        int i = 24;
        int j = this.height / 4 + 48;
        if (this.client.isDemo()) {
            this.initWidgetsDemo(j, 24);
        } else {
            this.initWidgetsNormal(j, 24);
        }
        this.addButton(new TexturedButtonWidget(this.width / 2 - 124, j + 72 + 12, 20, 20, 0, 106, 20, ButtonWidget.WIDGETS_LOCATION, 256, 256, arg -> this.client.openScreen(new LanguageOptionsScreen((Screen)this, this.client.options, this.client.getLanguageManager())), new TranslatableText("narrator.button.language")));
        this.addButton(new ButtonWidget(this.width / 2 - 100, j + 72 + 12, 98, 20, new TranslatableText("menu.options"), arg -> this.client.openScreen(new OptionsScreen(this, this.client.options))));
        this.addButton(new ButtonWidget(this.width / 2 + 2, j + 72 + 12, 98, 20, new TranslatableText("menu.quit"), arg -> this.client.scheduleStop()));
        this.addButton(new TexturedButtonWidget(this.width / 2 + 104, j + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_ICON_TEXTURE, 32, 64, arg -> this.client.openScreen(new AccessibilityOptionsScreen(this, this.client.options)), new TranslatableText("narrator.button.accessibility")));
        this.client.setConnectedToRealms(false);
        if (this.client.options.realmsNotifications && !this.realmsNotificationsInitialized) {
            RealmsBridge lv = new RealmsBridge();
            this.realmsNotificationGui = lv.getNotificationScreen(this);
            this.realmsNotificationsInitialized = true;
        }
        if (this.areRealmsNotificationsEnabled()) {
            this.realmsNotificationGui.init(this.client, this.width, this.height);
        }
    }

    private void initWidgetsNormal(int i2, int j2) {
        this.addButton(new ButtonWidget(this.width / 2 - 100, i2, 200, 20, new TranslatableText("menu.singleplayer"), arg -> this.client.openScreen(new SelectWorldScreen(this))));
        boolean bl = this.client.isMultiplayerEnabled();
        ButtonWidget.TooltipSupplier lv = bl ? ButtonWidget.EMPTY : (arg, arg2, i, j) -> {
            if (!arg.active) {
                this.renderTooltip(arg2, this.client.textRenderer.wrapLines(new TranslatableText("title.multiplayer.disabled"), Math.max(this.width / 2 - 43, 170)), i, j);
            }
        };
        this.addButton(new ButtonWidget((int)(this.width / 2 - 100), (int)(i2 + j2 * 1), (int)200, (int)20, (Text)new TranslatableText((String)"menu.multiplayer"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, method_19860(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this), (ButtonWidget.TooltipSupplier)lv)).active = bl;
        this.addButton(new ButtonWidget((int)(this.width / 2 - 100), (int)(i2 + j2 * 2), (int)200, (int)20, (Text)new TranslatableText((String)"menu.online"), (ButtonWidget.PressAction)(ButtonWidget.PressAction)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/widget/ButtonWidget;)V, method_19859(net.minecraft.client.gui.widget.ButtonWidget ), (Lnet/minecraft/client/gui/widget/ButtonWidget;)V)((TitleScreen)this), (ButtonWidget.TooltipSupplier)lv)).active = bl;
    }

    private void initWidgetsDemo(int i, int j) {
        this.addButton(new ButtonWidget(this.width / 2 - 100, i, 200, 20, new TranslatableText("menu.playdemo"), arg -> this.client.method_29607("Demo_World", MinecraftServer.DEMO_LEVEL_INFO, RegistryTracker.create(), GeneratorOptions.DEMO_CONFIG)));
        this.buttonResetDemo = this.addButton(new ButtonWidget(this.width / 2 - 100, i + j * 1, 200, 20, new TranslatableText("menu.resetdemo"), arg -> {
            LevelStorage lv = this.client.getLevelStorage();
            try (LevelStorage.Session lv2 = lv.createSession("Demo_World");){
                LevelSummary lv3 = lv2.method_29584();
                if (lv3 != null) {
                    this.client.openScreen(new ConfirmScreen(this::onDemoDeletionConfirmed, new TranslatableText("selectWorld.deleteQuestion"), new TranslatableText("selectWorld.deleteWarning", lv3.getDisplayName()), new TranslatableText("selectWorld.deleteButton"), ScreenTexts.CANCEL));
                }
            }
            catch (IOException iOException) {
                SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
                field_23775.warn("Failed to access demo world", (Throwable)iOException);
            }
        }));
        try (LevelStorage.Session lv = this.client.getLevelStorage().createSession("Demo_World");){
            LevelSummary lv2 = lv.method_29584();
            if (lv2 == null) {
                this.buttonResetDemo.active = false;
            }
        }
        catch (IOException iOException) {
            SystemToast.addWorldAccessFailureToast(this.client, "Demo_World");
            field_23775.warn("Failed to read demo world data", (Throwable)iOException);
        }
    }

    private void switchToRealms() {
        RealmsBridge lv = new RealmsBridge();
        lv.switchToRealms(this);
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (this.backgroundFadeStart == 0L && this.doBackgroundFade) {
            this.backgroundFadeStart = Util.getMeasuringTimeMs();
        }
        float g = this.doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0f : 1.0f;
        TitleScreen.fill(arg, 0, 0, this.width, this.height, -1);
        this.backgroundRenderer.render(f, MathHelper.clamp(g, 0.0f, 1.0f));
        int k = 274;
        int l = this.width / 2 - 137;
        int m = 30;
        this.client.getTextureManager().bindTexture(PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, this.doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(g, 0.0f, 1.0f)) : 1.0f);
        TitleScreen.drawTexture(arg, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        float h = this.doBackgroundFade ? MathHelper.clamp(g - 1.0f, 0.0f, 1.0f) : 1.0f;
        int n = MathHelper.ceil(h * 255.0f) << 24;
        if ((n & 0xFC000000) == 0) {
            return;
        }
        this.client.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURE);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, h);
        if (this.isMinceraft) {
            this.method_29343(l, 30, (integer, integer2) -> {
                this.drawTexture(arg, integer + 0, (int)integer2, 0, 0, 99, 44);
                this.drawTexture(arg, integer + 99, (int)integer2, 129, 0, 27, 44);
                this.drawTexture(arg, integer + 99 + 26, (int)integer2, 126, 0, 3, 44);
                this.drawTexture(arg, integer + 99 + 26 + 3, (int)integer2, 99, 0, 26, 44);
                this.drawTexture(arg, integer + 155, (int)integer2, 0, 45, 155, 44);
            });
        } else {
            this.method_29343(l, 30, (integer, integer2) -> {
                this.drawTexture(arg, integer + 0, (int)integer2, 0, 0, 155, 44);
                this.drawTexture(arg, integer + 155, (int)integer2, 0, 45, 155, 44);
            });
        }
        this.client.getTextureManager().bindTexture(EDITION_TITLE_TEXTURE);
        TitleScreen.drawTexture(arg, l + 88, 67, 0.0f, 0.0f, 98, 14, 128, 16);
        if (this.splashText != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.width / 2 + 90, 70.0f, 0.0f);
            RenderSystem.rotatef(-20.0f, 0.0f, 0.0f, 1.0f);
            float o = 1.8f - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
            o = o * 100.0f / (float)(this.textRenderer.getWidth(this.splashText) + 32);
            RenderSystem.scalef(o, o, o);
            this.drawCenteredString(arg, this.textRenderer, this.splashText, 0, -8, 0xFFFF00 | n);
            RenderSystem.popMatrix();
        }
        String string = "Minecraft " + SharedConstants.getGameVersion().getName();
        string = this.client.isDemo() ? string + " Demo" : string + ("release".equalsIgnoreCase(this.client.getVersionType()) ? "" : "/" + this.client.getVersionType());
        if (this.client.isModded()) {
            string = string + I18n.translate("menu.modded", new Object[0]);
        }
        this.drawStringWithShadow(arg, this.textRenderer, string, 2, this.height - 10, 0xFFFFFF | n);
        this.drawStringWithShadow(arg, this.textRenderer, "Copyright Mojang AB. Do not distribute!", this.copyrightTextX, this.height - 10, 0xFFFFFF | n);
        if (i > this.copyrightTextX && i < this.copyrightTextX + this.copyrightTextWidth && j > this.height - 10 && j < this.height) {
            TitleScreen.fill(arg, this.copyrightTextX, this.height - 1, this.copyrightTextX + this.copyrightTextWidth, this.height, 0xFFFFFF | n);
        }
        for (AbstractButtonWidget lv : this.buttons) {
            lv.setAlpha(h);
        }
        super.render(arg, i, j, f);
        if (this.areRealmsNotificationsEnabled() && h >= 1.0f) {
            this.realmsNotificationGui.render(arg, i, j, f);
        }
    }

    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (super.mouseClicked(d, e, i)) {
            return true;
        }
        if (this.areRealmsNotificationsEnabled() && this.realmsNotificationGui.mouseClicked(d, e, i)) {
            return true;
        }
        if (d > (double)this.copyrightTextX && d < (double)(this.copyrightTextX + this.copyrightTextWidth) && e > (double)(this.height - 10) && e < (double)this.height) {
            this.client.openScreen(new CreditsScreen(false, Runnables.doNothing()));
        }
        return false;
    }

    @Override
    public void removed() {
        if (this.realmsNotificationGui != null) {
            this.realmsNotificationGui.removed();
        }
    }

    private void onDemoDeletionConfirmed(boolean bl) {
        if (bl) {
            try (LevelStorage.Session lv = this.client.getLevelStorage().createSession("Demo_World");){
                lv.deleteSessionLock();
            }
            catch (IOException iOException) {
                SystemToast.addWorldDeleteFailureToast(this.client, "Demo_World");
                field_23775.warn("Failed to delete demo world", (Throwable)iOException);
            }
        }
        this.client.openScreen(this);
    }

    private /* synthetic */ void method_19859(ButtonWidget arg) {
        this.switchToRealms();
    }

    private /* synthetic */ void method_19860(ButtonWidget arg) {
        Screen lv = this.client.options.skipMultiplayerWarning ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
        this.client.openScreen(lv);
    }
}

