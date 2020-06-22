/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.CrossbowAttackGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.BannerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class PillagerEntity
extends IllagerEntity
implements CrossbowUser {
    private static final TrackedData<Boolean> CHARGING = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private final SimpleInventory inventory = new SimpleInventory(5);

    public PillagerEntity(EntityType<? extends PillagerEntity> arg, World arg2) {
        super((EntityType<? extends IllagerEntity>)arg, arg2);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new RaiderEntity.PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(3, new CrossbowAttackGoal<PillagerEntity>(this, 1.0, 8.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 15.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 15.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>((MobEntity)this, PlayerEntity.class, true));
        this.targetSelector.add(3, new FollowTargetGoal<AbstractTraderEntity>((MobEntity)this, AbstractTraderEntity.class, false));
        this.targetSelector.add(3, new FollowTargetGoal<IronGolemEntity>((MobEntity)this, IronGolemEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CHARGING, false);
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem arg) {
        return arg == Items.CROSSBOW;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isCharging() {
        return this.dataTracker.get(CHARGING);
    }

    @Override
    public void setCharging(boolean bl) {
        this.dataTracker.set(CHARGING, bl);
    }

    @Override
    public void postShoot() {
        this.despawnCounter = 0;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        ListTag lv = new ListTag();
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack lv2 = this.inventory.getStack(i);
            if (lv2.isEmpty()) continue;
            lv.add(lv2.toTag(new CompoundTag()));
        }
        arg.put("Inventory", lv);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public IllagerEntity.State getState() {
        if (this.isCharging()) {
            return IllagerEntity.State.CROSSBOW_CHARGE;
        }
        if (this.isHolding(Items.CROSSBOW)) {
            return IllagerEntity.State.CROSSBOW_HOLD;
        }
        if (this.isAttacking()) {
            return IllagerEntity.State.ATTACKING;
        }
        return IllagerEntity.State.NEUTRAL;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        ListTag lv = arg.getList("Inventory", 10);
        for (int i = 0; i < lv.size(); ++i) {
            ItemStack lv2 = ItemStack.fromTag(lv.getCompound(i));
            if (lv2.isEmpty()) continue;
            this.inventory.addStack(lv2);
        }
        this.setCanPickUpLoot(true);
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        BlockState lv = arg2.getBlockState(arg.down());
        if (lv.isOf(Blocks.GRASS_BLOCK) || lv.isOf(Blocks.SAND)) {
            return 10.0f;
        }
        return 0.5f - arg2.getBrightness(arg);
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.initEquipment(arg2);
        this.updateEnchantments(arg2);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    protected void initEquipment(LocalDifficulty arg) {
        ItemStack lv = new ItemStack(Items.CROSSBOW);
        if (this.random.nextInt(300) == 0) {
            HashMap map = Maps.newHashMap();
            map.put(Enchantments.PIERCING, 1);
            EnchantmentHelper.set(map, lv);
        }
        this.equipStack(EquipmentSlot.MAINHAND, lv);
    }

    @Override
    public boolean isTeammate(Entity arg) {
        if (super.isTeammate(arg)) {
            return true;
        }
        if (arg instanceof LivingEntity && ((LivingEntity)arg).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && arg.getScoreboardTeam() == null;
        }
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PILLAGER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_PILLAGER_HURT;
    }

    @Override
    public void attack(LivingEntity arg, float f) {
        this.shoot(this, 1.6f);
    }

    @Override
    public void shoot(LivingEntity arg, ItemStack arg2, ProjectileEntity arg3, float f) {
        this.shoot(this, arg, arg3, f, 1.6f);
    }

    @Override
    protected void loot(ItemEntity arg) {
        ItemStack lv = arg.getStack();
        if (lv.getItem() instanceof BannerItem) {
            super.loot(arg);
        } else {
            Item lv2 = lv.getItem();
            if (this.method_7111(lv2)) {
                this.method_29499(arg);
                ItemStack lv3 = this.inventory.addStack(lv);
                if (lv3.isEmpty()) {
                    arg.remove();
                } else {
                    lv.setCount(lv3.getCount());
                }
            }
        }
    }

    private boolean method_7111(Item arg) {
        return this.hasActiveRaid() && arg == Items.WHITE_BANNER;
    }

    @Override
    public boolean equip(int i, ItemStack arg) {
        if (super.equip(i, arg)) {
            return true;
        }
        int j = i - 300;
        if (j >= 0 && j < this.inventory.size()) {
            this.inventory.setStack(j, arg);
            return true;
        }
        return false;
    }

    @Override
    public void addBonusForWave(int i, boolean bl) {
        boolean bl2;
        Raid lv = this.getRaid();
        boolean bl3 = bl2 = this.random.nextFloat() <= lv.getEnchantmentChance();
        if (bl2) {
            ItemStack lv2 = new ItemStack(Items.CROSSBOW);
            HashMap map = Maps.newHashMap();
            if (i > lv.getMaxWaves(Difficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            } else if (i > lv.getMaxWaves(Difficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 1);
            }
            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentHelper.set(map, lv2);
            this.equipStack(EquipmentSlot.MAINHAND, lv2);
        }
    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }
}

