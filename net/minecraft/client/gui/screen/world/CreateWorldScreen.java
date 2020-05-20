/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.world;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5219;
import net.minecraft.class_5285;
import net.minecraft.class_5292;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.FileNameUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.LevelInfo;

@Environment(value=EnvType.CLIENT)
public class CreateWorldScreen
extends Screen {
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
    private boolean creatingLevel;
    private boolean moreOptionsOpen;
    private ButtonWidget createLevelButton;
    private ButtonWidget gameModeSwitchButton;
    private ButtonWidget field_24286;
    private ButtonWidget moreOptionsButton;
    private ButtonWidget gameRulesButton;
    private ButtonWidget enableCheatsButton;
    private Text firstGameModeDescriptionLine;
    private Text secondGameModeDescriptionLine;
    private String levelName;
    private GameRules gameRules = new GameRules();
    public final class_5292 field_24588;

    public CreateWorldScreen(@Nullable Screen arg, class_5219 arg2) {
        this(arg, new class_5292(arg2.getLevelInfo().getGeneratorOptions()));
        LevelInfo lv = arg2.getLevelInfo();
        this.levelName = lv.getLevelName();
        this.cheatsEnabled = lv.isHardcore();
        this.tweakedCheats = true;
        this.field_24290 = this.field_24289 = lv.getDifficulty();
        this.gameRules.setAllValues(arg2.getGameRules(), null);
        if (lv.hasStructures()) {
            this.currentMode = Mode.HARDCORE;
        } else if (lv.getGameMode().isSurvivalLike()) {
            this.currentMode = Mode.SURVIVAL;
        } else if (lv.getGameMode().isCreative()) {
            this.currentMode = Mode.CREATIVE;
        }
    }

    public CreateWorldScreen(@Nullable Screen arg) {
        this(arg, new class_5292());
    }

    private CreateWorldScreen(@Nullable Screen arg, class_5292 arg2) {
        super(new TranslatableText("selectWorld.create"));
        this.parent = arg;
        this.levelName = I18n.translate("selectWorld.newWorld", new Object[0]);
        this.field_24588 = arg2;
    }

    @Override
    public void tick() {
        this.levelNameField.tick();
        this.field_24588.tick();
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
        this.gameModeSwitchButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 100, 150, 20, new TranslatableText("selectWorld.gameMode"), arg -> {
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
        this.field_24286 = this.addButton(new ButtonWidget(this.width / 2 + 5, 100, 150, 20, new TranslatableText("options.difficulty"), arg -> {
            this.field_24290 = this.field_24289 = this.field_24289.cycle();
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return new TranslatableText("options.difficulty").append(": ").append(CreateWorldScreen.this.field_24290.getTranslatableName());
            }
        });
        this.enableCheatsButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 151, 150, 20, new TranslatableText("selectWorld.allowCommands"), arg -> {
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
        this.gameRulesButton = this.addButton(new ButtonWidget(this.width / 2 - 155, 185, 150, 20, new TranslatableText("selectWorld.gameRules"), arg -> this.client.openScreen(new EditGameRulesScreen(this.gameRules.copy(), optional -> {
            this.client.openScreen(this);
            optional.ifPresent(arg -> {
                this.gameRules = arg;
            });
        }))));
        this.field_24588.method_28092(this, this.client, this.textRenderer);
        this.moreOptionsButton = this.addButton(new ButtonWidget(this.width / 2 + 5, 185, 150, 20, new TranslatableText("selectWorld.moreWorldOptions"), arg -> this.toggleMoreOptions()));
        this.createLevelButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, new TranslatableText("selectWorld.create"), arg -> this.createLevel()));
        this.createLevelButton.active = !this.levelName.isEmpty();
        this.addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.method_28084();
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
        this.client.openScreen(null);
        if (this.creatingLevel) {
            return;
        }
        this.creatingLevel = true;
        class_5285 lv = this.field_24588.method_28096(this.hardcore);
        if (lv.method_28033()) {
            GameRules lv2 = new GameRules();
            lv2.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, null);
            LevelInfo lv3 = new LevelInfo(this.levelNameField.getText().trim(), GameMode.SPECTATOR, false, Difficulty.PEACEFUL, true, lv2, lv);
        } else {
            lv4 = new LevelInfo(this.levelNameField.getText().trim(), this.currentMode.defaultGameMode, this.hardcore, this.field_24290, this.cheatsEnabled && !this.hardcore, this.gameRules, lv);
        }
        this.client.startIntegratedServer(this.saveDirectoryName, lv4);
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
            this.field_24588.field_24589.active = false;
            this.field_24290 = Difficulty.HARD;
            this.field_24286.active = false;
        } else {
            this.hardcore = false;
            this.enableCheatsButton.active = true;
            this.field_24588.field_24589.active = true;
            this.field_24290 = this.field_24289;
            this.field_24286.active = true;
        }
        this.currentMode = arg;
        this.updateSettingsLabels();
    }

    public void method_28084() {
        this.setMoreOptionsOpen(this.moreOptionsOpen);
    }

    private void setMoreOptionsOpen(boolean bl) {
        this.moreOptionsOpen = bl;
        this.gameModeSwitchButton.visible = !this.moreOptionsOpen;
        boolean bl2 = this.field_24286.visible = !this.moreOptionsOpen;
        if (this.field_24588.method_28085()) {
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
        }
        this.field_24588.method_28101(this.moreOptionsOpen);
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
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawCenteredText(arg, this.textRenderer, this.title, this.width / 2, 20, -1);
        if (this.moreOptionsOpen) {
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.enterSeed", new Object[0]), this.width / 2 - 100, 47, -6250336);
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("selectWorld.seedInfo", new Object[0]), this.width / 2 - 100, 85, -6250336);
            this.field_24588.render(arg, i, j, f);
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

