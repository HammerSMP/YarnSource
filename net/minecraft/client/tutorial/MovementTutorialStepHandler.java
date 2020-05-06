/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class MovementTutorialStepHandler
implements TutorialStepHandler {
    private static final Text MOVE_TITLE = new TranslatableText("tutorial.move.title", TutorialManager.getKeybindName("forward"), TutorialManager.getKeybindName("left"), TutorialManager.getKeybindName("back"), TutorialManager.getKeybindName("right"));
    private static final Text MOVE_DESCRIPTION = new TranslatableText("tutorial.move.description", TutorialManager.getKeybindName("jump"));
    private static final Text LOOK_TITLE = new TranslatableText("tutorial.look.title");
    private static final Text LOOK_DESCRIPTION = new TranslatableText("tutorial.look.description");
    private final TutorialManager manager;
    private TutorialToast moveToast;
    private TutorialToast lookAroundToast;
    private int ticks;
    private int movedTicks;
    private int lookedAroundTicks;
    private boolean movedLastTick;
    private boolean lookedAroundLastTick;
    private int moveAroundCompletionTicks = -1;
    private int lookAroundCompletionTicks = -1;

    public MovementTutorialStepHandler(TutorialManager arg) {
        this.manager = arg;
    }

    @Override
    public void tick() {
        ++this.ticks;
        if (this.movedLastTick) {
            ++this.movedTicks;
            this.movedLastTick = false;
        }
        if (this.lookedAroundLastTick) {
            ++this.lookedAroundTicks;
            this.lookedAroundLastTick = false;
        }
        if (this.moveAroundCompletionTicks == -1 && this.movedTicks > 40) {
            if (this.moveToast != null) {
                this.moveToast.hide();
                this.moveToast = null;
            }
            this.moveAroundCompletionTicks = this.ticks;
        }
        if (this.lookAroundCompletionTicks == -1 && this.lookedAroundTicks > 40) {
            if (this.lookAroundToast != null) {
                this.lookAroundToast.hide();
                this.lookAroundToast = null;
            }
            this.lookAroundCompletionTicks = this.ticks;
        }
        if (this.moveAroundCompletionTicks != -1 && this.lookAroundCompletionTicks != -1) {
            if (this.manager.getGameMode() == GameMode.SURVIVAL) {
                this.manager.setStep(TutorialStep.FIND_TREE);
            } else {
                this.manager.setStep(TutorialStep.NONE);
            }
        }
        if (this.moveToast != null) {
            this.moveToast.setProgress((float)this.movedTicks / 40.0f);
        }
        if (this.lookAroundToast != null) {
            this.lookAroundToast.setProgress((float)this.lookedAroundTicks / 40.0f);
        }
        if (this.ticks >= 100) {
            if (this.moveAroundCompletionTicks == -1 && this.moveToast == null) {
                this.moveToast = new TutorialToast(TutorialToast.Type.MOVEMENT_KEYS, MOVE_TITLE, MOVE_DESCRIPTION, true);
                this.manager.getClient().getToastManager().add(this.moveToast);
            } else if (this.moveAroundCompletionTicks != -1 && this.ticks - this.moveAroundCompletionTicks >= 20 && this.lookAroundCompletionTicks == -1 && this.lookAroundToast == null) {
                this.lookAroundToast = new TutorialToast(TutorialToast.Type.MOUSE, LOOK_TITLE, LOOK_DESCRIPTION, true);
                this.manager.getClient().getToastManager().add(this.lookAroundToast);
            }
        }
    }

    @Override
    public void destroy() {
        if (this.moveToast != null) {
            this.moveToast.hide();
            this.moveToast = null;
        }
        if (this.lookAroundToast != null) {
            this.lookAroundToast.hide();
            this.lookAroundToast = null;
        }
    }

    @Override
    public void onMovement(Input arg) {
        if (arg.pressingForward || arg.pressingBack || arg.pressingLeft || arg.pressingRight || arg.jumping) {
            this.movedLastTick = true;
        }
    }

    @Override
    public void onMouseUpdate(double d, double e) {
        if (Math.abs(d) > 0.01 || Math.abs(e) > 0.01) {
            this.lookedAroundLastTick = true;
        }
    }
}

