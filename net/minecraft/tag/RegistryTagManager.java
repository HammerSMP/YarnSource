/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 */
package net.minecraft.tag;

import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.class_5413;
import net.minecraft.class_5414;
import net.minecraft.class_5415;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.tag.TagContainers;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class RegistryTagManager
implements ResourceReloadListener {
    private final TagContainer<Block> blocks = new TagContainer(Registry.BLOCK::getOrEmpty, "tags/blocks", "block");
    private final TagContainer<Item> items = new TagContainer(Registry.ITEM::getOrEmpty, "tags/items", "item");
    private final TagContainer<Fluid> fluids = new TagContainer(Registry.FLUID::getOrEmpty, "tags/fluids", "fluid");
    private final TagContainer<EntityType<?>> entityTypes = new TagContainer(Registry.ENTITY_TYPE::getOrEmpty, "tags/entity_types", "entity_type");
    private class_5415 field_25749 = class_5415.field_25744;

    public class_5415 method_30223() {
        return this.field_25749;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg2, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.blocks.prepareReload(arg2, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture2 = this.items.prepareReload(arg2, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture3 = this.fluids.prepareReload(arg2, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture4 = this.entityTypes.prepareReload(arg2, executor);
        return ((CompletableFuture)CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4).thenCompose(arg::whenPrepared)).thenAcceptAsync(void_ -> {
            class_5414<EntityType<?>> lv4;
            class_5414<Fluid> lv3;
            class_5414<Item> lv2;
            class_5414<Block> lv = this.blocks.applyReload((Map)completableFuture.join());
            class_5415 lv5 = class_5415.method_30216(lv, lv2 = this.items.applyReload((Map)completableFuture2.join()), lv3 = this.fluids.applyReload((Map)completableFuture3.join()), lv4 = this.entityTypes.applyReload((Map)completableFuture4.join()));
            Multimap<Identifier, Identifier> multimap = class_5413.method_30203(lv5);
            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
            }
            TagContainers.method_29219(lv5);
            this.field_25749 = lv5;
        }, executor2);
    }
}

