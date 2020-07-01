/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.pack;

import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.PackListWidget;
import net.minecraft.client.gui.screen.pack.ResourcePackOrganizer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class AbstractPackScreen
extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Text DROP_INFO = new TranslatableText("pack.dropInfo").formatted(Formatting.DARK_GRAY);
    private static final Text FOLDER_INFO = new TranslatableText("pack.folderInfo");
    private static final Identifier field_25786 = new Identifier("textures/misc/unknown_pack.png");
    private final ResourcePackOrganizer organizer;
    private final Screen parent;
    @Nullable
    private class_5426 field_25787;
    private long field_25788;
    private PackListWidget availablePackList;
    private PackListWidget selectedPackList;
    private final File field_25474;
    private ButtonWidget doneButton;
    private final Map<String, Identifier> field_25789 = Maps.newHashMap();

    public AbstractPackScreen(Screen arg, ResourcePackManager arg2, Consumer<ResourcePackManager> consumer, File file, TranslatableText arg3) {
        super(arg3);
        this.parent = arg;
        this.organizer = new ResourcePackOrganizer(this::updatePackLists, this::method_30287, arg2, consumer);
        this.field_25474 = file;
        this.field_25787 = class_5426.method_30293(file);
    }

    @Override
    public void onClose() {
        this.organizer.apply();
        this.client.openScreen(this.parent);
        this.method_30291();
    }

    private void method_30291() {
        if (this.field_25787 != null) {
            try {
                this.field_25787.close();
                this.field_25787 = null;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
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
        this.method_29680();
    }

    @Override
    public void tick() {
        if (this.field_25787 != null) {
            try {
                if (this.field_25787.method_30292()) {
                    this.field_25788 = 20L;
                }
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to poll for directory {} changes, stopping", (Object)this.field_25474);
                this.method_30291();
            }
        }
        if (this.field_25788 > 0L && --this.field_25788 == 0L) {
            this.method_29680();
        }
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
        this.field_25788 = 0L;
        this.field_25789.clear();
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

    /*
     * Exception decompiling
     */
    private Identifier method_30289(TextureManager arg, ResourcePackProfile arg2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 5[TRYBLOCK]
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:428)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:477)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    private Identifier method_30287(ResourcePackProfile arg) {
        return this.field_25789.computeIfAbsent(arg.getName(), string -> this.method_30289(this.client.getTextureManager(), arg));
    }

    @Environment(value=EnvType.CLIENT)
    static class class_5426
    implements AutoCloseable {
        private final WatchService field_25790;
        private final Path field_25791;

        public class_5426(File file) throws IOException {
            this.field_25791 = file.toPath();
            this.field_25790 = this.field_25791.getFileSystem().newWatchService();
            try {
                this.method_30294(this.field_25791);
                try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(this.field_25791);){
                    for (Path path : directoryStream) {
                        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) continue;
                        this.method_30294(path);
                    }
                }
            }
            catch (Exception exception) {
                this.field_25790.close();
                throw exception;
            }
        }

        @Nullable
        public static class_5426 method_30293(File file) {
            try {
                return new class_5426(file);
            }
            catch (IOException iOException) {
                LOGGER.warn("Failed to initialize pack directory {} monitoring", (Object)file, (Object)iOException);
                return null;
            }
        }

        private void method_30294(Path path) throws IOException {
            path.register(this.field_25790, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        }

        public boolean method_30292() throws IOException {
            WatchKey watchKey;
            boolean bl = false;
            while ((watchKey = this.field_25790.poll()) != null) {
                List<WatchEvent<?>> list = watchKey.pollEvents();
                for (WatchEvent<?> watchEvent : list) {
                    bl = true;
                    if (watchKey.watchable() != this.field_25791 || watchEvent.kind() != StandardWatchEventKinds.ENTRY_CREATE) continue;
                    Path path = this.field_25791.resolve((Path)watchEvent.context());
                    if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) continue;
                    this.method_30294(path);
                }
                watchKey.reset();
            }
            return bl;
        }

        @Override
        public void close() throws IOException {
            this.field_25790.close();
        }
    }
}

