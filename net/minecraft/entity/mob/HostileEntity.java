/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.mob;

import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public abstract class HostileEntity
extends MobEntityWithAi
implements Monster {
    protected HostileEntity(EntityType<? extends HostileEntity> arg, World arg2) {
        super((EntityType<? extends MobEntityWithAi>)arg, arg2);
        this.experiencePoints = 5;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public void tickMovement() {
        this.tickHandSwing();
        this.updateDespawnCounter();
        super.tickMovement();
    }

    protected void updateDespawnCounter() {
        float f = this.getBrightnessAtEyes();
        if (f > 0.5f) {
            this.despawnCounter += 2;
        }
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected SoundEvent getSplashSound() {
        return SoundEvents.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        return super.damage(arg, f);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource arg) {
        return SoundEvents.ENTITY_HOSTILE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_HOSTILE_DEATH;
    }

    @Override
    protected SoundEvent getFallSound(int i) {
        if (i > 4) {
            return SoundEvents.ENTITY_HOSTILE_BIG_FALL;
        }
        return SoundEvents.ENTITY_HOSTILE_SMALL_FALL;
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        return 0.5f - arg2.getBrightness(arg);
    }

    public static boolean isSpawnDark(IWorld arg, BlockPos arg2, Random random) {
        if (arg.getLightLevel(LightType.SKY, arg2) > random.nextInt(32)) {
            return false;
        }
        int i = arg.getWorld().isThundering() ? arg.getLightLevel(arg2, 10) : arg.getLightLevel(arg2);
        return i <= random.nextInt(8);
    }

    public static boolean canSpawnInDark(EntityType<? extends HostileEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        return arg2.getDifficulty() != Difficulty.PEACEFUL && HostileEntity.isSpawnDark(arg2, arg4, random) && HostileEntity.canMobSpawn(arg, arg2, arg3, arg4, random);
    }

    public static boolean canSpawnIgnoreLightLevel(EntityType<? extends HostileEntity> arg, IWorld arg2, SpawnType arg3, BlockPos arg4, Random random) {
        return arg2.getDifficulty() != Difficulty.PEACEFUL && HostileEntity.canMobSpawn(arg, arg2, arg3, arg4, random);
    }

    public static DefaultAttributeContainer.Builder createHostileAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    protected boolean canDropLootAndXp() {
        return true;
    }

    @Override
    protected boolean method_27071() {
        return true;
    }

    public boolean isAngryAt(PlayerEntity arg) {
        return true;
    }

    @Override
    public ItemStack getArrowType(ItemStack arg) {
        if (arg.getItem() instanceof RangedWeaponItem) {
            Predicate<ItemStack> predicate = ((RangedWeaponItem)arg.getItem()).getHeldProjectiles();
            ItemStack lv = RangedWeaponItem.getHeldProjectile(this, predicate);
            return lv.isEmpty() ? new ItemStack(Items.ARROW) : lv;
        }
        return ItemStack.EMPTY;
    }
}

