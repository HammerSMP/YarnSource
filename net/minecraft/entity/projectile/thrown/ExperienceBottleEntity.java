/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.projectile.thrown;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ExperienceBottleEntity
extends ThrownItemEntity {
    public ExperienceBottleEntity(EntityType<? extends ExperienceBottleEntity> arg, World arg2) {
        super((EntityType<? extends ThrownItemEntity>)arg, arg2);
    }

    public ExperienceBottleEntity(World arg, LivingEntity arg2) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.EXPERIENCE_BOTTLE, arg2, arg);
    }

    public ExperienceBottleEntity(World arg, double d, double e, double f) {
        super((EntityType<? extends ThrownItemEntity>)EntityType.EXPERIENCE_BOTTLE, d, e, f, arg);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected float getGravity() {
        return 0.07f;
    }

    @Override
    protected void onCollision(HitResult arg) {
        super.onCollision(arg);
        if (!this.world.isClient) {
            int j;
            this.world.syncWorldEvent(2002, this.getBlockPos(), PotionUtil.getColor(Potions.WATER));
            for (int i = 3 + this.world.random.nextInt(5) + this.world.random.nextInt(5); i > 0; i -= j) {
                j = ExperienceOrbEntity.roundToOrbSize(i);
                this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), j));
            }
            this.remove();
        }
    }
}

