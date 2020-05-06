/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DefaultResourcePack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class SplashScreen
extends Overlay {
    private static final Identifier LOGO = new Identifier("textures/gui/title/mojang.png");
    private final MinecraftClient client;
    private final ResourceReloadMonitor reloadMonitor;
    private final Consumer<Optional<Throwable>> exceptionHandler;
    private final boolean reloading;
    private float progress;
    private long applyCompleteTime = -1L;
    private long prepareCompleteTime = -1L;

    public SplashScreen(MinecraftClient arg, ResourceReloadMonitor arg2, Consumer<Optional<Throwable>> consumer, boolean bl) {
        this.client = arg;
        this.reloadMonitor = arg2;
        this.exceptionHandler = consumer;
        this.reloading = bl;
    }

    public static void init(MinecraftClient arg) {
        arg.getTextureManager().registerTexture(LOGO, new LogoTexture());
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        float r;
        float h;
        int k = this.client.getWindow().getScaledWidth();
        int l = this.client.getWindow().getScaledHeight();
        long m = Util.getMeasuringTimeMs();
        if (this.reloading && (this.reloadMonitor.isPrepareStageComplete() || this.client.currentScreen != null) && this.prepareCompleteTime == -1L) {
            this.prepareCompleteTime = m;
        }
        float g = this.applyCompleteTime > -1L ? (float)(m - this.applyCompleteTime) / 1000.0f : -1.0f;
        float f2 = h = this.prepareCompleteTime > -1L ? (float)(m - this.prepareCompleteTime) / 500.0f : -1.0f;
        if (g >= 1.0f) {
            if (this.client.currentScreen != null) {
                this.client.currentScreen.render(arg, 0, 0, f);
            }
            int n = MathHelper.ceil((1.0f - MathHelper.clamp(g - 1.0f, 0.0f, 1.0f)) * 255.0f);
            SplashScreen.fill(arg, 0, 0, k, l, 0xFFFFFF | n << 24);
            float o = 1.0f - MathHelper.clamp(g - 1.0f, 0.0f, 1.0f);
        } else if (this.reloading) {
            if (this.client.currentScreen != null && h < 1.0f) {
                this.client.currentScreen.render(arg, i, j, f);
            }
            int p = MathHelper.ceil(MathHelper.clamp((double)h, 0.15, 1.0) * 255.0);
            SplashScreen.fill(arg, 0, 0, k, l, 0xFFFFFF | p << 24);
            float q = MathHelper.clamp(h, 0.0f, 1.0f);
        } else {
            SplashScreen.fill(arg, 0, 0, k, l, -1);
            r = 1.0f;
        }
        int s = (this.client.getWindow().getScaledWidth() - 256) / 2;
        int t = (this.client.getWindow().getScaledHeight() - 256) / 2;
        this.client.getTextureManager().bindTexture(LOGO);
        RenderSystem.enableBlend();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, r);
        this.drawTexture(arg, s, t, 0, 0, 256, 256);
        float u = this.reloadMonitor.getProgress();
        this.progress = MathHelper.clamp(this.progress * 0.95f + u * 0.050000012f, 0.0f, 1.0f);
        if (g < 1.0f) {
            this.renderProgressBar(arg, k / 2 - 150, l / 4 * 3, k / 2 + 150, l / 4 * 3 + 10, 1.0f - MathHelper.clamp(g, 0.0f, 1.0f));
        }
        if (g >= 2.0f) {
            this.client.setOverlay(null);
        }
        if (this.applyCompleteTime == -1L && this.reloadMonitor.isApplyStageComplete() && (!this.reloading || h >= 2.0f)) {
            try {
                this.reloadMonitor.throwExceptions();
                this.exceptionHandler.accept(Optional.empty());
            }
            catch (Throwable throwable) {
                this.exceptionHandler.accept(Optional.of(throwable));
            }
            this.applyCompleteTime = Util.getMeasuringTimeMs();
            if (this.client.currentScreen != null) {
                this.client.currentScreen.init(this.client, this.client.getWindow().getScaledWidth(), this.client.getWindow().getScaledHeight());
            }
        }
    }

    private void renderProgressBar(MatrixStack arg, int i, int j, int k, int l, float f) {
        int m = MathHelper.ceil((float)(k - i - 1) * this.progress);
        SplashScreen.fill(arg, i - 1, j - 1, k + 1, l + 1, 0xFF000000 | Math.round((1.0f - f) * 255.0f) << 16 | Math.round((1.0f - f) * 255.0f) << 8 | Math.round((1.0f - f) * 255.0f));
        SplashScreen.fill(arg, i, j, k, l, -1);
        SplashScreen.fill(arg, i + 1, j + 1, i + m, l - 1, 0xFF000000 | (int)MathHelper.lerp(1.0f - f, 226.0f, 255.0f) << 16 | (int)MathHelper.lerp(1.0f - f, 40.0f, 255.0f) << 8 | (int)MathHelper.lerp(1.0f - f, 55.0f, 255.0f));
    }

    @Override
    public boolean pausesGame() {
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    static class LogoTexture
    extends ResourceTexture {
        public LogoTexture() {
            super(LOGO);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        protected ResourceTexture.TextureData loadTextureData(ResourceManager arg) {
            MinecraftClient lv = MinecraftClient.getInstance();
            DefaultResourcePack lv2 = lv.getResourcePackDownloader().getPack();
            try (InputStream inputStream = lv2.open(ResourceType.CLIENT_RESOURCES, LOGO);){
                ResourceTexture.TextureData textureData = new ResourceTexture.TextureData(null, NativeImage.read(inputStream));
                return textureData;
            }
            catch (IOException iOException) {
                return new ResourceTexture.TextureData(iOException);
            }
        }
    }
}

