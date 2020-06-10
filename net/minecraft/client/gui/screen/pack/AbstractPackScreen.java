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
package net.minecraft.client.gui.screen.pack;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
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
public abstract class AbstractPackScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Text DROP_INFO = new TranslatableText("pack.dropInfo").formatted(Formatting.DARK_GRAY);
    private static final Text FOLDER_INFO = new TranslatableText("pack.folderInfo");
    private final ResourcePackOrganizer<?> organizer;
    private final Screen parent;
    private boolean shouldSave;
    private PackListWidget availablePackList;
    private PackListWidget selectedPackList;
    private final File field_25474;
    private ButtonWidget doneButton;

    public AbstractPackScreen(Screen arg, TranslatableText arg2, Function<Runnable, ResourcePackOrganizer<?>> function, File file) {
        super(arg2);
        this.parent = arg;
        this.organizer = function.apply(this::updatePackLists);
        this.field_25474 = file;
    }

    @Override
    public void removed() {
        if (this.shouldSave) {
            this.shouldSave = false;
            this.organizer.apply();
        }
    }

    @Override
    public void onClose() {
        this.shouldSave = true;
        this.client.openScreen(this.parent);
    }

    @Override
    protected void init() {
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 + 4, this.height - 48, 150, 20, ScreenTexts.DONE, arg -> this.onClose()));
        this.addButton(new ButtonWidget(this.width / 2 - 154, this.height - 48, 150, 20, new TranslatableText("pack.openFolder"), arg -> Util.getOperatingSystem().open(this.field_25474), (arg, arg2, i, j) -> this.renderTooltip(arg2, FOLDER_INFO, i, j)));
        this.availablePackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.available.title"));
        this.availablePackList.setLeftPos(this.width / 2 - 4 - 200);
        this.children.add(this.availablePackList);
        this.selectedPackList = new PackListWidget(this.client, 200, this.height, new TranslatableText("pack.selected.title"));
        this.selectedPackList.setLeftPos(this.width / 2 + 4);
        this.children.add(this.selectedPackList);
        this.updatePackLists();
    }

    private void updatePackLists() {
        this.updatePackList(this.selectedPackList, this.organizer.getEnabledPacks());
        this.updatePackList(this.availablePackList, this.organizer.getDisabledPacks());
        this.doneButton.active = !this.selectedPackList.children().isEmpty();
    }

    private void updatePackList(PackListWidget arg, Stream<ResourcePackOrganizer.Pack> stream) {
        arg.children().clear();
        stream.forEach(arg2 -> arg.children().add(new PackListWidget.ResourcePackEntry(this.client, arg, this, (ResourcePackOrganizer.Pack)arg2)));
    }

    private void method_29680() {
        this.organizer.method_29981();
        this.updatePackLists();
        this.shouldSave = true;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackgroundTexture(0);
        this.availablePackList.render(arg, i, j, f);
        this.selectedPackList.render(arg, i, j, f);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        this.drawCenteredText(arg, this.textRenderer, DROP_INFO, this.width / 2, 20, 0xFFFFFF);
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
                        LOGGER.warn("Failed to copy datapack file  from {} to {}", path3, (Object)path, (Object)iOException);
                        mutableBoolean.setTrue();
                    }
                });
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to copy datapack file from {} to {}", path2, (Object)path);
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
                AbstractPackScreen.method_29669(this.client, list, this.field_25474.toPath());
                this.method_29680();
            }
            this.client.openScreen(this);
        }, new TranslatableText("pack.dropConfirm"), new LiteralText(string)));
    }
}

