/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Table
 *  com.google.common.primitives.UnsignedLong
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.timer;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
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

    public Timer(TimerCallbackSerializer<T> arg) {
        this.callback = arg;
    }

    public void processEvents(T object, long l) {
        Event<T> lv;
        while ((lv = this.events.peek()) != null && lv.triggerTime <= l) {
            this.events.remove();
            this.eventsByName.remove((Object)lv.name, (Object)l);
            lv.callback.call(object, this, l);
        }
    }

    public void setEvent(String string, long l, TimerCallback<T> arg) {
        if (this.eventsByName.contains((Object)string, (Object)l)) {
            return;
        }
        this.eventCounter = this.eventCounter.plus(UnsignedLong.ONE);
        Event lv = new Event(l, this.eventCounter, string, arg);
        this.eventsByName.put((Object)string, (Object)l, lv);
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

    private void addEvent(CompoundTag arg) {
        CompoundTag lv = arg.getCompound("Callback");
        TimerCallback<T> lv2 = this.callback.deserialize(lv);
        if (lv2 != null) {
            String string = arg.getString("Name");
            long l = arg.getLong("TriggerTime");
            this.setEvent(string, l, lv2);
        }
    }

    public void fromTag(ListTag arg) {
        this.events.clear();
        this.eventsByName.clear();
        this.eventCounter = UnsignedLong.ZERO;
        if (arg.isEmpty()) {
            return;
        }
        if (arg.getElementType() != 10) {
            LOGGER.warn("Invalid format of events: " + arg);
            return;
        }
        for (Tag lv : arg) {
            this.addEvent((CompoundTag)lv);
        }
    }

    private CompoundTag serialize(Event<T> arg) {
        CompoundTag lv = new CompoundTag();
        lv.putString("Name", arg.name);
        lv.putLong("TriggerTime", arg.triggerTime);
        lv.put("Callback", this.callback.serialize(arg.callback));
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

        private Event(long l, UnsignedLong unsignedLong, String string, TimerCallback<T> arg) {
            this.triggerTime = l;
            this.id = unsignedLong;
            this.name = string;
            this.callback = arg;
        }
    }
}

