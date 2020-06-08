/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public abstract class AnimalEntity
extends PassiveEntity {
    private int loveTicks;
    private UUID lovingPlayer;

    protected AnimalEntity(EntityType<? extends AnimalEntity> arg, World arg2) {
        super((EntityType<? extends PassiveEntity>)arg, arg2);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 16.0f);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0f);
    }

    @Override
    protected void mobTick() {
        if (this.getBreedingAge() != 0) {
            this.loveTicks = 0;
        }
        super.mobTick();
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.getBreedingAge() != 0) {
            this.loveTicks = 0;
        }
        if (this.loveTicks > 0) {
            --this.loveTicks;
            if (this.loveTicks % 10 == 0) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.world.addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
            }
        }
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        this.loveTicks = 0;
        return super.damage(arg, f);
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        if (arg2.getBlockState(arg.down()).isOf(Blocks.GRASS_BLOCK)) {
            return 10.0f;
        }
        return arg2.getBrightness(arg) - 0.5f;
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("InLove", this.loveTicks);
        if (this.lovingPlayer != null) {
            arg.putUuid("LoveCause", this.lovingPlayer);
        }
    }

    @Override
    public double getHeightOffset() {
        return 0.14;
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.loveTicks = arg.getInt("InLove");
        this.lovingPlayer = arg.containsUuid("LoveCause") ? arg.getUuid("LoveCause") : null;
    }

    public static boolean isValidNaturalSpawn(EntityType<? extends AnimalEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return arg2.getBlockState(arg4.down()).isOf(Blocks.GRASS_BLOCK) && arg2.getBaseLightLevel(arg4, 0) > 8;
    }

    @Override
    public int getMinAmbientSoundDelay() {
        return 120;
    }

    @Override
    public boolean canImmediatelyDespawn(double d) {
        return false;
    }

    @Override
    protected int getCurrentExperience(PlayerEntity arg) {
        return 1 + this.world.random.nextInt(3);
    }

    public boolean isBreedingItem(ItemStack arg) {
        return arg.getItem() == Items.WHEAT;
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg2) {
        ItemStack lv = arg.getStackInHand(arg2);
        if (this.isBreedingItem(lv)) {
            int i = this.getBreedingAge();
            if (!this.world.isClient && i == 0 && this.canEat()) {
                this.eat(arg, lv);
                this.lovePlayer(arg);
                return ActionResult.SUCCESS;
            }
            if (this.isBaby()) {
                this.eat(arg, lv);
                this.growUp((int)((float)(-i / 20) * 0.1f), true);
                return ActionResult.success(this.world.isClient);
            }
            if (this.world.isClient) {
                return ActionResult.CONSUME;
            }
        }
        return super.interactMob(arg, arg2);
    }

    protected void eat(PlayerEntity arg, ItemStack arg2) {
        if (!arg.abilities.creativeMode) {
            arg2.decrement(1);
        }
    }

    public boolean canEat() {
        return this.loveTicks <= 0;
    }

    public void lovePlayer(@Nullable PlayerEntity arg) {
        this.loveTicks = 600;
        if (arg != null) {
            this.lovingPlayer = arg.getUuid();
        }
        this.world.sendEntityStatus(this, (byte)18);
    }

    public void setLoveTicks(int i) {
        this.loveTicks = i;
    }

    public int getLoveTicks() {
        return this.loveTicks;
    }

    @Nullable
    public ServerPlayerEntity getLovingPlayer() {
        if (this.lovingPlayer == null) {
            return null;
        }
        PlayerEntity lv = this.world.getPlayerByUuid(this.lovingPlayer);
        if (lv instanceof ServerPlayerEntity) {
            return (ServerPlayerEntity)lv;
        }
        return null;
    }

    public boolean isInLove() {
        return this.loveTicks > 0;
    }

    public void resetLoveTicks() {
        this.loveTicks = 0;
    }

    public boolean canBreedWith(AnimalEntity arg) {
        if (arg == this) {
            return false;
        }
        if (arg.getClass() != this.getClass()) {
            return false;
        }
        return this.isInLove() && arg.isInLove();
    }

    public void breed(World arg, AnimalEntity arg2) {
        PassiveEntity lv = this.createChild(arg2);
        if (lv == null) {
            return;
        }
        ServerPlayerEntity lv2 = this.getLovingPlayer();
        if (lv2 == null && arg2.getLovingPlayer() != null) {
            lv2 = arg2.getLovingPlayer();
        }
        if (lv2 != null) {
            lv2.incrementStat(Stats.ANIMALS_BRED);
            Criteria.BRED_ANIMALS.trigger(lv2, this, arg2, lv);
        }
        this.setBreedingAge(6000);
        arg2.setBreedingAge(6000);
        this.resetLoveTicks();
        arg2.resetLoveTicks();
        lv.setBaby(true);
        lv.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), 0.0f, 0.0f);
        arg.spawnEntity(lv);
        arg.sendEntityStatus(this, (byte)18);
        if (arg.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            arg.spawnEntity(new ExperienceOrbEntity(arg, this.getX(), this.getY(), this.getZ(), this.getRandom().nextInt(7) + 1));
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
        if (b == 18) {
            for (int i = 0; i < 7; ++i) {
                double d = this.random.nextGaussian() * 0.02;
                double e = this.random.nextGaussian() * 0.02;
                double f = this.random.nextGaussian() * 0.02;
                this.world.addParticle(ParticleTypes.HEART, this.getParticleX(1.0), this.getRandomBodyY() + 0.5, this.getParticleZ(1.0), d, e, f);
            }
        } else {
            super.handleStatus(b);
        }
    }
}

