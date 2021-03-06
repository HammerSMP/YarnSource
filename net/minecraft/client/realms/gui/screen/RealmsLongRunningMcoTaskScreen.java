/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.realms.gui.screen;

import com.google.common.collect.Sets;
import java.util.HashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.realms.Errable;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.realms.RealmsScreen;
import net.minecraft.client.realms.exception.RealmsDefaultUncaughtExceptionHandler;
import net.minecraft.client.realms.gui.LongRunningTask;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsLongRunningMcoTaskScreen
extends RealmsScreen
implements Errable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen parent;
    private volatile String title = "";
    private volatile boolean error;
    private volatile Text errorMessage;
    private volatile boolean aborted;
    private int animTicks;
    private final LongRunningTask task;
    private final int buttonLength = 212;
    public static final String[] symbols = new String[]{"\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588", "_ _ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587", "_ _ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586", "_ _ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585", "_ \u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584", "\u2583 \u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _ _", "\u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _ _", "\u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _ _", "\u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _ _", "\u2584 \u2585 \u2586 \u2587 \u2588 \u2587 \u2586 \u2585 \u2584 \u2583 _"};

    public RealmsLongRunningMcoTaskScreen(Screen parent, LongRunningTask task) {
        this.parent = parent;
        this.task = task;
        task.setScreen(this);
        Thread thread = new Thread((Runnable)task, "Realms-long-running-task");
        thread.setUncaughtExceptionHandler(new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        thread.start();
    }

    @Override
    public void tick() {
        super.tick();
        Realms.narrateRepeatedly(this.title);
        ++this.animTicks;
        this.task.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.cancelOrBackButtonClicked();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void init() {
        this.task.init();
        this.addButton(new ButtonWidget(this.width / 2 - 106, RealmsLongRunningMcoTaskScreen.row(12), 212, 20, ScreenTexts.CANCEL, arg -> this.cancelOrBackButtonClicked()));
    }

    private void cancelOrBackButtonClicked() {
        this.aborted = true;
        this.task.abortTask();
        this.client.openScreen(this.parent);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.drawCenteredString(matrices, this.textRenderer, this.title, this.width / 2, RealmsLongRunningMcoTaskScreen.row(3), 0xFFFFFF);
        if (!this.error) {
            this.drawCenteredString(matrices, this.textRenderer, symbols[this.animTicks % symbols.length], this.width / 2, RealmsLongRunningMcoTaskScreen.row(8), 0x808080);
        }
        if (this.error) {
            this.drawCenteredText(matrices, this.textRenderer, this.errorMessage, this.width / 2, RealmsLongRunningMcoTaskScreen.row(8), 0xFF0000);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void error(Text arg2) {
        this.error = true;
        this.errorMessage = arg2;
        Realms.narrateNow(arg2.getString());
        this.onError();
        this.addButton(new ButtonWidget(this.width / 2 - 106, this.height / 4 + 120 + 12, 200, 20, ScreenTexts.BACK, arg -> this.cancelOrBackButtonClicked()));
    }

    public void onError() {
        HashSet set = Sets.newHashSet((Iterable)this.buttons);
        this.children.removeIf(set::contains);
        this.buttons.clear();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean aborted() {
        return this.aborted;
    }
}

