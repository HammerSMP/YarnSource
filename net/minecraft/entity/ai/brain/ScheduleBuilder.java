/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Schedule;

public class ScheduleBuilder {
    private final Schedule schedule;
    private final List<ActivityEntry> activities = Lists.newArrayList();

    public ScheduleBuilder(Schedule arg) {
        this.schedule = arg;
    }

    public ScheduleBuilder withActivity(int i, Activity arg) {
        this.activities.add(new ActivityEntry(i, arg));
        return this;
    }

    public Schedule build() {
        this.activities.stream().map(ActivityEntry::getActivity).collect(Collectors.toSet()).forEach(this.schedule::addActivity);
        this.activities.forEach(arg -> {
            Activity lv = arg.getActivity();
            this.schedule.getOtherRules(lv).forEach(arg2 -> arg2.add(arg.getStartTime(), 0.0f));
            this.schedule.getRule(lv).add(arg.getStartTime(), 1.0f);
        });
        return this.schedule;
    }

    static class ActivityEntry {
        private final int startTime;
        private final Activity activity;

        public ActivityEntry(int i, Activity arg) {
            this.startTime = i;
            this.activity = arg;
        }

        public int getStartTime() {
            return this.startTime;
        }

        public Activity getActivity() {
            return this.activity;
        }
    }
}

