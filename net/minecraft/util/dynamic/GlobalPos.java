/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft.util.dynamic;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import net.minecraft.class_5321;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos {
    public static final Codec<GlobalPos> field_25066 = RecordCodecBuilder.create(instance -> instance.group((App)DimensionType.field_24751.fieldOf("dimension").forGetter(GlobalPos::getDimension), (App)BlockPos.field_25064.fieldOf("pos").forGetter(GlobalPos::getPos)).apply((Applicative)instance, GlobalPos::create));
    private final class_5321<DimensionType> dimension;
    private final BlockPos pos;

    private GlobalPos(class_5321<DimensionType> arg, BlockPos arg2) {
        this.dimension = arg;
        this.pos = arg2;
    }

    public static GlobalPos create(class_5321<DimensionType> arg, BlockPos arg2) {
        return new GlobalPos(arg, arg2);
    }

    public class_5321<DimensionType> getDimension() {
        return this.dimension;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        GlobalPos lv = (GlobalPos)object;
        return Objects.equals(this.dimension, lv.dimension) && Objects.equals(this.pos, lv.pos);
    }

    public int hashCode() {
        return Objects.hash(this.dimension, this.pos);
    }

    public String toString() {
        return this.dimension.toString() + " " + this.pos;
    }
}

