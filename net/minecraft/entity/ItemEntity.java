/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.tag.FluidTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ItemEntity
extends Entity {
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(ItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private int age;
    private int pickupDelay;
    private int health = 5;
    private UUID thrower;
    private UUID owner;
    public final float hoverHeight = (float)(Math.random() * Math.PI * 2.0);

    public ItemEntity(EntityType<? extends ItemEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public ItemEntity(World arg, double d, double e, double f) {
        this((EntityType<? extends ItemEntity>)EntityType.ITEM, arg);
        this.updatePosition(d, e, f);
        this.yaw = this.random.nextFloat() * 360.0f;
        this.setVelocity(this.random.nextDouble() * 0.2 - 0.1, 0.2, this.random.nextDouble() * 0.2 - 0.1);
    }

    public ItemEntity(World arg, double d, double e, double f, ItemStack arg2) {
        this(arg, d, e, f);
        this.setStack(arg2);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STACK, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
        double d;
        int i;
        if (this.getStack().isEmpty()) {
            this.remove();
            return;
        }
        super.tick();
        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
        }
        this.prevX = this.getX();
        this.prevY = this.getY();
        this.prevZ = this.getZ();
        Vec3d lv = this.getVelocity();
        if (this.isSubmergedIn(FluidTags.WATER)) {
            this.applyBuoyancy();
        } else if (this.isSubmergedIn(FluidTags.LAVA)) {
            this.method_24348();
        } else if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        if (this.world.isClient) {
            this.noClip = false;
        } else {
            boolean bl = this.noClip = !this.world.doesNotCollide(this);
            if (this.noClip) {
                this.pushOutOfBlocks(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
            }
        }
        if (!this.onGround || ItemEntity.squaredHorizontalLength(this.getVelocity()) > (double)1.0E-5f || (this.age + this.getEntityId()) % 4 == 0) {
            this.move(MovementType.SELF, this.getVelocity());
            float f = 0.98f;
            if (this.onGround) {
                f = this.world.getBlockState(new BlockPos(this.getX(), this.getY() - 1.0, this.getZ())).getBlock().getSlipperiness() * 0.98f;
            }
            this.setVelocity(this.getVelocity().multiply(f, 0.98, f));
            if (this.onGround) {
                this.setVelocity(this.getVelocity().multiply(1.0, -0.5, 1.0));
            }
        }
        boolean bl = MathHelper.floor(this.prevX) != MathHelper.floor(this.getX()) || MathHelper.floor(this.prevY) != MathHelper.floor(this.getY()) || MathHelper.floor(this.prevZ) != MathHelper.floor(this.getZ());
        int n = i = bl ? 2 : 40;
        if (this.age % i == 0) {
            if (this.world.getFluidState(this.getBlockPos()).matches(FluidTags.LAVA) && !this.isFireImmune()) {
                this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + this.random.nextFloat() * 0.4f);
            }
            if (!this.world.isClient && this.canMerge()) {
                this.tryMerge();
            }
        }
        if (this.age != -32768) {
            ++this.age;
        }
        this.velocityDirty |= this.updateWaterState();
        if (!this.world.isClient && (d = this.getVelocity().subtract(lv).lengthSquared()) > 0.01) {
            this.velocityDirty = true;
        }
        if (!this.world.isClient && this.age >= 6000) {
            this.remove();
        }
    }

    private void applyBuoyancy() {
        Vec3d lv = this.getVelocity();
        this.setVelocity(lv.x * (double)0.99f, lv.y + (double)(lv.y < (double)0.06f ? 5.0E-4f : 0.0f), lv.z * (double)0.99f);
    }

    private void method_24348() {
        Vec3d lv = this.getVelocity();
        this.setVelocity(lv.x * (double)0.95f, lv.y + (double)(lv.y < (double)0.06f ? 5.0E-4f : 0.0f), lv.z * (double)0.95f);
    }

    private void tryMerge() {
        if (!this.canMerge()) {
            return;
        }
        List<ItemEntity> list = this.world.getEntities(ItemEntity.class, this.getBoundingBox().expand(0.5, 0.0, 0.5), arg -> arg != this && arg.canMerge());
        for (ItemEntity lv : list) {
            if (!lv.canMerge()) continue;
            this.tryMerge(lv);
            if (!this.removed) continue;
            break;
        }
    }

    private boolean canMerge() {
        ItemStack lv = this.getStack();
        return this.isAlive() && this.pickupDelay != 32767 && this.age != -32768 && this.age < 6000 && lv.getCount() < lv.getMaxCount();
    }

    private void tryMerge(ItemEntity arg) {
        ItemStack lv = this.getStack();
        ItemStack lv2 = arg.getStack();
        if (!Objects.equals(this.getOwner(), arg.getOwner()) || !ItemEntity.canMerge(lv, lv2)) {
            return;
        }
        if (lv2.getCount() < lv.getCount()) {
            ItemEntity.merge(this, lv, arg, lv2);
        } else {
            ItemEntity.merge(arg, lv2, this, lv);
        }
    }

    public static boolean canMerge(ItemStack arg, ItemStack arg2) {
        if (arg2.getItem() != arg.getItem()) {
            return false;
        }
        if (arg2.getCount() + arg.getCount() > arg2.getMaxCount()) {
            return false;
        }
        if (arg2.hasTag() ^ arg.hasTag()) {
            return false;
        }
        return !arg2.hasTag() || arg2.getTag().equals(arg.getTag());
    }

    public static ItemStack merge(ItemStack arg, ItemStack arg2, int i) {
        int j = Math.min(Math.min(arg.getMaxCount(), i) - arg.getCount(), arg2.getCount());
        ItemStack lv = arg.copy();
        lv.increment(j);
        arg2.decrement(j);
        return lv;
    }

    private static void merge(ItemEntity arg, ItemStack arg2, ItemStack arg3) {
        ItemStack lv = ItemEntity.merge(arg2, arg3, 64);
        arg.setStack(lv);
    }

    private static void merge(ItemEntity arg, ItemStack arg2, ItemEntity arg3, ItemStack arg4) {
        ItemEntity.merge(arg, arg2, arg4);
        arg.pickupDelay = Math.max(arg.pickupDelay, arg3.pickupDelay);
        arg.age = Math.min(arg.age, arg3.age);
        if (arg4.isEmpty()) {
            arg3.remove();
        }
    }

    @Override
    public boolean isFireImmune() {
        return this.getStack().getItem().isFireproof() || super.isFireImmune();
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        if (!this.getStack().isEmpty() && this.getStack().getItem() == Items.NETHER_STAR && arg.isExplosive()) {
            return false;
        }
        if (!this.getStack().getItem().damage(arg)) {
            return false;
        }
        this.scheduleVelocityUpdate();
        this.health = (int)((float)this.health - f);
        if (this.health <= 0) {
            this.remove();
        }
        return false;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        arg.putShort("Health", (short)this.health);
        arg.putShort("Age", (short)this.age);
        arg.putShort("PickupDelay", (short)this.pickupDelay);
        if (this.getThrower() != null) {
            arg.putUuidNew("Thrower", this.getThrower());
        }
        if (this.getOwner() != null) {
            arg.putUuidNew("Owner", this.getOwner());
        }
        if (!this.getStack().isEmpty()) {
            arg.put("Item", this.getStack().toTag(new CompoundTag()));
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        this.health = arg.getShort("Health");
        this.age = arg.getShort("Age");
        if (arg.contains("PickupDelay")) {
            this.pickupDelay = arg.getShort("PickupDelay");
        }
        if (arg.containsUuidNew("Owner")) {
            this.owner = arg.getUuidNew("Owner");
        }
        if (arg.containsUuidNew("Thrower")) {
            this.thrower = arg.getUuidNew("Thrower");
        }
        CompoundTag lv = arg.getCompound("Item");
        this.setStack(ItemStack.fromTag(lv));
        if (this.getStack().isEmpty()) {
            this.remove();
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity arg) {
        if (this.world.isClient) {
            return;
        }
        ItemStack lv = this.getStack();
        Item lv2 = lv.getItem();
        int i = lv.getCount();
        if (this.pickupDelay == 0 && (this.owner == null || this.owner.equals(arg.getUuid())) && arg.inventory.insertStack(lv)) {
            arg.sendPickup(this, i);
            if (lv.isEmpty()) {
                this.remove();
                lv.setCount(i);
            }
            arg.increaseStat(Stats.PICKED_UP.getOrCreateStat(lv2), i);
        }
    }

    @Override
    public Text getName() {
        Text lv = this.getCustomName();
        if (lv != null) {
            return lv;
        }
        return new TranslatableText(this.getStack().getTranslationKey());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    @Nullable
    public Entity changeDimension(DimensionType arg) {
        Entity lv = super.changeDimension(arg);
        if (!this.world.isClient && lv instanceof ItemEntity) {
            ((ItemEntity)lv).tryMerge();
        }
        return lv;
    }

    public ItemStack getStack() {
        return this.getDataTracker().get(STACK);
    }

    public void setStack(ItemStack arg) {
        this.getDataTracker().set(STACK, arg);
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        super.onTrackedDataSet(arg);
        if (STACK.equals(arg)) {
            this.getStack().setHolder(this);
        }
    }

    @Nullable
    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable UUID uUID) {
        this.owner = uUID;
    }

    @Nullable
    public UUID getThrower() {
        return this.thrower;
    }

    public void setThrower(@Nullable UUID uUID) {
        this.thrower = uUID;
    }

    @Environment(value=EnvType.CLIENT)
    public int getAge() {
        return this.age;
    }

    public void setToDefaultPickupDelay() {
        this.pickupDelay = 10;
    }

    public void resetPickupDelay() {
        this.pickupDelay = 0;
    }

    public void setPickupDelayInfinite() {
        this.pickupDelay = 32767;
    }

    public void setPickupDelay(int i) {
        this.pickupDelay = i;
    }

    public boolean cannotPickup() {
        return this.pickupDelay > 0;
    }

    public void setCovetedItem() {
        this.age = -6000;
    }

    public void setDespawnImmediately() {
        this.setPickupDelayInfinite();
        this.age = 5999;
    }

    @Environment(value=EnvType.CLIENT)
    public float method_27314(float f) {
        return ((float)this.getAge() + f) / 20.0f + this.hoverHeight;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

