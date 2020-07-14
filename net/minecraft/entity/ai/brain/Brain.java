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

    public static <E extends LivingEntity> Profile<E> createProfile(Collection<? extends MemoryModuleType<?>> memoryModules, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors) {
        return new Profile(memoryModules, sensors);
    }

    public static <E extends LivingEntity> Codec<Brain<E>> createBrainCodec(final Collection<? extends MemoryModuleType<?>> memoryModules, final Collection<? extends SensorType<? extends Sensor<? super E>>> sensors) {
        final MutableObject mutableObject = new MutableObject();
        mutableObject.setValue((Object)new MapCodec<Brain<E>>(){

            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                return memoryModules.stream().flatMap(arg -> Util.stream(arg.getFactory().map(codec -> Registry.MEMORY_MODULE_TYPE.getId((MemoryModuleType<?>)arg)))).map(arg -> dynamicOps.createString(arg.toString()));
            }

            public <T> DataResult<Brain<E>> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
                MutableObject mutableObject2 = new MutableObject((Object)DataResult.success((Object)ImmutableList.builder()));
                mapLike.entries().forEach(pair -> {
                    DataResult dataResult = Registry.MEMORY_MODULE_TYPE.parse(dynamicOps, pair.getFirst());
                    DataResult dataResult2 = dataResult.flatMap(arg -> this.method_28320((MemoryModuleType)arg, dynamicOps, (Object)pair.getSecond()));
                    mutableObject2.setValue((Object)((DataResult)mutableObject2.getValue()).apply2(ImmutableList.Builder::add, dataResult2));
                });
                ImmutableList immutableList = ((DataResult)mutableObject2.getValue()).resultOrPartial(((Logger)LOGGER)::error).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
                return DataResult.success(new Brain(memoryModules, sensors, immutableList, ((MutableObject)mutableObject)::getValue));
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

    public Brain(Collection<? extends MemoryModuleType<?>> memories, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors, ImmutableList<MemoryEntry<?>> memoryEntries, Supplier<Codec<Brain<E>>> codecSupplier) {
        this.codecSupplier = codecSupplier;
        for (MemoryModuleType<?> memoryModuleType : memories) {
            this.memories.put(memoryModuleType, Optional.empty());
        }
        for (SensorType sensorType : sensors) {
            this.sensors.put(sensorType, (Sensor<E>)sensorType.create());
        }
        for (Sensor sensor : this.sensors.values()) {
            for (MemoryModuleType<?> lv4 : sensor.getOutputMemoryModules()) {
                this.memories.put(lv4, Optional.empty());
            }
        }
        for (MemoryEntry memoryEntry : memoryEntries) {
            memoryEntry.apply(this);
        }
    }

    public <T> DataResult<T> encode(DynamicOps<T> ops) {
        return this.codecSupplier.get().encodeStart(ops, (Object)this);
    }

    private Stream<MemoryEntry<?>> streamMemories() {
        return this.memories.entrySet().stream().map(entry -> MemoryEntry.of((MemoryModuleType)entry.getKey(), (Optional)entry.getValue()));
    }

    public boolean hasMemoryModule(MemoryModuleType<?> type) {
        return this.isMemoryInState(type, MemoryModuleState.VALUE_PRESENT);
    }

    public <U> void forget(MemoryModuleType<U> type) {
        this.remember(type, Optional.empty());
    }

    public <U> void remember(MemoryModuleType<U> type, @Nullable U value) {
        this.remember(type, Optional.ofNullable(value));
    }

    public <U> void remember(MemoryModuleType<U> type, U value, long startTime) {
        this.setMemory(type, Optional.of(Memory.timed(value, startTime)));
    }

    public <U> void remember(MemoryModuleType<U> type, Optional<? extends U> value) {
        this.setMemory(type, value.map(Memory::method_28355));
    }

    private <U> void setMemory(MemoryModuleType<U> type, Optional<? extends Memory<?>> memory) {
        if (this.memories.containsKey(type)) {
            if (memory.isPresent() && this.isEmptyCollection(memory.get().getValue())) {
                this.forget(type);
            } else {
                this.memories.put(type, memory);
            }
        }
    }

    public <U> Optional<U> getOptionalMemory(MemoryModuleType<U> type) {
        return this.memories.get(type).map(Memory::getValue);
    }

    public <U> boolean method_29519(MemoryModuleType<U> arg, U object) {
        if (!this.hasMemoryModule(arg)) {
            return false;
        }
        return this.getOptionalMemory(arg).filter(object2 -> object2.equals(object)).isPresent();
    }

    public boolean isMemoryInState(MemoryModuleType<?> type, MemoryModuleState state) {
        Optional<Memory<?>> optional = this.memories.get(type);
        if (optional == null) {
            return false;
        }
        return state == MemoryModuleState.REGISTERED || state == MemoryModuleState.VALUE_PRESENT && optional.isPresent() || state == MemoryModuleState.VALUE_ABSENT && !optional.isPresent();
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setCoreActivities(Set<Activity> coreActivities) {
        this.coreActivities = coreActivities;
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

    public void doExclusively(Activity activity) {
        if (this.canDoActivity(activity)) {
            this.resetPossibleActivities(activity);
        } else {
            this.resetPossibleActivities();
        }
    }

    private void resetPossibleActivities(Activity except) {
        if (this.hasActivity(except)) {
            return;
        }
        this.forgetIrrelevantMemories(except);
        this.possibleActivities.clear();
        this.possibleActivities.addAll(this.coreActivities);
        this.possibleActivities.add(except);
    }

    private void forgetIrrelevantMemories(Activity except) {
        for (Activity lv : this.possibleActivities) {
            Set<MemoryModuleType<?>> set;
            if (lv == except || (set = this.forgettingActivityMemories.get(lv)) == null) continue;
            for (MemoryModuleType<?> lv2 : set) {
                this.forget(lv2);
            }
        }
    }

    public void refreshActivities(long timeOfDay, long time) {
        if (time - this.activityStartTime > 20L) {
            this.activityStartTime = time;
            Activity lv = this.getSchedule().getActivityForTime((int)(timeOfDay % 24000L));
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

    public void setDefaultActivity(Activity activity) {
        this.defaultActivity = activity;
    }

    public void setTaskList(Activity activity, int begin, ImmutableList<? extends Task<? super E>> list) {
        this.setTaskList(activity, this.indexTaskList(begin, list));
    }

    public void setTaskList(Activity activity, int begin, ImmutableList<? extends Task<? super E>> tasks, MemoryModuleType<?> memoryType) {
        ImmutableSet set = ImmutableSet.of((Object)Pair.of(memoryType, (Object)((Object)MemoryModuleState.VALUE_PRESENT)));
        ImmutableSet set2 = ImmutableSet.of(memoryType);
        this.setTaskList(activity, (ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>>)this.indexTaskList(begin, tasks), (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)set, (Set<MemoryModuleType<?>>)set2);
    }

    public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks) {
        this.setTaskList(activity, indexedTasks, (Set<Pair<MemoryModuleType<?>, MemoryModuleState>>)ImmutableSet.of(), Sets.newHashSet());
    }

    public void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories) {
        this.setTaskList(activity, indexedTasks, requiredMemories, Sets.newHashSet());
    }

    private void setTaskList(Activity activity, ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexedTasks, Set<Pair<MemoryModuleType<?>, MemoryModuleState>> requiredMemories, Set<MemoryModuleType<?>> forgettingMemories) {
        this.requiredActivityMemories.put(activity, requiredMemories);
        if (!forgettingMemories.isEmpty()) {
            this.forgettingActivityMemories.put(activity, forgettingMemories);
        }
        for (Pair pair : indexedTasks) {
            this.tasks.computeIfAbsent((Integer)pair.getFirst(), integer -> Maps.newHashMap()).computeIfAbsent(activity, arg -> Sets.newLinkedHashSet()).add(pair.getSecond());
        }
    }

    public boolean hasActivity(Activity activity) {
        return this.possibleActivities.contains(activity);
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

    public void tick(ServerWorld world, E entity) {
        this.tickMemories();
        this.tickSensors(world, entity);
        this.startTasks(world, entity);
        this.updateTasks(world, entity);
    }

    private void tickSensors(ServerWorld world, E entity) {
        for (Sensor<E> lv : this.sensors.values()) {
            lv.tick(world, entity);
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

    public void stopAllTasks(ServerWorld world, E entity) {
        long l = ((LivingEntity)entity).world.getTime();
        for (Task<E> lv : this.getRunningTasks()) {
            lv.stop(world, entity, l);
        }
    }

    private void startTasks(ServerWorld world, E entity) {
        long l = world.getTime();
        for (Map<Activity, Set<Task<E>>> map : this.tasks.values()) {
            for (Map.Entry<Activity, Set<Task<E>>> entry : map.entrySet()) {
                Activity lv = entry.getKey();
                if (!this.possibleActivities.contains(lv)) continue;
                Set<Task<E>> set = entry.getValue();
                for (Task<E> lv2 : set) {
                    if (lv2.getStatus() != Task.Status.STOPPED) continue;
                    lv2.tryStarting(world, entity, l);
                }
            }
        }
    }

    private void updateTasks(ServerWorld world, E entity) {
        long l = world.getTime();
        for (Task<E> lv : this.getRunningTasks()) {
            lv.tick(world, entity, l);
        }
    }

    private boolean canDoActivity(Activity activity) {
        if (!this.requiredActivityMemories.containsKey(activity)) {
            return false;
        }
        for (Pair<MemoryModuleType<?>, MemoryModuleState> pair : this.requiredActivityMemories.get(activity)) {
            MemoryModuleState lv2;
            MemoryModuleType lv = (MemoryModuleType)pair.getFirst();
            if (this.isMemoryInState(lv, lv2 = (MemoryModuleState)((Object)pair.getSecond()))) continue;
            return false;
        }
        return true;
    }

    private boolean isEmptyCollection(Object value) {
        return value instanceof Collection && ((Collection)value).isEmpty();
    }

    ImmutableList<? extends Pair<Integer, ? extends Task<? super E>>> indexTaskList(int begin, ImmutableList<? extends Task<? super E>> tasks) {
        int j = begin;
        ImmutableList.Builder builder = ImmutableList.builder();
        for (Task lv : tasks) {
            builder.add((Object)Pair.of((Object)j++, (Object)lv));
        }
        return builder.build();
    }

    static final class MemoryEntry<U> {
        private final MemoryModuleType<U> type;
        private final Optional<? extends Memory<U>> data;

        private static <U> MemoryEntry<U> of(MemoryModuleType<U> type, Optional<? extends Memory<?>> data) {
            return new MemoryEntry<U>(type, data);
        }

        private MemoryEntry(MemoryModuleType<U> type, Optional<? extends Memory<U>> data) {
            this.type = type;
            this.data = data;
        }

        private void apply(Brain<?> brain) {
            ((Brain)brain).setMemory(this.type, this.data);
        }

        public <T> void serialize(DynamicOps<T> ops, RecordBuilder<T> builder) {
            this.type.getFactory().ifPresent(codec -> this.data.ifPresent(arg -> builder.add(Registry.MEMORY_MODULE_TYPE.encodeStart(ops, this.type), codec.encodeStart(ops, arg))));
        }
    }

    public static final class Profile<E extends LivingEntity> {
        private final Collection<? extends MemoryModuleType<?>> memoryModules;
        private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensors;
        private final Codec<Brain<E>> codec;

        private Profile(Collection<? extends MemoryModuleType<?>> memoryModules, Collection<? extends SensorType<? extends Sensor<? super E>>> sensors) {
            this.memoryModules = memoryModules;
            this.sensors = sensors;
            this.codec = Brain.createBrainCodec(memoryModules, sensors);
        }

        public Brain<E> deserialize(Dynamic<?> data) {
            return this.codec.parse(data).resultOrPartial(((Logger)LOGGER)::error).orElseGet(() -> new Brain(this.memoryModules, this.sensors, ImmutableList.of(), () -> this.codec));
        }
    }
}

