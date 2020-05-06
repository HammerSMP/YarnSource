/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.ScheduleBuilder;
import net.minecraft.entity.ai.brain.ScheduleRule;
import net.minecraft.util.registry.Registry;

public class Schedule {
    public static final Schedule EMPTY = Schedule.register("empty").withActivity(0, Activity.IDLE).build();
    public static final Schedule SIMPLE = Schedule.register("simple").withActivity(5000, Activity.WORK).withActivity(11000, Activity.REST).build();
    public static final Schedule VILLAGER_BABY = Schedule.register("villager_baby").withActivity(10, Activity.IDLE).withActivity(3000, Activity.PLAY).withActivity(6000, Activity.IDLE).withActivity(10000, Activity.PLAY).withActivity(12000, Activity.REST).build();
    public static final Schedule VILLAGER_DEFAULT = Schedule.register("villager_default").withActivity(10, Activity.IDLE).withActivity(2000, Activity.WORK).withActivity(9000, Activity.MEET).withActivity(11000, Activity.IDLE).withActivity(12000, Activity.REST).build();
    private final Map<Activity, ScheduleRule> scheduleRules = Maps.newHashMap();

    protected static ScheduleBuilder register(String string) {
        Schedule lv = Registry.register(Registry.SCHEDULE, string, new Schedule());
        return new ScheduleBuilder(lv);
    }

    protected void addActivity(Activity arg) {
        if (!this.scheduleRules.containsKey(arg)) {
            this.scheduleRules.put(arg, new ScheduleRule());
        }
    }

    protected ScheduleRule getRule(Activity arg) {
        return this.scheduleRules.get(arg);
    }

    protected List<ScheduleRule> getOtherRules(Activity arg) {
        return this.scheduleRules.entrySet().stream().filter(entry -> entry.getKey() != arg).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Activity getActivityForTime(int i) {
        return this.scheduleRules.entrySet().stream().max(Comparator.comparingDouble(entry -> ((ScheduleRule)entry.getValue()).getPriority(i))).map(Map.Entry::getKey).orElse(Activity.IDLE);
    }
}

