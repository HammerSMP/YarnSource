/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.entity.mob;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;

public class MobVisibilityCache {
    private final MobEntity owner;
    private final List<Entity> visibleEntities = Lists.newArrayList();
    private final List<Entity> invisibleEntities = Lists.newArrayList();

    public MobVisibilityCache(MobEntity arg) {
        this.owner = arg;
    }

    public void clear() {
        this.visibleEntities.clear();
        this.invisibleEntities.clear();
    }

    public boolean canSee(Entity arg) {
        if (this.visibleEntities.contains(arg)) {
            return true;
        }
        if (this.invisibleEntities.contains(arg)) {
            return false;
        }
        this.owner.world.getProfiler().push("canSee");
        boolean bl = this.owner.canSee(arg);
        this.owner.world.getProfiler().pop();
        if (bl) {
            this.visibleEntities.add(arg);
        } else {
            this.invisibleEntities.add(arg);
        }
        return bl;
    }
}

