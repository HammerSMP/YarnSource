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
    private static final Set<Block> TREE_BLOCKS = Sets.newHashSet((Object[])new Block[]{Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.WARPED_STEM, Blocks.CRIMSON_STEM, Blocks.OAK_WOOD, Blocks.SPRUCE_WOOD, Blocks.BIRCH_WOOD, Blocks.JUNGLE_WOOD, Blocks.ACACIA_WOOD, Blocks.DARK_OAK_WOOD, Blocks.WARPED_HYPHAE, Blocks.CRIMSON_HYPHAE, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES, Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK});
    private static final Text TITLE = new TranslatableText("tutorial.find_tree.title");
    private static final Text DESCRIPTION = new TranslatableText("tutorial.find_tree.description");
    private final TutorialManager manager;
    private TutorialToast toast;
    private int ticks;

    public FindTreeTutorialStepHandler(TutorialManager manager) {
        this.manager = manager;
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
            for (Block lv2 : TREE_BLOCKS) {
                if (!lv.inventory.contains(new ItemStack(lv2))) continue;
                this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
            if (FindTreeTutorialStepHandler.hasBrokenTreeBlocks(lv)) {
                this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                return;
            }
        }
        if (this.ticks >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Type.TREE, TITLE, DESCRIPTION, false);
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
    public void onTarget(ClientWorld world, HitResult hitResult) {
        BlockState lv;
        if (hitResult.getType() == HitResult.Type.BLOCK && TREE_BLOCKS.contains((lv = world.getBlockState(((BlockHitResult)hitResult).getBlockPos())).getBlock())) {
            this.manager.setStep(TutorialStep.PUNCH_TREE);
        }
    }

    @Override
    public void onSlotUpdate(ItemStack stack) {
        for (Block lv : TREE_BLOCKS) {
            if (stack.getItem() != lv.asItem()) continue;
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
            return;
        }
    }

    public static boolean hasBrokenTreeBlocks(ClientPlayerEntity player) {
        for (Block lv : TREE_BLOCKS) {
            if (player.getStatHandler().getStat(Stats.MINED.getOrCreateStat(lv)) <= 0) continue;
            return true;
        }
        return false;
    }
}

