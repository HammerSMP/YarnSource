/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TridentEntity
extends PersistentProjectileEntity {
    private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Boolean> ENCHANTED = DataTracker.registerData(TridentEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private ItemStack tridentStack = new ItemStack(Items.TRIDENT);
    private boolean dealtDamage;
    public int returnTimer;

    public TridentEntity(EntityType<? extends TridentEntity> arg, World arg2) {
        super((EntityType<? extends PersistentProjectileEntity>)arg, arg2);
    }

    public TridentEntity(World world, LivingEntity owner, ItemStack stack) {
        super(EntityType.TRIDENT, owner, world);
        this.tridentStack = stack.copy();
        this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(stack));
        this.dataTracker.set(ENCHANTED, stack.hasGlint());
    }

    @Environment(value=EnvType.CLIENT)
    public TridentEntity(World world, double x, double y, double z) {
        super(EntityType.TRIDENT, x, y, z, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LOYALTY, (byte)0);
        this.dataTracker.startTracking(ENCHANTED, false);
    }

    @Override
    public void tick() {
        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }
        Entity lv = this.getOwner();
        if ((this.dealtDamage || this.isNoClip()) && lv != null) {
            byte i = this.dataTracker.get(LOYALTY);
            if (i > 0 && !this.isOwnerAlive()) {
                if (!this.world.isClient && this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(this.asItemStack(), 0.1f);
                }
                this.remove();
            } else if (i > 0) {
                this.setNoClip(true);
                Vec3d lv2 = new Vec3d(lv.getX() - this.getX(), lv.getEyeY() - this.getY(), lv.getZ() - this.getZ());
                this.setPos(this.getX(), this.getY() + lv2.y * 0.015 * (double)i, this.getZ());
                if (this.world.isClient) {
                    this.lastRenderY = this.getY();
                }
                double d = 0.05 * (double)i;
                this.setVelocity(this.getVelocity().multiply(0.95).add(lv2.normalize().multiply(d)));
                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10.0f, 1.0f);
                }
                ++this.returnTimer;
            }
        }
        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity lv = this.getOwner();
        if (lv == null || !lv.isAlive()) {
            return false;
        }
        return !(lv instanceof ServerPlayerEntity) || !lv.isSpectator();
    }

    @Override
    protected ItemStack asItemStack() {
        return this.tridentStack.copy();
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isEnchanted() {
        return this.dataTracker.get(ENCHANTED);
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        if (this.dealtDamage) {
            return null;
        }
        return super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        BlockPos lv7;
        Entity lv3;
        Entity lv = entityHitResult.getEntity();
        float f = 8.0f;
        if (lv instanceof LivingEntity) {
            LivingEntity lv2 = (LivingEntity)lv;
            f += EnchantmentHelper.getAttackDamage(this.tridentStack, lv2.getGroup());
        }
        DamageSource lv4 = DamageSource.trident(this, (lv3 = this.getOwner()) == null ? this : lv3);
        this.dealtDamage = true;
        SoundEvent lv5 = SoundEvents.ITEM_TRIDENT_HIT;
        if (lv.damage(lv4, f)) {
            if (lv.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (lv instanceof LivingEntity) {
                LivingEntity lv6 = (LivingEntity)lv;
                if (lv3 instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged(lv6, lv3);
                    EnchantmentHelper.onTargetDamaged((LivingEntity)lv3, lv6);
                }
                this.onHit(lv6);
            }
        }
        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float g = 1.0f;
        if (this.world instanceof ServerWorld && this.world.isThundering() && EnchantmentHelper.hasChanneling(this.tridentStack) && this.world.isSkyVisible(lv7 = lv.getBlockPos())) {
            LightningEntity lv8 = EntityType.LIGHTNING_BOLT.create(this.world);
            lv8.method_29495(Vec3d.ofBottomCenter(lv7));
            lv8.setChanneler(lv3 instanceof ServerPlayerEntity ? (ServerPlayerEntity)lv3 : null);
            this.world.spawnEntity(lv8);
            lv5 = SoundEvents.ITEM_TRIDENT_THUNDER;
            g = 5.0f;
        }
        this.playSound(lv5, g, 1.0f);
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        Entity lv = this.getOwner();
        if (lv != null && lv.getUuid() != player.getUuid()) {
            return;
        }
        super.onPlayerCollision(player);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        if (tag.contains("Trident", 10)) {
            this.tridentStack = ItemStack.fromTag(tag.getCompound("Trident"));
        }
        this.dealtDamage = tag.getBoolean("DealtDamage");
        this.dataTracker.set(LOYALTY, (byte)EnchantmentHelper.getLoyalty(this.tridentStack));
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.put("Trident", this.tridentStack.toTag(new CompoundTag()));
        tag.putBoolean("DealtDamage", this.dealtDamage);
    }

    @Override
    public void age() {
        byte i = this.dataTracker.get(LOYALTY);
        if (this.pickupType != PersistentProjectileEntity.PickupPermission.ALLOWED || i <= 0) {
            super.age();
        }
    }

    @Override
    protected float getDragInWater() {
        return 0.99f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}

