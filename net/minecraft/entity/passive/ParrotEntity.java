/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_5425;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Flutterer;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FlyOntoTreeGoal;
import net.minecraft.entity.ai.goal.FollowMobGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SitOnOwnerShoulderGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableShoulderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class ParrotEntity
extends TameableShoulderEntity
implements Flutterer {
    private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(ParrotEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final Predicate<MobEntity> CAN_IMITATE = new Predicate<MobEntity>(){

        @Override
        public boolean test(@Nullable MobEntity arg) {
            return arg != null && MOB_SOUNDS.containsKey(arg.getType());
        }

        @Override
        public /* synthetic */ boolean test(@Nullable Object object) {
            return this.test((MobEntity)object);
        }
    };
    private static final Item COOKIE = Items.COOKIE;
    private static final Set<Item> TAMING_INGREDIENTS = Sets.newHashSet((Object[])new Item[]{Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    private static final Map<EntityType<?>, SoundEvent> MOB_SOUNDS = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(EntityType.BLAZE, SoundEvents.ENTITY_PARROT_IMITATE_BLAZE);
        hashMap.put(EntityType.CAVE_SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
        hashMap.put(EntityType.CREEPER, SoundEvents.ENTITY_PARROT_IMITATE_CREEPER);
        hashMap.put(EntityType.DROWNED, SoundEvents.ENTITY_PARROT_IMITATE_DROWNED);
        hashMap.put(EntityType.ELDER_GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
        hashMap.put(EntityType.ENDER_DRAGON, SoundEvents.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
        hashMap.put(EntityType.ENDERMITE, SoundEvents.ENTITY_PARROT_IMITATE_ENDERMITE);
        hashMap.put(EntityType.EVOKER, SoundEvents.ENTITY_PARROT_IMITATE_EVOKER);
        hashMap.put(EntityType.GHAST, SoundEvents.ENTITY_PARROT_IMITATE_GHAST);
        hashMap.put(EntityType.GUARDIAN, SoundEvents.ENTITY_PARROT_IMITATE_GUARDIAN);
        hashMap.put(EntityType.HOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_HOGLIN);
        hashMap.put(EntityType.HUSK, SoundEvents.ENTITY_PARROT_IMITATE_HUSK);
        hashMap.put(EntityType.ILLUSIONER, SoundEvents.ENTITY_PARROT_IMITATE_ILLUSIONER);
        hashMap.put(EntityType.MAGMA_CUBE, SoundEvents.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
        hashMap.put(EntityType.PHANTOM, SoundEvents.ENTITY_PARROT_IMITATE_PHANTOM);
        hashMap.put(EntityType.PIGLIN, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN);
        hashMap.put(EntityType.PIGLIN_BRUTE, SoundEvents.ENTITY_PARROT_IMITATE_PIGLIN_BRUTE);
        hashMap.put(EntityType.PILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_PILLAGER);
        hashMap.put(EntityType.RAVAGER, SoundEvents.ENTITY_PARROT_IMITATE_RAVAGER);
        hashMap.put(EntityType.SHULKER, SoundEvents.ENTITY_PARROT_IMITATE_SHULKER);
        hashMap.put(EntityType.SILVERFISH, SoundEvents.ENTITY_PARROT_IMITATE_SILVERFISH);
        hashMap.put(EntityType.SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_SKELETON);
        hashMap.put(EntityType.SLIME, SoundEvents.ENTITY_PARROT_IMITATE_SLIME);
        hashMap.put(EntityType.SPIDER, SoundEvents.ENTITY_PARROT_IMITATE_SPIDER);
        hashMap.put(EntityType.STRAY, SoundEvents.ENTITY_PARROT_IMITATE_STRAY);
        hashMap.put(EntityType.VEX, SoundEvents.ENTITY_PARROT_IMITATE_VEX);
        hashMap.put(EntityType.VINDICATOR, SoundEvents.ENTITY_PARROT_IMITATE_VINDICATOR);
        hashMap.put(EntityType.WITCH, SoundEvents.ENTITY_PARROT_IMITATE_WITCH);
        hashMap.put(EntityType.WITHER, SoundEvents.ENTITY_PARROT_IMITATE_WITHER);
        hashMap.put(EntityType.WITHER_SKELETON, SoundEvents.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
        hashMap.put(EntityType.ZOGLIN, SoundEvents.ENTITY_PARROT_IMITATE_ZOGLIN);
        hashMap.put(EntityType.ZOMBIE, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE);
        hashMap.put(EntityType.ZOMBIE_VILLAGER, SoundEvents.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
    });
    public float flapProgress;
    public float maxWingDeviation;
    public float prevMaxWingDeviation;
    public float prevFlapProgress;
    private float flapSpeed = 1.0f;
    private boolean songPlaying;
    private BlockPos songSource;

    public ParrotEntity(EntityType<? extends ParrotEntity> arg, World arg2) {
        super((EntityType<? extends TameableShoulderEntity>)arg, arg2);
        this.moveControl = new FlightMoveControl(this, 10, false);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0f);
        this.setPathfindingPenalty(PathNodeType.COCOA, -1.0f);
    }

    @Override
    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        this.setVariant(this.random.nextInt(5));
        if (arg4 == null) {
            arg4 = new PassiveEntity.PassiveData(false);
        }
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(2, new SitGoal(this));
        this.goalSelector.add(2, new FollowOwnerGoal(this, 1.0, 5.0f, 1.0f, true));
        this.goalSelector.add(2, new FlyOntoTreeGoal(this, 1.0));
        this.goalSelector.add(3, new SitOnOwnerShoulderGoal(this));
        this.goalSelector.add(3, new FollowMobGoal(this, 1.0, 3.0f, 7.0f));
    }

    public static DefaultAttributeContainer.Builder createParrotAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 6.0).add(EntityAttributes.GENERIC_FLYING_SPEED, 0.4f).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2f);
    }

    @Override
    protected EntityNavigation createNavigation(World arg) {
        BirdNavigation lv = new BirdNavigation(this, arg);
        lv.setCanPathThroughDoors(false);
        lv.setCanSwim(true);
        lv.setCanEnterOpenDoors(true);
        return lv;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.6f;
    }

    @Override
    public void tickMovement() {
        if (this.songSource == null || !this.songSource.isWithinDistance(this.getPos(), 3.46) || !this.world.getBlockState(this.songSource).isOf(Blocks.JUKEBOX)) {
            this.songPlaying = false;
            this.songSource = null;
        }
        if (this.world.random.nextInt(400) == 0) {
            ParrotEntity.imitateNearbyMob(this.world, this);
        }
        super.tickMovement();
        this.flapWings();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setNearbySongPlaying(BlockPos arg, boolean bl) {
        this.songSource = arg;
        this.songPlaying = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean getSongPlaying() {
        return this.songPlaying;
    }

    private void flapWings() {
        this.prevFlapProgress = this.flapProgress;
        this.prevMaxWingDeviation = this.maxWingDeviation;
        this.maxWingDeviation = (float)((double)this.maxWingDeviation + (double)(this.onGround || this.hasVehicle() ? -1 : 4) * 0.3);
        this.maxWingDeviation = MathHelper.clamp(this.maxWingDeviation, 0.0f, 1.0f);
        if (!this.onGround && this.flapSpeed < 1.0f) {
            this.flapSpeed = 1.0f;
        }
        this.flapSpeed = (float)((double)this.flapSpeed * 0.9);
        Vec3d lv = this.getVelocity();
        if (!this.onGround && lv.y < 0.0) {
            this.setVelocity(lv.multiply(1.0, 0.6, 1.0));
        }
        this.flapProgress += this.flapSpeed * 2.0f;
    }

    public static boolean imitateNearbyMob(World arg, Entity arg2) {
        MobEntity lv;
        if (!arg2.isAlive() || arg2.isSilent() || arg.random.nextInt(2) != 0) {
            return false;
        }
        List<MobEntity> list = arg.getEntities(MobEntity.class, arg2.getBoundingBox().expand(20.0), CAN_IMITATE);
        if (!list.isEmpty() && !(lv = list.get(arg.random.nextInt(list.size()))).isSilent()) {
            SoundEvent lv2 = ParrotEntity.getSound(lv.getType());
            arg.playSound(null, arg2.getX(), arg2.getY(), arg2.getZ(), lv2, arg2.getSoundCategory(), 0.7f, ParrotEntity.getSoundPitch(arg.random));
            return true;
        }
        return false;
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (!this.isTamed() && TAMING_INGREDIENTS.contains(lv.getItem())) {
            if (!arg.abilities.creativeMode) {
                lv.decrement(1);
            }
            if (!this.isSilent()) {
                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PARROT_EAT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
            }
            if (!this.world.isClient) {
                if (this.random.nextInt(10) == 0) {
                    this.setOwner(arg);
                    this.world.sendEntityStatus(this, (byte)7);
                } else {
                    this.world.sendEntityStatus(this, (byte)6);
                }
            }
            return ActionResult.success(this.world.isClient);
        }
        if (lv.getItem() == COOKIE) {
            if (!arg.abilities.creativeMode) {
                lv.decrement(1);
            }
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 900));
            if (arg.isCreative() || !this.isInvulnerable()) {
                this.damage(DamageSource.player(arg), Float.MAX_VALUE);
            }
            return ActionResult.success(this.world.isClient);
        }
        if (!this.isInAir() && this.isTamed() && this.isOwner(arg)) {
            if (!this.world.isClient) {
                this.setSitting(!this.isSitting());
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(arg, arg2);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return false;
    }

    public static boolean canSpawn(EntityType<ParrotEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        BlockState lv = arg2.getBlockState(arg4.down());
        return (lv.isIn(BlockTags.LEAVES) || lv.isOf(Blocks.GRASS_BLOCK) || lv.isIn(BlockTags.LOGS) || lv.isOf(Blocks.AIR)) && arg2.getBaseLightLevel(arg4, 0) > 8;
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    protected void fall(double d, boolean bl, BlockState arg, BlockPos arg2) {
    }

    @Override
    public boolean canBreedWith(AnimalEntity arg) {
        return false;
    }

    @Override
    @Nullable
    public PassiveEntity createChild(ServerWorld arg, PassiveEntity arg2) {
        return null;
    }

    @Override
    public boolean tryAttack(Entity arg) {
        return arg.damage(DamageSource.mob(this), 3.0f);
    }

    @Override
    @Nullable
    public SoundEvent getAmbientSound() {
        return ParrotEntity.getRandomSound(this.world, this.world.random);
    }

    public static SoundEvent getRandomSound(World arg, Random random) {
        if (arg.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(1000) == 0) {
            ArrayList list = Lists.newArrayList(MOB_SOUNDS.keySet());
            return ParrotEntity.getSound((EntityType)list.get(random.nextInt(list.size())));
        }
        return SoundEvents.ENTITY_PARROT_AMBIENT;
    }

    private static SoundEvent getSound(EntityType<?> arg) {
        return MOB_SOUNDS.getOrDefault(arg, SoundEvents.ENTITY_PARROT_AMBIENT);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_PARROT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PARROT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15f, 1.0f);
    }

    @Override
    protected float playFlySound(float f) {
        this.playSound(SoundEvents.ENTITY_PARROT_FLY, 0.15f, 1.0f);
        return f + this.maxWingDeviation / 2.0f;
    }

    @Override
    protected boolean hasWings() {
        return true;
    }

    @Override
    protected float getSoundPitch() {
        return ParrotEntity.getSoundPitch(this.random);
    }

    public static float getSoundPitch(Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected void pushAway(Entity arg) {
        if (arg instanceof PlayerEntity) {
            return;
        }
        super.pushAway(arg);
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        this.setSitting(false);
        return super.damage(arg, f);
    }

    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 4);
    }

    public void setVariant(int i) {
        this.dataTracker.set(ATTR_VARIANT, i);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTR_VARIANT, 0);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Variant", this.getVariant());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setVariant(arg.getInt("Variant"));
    }

    public boolean isInAir() {
        return !this.onGround;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_29919() {
        return new Vec3d(0.0, 0.5f * this.getStandingEyeHeight(), this.getWidth() * 0.4f);
    }
}

