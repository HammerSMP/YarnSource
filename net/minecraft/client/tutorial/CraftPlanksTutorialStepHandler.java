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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class CraftPlanksTutorialStepHandler
implements TutorialStepHandler {
    private static final Text TITLE = new TranslatableText("tutorial.craft_planks.title");
    private static final Text DESCRIPTION = new TranslatableText("tutorial.craft_planks.description");
    private final TutorialManager manager;
    private TutorialToast toast;
    private int ticks;

    public CraftPlanksTutorialStepHandler(TutorialManager arg) {
        this.manager = arg;
    }

    @Override
    public void tick() {
        ClientPlayerEntity lv;
        ++this.ticks;
        if (this.manager.getGameMode() != GameMode.SURVIVAL) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks == 1 && (lv = this.manager.getClient().player) != null) {
            if (lv.inventory.contains(ItemTags.PLANKS)) {
                this.manager.setStep(TutorialStep.NONE);
                return;
            }
            if (CraftPlanksTutorialStepHandler.hasCrafted(lv, ItemTags.PLANKS)) {
                this.manager.setStep(TutorialStep.NONE);
                return;
            }
        }
        if (this.ticks >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Type.WOODEN_PLANKS, TITLE, DESCRIPTION, false);
            this.manager.getClient().getToastManager().add(this.toast);
        }
    }

    @Override
    public void destroy() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }

    @Override
    public void onSlotUpdate(ItemStack arg) {
        Item lv = arg.getItem();
        if (ItemTags.PLANKS.contains(lv)) {
            this.manager.setStep(TutorialStep.NONE);
        }
    }

    public static boolean hasCrafted(ClientPlayerEntity arg, Tag<Item> arg2) {
        for (Item lv : arg2.values()) {
            if (arg.getStatHandler().getStat(Stats.CRAFTED.getOrCreateStat(lv)) <= 0) continue;
            return true;
        }
        return false;
    }
}

