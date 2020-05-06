/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.gui.hud.spectator;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.SpectatorHud;
import net.minecraft.client.gui.hud.spectator.RootSpectatorCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCloseCallback;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommand;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuCommandGroup;
import net.minecraft.client.gui.hud.spectator.SpectatorMenuState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class SpectatorMenu {
    private static final SpectatorMenuCommand CLOSE_COMMAND = new CloseSpectatorMenuCommand();
    private static final SpectatorMenuCommand PREVIOUS_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(-1, true);
    private static final SpectatorMenuCommand NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, true);
    private static final SpectatorMenuCommand DISABLED_NEXT_PAGE_COMMAND = new ChangePageSpectatorMenuCommand(1, false);
    public static final SpectatorMenuCommand BLANK_COMMAND = new SpectatorMenuCommand(){

        @Override
        public void use(SpectatorMenu arg) {
        }

        @Override
        public Text getName() {
            return LiteralText.EMPTY;
        }

        @Override
        public void renderIcon(MatrixStack arg, float f, int i) {
        }

        @Override
        public boolean isEnabled() {
            return false;
        }
    };
    private final SpectatorMenuCloseCallback closeCallback;
    private final List<SpectatorMenuState> stateStack = Lists.newArrayList();
    private SpectatorMenuCommandGroup currentGroup = new RootSpectatorCommandGroup();
    private int selectedSlot = -1;
    private int page;

    public SpectatorMenu(SpectatorMenuCloseCallback arg) {
        this.closeCallback = arg;
    }

    public SpectatorMenuCommand getCommand(int i) {
        int j = i + this.page * 6;
        if (this.page > 0 && i == 0) {
            return PREVIOUS_PAGE_COMMAND;
        }
        if (i == 7) {
            if (j < this.currentGroup.getCommands().size()) {
                return NEXT_PAGE_COMMAND;
            }
            return DISABLED_NEXT_PAGE_COMMAND;
        }
        if (i == 8) {
            return CLOSE_COMMAND;
        }
        if (j < 0 || j >= this.currentGroup.getCommands().size()) {
            return BLANK_COMMAND;
        }
        return (SpectatorMenuCommand)MoreObjects.firstNonNull((Object)this.currentGroup.getCommands().get(j), (Object)BLANK_COMMAND);
    }

    public List<SpectatorMenuCommand> getCommands() {
        ArrayList list = Lists.newArrayList();
        for (int i = 0; i <= 8; ++i) {
            list.add(this.getCommand(i));
        }
        return list;
    }

    public SpectatorMenuCommand getSelectedCommand() {
        return this.getCommand(this.selectedSlot);
    }

    public SpectatorMenuCommandGroup getCurrentGroup() {
        return this.currentGroup;
    }

    public void useCommand(int i) {
        SpectatorMenuCommand lv = this.getCommand(i);
        if (lv != BLANK_COMMAND) {
            if (this.selectedSlot == i && lv.isEnabled()) {
                lv.use(this);
            } else {
                this.selectedSlot = i;
            }
        }
    }

    public void close() {
        this.closeCallback.close(this);
    }

    public int getSelectedSlot() {
        return this.selectedSlot;
    }

    public void selectElement(SpectatorMenuCommandGroup arg) {
        this.stateStack.add(this.getCurrentState());
        this.currentGroup = arg;
        this.selectedSlot = -1;
        this.page = 0;
    }

    public SpectatorMenuState getCurrentState() {
        return new SpectatorMenuState(this.currentGroup, this.getCommands(), this.selectedSlot);
    }

    @Environment(value=EnvType.CLIENT)
    static class ChangePageSpectatorMenuCommand
    implements SpectatorMenuCommand {
        private final int direction;
        private final boolean enabled;

        public ChangePageSpectatorMenuCommand(int i, boolean bl) {
            this.direction = i;
            this.enabled = bl;
        }

        @Override
        public void use(SpectatorMenu arg) {
            arg.page = arg.page + this.direction;
        }

        @Override
        public Text getName() {
            if (this.direction < 0) {
                return new TranslatableText("spectatorMenu.previous_page");
            }
            return new TranslatableText("spectatorMenu.next_page");
        }

        @Override
        public void renderIcon(MatrixStack arg, float f, int i) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
            if (this.direction < 0) {
                DrawableHelper.drawTexture(arg, 0, 0, 144.0f, 0.0f, 16, 16, 256, 256);
            } else {
                DrawableHelper.drawTexture(arg, 0, 0, 160.0f, 0.0f, 16, 16, 256, 256);
            }
        }

        @Override
        public boolean isEnabled() {
            return this.enabled;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class CloseSpectatorMenuCommand
    implements SpectatorMenuCommand {
        private CloseSpectatorMenuCommand() {
        }

        @Override
        public void use(SpectatorMenu arg) {
            arg.close();
        }

        @Override
        public Text getName() {
            return new TranslatableText("spectatorMenu.close");
        }

        @Override
        public void renderIcon(MatrixStack arg, float f, int i) {
            MinecraftClient.getInstance().getTextureManager().bindTexture(SpectatorHud.SPECTATOR_TEX);
            DrawableHelper.drawTexture(arg, 0, 0, 128.0f, 0.0f, 16, 16, 256, 256);
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}

