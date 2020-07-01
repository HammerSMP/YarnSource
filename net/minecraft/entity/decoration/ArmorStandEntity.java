/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.decoration;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArmorStandEntity
extends LivingEntity {
    private static final EulerAngle DEFAULT_HEAD_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
    private static final EulerAngle DEFAULT_BODY_ROTATION = new EulerAngle(0.0f, 0.0f, 0.0f);
    private static final EulerAngle DEFAULT_LEFT_ARM_ROTATION = new EulerAngle(-10.0f, 0.0f, -10.0f);
    private static final EulerAngle DEFAULT_RIGHT_ARM_ROTATION = new EulerAngle(-15.0f, 0.0f, 10.0f);
    private static final EulerAngle DEFAULT_LEFT_LEG_ROTATION = new EulerAngle(-1.0f, 0.0f, -1.0f);
    private static final EulerAngle DEFAULT_RIGHT_LEG_ROTATION = new EulerAngle(1.0f, 0.0f, 1.0f);
    public static final TrackedData<Byte> ARMOR_STAND_FLAGS = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.BYTE);
    public static final TrackedData<EulerAngle> TRACKER_HEAD_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_BODY_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_LEFT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_RIGHT_ARM_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_LEFT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    public static final TrackedData<EulerAngle> TRACKER_RIGHT_LEG_ROTATION = DataTracker.registerData(ArmorStandEntity.class, TrackedDataHandlerRegistry.ROTATION);
    private static final Predicate<Entity> RIDEABLE_MINECART_PREDICATE = arg -> arg instanceof AbstractMinecartEntity && ((AbstractMinecartEntity)arg).getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE;
    private final DefaultedList<ItemStack> heldItems = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> armorItems = DefaultedList.ofSize(4, ItemStack.EMPTY);
    private boolean invisible;
    public long lastHitTime;
    private int disabledSlots;
    private EulerAngle headRotation = DEFAULT_HEAD_ROTATION;
    private EulerAngle bodyRotation = DEFAULT_BODY_ROTATION;
    private EulerAngle leftArmRotation = DEFAULT_LEFT_ARM_ROTATION;
    private EulerAngle rightArmRotation = DEFAULT_RIGHT_ARM_ROTATION;
    private EulerAngle leftLegRotation = DEFAULT_LEFT_LEG_ROTATION;
    private EulerAngle rightLegRotation = DEFAULT_RIGHT_LEG_ROTATION;

    public ArmorStandEntity(EntityType<? extends ArmorStandEntity> arg, World arg2) {
        super((EntityType<? extends LivingEntity>)arg, arg2);
        this.stepHeight = 0.0f;
    }

    public ArmorStandEntity(World arg, double d, double e, double f) {
        this((EntityType<? extends ArmorStandEntity>)EntityType.ARMOR_STAND, arg);
        this.updatePosition(d, e, f);
    }

    @Override
    public void calculateDimensions() {
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        super.calculateDimensions();
        this.updatePosition(d, e, f);
    }

    private boolean canClip() {
        return !this.isMarker() && !this.hasNoGravity();
    }

    @Override
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && this.canClip();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(ARMOR_STAND_FLAGS, (byte)0);
        this.dataTracker.startTracking(TRACKER_HEAD_ROTATION, DEFAULT_HEAD_ROTATION);
        this.dataTracker.startTracking(TRACKER_BODY_ROTATION, DEFAULT_BODY_ROTATION);
        this.dataTracker.startTracking(TRACKER_LEFT_ARM_ROTATION, DEFAULT_LEFT_ARM_ROTATION);
        this.dataTracker.startTracking(TRACKER_RIGHT_ARM_ROTATION, DEFAULT_RIGHT_ARM_ROTATION);
        this.dataTracker.startTracking(TRACKER_LEFT_LEG_ROTATION, DEFAULT_LEFT_LEG_ROTATION);
        this.dataTracker.startTracking(TRACKER_RIGHT_LEG_ROTATION, DEFAULT_RIGHT_LEG_ROTATION);
    }

    @Override
    public Iterable<ItemStack> getItemsHand() {
        return this.heldItems;
    }

    @Override
    public Iterable<ItemStack> getArmorItems() {
        return this.armorItems;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot arg) {
        switch (arg.getType()) {
            case HAND: {
                return this.heldItems.get(arg.getEntitySlotId());
            }
            case ARMOR: {
                return this.armorItems.get(arg.getEntitySlotId());
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void equipStack(EquipmentSlot arg, ItemStack arg2) {
        switch (arg.getType()) {
            case HAND: {
                this.onEquipStack(arg2);
                this.heldItems.set(arg.getEntitySlotId(), arg2);
                break;
            }
            case ARMOR: {
                this.onEquipStack(arg2);
                this.armorItems.set(arg.getEntitySlotId(), arg2);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public boolean equip(int i, ItemStack arg) {
        void lv7;
        if (i == 98) {
            EquipmentSlot lv = EquipmentSlot.MAINHAND;
        } else if (i == 99) {
            EquipmentSlot lv2 = EquipmentSlot.OFFHAND;
        } else if (i == 100 + EquipmentSlot.HEAD.getEntitySlotId()) {
            EquipmentSlot lv3 = EquipmentSlot.HEAD;
        } else if (i == 100 + EquipmentSlot.CHEST.getEntitySlotId()) {
            EquipmentSlot lv4 = EquipmentSlot.CHEST;
        } else if (i == 100 + EquipmentSlot.LEGS.getEntitySlotId()) {
            EquipmentSlot lv5 = EquipmentSlot.LEGS;
        } else if (i == 100 + EquipmentSlot.FEET.getEntitySlotId()) {
            EquipmentSlot lv6 = EquipmentSlot.FEET;
        } else {
            return false;
        }
        if (arg.isEmpty() || MobEntity.canEquipmentSlotContain((EquipmentSlot)lv7, arg) || lv7 == EquipmentSlot.HEAD) {
            this.equipStack((EquipmentSlot)lv7, arg);
            return true;
        }
        return false;
    }

    @Override
    public boolean canPickUp(ItemStack arg) {
        EquipmentSlot lv = MobEntity.getPreferredEquipmentSlot(arg);
        return this.getEquippedStack(lv).isEmpty() && !this.isSlotDisabled(lv);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        ListTag lv = new ListTag();
        for (ItemStack lv2 : this.armorItems) {
            CompoundTag lv3 = new CompoundTag();
            if (!lv2.isEmpty()) {
                lv2.toTag(lv3);
            }
            lv.add(lv3);
        }
        arg.put("ArmorItems", lv);
        ListTag lv4 = new ListTag();
        for (ItemStack lv5 : this.heldItems) {
            CompoundTag lv6 = new CompoundTag();
            if (!lv5.isEmpty()) {
                lv5.toTag(lv6);
            }
            lv4.add(lv6);
        }
        arg.put("HandItems", lv4);
        arg.putBoolean("Invisible", this.isInvisible());
        arg.putBoolean("Small", this.isSmall());
        arg.putBoolean("ShowArms", this.shouldShowArms());
        arg.putInt("DisabledSlots", this.disabledSlots);
        arg.putBoolean("NoBasePlate", this.shouldHideBasePlate());
        if (this.isMarker()) {
            arg.putBoolean("Marker", this.isMarker());
        }
        arg.put("Pose", this.serializePose());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        if (arg.contains("ArmorItems", 9)) {
            ListTag lv = arg.getList("ArmorItems", 10);
            for (int i = 0; i < this.armorItems.size(); ++i) {
                this.armorItems.set(i, ItemStack.fromTag(lv.getCompound(i)));
            }
        }
        if (arg.contains("HandItems", 9)) {
            ListTag lv2 = arg.getList("HandItems", 10);
            for (int j = 0; j < this.heldItems.size(); ++j) {
                this.heldItems.set(j, ItemStack.fromTag(lv2.getCompound(j)));
            }
        }
        this.setInvisible(arg.getBoolean("Invisible"));
        this.setSmall(arg.getBoolean("Small"));
        this.setShowArms(arg.getBoolean("ShowArms"));
        this.disabledSlots = arg.getInt("DisabledSlots");
        this.setHideBasePlate(arg.getBoolean("NoBasePlate"));
        this.setMarker(arg.getBoolean("Marker"));
        this.noClip = !this.canClip();
        CompoundTag lv3 = arg.getCompound("Pose");
        this.deserializePose(lv3);
    }

    private void deserializePose(CompoundTag arg) {
        ListTag lv = arg.getList("Head", 5);
        this.setHeadRotation(lv.isEmpty() ? DEFAULT_HEAD_ROTATION : new EulerAngle(lv));
        ListTag lv2 = arg.getList("Body", 5);
        this.setBodyRotation(lv2.isEmpty() ? DEFAULT_BODY_ROTATION : new EulerAngle(lv2));
        ListTag lv3 = arg.getList("LeftArm", 5);
        this.setLeftArmRotation(lv3.isEmpty() ? DEFAULT_LEFT_ARM_ROTATION : new EulerAngle(lv3));
        ListTag lv4 = arg.getList("RightArm", 5);
        this.setRightArmRotation(lv4.isEmpty() ? DEFAULT_RIGHT_ARM_ROTATION : new EulerAngle(lv4));
        ListTag lv5 = arg.getList("LeftLeg", 5);
        this.setLeftLegRotation(lv5.isEmpty() ? DEFAULT_LEFT_LEG_ROTATION : new EulerAngle(lv5));
        ListTag lv6 = arg.getList("RightLeg", 5);
        this.setRightLegRotation(lv6.isEmpty() ? DEFAULT_RIGHT_LEG_ROTATION : new EulerAngle(lv6));
    }

    private CompoundTag serializePose() {
        CompoundTag lv = new CompoundTag();
        if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
            lv.put("Head", this.headRotation.serialize());
        }
        if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
            lv.put("Body", this.bodyRotation.serialize());
        }
        if (!DEFAULT_LEFT_ARM_ROTATION.equals(this.leftArmRotation)) {
            lv.put("LeftArm", this.leftArmRotation.serialize());
        }
        if (!DEFAULT_RIGHT_ARM_ROTATION.equals(this.rightArmRotation)) {
            lv.put("RightArm", this.rightArmRotation.serialize());
        }
        if (!DEFAULT_LEFT_LEG_ROTATION.equals(this.leftLegRotation)) {
            lv.put("LeftLeg", this.leftLegRotation.serialize());
        }
        if (!DEFAULT_RIGHT_LEG_ROTATION.equals(this.rightLegRotation)) {
            lv.put("RightLeg", this.rightLegRotation.serialize());
        }
        return lv;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushAway(Entity arg) {
    }

    @Override
    protected void tickCramming() {
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), RIDEABLE_MINECART_PREDICATE);
        for (int i = 0; i < list.size(); ++i) {
            Entity lv = list.get(i);
            if (!(this.squaredDistanceTo(lv) <= 0.2)) continue;
            lv.pushAwayFrom(this);
        }
    }

    @Override
    public ActionResult interactAt(PlayerEntity arg, Vec3d arg2, Hand arg3) {
        ItemStack lv = arg.getStackInHand(arg3);
        if (this.isMarker() || lv.getItem() == Items.NAME_TAG) {
            return ActionResult.PASS;
        }
        if (arg.isSpectator()) {
            return ActionResult.SUCCESS;
        }
        if (arg.world.isClient) {
            return ActionResult.CONSUME;
        }
        EquipmentSlot lv2 = MobEntity.getPreferredEquipmentSlot(lv);
        if (lv.isEmpty()) {
            EquipmentSlot lv4;
            EquipmentSlot lv3 = this.slotFromPosition(arg2);
            EquipmentSlot equipmentSlot = lv4 = this.isSlotDisabled(lv3) ? lv2 : lv3;
            if (this.hasStackEquipped(lv4) && this.equip(arg, lv4, lv, arg3)) {
                return ActionResult.SUCCESS;
            }
        } else {
            if (this.isSlotDisabled(lv2)) {
                return ActionResult.FAIL;
            }
            if (lv2.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms()) {
                return ActionResult.FAIL;
            }
            if (this.equip(arg, lv2, lv, arg3)) {
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private EquipmentSlot slotFromPosition(Vec3d arg) {
        EquipmentSlot lv = EquipmentSlot.MAINHAND;
        boolean bl = this.isSmall();
        double d = bl ? arg.y * 2.0 : arg.y;
        EquipmentSlot lv2 = EquipmentSlot.FEET;
        if (d >= 0.1) {
            double d2 = bl ? 0.8 : 0.45;
            if (d < 0.1 + d2 && this.hasStackEquipped(lv2)) {
                return EquipmentSlot.FEET;
            }
        }
        double d3 = bl ? 0.3 : 0.0;
        if (d >= 0.9 + d3) {
            double d4 = bl ? 1.0 : 0.7;
            if (d < 0.9 + d4 && this.hasStackEquipped(EquipmentSlot.CHEST)) {
                return EquipmentSlot.CHEST;
            }
        }
        if (d >= 0.4) {
            double d5 = bl ? 1.0 : 0.8;
            if (d < 0.4 + d5 && this.hasStackEquipped(EquipmentSlot.LEGS)) {
                return EquipmentSlot.LEGS;
            }
        }
        if (d >= 1.6 && this.hasStackEquipped(EquipmentSlot.HEAD)) {
            return EquipmentSlot.HEAD;
        }
        if (this.hasStackEquipped(EquipmentSlot.MAINHAND)) return lv;
        if (!this.hasStackEquipped(EquipmentSlot.OFFHAND)) return lv;
        return EquipmentSlot.OFFHAND;
    }

    private boolean isSlotDisabled(EquipmentSlot arg) {
        return (this.disabledSlots & 1 << arg.getArmorStandSlotId()) != 0 || arg.getType() == EquipmentSlot.Type.HAND && !this.shouldShowArms();
    }

    private boolean equip(PlayerEntity arg, EquipmentSlot arg2, ItemStack arg3, Hand arg4) {
        ItemStack lv = this.getEquippedStack(arg2);
        if (!lv.isEmpty() && (this.disabledSlots & 1 << arg2.getArmorStandSlotId() + 8) != 0) {
            return false;
        }
        if (lv.isEmpty() && (this.disabledSlots & 1 << arg2.getArmorStandSlotId() + 16) != 0) {
            return false;
        }
        if (arg.abilities.creativeMode && lv.isEmpty() && !arg3.isEmpty()) {
            ItemStack lv2 = arg3.copy();
            lv2.setCount(1);
            this.equipStack(arg2, lv2);
            return true;
        }
        if (!arg3.isEmpty() && arg3.getCount() > 1) {
            if (!lv.isEmpty()) {
                return false;
            }
            ItemStack lv3 = arg3.copy();
            lv3.setCount(1);
            this.equipStack(arg2, lv3);
            arg3.decrement(1);
            return true;
        }
        this.equipStack(arg2, arg3);
        arg.setStackInHand(arg4, lv);
        return true;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.world.isClient || this.removed) {
            return false;
        }
        if (DamageSource.OUT_OF_WORLD.equals(arg)) {
            this.remove();
            return false;
        }
        if (this.isInvulnerableTo(arg) || this.invisible || this.isMarker()) {
            return false;
        }
        if (arg.isExplosive()) {
            this.onBreak(arg);
            this.remove();
            return false;
        }
        if (DamageSource.IN_FIRE.equals(arg)) {
            if (this.isOnFire()) {
                this.updateHealth(arg, 0.15f);
            } else {
                this.setOnFireFor(5);
            }
            return false;
        }
        if (DamageSource.ON_FIRE.equals(arg) && this.getHealth() > 0.5f) {
            this.updateHealth(arg, 4.0f);
            return false;
        }
        boolean bl = arg.getSource() instanceof PersistentProjectileEntity;
        boolean bl2 = bl && ((PersistentProjectileEntity)arg.getSource()).getPierceLevel() > 0;
        boolean bl3 = "player".equals(arg.getName());
        if (!bl3 && !bl) {
            return false;
        }
        if (arg.getAttacker() instanceof PlayerEntity && !((PlayerEntity)arg.getAttacker()).abilities.allowModifyWorld) {
            return false;
        }
        if (arg.isSourceCreativePlayer()) {
            this.playBreakSound();
            this.spawnBreakParticles();
            this.remove();
            return bl2;
        }
        long l = this.world.getTime();
        if (l - this.lastHitTime <= 5L || bl) {
            this.breakAndDropItem(arg);
            this.spawnBreakParticles();
            this.remove();
        } else {
            this.world.sendEntityStatus(this, (byte)32);
            this.lastHitTime = l;
        }
        return true;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 32) {
            if (this.world.isClient) {
                this.world.playSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_HIT, this.getSoundCategory(), 0.3f, 1.0f, false);
                this.lastHitTime = this.world.getTime();
            }
        } else {
            super.handleStatus(b);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(e) || e == 0.0) {
            e = 4.0;
        }
        return d < (e *= 64.0) * e;
    }

    private void spawnBreakParticles() {
        if (this.world instanceof ServerWorld) {
            ((ServerWorld)this.world).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.getDefaultState()), this.getX(), this.getBodyY(0.6666666666666666), this.getZ(), 10, this.getWidth() / 4.0f, this.getHeight() / 4.0f, this.getWidth() / 4.0f, 0.05);
        }
    }

    private void updateHealth(DamageSource arg, float f) {
        float g = this.getHealth();
        if ((g -= f) <= 0.5f) {
            this.onBreak(arg);
            this.remove();
        } else {
            this.setHealth(g);
        }
    }

    private void breakAndDropItem(DamageSource arg) {
        Block.dropStack(this.world, this.getBlockPos(), new ItemStack(Items.ARMOR_STAND));
        this.onBreak(arg);
    }

    private void onBreak(DamageSource arg) {
        this.playBreakSound();
        this.drop(arg);
        for (int i = 0; i < this.heldItems.size(); ++i) {
            ItemStack lv = this.heldItems.get(i);
            if (lv.isEmpty()) continue;
            Block.dropStack(this.world, this.getBlockPos().up(), lv);
            this.heldItems.set(i, ItemStack.EMPTY);
        }
        for (int j = 0; j < this.armorItems.size(); ++j) {
            ItemStack lv2 = this.armorItems.get(j);
            if (lv2.isEmpty()) continue;
            Block.dropStack(this.world, this.getBlockPos().up(), lv2);
            this.armorItems.set(j, ItemStack.EMPTY);
        }
    }

    private void playBreakSound() {
        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ARMOR_STAND_BREAK, this.getSoundCategory(), 1.0f, 1.0f);
    }

    @Override
    protected float turnHead(float f, float g) {
        this.prevBodyYaw = this.prevYaw;
        this.bodyYaw = this.yaw;
        return 0.0f;
    }

    @Override
    protected float getActiveEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return arg2.height * (this.isBaby() ? 0.5f : 0.9f);
    }

    @Override
    public double getHeightOffset() {
        return this.isMarker() ? 0.0 : (double)0.1f;
    }

    @Override
    public void travel(Vec3d arg) {
        if (!this.canClip()) {
            return;
        }
        super.travel(arg);
    }

    @Override
    public void setYaw(float f) {
        this.prevBodyYaw = this.prevYaw = f;
        this.prevHeadYaw = this.headYaw = f;
    }

    @Override
    public void setHeadYaw(float f) {
        this.prevBodyYaw = this.prevYaw = f;
        this.prevHeadYaw = this.headYaw = f;
    }

    @Override
    public void tick() {
        EulerAngle lv6;
        EulerAngle lv5;
        EulerAngle lv4;
        EulerAngle lv3;
        EulerAngle lv2;
        super.tick();
        EulerAngle lv = this.dataTracker.get(TRACKER_HEAD_ROTATION);
        if (!this.headRotation.equals(lv)) {
            this.setHeadRotation(lv);
        }
        if (!this.bodyRotation.equals(lv2 = this.dataTracker.get(TRACKER_BODY_ROTATION))) {
            this.setBodyRotation(lv2);
        }
        if (!this.leftArmRotation.equals(lv3 = this.dataTracker.get(TRACKER_LEFT_ARM_ROTATION))) {
            this.setLeftArmRotation(lv3);
        }
        if (!this.rightArmRotation.equals(lv4 = this.dataTracker.get(TRACKER_RIGHT_ARM_ROTATION))) {
            this.setRightArmRotation(lv4);
        }
        if (!this.leftLegRotation.equals(lv5 = this.dataTracker.get(TRACKER_LEFT_LEG_ROTATION))) {
            this.setLeftLegRotation(lv5);
        }
        if (!this.rightLegRotation.equals(lv6 = this.dataTracker.get(TRACKER_RIGHT_LEG_ROTATION))) {
            this.setRightLegRotation(lv6);
        }
    }

    @Override
    protected void updatePotionVisibility() {
        this.setInvisible(this.invisible);
    }

    @Override
    public void setInvisible(boolean bl) {
        this.invisible = bl;
        super.setInvisible(bl);
    }

    @Override
    public boolean isBaby() {
        return this.isSmall();
    }

    @Override
    public void kill() {
        this.remove();
    }

    @Override
    public boolean isImmuneToExplosion() {
        return this.isInvisible();
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        if (this.isMarker()) {
            return PistonBehavior.IGNORE;
        }
        return super.getPistonBehavior();
    }

    private void setSmall(boolean bl) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 1, bl));
    }

    public boolean isSmall() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 1) != 0;
    }

    private void setShowArms(boolean bl) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 4, bl));
    }

    public boolean shouldShowArms() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 4) != 0;
    }

    private void setHideBasePlate(boolean bl) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 8, bl));
    }

    public boolean shouldHideBasePlate() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 8) != 0;
    }

    private void setMarker(boolean bl) {
        this.dataTracker.set(ARMOR_STAND_FLAGS, this.setBitField(this.dataTracker.get(ARMOR_STAND_FLAGS), 16, bl));
    }

    public boolean isMarker() {
        return (this.dataTracker.get(ARMOR_STAND_FLAGS) & 0x10) != 0;
    }

    private byte setBitField(byte b, int i, boolean bl) {
        b = bl ? (byte)(b | i) : (byte)(b & ~i);
        return b;
    }

    public void setHeadRotation(EulerAngle arg) {
        this.headRotation = arg;
        this.dataTracker.set(TRACKER_HEAD_ROTATION, arg);
    }

    public void setBodyRotation(EulerAngle arg) {
        this.bodyRotation = arg;
        this.dataTracker.set(TRACKER_BODY_ROTATION, arg);
    }

    public void setLeftArmRotation(EulerAngle arg) {
        this.leftArmRotation = arg;
        this.dataTracker.set(TRACKER_LEFT_ARM_ROTATION, arg);
    }

    public void setRightArmRotation(EulerAngle arg) {
        this.rightArmRotation = arg;
        this.dataTracker.set(TRACKER_RIGHT_ARM_ROTATION, arg);
    }

    public void setLeftLegRotation(EulerAngle arg) {
        this.leftLegRotation = arg;
        this.dataTracker.set(TRACKER_LEFT_LEG_ROTATION, arg);
    }

    public void setRightLegRotation(EulerAngle arg) {
        this.rightLegRotation = arg;
        this.dataTracker.set(TRACKER_RIGHT_LEG_ROTATION, arg);
    }

    public EulerAngle getHeadRotation() {
        return this.headRotation;
    }

    public EulerAngle getBodyRotation() {
        return this.bodyRotation;
    }

    @Environment(value=EnvType.CLIENT)
    public EulerAngle getLeftArmRotation() {
        return this.leftArmRotation;
    }

    @Environment(value=EnvType.CLIENT)
    public EulerAngle getRightArmRotation() {
        return this.rightArmRotation;
    }

    @Environment(value=EnvType.CLIENT)
    public EulerAngle getLeftLegRotation() {
        return this.leftLegRotation;
    }

    @Environment(value=EnvType.CLIENT)
    public EulerAngle getRightLegRotation() {
        return this.rightLegRotation;
    }

    @Override
    public boolean collides() {
        return super.collides() && !this.isMarker();
    }

    @Override
    public boolean handleAttack(Entity arg) {
        return arg instanceof PlayerEntity && !this.world.canPlayerModifyAt((PlayerEntity)arg, this.getBlockPos());
    }

    @Override
    public Arm getMainArm() {
        return Arm.RIGHT;
    }

    @Override
    protected SoundEvent getFallSound(int i) {
        return SoundEvents.ENTITY_ARMOR_STAND_FALL;
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_ARMOR_STAND_HIT;
    }

    @Override
    @Nullable
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ARMOR_STAND_BREAK;
    }

    @Override
    public void onStruckByLightning(ServerWorld arg, LightningEntity arg2) {
    }

    @Override
    public boolean isAffectedBySplashPotions() {
        return false;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        if (ARMOR_STAND_FLAGS.equals(arg)) {
            this.calculateDimensions();
            this.inanimate = !this.isMarker();
        }
        super.onTrackedDataSet(arg);
    }

    @Override
    public boolean isMobOrPlayer() {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(EntityPose arg) {
        float f = this.isMarker() ? 0.0f : (this.isBaby() ? 0.5f : 1.0f);
        return this.getType().getDimensions().scaled(f);
    }
}

