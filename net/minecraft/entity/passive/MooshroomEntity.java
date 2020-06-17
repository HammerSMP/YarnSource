/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity
extends CowEntity
implements Shearable {
    private static final TrackedData<String> TYPE = DataTracker.registerData(MooshroomEntity.class, TrackedDataHandlerRegistry.STRING);
    private StatusEffect stewEffect;
    private int stewEffectDuration;
    private UUID lightningId;

    public MooshroomEntity(EntityType<? extends MooshroomEntity> arg, World arg2) {
        super((EntityType<? extends CowEntity>)arg, arg2);
    }

    @Override
    public float getPathfindingFavor(BlockPos arg, WorldView arg2) {
        if (arg2.getBlockState(arg.down()).isOf(Blocks.MYCELIUM)) {
            return 10.0f;
        }
        return arg2.getBrightness(arg) - 0.5f;
    }

    public static boolean canSpawn(EntityType<MooshroomEntity> arg, WorldAccess arg2, SpawnReason arg3, BlockPos arg4, Random random) {
        return arg2.getBlockState(arg4.down()).isOf(Blocks.MYCELIUM) && arg2.getBaseLightLevel(arg4, 0) > 8;
    }

    @Override
    public void onStruckByLightning(LightningEntity arg) {
        UUID uUID = arg.getUuid();
        if (!uUID.equals(this.lightningId)) {
            this.setType(this.getMooshroomType() == Type.RED ? Type.BROWN : Type.RED);
            this.lightningId = uUID;
            this.playSound(SoundEvents.ENTITY_MOOSHROOM_CONVERT, 2.0f, 1.0f);
        }
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TYPE, Type.RED.name);
    }

    @Override
    public ActionResult interactMob(PlayerEntity arg, Hand arg22) {
        ItemStack lv = arg.getStackInHand(arg22);
        if (lv.getItem() == Items.BOWL && !this.isBaby()) {
            SoundEvent lv6;
            ItemStack lv3;
            boolean bl = false;
            if (this.stewEffect != null) {
                bl = true;
                ItemStack lv2 = new ItemStack(Items.SUSPICIOUS_STEW);
                SuspiciousStewItem.addEffectToStew(lv2, this.stewEffect, this.stewEffectDuration);
                this.stewEffect = null;
                this.stewEffectDuration = 0;
            } else {
                lv3 = new ItemStack(Items.MUSHROOM_STEW);
            }
            ItemStack lv4 = ItemUsage.method_30012(lv, arg, lv3);
            arg.setStackInHand(arg22, lv4);
            if (bl) {
                SoundEvent lv5 = SoundEvents.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
            } else {
                lv6 = SoundEvents.ENTITY_MOOSHROOM_MILK;
            }
            this.playSound(lv6, 1.0f, 1.0f);
            return ActionResult.success(this.world.isClient);
        }
        if (lv.getItem() == Items.SHEARS && this.isShearable()) {
            this.sheared(SoundCategory.PLAYERS);
            if (!this.world.isClient) {
                lv.damage(1, arg, arg2 -> arg2.sendToolBreakStatus(arg22));
            }
            return ActionResult.success(this.world.isClient);
        }
        if (this.getMooshroomType() == Type.BROWN && lv.getItem().isIn(ItemTags.SMALL_FLOWERS)) {
            if (this.stewEffect != null) {
                for (int i = 0; i < 2; ++i) {
                    this.world.addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextDouble() / 2.0, this.getBodyY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }
            } else {
                Optional<Pair<StatusEffect, Integer>> optional = this.getStewEffectFrom(lv);
                if (!optional.isPresent()) {
                    return ActionResult.PASS;
                }
                Pair<StatusEffect, Integer> pair = optional.get();
                if (!arg.abilities.creativeMode) {
                    lv.decrement(1);
                }
                for (int j = 0; j < 4; ++j) {
                    this.world.addParticle(ParticleTypes.EFFECT, this.getX() + this.random.nextDouble() / 2.0, this.getBodyY(0.5), this.getZ() + this.random.nextDouble() / 2.0, 0.0, this.random.nextDouble() / 5.0, 0.0);
                }
                this.stewEffect = (StatusEffect)pair.getLeft();
                this.stewEffectDuration = (Integer)pair.getRight();
                this.playSound(SoundEvents.ENTITY_MOOSHROOM_EAT, 2.0f, 1.0f);
            }
            return ActionResult.success(this.world.isClient);
        }
        return super.interactMob(arg, arg22);
    }

    @Override
    public void sheared(SoundCategory arg) {
        this.world.playSoundFromEntity(null, this, SoundEvents.ENTITY_MOOSHROOM_SHEAR, arg, 1.0f, 1.0f);
        if (!this.world.isClient()) {
            ((ServerWorld)this.world).spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getBodyY(0.5), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            this.remove();
            CowEntity lv = EntityType.COW.create(this.world);
            lv.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, this.pitch);
            lv.setHealth(this.getHealth());
            lv.bodyYaw = this.bodyYaw;
            if (this.hasCustomName()) {
                lv.setCustomName(this.getCustomName());
                lv.setCustomNameVisible(this.isCustomNameVisible());
            }
            if (this.isPersistent()) {
                lv.setPersistent();
            }
            lv.setInvulnerable(this.isInvulnerable());
            this.world.spawnEntity(lv);
            for (int i = 0; i < 5; ++i) {
                this.world.spawnEntity(new ItemEntity(this.world, this.getX(), this.getBodyY(1.0), this.getZ(), new ItemStack(this.getMooshroomType().mushroom.getBlock())));
            }
        }
    }

    @Override
    public boolean isShearable() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putString("Type", this.getMooshroomType().name);
        if (this.stewEffect != null) {
            arg.putByte("EffectId", (byte)StatusEffect.getRawId(this.stewEffect));
            arg.putInt("EffectDuration", this.stewEffectDuration);
        }
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.setType(Type.fromName(arg.getString("Type")));
        if (arg.contains("EffectId", 1)) {
            this.stewEffect = StatusEffect.byRawId(arg.getByte("EffectId"));
        }
        if (arg.contains("EffectDuration", 3)) {
            this.stewEffectDuration = arg.getInt("EffectDuration");
        }
    }

    private Optional<Pair<StatusEffect, Integer>> getStewEffectFrom(ItemStack arg) {
        Block lv2;
        Item lv = arg.getItem();
        if (lv instanceof BlockItem && (lv2 = ((BlockItem)lv).getBlock()) instanceof FlowerBlock) {
            FlowerBlock lv3 = (FlowerBlock)lv2;
            return Optional.of(Pair.of((Object)lv3.getEffectInStew(), (Object)lv3.getEffectInStewDuration()));
        }
        return Optional.empty();
    }

    private void setType(Type arg) {
        this.dataTracker.set(TYPE, arg.name);
    }

    public Type getMooshroomType() {
        return Type.fromName(this.dataTracker.get(MooshroomEntity.TYPE));
    }

    @Override
    public MooshroomEntity createChild(PassiveEntity arg) {
        MooshroomEntity lv = EntityType.MOOSHROOM.create(this.world);
        lv.setType(this.chooseBabyType((MooshroomEntity)arg));
        return lv;
    }

    private Type chooseBabyType(MooshroomEntity arg) {
        Type lv4;
        Type lv2;
        Type lv = this.getMooshroomType();
        if (lv == (lv2 = arg.getMooshroomType()) && this.random.nextInt(1024) == 0) {
            Type lv3 = lv == Type.BROWN ? Type.RED : Type.BROWN;
        } else {
            lv4 = this.random.nextBoolean() ? lv : lv2;
        }
        return lv4;
    }

    @Override
    public /* synthetic */ CowEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    @Override
    public /* synthetic */ PassiveEntity createChild(PassiveEntity arg) {
        return this.createChild(arg);
    }

    public static enum Type {
        RED("red", Blocks.RED_MUSHROOM.getDefaultState()),
        BROWN("brown", Blocks.BROWN_MUSHROOM.getDefaultState());

        private final String name;
        private final BlockState mushroom;

        private Type(String string2, BlockState arg) {
            this.name = string2;
            this.mushroom = arg;
        }

        @Environment(value=EnvType.CLIENT)
        public BlockState getMushroomState() {
            return this.mushroom;
        }

        private static Type fromName(String string) {
            for (Type lv : Type.values()) {
                if (!lv.name.equals(string)) continue;
                return lv;
            }
            return RED;
        }
    }
}

