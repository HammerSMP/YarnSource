/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ThreadLocalRandom
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.attribute;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAttributeModifier {
    private static final Logger LOGGER = LogManager.getLogger();
    private final double value;
    private final Operation operation;
    private final Supplier<String> nameGetter;
    private final UUID uuid;

    public EntityAttributeModifier(String name, double value, Operation operation) {
        this(MathHelper.randomUuid((Random)ThreadLocalRandom.current()), () -> name, value, operation);
    }

    public EntityAttributeModifier(UUID uuid, String name, double value, Operation operation) {
        this(uuid, () -> name, value, operation);
    }

    public EntityAttributeModifier(UUID uuid, Supplier<String> nameGetter, double value, Operation operation) {
        this.uuid = uuid;
        this.nameGetter = nameGetter;
        this.value = value;
        this.operation = operation;
    }

    public UUID getId() {
        return this.uuid;
    }

    public String getName() {
        return this.nameGetter.get();
    }

    public Operation getOperation() {
        return this.operation;
    }

    public double getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EntityAttributeModifier lv = (EntityAttributeModifier)o;
        return Objects.equals(this.uuid, lv.uuid);
    }

    public int hashCode() {
        return this.uuid.hashCode();
    }

    public String toString() {
        return "AttributeModifier{amount=" + this.value + ", operation=" + (Object)((Object)this.operation) + ", name='" + this.nameGetter.get() + '\'' + ", id=" + this.uuid + '}';
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", this.getName());
        lv.putDouble("Amount", this.value);
        lv.putInt("Operation", this.operation.getId());
        lv.putUuid("UUID", this.uuid);
        return lv;
    }

    @Nullable
    public static EntityAttributeModifier fromTag(CompoundTag tag) {
        try {
            UUID uUID = tag.getUuid("UUID");
            Operation lv = Operation.fromId(tag.getInt("Operation"));
            return new EntityAttributeModifier(uUID, tag.getString("Name"), tag.getDouble("Amount"), lv);
        }
        catch (Exception exception) {
            LOGGER.warn("Unable to create attribute: {}", (Object)exception.getMessage());
            return null;
        }
    }

    public static enum Operation {
        ADDITION(0),
        MULTIPLY_BASE(1),
        MULTIPLY_TOTAL(2);

        private static final Operation[] VALUES;
        private final int id;

        private Operation(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static Operation fromId(int id) {
            if (id < 0 || id >= VALUES.length) {
                throw new IllegalArgumentException("No operation with value " + id);
            }
            return VALUES[id];
        }

        static {
            VALUES = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }
    }
}

