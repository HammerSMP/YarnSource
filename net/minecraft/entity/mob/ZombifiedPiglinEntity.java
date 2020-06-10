/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.mob;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.class_5398;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.Durations;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.IntRange;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ZombifiedPiglinEntity
extends ZombieEntity
implements Angerable {
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.ADDITION);
    private static final IntRange field_25382 = Durations.betweenSeconds(0, 2);
    private int angrySoundDelay;
    private static final IntRange field_25379 = Durations.betweenSeconds(20, 39);
    private int field_25380;
    private UUID field_25381;
    private static final IntRange field_25609 = Durations.betweenSeconds(4, 6);
    private int field_25608;

    public ZombifiedPiglinEntity(EntityType<? extends ZombifiedPiglinEntity> arg, World arg2) {
        super((EntityType<? extends ZombieEntity>)arg, arg2);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0f);
    }

    @Override
    public void setAngryAt(@Nullable UUID uUID) {
        this.field_25381 = uUID;
    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new RevengeGoal(this, new Class[0]).setGroupRevenge(new Class[0]));
        this.targetSelector.add(2, new FollowTargetGoal<PlayerEntity>(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, new class_5398<ZombifiedPiglinEntity>(this, true));
    }

    public static DefaultAttributeContainer.Builder createZombifiedPiglinAttributes() {
        return ZombieEntity.createZombieAttributes().add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
    }

    @Override
    protected boolean canConvertInWater() {
        return false;
    }

    @Override
    protected void mobTick() {
        EntityAttributeInstance lv = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (this.hasAngerTime()) {
            if (!this.isBaby() && !lv.hasModifier(ATTACKING_SPEED_BOOST)) {
                lv.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
            if (this.angrySoundDelay == 0) {
                this.method_29533();
                this.angrySoundDelay = field_25382.choose(this.random);
            } else {
                --this.angrySoundDelay;
            }
        } else if (lv.hasModifier(ATTACKING_SPEED_BOOST)) {
            lv.removeModifier(ATTACKING_SPEED_BOOST);
        }
        this.tickAngerLogic((ServerWorld)this.world, true);
        if (this.getTarget() != null) {
            this.method_29941();
        }
        super.mobTick();
    }

    private void method_29941() {
        if (this.field_25608 > 0) {
            --this.field_25608;
            return;
        }
        if (this.getVisibilityCache().canSee(this.getTarget())) {
            this.method_29942();
        }
        this.field_25608 = field_25609.choose(this.random);
    }

    private void method_29942() {
        double d = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
        Box lv = Box.method_29968(this.getPos()).expand(d, 10.0, d);
        this.world.getEntitiesIncludingUngeneratedChunks(ZombifiedPiglinEntity.class, lv).stream().filter(arg -> arg != this).filter(arg -> arg.getTarget() == null).filter(arg -> !arg.isTeammate(this.getTarget())).forEach(arg -> arg.setTarget(this.getTarget()));
    }

    private void method_29533() {
        this.playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0f, this.getSoundPitch() * 1.8f);
    }

    @Override
    public void setTarget(@Nullable LivingEntity arg) {
        if (this.getTarget() == null && arg != null) {
            this.method_29533();
            this.angrySoundDelay = field_25382.choose(this.random);
            this.field_25608 = field_25609.choose(this.random);
        }
        super.setTarget(arg);
    }

    @Override
    protected boolean shouldAlwaysDropXp() {
        return this.getTarget() != null;
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(field_25379.choose(this.random));
    }

    public static boolean canSpawn(EntityType<ZombifiedPiglinEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return arg2.getDifficulty() != Difficulty.PEACEFUL && arg2.getBlockState(arg4.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
    }

    @Override
    public boolean canSpawn(WorldView arg) {
        return arg.intersectsEntities(this) && !arg.containsFluid(this.getBoundingBox());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        this.angerToTag(arg);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.angerFromTag((ServerWorld)this.world, arg);
    }

    @Override
    public void setAngerTime(int i) {
        this.field_25380 = i;
    }

    @Override
    public int getAngerTime() {
        return this.field_25380;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        return super.damage(arg, f);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
    }

    @Override
    protected void initEquipment(LocalDifficulty arg) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void initAttributes() {
        this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.0);
    }

    @Override
    public UUID getAngryAt() {
        return this.field_25381;
    }

    @Override
    public boolean isAngryAt(PlayerEntity arg) {
        return this.shouldAngerAt(arg);
    }
}

