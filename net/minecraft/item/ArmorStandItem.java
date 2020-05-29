/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.item;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArmorStandItem
extends Item {
    public ArmorStandItem(Item.Settings arg) {
        super(arg);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext arg) {
        Direction lv = arg.getSide();
        if (lv == Direction.DOWN) {
            return ActionResult.FAIL;
        }
        World lv2 = arg.getWorld();
        ItemPlacementContext lv3 = new ItemPlacementContext(arg);
        BlockPos lv4 = lv3.getBlockPos();
        ItemStack lv5 = arg.getStack();
        ArmorStandEntity lv6 = EntityType.ARMOR_STAND.create(lv2, lv5.getTag(), null, arg.getPlayer(), lv4, SpawnReason.SPAWN_EGG, true, true);
        if (!lv2.doesNotCollide(lv6) || !lv2.getEntities(lv6, lv6.getBoundingBox()).isEmpty()) {
            return ActionResult.FAIL;
        }
        if (!lv2.isClient) {
            float f = (float)MathHelper.floor((MathHelper.wrapDegrees(arg.getPlayerYaw() - 180.0f) + 22.5f) / 45.0f) * 45.0f;
            lv6.refreshPositionAndAngles(lv6.getX(), lv6.getY(), lv6.getZ(), f, 0.0f);
            this.setRotations(lv6, lv2.random);
            lv2.spawnEntity(lv6);
            lv2.playSound(null, lv6.getX(), lv6.getY(), lv6.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75f, 0.8f);
        }
        lv5.decrement(1);
        return ActionResult.method_29236(lv2.isClient);
    }

    private void setRotations(ArmorStandEntity arg, Random random) {
        EulerAngle lv = arg.getHeadRotation();
        float f = random.nextFloat() * 5.0f;
        float g = random.nextFloat() * 20.0f - 10.0f;
        EulerAngle lv2 = new EulerAngle(lv.getPitch() + f, lv.getYaw() + g, lv.getRoll());
        arg.setHeadRotation(lv2);
        lv = arg.getBodyRotation();
        f = random.nextFloat() * 10.0f - 5.0f;
        lv2 = new EulerAngle(lv.getPitch(), lv.getYaw() + f, lv.getRoll());
        arg.setBodyRotation(lv2);
    }
}

