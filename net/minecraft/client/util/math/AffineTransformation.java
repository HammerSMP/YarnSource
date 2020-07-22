/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.tuple.Triple
 */
package net.minecraft.client.util.math;

import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(value=EnvType.CLIENT)
public final class AffineTransformation {
    private final Matrix4f matrix;
    private boolean initialized;
    @Nullable
    private Vector3f translation;
    @Nullable
    private Quaternion rotation2;
    @Nullable
    private Vector3f scale;
    @Nullable
    private Quaternion rotation1;
    private static final AffineTransformation IDENTITY = Util.make(() -> {
        Matrix4f lv = new Matrix4f();
        lv.loadIdentity();
        AffineTransformation lv2 = new AffineTransformation(lv);
        lv2.getRotation2();
        return lv2;
    });

    public AffineTransformation(@Nullable Matrix4f matrix) {
        this.matrix = matrix == null ? AffineTransformation.IDENTITY.matrix : matrix;
    }

    public AffineTransformation(@Nullable Vector3f translation, @Nullable Quaternion rotation2, @Nullable Vector3f scale, @Nullable Quaternion rotation1) {
        this.matrix = AffineTransformation.setup(translation, rotation2, scale, rotation1);
        this.translation = translation != null ? translation : new Vector3f();
        this.rotation2 = rotation2 != null ? rotation2 : Quaternion.IDENTITY.copy();
        this.scale = scale != null ? scale : new Vector3f(1.0f, 1.0f, 1.0f);
        this.rotation1 = rotation1 != null ? rotation1 : Quaternion.IDENTITY.copy();
        this.initialized = true;
    }

    public static AffineTransformation identity() {
        return IDENTITY;
    }

    public AffineTransformation multiply(AffineTransformation other) {
        Matrix4f lv = this.getMatrix();
        lv.multiply(other.getMatrix());
        return new AffineTransformation(lv);
    }

    @Nullable
    public AffineTransformation invert() {
        if (this == IDENTITY) {
            return this;
        }
        Matrix4f lv = this.getMatrix();
        if (lv.invert()) {
            return new AffineTransformation(lv);
        }
        return null;
    }

    private void init() {
        if (!this.initialized) {
            Pair<Matrix3f, Vector3f> pair = AffineTransformation.getLinearTransformationAndTranslationFromAffine(this.matrix);
            Triple<Quaternion, Vector3f, Quaternion> triple = ((Matrix3f)pair.getFirst()).decomposeLinearTransformation();
            this.translation = (Vector3f)pair.getSecond();
            this.rotation2 = (Quaternion)triple.getLeft();
            this.scale = (Vector3f)triple.getMiddle();
            this.rotation1 = (Quaternion)triple.getRight();
            this.initialized = true;
        }
    }

    private static Matrix4f setup(@Nullable Vector3f translation, @Nullable Quaternion rotation2, @Nullable Vector3f scale, @Nullable Quaternion rotation1) {
        Matrix4f lv = new Matrix4f();
        lv.loadIdentity();
        if (rotation2 != null) {
            lv.multiply(new Matrix4f(rotation2));
        }
        if (scale != null) {
            lv.multiply(Matrix4f.scale(scale.getX(), scale.getY(), scale.getZ()));
        }
        if (rotation1 != null) {
            lv.multiply(new Matrix4f(rotation1));
        }
        if (translation != null) {
            lv.a03 = translation.getX();
            lv.a13 = translation.getY();
            lv.a23 = translation.getZ();
        }
        return lv;
    }

    public static Pair<Matrix3f, Vector3f> getLinearTransformationAndTranslationFromAffine(Matrix4f affineTransform) {
        affineTransform.multiply(1.0f / affineTransform.a33);
        Vector3f lv = new Vector3f(affineTransform.a03, affineTransform.a13, affineTransform.a23);
        Matrix3f lv2 = new Matrix3f(affineTransform);
        return Pair.of((Object)lv2, (Object)lv);
    }

    public Matrix4f getMatrix() {
        return this.matrix.copy();
    }

    public Quaternion getRotation2() {
        this.init();
        return this.rotation2.copy();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        AffineTransformation lv = (AffineTransformation)object;
        return Objects.equals(this.matrix, lv.matrix);
    }

    public int hashCode() {
        return Objects.hash(this.matrix);
    }
}

