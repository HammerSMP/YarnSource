/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmithingTableBlock
extends CraftingTableBlock {
    private static final TranslatableText SCREEN_TITLE = new TranslatableText("container.upgrade");

    protected SmithingTableBlock(AbstractBlock.Settings arg) {
        super(arg);
    }

    @Override
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState arg, World arg2, BlockPos arg32) {
        return new SimpleNamedScreenHandlerFactory((i, arg3, arg4) -> new SmithingScreenHandler(i, arg3, ScreenHandlerContext.create(arg2, arg32)), SCREEN_TITLE);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        if (arg2.isClient) {
            return ActionResult.SUCCESS;
        }
        arg4.openHandledScreen(arg.createScreenHandlerFactory(arg2, arg3));
        arg4.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE);
        return ActionResult.SUCCESS;
    }
}

