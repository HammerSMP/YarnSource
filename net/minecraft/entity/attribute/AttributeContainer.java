/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AttributeContainer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<EntityAttribute, EntityAttributeInstance> custom = Maps.newHashMap();
    private final Set<EntityAttributeInstance> tracked = Sets.newHashSet();
    private final DefaultAttributeContainer fallback;

    public AttributeContainer(DefaultAttributeContainer defaultAttributes) {
        this.fallback = defaultAttributes;
    }

    private void updateTrackedStatus(EntityAttributeInstance instance) {
        if (instance.getAttribute().isTracked()) {
            this.tracked.add(instance);
        }
    }

    public Set<EntityAttributeInstance> getTracked() {
        return this.tracked;
    }

    public Collection<EntityAttributeInstance> getAttributesToSend() {
        return this.custom.values().stream().filter(arg -> arg.getAttribute().isTracked()).collect(Collectors.toList());
    }

    @Nullable
    public EntityAttributeInstance getCustomInstance(EntityAttribute attribute) {
        return this.custom.computeIfAbsent(attribute, arg -> this.fallback.createOverride(this::updateTrackedStatus, (EntityAttribute)arg));
    }

    public boolean hasAttribute(EntityAttribute attribute) {
        return this.custom.get(attribute) != null || this.fallback.method_27310(attribute);
    }

    public boolean hasModifierForAttribute(EntityAttribute attribute, UUID uuid) {
        EntityAttributeInstance lv = this.custom.get(attribute);
        return lv != null ? lv.getModifier(uuid) != null : this.fallback.method_27309(attribute, uuid);
    }

    public double getValue(EntityAttribute attribute) {
        EntityAttributeInstance lv = this.custom.get(attribute);
        return lv != null ? lv.getValue() : this.fallback.getValue(attribute);
    }

    public double getBaseValue(EntityAttribute attribute) {
        EntityAttributeInstance lv = this.custom.get(attribute);
        return lv != null ? lv.getBaseValue() : this.fallback.getBaseValue(attribute);
    }

    public double getModifierValue(EntityAttribute attribute, UUID uuid) {
        EntityAttributeInstance lv = this.custom.get(attribute);
        return lv != null ? lv.getModifier(uuid).getValue() : this.fallback.getModifierValue(attribute, uuid);
    }

    public void removeModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
        attributeModifiers.asMap().forEach((arg, collection) -> {
            EntityAttributeInstance lv = this.custom.get(arg);
            if (lv != null) {
                collection.forEach(lv::removeModifier);
            }
        });
    }

    public void addTemporaryModifiers(Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers) {
        attributeModifiers.forEach((arg, arg2) -> {
            EntityAttributeInstance lv = this.getCustomInstance((EntityAttribute)arg);
            if (lv != null) {
                lv.removeModifier((EntityAttributeModifier)arg2);
                lv.addTemporaryModifier((EntityAttributeModifier)arg2);
            }
        });
    }

    @Environment(value=EnvType.CLIENT)
    public void setFrom(AttributeContainer other) {
        other.custom.values().forEach(arg -> {
            EntityAttributeInstance lv = this.getCustomInstance(arg.getAttribute());
            if (lv != null) {
                lv.setFrom((EntityAttributeInstance)arg);
            }
        });
    }

    public ListTag toTag() {
        ListTag lv = new ListTag();
        for (EntityAttributeInstance lv2 : this.custom.values()) {
            lv.add(lv2.toTag());
        }
        return lv;
    }

    public void fromTag(ListTag tag) {
        for (int i = 0; i < tag.size(); ++i) {
            CompoundTag lv = tag.getCompound(i);
            String string = lv.getString("Name");
            Util.ifPresentOrElse(Registry.ATTRIBUTE.getOrEmpty(Identifier.tryParse(string)), arg2 -> {
                EntityAttributeInstance lv = this.getCustomInstance((EntityAttribute)arg2);
                if (lv != null) {
                    lv.fromTag(lv);
                }
            }, () -> LOGGER.warn("Ignoring unknown attribute '{}'", (Object)string));
        }
    }
}

