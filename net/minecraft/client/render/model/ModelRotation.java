/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.DirectionTransformation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public enum ModelRotation implements ModelBakeSettings
{
    X0_Y0(0, 0),
    X0_Y90(0, 90),
    X0_Y180(0, 180),
    X0_Y270(0, 270),
    X90_Y0(90, 0),
    X90_Y90(90, 90),
    X90_Y180(90, 180),
    X90_Y270(90, 270),
    X180_Y0(180, 0),
    X180_Y90(180, 90),
    X180_Y180(180, 180),
    X180_Y270(180, 270),
    X270_Y0(270, 0),
    X270_Y90(270, 90),
    X270_Y180(270, 180),
    X270_Y270(270, 270);

    private static final Map<Integer, ModelRotation> BY_INDEX;
    private final AffineTransformation rotation;
    private final DirectionTransformation directionTransformation;
    private final int index;

    private static int getIndex(int x, int y) {
        return x * 360 + y;
    }

    private ModelRotation(int x, int y) {
        this.index = ModelRotation.getIndex(x, y);
        Quaternion lv = new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -y, true);
        lv.hamiltonProduct(new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -x, true));
        DirectionTransformation lv2 = DirectionTransformation.IDENTITY;
        for (int l = 0; l < y; l += 90) {
            lv2 = lv2.prepend(DirectionTransformation.ROT_90_Y_NEG);
        }
        for (int m = 0; m < x; m += 90) {
            lv2 = lv2.prepend(DirectionTransformation.ROT_90_X_NEG);
        }
        this.rotation = new AffineTransformation(null, lv, null, null);
        this.directionTransformation = lv2;
    }

    @Override
    public AffineTransformation getRotation() {
        return this.rotation;
    }

    public static ModelRotation get(int x, int y) {
        return BY_INDEX.get(ModelRotation.getIndex(MathHelper.floorMod(x, 360), MathHelper.floorMod(y, 360)));
    }

    static {
        BY_INDEX = Arrays.stream(ModelRotation.values()).collect(Collectors.toMap(arg -> arg.index, arg -> arg));
    }
}

