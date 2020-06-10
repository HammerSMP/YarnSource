/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.SaveLevelScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.pack.DataPackScreen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.screen.world.MoreOptionsDialog;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.FileResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.util.Util;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.registry.RegistryTracker;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
    private static final Logger field_25480 = LogManager.getLogger();
    private final Screen parent;
    private TextFieldWidget levelNameField;
    private String saveDirectoryName;
    private Mode currentMode = Mode.SURVIVAL;
    @Nullable
    private Mode lastMode;
    private Difficulty field_24289 = Difficulty.NORMAL;
    private Difficulty field_24290 = Difficulty.NORMAL;
    private boolean cheatsEnabled;
    private boolean tweakedCheats;
    public boolean hardcore;
    protected DataPackSettings field_25479 = DataPackSettings.SAFE_MODE;
    @Nullable
    private Path field_25477;
    private boolean moreOptionsOpen;
    private ButtonWidget createLevelButton;
    private ButtonWidget gameModeSwitchButton;
    private ButtonWidget field_24286;
    private ButtonWidget moreOptionsButton;
    private ButtonWidget gameRulesButton;
    private ButtonWidget field_25478;
    private ButtonWidget enableCheatsButton;
    private Text firstGameModeDescriptionLine;
    private Text secondGameModeDescriptionLine;
    private String levelName;
    private GameRules gameRules = new GameRules();
    public final MoreOptionsDialog moreOptionsDialog;

    public CreateWorldScreen(@Nullable Screen arg, LevelInfo arg2, GeneratorOptions arg3, @Nullable Path path, RegistryTracker.Modifiable arg4) {
        this(arg, new MoreOptionsDialog(arg4, arg3));
        this.levelName = arg2.getLevelName();
        this.cheatsEnabled = arg2.isHardcore();
        this.tweakedCheats = true;
        this.field_24290 = this.field_24289 = arg2.getDifficulty();
        this.gameRules.setAllValues(arg2.getGameRules(), null);
        this.field_25479 = arg2.method_29558();
        if (arg2.hasStructures()) {
            this.currentMode = Mode.HARDCORE;
        } else if (arg2.getGameMode().isSurvivalLike()) {
            this.currentMode = Mode.SURVIVAL;
        } else if (arg2.getGameMode().isCreative()) {
            this.currentMode = Mode.CREATIVE;
        }
        this.field_25477 = path;
    }

    public CreateWorldScreen(@Nullable Screen arg) {
        this(arg, new MoreOptionsDialog());
    }

    private CreateWorldScreen(@Nullable Screen arg, MoreOptionsDialog arg2) {
        super(new TranslatableText("selectWorld.create"));
        this.parent = arg;
        this.levelName = I18n.translate("selectWorld.newWorld", new Object[0]);
        this.moreOptionsDialog = arg2;
    }

    @Override
    public void tick() {
        this.levelNameField.tick();
        this.moreOptionsDialog.tick();
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.levelNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 60, 200, 20, (Text)new TranslatableText("selectWorld.enterName")){

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.resultFolder")).append(" ").append(CreateWorldScreen.this.saveDirectoryName);
            }
        };
        this.levelNameField.setText(this.levelName);
        this.levelNameField.setChangedListener(string -> {
            this.levelName = string;
            this.createLevelButton.active = !this.levelNameField.getText().isEmpty();
            this.updateSaveFolderName();
        });
        this.children.add(this.levelNameField);
        int i = this.width / 2 - 155;
        int j = this.width / 2 + 5;
        this.gameModeSwitchButton = this.addButton(new ButtonWidget(i, 100, 150, 20, new TranslatableText("selectWorld.gameMode"), arg -> {
            switch (this.currentMode) {
                case SURVIVAL: {
                    this.tweakDefaultsTo(Mode.HARDCORE);
                    break;
                }
                case HARDCORE: {
                    this.tweakDefaultsTo(Mode.CREATIVE);
                    break;
                }
                case CREATIVE: {
                    this.tweakDefaultsTo(Mode.SURVIVAL);
                }
            }
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(": ").append(new TranslatableText("selectWorld.gameMode." + CreateWorldScreen.this.currentMode.translationSuffix));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(CreateWorldScreen.this.firstGameModeDescriptionLine).append(" ").append(CreateWorldScreen.this.secondGameModeDescriptionLine);
            }
        });
        this.field_24286 = this.addButton(new ButtonWidget(j, 100, 150, 20, new TranslatableText("options.difficulty"), arg -> {
            this.field_24290 = this.field_24289 = this.field_24289.cycle();
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return new TranslatableText("options.difficulty").append(": ").append(CreateWorldScreen.this.field_24290.getTranslatableName());
            }
        });
        this.enableCheatsButton = this.addButton(new ButtonWidget(i, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), arg -> {
            this.tweakedCheats = true;
            this.cheatsEnabled = !this.cheatsEnabled;
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return super.getMessage().shallowCopy().append(" ").append(ScreenTexts.getToggleText(CreateWorldScreen.this.cheatsEnabled && !CreateWorldScreen.this.hardcore));
            }

            @Override
            protected MutableText getNarrationMessage() {
                return super.getNarrationMessage().append(". ").append(new TranslatableText("selectWorld.allowCommands.info"));
            }
        });
        this.field_25478 = this.addButton(new ButtonWidget(j, 151, 150, 20, new TranslatableText("selectWorld.dataPacks"), arg -> this.method_29694()));
        this.gameRulesButton = this.addButton(new ButtonWidget(i, 185, 150, 20, new TranslatableText("selectWorld.gameRules"), arg -> this.client.openScreen(new EditGameRulesScreen(this.gameRules.copy(), optional -> {
            this.client.openScreen(this);
            optional.ifPresent(arg -> {
                this.gameRules = arg;
            });
        }))));
        this.moreOptionsDialog.method_28092(this, this.client, this.textRenderer);
        this.moreOptionsButton = this.addButton(new ButtonWidget(j, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), arg -> this.toggleMoreOptions()));
        this.createLevelButton = this.addButton(new ButtonWidget(i, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), arg -> this.createLevel()));
        this.createLevelButton.active = !this.levelName.isEmpty();
        this.addButton(new ButtonWidget(j, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> {
            this.method_29695();
            this.client.openScreen(this.parent);
        }));
        this.setMoreOptionsOpen();
        this.setInitialFocus(this.levelNameField);
        this.tweakDefaultsTo(this.currentMode);
        this.updateSaveFolderName();
    }

    private void updateSettingsLabels() {
        this.firstGameModeDescriptionLine = new TranslatableText("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line1");
        this.secondGameModeDescriptionLine = new TranslatableText("selectWorld.gameMode." + this.currentMode.translationSuffix + ".line2");
    }

    private void updateSaveFolderName() {
        this.saveDirectoryName = this.levelNameField.getText().trim();
        if (this.saveDirectoryName.isEmpty()) {
            this.saveDirectoryName = "World";
        }
        try {
            this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
        }
        catch (Exception exception) {
            this.saveDirectoryName = "World";
            try {
                this.saveDirectoryName = FileNameUtil.getNextUniqueName(this.client.getLevelStorage().getSavesDirectory(), this.saveDirectoryName, "");
            }
            catch (Exception exception2) {
                throw new RuntimeException("Could not create save folder", exception2);
            }
        }
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void createLevel() {
        LevelInfo lv4;
        this.client.method_29970(new SaveLevelScreen(new TranslatableText("createWorld.preparing")));
        if (!this.method_29696()) {
            return;
        }
        GeneratorOptions lv = this.moreOptionsDialog.getGeneratorOptions(this.hardcore);
        if (lv.isDebugWorld()) {
            GameRules lv2 = new GameRules();
            lv2.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            LevelInfo lv3 = new LevelInfo(this.levelNameField.getText().trim(), GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, lv2, DataPackSettings.SAFE_MODE);
        } else {
            lv4 = new LevelInfo(this.levelNameField.getText().trim(), this.currentMode.defaultGameMode, this.hardcore, this.field_24290, this.cheatsEnabled && !this.hardcore, this.gameRules, this.field_25479);
        }
        this.client.method_29607(this.saveDirectoryName, lv4, this.moreOptionsDialog.method_29700(), lv);
    }

    private void toggleMoreOptions() {
        this.setMoreOptionsOpen(!this.moreOptionsOpen);
    }

    private void tweakDefaultsTo(Mode arg) {
        if (!this.tweakedCheats) {
            boolean bl = this.cheatsEnabled = arg == Mode.CREATIVE;
        }
        if (arg == Mode.HARDCORE) {
            this.hardcore = true;
            this.enableCheatsButton.active = false;
            this.moreOptionsDialog.bonusItemsButton.active = false;
            this.field_24290 = Difficulty.HARD;
            this.field_24286.active = false;
        } else {
            this.hardcore = false;
            this.enableCheatsButton.active = true;
            this.moreOptionsDialog.bonusItemsButton.active = true;
            this.field_24290 = this.field_24289;
            this.field_24286.active = true;
        }
        this.currentMode = arg;
        this.updateSettingsLabels();
    }

    public void setMoreOptionsOpen() {
        this.setMoreOptionsOpen(this.moreOptionsOpen);
    }

    private void setMoreOptionsOpen(boolean bl) {
        this.moreOptionsOpen = bl;
        this.gameModeSwitchButton.visible = !this.moreOptionsOpen;
        boolean bl2 = this.field_24286.visible = !this.moreOptionsOpen;
        if (this.moreOptionsDialog.isDebugWorld()) {
            this.field_25478.visible = false;
            this.gameModeSwitchButton.active = false;
            if (this.lastMode == null) {
                this.lastMode = this.currentMode;
            }
            this.tweakDefaultsTo(Mode.DEBUG);
            this.enableCheatsButton.visible = false;
        } else {
            this.gameModeSwitchButton.active = true;
            if (this.lastMode != null) {
                this.tweakDefaultsTo(this.lastMode);
            }
            this.enableCheatsButton.visible = !this.moreOptionsOpen;
            this.field_25478.visible = !this.moreOptionsOpen;
        }
        this.moreOptionsDialog.setVisible(this.moreOptionsOpen);
        this.levelNameField.setVisible(!this.moreOptionsOpen);
        if (this.moreOptionsOpen) {
            this.moreOptionsButton.setMessage(ScreenTexts.DONE);
        } else {
            this.moreOptionsButton.setMessage(new TranslatableText("selectWorld.moreWorldOptions"));
        }
        this.gameRulesButton.visible = !this.moreOptionsOpen;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (i == 257 || i == 335) {
            this.createLevel();
            return true;
        }
        return false;
    }

    @Override
    public void onClose() {
        if (this.moreOptionsOpen) {
            this.setMoreOptionsOpen(false);
        } else {
            this.client.openScreen(this.parent);
        }
        this.method_29695();
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 20, -1);
        if (this.moreOptionsOpen) {
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.enterSeed", new Object[0]), this.width / 2 - 100, 47, -6250336);
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.seedInfo", new Object[0]), this.width / 2 - 100, 85, -6250336);
            this.moreOptionsDialog.render(arg, i, j, f);
        } else {
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.enterName", new Object[0]), this.width / 2 - 100, 47, -6250336);
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.resultFolder", new Object[0]) + " " + this.saveDirectoryName, this.width / 2 - 100, 85, -6250336);
            this.levelNameField.render(arg, i, j, f);
            this.drawCenteredText(arg, this.textRenderer, this.firstGameModeDescriptionLine, this.width / 2 - 155 + 75, 122, -6250336);
            this.drawCenteredText(arg, this.textRenderer, this.secondGameModeDescriptionLine, this.width / 2 - 155 + 75, 134, -6250336);
            if (this.enableCheatsButton.visible) {
                this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.allowCommands.info", new Object[0]), this.width / 2 - 150, 172, -6250336);
            }
        }
        super.render(arg, i, j, f);
    }

    @Override
    protected <T extends Element> T addChild(T arg) {
        return super.addChild(arg);
    }

    @Override
    protected <T extends AbstractButtonWidget> T addButton(T arg) {
        return super.addButton(arg);
    }

    @Nullable
    protected Path method_29693() {
        if (this.field_25477 == null) {
            try {
                this.field_25477 = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
            }
            catch (IOException iOException) {
                field_25480.warn("Failed to create temporary dir", (Throwable)iOException);
                SystemToast.method_29627(this.client, this.saveDirectoryName);
                this.client.openScreen(this.parent);
            }
        }
        return this.field_25477;
    }

    private void method_29694() {
        Path path = this.method_29693();
        if (path != null) {
            File file = path.toFile();
            ResourcePackManager<ResourcePackProfile> lv = new ResourcePackManager<ResourcePackProfile>(ResourcePackProfile::new, new VanillaDataPackProvider(), new FileResourcePackProvider(file, ResourcePackSource.field_25347));
            lv.scanPacks();
            lv.setEnabledProfiles(this.field_25479.getEnabled());
            this.client.openScreen(new DataPackScreen((Screen)this, lv, this::method_29682, file));
        }
    }

    private void method_29682(ResourcePackManager<ResourcePackProfile> arg) {
        ImmutableList list = ImmutableList.copyOf(arg.getEnabledNames());
        List list2 = (List)arg.getNames().stream().filter(arg_0 -> CreateWorldScreen.method_29983((List)list, arg_0)).collect(ImmutableList.toImmutableList());
        DataPackSettings lv = new DataPackSettings((List<String>)list, list2);
        if (list.equals(this.field_25479.getEnabled())) {
            this.field_25479 = lv;
            return;
        }
        this.client.send(() -> this.client.openScreen(new SaveLevelScreen(new TranslatableText("dataPack.validation.working"))));
        ServerResourceManager.reload(arg.createResourcePacks(), CommandManager.RegistrationEnvironment.INTEGRATED, 2, Util.getServerWorkerExecutor(), this.client).handle((arg2, throwable) -> {
            if (throwable != null) {
                field_25480.warn("Failed to validate datapack", throwable);
                this.client.send(() -> this.client.openScreen(new ConfirmScreen(bl -> {
                    if (bl) {
                        this.method_29694();
                    } else {
                        this.field_25479 = DataPackSettings.SAFE_MODE;
                        this.client.openScreen(this);
                    }
                }, new TranslatableText("dataPack.validation.failed"), LiteralText.EMPTY, new TranslatableText("dataPack.validation.back"), new TranslatableText("dataPack.validation.reset"))));
            } else {
                this.client.send(() -> {
                    this.field_25479 = lv;
                    this.client.openScreen(this);
                });
            }
            return null;
        });
    }

    private void method_29695() {
        if (this.field_25477 != null) {
            try (Stream<Path> stream = Files.walk(this.field_25477, new FileVisitOption[0]);){
                stream.sorted(Comparator.reverseOrder()).forEach(path -> {
                    try {
                        Files.delete(path);
                    }
                    catch (IOException iOException) {
                        field_25480.warn("Failed to remove temporary file {}", path, (Object)iOException);
                    }
                });
            }
            catch (IOException iOException) {
                field_25480.warn("Failed to list temporary dir {}", (Object)this.field_25477);
            }
            this.field_25477 = null;
        }
    }

    private static void method_29687(Path path, Path path2, Path path3) {
        try {
            Util.method_29775(path, path2, path3);
        }
        catch (IOException iOException) {
            field_25480.warn("Failed to copy datapack file from {} to {}", (Object)path3, (Object)path2);
            throw new WorldCreationException(iOException);
        }
    }

    private boolean method_29696() {
        if (this.field_25477 != null) {
            try (LevelStorage.Session lv = this.client.getLevelStorage().createSession(this.saveDirectoryName);
                 Stream<Path> stream = Files.walk(this.field_25477, new FileVisitOption[0]);){
                Path path3 = lv.getDirectory(WorldSavePath.DATAPACKS);
                Files.createDirectories(path3, new FileAttribute[0]);
                stream.filter(path -> !path.equals(this.field_25477)).forEach(path2 -> CreateWorldScreen.method_29687(this.field_25477, path3, path2));
            }
            catch (IOException | WorldCreationException exception) {
                field_25480.warn("Failed to copy datapacks to world {}", (Object)this.saveDirectoryName, (Object)exception);
                SystemToast.method_29627(this.client, this.saveDirectoryName);
                this.client.openScreen(this.parent);
                this.method_29695();
                return false;
            }
            this.method_29695();
        }
        return true;
    }

    @Nullable
    public static Path method_29685(Path path, MinecraftClient arg) {
        MutableObject mutableObject = new MutableObject();
        try (Stream<Path> stream = Files.walk(path, new FileVisitOption[0]);){
            stream.filter(path2 -> !path2.equals(path)).forEach(path2 -> {
                Path path3 = (Path)mutableObject.getValue();
                if (path3 == null) {
                    try {
                        path3 = Files.createTempDirectory("mcworld-", new FileAttribute[0]);
                    }
                    catch (IOException iOException) {
                        field_25480.warn("Failed to create temporary dir");
                        throw new WorldCreationException(iOException);
                    }
                    mutableObject.setValue((Object)path3);
                }
                CreateWorldScreen.method_29687(path, path3, path2);
            });
        }
        catch (IOException | WorldCreationException exception) {
            field_25480.warn("Failed to copy datapacks from world {}", (Object)path, (Object)exception);
            SystemToast.method_29627(arg, path.toString());
            return null;
        }
        return (Path)mutableObject.getValue();
    }

    private static /* synthetic */ boolean method_29983(List list, String string) {
        return !list.contains(string);
    }

    @Environment(value=EnvType.CLIENT)
    static class WorldCreationException
    extends RuntimeException {
        public WorldCreationException(Throwable throwable) {
            super(throwable);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum Mode {
        SURVIVAL("survival", GameMode.SURVIVAL),
        HARDCORE("hardcore", GameMode.SURVIVAL),
        CREATIVE("creative", GameMode.CREATIVE),
        DEBUG("spectator", GameMode.SPECTATOR);

        private final String translationSuffix;
        private final GameMode defaultGameMode;

        private Mode(String string2, GameMode arg) {
            this.translationSuffix = string2;
            this.defaultGameMode = arg;
        }
    }
}

