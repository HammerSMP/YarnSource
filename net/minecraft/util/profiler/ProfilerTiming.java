/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class ProfilerTiming
implements Comparable<ProfilerTiming> {
    public final double parentSectionUsagePercentage;
    public final double totalUsagePercentage;
    public final long visitCount;
    public final String name;

    public ProfilerTiming(String name, double parentUsagePercentage, double totalUsagePercentage, long visitCount) {
        this.name = name;
        this.parentSectionUsagePercentage = parentUsagePercentage;
        this.totalUsagePercentage = totalUsagePercentage;
        this.visitCount = visitCount;
    }

    @Override
    public int compareTo(ProfilerTiming arg) {
        if (arg.parentSectionUsagePercentage < this.parentSectionUsagePercentage) {
            return -1;
        }
        if (arg.parentSectionUsagePercentage > this.parentSectionUsagePercentage) {
            return 1;
        }
        return arg.name.compareTo(this.name);
    }

    @Environment(value=EnvType.CLIENT)
    public int getColor() {
        return (this.name.hashCode() & 0xAAAAAA) + 0x444444;
    }

    @Override
    public /* synthetic */ int compareTo(Object object) {
        return this.compareTo((ProfilerTiming)object);
    }
}

