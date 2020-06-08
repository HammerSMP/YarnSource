/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Functions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.resource;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;

public class ResourcePackManager<T extends ResourcePackProfile>
implements AutoCloseable {
    private final Set<ResourcePackProvider> providers;
    private Map<String, T> profiles = ImmutableMap.of();
    private List<T> enabled = ImmutableList.of();
    private final ResourcePackProfile.Factory<T> profileFactory;

    public ResourcePackManager(ResourcePackProfile.Factory<T> arg, ResourcePackProvider ... args) {
        this.profileFactory = arg;
        this.providers = ImmutableSet.copyOf((Object[])args);
    }

    public void scanPacks() {
        List list = (List)this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableList.toImmutableList());
        this.close();
        this.profiles = this.providePackProfiles();
        this.enabled = this.buildEnabledProfiles(list);
    }

    private Map<String, T> providePackProfiles() {
        TreeMap map = Maps.newTreeMap();
        for (ResourcePackProvider lv : this.providers) {
            lv.register(arg -> map.put(arg.getName(), arg), this.profileFactory);
        }
        return ImmutableMap.copyOf((Map)map);
    }

    public void setEnabledProfiles(Collection<String> collection) {
        this.enabled = this.buildEnabledProfiles(collection);
    }

    private List<T> buildEnabledProfiles(Collection<String> collection) {
        List list = this.streamProfilesByName(collection).collect(Collectors.toList());
        for (ResourcePackProfile lv : this.profiles.values()) {
            if (!lv.isAlwaysEnabled() || list.contains(lv)) continue;
            lv.getInitialPosition().insert(list, lv, Functions.identity(), false);
        }
        return ImmutableList.copyOf(list);
    }

    private Stream<T> streamProfilesByName(Collection<String> collection) {
        return collection.stream().map(this.profiles::get).filter(Objects::nonNull);
    }

    public Collection<String> getNames() {
        return this.profiles.keySet();
    }

    public Collection<T> getProfiles() {
        return this.profiles.values();
    }

    public Collection<String> getEnabledNames() {
        return (Collection)this.enabled.stream().map(ResourcePackProfile::getName).collect(ImmutableSet.toImmutableSet());
    }

    public Collection<T> getEnabledProfiles() {
        return this.enabled;
    }

    @Nullable
    public T getProfile(String string) {
        return (T)((ResourcePackProfile)this.profiles.get(string));
    }

    @Override
    public void close() {
        this.profiles.values().forEach(ResourcePackProfile::close);
    }

    public boolean hasProfile(String string) {
        return this.profiles.containsKey(string);
    }

    public List<ResourcePack> createResourcePacks() {
        return (List)this.enabled.stream().map(ResourcePackProfile::createResourcePack).collect(ImmutableList.toImmutableList());
    }
}

