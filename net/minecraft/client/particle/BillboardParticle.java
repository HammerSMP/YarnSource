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
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public abstract class BillboardParticle
extends Particle {
    protected float scale;

    protected BillboardParticle(ClientWorld arg, double d, double e, double f) {
        super(arg, d, e, f);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    protected BillboardParticle(ClientWorld arg, double d, double e, double f, double g, double h, double i) {
        super(arg, d, e, f, g, h, i);
        this.scale = 0.1f * (this.random.nextFloat() * 0.5f + 0.5f) * 2.0f;
    }

    @Override
    public void buildGeometry(VertexConsumer arg, Camera arg2, float f) {
        Quaternion lv3;
        Vec3d lv = arg2.getPos();
        float g = (float)(MathHelper.lerp((double)f, this.prevPosX, this.x) - lv.getX());
        float h = (float)(MathHelper.lerp((double)f, this.prevPosY, this.y) - lv.getY());
        float i = (float)(MathHelper.lerp((double)f, this.prevPosZ, this.z) - lv.getZ());
        if (this.angle == 0.0f) {
            Quaternion lv2 = arg2.getRotation();
        } else {
            lv3 = new Quaternion(arg2.getRotation());
            float j = MathHelper.lerp(f, this.prevAngle, this.angle);
            lv3.hamiltonProduct(Vector3f.POSITIVE_Z.getRadialQuaternion(j));
        }
        Vector3f lv4 = new Vector3f(-1.0f, -1.0f, 0.0f);
        lv4.rotate(lv3);
        Vector3f[] lvs = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float k = this.getSize(f);
        for (int l = 0; l < 4; ++l) {
            Vector3f lv5 = lvs[l];
            lv5.rotate(lv3);
            lv5.scale(k);
            lv5.add(g, h, i);
        }
        float m = this.getMinU();
        float n = this.getMaxU();
        float o = this.getMinV();
        float p = this.getMaxV();
        int q = this.getColorMultiplier(f);
        arg.vertex(lvs[0].getX(), lvs[0].getY(), lvs[0].getZ()).texture(n, p).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
        arg.vertex(lvs[1].getX(), lvs[1].getY(), lvs[1].getZ()).texture(n, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
        arg.vertex(lvs[2].getX(), lvs[2].getY(), lvs[2].getZ()).texture(m, o).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
        arg.vertex(lvs[3].getX(), lvs[3].getY(), lvs[3].getZ()).texture(m, p).color(this.colorRed, this.colorGreen, this.colorBlue, this.colorAlpha).light(q).next();
    }

    public float getSize(float f) {
        return this.scale;
    }

    @Override
    public Particle scale(float f) {
        this.scale *= f;
        return super.scale(f);
    }

    protected abstract float getMinU();

    protected abstract float getMaxU();

    protected abstract float getMinV();

    protected abstract float getMaxV();
}

