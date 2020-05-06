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

    public EntityAttributeInstance(EntityAttribute arg, Consumer<EntityAttributeInstance> consumer) {
        this.type = arg;
        this.updateCallback = consumer;
        this.baseValue = arg.getDefaultValue();
    }

    public EntityAttribute getAttribute() {
        return this.type;
    }

    public double getBaseValue() {
        return this.baseValue;
    }

    public void setBaseValue(double d) {
        if (d == this.baseValue) {
            return;
        }
        this.baseValue = d;
        this.onUpdate();
    }

    public Set<EntityAttributeModifier> getModifiers(EntityAttributeModifier.Operation arg2) {
        return this.operationToModifiers.computeIfAbsent(arg2, arg -> Sets.newHashSet());
    }

    public Set<EntityAttributeModifier> getModifiers() {
        return ImmutableSet.copyOf(this.byId.values());
    }

    @Nullable
    public EntityAttributeModifier getModifier(UUID uUID) {
        return this.byId.get(uUID);
    }

    public boolean hasModifier(EntityAttributeModifier arg) {
        return this.byId.get(arg.getId()) != null;
    }

    private void addModifier(EntityAttributeModifier arg) {
        EntityAttributeModifier lv = this.byId.putIfAbsent(arg.getId(), arg);
        if (lv != null) {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        this.getModifiers(arg.getOperation()).add(arg);
        this.onUpdate();
    }

    public void addTemporaryModifier(EntityAttributeModifier arg) {
        this.addModifier(arg);
    }

    public void addPersistentModifier(EntityAttributeModifier arg) {
        this.addModifier(arg);
        this.persistentModifiers.add(arg);
    }

    protected void onUpdate() {
        this.dirty = true;
        this.updateCallback.accept(this);
    }

    public void removeModifier(EntityAttributeModifier arg) {
        this.getModifiers(arg.getOperation()).remove(arg);
        this.byId.remove(arg.getId());
        this.persistentModifiers.remove(arg);
        this.onUpdate();
    }

    public void removeModifier(UUID uUID) {
        EntityAttributeModifier lv = this.getModifier(uUID);
        if (lv != null) {
            this.removeModifier(lv);
        }
    }

    public boolean tryRemoveModifier(UUID uUID) {
        EntityAttributeModifier lv = this.getModifier(uUID);
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

    public void setFrom(EntityAttributeInstance arg2) {
        this.baseValue = arg2.baseValue;
        this.byId.clear();
        this.byId.putAll(arg2.byId);
        this.persistentModifiers.clear();
        this.persistentModifiers.addAll(arg2.persistentModifiers);
        this.operationToModifiers.clear();
        arg2.operationToModifiers.forEach((arg, set) -> this.getModifiers((EntityAttributeModifier.Operation)((Object)arg)).addAll((Collection<EntityAttributeModifier>)set));
        this.onUpdate();
    }

    public CompoundTag toTag() {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", Registry.ATTRIBUTES.getId(this.type).toString());
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

    public void fromTag(CompoundTag arg) {
        this.baseValue = arg.getDouble("Base");
        if (arg.contains("Modifiers", 9)) {
            ListTag lv = arg.getList("Modifiers", 10);
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

