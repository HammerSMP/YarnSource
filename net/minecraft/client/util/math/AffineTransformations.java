/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.client.util.math;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class AffineTransformations {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final EnumMap<Direction, AffineTransformation> DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.SOUTH, AffineTransformation.identity());
        enumMap.put(Direction.EAST, new AffineTransformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 90.0f, true), null, null));
        enumMap.put(Direction.WEST, new AffineTransformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.NORTH, new AffineTransformation(null, new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), 180.0f, true), null, null));
        enumMap.put(Direction.UP, new AffineTransformation(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), -90.0f, true), null, null));
        enumMap.put(Direction.DOWN, new AffineTransformation(null, new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), 90.0f, true), null, null));
    });
    public static final EnumMap<Direction, AffineTransformation> INVERSED_DIRECTION_ROTATIONS = Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        for (Direction lv : Direction.values()) {
            enumMap.put(lv, DIRECTION_ROTATIONS.get(lv).invert());
        }
    });

    public static AffineTransformation setupUvLock(AffineTransformation arg) {
        Matrix4f lv = Matrix4f.translate(0.5f, 0.5f, 0.5f);
        lv.multiply(arg.getMatrix());
        lv.multiply(Matrix4f.translate(-0.5f, -0.5f, -0.5f));
        return new AffineTransformation(lv);
    }

    public static AffineTransformation uvLock(AffineTransformation arg, Direction arg2, Supplier<String> supplier) {
        Direction lv = Direction.transform(arg.getMatrix(), arg2);
        AffineTransformation lv2 = arg.invert();
        if (lv2 == null) {
            LOGGER.warn(supplier.get());
            return new AffineTransformation(null, null, new Vector3f(0.0f, 0.0f, 0.0f), null);
        }
        AffineTransformation lv3 = INVERSED_DIRECTION_ROTATIONS.get(arg2).multiply(lv2).multiply(DIRECTION_ROTATIONS.get(lv));
        return AffineTransformations.setupUvLock(lv3);
    }
}

