/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.types.DynamicOps
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.DynamicSerializable;
import net.minecraft.util.registry.Registry;

public class Brain<E extends LivingEntity>
implements DynamicSerializable {
    private final Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories = Maps.newHashMap();
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
    private final Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks = Maps.newTreeMap();
    private Schedule schedule = Schedule.EMPTY;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>> requiredActivityMemories = Maps.newHashMap();
    private final Map<Activity, Set<MemoryModuleType<?>>> forgettingActivityMemories = Maps.newHashMap();
    private Set<Activity> coreActivities = Sets.newHashSet();
    private final Set<Activity> possibleActivities = Sets.newHashSet();
    private Activity defaultActivity = Activity.IDLE;
    private long activityStartTime = -9999L;

    public <T> Brain(Collection<MemoryModuleType<?>> collection, Collection<SensorType<? extends Sensor<? super E>>> collection2, Dynamic<T> dynamic) {
        for (MemoryModuleType<?> memoryModuleType : collection) {
            this.memories.put(memoryModuleType, Optional.empty());
        }
        for (SensorType sensorType : collection2) {
            this.sensors.put(sensorType, (Sensor<E>)sensorType.create());
        }
        for (Sensor sensor : this.sensors.values()) {
            for (MemoryModuleType<?> lv4 : sensor.getOutputMemoryModules()) {
                this.memories.put(lv4, Optional.empty());
            }
        }
        for (Map.Entry entry : dynamic.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
            this.readMemory(Registry.MEMORY_MODULE_TYPE.get(new Identifier(((Dynamic)entry.getKey()).asString(""))), (Dynamic)entry.getValue());
        }
    }

    public boolean hasMemoryModule(MemoryModuleType<?> arg) {
        return this.isMemoryInState(arg, MemoryModuleState.VALUE_PRESENT);
    }

    private <T, U> void readMemory(MemoryModuleType<U> arg, Dynamic<T> dynamic) {
        Memory<U> lv = new Memory<U>(arg.getFactory().orElseThrow(RuntimeException::new), dynamic);
        this.setMemory(arg, Optional.of(lv));
    }

    public <U> void forget(MemoryModuleType<U> arg) {
        this.remember(arg, Optional.empty());
    }

    public <U> void remember(MemoryModuleType<U> arg, @Nullable U object) {
        this.remember(arg, Optional.ofNullable(object));
    }

    public <U> void remember(MemoryModuleType<U> arg, U object, long l) {
        this.setMemory(arg, Optional.of(Memory.timed(object, l)));
    }

    public <U> void remember(MemoryModuleType<U> arg, Optional<? extends U> optional) {
        this.setMemory(arg, optional.map(Memory::permanent));
    }

    private <U> void setMemory(MemoryModuleType<U> arg, Optional<? extends Memory<?>> optional) {
        if (this.memories.containsKey(arg)) {
            if (optional.isPresent() && this.isEmptyCollection(optional.get().getValue())) {
                this.forget(arg);
            } else {
                this.memories.put(arg, optional);
            }
        }
    }

    public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> arg) {
        return this.memories.get(arg).map(Memory::getValue);
    }

    public boolean isMemoryInState(MemoryModuleType<?> arg, MemoryModuleState arg2) {
        Optional<Memory<?>> optional = this.memories.get(arg);
        if (optional == null) {
            return false;
        }
        return arg2 == MemoryModuleState.REGISTERED || arg2 == MemoryModuleState.VALUE_PRESENT && optional.isPresent() || arg2 == MemoryModuleState.VALUE_ABSENT && !optional.isPresent();
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule arg) {
        this.schedule = arg;
    }

    public void setCoreActivities(Set<Activity> set) {
        this.coreActivities = set;
    }

    @Deprecated
    public List<Task<? super E>> method_27074() {
        ObjectArrayList list = new ObjectArrayList();
        for (Map<Activity, Set<Task<E>>> map : this.tasks.values()) {
            for (Set<Task<E>> set : map.values()) {
                for (Task<E> lv : set) {
                    if (lv.getStatus() != Task.Status.RUNNING) continue;
                    list.add(lv);
                }
            }
        }
        return list;
    }

    public void resetPossibleActivities() {
        this.resetPossibleActivities(this.defaultActivity);
    }

    public Optional<Activity> getFirstPossibleNonCoreActivity() {
        for (Activity lv : this.possibleActivities) {
            if (this.coreActivities.contains(lv)) continue;
            return Optional.of(lv);
        }
        return Optional.empty();
    }

    public void method_24526(Activity arg) {
        if (this.canDoActivity(arg)) {
            this.resetPossibleActivities(arg);
        } else {
            this.resetPossibleActivities();
        }
    }

    private void resetPossibleActivities(Activity arg) {
        if (this.hasActivity(arg)) {
            return;
        }
        this.method_24537(arg);
        this.possibleActivities.clear();
        this.possibleActivities.addAll(this.coreActivities);
        this.possibleActivities.add(arg);
    }

    private void method_24537(Activity arg) {
        for (Activity lv : this.possibleActivities) {
            Set<MemoryModuleType<?>> set;
            if (lv == arg || (set = this.forgettingActivityMemories.get(lv)) == null) continue;
            for (MemoryModuleType<?> lv2 : set) {
                this.forget(lv2);
            }
        }
    }

    public void refreshActivities(long l, long m) {
        if (m - this.activityStartTime > 20L) {
            this.activityStartTime = m;
            Activity lv = this.getSchedule().getActivityForTime((int)(l % 24000L));
            if (!this.possibleActivities.contains(lv)) {
                this.method_24526(lv);
            }
        }
    }

    public void resetPossibleActivities(List<Activity> list) {
        for (Activity lv : list) {
            if (!this.canDoActivity(lv)) continue;
            this.resetPossibleActivities(lv);
            break;
        }
    }

    public void setDefaultActivity(Activity arg) {
        this.defaultActivity = arg;
    }

    public void setTaskList(Activity arg, int i, ImmutableList<? extends Task<? super E>> immutableList) {
        this.setTaskList(arg, this.indexTaskList(i, immutableList));
    }

    public void setTaskList(Activity arg, int i, ImmutableList<? extends Task<? super E>> immutableList, MemoryModuleType<?> arg2) {
        ImmutableSet set = ImmutableSet.of((Object)Pair.of(arg2, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        ImmutableSet set2 = ImmutableSet.of(arg2);
        this.setTaskList(arg, (ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>>)this.indexTaskList(i, immutableList), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)set, (Set<MemoryModuleType<?>>)set2);
    }

    public void setTaskList(Activity arg, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> immutableList) {
        this.setTaskList(arg, immutableList, (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of(), Sets.newHashSet());
    }

    public void setTaskList(Activity arg, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> immutableList, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set) {
        this.setTaskList(arg, immutableList, set, Sets.newHashSet());
    }

    private void setTaskList(Activity arg2, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> immutableList, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set, Set<MemoryModuleType<?>> set2) {
        this.requiredActivityMemories.put(arg2, set);
        if (!set2.isEmpty()) {
            this.forgettingActivityMemories.put(arg2, set2);
        }
        for (Pair pair : immutableList) {
            this.tasks.computeIfAbsent((Integer)pair.getFirst(), integer -> Maps.newHashMap()).computeIfAbsent(arg2, arg -> Sets.newLinkedHashSet()).add(pair.getSecond());
        }
    }

    public boolean hasActivity(Activity arg) {
        return this.possibleActivities.contains(arg);
    }

    public Brain<E> copy() {
        Brain<E> lv = new Brain<E>(this.memories.keySet(), this.sensors.keySet(), new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)new CompoundTag()));
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            MemoryModuleType<?> lv2 = entry.getKey();
            if (!entry.getValue().isPresent()) continue;
            lv.memories.put(lv2, entry.getValue());
        }
        return lv;
    }

    public void tick(ServerWorld arg, E arg2) {
        this.method_27075();
        this.method_27073(arg, arg2);
        this.startTasks(arg, arg2);
        this.updateTasks(arg, arg2);
    }

    private void method_27073(ServerWorld arg, E arg2) {
        for (Sensor<E> lv : this.sensors.values()) {
            lv.canSense(arg, arg2);
        }
    }

    private void method_27075() {
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            if (!entry.getValue().isPresent()) continue;
            Memory<?> lv = entry.getValue().get();
            lv.method_24913();
            if (!lv.isExpired()) continue;
            this.forget(entry.getKey());
        }
    }

    public void stopAllTasks(ServerWorld arg, E arg2) {
        long l = ((LivingEntity)arg2).world.getTime();
        for (Task<E> lv : this.method_27074()) {
            lv.stop(arg, arg2, l);
        }
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            MemoryModuleType<?> lv = entry.getKey();
            if (!entry.getValue().isPresent() || !lv.getFactory().isPresent()) continue;
            Memory<?> lv2 = entry.getValue().get();
            Object object = dynamicOps.createString(Registry.MEMORY_MODULE_TYPE.getId(lv).toString());
            Object object2 = lv2.serialize(dynamicOps);
            builder.put(object, object2);
        }
        return (T)dynamicOps.createMap((Map)ImmutableMap.of((Object)dynamicOps.createString("memories"), (Object)dynamicOps.createMap((Map)builder.build())));
    }

    private void startTasks(ServerWorld arg, E arg2) {
        long l = arg.getTime();
        for (Map<Activity, Set<Task<E>>> map : this.tasks.values()) {
            for (Map.Entry<Activity, Set<Task<E>>> entry : map.entrySet()) {
                Activity lv = entry.getKey();
                if (!this.possibleActivities.contains(lv)) continue;
                Set<Task<E>> set = entry.getValue();
                for (Task<E> lv2 : set) {
                    if (lv2.getStatus() != Task.Status.STOPPED) continue;
                    lv2.tryStarting(arg, arg2, l);
                }
            }
        }
    }

    private void updateTasks(ServerWorld arg, E arg2) {
        long l = arg.getTime();
        for (Task<E> lv : this.method_27074()) {
            lv.tick(arg, arg2, l);
        }
    }

    private boolean canDoActivity(Activity arg) {
        if (!this.requiredActivityMemories.containsKey(arg)) {
            return false;
        }
        for (Pair<MemoryModuleType<?>, MemoryModuleState> pair : this.requiredActivityMemories.get(arg)) {
            MemoryModuleState lv2;
            MemoryModuleType lv = (MemoryModuleType)pair.getFirst();
            if (this.isMemoryInState(lv, lv2 = (MemoryModuleState)((Object)pair.getSecond()))) continue;
            return false;
        }
        return true;
    }

    private boolean isEmptyCollection(Object object) {
        return object instanceof Collection && ((Collection)object).isEmpty();
    }

    ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexTaskList(int i, ImmutableList<? extends Task<? super E>> immutableList) {
        int j = i;
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Task lv : immutableList) {
            builder.add((Object)Pair.of((Object)j++, (Object)lv));
        }
        return builder.build();
    }
}

