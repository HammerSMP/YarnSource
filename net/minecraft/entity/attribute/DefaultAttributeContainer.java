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

    public DefaultAttributeContainer(Map<EntityAttribute, EntityAttributeInstance> instances) {
        this.instances = ImmutableMap.copyOf(instances);
    }

    private EntityAttributeInstance require(EntityAttribute attribute) {
        EntityAttributeInstance lv = this.instances.get(attribute);
        if (lv == null) {
            throw new IllegalArgumentException("Can't find attribute " + Registry.ATTRIBUTE.getId(attribute));
        }
        return lv;
    }

    public double getValue(EntityAttribute attribute) {
        return this.require(attribute).getValue();
    }

    public double getBaseValue(EntityAttribute attribute) {
        return this.require(attribute).getBaseValue();
    }

    public double getModifierValue(EntityAttribute attribute, UUID uuid) {
        EntityAttributeModifier lv = this.require(attribute).getModifier(uuid);
        if (lv == null) {
            throw new IllegalArgumentException("Can't find modifier " + uuid + " on attribute " + Registry.ATTRIBUTE.getId(attribute));
        }
        return lv.getValue();
    }

    @Nullable
    public EntityAttributeInstance createOverride(Consumer<EntityAttributeInstance> updateCallback, EntityAttribute attribute) {
        EntityAttributeInstance lv = this.instances.get(attribute);
        if (lv == null) {
            return null;
        }
        EntityAttributeInstance lv2 = new EntityAttributeInstance(attribute, updateCallback);
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

        private EntityAttributeInstance checkedAdd(EntityAttribute attribute) {
            EntityAttributeInstance lv = new EntityAttributeInstance(attribute, arg2 -> {
                if (this.unmodifiable) {
                    throw new UnsupportedOperationException("Tried to change value for default attribute instance: " + Registry.ATTRIBUTE.getId(attribute));
                }
            });
            this.instances.put(attribute, lv);
            return lv;
        }

        public Builder add(EntityAttribute attribute) {
            this.checkedAdd(attribute);
            return this;
        }

        public Builder add(EntityAttribute attribute, double baseValue) {
            EntityAttributeInstance lv = this.checkedAdd(attribute);
            lv.setBaseValue(baseValue);
            return this;
        }

        public DefaultAttributeContainer build() {
            this.unmodifiable = true;
            return new DefaultAttributeContainer(this.instances);
        }
    }
}

