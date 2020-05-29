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

    public void angerBees(@Nullable PlayerEntity arg, BlockState arg2, BeeState arg3) {
        List<Entity> list = this.tryReleaseBee(arg2, arg3);
        if (arg != null) {
            for (Entity lv : list) {
                if (!(lv instanceof BeeEntity)) continue;
                BeeEntity lv2 = (BeeEntity)lv;
                if (!(arg.getPos().squaredDistanceTo(lv.getPos()) <= 16.0)) continue;
                if (!this.isSmoked()) {
                    lv2.setBeeAttacker(arg);
                    continue;
                }
                lv2.setCannotEnterHiveTicks(400);
            }
        }
    }

    private List<Entity> tryReleaseBee(BlockState arg, BeeState arg2) {
        ArrayList list = Lists.newArrayList();
        this.bees.removeIf(arg3 -> this.releaseBee(arg, (Bee)arg3, list, arg2));
        return list;
    }

    public void tryEnterHive(Entity arg, boolean bl) {
        this.tryEnterHive(arg, bl, 0);
    }

    public int getBeeCount() {
        return this.bees.size();
    }

    public static int getHoneyLevel(BlockState arg) {
        return arg.get(BeehiveBlock.HONEY_LEVEL);
    }

    public boolean isSmoked() {
        return CampfireBlock.isLitCampfireInRange(this.world, this.getPos());
    }

    protected void sendDebugData() {
        DebugInfoSender.sendBeehiveDebugData(this);
    }

    public void tryEnterHive(Entity arg, boolean bl, int i) {
        if (this.bees.size() >= 3) {
            return;
        }
        arg.stopRiding();
        arg.removeAllPassengers();
        CompoundTag lv = new CompoundTag();
        arg.saveToTag(lv);
        this.bees.add(new Bee(lv, i, bl ? 2400 : 600));
        if (this.world != null) {
            BeeEntity lv2;
            if (arg instanceof BeeEntity && (lv2 = (BeeEntity)arg).hasFlower() && (!this.hasFlowerPos() || this.world.random.nextBoolean())) {
                this.flowerPos = lv2.getFlowerPos();
            }
            BlockPos lv3 = this.getPos();
            this.world.playSound(null, (double)lv3.getX(), (double)lv3.getY(), lv3.getZ(), SoundEvents.BLOCK_BEEHIVE_ENTER, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        arg.remove();
    }

    private boolean releaseBee(BlockState arg2, Bee arg22, @Nullable List<Entity> list, BeeState arg3) {
        boolean bl;
        if ((this.world.isNight() || this.world.isRaining()) && arg3 != BeeState.EMERGENCY) {
            return false;
        }
        BlockPos lv = this.getPos();
        CompoundTag lv2 = arg22.entityData;
        lv2.remove("Passengers");
        lv2.remove("Leash");
        lv2.remove("UUID");
        Direction lv3 = arg2.get(BeehiveBlock.FACING);
        BlockPos lv4 = lv.offset(lv3);
        boolean bl2 = bl = !this.world.getBlockState(lv4).getCollisionShape(this.world, lv4).isEmpty();
        if (bl && arg3 != BeeState.EMERGENCY) {
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
                if (arg3 == BeeState.HONEY_DELIVERED) {
                    int i;
                    lv6.onHoneyDelivered();
                    if (arg2.getBlock().isIn(BlockTags.BEEHIVES) && (i = BeehiveBlockEntity.getHoneyLevel(arg2)) < 5) {
                        int j;
                        int n = j = this.world.random.nextInt(100) == 0 ? 2 : 1;
                        if (i + j > 5) {
                            --j;
                        }
                        this.world.setBlockState(this.getPos(), (BlockState)arg2.with(BeehiveBlock.HONEY_LEVEL, i + j));
                    }
                }
                int k = arg22.ticksInHive;
                lv6.growUp(k);
                lv6.setLoveTicks(Math.max(0, lv6.method_29270() - k));
                lv6.resetPollinationTicks();
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

    private boolean hasFlowerPos() {
        return this.flowerPos != null;
    }

    private void tickBees() {
        Iterator<Bee> iterator = this.bees.iterator();
        BlockState lv = this.getCachedState();
        while (iterator.hasNext()) {
            Bee lv2 = iterator.next();
            if (lv2.ticksInHive > lv2.minOccupationTIcks) {
                BeeState lv3 = lv2.entityData.getBoolean("HasNectar") ? BeeState.HONEY_DELIVERED : BeeState.BEE_RELEASED;
                if (!this.releaseBee(lv, lv2, null, lv3)) continue;
                iterator.remove();
                continue;
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
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.bees.clear();
        ListTag lv = arg2.getList("Bees", 10);
        for (int i = 0; i < lv.size(); ++i) {
            CompoundTag lv2 = lv.getCompound(i);
            Bee lv3 = new Bee(lv2.getCompound("EntityData"), lv2.getInt("TicksInHive"), lv2.getInt("MinOccupationTicks"));
            this.bees.add(lv3);
        }
        this.flowerPos = null;
        if (arg2.contains("FlowerPos")) {
            this.flowerPos = NbtHelper.toBlockPos(arg2.getCompound("FlowerPos"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        arg.put("Bees", this.getBees());
        if (this.hasFlowerPos()) {
            arg.put("FlowerPos", NbtHelper.fromBlockPos(this.flowerPos));
        }
        return arg;
    }

    public ListTag getBees() {
        ListTag lv = new ListTag();
        for (Bee lv2 : this.bees) {
            lv2.entityData.remove("UUID");
            CompoundTag lv3 = new CompoundTag();
            lv3.put("EntityData", lv2.entityData);
            lv3.putInt("TicksInHive", lv2.ticksInHive);
            lv3.putInt("MinOccupationTicks", lv2.minOccupationTIcks);
            lv.add(lv3);
        }
        return lv;
    }

    static class Bee {
        private final CompoundTag entityData;
        private int ticksInHive;
        private final int minOccupationTIcks;

        private Bee(CompoundTag arg, int i, int j) {
            arg.remove("UUID");
            this.entityData = arg;
            this.ticksInHive = i;
            this.minOccupationTIcks = j;
        }
    }

    public static enum BeeState {
        HONEY_DELIVERED,
        BEE_RELEASED,
        EMERGENCY;

    }
}

