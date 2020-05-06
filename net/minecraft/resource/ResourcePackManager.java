/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Functions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 */
package net.minecraft.resource;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;

public class ResourcePackManager<T extends ResourcePackProfile>
implements AutoCloseable {
    private final Set<ResourcePackProvider> providers = Sets.newHashSet();
    private final Map<String, T> profiles = Maps.newLinkedHashMap();
    private final List<T> enabled = Lists.newLinkedList();
    private final ResourcePackProfile.Factory<T> profileFactory;

    public ResourcePackManager(ResourcePackProfile.Factory<T> arg) {
        this.profileFactory = arg;
    }

    public void scanPacks() {
        this.close();
        Set set = this.enabled.stream().map(ResourcePackProfile::getName).collect(Collectors.toCollection(LinkedHashSet::new));
        this.profiles.clear();
        this.enabled.clear();
        for (ResourcePackProvider lv : this.providers) {
            lv.register(this.profiles, this.profileFactory);
        }
        this.sort();
        this.enabled.addAll(set.stream().map(this.profiles::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
        for (ResourcePackProfile lv2 : this.profiles.values()) {
            if (!lv2.isAlwaysEnabled() || this.enabled.contains(lv2)) continue;
            lv2.getInitialPosition().insert(this.enabled, lv2, Functions.identity(), false);
        }
    }

    private void sort() {
        ArrayList list = Lists.newArrayList(this.profiles.entrySet());
        this.profiles.clear();
        list.stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(entry -> {
            ResourcePackProfile cfr_ignored_0 = (ResourcePackProfile)this.profiles.put((String)entry.getKey(), (T)entry.getValue());
        });
    }

    public void setEnabledProfiles(Collection<T> collection) {
        this.enabled.clear();
        this.enabled.addAll(collection);
        for (ResourcePackProfile lv : this.profiles.values()) {
            if (!lv.isAlwaysEnabled() || this.enabled.contains(lv)) continue;
            lv.getInitialPosition().insert(this.enabled, lv, Functions.identity(), false);
        }
    }

    public Collection<T> getProfiles() {
        return this.profiles.values();
    }

    public Collection<T> getDisabledProfiles() {
        ArrayList collection = Lists.newArrayList(this.profiles.values());
        collection.removeAll(this.enabled);
        return collection;
    }

    public Collection<T> getEnabledProfiles() {
        return this.enabled;
    }

    @Nullable
    public T getProfile(String string) {
        return (T)((ResourcePackProfile)this.profiles.get(string));
    }

    public void registerProvider(ResourcePackProvider arg) {
        this.providers.add(arg);
    }

    @Override
    public void close() {
        this.profiles.values().forEach(ResourcePackProfile::close);
    }
}

