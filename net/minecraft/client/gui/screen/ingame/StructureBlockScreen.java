/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class StructureBlockScreen
extends Screen {
    private final StructureBlockBlockEntity structureBlock;
    private BlockMirror mirror = BlockMirror.NONE;
    private BlockRotation rotation = BlockRotation.NONE;
    private StructureBlockMode mode = StructureBlockMode.DATA;
    private boolean ignoreEntities;
    private boolean showAir;
    private boolean showBoundingBox;
    private TextFieldWidget inputName;
    private TextFieldWidget inputPosX;
    private TextFieldWidget inputPosY;
    private TextFieldWidget inputPosZ;
    private TextFieldWidget inputSizeX;
    private TextFieldWidget inputSizeY;
    private TextFieldWidget inputSizeZ;
    private TextFieldWidget inputIntegrity;
    private TextFieldWidget inputSeed;
    private TextFieldWidget inputMetadata;
    private ButtonWidget buttonDone;
    private ButtonWidget buttonCancel;
    private ButtonWidget buttonSave;
    private ButtonWidget buttonLoad;
    private ButtonWidget buttonRotate0;
    private ButtonWidget buttonRotate90;
    private ButtonWidget buttonRotate180;
    private ButtonWidget buttonRotate270;
    private ButtonWidget buttonMode;
    private ButtonWidget buttonDetect;
    private ButtonWidget buttonEntities;
    private ButtonWidget buttonMirror;
    private ButtonWidget buttonShowAir;
    private ButtonWidget buttonShowBoundingBox;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.0###");

    public StructureBlockScreen(StructureBlockBlockEntity structureBlock) {
        super(new TranslatableText(Blocks.STRUCTURE_BLOCK.getTranslationKey()));
        this.structureBlock = structureBlock;
        this.decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
    }

    @Override
    public void tick() {
        this.inputName.tick();
        this.inputPosX.tick();
        this.inputPosY.tick();
        this.inputPosZ.tick();
        this.inputSizeX.tick();
        this.inputSizeY.tick();
        this.inputSizeZ.tick();
        this.inputIntegrity.tick();
        this.inputSeed.tick();
        this.inputMetadata.tick();
    }

    private void done() {
        if (this.method_2516(StructureBlockBlockEntity.Action.UPDATE_DATA)) {
            this.client.openScreen(null);
        }
    }

    private void cancel() {
        this.structureBlock.setMirror(this.mirror);
        this.structureBlock.setRotation(this.rotation);
        this.structureBlock.setMode(this.mode);
        this.structureBlock.setIgnoreEntities(this.ignoreEntities);
        this.structureBlock.setShowAir(this.showAir);
        this.structureBlock.setShowBoundingBox(this.showBoundingBox);
        this.client.openScreen(null);
    }

    @Override
    protected void init() {
        this.client.keyboard.enableRepeatEvents(true);
        this.buttonDone = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, ScreenTexts.DONE, arg -> this.done()));
        this.buttonCancel = this.addButton(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, ScreenTexts.CANCEL, arg -> this.cancel()));
        this.buttonSave = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 185, 50, 20, new TranslatableText("structure_block.button.save"), arg -> {
            if (this.structureBlock.getMode() == StructureBlockMode.SAVE) {
                this.method_2516(StructureBlockBlockEntity.Action.SAVE_AREA);
                this.client.openScreen(null);
            }
        }));
        this.buttonLoad = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 185, 50, 20, new TranslatableText("structure_block.button.load"), arg -> {
            if (this.structureBlock.getMode() == StructureBlockMode.LOAD) {
                this.method_2516(StructureBlockBlockEntity.Action.LOAD_AREA);
                this.client.openScreen(null);
            }
        }));
        this.buttonMode = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, 185, 50, 20, new LiteralText("MODE"), arg -> {
            this.structureBlock.cycleMode();
            this.updateMode();
        }));
        this.buttonDetect = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 120, 50, 20, new TranslatableText("structure_block.button.detect_size"), arg -> {
            if (this.structureBlock.getMode() == StructureBlockMode.SAVE) {
                this.method_2516(StructureBlockBlockEntity.Action.SCAN_AREA);
                this.client.openScreen(null);
            }
        }));
        this.buttonEntities = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 160, 50, 20, new LiteralText("ENTITIES"), arg -> {
            this.structureBlock.setIgnoreEntities(!this.structureBlock.shouldIgnoreEntities());
            this.updateIgnoreEntitiesButton();
        }));
        this.buttonMirror = this.addButton(new ButtonWidget(this.width / 2 - 20, 185, 40, 20, new LiteralText("MIRROR"), arg -> {
            switch (this.structureBlock.getMirror()) {
                case NONE: {
                    this.structureBlock.setMirror(BlockMirror.LEFT_RIGHT);
                    break;
                }
                case LEFT_RIGHT: {
                    this.structureBlock.setMirror(BlockMirror.FRONT_BACK);
                    break;
                }
                case FRONT_BACK: {
                    this.structureBlock.setMirror(BlockMirror.NONE);
                }
            }
            this.updateMirrorButton();
        }));
        this.buttonShowAir = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 80, 50, 20, new LiteralText("SHOWAIR"), arg -> {
            this.structureBlock.setShowAir(!this.structureBlock.shouldShowAir());
            this.updateShowAirButton();
        }));
        this.buttonShowBoundingBox = this.addButton(new ButtonWidget(this.width / 2 + 4 + 100, 80, 50, 20, new LiteralText("SHOWBB"), arg -> {
            this.structureBlock.setShowBoundingBox(!this.structureBlock.shouldShowBoundingBox());
            this.updateShowBoundingBoxButton();
        }));
        this.buttonRotate0 = this.addButton(new ButtonWidget(this.width / 2 - 1 - 40 - 1 - 40 - 20, 185, 40, 20, new LiteralText("0"), arg -> {
            this.structureBlock.setRotation(BlockRotation.NONE);
            this.updateRotationButton();
        }));
        this.buttonRotate90 = this.addButton(new ButtonWidget(this.width / 2 - 1 - 40 - 20, 185, 40, 20, new LiteralText("90"), arg -> {
            this.structureBlock.setRotation(BlockRotation.CLOCKWISE_90);
            this.updateRotationButton();
        }));
        this.buttonRotate180 = this.addButton(new ButtonWidget(this.width / 2 + 1 + 20, 185, 40, 20, new LiteralText("180"), arg -> {
            this.structureBlock.setRotation(BlockRotation.CLOCKWISE_180);
            this.updateRotationButton();
        }));
        this.buttonRotate270 = this.addButton(new ButtonWidget(this.width / 2 + 1 + 40 + 1 + 20, 185, 40, 20, new LiteralText("270"), arg -> {
            this.structureBlock.setRotation(BlockRotation.COUNTERCLOCKWISE_90);
            this.updateRotationButton();
        }));
        this.inputName = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 40, 300, 20, (Text)new TranslatableText("structure_block.structure_name")){

            @Override
            public boolean charTyped(char chr, int keyCode) {
                if (!StructureBlockScreen.this.isValidCharacterForName(this.getText(), chr, this.getCursor())) {
                    return false;
                }
                return super.charTyped(chr, keyCode);
            }
        };
        this.inputName.setMaxLength(64);
        this.inputName.setText(this.structureBlock.getStructureName());
        this.children.add(this.inputName);
        BlockPos lv = this.structureBlock.getOffset();
        this.inputPosX = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 80, 80, 20, new TranslatableText("structure_block.position.x"));
        this.inputPosX.setMaxLength(15);
        this.inputPosX.setText(Integer.toString(lv.getX()));
        this.children.add(this.inputPosX);
        this.inputPosY = new TextFieldWidget(this.textRenderer, this.width / 2 - 72, 80, 80, 20, new TranslatableText("structure_block.position.y"));
        this.inputPosY.setMaxLength(15);
        this.inputPosY.setText(Integer.toString(lv.getY()));
        this.children.add(this.inputPosY);
        this.inputPosZ = new TextFieldWidget(this.textRenderer, this.width / 2 + 8, 80, 80, 20, new TranslatableText("structure_block.position.z"));
        this.inputPosZ.setMaxLength(15);
        this.inputPosZ.setText(Integer.toString(lv.getZ()));
        this.children.add(this.inputPosZ);
        BlockPos lv2 = this.structureBlock.getSize();
        this.inputSizeX = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 120, 80, 20, new TranslatableText("structure_block.size.x"));
        this.inputSizeX.setMaxLength(15);
        this.inputSizeX.setText(Integer.toString(lv2.getX()));
        this.children.add(this.inputSizeX);
        this.inputSizeY = new TextFieldWidget(this.textRenderer, this.width / 2 - 72, 120, 80, 20, new TranslatableText("structure_block.size.y"));
        this.inputSizeY.setMaxLength(15);
        this.inputSizeY.setText(Integer.toString(lv2.getY()));
        this.children.add(this.inputSizeY);
        this.inputSizeZ = new TextFieldWidget(this.textRenderer, this.width / 2 + 8, 120, 80, 20, new TranslatableText("structure_block.size.z"));
        this.inputSizeZ.setMaxLength(15);
        this.inputSizeZ.setText(Integer.toString(lv2.getZ()));
        this.children.add(this.inputSizeZ);
        this.inputIntegrity = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 120, 80, 20, new TranslatableText("structure_block.integrity.integrity"));
        this.inputIntegrity.setMaxLength(15);
        this.inputIntegrity.setText(this.decimalFormat.format(this.structureBlock.getIntegrity()));
        this.children.add(this.inputIntegrity);
        this.inputSeed = new TextFieldWidget(this.textRenderer, this.width / 2 - 72, 120, 80, 20, new TranslatableText("structure_block.integrity.seed"));
        this.inputSeed.setMaxLength(31);
        this.inputSeed.setText(Long.toString(this.structureBlock.getSeed()));
        this.children.add(this.inputSeed);
        this.inputMetadata = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 120, 240, 20, new TranslatableText("structure_block.custom_data"));
        this.inputMetadata.setMaxLength(128);
        this.inputMetadata.setText(this.structureBlock.getMetadata());
        this.children.add(this.inputMetadata);
        this.mirror = this.structureBlock.getMirror();
        this.updateMirrorButton();
        this.rotation = this.structureBlock.getRotation();
        this.updateRotationButton();
        this.mode = this.structureBlock.getMode();
        this.updateMode();
        this.ignoreEntities = this.structureBlock.shouldIgnoreEntities();
        this.updateIgnoreEntitiesButton();
        this.showAir = this.structureBlock.shouldShowAir();
        this.updateShowAirButton();
        this.showBoundingBox = this.structureBlock.shouldShowBoundingBox();
        this.updateShowBoundingBoxButton();
        this.setInitialFocus(this.inputName);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        String string = this.inputName.getText();
        String string2 = this.inputPosX.getText();
        String string3 = this.inputPosY.getText();
        String string4 = this.inputPosZ.getText();
        String string5 = this.inputSizeX.getText();
        String string6 = this.inputSizeY.getText();
        String string7 = this.inputSizeZ.getText();
        String string8 = this.inputIntegrity.getText();
        String string9 = this.inputSeed.getText();
        String string10 = this.inputMetadata.getText();
        this.init(client, width, height);
        this.inputName.setText(string);
        this.inputPosX.setText(string2);
        this.inputPosY.setText(string3);
        this.inputPosZ.setText(string4);
        this.inputSizeX.setText(string5);
        this.inputSizeY.setText(string6);
        this.inputSizeZ.setText(string7);
        this.inputIntegrity.setText(string8);
        this.inputSeed.setText(string9);
        this.inputMetadata.setText(string10);
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    private void updateIgnoreEntitiesButton() {
        this.buttonEntities.setMessage(ScreenTexts.getToggleText(!this.structureBlock.shouldIgnoreEntities()));
    }

    private void updateShowAirButton() {
        this.buttonShowAir.setMessage(ScreenTexts.getToggleText(this.structureBlock.shouldShowAir()));
    }

    private void updateShowBoundingBoxButton() {
        this.buttonShowBoundingBox.setMessage(ScreenTexts.getToggleText(this.structureBlock.shouldShowBoundingBox()));
    }

    private void updateMirrorButton() {
        BlockMirror lv = this.structureBlock.getMirror();
        switch (lv) {
            case NONE: {
                this.buttonMirror.setMessage(new LiteralText("|"));
                break;
            }
            case LEFT_RIGHT: {
                this.buttonMirror.setMessage(new LiteralText("< >"));
                break;
            }
            case FRONT_BACK: {
                this.buttonMirror.setMessage(new LiteralText("^ v"));
            }
        }
    }

    private void updateRotationButton() {
        this.buttonRotate0.active = true;
        this.buttonRotate90.active = true;
        this.buttonRotate180.active = true;
        this.buttonRotate270.active = true;
        switch (this.structureBlock.getRotation()) {
            case NONE: {
                this.buttonRotate0.active = false;
                break;
            }
            case CLOCKWISE_180: {
                this.buttonRotate180.active = false;
                break;
            }
            case COUNTERCLOCKWISE_90: {
                this.buttonRotate270.active = false;
                break;
            }
            case CLOCKWISE_90: {
                this.buttonRotate90.active = false;
            }
        }
    }

    private void updateMode() {
        this.inputName.setVisible(false);
        this.inputPosX.setVisible(false);
        this.inputPosY.setVisible(false);
        this.inputPosZ.setVisible(false);
        this.inputSizeX.setVisible(false);
        this.inputSizeY.setVisible(false);
        this.inputSizeZ.setVisible(false);
        this.inputIntegrity.setVisible(false);
        this.inputSeed.setVisible(false);
        this.inputMetadata.setVisible(false);
        this.buttonSave.visible = false;
        this.buttonLoad.visible = false;
        this.buttonDetect.visible = false;
        this.buttonEntities.visible = false;
        this.buttonMirror.visible = false;
        this.buttonRotate0.visible = false;
        this.buttonRotate90.visible = false;
        this.buttonRotate180.visible = false;
        this.buttonRotate270.visible = false;
        this.buttonShowAir.visible = false;
        this.buttonShowBoundingBox.visible = false;
        switch (this.structureBlock.getMode()) {
            case SAVE: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputSizeX.setVisible(true);
                this.inputSizeY.setVisible(true);
                this.inputSizeZ.setVisible(true);
                this.buttonSave.visible = true;
                this.buttonDetect.visible = true;
                this.buttonEntities.visible = true;
                this.buttonShowAir.visible = true;
                break;
            }
            case LOAD: {
                this.inputName.setVisible(true);
                this.inputPosX.setVisible(true);
                this.inputPosY.setVisible(true);
                this.inputPosZ.setVisible(true);
                this.inputIntegrity.setVisible(true);
                this.inputSeed.setVisible(true);
                this.buttonLoad.visible = true;
                this.buttonEntities.visible = true;
                this.buttonMirror.visible = true;
                this.buttonRotate0.visible = true;
                this.buttonRotate90.visible = true;
                this.buttonRotate180.visible = true;
                this.buttonRotate270.visible = true;
                this.buttonShowBoundingBox.visible = true;
                this.updateRotationButton();
                break;
            }
            case CORNER: {
                this.inputName.setVisible(true);
                break;
            }
            case DATA: {
                this.inputMetadata.setVisible(true);
            }
        }
        this.buttonMode.setMessage(new TranslatableText("structure_block.mode." + this.structureBlock.getMode().asString()));
    }

    private boolean method_2516(StructureBlockBlockEntity.Action arg) {
        BlockPos lv = new BlockPos(this.parseInt(this.inputPosX.getText()), this.parseInt(this.inputPosY.getText()), this.parseInt(this.inputPosZ.getText()));
        BlockPos lv2 = new BlockPos(this.parseInt(this.inputSizeX.getText()), this.parseInt(this.inputSizeY.getText()), this.parseInt(this.inputSizeZ.getText()));
        float f = this.parseFloat(this.inputIntegrity.getText());
        long l = this.parseLong(this.inputSeed.getText());
        this.client.getNetworkHandler().sendPacket(new UpdateStructureBlockC2SPacket(this.structureBlock.getPos(), arg, this.structureBlock.getMode(), this.inputName.getText(), lv, lv2, this.structureBlock.getMirror(), this.structureBlock.getRotation(), this.inputMetadata.getText(), this.structureBlock.shouldIgnoreEntities(), this.structureBlock.shouldShowAir(), this.structureBlock.shouldShowBoundingBox(), f, l));
        return true;
    }

    private long parseLong(String string) {
        try {
            return Long.valueOf(string);
        }
        catch (NumberFormatException numberFormatException) {
            return 0L;
        }
    }

    private float parseFloat(String string) {
        try {
            return Float.valueOf(string).floatValue();
        }
        catch (NumberFormatException numberFormatException) {
            return 1.0f;
        }
    }

    private int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException numberFormatException) {
            return 0;
        }
    }

    @Override
    public void onClose() {
        this.cancel();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 257 || keyCode == 335) {
            this.done();
            return true;
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        StructureBlockMode lv = this.structureBlock.getMode();
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        if (lv != StructureBlockMode.DATA) {
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("structure_block.structure_name", new Object[0]), this.width / 2 - 153, 30, 0xA0A0A0);
            this.inputName.render(matrices, mouseX, mouseY, delta);
        }
        if (lv == StructureBlockMode.LOAD || lv == StructureBlockMode.SAVE) {
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("structure_block.position", new Object[0]), this.width / 2 - 153, 70, 0xA0A0A0);
            this.inputPosX.render(matrices, mouseX, mouseY, delta);
            this.inputPosY.render(matrices, mouseX, mouseY, delta);
            this.inputPosZ.render(matrices, mouseX, mouseY, delta);
            String string = I18n.translate("structure_block.include_entities", new Object[0]);
            int k = this.textRenderer.getWidth(string);
            this.drawStringWithShadow(matrices, this.textRenderer, string, this.width / 2 + 154 - k, 150, 0xA0A0A0);
        }
        if (lv == StructureBlockMode.SAVE) {
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("structure_block.size", new Object[0]), this.width / 2 - 153, 110, 0xA0A0A0);
            this.inputSizeX.render(matrices, mouseX, mouseY, delta);
            this.inputSizeY.render(matrices, mouseX, mouseY, delta);
            this.inputSizeZ.render(matrices, mouseX, mouseY, delta);
            String string2 = I18n.translate("structure_block.detect_size", new Object[0]);
            int l = this.textRenderer.getWidth(string2);
            this.drawStringWithShadow(matrices, this.textRenderer, string2, this.width / 2 + 154 - l, 110, 0xA0A0A0);
            String string3 = I18n.translate("structure_block.show_air", new Object[0]);
            int m = this.textRenderer.getWidth(string3);
            this.drawStringWithShadow(matrices, this.textRenderer, string3, this.width / 2 + 154 - m, 70, 0xA0A0A0);
        }
        if (lv == StructureBlockMode.LOAD) {
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("structure_block.integrity", new Object[0]), this.width / 2 - 153, 110, 0xA0A0A0);
            this.inputIntegrity.render(matrices, mouseX, mouseY, delta);
            this.inputSeed.render(matrices, mouseX, mouseY, delta);
            String string4 = I18n.translate("structure_block.show_boundingbox", new Object[0]);
            int n = this.textRenderer.getWidth(string4);
            this.drawStringWithShadow(matrices, this.textRenderer, string4, this.width / 2 + 154 - n, 70, 0xA0A0A0);
        }
        if (lv == StructureBlockMode.DATA) {
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("structure_block.custom_data", new Object[0]), this.width / 2 - 153, 110, 0xA0A0A0);
            this.inputMetadata.render(matrices, mouseX, mouseY, delta);
        }
        String string5 = "structure_block.mode_info." + lv.asString();
        this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate(string5, new Object[0]), this.width / 2 - 153, 174, 0xA0A0A0);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

