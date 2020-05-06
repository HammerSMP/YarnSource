/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.booleans.BooleanArrayList
 *  it.unimi.dsi.fastutil.booleans.BooleanList
 *  javax.annotation.Nullable
 */
package net.minecraft.util.math;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisTransformation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;

public enum DirectionTransformation implements StringIdentifiable
{
    IDENTITY("identity", AxisTransformation.P123, false, false, false),
    ROT_180_FACE_XY("rot_180_face_xy", AxisTransformation.P123, true, true, false),
    ROT_180_FACE_XZ("rot_180_face_xz", AxisTransformation.P123, true, false, true),
    ROT_180_FACE_YZ("rot_180_face_yz", AxisTransformation.P123, false, true, true),
    ROT_120_NNN("rot_120_nnn", AxisTransformation.P231, false, false, false),
    ROT_120_NNP("rot_120_nnp", AxisTransformation.P312, true, false, true),
    ROT_120_NPN("rot_120_npn", AxisTransformation.P312, false, true, true),
    ROT_120_NPP("rot_120_npp", AxisTransformation.P231, true, false, true),
    ROT_120_PNN("rot_120_pnn", AxisTransformation.P312, true, true, false),
    ROT_120_PNP("rot_120_pnp", AxisTransformation.P231, true, true, false),
    ROT_120_PPN("rot_120_ppn", AxisTransformation.P231, false, true, true),
    ROT_120_PPP("rot_120_ppp", AxisTransformation.P312, false, false, false),
    ROT_180_EDGE_XY_NEG("rot_180_edge_xy_neg", AxisTransformation.P213, true, true, true),
    ROT_180_EDGE_XY_POS("rot_180_edge_xy_pos", AxisTransformation.P213, false, false, true),
    ROT_180_EDGE_XZ_NEG("rot_180_edge_xz_neg", AxisTransformation.P321, true, true, true),
    ROT_180_EDGE_XZ_POS("rot_180_edge_xz_pos", AxisTransformation.P321, false, true, false),
    ROT_180_EDGE_YZ_NEG("rot_180_edge_yz_neg", AxisTransformation.P132, true, true, true),
    ROT_180_EDGE_YZ_POS("rot_180_edge_yz_pos", AxisTransformation.P132, true, false, false),
    ROT_90_X_NEG("rot_90_x_neg", AxisTransformation.P132, false, false, true),
    ROT_90_X_POS("rot_90_x_pos", AxisTransformation.P132, false, true, false),
    ROT_90_Y_NEG("rot_90_y_neg", AxisTransformation.P321, true, false, false),
    ROT_90_Y_POS("rot_90_y_pos", AxisTransformation.P321, false, false, true),
    ROT_90_Z_NEG("rot_90_z_neg", AxisTransformation.P213, false, true, false),
    ROT_90_Z_POS("rot_90_z_pos", AxisTransformation.P213, true, false, false),
    INVERSION("inversion", AxisTransformation.P123, true, true, true),
    INVERT_X("invert_x", AxisTransformation.P123, true, false, false),
    INVERT_Y("invert_y", AxisTransformation.P123, false, true, false),
    INVERT_Z("invert_z", AxisTransformation.P123, false, false, true),
    ROT_60_REF_NNN("rot_60_ref_nnn", AxisTransformation.P312, true, true, true),
    ROT_60_REF_NNP("rot_60_ref_nnp", AxisTransformation.P231, true, false, false),
    ROT_60_REF_NPN("rot_60_ref_npn", AxisTransformation.P231, false, false, true),
    ROT_60_REF_NPP("rot_60_ref_npp", AxisTransformation.P312, false, false, true),
    ROT_60_REF_PNN("rot_60_ref_pnn", AxisTransformation.P231, false, true, false),
    ROT_60_REF_PNP("rot_60_ref_pnp", AxisTransformation.P312, true, false, false),
    ROT_60_REF_PPN("rot_60_ref_ppn", AxisTransformation.P312, false, true, false),
    ROT_60_REF_PPP("rot_60_ref_ppp", AxisTransformation.P231, true, true, true),
    SWAP_XY("swap_xy", AxisTransformation.P213, false, false, false),
    SWAP_YZ("swap_yz", AxisTransformation.P132, false, false, false),
    SWAP_XZ("swap_xz", AxisTransformation.P321, false, false, false),
    SWAP_NEG_XY("swap_neg_xy", AxisTransformation.P213, true, true, false),
    SWAP_NEG_YZ("swap_neg_yz", AxisTransformation.P132, false, true, true),
    SWAP_NEG_XZ("swap_neg_xz", AxisTransformation.P321, true, false, true),
    ROT_90_REF_X_NEG("rot_90_ref_x_neg", AxisTransformation.P132, true, false, true),
    ROT_90_REF_X_POS("rot_90_ref_x_pos", AxisTransformation.P132, true, true, false),
    ROT_90_REF_Y_NEG("rot_90_ref_y_neg", AxisTransformation.P321, true, true, false),
    ROT_90_REF_Y_POS("rot_90_ref_y_pos", AxisTransformation.P321, false, true, true),
    ROT_90_REF_Z_NEG("rot_90_ref_z_neg", AxisTransformation.P213, false, true, true),
    ROT_90_REF_Z_POS("rot_90_ref_z_pos", AxisTransformation.P213, true, false, true);

