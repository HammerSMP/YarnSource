/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5369;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class class_5375
extends Screen {
    private static final Logger field_25464 = LogManager.getLogger();
    private static final Text field_25465 = new TranslatableText("pack.dropInfo").formatted(Formatting.DARK_GRAY);
    private static final Text field_25466 = new TranslatableText("pack.folderInfo");
    private final Function<Runnable, class_5369<?>> field_25467;
    private class_5369<?> field_25468;
    private final Screen field_25469;
    private boolean field_25470;
    private boolean field_25471;
    private ResourcePackListWidget field_25472;
    private ResourcePackListWidget field_25473;
    private final Function<MinecraftClient, File> field_25474;
    private ButtonWidget field_25475;

    public class_5375(Screen arg, TranslatableText arg2, Function<Runnable, class_5369<?>> function, Function<MinecraftClient, File> function2) {
        super(arg2);
        this.field_25469 = arg;
        this.field_25467 = function;
        this.field_25468 = function.apply(this::method_29679);
        this.field_25474 = function2;
    }

    @Override
    public void removed() {
        if (this.field_25471) {
            this.field_25471 = false;
            this.field_25468.method_29642(false);
        }
    }

    @Override
    public void onClose() {
        this.client.openScreen(this.field_25469);
    }

    @Override
    protected void init() {
        this.field_25475 = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, arg -> {
            this.field_25471 = this.field_25470;
            this.onClose();
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, new TranslatableText("pack.openFolder"), arg -> Util.getOperatingSystem().open(this.field_25474.apply(this.client)), (arg, arg2, i, j) -> this.renderTooltip(arg2, field_25466, i, j)));
        this.field_25472 = new ResourcePackListWidget(this.client, 200, this.height, new TranslatableText("pack.available.title"));
        this.field_25472.setLeftPos(this.width / 2 - 4 - 200);
        this.children.add(this.field_25472);
        this.field_25473 = new ResourcePackListWidget(this.client, 200, this.height, new TranslatableText("pack.selected.title"));
        this.field_25473.setLeftPos(this.width / 2 + 4);
        this.children.add(this.field_25473);
        this.method_29678();
    }

    private void method_29678() {
        this.method_29673(this.field_25473, this.field_25468.method_29643());
        this.method_29673(this.field_25472, this.field_25468.method_29639());
        this.field_25475.active = !this.field_25473.children().isEmpty();
    }

    private void method_29673(ResourcePackListWidget arg, Stream<class_5369.class_5371> stream) {
        arg.children().clear();
        stream.forEach(arg2 -> arg.children().add(new ResourcePackListWidget.ResourcePackEntry(this.client, arg, this, (class_5369.class_5371)arg2)));
    }

    protected void method_29679() {
        this.method_29678();
        this.field_25470 = true;
    }

    protected void method_29680() {
        this.field_25468.method_29642(true);
        this.field_25468 = this.field_25467.apply(this::method_29679);
        this.method_29678();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackgroundTexture(0);
        this.field_25472.render(arg, i, j, f);
        this.field_25473.render(arg, i, j, f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        this.drawCenteredText(arg, this.textRenderer, field_25465, this.width / 2, 20, 0xFFFFFF);
        super.render(arg, i, j, f);
    }

    protected static void method_29669(MinecraftClient arg, List<Path> list, Path path) {
        MutableBoolean mutableBoolean = new MutableBoolean();
        list.forEach(path2 -> {
            try (Stream<Path> stream = Files.walk(path2, new FileVisitOption[0]);){
                stream.forEach(path3 -> {
                    try {
                        Util.method_29775(path2.getParent(), path, path3);
                    }
                    catch (IOException iOException) {
                        field_25464.warn("Failed to copy datapack file  from {} to {}", path3, (Object)path, (Object)iOException);
                        mutableBoolean.setTrue();
                    }
                });
            }
            catch (IOException iOException) {
                field_25464.warn("Failed to copy datapack file from {} to {}", path2, (Object)path);
                mutableBoolean.setTrue();
            }
        });
        if (mutableBoolean.isTrue()) {
            SystemToast.method_29627(arg, path.toString());
        }
    }

    @Override
    public void method_29638(List<Path> list) {
        String string = list.stream().map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", "));
        this.client.openScreen(new ConfirmScreen(bl -> {
            if (bl) {
                class_5375.method_29669(this.client, list, this.field_25474.apply(this.client).toPath());
                this.method_29680();
            }
            this.client.openScreen(this);
        }, new TranslatableText("pack.dropConfirm"), new LiteralText(string)));
    }
}

