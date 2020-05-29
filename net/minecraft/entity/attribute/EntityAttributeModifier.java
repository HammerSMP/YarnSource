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

    public EntityAttributeModifier(String string, double d, Operation arg) {
        this(MathHelper.randomUuid((Random)ThreadLocalRandom.current()), () -> string, d, arg);
    }

    public EntityAttributeModifier(UUID uUID, String string, double d, Operation arg) {
        this(uUID, () -> string, d, arg);
    }

    public EntityAttributeModifier(UUID uUID, Supplier<String> supplier, double d, Operation arg) {
        this.uuid = uUID;
        this.nameGetter = supplier;
        this.value = d;
        this.operation = arg;
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

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        EntityAttributeModifier lv = (EntityAttributeModifier)object;
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
    public static EntityAttributeModifier fromTag(CompoundTag arg) {
        try {
            UUID uUID = arg.getUuid("UUID");
            Operation lv = Operation.fromId(arg.getInt("Operation"));
            return new EntityAttributeModifier(uUID, arg.getString("Name"), arg.getDouble("Amount"), lv);
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

        private Operation(int j) {
            this.id = j;
        }

        public int getId() {
            return this.id;
        }

        public static Operation fromId(int i) {
            if (i < 0 || i >= VALUES.length) {
                throw new IllegalArgumentException("No operation with value " + i);
            }
            return VALUES[i];
        }

        static {
            VALUES = new Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
        }
    }
}

