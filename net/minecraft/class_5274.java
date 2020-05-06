/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;

public class class_5274
extends WanderAroundGoal {
    public class_5274(MobEntityWithAi arg, double d) {
        super(arg, d, 240, false);
    }

    @Override
    @Nullable
    protected Vec3d getWanderTarget() {
        Vec3d lv2;
        float f = this.mob.world.random.nextFloat();
        if (this.mob.world.random.nextFloat() < 0.3f) {
            return this.method_27925();
        }
        if (f < 0.7f) {
            Vec3d lv = this.method_27926();
            if (lv == null) {
                lv = this.method_27927();
            }
        } else {
            lv2 = this.method_27927();
            if (lv2 == null) {
                lv2 = this.method_27926();
            }
        }
        return lv2 == null ? this.method_27925() : lv2;
    }

    @Nullable
    private Vec3d method_27925() {
        return TargetFinder.findGroundTarget(this.mob, 10, 7);
    }

    @Nullable
    private Vec3d method_27926() {
        ServerWorld lv = (ServerWorld)this.mob.world;
        List<VillagerEntity> list = lv.getEntities(EntityType.VILLAGER, this.mob.getBoundingBox().expand(32.0), this::method_27922);
        if (list.isEmpty()) {
            return null;
        }
        VillagerEntity lv2 = list.get(this.mob.world.random.nextInt(list.size()));
        Vec3d lv3 = lv2.getPos();
        return TargetFinder.method_27929(this.mob, 10, 7, lv3);
    }

    @Nullable
    private Vec3d method_27927() {
        ChunkSectionPos lv = this.method_27928();
        if (lv == null) {
            return null;
        }
        BlockPos lv2 = this.method_27923(lv);
        if (lv2 == null) {
            return null;
        }
        return TargetFinder.method_27929(this.mob, 10, 7, Vec3d.method_24955(lv2));
    }

    @Nullable
    private ChunkSectionPos method_27928() {
        ServerWorld lv = (ServerWorld)this.mob.world;
        List list = ChunkSectionPos.stream(ChunkSectionPos.from(this.mob), 2).filter(arg2 -> lv.getOccupiedPointOfInterestDistance((ChunkSectionPos)arg2) == 0).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return (ChunkSectionPos)list.get(lv.random.nextInt(list.size()));
    }

    @Nullable
    private BlockPos method_27923(ChunkSectionPos arg2) {
        ServerWorld lv = (ServerWorld)this.mob.world;
        PointOfInterestStorage lv2 = lv.getPointOfInterestStorage();
        List list = lv2.getInCircle(arg -> true, arg2.getCenterPos(), 8, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED).map(PointOfInterest::getPos).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return (BlockPos)list.get(lv.random.nextInt(list.size()));
    }

    private boolean method_27922(VillagerEntity arg) {
        return arg.canSummonGolem(this.mob.world.getTime());
    }
}

