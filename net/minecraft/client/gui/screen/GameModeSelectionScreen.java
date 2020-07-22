/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GameModeSelectionScreen
extends Screen {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/gamemode_switcher.png");
    private static final int UI_WIDTH = GameMode.values().length * 30 - 5;
    private static final Text field_25454 = new TranslatableText("debug.gamemodes.select_next", new TranslatableText("debug.gamemodes.press_f4").formatted(Formatting.AQUA));
    private final Optional<GameMode> currentGameMode;
    private Optional<GameMode> gameMode = Optional.empty();
    private int lastMouseX;
    private int lastMouseY;
    private boolean mouseUsedForSelection;
    private final List<ButtonWidget> gameModeButtons = Lists.newArrayList();

    public GameModeSelectionScreen() {
        super(NarratorManager.EMPTY);
        this.currentGameMode = GameMode.of(this.method_30106());
    }

    private net.minecraft.world.GameMode method_30106() {
        net.minecraft.world.GameMode lv = MinecraftClient.getInstance().interactionManager.getCurrentGameMode();
        net.minecraft.world.GameMode lv2 = MinecraftClient.getInstance().interactionManager.getPreviousGameMode();
        if (lv2 == net.minecraft.world.GameMode.NOT_SET) {
            lv2 = lv == net.minecraft.world.GameMode.CREATIVE ? net.minecraft.world.GameMode.SURVIVAL : net.minecraft.world.GameMode.CREATIVE;
        }
        return lv2;
    }

    @Override
    protected void init() {
        super.init();
        this.gameMode = this.currentGameMode.isPresent() ? this.currentGameMode : GameMode.of(this.client.interactionManager.getCurrentGameMode());
        for (int i = 0; i < GameMode.VALUES.length; ++i) {
            GameMode lv = GameMode.VALUES[i];
            this.gameModeButtons.add(new ButtonWidget(lv, this.width / 2 - UI_WIDTH / 2 + i * 30, this.height / 2 - 30));
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.checkForClose()) {
            return;
        }
        matrices.push();
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = this.width / 2 - 62;
        int l = this.height / 2 - 30 - 27;
        GameModeSelectionScreen.drawTexture(matrices, k, l, 0.0f, 0.0f, 125, 75, 128, 128);
        matrices.pop();
        super.render(matrices, mouseX, mouseY, delta);
        this.gameMode.ifPresent(arg2 -> this.drawCenteredText(matrices, this.textRenderer, ((GameMode)arg2).getText(), this.width / 2, this.height / 2 - 30 - 20, -1));
        this.drawCenteredText(matrices, this.textRenderer, field_25454, this.width / 2, this.height / 2 + 5, 0xFFFFFF);
        if (!this.mouseUsedForSelection) {
            this.lastMouseX = mouseX;
            this.lastMouseY = mouseY;
            this.mouseUsedForSelection = true;
        }
        boolean bl = this.lastMouseX == mouseX && this.lastMouseY == mouseY;
        for (ButtonWidget lv : this.gameModeButtons) {
            lv.render(matrices, mouseX, mouseY, delta);
            this.gameMode.ifPresent(arg2 -> lv.setSelected(arg2 == lv.gameMode));
            if (bl || !lv.isHovered()) continue;
            this.gameMode = Optional.of(lv.gameMode);
        }
    }

    private void apply() {
        GameModeSelectionScreen.apply(this.client, this.gameMode);
    }

    private static void apply(MinecraftClient client, Optional<GameMode> gameMode) {
        if (client.interactionManager == null || client.player == null || !gameMode.isPresent()) {
            return;
        }
        Optional optional2 = GameMode.of(client.interactionManager.getCurrentGameMode());
        GameMode lv = gameMode.get();
        if (optional2.isPresent() && client.player.hasPermissionLevel(2) && lv != optional2.get()) {
            client.player.sendChatMessage(lv.getCommand());
        }
    }

    private boolean checkForClose() {
        if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), 292)) {
            this.apply();
            this.client.openScreen(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 293 && this.gameMode.isPresent()) {
            this.mouseUsedForSelection = false;
            this.gameMode = this.gameMode.get().next();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public class ButtonWidget
    extends AbstractButtonWidget {
        private final GameMode gameMode;
        private boolean selected;

        public ButtonWidget(GameMode arg2, int x, int y) {
            super(x, y, 25, 25, arg2.getText());
            this.gameMode = arg2;
        }

        @Override
        public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
            MinecraftClient lv = MinecraftClient.getInstance();
            this.drawBackground(matrices, lv.getTextureManager());
            this.gameMode.renderIcon(GameModeSelectionScreen.this.itemRenderer, this.x + 5, this.y + 5);
            if (this.selected) {
                this.drawSelectionBox(matrices, lv.getTextureManager());
            }
        }

        @Override
        public boolean isHovered() {
            return super.isHovered() || this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            this.narrate();
        }

        private void drawBackground(MatrixStack matrices, TextureManager arg2) {
            arg2.bindTexture(TEXTURE);
            matrices.push();
            matrices.translate(this.x, this.y, 0.0);
            ButtonWidget.drawTexture(matrices, 0, 0, 0.0f, 75.0f, 25, 25, 128, 128);
            matrices.pop();
        }

        private void drawSelectionBox(MatrixStack matrices, TextureManager arg2) {
            arg2.bindTexture(TEXTURE);
            matrices.push();
            matrices.translate(this.x, this.y, 0.0);
            ButtonWidget.drawTexture(matrices, 0, 0, 25.0f, 75.0f, 25, 25, 128, 128);
            matrices.pop();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum GameMode {
        CREATIVE(new TranslatableText("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
        SURVIVAL(new TranslatableText("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
        ADVENTURE(new TranslatableText("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
        SPECTATOR(new TranslatableText("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

        protected static final GameMode[] VALUES;
        final Text text;
        final String command;
        final ItemStack icon;

        private GameMode(Text arg, String command, ItemStack icon) {
            this.text = arg;
            this.command = command;
            this.icon = icon;
        }

        private void renderIcon(ItemRenderer arg, int x, int y) {
            arg.renderInGuiWithOverrides(this.icon, x, y);
        }

        private Text getText() {
            return this.text;
        }

        private String getCommand() {
            return this.command;
        }

        private Optional<GameMode> next() {
            switch (this) {
                case CREATIVE: {
                    return Optional.of(SURVIVAL);
                }
                case SURVIVAL: {
                    return Optional.of(ADVENTURE);
                }
                case ADVENTURE: {
                    return Optional.of(SPECTATOR);
                }
            }
            return Optional.of(CREATIVE);
        }

        private static Optional<GameMode> of(net.minecraft.world.GameMode arg) {
            switch (arg) {
                case SPECTATOR: {
                    return Optional.of(SPECTATOR);
                }
                case SURVIVAL: {
                    return Optional.of(SURVIVAL);
                }
                case CREATIVE: {
                    return Optional.of(CREATIVE);
                }
                case ADVENTURE: {
                    return Optional.of(ADVENTURE);
                }
            }
            return Optional.empty();
        }

        static {
            VALUES = GameMode.values();
        }
    }
}

