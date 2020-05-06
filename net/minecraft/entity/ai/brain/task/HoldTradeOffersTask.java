/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;

public class HoldTradeOffersTask
extends Task<VillagerEntity> {
    @Nullable
    private ItemStack customerHeldStack;
    private final List<ItemStack> offers = Lists.newArrayList();
    private int offerShownTicks;
    private int offerIndex;
    private int ticksLeft;

    public HoldTradeOffersTask(int i, int j) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), i, j);
    }

    @Override
    public boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        Brain<VillagerEntity> lv = arg2.getBrain();
        if (!lv.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent()) {
            return false;
        }
        LivingEntity lv2 = lv.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        return lv2.getType() == EntityType.PLAYER && arg2.isAlive() && lv2.isAlive() && !arg2.isBaby() && arg2.squaredDistanceTo(lv2) <= 17.0;
    }

    @Override
    public boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return this.shouldRun(arg, arg2) && this.ticksLeft > 0 && arg2.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    @Override
    public void run(ServerWorld arg, VillagerEntity arg2, long l) {
        super.run(arg, arg2, l);
        this.findPotentialCuatomer(arg2);
        this.offerShownTicks = 0;
        this.offerIndex = 0;
        this.ticksLeft = 40;
    }

    @Override
    public void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        LivingEntity lv = this.findPotentialCuatomer(arg2);
        this.setupOffers(lv, arg2);
        if (!this.offers.isEmpty()) {
            this.refreshShownOffer(arg2);
        } else {
            arg2.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.ticksLeft = Math.min(this.ticksLeft, 40);
        }
        --this.ticksLeft;
    }

    @Override
    public void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        super.finishRunning(arg, arg2, l);
        arg2.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
        arg2.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.customerHeldStack = null;
    }

    private void setupOffers(LivingEntity arg, VillagerEntity arg2) {
        boolean bl = false;
        ItemStack lv = arg.getMainHandStack();
        if (this.customerHeldStack == null || !ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, lv)) {
            this.customerHeldStack = lv;
            bl = true;
            this.offers.clear();
        }
        if (bl && !this.customerHeldStack.isEmpty()) {
            this.loadPossibleOffers(arg2);
            if (!this.offers.isEmpty()) {
                this.ticksLeft = 900;
                this.holdOffer(arg2);
            }
        }
    }

    private void holdOffer(VillagerEntity arg) {
        arg.equipStack(EquipmentSlot.MAINHAND, this.offers.get(0));
    }

    private void loadPossibleOffers(VillagerEntity arg) {
        for (TradeOffer lv : arg.getOffers()) {
            if (lv.isDisabled() || !this.isPossible(lv)) continue;
            this.offers.add(lv.getMutableSellItem());
        }
    }

    private boolean isPossible(TradeOffer arg) {
        return ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, arg.getAdjustedFirstBuyItem()) || ItemStack.areItemsEqualIgnoreDamage(this.customerHeldStack, arg.getSecondBuyItem());
    }

    private LivingEntity findPotentialCuatomer(VillagerEntity arg) {
        Brain<VillagerEntity> lv = arg.getBrain();
        LivingEntity lv2 = lv.getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        lv.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(lv2, true));
        return lv2;
    }

    private void refreshShownOffer(VillagerEntity arg) {
        if (this.offers.size() >= 2 && ++this.offerShownTicks >= 40) {
            ++this.offerIndex;
            this.offerShownTicks = 0;
            if (this.offerIndex > this.offers.size() - 1) {
                this.offerIndex = 0;
            }
            arg.equipStack(EquipmentSlot.MAINHAND, this.offers.get(this.offerIndex));
        }
    }

    @Override
    public /* synthetic */ boolean shouldKeepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        return this.shouldKeepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    public /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    public /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    public /* synthetic */ void run(ServerWorld arg, LivingEntity arg2, long l) {
        this.run(arg, (VillagerEntity)arg2, l);
    }
}

