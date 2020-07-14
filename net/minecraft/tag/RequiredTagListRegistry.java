/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.RequiredTagList;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagManager;
import net.minecraft.util.Identifier;

public class RequiredTagListRegistry {
    private static final Map<Identifier, RequiredTagList<?>> REQUIRED_TAG_LISTS = Maps.newHashMap();

    public static <T> RequiredTagList<T> register(Identifier id, Function<TagManager, TagGroup<T>> containerGetter) {
        RequiredTagList<T> lv = new RequiredTagList<T>(containerGetter);
        RequiredTagList<T> lv2 = REQUIRED_TAG_LISTS.putIfAbsent(id, lv);
        if (lv2 != null) {
            throw new IllegalStateException("Duplicate entry for static tag collection: " + id);
        }
        return lv;
    }

    public static void updateTagManager(TagManager tagManager) {
        REQUIRED_TAG_LISTS.values().forEach(list -> list.updateTagManager(tagManager));
    }

    @Environment(value=EnvType.CLIENT)
    public static void clearAllTags() {
        REQUIRED_TAG_LISTS.values().forEach(RequiredTagList::clearAllTags);
    }

    public static Multimap<Identifier, Identifier> getMissingTags(TagManager tagManager) {
        HashMultimap multimap = HashMultimap.create();
        REQUIRED_TAG_LISTS.forEach((arg_0, arg_1) -> RequiredTagListRegistry.method_30200((Multimap)multimap, tagManager, arg_0, arg_1));
        return multimap;
    }

    public static void validateRegistrations() {
        RequiredTagList[] lvs = new RequiredTagList[]{BlockTags.REQUIRED_TAGS, ItemTags.REQUIRED_TAGS, FluidTags.REQUIRED_TAGS, EntityTypeTags.REQUIRED_TAGS};
        boolean bl = Stream.of(lvs).anyMatch(list -> !REQUIRED_TAG_LISTS.containsValue(list));
        if (bl) {
            throw new IllegalStateException("Missing helper registrations");
        }
    }

    private static /* synthetic */ void method_30200(Multimap multimap, TagManager arg, Identifier id, RequiredTagList list) {
        multimap.putAll((Object)id, list.getMissingTags(arg));
    }
}

