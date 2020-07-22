/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  it.unimi.dsi.fastutil.objects.ObjectArraySet
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.entity.attribute;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.registry.Registry;

public class EntityAttributeInstance {
    private final EntityAttribute type;
    private final Map<EntityAttributeModifier.Operation, Set<EntityAttributeModifier>> operationToModifiers = Maps.newEnumMap(EntityAttributeModifier.Operation.class);
    private final Map<UUID, EntityAttributeModifier> byId = new Object2ObjectArrayMap();
    private final Set<EntityAttributeModifier> persistentModifiers = new ObjectArraySet();
    private double baseValue;
    private boolean dirty = true;
    private double value;
    private final Consumer<EntityAttributeInstance> updateCallback;

    public EntityAttributeInstance(EntityAttribute type, Consumer<EntityAttributeInstance> updateCallback) {
        this.type = type;
        this.updateCallback = updateCallback;
        this.baseValue = type.getDefaultValue();
    }

    public EntityAttribute getAttribute() {
        return this.type;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double baseValue) {
        if (baseValue == this.baseValue) {
            return;
        }
        this.baseValue = baseValue;
        this.onUpdate();
    }

    public Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation operation) {
        return this.operationToModifiers.computeIfAbsent(operation, arg -> Sets.newHashSet());
    }

    public Set<EntityAttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.byId.values());
    }

    @Nullable
    public EntityAttributeModifier getModifier(UUID uuid) {
        return this.byId.get(uuid);
    }

    public boolean hasModifier(EntityAttributeModifier modifier) {
        return this.byId.get(modifier.getId()) != null;
    }

    private void addModifier(EntityAttributeModifier modifier) {
        EntityAttributeModifier lv = this.byId.putIfAbsent(modifier.getId(), modifier);
        if (lv != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        this.getModifiers(modifier.getOperation()).add(modifier);
        this.onUpdate();
    }

    public void addTemporaryModifier(EntityAttributeModifier modifier) {
        this.addModifier(modifier);
    }

    public void addPersistentModifier(EntityAttributeModifier modifier) {
        this.addModifier(modifier);
        this.persistentModifiers.add(modifier);
    }

    protected void onUpdate() {
        this.dirty = true;
        this.updateCallback.accept(this);
    }

    public void removeModifier(EntityAttributeModifier modifier) {
        this.getModifiers(modifier.getOperation()).remove(modifier);
        this.byId.remove(modifier.getId());
        this.persistentModifiers.remove(modifier);
        this.onUpdate();
    }

    public void removeModifier(UUID uuid) {
        EntityAttributeModifier lv = this.getModifier(uuid);
        if (lv != null) {
            this.removeModifier(lv);
        }
    }

    public boolean tryRemoveModifier(UUID uuid) {
        EntityAttributeModifier lv = this.getModifier(uuid);
        if (lv != null && this.persistentModifiers.contains(lv)) {
            this.removeModifier(lv);
            return true;
        }
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public void clearModifiers() {
        for (EntityAttributeModifier lv : this.getModifiers()) {
            this.removeModifier(lv);
        }
    }

    public double getValue() {
        if (this.dirty) {
            this.value = this.computeValue();
            this.dirty = false;
        }
        return this.value;
    }

    private double computeValue() {
        double d = this.getBaseValue();
        for (EntityAttributeModifier lv : this.getModifiersByOperation(EntityAttributeModifier.Operation.ADDITION)) {
            d += lv.getValue();
        }
        double e = d;
        for (EntityAttributeModifier lv2 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_BASE)) {
            e += d * lv2.getValue();
        }
        for (EntityAttributeModifier lv3 : this.getModifiersByOperation(EntityAttributeModifier.Operation.MULTIPLY_TOTAL)) {
            e *= 1.0 + lv3.getValue();
        }
        return this.type.clamp(e);
    }

    private Collection<EntityAttributeModifier> getModifiersByOperation(EntityAttributeModifier.Operation arg) {
        return this.operationToModifiers.getOrDefault((Object)arg, Collections.emptySet());
    }

    public void setFrom(EntityAttributeInstance other) {
        this.baseValue = other.baseValue;
        this.byId.clear();
        this.byId.putAll(other.byId);
        this.persistentModifiers.clear();
        this.persistentModifiers.addAll(other.persistentModifiers);
        this.operationToModifiers.clear();
        other.operationToModifiers.forEach((arg, set) -> this.getModifiers((EntityAttributeModifier.Operation)((Object)arg)).addAll((Collection<EntityAttributeModifier>)set));
        this.onUpdate();
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", Registry.ATTRIBUTE.getId(this.type).toString());
        lv.putDouble("Base", this.baseValue);
        if (!this.persistentModifiers.isEmpty()) {
            ListTag lv2 = new ListTag();
            for (EntityAttributeModifier lv3 : this.persistentModifiers) {
                lv2.add(lv3.toTag());
            }
            lv.put("Modifiers", lv2);
        }
        return lv;
    }

    public void fromTag(CompoundTag tag) {
        this.baseValue = tag.getDouble("Base");
        if (tag.contains("Modifiers", 9)) {
            ListTag lv = tag.getList("Modifiers", 10);
            for (int i = 0; i < lv.size(); ++i) {
                EntityAttributeModifier lv2 = EntityAttributeModifier.fromTag(lv.getCompound(i));
                if (lv2 == null) continue;
                this.byId.put(lv2.getId(), lv2);
                this.getModifiers(lv2.getOperation()).add(lv2);
                this.persistentModifiers.add(lv2);
            }
        }
        this.onUpdate();
    }
}

