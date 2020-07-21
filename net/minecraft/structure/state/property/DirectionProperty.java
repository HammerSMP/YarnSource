/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.state.property;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.Direction;

public class DirectionProperty
extends EnumProperty<Direction> {
    protected DirectionProperty(String string, Collection<Direction> collection) {
        super(string, Direction.class, collection);
    }

    public static DirectionProperty of(String string, Predicate<Direction> predicate) {
        return DirectionProperty.of(string, Arrays.stream(Direction.values()).filter(predicate).collect(Collectors.toList()));
    }

    public static DirectionProperty of(String string, Direction ... args) {
        return DirectionProperty.of(string, Lists.newArrayList((Object[])args));
    }

    public static DirectionProperty of(String string, Collection<Direction> collection) {
        return new DirectionProperty(string, collection);
    }
}

