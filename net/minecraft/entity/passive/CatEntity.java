/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.CatSitOnBlockGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.entity.ai.goal.FollowTargetIfTamedGoal;
import net.minecraft.entity.ai.goal.GoToOwnerAndPurrGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SitGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.StructureFeature;

public class CatEntity
extends TameableEntity {
    private static final Ingredient TAMING_INGREDIENT = Ingredient.ofItems(Items.COD, Items.SALMON);
    private static final TrackedData<Integer> CAT_TYPE = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> SLEEPING_WITH_OWNER = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> HEAD_DOWN = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> COLLAR_COLOR = DataTracker.registerData(CatEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final Map<Integer, Identifier> TEXTURES = Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(0, new Identifier("textures/entity/cat/tabby.png"));
        hashMap.put(1, new Identifier("textures/entity/cat/black.png"));
        hashMap.put(2, new Identifier("textures/entity/cat/red.png"));
        hashMap.put(3, new Identifier("textures/entity/cat/siamese.png"));
        hashMap.put(4, new Identifier("textures/entity/cat/british_shorthair.png"));
        hashMap.put(5, new Identifier("textures/entity/cat/calico.png"));
        hashMap.put(6, new Identifier("textures/entity/cat/persian.png"));
        hashMap.put(7, new Identifier("textures/entity/cat/ragdoll.png"));
        hashMap.put(8, new Identifier("textures/entity/cat/white.png"));
        hashMap.put(9, new Identifier("textures/entity/cat/jellie.png"));
        hashMap.put(10, new Identifier("textures/entity/cat/all_black.png"));
    });
    private CatFleeGoal<PlayerEntity> fleeGoal;
    private net.minecraft.entity.ai.goal.TemptGoal temptGoal;
    private float sleepAnimation;
    private float prevSleepAnimation;
    private float tailCurlAnimation;
    private float prevTailCurlAnimation;
    private float headDownAnimation;
    private float prevHeadDownAniamtion;

    public CatEntity(EntityType<? extends CatEntity> arg, World arg2) {
        super((EntityType<? extends TameableEntity>)arg, arg2);
    }

    public Identifier getTexture() {
        return TEXTURES.getOrDefault(this.getCatType(), TEXTURES.get(0));
    }

    @Override
    protected void initGoals() {
        this.temptGoal = new TemptGoal(this, 0.6, TAMING_INGREDIENT, true);
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(1, new SitGoal(this));
        this.goalSelector.add(2, new SleepWithOwnerGoal(this));
        this.goalSelector.add(3, this.temptGoal);
        this.goalSelector.add(5, new GoToOwnerAndPurrGoal(this, 1.1, 8));
        this.goalSelector.add(6, new FollowOwnerGoal(this, 1.0, 10.0f, 5.0f, false));
        this.goalSelector.add(7, new CatSitOnBlockGoal(this, 0.8));
        this.goalSelector.add(8, new PounceAtTargetGoal(this, 0.3f));
        this.goalSelector.add(9, new AttackGoal(this));
        this.goalSelector.add(10, new AnimalMateGoal(this, 0.8));
        this.goalSelector.add(11, new WanderAroundFarGoal((MobEntityWithAi)this, 0.8, 1.0000001E-5f));
        this.goalSelector.add(12, new LookAtEntityGoal(this, PlayerEntity.class, 10.0f));
        this.targetSelector.add(1, new FollowTargetIfTamedGoal<RabbitEntity>(this, RabbitEntity.class, false, null));
        this.targetSelector.add(1, new FollowTargetIfTamedGoal<TurtleEntity>(this, TurtleEntity.class, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    public int getCatType() {
        return this.dataTracker.get(CAT_TYPE);
    }

    public void setCatType(int i) {
        if (i < 0 || i >= 11) {
            i = this.random.nextInt(10);
        }
        this.dataTracker.set(CAT_TYPE, i);
    }

    public void setSleepingWithOwner(boolean bl) {
        this.dataTracker.set(SLEEPING_WITH_OWNER, bl);
    }

    public boolean isSleepingWithOwner() {
        return this.dataTracker.get(SLEEPING_WITH_OWNER);
    }

    public void setHeadDown(boolean bl) {
        this.dataTracker.set(HEAD_DOWN, bl);
    }

    public boolean isHeadDown() {
        return this.dataTracker.get(HEAD_DOWN);
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.dataTracker.get(COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor arg) {
        this.dataTracker.set(COLLAR_COLOR, arg.getId());
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(CAT_TYPE, 1);
        this.dataTracker.startTracking(SLEEPING_WITH_OWNER, false);
        this.dataTracker.startTracking(HEAD_DOWN, false);
        this.dataTracker.startTracking(COLLAR_COLOR, DyeColor.RED.getId());
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("CatType", this.getCatType());
        arg.putByte("CollarColor", (byte)this.getCollarColor().getId());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setCatType(arg.getInt("CatType"));
        if (arg.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(arg.getInt("CollarColor")));
        }
    }

    @Override
    public void mobTick() {
        if (this.getMoveControl().isMoving()) {
            double d = this.getMoveControl().getSpeed();
            if (d == 0.6) {
                this.setPose(EntityPose.CROUCHING);
                this.setSprinting(false);
            } else if (d == 1.33) {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(true);
            } else {
                this.setPose(EntityPose.STANDING);
                this.setSprinting(false);
            }
        } else {
            this.setPose(EntityPose.STANDING);
            this.setSprinting(false);
        }
    }

    @Override
    @Nullable
    protected SoundEvent getAmbientSound() {
        if (this.isTamed()) {
            if (this.isInLove()) {
                return SoundEvents.ENTITY_CAT_PURR;
            }
            if (this.random.nextInt(4) == 0) {
                return SoundEvents.ENTITY_CAT_PURREOW;
            }
            return SoundEvents.ENTITY_CAT_AMBIENT;
        }
        return SoundEvents.ENTITY_CAT_STRAY_AMBIENT;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }

    public void hiss() {
        this.playSound(SoundEvents.ENTITY_CAT_HISS, this.getSoundVolume(), this.getSoundPitch());
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_CAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_CAT_DEATH;
    }

    public static DefaultAttributeContainer.Builder createCatAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    @Override
    public boolean handleFallDamage(float f, float g) {
        return false;
    }

    @Override
    protected void eat(PlayerEntity arg, ItemStack arg2) {
        if (this.isBreedingItem(arg2)) {
            this.playSound(SoundEvents.ENTITY_CAT_EAT, 1.0f, 1.0f);
        }
        super.eat(arg, arg2);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    public boolean tryAttack(Entity arg) {
        return arg.damage(DamageSource.mob(this), this.getAttackDamage());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.temptGoal != null && this.temptGoal.isActive() && !this.isTamed() && this.age % 100 == 0) {
            this.playSound(SoundEvents.ENTITY_CAT_BEG_FOR_FOOD, 1.0f, 1.0f);
        }
        this.updateAnimations();
    }

    private void updateAnimations() {
        if ((this.isSleepingWithOwner() || this.isHeadDown()) && this.age % 5 == 0) {
            this.playSound(SoundEvents.ENTITY_CAT_PURR, 0.6f + 0.4f * (this.random.nextFloat() - this.random.nextFloat()), 1.0f);
        }
        this.updateSleepAnimation();
        this.updateHeadDownAnimation();
    }

    private void updateSleepAnimation() {
        this.prevSleepAnimation = this.sleepAnimation;
        this.prevTailCurlAnimation = this.tailCurlAnimation;
        if (this.isSleepingWithOwner()) {
            this.sleepAnimation = Math.min(1.0f, this.sleepAnimation + 0.15f);
            this.tailCurlAnimation = Math.min(1.0f, this.tailCurlAnimation + 0.08f);
        } else {
            this.sleepAnimation = Math.max(0.0f, this.sleepAnimation - 0.22f);
            this.tailCurlAnimation = Math.max(0.0f, this.tailCurlAnimation - 0.13f);
        }
    }

    private void updateHeadDownAnimation() {
        this.prevHeadDownAniamtion = this.headDownAnimation;
        this.headDownAnimation = this.isHeadDown() ? Math.min(1.0f, this.headDownAnimation + 0.1f) : Math.max(0.0f, this.headDownAnimation - 0.13f);
    }

    @Environment(value=EnvType.CLIENT)
    public float getSleepAnimation(float f) {
        return MathHelper.lerp(f, this.prevSleepAnimation, this.sleepAnimation);
    }

    @Environment(value=EnvType.CLIENT)
    public float getTailCurlAnimation(float f) {
        return MathHelper.lerp(f, this.prevTailCurlAnimation, this.tailCurlAnimation);
    }

    @Environment(value=EnvType.CLIENT)
    public float getHeadDownAnimation(float f) {
        return MathHelper.lerp(f, this.prevHeadDownAniamtion, this.headDownAnimation);
    }

    @Override
    public CatEntity createChild(PassiveEntity arg) {
        CatEntity lv = EntityType.CAT.create(this.world);
        if (arg instanceof CatEntity) {
            if (this.random.nextBoolean()) {
                lv.setCatType(this.getCatType());
            } else {
                lv.setCatType(((CatEntity)arg).getCatType());
            }
            if (this.isTamed()) {
                lv.setOwnerUuid(this.getOwnerUuid());
                lv.setTamed(true);
                if (this.random.nextBoolean()) {
                    lv.setCollarColor(this.getCollarColor());
                } else {
                    lv.setCollarColor(((CatEntity)arg).getCollarColor());
                }
            }
        }
        return lv;
    }

    @Override
    public boolean canBreedWith(AnimalEntity arg) {
        if (!this.isTamed()) {
            return false;
        }
        if (!(arg instanceof CatEntity)) {
            return false;
        }
        CatEntity lv = (CatEntity)arg;
        return lv.isTamed() && super.canBreedWith(arg);
    }

    @Override
    @Nullable
    public EntityData initialize(WorldAccess arg, LocalDifficulty arg2, SpawnReason arg3, @Nullable EntityData arg4, @Nullable CompoundTag arg5) {
        arg4 = super.initialize(arg, arg2, arg3, arg4, arg5);
        if (arg.getMoonSize() > 0.9f) {
            this.setCatType(this.random.nextInt(11));
        } else {
            this.setCatType(this.random.nextInt(10));
        }
        World lv = arg.getWorld();
        if (lv instanceof ServerWorld && ((ServerWorld)lv).getStructureAccessor().method_28388(this.getBlockPos(), true, StructureFeature.SWAMP_HUT).hasChildren()) {
            this.setCatType(10);
            this.setPersistent();
        }
        return arg4;
    }

    /*
     * Exception decompiling
     */
    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:405)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:480)
         * org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:619)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:779)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:251)
         * org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:185)
         * org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         * org.benf.cfr.reader.entities.Method.analyse(Method.java:463)
         * org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1001)
         * org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:888)
         * org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:252)
         * org.benf.cfr.reader.Driver.doJar(Driver.java:134)
         * org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
         * org.benf.cfr.reader.Main.main(Main.java:49)
         */
        throw new IllegalStateException(Decompilation failed);
    }

    @Override
    public boolean isBreedingItem(ItemStack arg) {
        return TAMING_INGREDIENT.test(arg);
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * 0.5f;
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return !this.isTamed() && this.age > 2400;
    }

    @Override
    protected void onTamedChanged() {
        if (this.fleeGoal == null) {
            this.fleeGoal = new CatFleeGoal<PlayerEntity>(this, PlayerEntity.class, 16.0f, 0.8, 1.33);
        }
        this.goalSelector.remove(this.fleeGoal);
        if (!this.isTamed()) {
            this.goalSelector.add(4, this.fleeGoal);
        }
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    static class SleepWithOwnerGoal
    extends Goal {
        private final CatEntity cat;
        private PlayerEntity owner;
        private BlockPos bedPos;
        private int ticksOnBed;

        public SleepWithOwnerGoal(CatEntity arg) {
            this.cat = arg;
        }

        @Override
        public boolean canStart() {
            if (!this.cat.isTamed()) {
                return false;
            }
            if (this.cat.isSitting()) {
                return false;
            }
            LivingEntity lv = this.cat.getOwner();
            if (lv instanceof PlayerEntity) {
                this.owner = (PlayerEntity)lv;
                if (!lv.isSleeping()) {
                    return false;
                }
                if (this.cat.squaredDistanceTo(this.owner) > 100.0) {
                    return false;
                }
                BlockPos lv2 = this.owner.getBlockPos();
                BlockState lv3 = this.cat.world.getBlockState(lv2);
                if (lv3.getBlock().isIn(BlockTags.BEDS)) {
                    this.bedPos = lv3.method_28500(BedBlock.FACING).map(arg2 -> lv2.offset(arg2.getOpposite())).orElseGet(() -> new BlockPos(lv2));
                    return !this.method_16098();
                }
            }
            return false;
        }

        private boolean method_16098() {
            List<CatEntity> list = this.cat.world.getNonSpectatingEntities(CatEntity.class, new Box(this.bedPos).expand(2.0));
            for (CatEntity lv : list) {
                if (lv == this.cat || !lv.isSleepingWithOwner() && !lv.isHeadDown()) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return this.cat.isTamed() && !this.cat.isSitting() && this.owner != null && this.owner.isSleeping() && this.bedPos != null && !this.method_16098();
        }

        @Override
        public void start() {
            if (this.bedPos != null) {
                this.cat.setInSittingPose(false);
                this.cat.getNavigation().startMovingTo(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.1f);
            }
        }

        @Override
        public void stop() {
            this.cat.setSleepingWithOwner(false);
            float f = this.cat.world.getSkyAngle(1.0f);
            if (this.owner.getSleepTimer() >= 100 && (double)f > 0.77 && (double)f < 0.8 && (double)this.cat.world.getRandom().nextFloat() < 0.7) {
                this.dropMorningGifts();
            }
            this.ticksOnBed = 0;
            this.cat.setHeadDown(false);
            this.cat.getNavigation().stop();
        }

        private void dropMorningGifts() {
            Random random = this.cat.getRandom();
            BlockPos.Mutable lv = new BlockPos.Mutable();
            lv.set(this.cat.getBlockPos());
            this.cat.teleport(lv.getX() + random.nextInt(11) - 5, lv.getY() + random.nextInt(5) - 2, lv.getZ() + random.nextInt(11) - 5, false);
            lv.set(this.cat.getBlockPos());
            LootTable lv2 = this.cat.world.getServer().getLootManager().getTable(LootTables.CAT_MORNING_GIFT_GAMEPLAY);
            LootContext.Builder lv3 = new LootContext.Builder((ServerWorld)this.cat.world).parameter(LootContextParameters.POSITION, lv).parameter(LootContextParameters.THIS_ENTITY, this.cat).random(random);
            List<ItemStack> list = lv2.generateLoot(lv3.build(LootContextTypes.GIFT));
            for (ItemStack lv4 : list) {
                this.cat.world.spawnEntity(new ItemEntity(this.cat.world, (double)lv.getX() - (double)MathHelper.sin(this.cat.bodyYaw * ((float)Math.PI / 180)), lv.getY(), (double)lv.getZ() + (double)MathHelper.cos(this.cat.bodyYaw * ((float)Math.PI / 180)), lv4));
            }
        }

        @Override
        public void tick() {
            if (this.owner != null && this.bedPos != null) {
                this.cat.setInSittingPose(false);
                this.cat.getNavigation().startMovingTo(this.bedPos.getX(), this.bedPos.getY(), this.bedPos.getZ(), 1.1f);
                if (this.cat.squaredDistanceTo(this.owner) < 2.5) {
                    ++this.ticksOnBed;
                    if (this.ticksOnBed > 16) {
                        this.cat.setSleepingWithOwner(true);
                        this.cat.setHeadDown(false);
                    } else {
                        this.cat.lookAtEntity(this.owner, 45.0f, 45.0f);
                        this.cat.setHeadDown(true);
                    }
                } else {
                    this.cat.setSleepingWithOwner(false);
                }
            }
        }
    }

    static class TemptGoal
    extends net.minecraft.entity.ai.goal.TemptGoal {
        @Nullable
        private PlayerEntity player;
        private final CatEntity cat;

        public TemptGoal(CatEntity arg, double d, Ingredient arg2, boolean bl) {
            super((MobEntityWithAi)arg, d, arg2, bl);
            this.cat = arg;
        }

        @Override
        public void tick() {
            super.tick();
            if (this.player == null && this.mob.getRandom().nextInt(600) == 0) {
                this.player = this.closestPlayer;
            } else if (this.mob.getRandom().nextInt(500) == 0) {
                this.player = null;
            }
        }

        @Override
        protected boolean canBeScared() {
            if (this.player != null && this.player.equals(this.closestPlayer)) {
                return false;
            }
            return super.canBeScared();
        }

        @Override
        public boolean canStart() {
            return super.canStart() && !this.cat.isTamed();
        }
    }

    static class CatFleeGoal<T extends LivingEntity>
    extends FleeEntityGoal<T> {
        private final CatEntity cat;

        public CatFleeGoal(CatEntity arg, Class<T> class_, float f, double d, double e) {
            super(arg, class_, f, d, e, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);
            this.cat = arg;
        }

        @Override
        public boolean canStart() {
            return !this.cat.isTamed() && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return !this.cat.isTamed() && super.shouldContinue();
        }
    }
}

