/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class BoatItem
extends Item {
    private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::collides);
    private final BoatEntity.Type type;

    public BoatItem(BoatEntity.Type type, Item.Settings settings) {
        super(settings);
        this.type = type;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack lv = user.getStackInHand(hand);
        BlockHitResult lv2 = BoatItem.rayTrace(world, user, RayTraceContext.FluidHandling.ANY);
        if (((HitResult)lv2).getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(lv);
        }
        Vec3d lv3 = user.getRotationVec(1.0f);
        double d = 5.0;
        List<Entity> list = world.getOtherEntities(user, user.getBoundingBox().stretch(lv3.multiply(5.0)).expand(1.0), RIDERS);
        if (!list.isEmpty()) {
            Vec3d lv4 = user.getCameraPosVec(1.0f);
            for (Entity lv5 : list) {
                Box lv6 = lv5.getBoundingBox().expand(lv5.getTargetingMargin());
                if (!lv6.contains(lv4)) continue;
                return TypedActionResult.pass(lv);
            }
        }
        if (((HitResult)lv2).getType() == HitResult.Type.BLOCK) {
            BoatEntity lv7 = new BoatEntity(world, lv2.getPos().x, lv2.getPos().y, lv2.getPos().z);
            lv7.setBoatType(this.type);
            lv7.yaw = user.yaw;
            if (!world.doesNotCollide(lv7, lv7.getBoundingBox().expand(-0.1))) {
                return TypedActionResult.fail(lv);
            }
            if (!world.isClient) {
                world.spawnEntity(lv7);
                if (!user.abilities.creativeMode) {
                    lv.decrement(1);
                }
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.method_29237(lv, world.isClient());
        }
        return TypedActionResult.pass(lv);
    }
}

