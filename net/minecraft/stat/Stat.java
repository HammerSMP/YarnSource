/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.stat;

import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Stat<T>
extends ScoreboardCriterion {
    private final StatFormatter formatter;
    private final T value;
    private final StatType<T> type;

    protected Stat(StatType<T> arg, T object, StatFormatter arg2) {
        super(Stat.getName(arg, object));
        this.type = arg;
        this.formatter = arg2;
        this.value = object;
    }

    public static <T> String getName(StatType<T> arg, T object) {
        return Stat.getName(Registry.STAT_TYPE.getId(arg)) + ":" + Stat.getName(arg.getRegistry().getId(object));
    }

    private static <T> String getName(@Nullable Identifier arg) {
        return arg.toString().replace(':', '.');
    }

    public StatType<T> getType() {
        return this.type;
    }

    public T getValue() {
        return this.value;
    }

    @Environment(value=EnvType.CLIENT)
    public String format(int i) {
        return this.formatter.format(i);
    }

    public boolean equals(Object object) {
        return this == object || object instanceof Stat && Objects.equals(this.getName(), ((Stat)object).getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public String toString() {
        return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + '}';
    }
}

