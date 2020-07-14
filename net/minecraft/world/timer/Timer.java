/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 *  com.google.common.primitives.UnsignedLong
 *  com.mojang.serialization.Dynamic
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.timer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import com.mojang.serialization.Dynamic;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.timer.TimerCallback;
import net.minecraft.world.timer.TimerCallbackSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Timer<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final TimerCallbackSerializer<T> callback;
    private final Queue<Event<T>> events = new PriorityQueue<Event<T>>(Timer.createEventComparator());
    private UnsignedLong eventCounter = UnsignedLong.ZERO;
    private final Table<String, Long, Event<T>> eventsByName = HashBasedTable.create();

    private static <T> Comparator<Event<T>> createEventComparator() {
        return Comparator.comparingLong(arg -> arg.triggerTime).thenComparing(arg -> arg.id);
    }

    public Timer(TimerCallbackSerializer<T> arg, Stream<Dynamic<Tag>> stream) {
        this(arg);
        this.events.clear();
        this.eventsByName.clear();
        this.eventCounter = UnsignedLong.ZERO;
        stream.forEach(dynamic -> {
            if (!(dynamic.getValue() instanceof CompoundTag)) {
                LOGGER.warn("Invalid format of events: {}", dynamic);
                return;
            }
            this.addEvent((CompoundTag)dynamic.getValue());
        });
    }

    public Timer(TimerCallbackSerializer<T> timerCallbackSerializer) {
        this.callback = timerCallbackSerializer;
    }

    public void processEvents(T server, long time) {
        Event<T> lv;
        while ((lv = this.events.peek()) != null && lv.triggerTime <= time) {
            this.events.remove();
            this.eventsByName.remove((Object)lv.name, (Object)time);
            lv.callback.call(server, this, time);
        }
    }

    public void setEvent(String name, long triggerTime, TimerCallback<T> callback) {
        if (this.eventsByName.contains((Object)name, (Object)triggerTime)) {
            return;
        }
        this.eventCounter = this.eventCounter.plus(UnsignedLong.ONE);
        Event lv = new Event(triggerTime, this.eventCounter, name, callback);
        this.eventsByName.put((Object)name, (Object)triggerTime, lv);
        this.events.add(lv);
    }

    public int method_22593(String string) {
        Collection collection = this.eventsByName.row((Object)string).values();
        collection.forEach(this.events::remove);
        int i = collection.size();
        collection.clear();
        return i;
    }

    public Set<String> method_22592() {
        return Collections.unmodifiableSet(this.eventsByName.rowKeySet());
    }

    private void addEvent(CompoundTag tag) {
        CompoundTag lv = tag.getCompound("Callback");
        TimerCallback<T> lv2 = this.callback.deserialize(lv);
        if (lv2 != null) {
            String string = tag.getString("Name");
            long l = tag.getLong("TriggerTime");
            this.setEvent(string, l, lv2);
        }
    }

    private CompoundTag serialize(Event<T> event) {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", event.name);
        lv.putLong("TriggerTime", event.triggerTime);
        lv.put("Callback", this.callback.serialize(event.callback));
        return lv;
    }

    public ListTag toTag() {
        ListTag lv = new ListTag();
        this.events.stream().sorted(Timer.createEventComparator()).map(this::serialize).forEach(lv::add);
        return lv;
    }

    public static class Event<T> {
        public final long triggerTime;
        public final UnsignedLong id;
        public final String name;
        public final TimerCallback<T> callback;

        private Event(long triggerTime, UnsignedLong id, String name, TimerCallback<T> callback) {
            this.triggerTime = triggerTime;
            this.id = id;
            this.name = name;
            this.callback = callback;
        }
    }
}

