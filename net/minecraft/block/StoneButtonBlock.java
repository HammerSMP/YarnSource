/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class StoneButtonBlock
extends AbstractButtonBlock {
    protected StoneButtonBlock(AbstractBlock.Settings arg) {
        super(false, arg);
    }

    @Override
    protected SoundEvent getClickSound(boolean bl) {
        return bl ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    }
}

