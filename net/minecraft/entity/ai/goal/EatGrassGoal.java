/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EatGrassGoal
extends Goal {
    private static final Predicate<BlockState> GRASS_PREDICATE = BlockStatePredicate.forBlock(Blocks.GRASS);
    private final MobEntity mob;
    private final World world;
    private int timer;

    public EatGrassGoal(MobEntity arg) {
        this.mob = arg;
        this.world = arg.world;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
    }

    @Override
    public boolean canStart() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
            return false;
        }
        BlockPos lv = this.mob.getBlockPos();
        if (GRASS_PREDICATE.test(this.world.getBlockState(lv))) {
            return true;
        }
        return this.world.getBlockState(lv.down()).isOf(Blocks.GRASS_BLOCK);
    }

    @Override
    public void start() {
        this.timer = 40;
        this.world.sendEntityStatus(this.mob, (byte)10);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.timer = 0;
    }

    @Override
    public boolean shouldContinue() {
        return this.timer > 0;
    }

    public int getTimer() {
        return this.timer;
    }

    @Override
    public void tick() {
        this.timer = Math.max(0, this.timer - 1);
        if (this.timer != 4) {
            return;
        }
        BlockPos lv = this.mob.getBlockPos();
        if (GRASS_PREDICATE.test(this.world.getBlockState(lv))) {
            if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                this.world.breakBlock(lv, false);
            }
            this.mob.onEatingGrass();
        } else {
            BlockPos lv2 = lv.down();
            if (this.world.getBlockState(lv2).isOf(Blocks.GRASS_BLOCK)) {
                if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                    this.world.syncWorldEvent(2001, lv2, Block.getRawIdFromState(Blocks.GRASS_BLOCK.getDefaultState()));
                    this.world.setBlockState(lv2, Blocks.DIRT.getDefaultState(), 2);
                }
                this.mob.onEatingGrass();
            }
        }
    }
}

