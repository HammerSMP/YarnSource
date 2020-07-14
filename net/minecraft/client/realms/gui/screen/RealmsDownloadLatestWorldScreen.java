/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.FileDownload;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.SizeUnit;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.gui.screen.RealmsLongConfirmationScreen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsDownloadLatestWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ReentrantLock downloadLock = new ReentrantLock();
    private final Screen parent;
    private final WorldDownload worldDownload;
    private final Text downloadTitle;
    private final RateLimiter narrationRateLimiter;
    private ButtonWidget field_22694;
    private final String worldName;
    private final DownloadStatus downloadStatus;
    private volatile Text field_20494;
    private volatile Text status = new TranslatableText("mco.download.preparing");
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots = true;
    private volatile boolean finished;
    private volatile boolean extracting;
    private Long previousWrittenBytes;
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private int animTick;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private boolean checked;
    private final BooleanConsumer field_22693;

    public RealmsDownloadLatestWorldScreen(Screen parent, WorldDownload worldDownload, String worldName, BooleanConsumer booleanConsumer) {
        this.field_22693 = booleanConsumer;
        this.parent = parent;
        this.worldName = worldName;
        this.worldDownload = worldDownload;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = new TranslatableText("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
    }

    @Override
    public void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.field_22694 = this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - 42, 200, 20, ScreenTexts.CANCEL, arg -> {
            this.cancelled = true;
            this.backButtonClicked();
        }));
        this.checkDownloadSize();
    }

    private void checkDownloadSize() {
        if (this.finished) {
            return;
        }
        if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 0x140000000L) {
            TranslatableText lv = new TranslatableText("mco.download.confirmation.line1", SizeUnit.getUserFriendlyString(0x140000000L));
            TranslatableText lv2 = new TranslatableText("mco.download.confirmation.line2");
            this.client.openScreen(new RealmsLongConfirmationScreen(bl -> {
                this.checked = true;
                this.client.openScreen(this);
                this.downloadSave();
            }, RealmsLongConfirmationScreen.Type.Warning, lv, lv2, false));
        } else {
            this.downloadSave();
        }
    }

    private long getContentLength(String downloadLink) {
        FileDownload lv = new FileDownload();
        return lv.contentLength(downloadLink);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            ArrayList list = Lists.newArrayList();
            list.add(this.downloadTitle);
            list.add(this.status);
            if (this.progress != null) {
                list.add(new LiteralText(this.progress + "%"));
                list.add(new LiteralText(SizeUnit.getUserFriendlyString(this.bytesPersSecond) + "/s"));
            }
            if (this.field_20494 != null) {
                list.add(this.field_20494);
            }
            String string = list.stream().map(Text::getString).collect(Collectors.joining("\n"));
            Realms.narrateNow(string);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.cancelled = true;
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void backButtonClicked() {
        if (this.finished && this.field_22693 != null && this.field_20494 == null) {
            this.field_22693.accept(true);
        }
        this.client.openScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        if (this.extracting && !this.finished) {
            this.status = new TranslatableText("mco.download.extracting");
        }
        this.drawCenteredText(matrices, this.textRenderer, this.downloadTitle, this.width / 2, 20, 0xFFFFFF);
        this.drawCenteredText(matrices, this.textRenderer, this.status, this.width / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots(matrices);
        }
        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar(matrices);
            this.drawDownloadSpeed(matrices);
        }
        if (this.field_20494 != null) {
            this.drawCenteredText(matrices, this.textRenderer, this.field_20494, this.width / 2, 110, 0xFF0000);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    private void drawDots(MatrixStack arg) {
        int i = this.textRenderer.getWidth(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.textRenderer.draw(arg, DOTS[this.dotIndex % DOTS.length], (float)(this.width / 2 + i / 2 + 5), 50.0f, 0xFFFFFF);
    }

    private void drawProgressBar(MatrixStack arg) {
        double d = this.downloadStatus.bytesWritten.doubleValue() / this.downloadStatus.totalBytes.doubleValue() * 100.0;
        this.progress = String.format(Locale.ROOT, "%.1f", d);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        Tessellator lv = Tessellator.getInstance();
        BufferBuilder lv2 = lv.getBuffer();
        lv2.begin(7, VertexFormats.POSITION_COLOR);
        double e = this.width / 2 - 100;
        double f = 0.5;
        lv2.vertex(e - 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
        lv2.vertex(e + 200.0 * d / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).next();
        lv2.vertex(e + 200.0 * d / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
        lv2.vertex(e - 0.5, 79.5, 0.0).color(217, 210, 210, 255).next();
        lv2.vertex(e, 95.0, 0.0).color(128, 128, 128, 255).next();
        lv2.vertex(e + 200.0 * d / 100.0, 95.0, 0.0).color(128, 128, 128, 255).next();
        lv2.vertex(e + 200.0 * d / 100.0, 80.0, 0.0).color(128, 128, 128, 255).next();
        lv2.vertex(e, 80.0, 0.0).color(128, 128, 128, 255).next();
        lv.draw();
        RenderSystem.enableTexture();
        this.drawCenteredString(arg, this.textRenderer, this.progress + " %", this.width / 2, 84, 0xFFFFFF);
    }

    private void drawDownloadSpeed(MatrixStack arg) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long l = Util.getMeasuringTimeMs() - this.previousTimeSnapshot;
                if (l == 0L) {
                    l = 1L;
                }
                this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / l;
                this.drawDownloadSpeed0(arg, this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMeasuringTimeMs();
        } else {
            this.drawDownloadSpeed0(arg, this.bytesPersSecond);
        }
    }

    private void drawDownloadSpeed0(MatrixStack arg, long l) {
        if (l > 0L) {
            int i = this.textRenderer.getWidth(this.progress);
            String string = "(" + SizeUnit.getUserFriendlyString(l) + "/s)";
            this.textRenderer.draw(arg, string, (float)(this.width / 2 + i / 2 + 15), 84.0f, 0xFFFFFF);
        }
    }

    private void downloadSave() {
        new Thread(() -> {
            try {
                if (!downloadLock.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = new TranslatableText("mco.download.failed");
                    return;
                }
                if (this.cancelled) {
                    this.downloadCancelled();
                    return;
                }
                this.status = new TranslatableText("mco.download.downloading", this.worldName);
                FileDownload lv = new FileDownload();
                lv.contentLength(this.worldDownload.downloadLink);
                lv.downloadWorld(this.worldDownload, this.worldName, this.downloadStatus, this.client.getLevelStorage());
                while (!lv.isFinished()) {
                    if (lv.isError()) {
                        lv.cancel();
                        this.field_20494 = new TranslatableText("mco.download.failed");
                        this.field_22694.setMessage(ScreenTexts.DONE);
                        return;
                    }
                    if (lv.isExtracting()) {
                        this.extracting = true;
                    }
                    if (this.cancelled) {
                        lv.cancel();
                        this.downloadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep(500L);
                    }
                    catch (InterruptedException interruptedException) {
                        LOGGER.error("Failed to check Realms backup download status");
                    }
                }
                this.finished = true;
                this.status = new TranslatableText("mco.download.done");
                this.field_22694.setMessage(ScreenTexts.DONE);
            }
            catch (InterruptedException interruptedException2) {
                LOGGER.error("Could not acquire upload lock");
            }
            catch (Exception exception) {
                this.field_20494 = new TranslatableText("mco.download.failed");
                exception.printStackTrace();
            }
            finally {
                if (!downloadLock.isHeldByCurrentThread()) {
                    return;
                }
                downloadLock.unlock();
                this.showDots = false;
                this.finished = true;
            }
        }).start();
    }

    private void downloadCancelled() {
        this.status = new TranslatableText("mco.download.cancelled");
    }

    @Environment(value=EnvType.CLIENT)
    public class DownloadStatus {
        public volatile Long bytesWritten = 0L;
        public volatile Long totalBytes = 0L;
    }
}

