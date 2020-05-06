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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class ZombifiedPiglinEntity
extends ZombieEntity {
    private static final UUID ATTACKING_SPEED_BOOST_UUID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_UUID, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.ADDITION);
    private int anger;
    private int angrySoundDelay;
    private UUID angerTarget;

    public ZombifiedPiglinEntity(EntityType<? extends ZombifiedPiglinEntity> arg, World arg2) {
        super((EntityType<? extends ZombieEntity>)arg, arg2);
        this.setPathfindingPenalty(PathNodeType.LAVA, 8.0f);
    }

    @Override
    public void setAttacker(@Nullable LivingEntity arg) {
        super.setAttacker(arg);
        if (arg != null) {
            this.angerTarget = arg.getUuid();
        }
    }

    @Override
    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, new AvoidZombiesGoal(this));
        this.targetSelector.add(2, new FollowPlayerIfAngryGoal(this));
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
        LivingEntity lv2 = this.getAttacker();
        if (this.isAngry()) {
            LivingEntity lv3;
            if (!this.isBaby() && !lv.hasModifier(ATTACKING_SPEED_BOOST)) {
                lv.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            }
            --this.anger;
            LivingEntity livingEntity = lv3 = lv2 != null ? lv2 : this.getTarget();
            if (!this.isAngry() && lv3 != null) {
                if (!this.canSee(lv3)) {
                    this.setAttacker(null);
                    this.setTarget(null);
                } else {
                    this.anger = this.getNewAngerDuration();
                }
            }
        } else if (lv.hasModifier(ATTACKING_SPEED_BOOST)) {
            lv.removeModifier(ATTACKING_SPEED_BOOST);
        }
        if (this.angrySoundDelay > 0 && --this.angrySoundDelay == 0) {
            this.playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0f, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * 1.8f);
        }
        if (this.isAngry() && this.angerTarget != null && lv2 == null) {
            PlayerEntity lv4 = this.world.getPlayerByUuid(this.angerTarget);
            this.setAttacker(lv4);
            this.attackingPlayer = lv4;
            this.playerHitTimer = this.getLastAttackedTime();
        }
        super.mobTick();
    }

    public static boolean canSpawn(EntityType<ZombifiedPiglinEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        return arg2.getDifficulty() != Difficulty.PEACEFUL && arg2.getBlockState(arg4.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
    }

    @Override
    public boolean canSpawn(WorldView arg) {
        return arg.intersectsEntities(this) && !arg.containsFluid(this.getBoundingBox());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putShort("Anger", (short)this.anger);
        if (this.angerTarget != null) {
            arg.putUuidNew("HurtBy", this.angerTarget);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.anger = arg.getShort("Anger");
        if (arg.containsUuidNew("HurtBy")) {
            this.angerTarget = arg.getUuidNew("HurtBy");
            PlayerEntity lv = this.world.getPlayerByUuid(this.angerTarget);
            this.setAttacker(lv);
            if (lv != null) {
                this.attackingPlayer = lv;
                this.playerHitTimer = this.getLastAttackedTime();
            }
        }
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        Entity lv = arg.getAttacker();
        if (lv instanceof PlayerEntity && !((PlayerEntity)lv).isCreative() && this.canSee(lv)) {
            this.getAngryAt((LivingEntity)lv);
        }
        return super.damage(arg, f);
    }

    private boolean getAngryAt(LivingEntity arg) {
        this.anger = this.getNewAngerDuration();
        this.angrySoundDelay = this.random.nextInt(40);
        this.setAttacker(arg);
        return true;
    }

    private int getNewAngerDuration() {
        return 400 + this.random.nextInt(400);
    }

    private boolean isAngry() {
        return this.anger > 0;
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
    public boolean isAngryAt(PlayerEntity arg) {
        return this.isAngry();
    }

    static class FollowPlayerIfAngryGoal
    extends FollowTargetGoal<PlayerEntity> {
        public FollowPlayerIfAngryGoal(ZombifiedPiglinEntity arg) {
            super((MobEntity)arg, PlayerEntity.class, true);
        }

        @Override
        public boolean canStart() {
            return ((ZombifiedPiglinEntity)this.mob).isAngry() && super.canStart();
        }
    }

    static class AvoidZombiesGoal
    extends RevengeGoal {
        public AvoidZombiesGoal(ZombifiedPiglinEntity arg) {
            super(arg, new Class[0]);
            this.setGroupRevenge(ZombieEntity.class);
        }

        @Override
        protected void setMobEntityTarget(MobEntity arg, LivingEntity arg2) {
            if (arg instanceof ZombifiedPiglinEntity && this.mob.canSee(arg2) && ((ZombifiedPiglinEntity)arg).getAngryAt(arg2)) {
                arg.setTarget(arg2);
            }
        }
    }
}

