/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class JigsawBlockScreen
extends Screen {
    private final JigsawBlockEntity jigsaw;
    private TextFieldWidget nameField;
    private TextFieldWidget targetField;
    private TextFieldWidget poolField;
    private TextFieldWidget finalStateField;
    private int generationDepth;
    private boolean keepJigsaws = true;
    private ButtonWidget jointRotationButton;
    private ButtonWidget doneButton;
    private JigsawBlockEntity.Joint joint;

    public JigsawBlockScreen(JigsawBlockEntity arg) {
        super(NarratorManager.EMPTY);
        this.jigsaw = arg;
    }

    @Override
    public void tick() {
        this.nameField.tick();
        this.targetField.tick();
        this.poolField.tick();
        this.finalStateField.tick();
    }

    private void onDone() {
        this.updateServer();
        this.client.openScreen(null);
    }

    private void onCancel() {
        this.client.openScreen(null);
    }

    private void updateServer() {
        this.client.getNetworkHandler().sendPacket(new UpdateJigsawC2SPacket(this.jigsaw.getPos(), new Identifier(this.nameField.getText()), new Identifier(this.targetField.getText()), new Identifier(this.poolField.getText()), this.finalStateField.getText(), this.joint));
    }

    private void generate() {
        this.client.getNetworkHandler().sendPacket(new JigsawGeneratingC2SPacket(this.jigsaw.getPos(), this.generationDepth, this.keepJigsaws));
    }

    @Override
    public void onClose() {
        this.onCancel();
    }

    @Override
    protected void init() {
        boolean bl;
        this.client.keyboard.enableRepeatEvents(true);
        this.poolField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 20, 300, 20, new TranslatableText("jigsaw_block.pool"));
        this.poolField.setMaxLength(128);
        this.poolField.setText(this.jigsaw.getPool().toString());
        this.poolField.setChangedListener(string -> this.updateDoneButtonState());
        this.children.add(this.poolField);
        this.nameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 55, 300, 20, new TranslatableText("jigsaw_block.name"));
        this.nameField.setMaxLength(128);
        this.nameField.setText(this.jigsaw.getName().toString());
        this.nameField.setChangedListener(string -> this.updateDoneButtonState());
        this.children.add(this.nameField);
        this.targetField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 90, 300, 20, new TranslatableText("jigsaw_block.target"));
        this.targetField.setMaxLength(128);
        this.targetField.setText(this.jigsaw.getTarget().toString());
        this.targetField.setChangedListener(string -> this.updateDoneButtonState());
        this.children.add(this.targetField);
        this.finalStateField = new TextFieldWidget(this.textRenderer, this.width / 2 - 152, 125, 300, 20, new TranslatableText("jigsaw_block.final_state"));
        this.finalStateField.setMaxLength(256);
        this.finalStateField.setText(this.jigsaw.getFinalState());
        this.children.add(this.finalStateField);
        this.joint = this.jigsaw.getJoint();
        int i = this.textRenderer.getWidth(I18n.translate("jigsaw_block.joint_label", new Object[0])) + 10;
        this.jointRotationButton = this.addButton(new ButtonWidget(this.width / 2 - 152 + i, 150, 300 - i, 20, this.getLocalizedJointName(), arg -> {
            JigsawBlockEntity.Joint[] lvs = JigsawBlockEntity.Joint.values();
            int i = (this.joint.ordinal() + 1) % lvs.length;
            this.joint = lvs[i];
            arg.setMessage(this.getLocalizedJointName());
        }));
        this.jointRotationButton.active = bl = JigsawBlock.getFacing(this.jigsaw.getCachedState()).getAxis().isVertical();
        this.jointRotationButton.visible = bl;
        this.addButton(new SliderWidget(this.width / 2 - 154, 180, 100, 20, LiteralText.EMPTY, 0.0){
            {
                this.updateMessage();
            }

            @Override
            protected void updateMessage() {
                this.setMessage(new TranslatableText("jigsaw_block.levels", JigsawBlockScreen.this.generationDepth));
            }

            @Override
            protected void applyValue() {
                JigsawBlockScreen.this.generationDepth = MathHelper.floor(MathHelper.clampedLerp(0.0, 7.0, this.value));
            }
        });
        this.addButton(new ButtonWidget(this.width / 2 - 50, 180, 100, 20, new TranslatableText("jigsaw_block.keep_jigsaws"), arg -> {
            this.keepJigsaws = !this.keepJigsaws;
            arg.queueNarration(250);
        }){

            @Override
            public Text getMessage() {
                return ScreenTexts.method_30619(super.getMessage(), JigsawBlockScreen.this.keepJigsaws);
            }
        });
        this.addButton(new ButtonWidget(this.width / 2 + 54, 180, 100, 20, new TranslatableText("jigsaw_block.generate"), arg -> {
            this.onDone();
            this.generate();
        }));
        this.doneButton = this.addButton(new ButtonWidget(this.width / 2 - 4 - 150, 210, 150, 20, ScreenTexts.DONE, arg -> this.onDone()));
        this.addButton(new ButtonWidget(this.width / 2 + 4, 210, 150, 20, ScreenTexts.CANCEL, arg -> this.onCancel()));
        this.setInitialFocus(this.poolField);
        this.updateDoneButtonState();
    }

    private void updateDoneButtonState() {
        this.doneButton.active = Identifier.isValid(this.nameField.getText()) && Identifier.isValid(this.targetField.getText()) && Identifier.isValid(this.poolField.getText());
    }

    @Override
    public void resize(MinecraftClient arg, int i, int j) {
        String string = this.nameField.getText();
        String string2 = this.targetField.getText();
        String string3 = this.poolField.getText();
        String string4 = this.finalStateField.getText();
        int k = this.generationDepth;
        JigsawBlockEntity.Joint lv = this.joint;
        this.init(arg, i, j);
        this.nameField.setText(string);
        this.targetField.setText(string2);
        this.poolField.setText(string3);
        this.finalStateField.setText(string4);
        this.generationDepth = k;
        this.joint = lv;
        this.jointRotationButton.setMessage(this.getLocalizedJointName());
    }

    private Text getLocalizedJointName() {
        return new TranslatableText("jigsaw_block.joint." + this.joint.asString());
    }

    @Override
    public void removed() {
        this.client.keyboard.enableRepeatEvents(false);
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (super.keyPressed(i, j, k)) {
            return true;
        }
        if (this.doneButton.active && (i == 257 || i == 335)) {
            this.onDone();
            return true;
        }
        return false;
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("jigsaw_block.pool", new Object[0]), this.width / 2 - 153, 10, 0xA0A0A0);
        this.poolField.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("jigsaw_block.name", new Object[0]), this.width / 2 - 153, 45, 0xA0A0A0);
        this.nameField.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("jigsaw_block.target", new Object[0]), this.width / 2 - 153, 80, 0xA0A0A0);
        this.targetField.render(arg, i, j, f);
        this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("jigsaw_block.final_state", new Object[0]), this.width / 2 - 153, 115, 0xA0A0A0);
        this.finalStateField.render(arg, i, j, f);
        if (JigsawBlock.getFacing(this.jigsaw.getCachedState()).getAxis().isVertical()) {
            this.drawStringWithShadow(arg, this.textRenderer, I18n.translate("jigsaw_block.joint_label", new Object[0]), this.width / 2 - 153, 156, 0xFFFFFF);
        }
        super.render(arg, i, j, f);
    }
}

