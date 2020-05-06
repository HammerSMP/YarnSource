/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block;

import java.util.List;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class PressurePlateBlock
extends AbstractPressurePlateBlock {
    public static final BooleanProperty POWERED = Properties.POWERED;
    private final ActivationRule type;

    protected PressurePlateBlock(ActivationRule arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, false));
        this.type = arg;
    }

    @Override
    protected int getRedstoneOutput(BlockState arg) {
        return arg.get(POWERED) != false ? 15 : 0;
    }

    @Override
    protected BlockState setRedstoneOutput(BlockState arg, int i) {
        return (BlockState)arg.with(POWERED, i > 0);
    }

    @Override
    protected void playPressSound(IWorld arg, BlockPos arg2) {
        if (this.material == Material.WOOD || this.material == Material.NETHER_WOOD) {
            arg.playSound(null, arg2, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.8f);
        } else {
            arg.playSound(null, arg2, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3f, 0.6f);
        }
    }

    @Override
    protected void playDepressSound(IWorld arg, BlockPos arg2) {
        if (this.material == Material.WOOD || this.material == Material.NETHER_WOOD) {
            arg.playSound(null, arg2, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.7f);
        } else {
            arg.playSound(null, arg2, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3f, 0.5f);
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected int getRedstoneOutput(World arg, BlockPos arg2) {
        void list3;
        Box lv = BOX.offset(arg2);
        switch (this.type) {
            case EVERYTHING: {
                List<Entity> list = arg.getEntities(null, lv);
                break;
            }
            case MOBS: {
                List<LivingEntity> list2 = arg.getNonSpectatingEntities(LivingEntity.class, lv);
                break;
            }
            default: {
                return 0;
            }
        }
        if (!list3.isEmpty()) {
            for (Entity lv2 : list3) {
                if (lv2.canAvoidTraps()) continue;
                return 15;
            }
        }
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(POWERED);
    }

    public static enum ActivationRule {
        EVERYTHING,
        MOBS;

    }
}

