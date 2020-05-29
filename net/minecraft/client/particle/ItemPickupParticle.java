/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class ItemPickupParticle
extends Particle {
    private final BufferBuilderStorage bufferStorage;
    private final Entity itemEntity;
    private final Entity interactingEntity;
    private int ticksExisted;
    private final EntityRenderDispatcher dispatcher;

    public ItemPickupParticle(EntityRenderDispatcher arg, BufferBuilderStorage arg2, ClientWorld arg3, Entity arg4, Entity arg5) {
        this(arg, arg2, arg3, arg4, arg5, arg4.getVelocity());
    }

    private ItemPickupParticle(EntityRenderDispatcher arg, BufferBuilderStorage arg2, ClientWorld arg3, Entity arg4, Entity arg5, Vec3d arg6) {
        super(arg3, arg4.getX(), arg4.getY(), arg4.getZ(), arg6.x, arg6.y, arg6.z);
        this.bufferStorage = arg2;
        this.itemEntity = this.method_29358(arg4);
        this.interactingEntity = arg5;
        this.dispatcher = arg;
    }

    private Entity method_29358(Entity arg) {
        if (!(arg instanceof ItemEntity)) {
            return arg;
        }
        return ((ItemEntity)arg).method_29271();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.CUSTOM;
    }

    @Override
    public void buildGeometry(VertexConsumer arg, Camera arg2, float f) {
        float g = ((float)this.ticksExisted + f) / 3.0f;
        g *= g;
        double d = MathHelper.lerp((double)f, this.interactingEntity.lastRenderX, this.interactingEntity.getX());
        double e = MathHelper.lerp((double)f, this.interactingEntity.lastRenderY, this.interactingEntity.getY()) + 0.5;
        double h = MathHelper.lerp((double)f, this.interactingEntity.lastRenderZ, this.interactingEntity.getZ());
        double i = MathHelper.lerp((double)g, this.itemEntity.getX(), d);
        double j = MathHelper.lerp((double)g, this.itemEntity.getY(), e);
        double k = MathHelper.lerp((double)g, this.itemEntity.getZ(), h);
        VertexConsumerProvider.Immediate lv = this.bufferStorage.getEntityVertexConsumers();
        Vec3d lv2 = arg2.getPos();
        this.dispatcher.render(this.itemEntity, i - lv2.getX(), j - lv2.getY(), k - lv2.getZ(), this.itemEntity.yaw, f, new MatrixStack(), lv, this.dispatcher.getLight(this.itemEntity, f));
        lv.draw();
    }

    @Override
    public void tick() {
        ++this.ticksExisted;
        if (this.ticksExisted == 3) {
            this.markDead();
        }
    }
}

