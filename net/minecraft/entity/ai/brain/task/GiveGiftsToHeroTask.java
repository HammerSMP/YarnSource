/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;

public class GiveGiftsToHeroTask
extends Task<VillagerEntity> {
    private static final Map<VillagerProfession, Identifier> GIFTS = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(VillagerProfession.ARMORER, LootTables.HERO_OF_THE_VILLAGE_ARMORER_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.BUTCHER, LootTables.HERO_OF_THE_VILLAGE_BUTCHER_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.CARTOGRAPHER, LootTables.HERO_OF_THE_VILLAGE_CARTOGRAPHER_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.CLERIC, LootTables.HERO_OF_THE_VILLAGE_CLERIC_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.FARMER, LootTables.HERO_OF_THE_VILLAGE_FARMER_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.FISHERMAN, LootTables.HERO_OF_THE_VILLAGE_FISHERMAN_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.FLETCHER, LootTables.HERO_OF_THE_VILLAGE_FLETCHER_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.LEATHERWORKER, LootTables.HERO_OF_THE_VILLAGE_LEATHERWORKER_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.LIBRARIAN, LootTables.HERO_OF_THE_VILLAGE_LIBRARIAN_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.MASON, LootTables.HERO_OF_THE_VILLAGE_MASON_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.SHEPHERD, LootTables.HERO_OF_THE_VILLAGE_SHEPHERD_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.TOOLSMITH, LootTables.HERO_OF_THE_VILLAGE_TOOLSMITH_GIFT_GAMEPLAY);
        hashMap.put(VillagerProfession.WEAPONSMITH, LootTables.HERO_OF_THE_VILLAGE_WEAPONSMITH_GIFT_GAMEPLAY);
    });
    private int ticksLeft = 600;
    private boolean done;
    private long startTime;

    public GiveGiftsToHeroTask(int i) {
        super((Map<MemoryModuleType<?>, MemoryModuleState>)ImmutableMap.of(MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.INTERACTION_TARGET, (Object)((Object)MemoryModuleState.REGISTERED), MemoryModuleType.NEAREST_VISIBLE_PLAYER, (Object)((Object)MemoryModuleState.VALUE_PRESENT)), i);
    }

    @Override
    protected boolean shouldRun(ServerWorld arg, VillagerEntity arg2) {
        if (!this.isNearestPlayerHero(arg2)) {
            return false;
        }
        if (this.ticksLeft > 0) {
            --this.ticksLeft;
            return false;
        }
        return true;
    }

    @Override
    protected void run(ServerWorld arg, VillagerEntity arg2, long l) {
        this.done = false;
        this.startTime = l;
        PlayerEntity lv = this.getNearestPlayerIfHero(arg2).get();
        arg2.getBrain().remember(MemoryModuleType.INTERACTION_TARGET, lv);
        LookTargetUtil.lookAt(arg2, lv);
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        return this.isNearestPlayerHero(arg2) && !this.done;
    }

    @Override
    protected void keepRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        PlayerEntity lv = this.getNearestPlayerIfHero(arg2).get();
        LookTargetUtil.lookAt(arg2, lv);
        if (this.isCloseEnough(arg2, lv)) {
            if (l - this.startTime > 20L) {
                this.giveGifts(arg2, lv);
                this.done = true;
            }
        } else {
            LookTargetUtil.walkTowards((LivingEntity)arg2, lv, 0.5f, 5);
        }
    }

    @Override
    protected void finishRunning(ServerWorld arg, VillagerEntity arg2, long l) {
        this.ticksLeft = GiveGiftsToHeroTask.getNextGiftDelay(arg);
        arg2.getBrain().forget(MemoryModuleType.INTERACTION_TARGET);
        arg2.getBrain().forget(MemoryModuleType.WALK_TARGET);
        arg2.getBrain().forget(MemoryModuleType.LOOK_TARGET);
    }

    private void giveGifts(VillagerEntity arg, LivingEntity arg2) {
        List<ItemStack> list = this.getGifts(arg);
        for (ItemStack lv : list) {
            LookTargetUtil.give(arg, lv, arg2.getPos());
        }
    }

    private List<ItemStack> getGifts(VillagerEntity arg) {
        if (arg.isBaby()) {
            return ImmutableList.of((Object)new ItemStack(Items.POPPY));
        }
        VillagerProfession lv = arg.getVillagerData().getProfession();
        if (GIFTS.containsKey(lv)) {
            LootTable lv2 = arg.world.getServer().getLootManager().getTable(GIFTS.get(lv));
            LootContext.Builder lv3 = new LootContext.Builder((ServerWorld)arg.world).put(LootContextParameters.POSITION, arg.getBlockPos()).put(LootContextParameters.THIS_ENTITY, arg).setRandom(arg.getRandom());
            return lv2.getDrops(lv3.build(LootContextTypes.GIFT));
        }
        return ImmutableList.of((Object)new ItemStack(Items.WHEAT_SEEDS));
    }

    private boolean isNearestPlayerHero(VillagerEntity arg) {
        return this.getNearestPlayerIfHero(arg).isPresent();
    }

    private Optional<PlayerEntity> getNearestPlayerIfHero(VillagerEntity arg) {
        return arg.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::isHero);
    }

    private boolean isHero(PlayerEntity arg) {
        return arg.hasStatusEffect(StatusEffects.HERO_OF_THE_VILLAGE);
    }

    private boolean isCloseEnough(VillagerEntity arg, PlayerEntity arg2) {
        BlockPos lv = arg2.getBlockPos();
        BlockPos lv2 = arg.getBlockPos();
        return lv2.isWithinDistance(lv, 5.0);
    }

    private static int getNextGiftDelay(ServerWorld arg) {
        return 600 + arg.random.nextInt(6001);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.finishRunning(arg, (VillagerEntity)arg2, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld arg, LivingEntity arg2, long l) {
        this.keepRunning(arg, (VillagerEntity)arg2, l);
    }
}

