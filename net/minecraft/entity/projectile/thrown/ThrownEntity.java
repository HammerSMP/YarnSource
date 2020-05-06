/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public abstract class ThrownEntity
extends ProjectileEntity {
    protected ThrownEntity(EntityType<? extends ThrownEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> arg, double d, double e, double f, World arg2) {
        this(arg, arg2);
        this.updatePosition(d, e, f);
    }

    protected ThrownEntity(EntityType<? extends ThrownEntity> arg, LivingEntity arg2, World arg3) {
        this(arg, arg2.getX(), arg2.getEyeY() - (double)0.1f, arg2.getZ(), arg3);
        this.setOwner(arg2);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = this.getBoundingBox().getAverageSideLength() * 4.0;
        if (Double.isNaN(e)) {
            e = 4.0;
        }
        return d < (e *= 64.0) * e;
    }

    @Override
    public void tick() {
        float j;
        super.tick();
        HitResult lv = ProjectileUtil.getCollision(this, this::method_26958, RayTraceContext.ShapeType.OUTLINE);
        if (lv.getType() != HitResult.Type.MISS) {
            if (lv.getType() == HitResult.Type.BLOCK && this.world.getBlockState(((BlockHitResult)lv).getBlockPos()).isOf(Blocks.NETHER_PORTAL)) {
                this.setInNetherPortal(((BlockHitResult)lv).getBlockPos());
            } else {
                this.onCollision(lv);
            }
        }
        Vec3d lv2 = this.getVelocity();
        double d = this.getX() + lv2.x;
        double e = this.getY() + lv2.y;
        double f = this.getZ() + lv2.z;
        this.method_26962();
        if (this.isTouchingWater()) {
            for (int i = 0; i < 4; ++i) {
                float g = 0.25f;
                this.world.addParticle(ParticleTypes.BUBBLE, d - lv2.x * 0.25, e - lv2.y * 0.25, f - lv2.z * 0.25, lv2.x, lv2.y, lv2.z);
            }
            float h = 0.8f;
        } else {
            j = 0.99f;
        }
        this.setVelocity(lv2.multiply(j));
        if (!this.hasNoGravity()) {
            Vec3d lv3 = this.getVelocity();
            this.setVelocity(lv3.x, lv3.y - (double)this.getGravity(), lv3.z);
        }
        this.updatePosition(d, e, f);
    }

    protected float getGravity() {
        return 0.03f;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

