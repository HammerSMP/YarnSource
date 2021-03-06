/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.IronGolemLookGoal;
import net.minecraft.entity.ai.goal.IronGolemWanderAroundGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackIronGolemTargetGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundPointOfInterestGoal;
import net.minecraft.entity.ai.goal.WanderNearTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.IntRange;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class IronGolemEntity
extends GolemEntity
implements Angerable {
    protected static final TrackedData<Byte> IRON_GOLEM_FLAGS = DataTracker.registerData(IronGolemEntity.class, TrackedDataHandlerRegistry.BYTE);
    private int attackTicksLeft;
    private int lookingAtVillagerTicksLeft;
    private static final IntRange field_25365 = Durations.betweenSeconds(20, 39);
    private int field_25366;
    private UUID field_25367;

    public IronGolemEntity(EntityType<? extends IronGolemEntity> arg, World arg2) {
        super((EntityType<? extends GolemEntity>)arg, arg2);
        this.stepHeight = 1.0f;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.9, 32.0f));
        this.goalSelector.add(2, new WanderAroundPointOfInterestGoal((PathAwareEntity)this, 0.6, false));
        this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new IronGolemLookGoal(this));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new FollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new FollowTargetGoal<MobEntity>(this, MobEntity.class, 5, false, false, arg -> arg instanceof Monster && !(arg instanceof CreeperEntity)));
        this.targetSelector.add(4, new UniversalAngerGoal<IronGolemEntity>(this, false));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IRON_GOLEM_FLAGS, (byte)0);
    }

    public static DefaultAttributeContainer.Builder createIronGolemAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0);
    }

    @Override
    protected int getNextAirUnderwater(int air) {
        return air;
    }

    @Override
    protected void pushAway(Entity entity) {
        if (entity instanceof Monster && !(entity instanceof CreeperEntity) && this.getRandom().nextInt(20) == 0) {
            this.setTarget((LivingEntity)entity);
        }
        super.pushAway(entity);
    }

    @Override
    public void tickMovement() {
        int k;
        int j;
        int i;
        BlockState lv;
        super.tickMovement();
        if (this.attackTicksLeft > 0) {
            --this.attackTicksLeft;
        }
        if (this.lookingAtVillagerTicksLeft > 0) {
            --this.lookingAtVillagerTicksLeft;
        }
        if (IronGolemEntity.squaredHorizontalLength(this.getVelocity()) > 2.500000277905201E-7 && this.random.nextInt(5) == 0 && !(lv = this.world.getBlockState(new BlockPos(i = MathHelper.floor(this.getX()), j = MathHelper.floor(this.getY() - (double)0.2f), k = MathHelper.floor(this.getZ())))).isAir()) {
            this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, lv), this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(), this.getY() + 0.1, this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.getWidth(), 4.0 * ((double)this.random.nextFloat() - 0.5), 0.5, ((double)this.random.nextFloat() - 0.5) * 4.0);
        }
        if (!this.world.isClient) {
            this.tickAngerLogic((ServerWorld)this.world, true);
        }
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        if (this.isPlayerCreated() && type == EntityType.PLAYER) {
            return false;
        }
        if (type == EntityType.CREEPER) {
            return false;
        }
        return super.canTarget(type);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("PlayerCreated", this.isPlayerCreated());
        this.angerToTag(tag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setPlayerCreated(tag.getBoolean("PlayerCreated"));
        this.angerFromTag((ServerWorld)this.world, tag);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(field_25365.choose(this.random));
    }

    @Override
    public void setAngerTime(int ticks) {
        this.field_25366 = ticks;
    }

    @Override
    public int getAngerTime() {
        return this.field_25366;
    }

    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        this.field_25367 = uuid;
    }

    @Override
    public UUID getAngryAt() {
        return this.field_25367;
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    public boolean tryAttack(Entity target) {
        this.attackTicksLeft = 10;
        this.world.sendEntityStatus(this, (byte)4);
        float f = this.getAttackDamage();
        float g = (int)f > 0 ? f / 2.0f + (float)this.random.nextInt((int)f) : f;
        boolean bl = target.damage(DamageSource.mob(this), g);
        if (bl) {
            target.setVelocity(target.getVelocity().add(0.0, 0.4f, 0.0));
            this.dealDamage(this, target);
        }
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        return bl;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        Crack lv = this.getCrack();
        boolean bl = super.damage(source, amount);
        if (bl && this.getCrack() != lv) {
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 1.0f, 1.0f);
        }
        return bl;
    }

    public Crack getCrack() {
        return Crack.from(this.getHealth() / this.getMaxHealth());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 4) {
            this.attackTicksLeft = 10;
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
        } else if (status == 11) {
            this.lookingAtVillagerTicksLeft = 400;
        } else if (status == 34) {
            this.lookingAtVillagerTicksLeft = 0;
        } else {
            super.handleStatus(status);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public int getAttackTicksLeft() {
        return this.attackTicksLeft;
    }

    public void setLookingAtVillager(boolean lookingAtVillager) {
        if (lookingAtVillager) {
            this.lookingAtVillagerTicksLeft = 400;
            this.world.sendEntityStatus(this, (byte)11);
        } else {
            this.lookingAtVillagerTicksLeft = 0;
            this.world.sendEntityStatus(this, (byte)34);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack lv = player.getStackInHand(hand);
        Item lv2 = lv.getItem();
        if (lv2 != Items.IRON_INGOT) {
            return ActionResult.PASS;
        }
        float f = this.getHealth();
        this.heal(25.0f);
        if (this.getHealth() == f) {
            return ActionResult.PASS;
        }
        float g = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
        if (!player.abilities.creativeMode) {
            lv.decrement(1);
        }
        return ActionResult.success(this.world.isClient);
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_IRON_GOLEM_STEP, 1.0f, 1.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public int getLookingAtVillagerTicks() {
        return this.lookingAtVillagerTicksLeft;
    }

    public boolean isPlayerCreated() {
        return (this.dataTracker.get(IRON_GOLEM_FLAGS) & 1) != 0;
    }

    public void setPlayerCreated(boolean playerCreated) {
        byte b = this.dataTracker.get(IRON_GOLEM_FLAGS);
        if (playerCreated) {
            this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b | 1));
        } else {
            this.dataTracker.set(IRON_GOLEM_FLAGS, (byte)(b & 0xFFFFFFFE));
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
    }

    @Override
    public boolean canSpawn(WorldView world) {
        BlockPos lv = this.getBlockPos();
        BlockPos lv2 = lv.down();
        BlockState lv3 = world.getBlockState(lv2);
        if (lv3.hasSolidTopSurface(world, lv2, this)) {
            for (int i = 1; i < 3; ++i) {
                BlockState lv5;
                BlockPos lv4 = lv.up(i);
                if (SpawnHelper.isClearForSpawn(world, lv4, lv5 = world.getBlockState(lv4), lv5.getFluidState(), EntityType.IRON_GOLEM)) continue;
                return false;
            }
            return SpawnHelper.isClearForSpawn(world, lv, world.getBlockState(lv), Fluids.EMPTY.getDefaultState(), EntityType.IRON_GOLEM) && world.intersectsEntities(this);
        }
        return false;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.875f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }

    public static enum Crack {
        NONE(1.0f),
        LOW(0.75f),
        MEDIUM(0.5f),
        HIGH(0.25f);

        private static final List<Crack> VALUES;
        private final float maxHealthFraction;

        private Crack(float maxHealthFraction) {
            this.maxHealthFraction = maxHealthFraction;
        }

        public static Crack from(float healthFraction) {
            for (Crack lv : VALUES) {
                if (!(healthFraction < lv.maxHealthFraction)) continue;
                return lv;
            }
            return NONE;
        }

        static {
            VALUES = (List)Stream.of(Crack.values()).sorted(Comparator.comparingDouble(arg -> arg.maxHealthFraction)).collect(ImmutableList.toImmutableList());
        }
    }
}

