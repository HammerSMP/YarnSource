/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.realms.RealmsLabel;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.gui.screen.RealmsConfigureWorldScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RealmsSlotOptionsScreen
extends RealmsScreen {
    public static final Text[] DIFFICULTIES = new Text[]{new TranslatableText("options.difficulty.peaceful"), new TranslatableText("options.difficulty.easy"), new TranslatableText("options.difficulty.normal"), new TranslatableText("options.difficulty.hard")};
    public static final Text[] GAME_MODES = new Text[]{new TranslatableText("selectWorld.gameMode.survival"), new TranslatableText("selectWorld.gameMode.creative"), new TranslatableText("selectWorld.gameMode.adventure")};
    private static final TranslatableText field_24207 = new TranslatableText("mco.configure.world.on");
    private static final TranslatableText field_24208 = new TranslatableText("mco.configure.world.off");
    private TextFieldWidget nameEdit;
    protected final RealmsConfigureWorldScreen parent;
    private int column1_x;
    private int column_width;
    private int column2_x;
    private final RealmsWorldOptions options;
    private final RealmsServer.WorldType worldType;
    private final int activeSlot;
    private int difficultyIndex;
    private int gameModeIndex;
    private Boolean pvp;
    private Boolean spawnNPCs;
    private Boolean spawnAnimals;
    private Boolean spawnMonsters;
    private Integer spawnProtection;
    private Boolean commandBlocks;
    private Boolean forceGameMode;
    private ButtonWidget pvpButton;
    private ButtonWidget spawnAnimalsButton;
    private ButtonWidget spawnMonstersButton;
    private ButtonWidget spawnNPCsButton;
    private SettingsSlider spawnProtectionButton;
    private ButtonWidget commandBlocksButton;
    private ButtonWidget gameModeButton;
    private RealmsLabel titleLabel;
    private RealmsLabel toastMessage;

    public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen arg, RealmsWorldOptions arg2, RealmsServer.WorldType arg3, int i) {
        this.parent = arg;
        this.options = arg2;
        this.worldType = arg3;
        this.activeSlot = i;
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256) {
            this.client.openScreen(this.parent);
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public void init() {
        this.column_width = 170;
        this.column1_x = this.width / 2 - this.column_width;
        this.column2_x = this.width / 2 + 10;
        this.difficultyIndex = this.options.difficulty;
        this.gameModeIndex = this.options.gameMode;
        if (this.worldType == RealmsServer.WorldType.NORMAL) {
            this.pvp = this.options.pvp;
            this.spawnProtection = this.options.spawnProtection;
            this.forceGameMode = this.options.forceGameMode;
            this.spawnAnimals = this.options.spawnAnimals;
            this.spawnMonsters = this.options.spawnMonsters;
            this.spawnNPCs = this.options.spawnNPCs;
            this.commandBlocks = this.options.commandBlocks;
        } else {
            TranslatableText lv3;
            if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
                TranslatableText lv = new TranslatableText("mco.configure.world.edit.subscreen.adventuremap");
            } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
                TranslatableText lv2 = new TranslatableText("mco.configure.world.edit.subscreen.inspiration");
            } else {
                lv3 = new TranslatableText("mco.configure.world.edit.subscreen.experience");
            }
            this.toastMessage = new RealmsLabel(lv3, this.width / 2, 26, 0xFF0000);
            this.pvp = true;
            this.spawnProtection = 0;
            this.forceGameMode = false;
            this.spawnAnimals = true;
            this.spawnMonsters = true;
            this.spawnNPCs = true;
            this.commandBlocks = true;
        }
        this.nameEdit = new TextFieldWidget(this.client.textRenderer, this.column1_x + 2, RealmsSlotOptionsScreen.row(1), this.column_width - 4, 20, null, new TranslatableText("mco.configure.world.edit.slot.name"));
        this.nameEdit.setMaxLength(10);
        this.nameEdit.setText(this.options.getSlotName(this.activeSlot));
        this.focusOn(this.nameEdit);
        this.pvpButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(1), this.column_width, 20, this.pvpTitle(), arg -> {
            this.pvp = this.pvp == false;
            arg.setMessage(this.pvpTitle());
        }));
        this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(3), this.column_width, 20, this.gameModeTitle(), arg -> {
            this.gameModeIndex = (this.gameModeIndex + 1) % GAME_MODES.length;
            arg.setMessage(this.gameModeTitle());
        }));
        this.spawnAnimalsButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(3), this.column_width, 20, this.spawnAnimalsTitle(), arg -> {
            this.spawnAnimals = this.spawnAnimals == false;
            arg.setMessage(this.spawnAnimalsTitle());
        }));
        this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(5), this.column_width, 20, this.difficultyTitle(), arg -> {
            this.difficultyIndex = (this.difficultyIndex + 1) % DIFFICULTIES.length;
            arg.setMessage(this.difficultyTitle());
            if (this.worldType == RealmsServer.WorldType.NORMAL) {
                this.spawnMonstersButton.active = this.difficultyIndex != 0;
                this.spawnMonstersButton.setMessage(this.spawnMonstersTitle());
            }
        }));
        this.spawnMonstersButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(5), this.column_width, 20, this.spawnMonstersTitle(), arg -> {
            this.spawnMonsters = this.spawnMonsters == false;
            arg.setMessage(this.spawnMonstersTitle());
        }));
        this.spawnProtectionButton = this.addButton(new SettingsSlider(this.column1_x, RealmsSlotOptionsScreen.row(7), this.column_width, this.spawnProtection, 0.0f, 16.0f));
        this.spawnNPCsButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(7), this.column_width, 20, this.spawnNPCsTitle(), arg -> {
            this.spawnNPCs = this.spawnNPCs == false;
            arg.setMessage(this.spawnNPCsTitle());
        }));
        this.gameModeButton = this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(9), this.column_width, 20, this.forceGameModeTitle(), arg -> {
            this.forceGameMode = this.forceGameMode == false;
            arg.setMessage(this.forceGameModeTitle());
        }));
        this.commandBlocksButton = this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(9), this.column_width, 20, this.commandBlocksTitle(), arg -> {
            this.commandBlocks = this.commandBlocks == false;
            arg.setMessage(this.commandBlocksTitle());
        }));
        if (this.worldType != RealmsServer.WorldType.NORMAL) {
            this.pvpButton.active = false;
            this.spawnAnimalsButton.active = false;
            this.spawnNPCsButton.active = false;
            this.spawnMonstersButton.active = false;
            this.spawnProtectionButton.active = false;
            this.commandBlocksButton.active = false;
            this.gameModeButton.active = false;
        }
        if (this.difficultyIndex == 0) {
            this.spawnMonstersButton.active = false;
        }
        this.addButton(new ButtonWidget(this.column1_x, RealmsSlotOptionsScreen.row(13), this.column_width, 20, new TranslatableText("mco.configure.world.buttons.done"), arg -> this.saveSettings()));
        this.addButton(new ButtonWidget(this.column2_x, RealmsSlotOptionsScreen.row(13), this.column_width, 20, ScreenTexts.CANCEL, arg -> this.client.openScreen(this.parent)));
        this.addChild(this.nameEdit);
        this.titleLabel = this.addChild(new RealmsLabel(new TranslatableText("mco.configure.world.buttons.options"), this.width / 2, 17, 0xFFFFFF));
        if (this.toastMessage != null) {
            this.addChild(this.toastMessage);
        }
        this.narrateLabels();
    }

    private Text difficultyTitle() {
        return new TranslatableText("options.difficulty").append(": ").append(DIFFICULTIES[this.difficultyIndex]);
    }

    private Text gameModeTitle() {
        return new TranslatableText("selectWorld.gameMode").append(": ").append(GAME_MODES[this.gameModeIndex]);
    }

    private Text pvpTitle() {
        return new TranslatableText("mco.configure.world.pvp").append(": ").append(RealmsSlotOptionsScreen.getWorldConfigureMessage(this.pvp));
    }

    private Text spawnAnimalsTitle() {
        return new TranslatableText("mco.configure.world.spawnAnimals").append(": ").append(RealmsSlotOptionsScreen.getWorldConfigureMessage(this.spawnAnimals));
    }

    private Text spawnMonstersTitle() {
        if (this.difficultyIndex == 0) {
            return new TranslatableText("mco.configure.world.spawnMonsters").append(": ").append(new TranslatableText("mco.configure.world.off"));
        }
        return new TranslatableText("mco.configure.world.spawnMonsters").append(": ").append(RealmsSlotOptionsScreen.getWorldConfigureMessage(this.spawnMonsters));
    }

    private Text spawnNPCsTitle() {
        return new TranslatableText("mco.configure.world.spawnNPCs").append(": ").append(RealmsSlotOptionsScreen.getWorldConfigureMessage(this.spawnNPCs));
    }

    private Text commandBlocksTitle() {
        return new TranslatableText("mco.configure.world.commandBlocks").append(": ").append(RealmsSlotOptionsScreen.getWorldConfigureMessage(this.commandBlocks));
    }

    private Text forceGameModeTitle() {
        return new TranslatableText("mco.configure.world.forceGameMode").append(": ").append(RealmsSlotOptionsScreen.getWorldConfigureMessage(this.forceGameMode));
    }

    private static Text getWorldConfigureMessage(boolean bl) {
        return bl ? field_24207 : field_24208;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        String string = I18n.translate("mco.configure.world.edit.slot.name", new Object[0]);
        this.textRenderer.draw(arg, string, (float)(this.column1_x + this.column_width / 2 - this.textRenderer.getWidth(string) / 2), (float)(RealmsSlotOptionsScreen.row(0) - 5), 0xFFFFFF);
        this.titleLabel.render(this, arg);
        if (this.toastMessage != null) {
            this.toastMessage.render(this, arg);
        }
        this.nameEdit.render(arg, i, j, f);
        super.render(arg, i, j, f);
    }

    private String getSlotName() {
        if (this.nameEdit.getText().equals(this.options.getDefaultSlotName(this.activeSlot))) {
            return "";
        }
        return this.nameEdit.getText();
    }

    private void saveSettings() {
        if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP || this.worldType == RealmsServer.WorldType.EXPERIENCE || this.worldType == RealmsServer.WorldType.INSPIRATION) {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.options.forceGameMode, this.getSlotName()));
        } else {
            this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.forceGameMode, this.getSlotName()));
        }
    }

    @Environment(value=EnvType.CLIENT)
    class SettingsSlider
    extends SliderWidget {
        private final double min;
        private final double max;

        public SettingsSlider(int i, int j, int k, int l, float f, float g) {
            super(i, j, k, 20, LiteralText.EMPTY, 0.0);
            this.min = f;
            this.max = g;
            this.value = (MathHelper.clamp((float)l, f, g) - f) / (g - f);
            this.updateMessage();
        }

        @Override
        public void applyValue() {
            if (!((RealmsSlotOptionsScreen)RealmsSlotOptionsScreen.this).spawnProtectionButton.active) {
                return;
            }
            RealmsSlotOptionsScreen.this.spawnProtection = (int)MathHelper.lerp(MathHelper.clamp(this.value, 0.0, 1.0), this.min, this.max);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(new TranslatableText("mco.configure.world.spawnProtection").append(": ").append(RealmsSlotOptionsScreen.this.spawnProtection == 0 ? new TranslatableText("mco.configure.world.off") : new LiteralText(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection))));
        }

        @Override
        public void onClick(double d, double e) {
        }

        @Override
        public void onRelease(double d, double e) {
        }
    }
}

