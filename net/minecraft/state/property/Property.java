/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 */
package net.minecraft.state.property;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.state.State;

public abstract class Property<T extends Comparable<T>> {
    private final Class<T> field_24742;
    private final String field_24743;
    private Integer field_24744;
    private final Codec<T> field_24745 = Codec.STRING.comapFlatMap(string -> this.parse((String)string).map(DataResult::success).orElseGet(() -> DataResult.error((String)("Unable to read property: " + this + " with value: " + string))), this::name);

    protected Property(String string2, Class<T> class_) {
        this.field_24742 = class_;
        this.field_24743 = string2;
    }

    public String getName() {
        return this.field_24743;
    }

    public Class<T> getType() {
        return this.field_24742;
    }

    public abstract Collection<T> getValues();

    public abstract String name(T var1);

    public abstract Optional<T> parse(String var1);

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("name", (Object)this.field_24743).add("clazz", this.field_24742).add("values", this.getValues()).toString();
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof Property) {
            Property lv = (Property)object;
            return this.field_24742.equals(lv.field_24742) && this.field_24743.equals(lv.field_24743);
        }
        return false;
    }

    public final int hashCode() {
        if (this.field_24744 == null) {
            this.field_24744 = this.computeHashCode();
        }
        return this.field_24744;
    }

    public int computeHashCode() {
        return 31 * this.field_24742.hashCode() + this.field_24743.hashCode();
    }

    public <U, S extends State<?, S>> DataResult<S> method_28503(DynamicOps<U> dynamicOps, S arg, U object) {
        DataResult dataResult = this.field_24745.parse(dynamicOps, object);
        return dataResult.map(comparable -> (State)arg.with(this, comparable)).setPartial(arg);
    }
}

