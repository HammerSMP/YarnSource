/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class SpawnEggItem
extends Item {
    private static final Map<EntityType<?>, SpawnEggItem> SPAWN_EGGS = Maps.newIdentityHashMap();
    private final int primaryColor;
    private final int secondaryColor;
    private final EntityType<?> type;

    public SpawnEggItem(EntityType<?> type, int primaryColor, int secondaryColor, Item.Settings settings) {
        super(settings);
        this.type = type;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        SPAWN_EGGS.put(type, this);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos lv10;
        BlockEntity lv6;
        World lv = context.getWorld();
        if (!(lv instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        }
        ItemStack lv2 = context.getStack();
        BlockPos lv3 = context.getBlockPos();
        Direction lv4 = context.getSide();
        BlockState lv5 = lv.getBlockState(lv3);
        if (lv5.isOf(Blocks.SPAWNER) && (lv6 = lv.getBlockEntity(lv3)) instanceof MobSpawnerBlockEntity) {
            MobSpawnerLogic lv7 = ((MobSpawnerBlockEntity)lv6).getLogic();
            EntityType<?> lv8 = this.getEntityType(lv2.getTag());
            lv7.setEntityId(lv8);
            lv6.markDirty();
            lv.updateListeners(lv3, lv5, lv5, 3);
            lv2.decrement(1);
            return ActionResult.CONSUME;
        }
        if (lv5.getCollisionShape(lv, lv3).isEmpty()) {
            BlockPos lv9 = lv3;
        } else {
            lv10 = lv3.offset(lv4);
        }
        EntityType<?> lv11 = this.getEntityType(lv2.getTag());
        if (lv11.spawnFromItemStack((ServerWorld)lv, lv2, context.getPlayer(), lv10, SpawnReason.SPAWN_EGG, true, !Objects.equals(lv3, lv10) && lv4 == Direction.UP) != null) {
            lv2.decrement(1);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        BlockHitResult lv2 = SpawnEggItem.rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (((HitResult)lv2).getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(lv);
        }
        if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(lv);
        }
        BlockHitResult lv3 = lv2;
        BlockPos lv4 = lv3.getBlockPos();
        if (!(world.getBlockState(lv4).getBlock() instanceof FluidBlock)) {
            return TypedActionResult.pass(lv);
        }
        if (!world.canPlayerModifyAt(user, lv4) || !user.canPlaceOn(lv4, lv3.getSide(), lv)) {
            return TypedActionResult.fail(lv);
        }
        EntityType<?> lv5 = this.getEntityType(lv.getTag());
        if (lv5.spawnFromItemStack((ServerWorld)world, lv, user, lv4, SpawnReason.SPAWN_EGG, false, false) == null) {
            return TypedActionResult.pass(lv);
        }
        if (!user.abilities.creativeMode) {
            lv.decrement(1);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        return TypedActionResult.consume(lv);
    }

    public boolean isOfSameEntityType(@Nullable CompoundTag tag, EntityType<?> type) {
        return Objects.equals(this.getEntityType(tag), type);
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor(int num) {
        return num == 0 ? this.primaryColor : this.secondaryColor;
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public static SpawnEggItem forEntity(@Nullable EntityType<?> type) {
        return SPAWN_EGGS.get(type);
    }

    public static Iterable<SpawnEggItem> getAll() {
        return Iterables.unmodifiableIterable(SPAWN_EGGS.values());
    }

    public EntityType<?> getEntityType(@Nullable CompoundTag tag) {
        CompoundTag lv;
        if (tag != null && tag.contains("EntityTag", 10) && (lv = tag.getCompound("EntityTag")).contains("id", 8)) {
            return EntityType.get(lv.getString("id")).orElse(this.type);
        }
        return this.type;
    }

    public Optional<MobEntity> spawnBaby(PlayerEntity user, MobEntity arg2, EntityType<? extends MobEntity> arg3, ServerWorld arg4, Vec3d arg5, ItemStack arg6) {
        MobEntity lv2;
        if (!this.isOfSameEntityType(arg6.getTag(), arg3)) {
            return Optional.empty();
        }
        if (arg2 instanceof PassiveEntity) {
            PassiveEntity lv = ((PassiveEntity)arg2).createChild(arg4, (PassiveEntity)arg2);
        } else {
            lv2 = arg3.create(arg4);
        }
        if (lv2 == null) {
            return Optional.empty();
        }
        lv2.setBaby(true);
        if (!lv2.isBaby()) {
            return Optional.empty();
        }
        lv2.refreshPositionAndAngles(arg5.getX(), arg5.getY(), arg5.getZ(), 0.0f, 0.0f);
        arg4.spawnEntity(lv2);
        if (arg6.hasCustomName()) {
            lv2.setCustomName(arg6.getName());
        }
        if (!user.abilities.creativeMode) {
            arg6.decrement(1);
        }
        return Optional.of(lv2);
    }
}

