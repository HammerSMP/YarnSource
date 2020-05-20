/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.screen.ingame;

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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class GameModeSwitcherScreen
extends Screen {
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/gamemode_switcher.png");
    private static final int WIDTH = GameMode.values().length * 30 - 5;
    private final Optional<GameMode> previousGameMode;
    private Optional<GameMode> selectedGameMode = Optional.empty();
    private int cachedMouseX;
    private int cachedMouseY;
    private boolean useMouse;
    private final List<Button> gameModeButtons = Lists.newArrayList();

    public GameModeSwitcherScreen() {
        super(NarratorManager.EMPTY);
        this.previousGameMode = GameMode.fromGameMode(MinecraftClient.getInstance().interactionManager.getPreviousGameMode());
    }

    @Override
    protected void init() {
        super.init();
        this.selectedGameMode = this.previousGameMode.isPresent() ? this.previousGameMode : GameMode.fromGameMode(this.client.interactionManager.getCurrentGameMode());
        for (int i = 0; i < GameMode.MODES.length; ++i) {
            GameMode lv = GameMode.MODES[i];
            this.gameModeButtons.add(new Button(lv, this.width / 2 - WIDTH / 2 + i * 30, this.height / 2 - 30));
        }
    }

    @Override
    public void render(MatrixStack arg, int i, int j, float f) {
        if (this.shouldClose()) {
            return;
        }
        arg.push();
        RenderSystem.enableBlend();
        this.client.getTextureManager().bindTexture(TEXTURE);
        int k = this.width / 2 - 62;
        int l = this.height / 2 - 30 - 27;
        GameModeSwitcherScreen.drawTexture(arg, k, l, 0.0f, 0.0f, 125, 75, 128, 128);
        arg.pop();
        super.render(arg, i, j, f);
        this.selectedGameMode.ifPresent(arg2 -> this.drawCenteredText(arg, this.textRenderer, ((GameMode)arg2).getName(), this.width / 2, this.height / 2 - 30 - 20, -1));
        int m = this.textRenderer.getWidth(I18n.translate("debug.gamemodes.press_f4", new Object[0]));
        this.drawText(arg, I18n.translate("debug.gamemodes.press_f4", new Object[0]), I18n.translate("debug.gamemodes.select_next", new Object[0]), 5, m);
        if (!this.useMouse) {
            this.cachedMouseX = i;
            this.cachedMouseY = j;
            this.useMouse = true;
        }
        boolean bl = this.cachedMouseX == i && this.cachedMouseY == j;
        for (Button lv : this.gameModeButtons) {
            lv.render(arg, i, j, f);
            this.selectedGameMode.ifPresent(arg2 -> lv.select(arg2 == lv.gameMode));
            if (bl || !lv.isHovered()) continue;
            this.selectedGameMode = Optional.of(lv.gameMode);
        }
    }

    private void drawText(MatrixStack arg, String string, String string2, int i, int j) {
        int k = 0x55FFFF;
        int l = 0xFFFFFF;
        this.drawStringWithShadow(arg, this.textRenderer, "[", this.width / 2 - j - 18, this.height / 2 + i, 0x55FFFF);
        this.drawCenteredString(arg, this.textRenderer, string, this.width / 2 - j / 2 - 10, this.height / 2 + i, 0x55FFFF);
        this.drawCenteredString(arg, this.textRenderer, "]", this.width / 2 - 5, this.height / 2 + i, 0x55FFFF);
        this.drawStringWithShadow(arg, this.textRenderer, string2, this.width / 2 + 5, this.height / 2 + i, 0xFFFFFF);
    }

    @Override
    private void onClose() {
        GameModeSwitcherScreen.switchGameMode(this.client, this.selectedGameMode);
    }

    private static void switchGameMode(MinecraftClient arg, Optional<GameMode> optional) {
        if (arg.interactionManager == null || arg.player == null || !optional.isPresent()) {
            return;
        }
        Optional optional2 = GameMode.fromGameMode(arg.interactionManager.getCurrentGameMode());
        GameMode lv = optional.get();
        if (optional2.isPresent() && arg.player.hasPermissionLevel(2) && lv != optional2.get()) {
            arg.player.sendChatMessage(lv.getCommand());
        }
    }

    private boolean shouldClose() {
        if (!InputUtil.isKeyPressed(this.client.getWindow().getHandle(), 292)) {
            this.onClose();
            this.client.openScreen(null);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 293 && this.selectedGameMode.isPresent()) {
            this.useMouse = false;
            this.selectedGameMode = this.selectedGameMode.get().getNext();
            return true;
        }
        return super.keyPressed(i, j, k);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public class Button
    extends AbstractButtonWidget {
        private final GameMode gameMode;
        private boolean selected;

        public Button(GameMode arg2, int i, int j) {
            super(i, j, 25, 25, arg2.getName());
            this.gameMode = arg2;
        }

        @Override
        public void renderButton(MatrixStack arg, int i, int j, float f) {
            MinecraftClient lv = MinecraftClient.getInstance();
            this.render(arg, lv.getTextureManager());
            this.gameMode.renderIcon(GameModeSwitcherScreen.this.itemRenderer, this.x + 5, this.y + 5);
            if (this.selected) {
                this.renderSelected(arg, lv.getTextureManager());
            }
        }

        @Override
        public boolean isHovered() {
            return super.isHovered() || this.selected;
        }

        public void select(boolean bl) {
            this.selected = bl;
            this.narrate();
        }

        private void render(MatrixStack arg, TextureManager arg2) {
            arg2.bindTexture(TEXTURE);
            arg.push();
            arg.translate(this.x, this.y, 0.0);
            Button.drawTexture(arg, 0, 0, 0.0f, 75.0f, 25, 25, 128, 128);
            arg.pop();
        }

        private void renderSelected(MatrixStack arg, TextureManager arg2) {
            arg2.bindTexture(TEXTURE);
            arg.push();
            arg.translate(this.x, this.y, 0.0);
            Button.drawTexture(arg, 0, 0, 25.0f, 75.0f, 25, 25, 128, 128);
            arg.pop();
        }
    }

    @Environment(value=EnvType.CLIENT)
    static enum GameMode {
        CREATIVE(new TranslatableText("gameMode.creative"), "/gamemode creative", new ItemStack(Blocks.GRASS_BLOCK)),
        SURVIVAL(new TranslatableText("gameMode.survival"), "/gamemode survival", new ItemStack(Items.IRON_SWORD)),
        ADVENTURE(new TranslatableText("gameMode.adventure"), "/gamemode adventure", new ItemStack(Items.MAP)),
        SPECTATOR(new TranslatableText("gameMode.spectator"), "/gamemode spectator", new ItemStack(Items.ENDER_EYE));

        protected static final GameMode[] MODES;
        final Text name;
        final String command;
        final ItemStack icon;

        private GameMode(Text arg, String string2, ItemStack arg2) {
            this.name = arg;
            this.command = string2;
            this.icon = arg2;
        }

        private void renderIcon(ItemRenderer arg, int i, int j) {
            arg.renderGuiItem(this.icon, i, j);
        }

        private Text getName() {
            return this.name;
        }

        private String getCommand() {
            return this.command;
        }

        private Optional<GameMode> getNext() {
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

        private static Optional<GameMode> fromGameMode(net.minecraft.world.GameMode arg) {
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
            MODES = GameMode.values();
        }
    }
}

