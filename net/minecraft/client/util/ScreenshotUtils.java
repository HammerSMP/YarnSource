/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ScreenshotUtils {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    public static void saveScreenshot(File file, int i, int j, Framebuffer arg, Consumer<Text> consumer) {
        ScreenshotUtils.saveScreenshot(file, null, i, j, arg, consumer);
    }

    public static void saveScreenshot(File file, @Nullable String string, int i, int j, Framebuffer arg, Consumer<Text> consumer) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> ScreenshotUtils.saveScreenshotInner(file, string, i, j, arg, consumer));
        } else {
            ScreenshotUtils.saveScreenshotInner(file, string, i, j, arg, consumer);
        }
    }

    private static void saveScreenshotInner(File file, @Nullable String string, int i, int j, Framebuffer arg, Consumer<Text> consumer) {
        File file4;
        NativeImage lv = ScreenshotUtils.takeScreenshot(i, j, arg);
        File file2 = new File(file, "screenshots");
        file2.mkdir();
        if (string == null) {
            File file3 = ScreenshotUtils.getScreenshotFilename(file2);
        } else {
            file4 = new File(file2, string);
        }
        Util.method_27958().execute(() -> {
            try {
                lv.writeFile(file4);
                MutableText lv = new LiteralText(file4.getName()).formatted(Formatting.UNDERLINE).styled(arg -> arg.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file4.getAbsolutePath())));
                consumer.accept(new TranslatableText("screenshot.success", lv));
            }
            catch (Exception exception) {
                LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
                consumer.accept(new TranslatableText("screenshot.failure", exception.getMessage()));
            }
            finally {
                lv.close();
            }
        });
    }

    public static NativeImage takeScreenshot(int i, int j, Framebuffer arg) {
        i = arg.textureWidth;
        j = arg.textureHeight;
        NativeImage lv = new NativeImage(i, j, false);
        RenderSystem.bindTexture(arg.colorAttachment);
        lv.loadFromTextureImage(0, true);
        lv.mirrorVertically();
        return lv;
    }

    private static File getScreenshotFilename(File file) {
        String string = DATE_FORMAT.format(new Date());
        int i = 1;
        File file2;
        while ((file2 = new File(file, string + (i == 1 ? "" : "_" + i) + ".png")).exists()) {
            ++i;
        }
        return file2;
    }
}

