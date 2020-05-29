/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.loot.entry;

import net.minecraft.class_5330;
import net.minecraft.class_5335;
import net.minecraft.class_5338;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.CombinedEntry;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.entry.SequenceEntry;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LootEntries {
    public static final class_5338 EMPTY = LootEntries.method_29317("empty", new EmptyEntry.Serializer());
    public static final class_5338 ITEM = LootEntries.method_29317("item", new ItemEntry.Serializer());
    public static final class_5338 LOOT_TABLE = LootEntries.method_29317("loot_table", new LootTableEntry.Serializer());
    public static final class_5338 DYNAMIC = LootEntries.method_29317("dynamic", new DynamicEntry.Serializer());
    public static final class_5338 TAG = LootEntries.method_29317("tag", new TagEntry.Serializer());
    public static final class_5338 ALTERNATIVES = LootEntries.method_29317("alternatives", CombinedEntry.createSerializer(AlternativeEntry::new));
    public static final class_5338 SEQUENCE = LootEntries.method_29317("sequence", CombinedEntry.createSerializer(GroupEntry::new));
    public static final class_5338 GROUP = LootEntries.method_29317("group", CombinedEntry.createSerializer(SequenceEntry::new));

    private static class_5338 method_29317(String string, class_5335<? extends LootEntry> arg) {
        return Registry.register(Registry.field_25293, new Identifier(string), new class_5338(arg));
    }

    public static Object method_29316() {
        return class_5330.method_29306(Registry.field_25293, "entry", "type", LootEntry::method_29318).method_29307();
    }
}

