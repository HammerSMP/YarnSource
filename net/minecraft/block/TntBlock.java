/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntBlock
extends Block {
    public static final BooleanProperty UNSTABLE = Properties.UNSTABLE;

    public TntBlock(AbstractBlock.Settings arg) {
        super(arg);
        this.setDefaultState((BlockState)this.getDefaultState().with(UNSTABLE, false));
    }

    @Override
    public void onBlockAdded(BlockState arg, World arg2, BlockPos arg3, BlockState arg4, boolean bl) {
        if (arg4.isOf(arg.getBlock())) {
            return;
        }
        if (arg2.isReceivingRedstonePower(arg3)) {
            TntBlock.primeTnt(arg2, arg3);
            arg2.removeBlock(arg3, false);
        }
    }

    @Override
    public void neighborUpdate(BlockState arg, World arg2, BlockPos arg3, Block arg4, BlockPos arg5, boolean bl) {
        if (arg2.isReceivingRedstonePower(arg3)) {
            TntBlock.primeTnt(arg2, arg3);
            arg2.removeBlock(arg3, false);
        }
    }

    @Override
    public void onBreak(World arg, BlockPos arg2, BlockState arg3, PlayerEntity arg4) {
        if (!arg.isClient() && !arg4.isCreative() && arg3.get(UNSTABLE).booleanValue()) {
            TntBlock.primeTnt(arg, arg2);
        }
        super.onBreak(arg, arg2, arg3, arg4);
    }

    @Override
    public void onDestroyedByExplosion(World arg, BlockPos arg2, Explosion arg3) {
        if (arg.isClient) {
            return;
        }
        TntEntity lv = new TntEntity(arg, (double)arg2.getX() + 0.5, arg2.getY(), (double)arg2.getZ() + 0.5, arg3.getCausingEntity());
        lv.setFuse((short)(arg.random.nextInt(lv.getFuseTimer() / 4) + lv.getFuseTimer() / 8));
        arg.spawnEntity(lv);
    }

    public static void primeTnt(World arg, BlockPos arg2) {
        TntBlock.primeTnt(arg, arg2, null);
    }

    private static void primeTnt(World arg, BlockPos arg2, @Nullable LivingEntity arg3) {
        if (arg.isClient) {
            return;
        }
        TntEntity lv = new TntEntity(arg, (double)arg2.getX() + 0.5, arg2.getY(), (double)arg2.getZ() + 0.5, arg3);
        arg.spawnEntity(lv);
        arg.playSound(null, lv.getX(), lv.getY(), lv.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public ActionResult onUse(BlockState arg, World arg22, BlockPos arg3, PlayerEntity arg4, Hand arg5, BlockHitResult arg6) {
        ItemStack lv = arg4.getStackInHand(arg5);
        Item lv2 = lv.getItem();
        if (lv2 == Items.FLINT_AND_STEEL || lv2 == Items.FIRE_CHARGE) {
            TntBlock.primeTnt(arg22, arg3, arg4);
            arg22.setBlockState(arg3, Blocks.AIR.getDefaultState(), 11);
            if (!arg4.isCreative()) {
                if (lv2 == Items.FLINT_AND_STEEL) {
                    lv.damage(1, arg4, arg2 -> arg2.sendToolBreakStatus(arg5));
                } else {
                    lv.decrement(1);
                }
            }
            return ActionResult.success(arg22.isClient);
        }
        return super.onUse(arg, arg22, arg3, arg4, arg5, arg6);
    }

    @Override
    public void onProjectileHit(World arg, BlockState arg2, BlockHitResult arg3, ProjectileEntity arg4) {
        if (!arg.isClient) {
            Entity lv = arg4.getOwner();
            if (arg4.isOnFire()) {
                BlockPos lv2 = arg3.getBlockPos();
                TntBlock.primeTnt(arg, lv2, lv instanceof LivingEntity ? (LivingEntity)lv : null);
                arg.removeBlock(lv2, false);
            }
        }
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion arg) {
        return false;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> arg) {
        arg.add(UNSTABLE);
    }
}

