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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.Blocks;
import net.minecraft.class_5425;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.JumpControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.LeashKnotEntity;
import net.minecraft.entity.mob.MobVisibilityCache;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.SwordItem;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class MobEntity
extends LivingEntity {
    private static final TrackedData<Byte> MOB_FLAGS = DataTracker.registerData(MobEntity.class, TrackedDataHandlerRegistry.BYTE);
    public int ambientSoundChance;
    protected int experiencePoints;
    protected LookControl lookControl;
    protected MoveControl moveControl;
    protected JumpControl jumpControl;
    private final BodyControl bodyControl;
    protected EntityNavigation navigation;
    protected final GoalSelector goalSelector;
    protected final GoalSelector targetSelector;
    private LivingEntity target;
    private final MobVisibilityCache visibilityCache;
    private final DefaultedList<ItemStack> handItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    protected final float[] handDropChances = new float[2];
    private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    protected final float[] armorDropChances = new float[4];
    private boolean pickUpLoot;
    private boolean persistent;
    private final Map<PathNodeType, Float> pathfindingPenalties = Maps.newEnumMap(PathNodeType.class);
    private Identifier lootTable;
    private long lootTableSeed;
    @Nullable
    private Entity holdingEntity;
    private int holdingEntityId;
    @Nullable
    private CompoundTag leashTag;
    private BlockPos positionTarget = BlockPos.ORIGIN;
    private float positionTargetRange = -1.0f;

    protected MobEntity(EntityType<? extends MobEntity> arg, World arg2) {
        super((EntityType<? extends LivingEntity>)arg, arg2);
        this.goalSelector = new GoalSelector(arg2.getProfilerSupplier());
        this.targetSelector = new GoalSelector(arg2.getProfilerSupplier());
        this.lookControl = new LookControl(this);
        this.moveControl = new MoveControl(this);
        this.jumpControl = new JumpControl(this);
        this.bodyControl = this.createBodyControl();
        this.navigation = this.createNavigation(arg2);
        this.visibilityCache = new MobVisibilityCache(this);
        Arrays.fill(this.armorDropChances, 0.085f);
        Arrays.fill(this.handDropChances, 0.085f);
        if (arg2 != null && !arg2.isClient) {
            this.initGoals();
        }
    }

    protected void initGoals() {
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        return LivingEntity.createLivingAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16.0).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
    }

    protected EntityNavigation createNavigation(World world) {
        return new MobNavigation(this, world);
    }

    protected boolean movesIndependently() {
        return false;
    }

    public float getPathfindingPenalty(PathNodeType nodeType) {
        MobEntity lv2;
        if (this.getVehicle() instanceof MobEntity && ((MobEntity)this.getVehicle()).movesIndependently()) {
            MobEntity lv = (MobEntity)this.getVehicle();
        } else {
            lv2 = this;
        }
        Float float_ = lv2.pathfindingPenalties.get((Object)nodeType);
        return float_ == null ? nodeType.getDefaultPenalty() : float_.floatValue();
    }

    public void setPathfindingPenalty(PathNodeType nodeType, float penalty) {
        this.pathfindingPenalties.put(nodeType, Float.valueOf(penalty));
    }

    public boolean method_29244(PathNodeType arg) {
        return arg != PathNodeType.DANGER_FIRE && arg != PathNodeType.DANGER_CACTUS && arg != PathNodeType.DANGER_OTHER;
    }

    protected BodyControl createBodyControl() {
        return new BodyControl(this);
    }

    public LookControl getLookControl() {
        return this.lookControl;
    }

    public MoveControl getMoveControl() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            MobEntity lv = (MobEntity)this.getVehicle();
            return lv.getMoveControl();
        }
        return this.moveControl;
    }

    public JumpControl getJumpControl() {
        return this.jumpControl;
    }

    public EntityNavigation getNavigation() {
        if (this.hasVehicle() && this.getVehicle() instanceof MobEntity) {
            MobEntity lv = (MobEntity)this.getVehicle();
            return lv.getNavigation();
        }
        return this.navigation;
    }

    public MobVisibilityCache getVisibilityCache() {
        return this.visibilityCache;
    }

    @Nullable
    public LivingEntity getTarget() {
        return this.target;
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }

    @Override
    public boolean canTarget(EntityType<?> type) {
        return type != EntityType.GHAST;
    }

    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return false;
    }

    public void onEatingGrass() {
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(MOB_FLAGS, (byte)0);
    }

    public int getMinAmbientSoundDelay() {
        return 80;
    }

    public void playAmbientSound() {
        SoundEvent lv = this.getAmbientSound();
        if (lv != null) {
            this.playSound(lv, this.getSoundVolume(), this.getSoundPitch());
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.world.getProfiler().push("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.ambientSoundChance++) {
            this.resetSoundDelay();
            this.playAmbientSound();
        }
        this.world.getProfiler().pop();
    }

    @Override
    protected void playHurtSound(DamageSource source) {
        this.resetSoundDelay();
        super.playHurtSound(source);
    }

    private void resetSoundDelay() {
        this.ambientSoundChance = -this.getMinAmbientSoundDelay();
    }

    @Override
    protected int getCurrentExperience(PlayerEntity player) {
        if (this.experiencePoints > 0) {
            int i = this.experiencePoints;
            for (int j = 0; j < this.armorItems.size(); ++j) {
                if (this.armorItems.get(j).isEmpty() || !(this.armorDropChances[j] <= 1.0f)) continue;
                i += 1 + this.random.nextInt(3);
            }
            for (int k = 0; k < this.handItems.size(); ++k) {
                if (this.handItems.get(k).isEmpty() || !(this.handDropChances[k] <= 1.0f)) continue;
                i += 1 + this.random.nextInt(3);
            }
            return i;
        }
        return this.experiencePoints;
    }

    public void playSpawnEffects() {
        if (this.world.isClient) {
            for (int i = 0; i < 20; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                double g = 10.0;
                this.world.addParticle(ParticleTypes.POOF, this.offsetX(1.0) - d * 10.0, this.getRandomBodyY() - e * 10.0, this.getParticleZ(1.0) - f * 10.0, d, e, f);
            }
        } else {
            this.world.sendEntityStatus(this, (byte)20);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        if (status == 20) {
            this.playSpawnEffects();
        } else {
            super.handleStatus(status);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient) {
            this.updateLeash();
            if (this.age % 5 == 0) {
                this.updateGoalControls();
            }
        }
    }

    protected void updateGoalControls() {
        boolean bl = !(this.getPrimaryPassenger() instanceof MobEntity);
        boolean bl2 = !(this.getVehicle() instanceof BoatEntity);
        this.goalSelector.setControlEnabled(Goal.Control.MOVE, bl);
        this.goalSelector.setControlEnabled(Goal.Control.JUMP, bl && bl2);
        this.goalSelector.setControlEnabled(Goal.Control.LOOK, bl);
    }

    @Override
    protected float turnHead(float bodyRotation, float headRotation) {
        this.bodyControl.tick();
        return headRotation;
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("CanPickUpLoot", this.canPickUpLoot());
        tag.putBoolean("PersistenceRequired", this.persistent);
        ListTag lv = new ListTag();
        for (ItemStack itemStack : this.armorItems) {
            CompoundTag compoundTag = new CompoundTag();
            if (!itemStack.isEmpty()) {
                itemStack.toTag(compoundTag);
            }
            lv.add(compoundTag);
        }
        tag.put("ArmorItems", lv);
        ListTag lv4 = new ListTag();
        for (ItemStack itemStack : this.handItems) {
            CompoundTag lv6 = new CompoundTag();
            if (!itemStack.isEmpty()) {
                itemStack.toTag(lv6);
            }
            lv4.add(lv6);
        }
        tag.put("HandItems", lv4);
        ListTag listTag = new ListTag();
        for (float f : this.armorDropChances) {
            listTag.add(FloatTag.of(f));
        }
        tag.put("ArmorDropChances", listTag);
        ListTag listTag2 = new ListTag();
        for (float g : this.handDropChances) {
            listTag2.add(FloatTag.of(g));
        }
        tag.put("HandDropChances", listTag2);
        if (this.holdingEntity != null) {
            CompoundTag lv9 = new CompoundTag();
            if (this.holdingEntity instanceof LivingEntity) {
                UUID uUID = this.holdingEntity.getUuid();
                lv9.putUuid("UUID", uUID);
            } else if (this.holdingEntity instanceof AbstractDecorationEntity) {
                BlockPos lv10 = ((AbstractDecorationEntity)this.holdingEntity).getDecorationBlockPos();
                lv9.putInt("X", lv10.getX());
                lv9.putInt("Y", lv10.getY());
                lv9.putInt("Z", lv10.getZ());
            }
            tag.put("Leash", lv9);
        } else if (this.leashTag != null) {
            tag.put("Leash", this.leashTag.copy());
        }
        tag.putBoolean("LeftHanded", this.isLeftHanded());
        if (this.lootTable != null) {
            tag.putString("DeathLootTable", this.lootTable.toString());
            if (this.lootTableSeed != 0L) {
                tag.putLong("DeathLootTableSeed", this.lootTableSeed);
            }
        }
        if (this.isAiDisabled()) {
            tag.putBoolean("NoAI", this.isAiDisabled());
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("CanPickUpLoot", 1)) {
            this.setCanPickUpLoot(tag.getBoolean("CanPickUpLoot"));
        }
        this.persistent = tag.getBoolean("PersistenceRequired");
        if (tag.contains("ArmorItems", 9)) {
            ListTag lv = tag.getList("ArmorItems", 10);
            for (int i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.fromTag(lv.getCompound(i)));
            }
        }
        if (tag.contains("HandItems", 9)) {
            ListTag lv2 = tag.getList("HandItems", 10);
            for (int j = 0; j < this.handItems.size(); ++j) {
                this.handItems.set(j, ItemStack.fromTag(lv2.getCompound(j)));
            }
        }
        if (tag.contains("ArmorDropChances", 9)) {
            ListTag lv3 = tag.getList("ArmorDropChances", 5);
            for (int k = 0; k < lv3.size(); ++k) {
                this.armorDropChances[k] = lv3.getFloat(k);
            }
        }
        if (tag.contains("HandDropChances", 9)) {
            ListTag lv4 = tag.getList("HandDropChances", 5);
            for (int l = 0; l < lv4.size(); ++l) {
                this.handDropChances[l] = lv4.getFloat(l);
            }
        }
        if (tag.contains("Leash", 10)) {
            this.leashTag = tag.getCompound("Leash");
        }
        this.setLeftHanded(tag.getBoolean("LeftHanded"));
        if (tag.contains("DeathLootTable", 8)) {
            this.lootTable = new Identifier(tag.getString("DeathLootTable"));
            this.lootTableSeed = tag.getLong("DeathLootTableSeed");
        }
        this.setAiDisabled(tag.getBoolean("NoAI"));
    }

    @Override
    protected void dropLoot(DamageSource source, boolean causedByPlayer) {
        super.dropLoot(source, causedByPlayer);
        this.lootTable = null;
    }

    @Override
    protected LootContext.Builder getLootContextBuilder(boolean causedByPlayer, DamageSource source) {
        return super.getLootContextBuilder(causedByPlayer, source).random(this.lootTableSeed, this.random);
    }

    @Override
    public final Identifier getLootTable() {
        return this.lootTable == null ? this.getLootTableId() : this.lootTable;
    }

    protected Identifier getLootTableId() {
        return super.getLootTable();
    }

    public void setForwardSpeed(float forwardSpeed) {
        this.forwardSpeed = forwardSpeed;
    }

    public void setUpwardSpeed(float upwardSpeed) {
        this.upwardSpeed = upwardSpeed;
    }

    public void setSidewaysSpeed(float sidewaysMovement) {
        this.sidewaysSpeed = sidewaysMovement;
    }

    @Override
    public void setMovementSpeed(float movementSpeed) {
        super.setMovementSpeed(movementSpeed);
        this.setForwardSpeed(movementSpeed);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        this.world.getProfiler().push("looting");
        if (!this.world.isClient && this.canPickUpLoot() && this.isAlive() && !this.dead && this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            List<ItemEntity> list = this.world.getNonSpectatingEntities(ItemEntity.class, this.getBoundingBox().expand(1.0, 0.0, 1.0));
            for (ItemEntity lv : list) {
                if (lv.removed || lv.getStack().isEmpty() || lv.cannotPickup() || !this.canGather(lv.getStack())) continue;
                this.loot(lv);
            }
        }
        this.world.getProfiler().pop();
    }

    protected void loot(ItemEntity item) {
        ItemStack lv = item.getStack();
        if (this.tryEquip(lv)) {
            this.method_29499(item);
            this.sendPickup(item, lv.getCount());
            item.remove();
        }
    }

    public boolean tryEquip(ItemStack equipment) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(equipment);
        ItemStack lv2 = this.getEquippedStack(lv);
        boolean bl = this.prefersNewEquipment(equipment, lv2);
        if (bl && this.canPickupItem(equipment)) {
            double d = this.getDropChance(lv);
            if (!lv2.isEmpty() && (double)Math.max(this.random.nextFloat() - 0.1f, 0.0f) < d) {
                this.dropStack(lv2);
            }
            this.equipLootStack(lv, equipment);
            this.onEquipStack(equipment);
            return true;
        }
        return false;
    }

    protected void equipLootStack(EquipmentSlot slot, ItemStack stack) {
        this.equipStack(slot, stack);
        this.updateDropChances(slot);
        this.persistent = true;
    }

    public void updateDropChances(EquipmentSlot slot) {
        switch (slot.getType()) {
            case HAND: {
                this.handDropChances[slot.getEntitySlotId()] = 2.0f;
                break;
            }
            case ARMOR: {
                this.armorDropChances[slot.getEntitySlotId()] = 2.0f;
            }
        }
    }

    protected boolean prefersNewEquipment(ItemStack newStack, ItemStack oldStack) {
        if (oldStack.isEmpty()) {
            return true;
        }
        if (newStack.getItem() instanceof SwordItem) {
            if (!(oldStack.getItem() instanceof SwordItem)) {
                return true;
            }
            SwordItem lv = (SwordItem)newStack.getItem();
            SwordItem lv2 = (SwordItem)oldStack.getItem();
            if (lv.getAttackDamage() != lv2.getAttackDamage()) {
                return lv.getAttackDamage() > lv2.getAttackDamage();
            }
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof BowItem && oldStack.getItem() instanceof BowItem) {
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof CrossbowItem && oldStack.getItem() instanceof CrossbowItem) {
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof ArmorItem) {
            if (EnchantmentHelper.hasBindingCurse(oldStack)) {
                return false;
            }
            if (!(oldStack.getItem() instanceof ArmorItem)) {
                return true;
            }
            ArmorItem lv3 = (ArmorItem)newStack.getItem();
            ArmorItem lv4 = (ArmorItem)oldStack.getItem();
            if (lv3.getProtection() != lv4.getProtection()) {
                return lv3.getProtection() > lv4.getProtection();
            }
            if (lv3.method_26353() != lv4.method_26353()) {
                return lv3.method_26353() > lv4.method_26353();
            }
            return this.prefersNewDamageableItem(newStack, oldStack);
        }
        if (newStack.getItem() instanceof MiningToolItem) {
            if (oldStack.getItem() instanceof BlockItem) {
                return true;
            }
            if (oldStack.getItem() instanceof MiningToolItem) {
                MiningToolItem lv5 = (MiningToolItem)newStack.getItem();
                MiningToolItem lv6 = (MiningToolItem)oldStack.getItem();
                if (lv5.getAttackDamage() != lv6.getAttackDamage()) {
                    return lv5.getAttackDamage() > lv6.getAttackDamage();
                }
                return this.prefersNewDamageableItem(newStack, oldStack);
            }
        }
        return false;
    }

    public boolean prefersNewDamageableItem(ItemStack newStack, ItemStack oldStack) {
        if (newStack.getDamage() < oldStack.getDamage() || newStack.hasTag() && !oldStack.hasTag()) {
            return true;
        }
        if (newStack.hasTag() && oldStack.hasTag()) {
            return newStack.getTag().getKeys().stream().anyMatch(string -> !string.equals("Damage")) && !oldStack.getTag().getKeys().stream().anyMatch(string -> !string.equals("Damage"));
        }
        return false;
    }

    public boolean canPickupItem(ItemStack stack) {
        return true;
    }

    public boolean canGather(ItemStack stack) {
        return this.canPickupItem(stack);
    }

    public boolean canImmediatelyDespawn(double distanceSquared) {
        return true;
    }

    public boolean cannotDespawn() {
        return this.hasVehicle();
    }

    protected boolean isDisallowedInPeaceful() {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.isDisallowedInPeaceful()) {
            this.remove();
            return;
        }
        if (this.isPersistent() || this.cannotDespawn()) {
            this.despawnCounter = 0;
            return;
        }
        PlayerEntity lv = this.world.getClosestPlayer(this, -1.0);
        if (lv != null) {
            int i;
            int j;
            double d = lv.squaredDistanceTo(this);
            if (d > (double)(j = (i = this.getType().getSpawnGroup().getImmediateDespawnRange()) * i) && this.canImmediatelyDespawn(d)) {
                this.remove();
            }
            int k = this.getType().getSpawnGroup().getDespawnStartRange();
            int l = k * k;
            if (this.despawnCounter > 600 && this.random.nextInt(800) == 0 && d > (double)l && this.canImmediatelyDespawn(d)) {
                this.remove();
            } else if (d < (double)l) {
                this.despawnCounter = 0;
            }
        }
    }

    @Override
    protected final void tickNewAi() {
        ++this.despawnCounter;
        this.world.getProfiler().push("sensing");
        this.visibilityCache.clear();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("targetSelector");
        this.targetSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("goalSelector");
        this.goalSelector.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("navigation");
        this.navigation.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("mob tick");
        this.mobTick();
        this.world.getProfiler().pop();
        this.world.getProfiler().push("controls");
        this.world.getProfiler().push("move");
        this.moveControl.tick();
        this.world.getProfiler().swap("look");
        this.lookControl.tick();
        this.world.getProfiler().swap("jump");
        this.jumpControl.tick();
        this.world.getProfiler().pop();
        this.world.getProfiler().pop();
        this.sendAiDebugData();
    }

    protected void sendAiDebugData() {
        DebugInfoSender.sendGoalSelector(this.world, this, this.goalSelector);
    }

    protected void mobTick() {
    }

    public int getLookPitchSpeed() {
        return 40;
    }

    public int getBodyYawSpeed() {
        return 75;
    }

    public int getLookYawSpeed() {
        return 10;
    }

    public void lookAtEntity(Entity targetEntity, float maxYawChange, float maxPitchChange) {
        double i;
        double d = targetEntity.getX() - this.getX();
        double e = targetEntity.getZ() - this.getZ();
        if (targetEntity instanceof LivingEntity) {
            LivingEntity lv = (LivingEntity)targetEntity;
            double h = lv.getEyeY() - this.getEyeY();
        } else {
            i = (targetEntity.getBoundingBox().minY + targetEntity.getBoundingBox().maxY) / 2.0 - this.getEyeY();
        }
        double j = MathHelper.sqrt(d * d + e * e);
        float k = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
        float l = (float)(-(MathHelper.atan2(i, j) * 57.2957763671875));
        this.pitch = this.changeAngle(this.pitch, l, maxPitchChange);
        this.yaw = this.changeAngle(this.yaw, k, maxYawChange);
    }

    private float changeAngle(float oldAngle, float newAngle, float maxChangeInAngle) {
        float i = MathHelper.wrapDegrees(newAngle - oldAngle);
        if (i > maxChangeInAngle) {
            i = maxChangeInAngle;
        }
        if (i < -maxChangeInAngle) {
            i = -maxChangeInAngle;
        }
        return oldAngle + i;
    }

    public static boolean canMobSpawn(EntityType<? extends MobEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        BlockPos lv = pos.down();
        return spawnReason == SpawnReason.SPAWNER || world.getBlockState(lv).allowsSpawning(world, lv, type);
    }

    public boolean canSpawn(WorldAccess world, SpawnReason spawnReason) {
        return true;
    }

    public boolean canSpawn(WorldView world) {
        return !world.containsFluid(this.getBoundingBox()) && world.intersectsEntities(this);
    }

    public int getLimitPerChunk() {
        return 4;
    }

    public boolean spawnsTooManyForEachTry(int count) {
        return false;
    }

    @Override
    public int getSafeFallDistance() {
        if (this.getTarget() == null) {
            return 3;
        }
        int i = (int)(this.getHealth() - this.getMaxHealth() * 0.33f);
        if ((i -= (3 - this.world.getDifficulty().getId()) * 4) < 0) {
            i = 0;
        }
        return i + 3;
    }

    @Override
    public Iterable<ItemStack> getItemsHand() {
        return this.handItems;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        switch (slot.getType()) {
            case HAND: {
                return this.handItems.get(slot.getEntitySlotId());
            }
            case ARMOR: {
                return this.armorItems.get(slot.getEntitySlotId());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        switch (slot.getType()) {
            case HAND: {
                this.handItems.set(slot.getEntitySlotId(), stack);
                break;
            }
            case ARMOR: {
                this.armorItems.set(slot.getEntitySlotId(), stack);
            }
        }
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        for (EquipmentSlot lv : EquipmentSlot.values()) {
            boolean bl2;
            ItemStack lv2 = this.getEquippedStack(lv);
            float f = this.getDropChance(lv);
            boolean bl = bl2 = f > 1.0f;
            if (lv2.isEmpty() || EnchantmentHelper.hasVanishingCurse(lv2) || !allowDrops && !bl2 || !(Math.max(this.random.nextFloat() - (float)lootingMultiplier * 0.01f, 0.0f) < f)) continue;
            if (!bl2 && lv2.isDamageable()) {
                lv2.setDamage(lv2.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(lv2.getMaxDamage() - 3, 1))));
            }
            this.dropStack(lv2);
        }
    }

    protected float getDropChance(EquipmentSlot slot) {
        float h;
        switch (slot.getType()) {
            case HAND: {
                float f = this.handDropChances[slot.getEntitySlotId()];
                break;
            }
            case ARMOR: {
                float g = this.armorDropChances[slot.getEntitySlotId()];
                break;
            }
            default: {
                h = 0.0f;
            }
        }
        return h;
    }

    protected void initEquipment(LocalDifficulty difficulty) {
        if (this.random.nextFloat() < 0.15f * difficulty.getClampedLocalDifficulty()) {
            float f;
            int i = this.random.nextInt(2);
            float f2 = f = this.world.getDifficulty() == Difficulty.HARD ? 0.1f : 0.25f;
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            if (this.random.nextFloat() < 0.095f) {
                ++i;
            }
            boolean bl = true;
            for (EquipmentSlot lv : EquipmentSlot.values()) {
                Item lv3;
                if (lv.getType() != EquipmentSlot.Type.ARMOR) continue;
                ItemStack lv2 = this.getEquippedStack(lv);
                if (!bl && this.random.nextFloat() < f) break;
                bl = false;
                if (!lv2.isEmpty() || (lv3 = MobEntity.getEquipmentForSlot(lv, i)) == null) continue;
                this.equipStack(lv, new ItemStack(lv3));
            }
        }
    }

    public static EquipmentSlot getPreferredEquipmentSlot(ItemStack stack) {
        Item lv = stack.getItem();
        if (lv == Blocks.CARVED_PUMPKIN.asItem() || lv instanceof BlockItem && ((BlockItem)lv).getBlock() instanceof AbstractSkullBlock) {
            return EquipmentSlot.HEAD;
        }
        if (lv instanceof ArmorItem) {
            return ((ArmorItem)lv).getSlotType();
        }
        if (lv == Items.ELYTRA) {
            return EquipmentSlot.CHEST;
        }
        if (lv == Items.SHIELD) {
            return EquipmentSlot.OFFHAND;
        }
        return EquipmentSlot.MAINHAND;
    }

    @Nullable
    public static Item getEquipmentForSlot(EquipmentSlot equipmentSlot, int equipmentLevel) {
        switch (equipmentSlot) {
            case HEAD: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_HELMET;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_HELMET;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_HELMET;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_HELMET;
                }
                if (equipmentLevel == 4) {
                    return Items.DIAMOND_HELMET;
                }
            }
            case CHEST: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_CHESTPLATE;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_CHESTPLATE;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_CHESTPLATE;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_CHESTPLATE;
                }
                if (equipmentLevel == 4) {
                    return Items.DIAMOND_CHESTPLATE;
                }
            }
            case LEGS: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_LEGGINGS;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_LEGGINGS;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_LEGGINGS;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_LEGGINGS;
                }
                if (equipmentLevel == 4) {
                    return Items.DIAMOND_LEGGINGS;
                }
            }
            case FEET: {
                if (equipmentLevel == 0) {
                    return Items.LEATHER_BOOTS;
                }
                if (equipmentLevel == 1) {
                    return Items.GOLDEN_BOOTS;
                }
                if (equipmentLevel == 2) {
                    return Items.CHAINMAIL_BOOTS;
                }
                if (equipmentLevel == 3) {
                    return Items.IRON_BOOTS;
                }
                if (equipmentLevel != 4) break;
                return Items.DIAMOND_BOOTS;
            }
        }
        return null;
    }

    protected void updateEnchantments(LocalDifficulty difficulty) {
        float f = difficulty.getClampedLocalDifficulty();
        if (!this.getMainHandStack().isEmpty() && this.random.nextFloat() < 0.25f * f) {
            this.equipStack(EquipmentSlot.MAINHAND, EnchantmentHelper.enchant(this.random, this.getMainHandStack(), (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
        for (EquipmentSlot lv : EquipmentSlot.values()) {
            ItemStack lv2;
            if (lv.getType() != EquipmentSlot.Type.ARMOR || (lv2 = this.getEquippedStack(lv)).isEmpty() || !(this.random.nextFloat() < 0.5f * f)) continue;
            this.equipStack(lv, EnchantmentHelper.enchant(this.random, lv2, (int)(5.0f + f * (float)this.random.nextInt(18)), false));
        }
    }

    @Nullable
    public EntityData initialize(class_5425 arg, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag) {
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05, EntityAttributeModifier.Operation.MULTIPLY_BASE));
        if (this.random.nextFloat() < 0.05f) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return entityData;
    }

    public boolean canBeControlledByRider() {
        return false;
    }

    public void setPersistent() {
        this.persistent = true;
    }

    public void setEquipmentDropChance(EquipmentSlot slot, float chance) {
        switch (slot.getType()) {
            case HAND: {
                this.handDropChances[slot.getEntitySlotId()] = chance;
                break;
            }
            case ARMOR: {
                this.armorDropChances[slot.getEntitySlotId()] = chance;
            }
        }
    }

    public boolean canPickUpLoot() {
        return this.pickUpLoot;
    }

    public void setCanPickUpLoot(boolean pickUpLoot) {
        this.pickUpLoot = pickUpLoot;
    }

    @Override
    public boolean canPickUp(ItemStack stack) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(stack);
        return this.getEquippedStack(lv).isEmpty() && this.canPickUpLoot();
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    @Override
    public final ActionResult interact(PlayerEntity player, Hand hand) {
        if (!this.isAlive()) {
            return ActionResult.PASS;
        }
        if (this.getHoldingEntity() == player) {
            this.detachLeash(true, !player.abilities.creativeMode);
            return ActionResult.success(this.world.isClient);
        }
        ActionResult lv = this.method_29506(player, hand);
        if (lv.isAccepted()) {
            return lv;
        }
        lv = this.interactMob(player, hand);
        if (lv.isAccepted()) {
            return lv;
        }
        return super.interact(player, hand);
    }

    private ActionResult method_29506(PlayerEntity arg, Hand arg22) {
        ActionResult lv2;
        ItemStack lv = arg.getStackInHand(arg22);
        if (lv.getItem() == Items.LEAD && this.canBeLeashedBy(arg)) {
            this.attachLeash(arg, true);
            lv.decrement(1);
            return ActionResult.success(this.world.isClient);
        }
        if (lv.getItem() == Items.NAME_TAG && (lv2 = lv.useOnEntity(arg, this, arg22)).isAccepted()) {
            return lv2;
        }
        if (lv.getItem() instanceof SpawnEggItem) {
            if (this.world instanceof ServerWorld) {
                SpawnEggItem lv3 = (SpawnEggItem)lv.getItem();
                Optional<MobEntity> optional = lv3.spawnBaby(arg, this, this.getType(), (ServerWorld)this.world, this.getPos(), lv);
                optional.ifPresent(arg2 -> this.onPlayerSpawnedChild(arg, (MobEntity)arg2));
                return optional.isPresent() ? ActionResult.SUCCESS : ActionResult.PASS;
            }
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }

    protected void onPlayerSpawnedChild(PlayerEntity player, MobEntity child) {
    }

    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        return ActionResult.PASS;
    }

    public boolean isInWalkTargetRange() {
        return this.isInWalkTargetRange(this.getBlockPos());
    }

    public boolean isInWalkTargetRange(BlockPos pos) {
        if (this.positionTargetRange == -1.0f) {
            return true;
        }
        return this.positionTarget.getSquaredDistance(pos) < (double)(this.positionTargetRange * this.positionTargetRange);
    }

    public void setPositionTarget(BlockPos target, int range) {
        this.positionTarget = target;
        this.positionTargetRange = range;
    }

    public BlockPos getPositionTarget() {
        return this.positionTarget;
    }

    public float getPositionTargetRange() {
        return this.positionTargetRange;
    }

    public boolean hasPositionTarget() {
        return this.positionTargetRange != -1.0f;
    }

    @Nullable
    protected <T extends MobEntity> T method_29243(EntityType<T> arg) {
        if (this.removed) {
            return null;
        }
        MobEntity lv = (MobEntity)arg.create(this.world);
        lv.copyPositionAndRotation(this);
        lv.setCanPickUpLoot(this.canPickUpLoot());
        lv.setBaby(this.isBaby());
        lv.setAiDisabled(this.isAiDisabled());
        if (this.hasCustomName()) {
            lv.setCustomName(this.getCustomName());
            lv.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isPersistent()) {
            lv.setPersistent();
        }
        lv.setInvulnerable(this.isInvulnerable());
        for (EquipmentSlot lv2 : EquipmentSlot.values()) {
            ItemStack lv3 = this.getEquippedStack(lv2);
            if (lv3.isEmpty()) continue;
            lv.equipStack(lv2, lv3.copy());
            lv.setEquipmentDropChance(lv2, this.getDropChance(lv2));
            lv3.setCount(0);
        }
        this.world.spawnEntity(lv);
        this.remove();
        return (T)lv;
    }

    protected void updateLeash() {
        if (this.leashTag != null) {
            this.deserializeLeashTag();
        }
        if (this.holdingEntity == null) {
            return;
        }
        if (!this.isAlive() || !this.holdingEntity.isAlive()) {
            this.detachLeash(true, true);
        }
    }

    public void detachLeash(boolean sendPacket, boolean dropItem) {
        if (this.holdingEntity != null) {
            this.teleporting = false;
            if (!(this.holdingEntity instanceof PlayerEntity)) {
                this.holdingEntity.teleporting = false;
            }
            this.holdingEntity = null;
            this.leashTag = null;
            if (!this.world.isClient && dropItem) {
                this.dropItem(Items.LEAD);
            }
            if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
                ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, null));
            }
        }
    }

    public boolean canBeLeashedBy(PlayerEntity player) {
        return !this.isLeashed() && !(this instanceof Monster);
    }

    public boolean isLeashed() {
        return this.holdingEntity != null;
    }

    @Nullable
    public Entity getHoldingEntity() {
        if (this.holdingEntity == null && this.holdingEntityId != 0 && this.world.isClient) {
            this.holdingEntity = this.world.getEntityById(this.holdingEntityId);
        }
        return this.holdingEntity;
    }

    public void attachLeash(Entity entity, boolean sendPacket) {
        this.holdingEntity = entity;
        this.leashTag = null;
        this.teleporting = true;
        if (!(this.holdingEntity instanceof PlayerEntity)) {
            this.holdingEntity.teleporting = true;
        }
        if (!this.world.isClient && sendPacket && this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).getChunkManager().sendToOtherNearbyPlayers(this, new EntityAttachS2CPacket(this, this.holdingEntity));
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void setHoldingEntityId(int id) {
        this.holdingEntityId = id;
        this.detachLeash(false, false);
    }

    @Override
    public boolean startRiding(Entity entity, boolean force) {
        boolean bl2 = super.startRiding(entity, force);
        if (bl2 && this.isLeashed()) {
            this.detachLeash(true, true);
        }
        return bl2;
    }

    private void deserializeLeashTag() {
        if (this.leashTag != null && this.world instanceof ServerWorld) {
            if (this.leashTag.containsUuid("UUID")) {
                UUID uUID = this.leashTag.getUuid("UUID");
                Entity lv = ((ServerWorld)this.world).getEntity(uUID);
                if (lv != null) {
                    this.attachLeash(lv, true);
                    return;
                }
            } else if (this.leashTag.contains("X", 99) && this.leashTag.contains("Y", 99) && this.leashTag.contains("Z", 99)) {
                BlockPos lv2 = new BlockPos(this.leashTag.getInt("X"), this.leashTag.getInt("Y"), this.leashTag.getInt("Z"));
                this.attachLeash(LeashKnotEntity.getOrCreate(this.world, lv2), true);
                return;
            }
            if (this.age > 100) {
                this.dropItem(Items.LEAD);
                this.leashTag = null;
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean equip(int slot, ItemStack item) {
        void lv7;
        if (slot == 98) {
            EquipmentSlot lv = EquipmentSlot.MAINHAND;
        } else if (slot == 99) {
            EquipmentSlot lv2 = EquipmentSlot.OFFHAND;
        } else if (slot == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            EquipmentSlot lv3 = EquipmentSlot.HEAD;
        } else if (slot == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            EquipmentSlot lv4 = EquipmentSlot.CHEST;
        } else if (slot == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            EquipmentSlot lv5 = EquipmentSlot.LEGS;
        } else if (slot == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            EquipmentSlot lv6 = EquipmentSlot.FEET;
        } else {
            return false;
        }
        if (item.isEmpty() || MobEntity.canEquipmentSlotContain((EquipmentSlot)lv7, item) || lv7 == EquipmentSlot.HEAD) {
            this.equipStack((EquipmentSlot)lv7, item);
            return true;
        }
        return false;
    }

    @Override
    public boolean isLogicalSideForUpdatingMovement() {
        return this.canBeControlledByRider() && super.isLogicalSideForUpdatingMovement();
    }

    public static boolean canEquipmentSlotContain(EquipmentSlot slot, ItemStack item) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(item);
        return lv == slot || lv == EquipmentSlot.MAINHAND && slot == EquipmentSlot.OFFHAND || lv == EquipmentSlot.OFFHAND && slot == EquipmentSlot.MAINHAND;
    }

    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && !this.isAiDisabled();
    }

    public void setAiDisabled(boolean aiDisabled) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, aiDisabled ? (byte)(b | 1) : (byte)(b & 0xFFFFFFFE));
    }

    public void setLeftHanded(boolean leftHanded) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, leftHanded ? (byte)(b | 2) : (byte)(b & 0xFFFFFFFD));
    }

    public void setAttacking(boolean attacking) {
        byte b = this.dataTracker.get(MOB_FLAGS);
        this.dataTracker.set(MOB_FLAGS, attacking ? (byte)(b | 4) : (byte)(b & 0xFFFFFFFB));
    }

    public boolean isAiDisabled() {
        return (this.dataTracker.get(MOB_FLAGS) & 1) != 0;
    }

    public boolean isLeftHanded() {
        return (this.dataTracker.get(MOB_FLAGS) & 2) != 0;
    }

    public boolean isAttacking() {
        return (this.dataTracker.get(MOB_FLAGS) & 4) != 0;
    }

    public void setBaby(boolean baby) {
    }

    @Override
    public Arm getMainArm() {
        return this.isLeftHanded() ? Arm.LEFT : Arm.RIGHT;
    }

    @Override
    public boolean canTarget(LivingEntity target) {
        if (target.getType() == EntityType.PLAYER && ((PlayerEntity)target).abilities.invulnerable) {
            return false;
        }
        return super.canTarget(target);
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean bl;
        int i;
        float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float g = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
            g += (float)EnchantmentHelper.getKnockback(this);
        }
        if ((i = EnchantmentHelper.getFireAspect(this)) > 0) {
            target.setOnFireFor(i * 4);
        }
        if (bl = target.damage(DamageSource.mob(this), f)) {
            if (g > 0.0f && target instanceof LivingEntity) {
                ((LivingEntity)target).takeKnockback(g * 0.5f, MathHelper.sin(this.yaw * ((float)Math.PI / 180)), -MathHelper.cos(this.yaw * ((float)Math.PI / 180)));
                this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
            }
            if (target instanceof PlayerEntity) {
                PlayerEntity lv;
                this.disablePlayerShield(lv, this.getMainHandStack(), (lv = (PlayerEntity)target).isUsingItem() ? lv.getActiveItem() : ItemStack.EMPTY);
            }
            this.dealDamage(this, target);
            this.onAttacking(target);
        }
        return bl;
    }

    private void disablePlayerShield(PlayerEntity player, ItemStack mobStack, ItemStack playerStack) {
        if (!mobStack.isEmpty() && !playerStack.isEmpty() && mobStack.getItem() instanceof AxeItem && playerStack.getItem() == Items.SHIELD) {
            float f = 0.25f + (float)EnchantmentHelper.getEfficiency(this) * 0.05f;
            if (this.random.nextFloat() < f) {
                player.getItemCooldownManager().set(Items.SHIELD, 100);
                this.world.sendEntityStatus(player, (byte)30);
            }
        }
    }

    protected boolean isInDaylight() {
        if (this.world.isDay() && !this.world.isClient) {
            BlockPos lv;
            float f = this.getBrightnessAtEyes();
            BlockPos blockPos = lv = this.getVehicle() instanceof BoatEntity ? new BlockPos(this.getX(), Math.round(this.getY()), this.getZ()).up() : new BlockPos(this.getX(), Math.round(this.getY()), this.getZ());
            if (f > 0.5f && this.random.nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.world.isSkyVisible(lv)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void swimUpward(Tag<Fluid> fluid) {
        if (this.getNavigation().canSwim()) {
            super.swimUpward(fluid);
        } else {
            this.setVelocity(this.getVelocity().add(0.0, 0.3, 0.0));
        }
    }

    @Override
    protected void method_30076() {
        super.method_30076();
        this.detachLeash(true, false);
    }
}

