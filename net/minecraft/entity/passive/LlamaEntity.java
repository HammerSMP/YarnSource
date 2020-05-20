/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.FormCaravanGoal;
import net.minecraft.entity.ai.goal.HorseBondWithPlayerGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class LlamaEntity
extends AbstractDonkeyEntity
implements RangedAttackMob {
    private static final TrackedData<Integer> ATTR_STRENGTH = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CARPET_COLOR = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> ATTR_VARIANT = DataTracker.registerData(LlamaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private boolean spit;
    @Nullable
    private LlamaEntity following;
    @Nullable
    private LlamaEntity follower;

    public LlamaEntity(EntityType<? extends LlamaEntity> arg, World arg2) {
        super((EntityType<? extends AbstractDonkeyEntity>)arg, arg2);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isTrader() {
        return false;
    }

    private void setStrength(int i) {
        this.dataTracker.set(ATTR_STRENGTH, Math.max(1, Math.min(5, i)));
    }

    private void initializeStrength() {
        int i = this.random.nextFloat() < 0.04f ? 5 : 3;
        this.setStrength(1 + this.random.nextInt(i));
    }

    public int getStrength() {
        return this.dataTracker.get(ATTR_STRENGTH);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("Variant", this.getVariant());
        arg.putInt("Strength", this.getStrength());
        if (!this.items.getStack(1).isEmpty()) {
            arg.put("DecorItem", this.items.getStack(1).toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        this.setStrength(arg.getInt("Strength"));
        super.readCustomDataFromTag(arg);
        this.setVariant(arg.getInt("Variant"));
        if (arg.contains("DecorItem", 10)) {
            this.items.setStack(1, ItemStack.fromTag(arg.getCompound("DecorItem")));
        }
        this.updateSaddle();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new HorseBondWithPlayerGoal(this, 1.2));
        this.goalSelector.add(2, new FormCaravanGoal(this, 2.1f));
        this.goalSelector.add(3, new ProjectileAttackGoal(this, 1.25, 40, 20.0f));
        this.goalSelector.add(3, new EscapeDangerGoal(this, 1.2));
        this.goalSelector.add(4, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(5, new FollowParentGoal(this, 1.0));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.7));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(1, new SpitRevengeGoal(this));
        this.targetSelector.add(2, new ChaseWolvesGoal(this));
    }

    public static DefaultAttributeContainer.Builder createLlamaAttributes() {
        return LlamaEntity.createAbstractDonkeyAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ATTR_STRENGTH, 0);
        this.dataTracker.startTracking(CARPET_COLOR, -1);
        this.dataTracker.startTracking(ATTR_VARIANT, 0);
    }

    public int getVariant() {
        return MathHelper.clamp(this.dataTracker.get(ATTR_VARIANT), 0, 3);
    }

    public void setVariant(int i) {
        this.dataTracker.set(ATTR_VARIANT, i);
    }

    @Override
    protected int getInventorySize() {
        if (this.hasChest()) {
            return 2 + 3 * this.getInventoryColumns();
        }
        return super.getInventorySize();
    }

    @Override
    public void updatePassengerPosition(Entity arg) {
        if (!this.hasPassenger(arg)) {
            return;
        }
        float f = MathHelper.cos(this.bodyYaw * ((float)Math.PI / 180));
        float g = MathHelper.sin(this.bodyYaw * ((float)Math.PI / 180));
        float h = 0.3f;
        arg.updatePosition(this.getX() + (double)(0.3f * g), this.getY() + this.getMountedHeightOffset() + arg.getHeightOffset(), this.getZ() - (double)(0.3f * f));
    }

    @Override
    public double getMountedHeightOffset() {
        return (double)this.getHeight() * 0.67;
    }

    @Override
    public boolean canBeControlledByRider() {
        return false;
    }

    @Override
    protected boolean receiveFood(PlayerEntity arg, ItemStack arg2) {
        SoundEvent lv2;
        int i = 0;
        int j = 0;
        float f = 0.0f;
        boolean bl = false;
        Item lv = arg2.getItem();
        if (lv == Items.WHEAT) {
            i = 10;
            j = 3;
            f = 2.0f;
        } else if (lv == Blocks.HAY_BLOCK.asItem()) {
            i = 90;
            j = 6;
            f = 10.0f;
            if (this.isTame() && this.getBreedingAge() == 0 && this.canEat()) {
                bl = true;
                this.lovePlayer(arg);
            }
        }
        if (this.getHealth() < this.getMaximumHealth() && f > 0.0f) {
            this.heal(f);
            bl = true;
        }
        if (this.isBaby() && i > 0) {
            this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), 0.0, 0.0, 0.0);
            if (!this.world.isClient) {
                this.growUp(i);
            }
            bl = true;
        }
        if (j > 0 && (bl || !this.isTame()) && this.getTemper() < this.getMaxTemper()) {
            bl = true;
            if (!this.world.isClient) {
                this.addTemper(j);
            }
        }
        if (bl && !this.isSilent() && (lv2 = this.method_28368()) != null) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), this.method_28368(), this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        return bl;
    }

    @Override
    protected boolean isImmobile() {
        return this.getHealth() <= 0.0f || this.isEatingGrass();
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        int j;
        this.initializeStrength();
        if (arg4 instanceof LlamaData) {
            int i = ((LlamaData)arg4).variant;
        } else {
            j = this.random.nextInt(4);
            arg4 = new LlamaData(j);
        }
        this.setVariant(j);
        return super.initialize(arg, arg2, arg3, arg4, arg5);
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.ENTITY_LLAMA_ANGRY;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_LLAMA_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_LLAMA_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_LLAMA_DEATH;
    }

    @Override
    @Nullable
    protected SoundEvent method_28368() {
        return SoundEvents.ENTITY_LLAMA_EAT;
    }

    @Override
    protected void playStepSound(BlockPos arg, BlockState arg2) {
        this.playSound(SoundEvents.ENTITY_LLAMA_STEP, 0.15f, 1.0f);
    }

    @Override
    protected void playAddChestSound() {
        this.playSound(SoundEvents.ENTITY_LLAMA_CHEST, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
    }

    @Override
    public void playAngrySound() {
        SoundEvent lv = this.getAngrySound();
        if (lv != null) {
            this.playSound(lv, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public int getInventoryColumns() {
        return this.getStrength();
    }

    @Override
    public boolean canEquip() {
        return true;
    }

    @Override
    public boolean setSaddled() {
        return !this.items.getStack(1).isEmpty();
    }

    @Override
    public boolean canEquip(ItemStack arg) {
        Item lv = arg.getItem();
        return ItemTags.CARPETS.contains(lv);
    }

    @Override
    public boolean canBeSaddled() {
        return false;
    }

    @Override
    public void onInventoryChanged(Inventory arg) {
        DyeColor lv = this.getCarpetColor();
        super.onInventoryChanged(arg);
        DyeColor lv2 = this.getCarpetColor();
        if (this.age > 20 && lv2 != null && lv2 != lv) {
            this.playSound(SoundEvents.ENTITY_LLAMA_SWAG, 0.5f, 1.0f);
        }
    }

    @Override
    protected void updateSaddle() {
        if (this.world.isClient) {
            return;
        }
        super.updateSaddle();
        this.setCarpetColor(LlamaEntity.getColorFromCarpet(this.items.getStack(1)));
    }

    private void setCarpetColor(@Nullable DyeColor arg) {
        this.dataTracker.set(CARPET_COLOR, arg == null ? -1 : arg.getId());
    }

    @Nullable
    private static DyeColor getColorFromCarpet(ItemStack arg) {
        Block lv = Block.getBlockFromItem(arg.getItem());
        if (lv instanceof CarpetBlock) {
            return ((CarpetBlock)lv).getColor();
        }
        return null;
    }

    @Nullable
    public DyeColor getCarpetColor() {
        int i = this.dataTracker.get(CARPET_COLOR);
        return i == -1 ? null : DyeColor.byId(i);
    }

    @Override
    public int getMaxTemper() {
        return 30;
    }

    @Override
    public boolean canBreedWith(AnimalEntity arg) {
        return arg != this && arg instanceof LlamaEntity && this.canBreed() && ((LlamaEntity)arg).canBreed();
    }

    @Override
    public LlamaEntity createChild(PassiveEntity arg) {
        LlamaEntity lv = this.createChild();
        this.setChildAttributes(arg, lv);
        LlamaEntity lv2 = (LlamaEntity)arg;
        int i = this.random.nextInt(Math.max(this.getStrength(), lv2.getStrength())) + 1;
        if (this.random.nextFloat() < 0.03f) {
            ++i;
        }
        lv.setStrength(i);
        lv.setVariant(this.random.nextBoolean() ? this.getVariant() : lv2.getVariant());
        return lv;
    }

    protected LlamaEntity createChild() {
        return EntityType.LLAMA.create(this.world);
    }

    private void spitAt(LivingEntity arg) {
        LlamaSpitEntity lv = new LlamaSpitEntity(this.world, this);
        double d = arg.getX() - this.getX();
        double e = arg.getBodyY(0.3333333333333333) - lv.getY();
        double f = arg.getZ() - this.getZ();
        float g = MathHelper.sqrt(d * d + f * f) * 0.2f;
        lv.setVelocity(d, e + (double)g, f, 1.5f, 10.0f);
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_LLAMA_SPIT, this.getSoundCategory(), 1.0f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f);
        }
        this.world.spawnEntity(lv);
        this.spit = true;
    }

    private void setSpit(boolean bl) {
        this.spit = bl;
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        int i = this.computeFallDamage(f, g);
        if (i <= 0) {
            return false;
        }
        if (f >= 6.0f) {
            this.damage(DamageSource.FALL, i);
            if (this.hasPassengers()) {
                for (Entity lv : this.getPassengersDeep()) {
                    lv.damage(DamageSource.FALL, i);
                }
            }
        }
        this.playBlockFallSound();
        return true;
    }

    public void stopFollowing() {
        if (this.following != null) {
            this.following.follower = null;
        }
        this.following = null;
    }

    public void follow(LlamaEntity arg) {
        this.following = arg;
        this.following.follower = this;
    }

    public boolean hasFollower() {
        return this.follower != null;
    }

    public boolean isFollowing() {
        return this.following != null;
    }

    @Nullable
    public LlamaEntity getFollowing() {
        return this.following;
    }

    @Override
    protected double getRunFromLeashSpeed() {
        return 2.0;
    }

    @Override
    protected void walkToParent() {
        if (!this.isFollowing() && this.isBaby()) {
            super.walkToParent();
        }
    }

    @Override
    public boolean eatsGrass() {
        return false;
    }

    @Override
    public void attack(LivingEntity arg, float f) {
        this.spitAt(arg);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    static class ChaseWolvesGoal
    extends FollowTargetGoal<WolfEntity> {
        public ChaseWolvesGoal(LlamaEntity arg2) {
            super(arg2, WolfEntity.class, 16, false, true, arg -> !((WolfEntity)arg).isTamed());
        }

        @Override
        protected double getFollowRange() {
            return super.getFollowRange() * 0.25;
        }
    }

    static class SpitRevengeGoal
    extends RevengeGoal {
        public SpitRevengeGoal(LlamaEntity arg) {
            super(arg, new Class[0]);
        }

        @Override
        public boolean shouldContinue() {
            LlamaEntity lv;
            if (this.mob instanceof LlamaEntity && (lv = (LlamaEntity)this.mob).spit) {
                lv.setSpit(false);
                return false;
            }
            return super.shouldContinue();
        }
    }

    static class LlamaData
    extends PassiveEntity.PassiveData {
        public final int variant;

        private LlamaData(int i) {
            this.variant = i;
        }
    }
}

