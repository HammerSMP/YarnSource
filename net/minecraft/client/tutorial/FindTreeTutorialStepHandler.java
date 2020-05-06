/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import com.google.common.collect.Sets;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStep;
import net.minecraft.client.tutorial.TutorialStepHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameMode;

@Environment(value=EnvType.CLIENT)
public class FindTreeTutorialStepHandler
implements TutorialStepHandler {
    private static final Set<Block> TREE_BLOCKS = Sets.newHashSet((Object[])new Block[]{Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES});
    private static final Text TITLE = new TranslatableText("tutorial.find_tree.title");
    private static final Text DESCRIPTION = new TranslatableText("tutorial.find_tree.description");
    private final TutorialManager tutorialManager;
    private TutorialToast toast;
    private int ticks;

    public FindTreeTutorialStepHandler(TutorialManager arg) {
        this.tutorialManager = arg;
    }

    @Override
    public void tick() {
        ClientPlayerEntity lv;
        ++this.ticks;
        if (this.tutorialManager.getGameMode() != GameMode.SURVIVAL) {
            this.tutorialManager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks == 1 && (lv = this.tutorialManager.getClient().player) != null) {
            for (Block lv2 : TREE_BLOCKS) {
                if (!lv.inventory.contains(new ItemStack(lv2))) continue;
                this.tutorialManager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
            if (FindTreeTutorialStepHandler.hasBrokenTreeBlocks(lv)) {
                this.tutorialManager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
        }
        if (this.ticks >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, false);
            this.tutorialManager.getClient().getToastManager().add(this.toast);
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
    public void onTarget(ClientWorld arg, HitResult arg2) {
        BlockState lv;
        if (arg2.getType() == HitResult.Type.BLOCK && TREE_BLOCKS.contains((lv = arg.getBlockState(((BlockHitResult)arg2).getBlockPos())).getBlock())) {
            this.tutorialManager.setStep(TutorialStep.PUNCH_TREE);
        }
    }

    @Override
    public void onSlotUpdate(ItemStack arg) {
        for (Block lv : TREE_BLOCKS) {
            if (arg.getItem() != lv.asItem()) continue;
            this.tutorialManager.setStep(TutorialStep.CRAFT_PLANKS);
            return;
        }
    }

    public static boolean hasBrokenTreeBlocks(ClientPlayerEntity arg) {
        for (Block lv : TREE_BLOCKS) {
            if (arg.getStatHandler().getStat(Stats.MINED.getOrCreateStat(lv)) <= 0) continue;
            return true;
        }
        return false;
    }
}

