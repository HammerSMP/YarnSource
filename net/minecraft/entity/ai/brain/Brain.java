/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Brain<E extends LivingEntity> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Supplier<Codec<Brain<E>>> codecSupplier;
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

    public static <E extends LivingEntity> Profile<E> createProfile(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends SensorType<? extends Sensor<? super E>>> collection2) {
        return new Profile(collection, collection2);
    }

    public static <E extends LivingEntity> Codec<Brain<E>> createBrainCodec(final Collection<? extends MemoryModuleType<?>> collection, final Collection<? extends SensorType<? extends Sensor<? super E>>> collection2) {
        final MutableObject mutableObject = new MutableObject();
        mutableObject.setValue((Object)new MapCodec<Brain<E>>(){

            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                return collection.stream().flatMap(arg -> Util.stream(arg.getFactory().map(codec -> Registry.MEMORY_MODULE_TYPE.getId((MemoryModuleType<?>)arg)))).map(arg -> dynamicOps.createString(arg.toString()));
            }

            public <T> DataResult<Brain<E>> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
                MutableObject mutableObject2 = new MutableObject((Object)DataResult.success((Object)ImmutableList.builder()));
                mapLike.entries().forEach(pair -> {
                    DataResult dataResult = Registry.MEMORY_MODULE_TYPE.parse(dynamicOps, pair.getFirst());
                    DataResult dataResult2 = dataResult.flatMap(arg -> this.method_28320((MemoryModuleType)arg, dynamicOps, (Object)pair.getSecond()));
                    mutableObject2.setValue((Object)((DataResult)mutableObject2.getValue()).apply2(ImmutableList.Builder::add, dataResult2));
                });
                ImmutableList immutableList = ((DataResult)mutableObject2.getValue()).resultOrPartial(((Logger)LOGGER)::error).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
                return DataResult.success(new Brain(collection, collection2, immutableList, ((MutableObject)mutableObject)::getValue));
            }

            private <T, U> DataResult<MemoryEntry<U>> method_28320(MemoryModuleType<U> arg, DynamicOps<T> dynamicOps, T object) {
                return arg.getFactory().map(DataResult::success).orElseGet(() -> DataResult.error((String)("No codec for memory: " + arg))).flatMap(codec -> codec.parse(dynamicOps, object)).map(arg2 -> new MemoryEntry(arg, Optional.of(arg2)));
            }

            public <T> RecordBuilder<T> encode(Brain<E> arg2, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
                arg2.streamMemories().forEach(arg -> arg.serialize(dynamicOps, recordBuilder));
                return recordBuilder;
            }

            public /* synthetic */ RecordBuilder encode(Object object, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
                return this.encode((Brain)object, dynamicOps, recordBuilder);
            }
        }.fieldOf("memories").codec());
        return (Codec)mutableObject.getValue();
    }

    public Brain(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends SensorType<? extends Sensor<? super E>>> collection2, ImmutableList<MemoryEntry<?>> immutableList, Supplier<Codec<Brain<E>>> supplier) {
        this.codecSupplier = supplier;
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
        for (MemoryEntry memoryEntry : immutableList) {
            memoryEntry.apply(this);
        }
    }

    public <T> DataResult<T> encode(DynamicOps<T> dynamicOps) {
        return this.codecSupplier.get().encodeStart(dynamicOps, (Object)this);
    }

    private Stream<MemoryEntry<?>> streamMemories() {
        return this.memories.entrySet().stream().map(entry -> MemoryEntry.of((MemoryModuleType)entry.getKey(), (Optional)entry.getValue()));
    }

    public boolean hasMemoryModule(MemoryModuleType<?> arg) {
        return this.isMemoryInState(arg, MemoryModuleState.VALUE_PRESENT);
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
        this.setMemory(arg, optional.map(Memory::method_28355));
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
    public List<Task<? super E>> getRunningTasks() {
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

    public void doExclusively(Activity arg) {
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
        this.forgetIrrelevantMemories(arg);
        this.possibleActivities.clear();
        this.possibleActivities.addAll(this.coreActivities);
        this.possibleActivities.add(arg);
    }

    private void forgetIrrelevantMemories(Activity arg) {
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
                this.doExclusively(lv);
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
        Brain<E> lv = new Brain<E>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codecSupplier);
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            MemoryModuleType<?> lv2 = entry.getKey();
            if (!entry.getValue().isPresent()) continue;
            lv.memories.put(lv2, entry.getValue());
        }
        return lv;
    }

    public void tick(ServerWorld arg, E arg2) {
        this.tickMemories();
        this.tickSensors(arg, arg2);
        this.startTasks(arg, arg2);
        this.updateTasks(arg, arg2);
    }

    private void tickSensors(ServerWorld arg, E arg2) {
        for (Sensor<E> lv : this.sensors.values()) {
            lv.tick(arg, arg2);
        }
    }

    private void tickMemories() {
        for (Map.Entry<MemoryModuleType<?>, Optional<Memory<?>>> entry : this.memories.entrySet()) {
            if (!entry.getValue().isPresent()) continue;
            Memory<?> lv = entry.getValue().get();
            lv.tick();
            if (!lv.isExpired()) continue;
            this.forget(entry.getKey());
        }
    }

    public void stopAllTasks(ServerWorld arg, E arg2) {
        long l = ((LivingEntity)arg2).world.getTime();
        for (Task<E> lv : this.getRunningTasks()) {
            lv.stop(arg, arg2, l);
        }
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
        for (Task<E> lv : this.getRunningTasks()) {
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

    static final class MemoryEntry<U> {
        private final MemoryModuleType<U> type;
        private final Optional<? extends Memory<U>> data;

        private static <U> MemoryEntry<U> of(MemoryModuleType<U> arg, Optional<? extends Memory<?>> optional) {
            return new MemoryEntry<U>(arg, optional);
        }

        private MemoryEntry(MemoryModuleType<U> arg, Optional<? extends Memory<U>> optional) {
            this.type = arg;
            this.data = optional;
        }

        private void apply(Brain<?> arg) {
            ((Brain)arg).setMemory(this.type, this.data);
        }

        public <T> void serialize(DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
            this.type.getFactory().ifPresent(codec -> this.data.ifPresent(arg -> recordBuilder.add(Registry.MEMORY_MODULE_TYPE.encodeStart(dynamicOps, this.type), codec.encodeStart(dynamicOps, arg))));
        }
    }

    public static final class Profile<E extends LivingEntity> {
        private final Collection<? extends MemoryModuleType<?>> memoryModules;
        private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensors;
        private final Codec<Brain<E>> codec;

        private Profile(Collection<? extends MemoryModuleType<?>> collection, Collection<? extends SensorType<? extends Sensor<? super E>>> collection2) {
            this.memoryModules = collection;
            this.sensors = collection2;
            this.codec = Brain.createBrainCodec(collection, collection2);
        }

        public Brain<E> deserialize(Dynamic<?> dynamic) {
            return this.codec.parse(dynamic).resultOrPartial(((Logger)LOGGER)::error).orElseGet(() -> new Brain(this.memoryModules, this.sensors, ImmutableList.of(), () -> this.codec));
        }
    }
}