    private final Matrix3f matrix;
    private final String name;
    @Nullable
    private Map<Direction, Direction> mappings;
    private final boolean flipX;
    private final boolean flipY;
    private final boolean flipZ;
    private final AxisTransformation axisTransformation;
    private static final DirectionTransformation[][] COMBINATIONS;
    private static final DirectionTransformation[] INVERSES;

    private DirectionTransformation(String string2, AxisTransformation arg, boolean bl, boolean bl2, boolean bl3) {
        this.name = string2;
        this.flipX = bl;
        this.flipY = bl2;
        this.flipZ = bl3;
        this.axisTransformation = arg;
        this.matrix = new Matrix3f();
        this.matrix.a00 = bl ? -1.0f : 1.0f;
        this.matrix.a11 = bl2 ? -1.0f : 1.0f;
        this.matrix.a22 = bl3 ? -1.0f : 1.0f;
        this.matrix.multiply(arg.getMatrix());
    }

    private BooleanList getAxisFlips() {
        return new BooleanArrayList(new boolean[]{this.flipX, this.flipY, this.flipZ});
    }

    public DirectionTransformation prepend(DirectionTransformation arg) {
        return COMBINATIONS[this.ordinal()][arg.ordinal()];
    }

    public String toString() {
        return this.name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public Direction map(Direction arg) {
        if (this.mappings == null) {
            this.mappings = Maps.newEnumMap(Direction.class);
            for (Direction lv : Direction.values()) {
                Direction.Axis lv2 = lv.getAxis();
                Direction.AxisDirection lv3 = lv.getDirection();
                Direction.Axis lv4 = Direction.Axis.values()[this.axisTransformation.map(lv2.ordinal())];
                Direction.AxisDirection lv5 = this.shouldFlipDirection(lv4) ? lv3.getOpposite() : lv3;
                Direction lv6 = Direction.from(lv4, lv5);
                this.mappings.put(lv, lv6);
            }
        }
        return this.mappings.get(arg);
    }

    public boolean shouldFlipDirection(Direction.Axis arg) {
        switch (arg) {
            case X: {
                return this.flipX;
            }
            case Y: {
                return this.flipY;
            }
        }
        return this.flipZ;
    }

    public JigsawOrientation mapJigsawOrientation(JigsawOrientation arg) {
        return JigsawOrientation.byDirections(this.map(arg.method_26426()), this.map(arg.method_26428()));
    }

    static {
        COMBINATIONS = Util.make(new DirectionTransformation[DirectionTransformation.values().length][DirectionTransformation.values().length], args -> {
            Map<Pair, DirectionTransformation> map = Arrays.stream(DirectionTransformation.values()).collect(Collectors.toMap(arg -> Pair.of((Object)((Object)arg.axisTransformation), (Object)arg.getAxisFlips()), arg -> arg));
            for (DirectionTransformation lv : DirectionTransformation.values()) {
                for (DirectionTransformation lv2 : DirectionTransformation.values()) {
                    BooleanList booleanList = lv.getAxisFlips();
                    BooleanList booleanList2 = lv2.getAxisFlips();
                    AxisTransformation lv3 = lv2.axisTransformation.prepend(lv.axisTransformation);
                    BooleanArrayList booleanArrayList = new BooleanArrayList(3);
                    for (int i = 0; i < 3; ++i) {
                        booleanArrayList.add(booleanList.getBoolean(i) ^ booleanList2.getBoolean(lv.axisTransformation.map(i)));
                    }
                    args[lv.ordinal()][lv2.ordinal()] = map.get((Object)Pair.of((Object)((Object)lv3), (Object)booleanArrayList));
                }
            }
        });
        INVERSES = (DirectionTransformation[])Arrays.stream(DirectionTransformation.values()).map((? super T arg) -> Arrays.stream(DirectionTransformation.values()).filter(arg2 -> arg.prepend((DirectionTransformation)arg2) == IDENTITY).findAny().get()).toArray(DirectionTransformation[]::new);
    }
}

