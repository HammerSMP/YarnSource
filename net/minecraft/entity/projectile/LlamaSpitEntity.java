/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class LlamaSpitEntity
extends ProjectileEntity {
    public LlamaSpitEntity(EntityType<? extends LlamaSpitEntity> arg, World arg2) {
        super((EntityType<? extends ProjectileEntity>)arg, arg2);
    }

    public LlamaSpitEntity(World arg, LlamaEntity arg2) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, arg);
        super.setOwner(arg2);
        this.updatePosition(arg2.getX() - (double)(arg2.getWidth() + 1.0f) * 0.5 * (double)MathHelper.sin(arg2.bodyYaw * ((float)Math.PI / 180)), arg2.getEyeY() - (double)0.1f, arg2.getZ() + (double)(arg2.getWidth() + 1.0f) * 0.5 * (double)MathHelper.cos(arg2.bodyYaw * ((float)Math.PI / 180)));
    }

    @Environment(value=EnvType.CLIENT)
    public LlamaSpitEntity(World arg, double d, double e, double f, double g, double h, double i) {
        this((EntityType<? extends LlamaSpitEntity>)EntityType.LLAMA_SPIT, arg);
        this.updatePosition(d, e, f);
        for (int j = 0; j < 7; ++j) {
            double k = 0.4 + 0.1 * (double)j;
            arg.addParticle(ParticleTypes.SPIT, d, e, f, g * k, h, i * k);
        }
        this.setVelocity(g, h, i);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3d lv = this.getVelocity();
        HitResult lv2 = ProjectileUtil.getCollision(this, this::method_26958, RayTraceContext.ShapeType.OUTLINE);
        if (lv2 != null) {
            this.onCollision(lv2);
        }
        double d = this.getX() + lv.x;
        double e = this.getY() + lv.y;
        double f = this.getZ() + lv.z;
        this.method_26962();
        float g = 0.99f;
        float h = 0.06f;
        if (this.world.method_29546(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
            this.remove();
            return;
        }
        if (this.isInsideWaterOrBubbleColumn()) {
            this.remove();
            return;
        }
        this.setVelocity(lv.multiply(0.99f));
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.06f, 0.0));
        }
        this.updatePosition(d, e, f);
    }

    @Override
    protected void onEntityHit(EntityHitResult arg) {
        super.onEntityHit(arg);
        Entity lv = this.getOwner();
        if (lv instanceof LivingEntity) {
            arg.getEntity().damage(DamageSource.mobProjectile(this, (LivingEntity)lv).setProjectile(), 1.0f);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult arg) {
        super.onBlockHit(arg);
        if (!this.world.isClient) {
            this.remove();
        }
    }

    @Override
    protected void initDataTracker() {
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}

