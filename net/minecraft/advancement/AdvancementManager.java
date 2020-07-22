/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.advancement;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.Advancement;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<Identifier, Advancement> advancements = Maps.newHashMap();
    private final Set<Advancement> roots = Sets.newLinkedHashSet();
    private final Set<Advancement> dependents = Sets.newLinkedHashSet();
    private Listener listener;

    @Environment(value=EnvType.CLIENT)
    private void remove(Advancement advancement) {
        for (Advancement lv : advancement.getChildren()) {
            this.remove(lv);
        }
        LOGGER.info("Forgot about advancement {}", (Object)advancement.getId());
        this.advancements.remove(advancement.getId());
        if (advancement.getParent() == null) {
            this.roots.remove(advancement);
            if (this.listener != null) {
                this.listener.onRootRemoved(advancement);
            }
        } else {
            this.dependents.remove(advancement);
            if (this.listener != null) {
                this.listener.onDependentRemoved(advancement);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public void removeAll(Set<Identifier> advancements) {
        for (Identifier lv : advancements) {
            Advancement lv2 = this.advancements.get(lv);
            if (lv2 == null) {
                LOGGER.warn("Told to remove advancement {} but I don't know what that is", (Object)lv);
                continue;
            }
            this.remove(lv2);
        }
    }

    public void load(Map<Identifier, Advancement.Task> map) {
        Function function = Functions.forMap(this.advancements, null);
        while (!map.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, Advancement.Task>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Advancement.Task> entry = iterator.next();
                Identifier lv = entry.getKey();
                Advancement.Task lv2 = entry.getValue();
                if (!lv2.findParent((java.util.function.Function<Identifier, Advancement>)function)) continue;
                Advancement lv3 = lv2.build(lv);
                this.advancements.put(lv, lv3);
                bl = true;
                iterator.remove();
                if (lv3.getParent() == null) {
                    this.roots.add(lv3);
                    if (this.listener == null) continue;
                    this.listener.onRootAdded(lv3);
                    continue;
                }
                this.dependents.add(lv3);
                if (this.listener == null) continue;
                this.listener.onDependentAdded(lv3);
            }
            if (bl) continue;
            for (Map.Entry<Identifier, Advancement.Task> entry2 : map.entrySet()) {
                LOGGER.error("Couldn't load advancement {}: {}", (Object)entry2.getKey(), (Object)entry2.getValue());
            }
        }
        LOGGER.info("Loaded {} advancements", (Object)this.advancements.size());
    }

    @Environment(value=EnvType.CLIENT)
    public void clear() {
        this.advancements.clear();
        this.roots.clear();
        this.dependents.clear();
        if (this.listener != null) {
            this.listener.onClear();
        }
    }

    public Iterable<Advancement> getRoots() {
        return this.roots;
    }

    public Collection<Advancement> getAdvancements() {
        return this.advancements.values();
    }

    @Nullable
    public Advancement get(Identifier id) {
        return this.advancements.get(id);
    }

    @Environment(value=EnvType.CLIENT)
    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
        if (listener != null) {
            for (Advancement lv : this.roots) {
                listener.onRootAdded(lv);
            }
            for (Advancement lv2 : this.dependents) {
                listener.onDependentAdded(lv2);
            }
        }
    }

    public static interface Listener {
        public void onRootAdded(Advancement var1);

        @Environment(value=EnvType.CLIENT)
        public void onRootRemoved(Advancement var1);

        public void onDependentAdded(Advancement var1);

        @Environment(value=EnvType.CLIENT)
        public void onDependentRemoved(Advancement var1);

        @Environment(value=EnvType.CLIENT)
        public void onClear();
    }
}

