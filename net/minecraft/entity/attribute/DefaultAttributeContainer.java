/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.registry.Registry;

public class DefaultAttributeContainer {
    private final Map<EntityAttribute, EntityAttributeInstance> instances;

    public DefaultAttributeContainer(Map<EntityAttribute, EntityAttributeInstance> map) {
        this.instances = ImmutableMap.copyOf(map);
    }

    private EntityAttributeInstance require(EntityAttribute arg) {
        EntityAttributeInstance lv = this.instances.get(arg);
        if (lv == null) {
            throw new IllegalArgumentException("Can't find attribute " + Registry.ATTRIBUTE.getId(arg));
        }
        return lv;
    }

    public double getValue(EntityAttribute arg) {
        return this.require(arg).getValue();
    }

    public double getBaseValue(EntityAttribute arg) {
        return this.require(arg).getBaseValue();
    }

    public double getModifierValue(EntityAttribute arg, UUID uUID) {
        EntityAttributeModifier lv = this.require(arg).getModifier(uUID);
        if (lv == null) {
            throw new IllegalArgumentException("Can't find modifier " + uUID + " on attribute " + Registry.ATTRIBUTE.getId(arg));
        }
        return lv.getValue();
    }

    @Nullable
    public EntityAttributeInstance createOverride(Consumer<EntityAttributeInstance> consumer, EntityAttribute arg) {
        EntityAttributeInstance lv = this.instances.get(arg);
        if (lv == null) {
            return null;
        }
        EntityAttributeInstance lv2 = new EntityAttributeInstance(arg, consumer);
        lv2.setFrom(lv);
        return lv2;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean method_27310(EntityAttribute arg) {
        return this.instances.containsKey(arg);
    }

    public boolean method_27309(EntityAttribute arg, UUID uUID) {
        EntityAttributeInstance lv = this.instances.get(arg);
        return lv != null && lv.getModifier(uUID) != null;
    }

    public static class Builder {
        private final Map<EntityAttribute, EntityAttributeInstance> instances = Maps.newHashMap();
        private boolean unmodifiable;

        private EntityAttributeInstance checkedAdd(EntityAttribute arg) {
            EntityAttributeInstance lv = new EntityAttributeInstance(arg, arg2 -> {
                if (this.unmodifiable) {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getId(arg));
                }
            });
            this.instances.put(arg, lv);
            return lv;
        }

        public Builder add(EntityAttribute arg) {
            this.checkedAdd(arg);
            return this;
        }

        public Builder add(EntityAttribute arg, double d) {
            EntityAttributeInstance lv = this.checkedAdd(arg);
            lv.setBaseValue(d);
            return this;
        }

        public DefaultAttributeContainer build() {
            this.unmodifiable = true;
            return new DefaultAttributeContainer(this.instances);
        }
    }
}

