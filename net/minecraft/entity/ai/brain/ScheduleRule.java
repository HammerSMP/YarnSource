/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap
 */
package net.minecraft.entity.ai.brain;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.Collection;
import java.util.List;
import net.minecraft.entity.ai.brain.ScheduleRuleEntry;

public class ScheduleRule {
    private final List<ScheduleRuleEntry> entries = Lists.newArrayList();
    private int prioritizedEntryIndex;

    public ScheduleRule add(int i, float f) {
        this.entries.add(new ScheduleRuleEntry(i, f));
        this.sort();
        return this;
    }

    private void sort() {
        Int2ObjectAVLTreeMap int2ObjectSortedMap = new Int2ObjectAVLTreeMap();
        this.entries.forEach(arg_0 -> ScheduleRule.method_19228((Int2ObjectSortedMap)int2ObjectSortedMap, arg_0));
        this.entries.clear();
        this.entries.addAll((Collection<ScheduleRuleEntry>)int2ObjectSortedMap.values());
        this.prioritizedEntryIndex = 0;
    }

    public float getPriority(int i) {
        ScheduleRuleEntry lv3;
        if (this.entries.size() <= 0) {
            return 0.0f;
        }
        ScheduleRuleEntry lv = this.entries.get(this.prioritizedEntryIndex);
        ScheduleRuleEntry lv2 = this.entries.get(this.entries.size() - 1);
        boolean bl = i < lv.getStartTime();
        int j = bl ? 0 : this.prioritizedEntryIndex;
        float f = bl ? lv2.getPriority() : lv.getPriority();
        int k = j;
        while (k < this.entries.size() && (lv3 = this.entries.get(k)).getStartTime() <= i) {
            this.prioritizedEntryIndex = k++;
            f = lv3.getPriority();
        }
        return f;
    }

    private static /* synthetic */ void method_19228(Int2ObjectSortedMap int2ObjectSortedMap, ScheduleRuleEntry arg) {
        ScheduleRuleEntry cfr_ignored_0 = (ScheduleRuleEntry)int2ObjectSortedMap.put(arg.getStartTime(), (Object)arg);
    }
}

