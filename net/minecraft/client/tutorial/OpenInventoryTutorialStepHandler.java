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
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler
implements TutorialStepHandler {
    private static final Text TITLE = new TranslatableText("tutorial.open_inventory.title");
    private static final Text DESCRIPTION = new TranslatableText("tutorial.open_inventory.description", TutorialManager.getKeybindName("inventory"));
    private final TutorialManager manager;
    private TutorialToast field_5642;
    private int ticks;

    public OpenInventoryTutorialStepHandler(TutorialManager arg) {
        this.manager = arg;
    }

    @Override
    public void tick() {
        ++this.ticks;
        if (this.manager.getGameMode() != GameMode.SURVIVAL) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks >= 600 && this.field_5642 == null) {
            this.field_5642 = new TutorialToast(TutorialToast.Type.RECIPE_BOOK, TITLE, DESCRIPTION, false);
            this.manager.getClient().getToastManager().add(this.field_5642);
        }
    }

    @Override
    public void destroy() {
        if (this.field_5642 != null) {
            this.field_5642.hide();
            this.field_5642 = null;
        }
    }

    @Override
    public void onInventoryOpened() {
        this.manager.setStep(TutorialStep.CRAFT_PLANKS);
    }
}

