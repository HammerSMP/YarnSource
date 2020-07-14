/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class BeehiveBlockEntity
extends BlockEntity
implements Tickable {
    private final List<Bee> bees = Lists.newArrayList();
    @Nullable
    private BlockPos flowerPos = null;

    public BeehiveBlockEntity() {
        super(BlockEntityType.BEEHIVE);
    }

    @Override
    public void markDirty() {
        if (this.isNearFire()) {
            this.angerBees(null, this.world.getBlockState(this.getPos()), BeeState.EMERGENCY);
        }
        super.markDirty();
    }

    public boolean isNearFire() {
        if (this.world == null) {
            return false;
        }
        for (BlockPos lv : BlockPos.iterate(this.pos.add(-1, -1, -1), this.pos.add(1, 1, 1))) {
            if (!(this.world.getBlockState(lv).getBlock() instanceof FireBlock)) continue;
            return true;
        }
        return false;
    }

    public boolean hasNoBees() {
        return this.bees.isEmpty();
    }

    public boolean isFullOfBees() {
        return this.bees.size() == 3;
    }

    public void angerBees(@Nullable PlayerEntity player, BlockState state, BeeState arg3) {
        List<Entity> list = this.tryReleaseBee(state, arg3);
        if (player != null) {
            for (Entity lv : list) {
                if (!(lv instanceof BeeEntity)) continue;
                BeeEntity lv2 = (BeeEntity)lv;
                if (!(player.getPos().squaredDistanceTo(lv.getPos()) <= 16.0)) continue;
                if (!this.isSmoked()) {
                    lv2.setTarget(player);
                    continue;
                }
                lv2.setCannotEnterHiveTicks(400);
            }
        }
    }

    private List<Entity> tryReleaseBee(BlockState state, BeeState arg2) {
        ArrayList list = Lists.newArrayList();
        this.bees.removeIf(bee -> this.releaseBee(state, (Bee)bee, list, arg2));
        return list;
    }

    public void tryEnterHive(Entity entity, boolean hasNectar) {
        this.tryEnterHive(entity, hasNectar, 0);
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    public static int getHoneyLevel(BlockState state) {
        return state.get(BeehiveBlock.HONEY_LEVEL);
    }

    public boolean isSmoked() {
        return CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
    }

    protected void sendDebugData() {
        DebugInfoSender.sendBeehiveDebugData(this);
    }

    public void tryEnterHive(Entity entity, boolean hasNectar, int ticksInHive) {
        if (this.bees.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.removeAllPassengers();
        CompoundTag lv = new CompoundTag();
        entity.saveToTag(lv);
        this.bees.add(new Bee(lv, ticksInHive, hasNectar ? 2400 : 600));
        if (this.world != null) {
            BeeEntity lv2;
            if (entity instanceof BeeEntity && (lv2 = (BeeEntity)entity).hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
                this.flowerPos = lv2.getFlowerPos();
            }
            BlockPos lv3 = this.getPos();
            this.world.playSound(null, (double)lv3.getX(), (double)lv3.getY(), lv3.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        entity.remove();
    }

    private boolean releaseBee(BlockState state, Bee bee, @Nullable List<Entity> list, BeeState beeState) {
        boolean bl;
        if ((this.world.isNight() || this.world.isRaining()) && beeState != BeeState.EMERGENCY) {
            return false;
        }
        BlockPos lv = this.getPos();
        CompoundTag lv2 = bee.entityData;
        lv2.remove("Passengers");
        lv2.remove("Leash");
        lv2.remove("UUID");
        Direction lv3 = state.get(BeehiveBlock.FACING);
        BlockPos lv4 = lv.offset(lv3);
        boolean bl2 = bl = !this.world.getBlockState(lv4).getCollisionShape(this.world, lv4).isEmpty();
        if (bl && beeState != BeeState.EMERGENCY) {
            return false;
        }
        Entity lv5 = EntityType.loadEntityWithPassengers(lv2, this.world, arg -> arg);
        if (lv5 != null) {
            if (!lv5.getType().isIn(EntityTypeTags.BEEHIVE_INHABITORS)) {
                return false;
            }
            if (lv5 instanceof BeeEntity) {
                BeeEntity lv6 = (BeeEntity)lv5;
                if (this.hasFlowerPos() && !lv6.hasFlower() && this.world.random.nextFloat() < 0.9f) {
                    lv6.setFlowerPos(this.flowerPos);
                }
                if (beeState == BeeState.HONEY_DELIVERED) {
                    int i;
                    lv6.onHoneyDelivered();
                    if (state.getBlock().isIn(BlockTags.BEEHIVES) && (i = BeehiveBlockEntity.getHoneyLevel(state)) < 5) {
                        int j;
                        int n = j = this.world.random.nextInt(100) == 0 ? 2 : 1;
                        if (i + j > 5) {
                            --j;
                        }
                        this.world.setBlockState(this.getPos(), (BlockState)state.with(BeehiveBlock.HONEY_LEVEL, i + j));
                    }
                }
                this.ageBee(bee.ticksInHive, lv6);
                if (list != null) {
                    list.add(lv6);
                }
                float f = lv5.getWidth();
                double d = bl ? 0.0 : 0.55 + (double)(f / 2.0f);
                double e = (double)lv.getX() + 0.5 + d * (double)lv3.getOffsetX();
                double g = (double)lv.getY() + 0.5 - (double)(lv5.getHeight() / 2.0f);
                double h = (double)lv.getZ() + 0.5 + d * (double)lv3.getOffsetZ();
                lv5.refreshPositionAndAngles(e, g, h, lv5.yaw, lv5.pitch);
            }
            this.world.playSound(null, lv, SoundEvents.BLOCK_BEEHIVE_EXIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            return this.world.spawnEntity(lv5);
        }
        return false;
    }

    private void ageBee(int ticks, BeeEntity bee) {
        int j = bee.getBreedingAge();
        if (j < 0) {
            bee.setBreedingAge(Math.min(0, j + ticks));
        } else if (j > 0) {
            bee.setBreedingAge(Math.max(0, j - ticks));
        }
        bee.setLoveTicks(Math.max(0, bee.getLoveTicks() - ticks));
        bee.resetPollinationTicks();
    }

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    private void tickBees() {
        Iterator<Bee> iterator = this.bees.iterator();
        BlockState lv = this.getCachedState();
        while (iterator.hasNext()) {
            Bee lv2 = iterator.next();
            if (lv2.ticksInHive > lv2.minOccupationTicks) {
                BeeState lv3;
                BeeState beeState = lv3 = lv2.entityData.getBoolean("HasNectar") ? BeeState.HONEY_DELIVERED : BeeState.BEE_RELEASED;
                if (this.releaseBee(lv, lv2, null, lv3)) {
                    iterator.remove();
                }
            }
            lv2.ticksInHive++;
        }
    }

    @Override
    public void tick() {
        if (this.world.isClient) {
            return;
        }
        this.tickBees();
        BlockPos lv = this.getPos();
        if (this.bees.size() > 0 && this.world.getRandom().nextDouble() < 0.005) {
            double d = (double)lv.getX() + 0.5;
            double e = lv.getY();
            double f = (double)lv.getZ() + 0.5;
            this.world.playSound(null, d, e, f, SoundEvents.BLOCK_BEEHIVE_WORK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        this.sendDebugData();
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.bees.clear();
        ListTag lv = tag.getList("Bees", 10);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv2 = lv.getCompound(i);
            Bee lv3 = new Bee(lv2.getCompound("EntityData"), lv2.getInt("TicksInHive"), lv2.getInt("MinOccupationTicks"));
            this.bees.add(lv3);
        }
        this.flowerPos = null;
        if (tag.contains("FlowerPos")) {
            this.flowerPos = NbtHelper.toBlockPos(tag.getCompound("FlowerPos"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Bees", this.getBees());
        if (this.hasFlowerPos()) {
            tag.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
        }
        return tag;
    }

    public ListTag getBees() {
        ListTag lv = new ListTag();
        for (Bee lv2 : this.bees) {
            lv2.entityData.remove("UUID");
            CompoundTag lv3 = new CompoundTag();
            lv3.put("EntityData", lv2.entityData);
            lv3.putInt("TicksInHive", lv2.ticksInHive);
            lv3.putInt("MinOccupationTicks", lv2.minOccupationTicks);
            lv.add(lv3);
        }
        return lv;
    }

    static class Bee {
        private final CompoundTag entityData;
        private int ticksInHive;
        private final int minOccupationTicks;

        private Bee(CompoundTag entityData, int ticksInHive, int minOccupationTicks) {
            entityData.remove("UUID");
            this.entityData = entityData;
            this.ticksInHive = ticksInHive;
            this.minOccupationTicks = minOccupationTicks;
        }
    }

    public static enum BeeState {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;

    }
}

