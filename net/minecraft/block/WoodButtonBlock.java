/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class WoodButtonBlock
extends AbstractButtonBlock {
    protected WoodButtonBlock(AbstractBlock.Settings arg) {
        super(true, arg);
    }

    @Override
    protected SoundEvent getClickSound(boolean bl) {
        return bl ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
    }
}

