/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 */
package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.RegistryTagContainer;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainers;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;

public class RegistryTagManager
implements ResourceReloadListener {
    private final RegistryTagContainer<Block> blocks = new RegistryTagContainer<Block>(Registry.BLOCK, "tags/blocks", "block");
    private final RegistryTagContainer<Item> items = new RegistryTagContainer<Item>(Registry.ITEM, "tags/items", "item");
    private final RegistryTagContainer<Fluid> fluids = new RegistryTagContainer<Fluid>(Registry.FLUID, "tags/fluids", "fluid");
    private final RegistryTagContainer<EntityType<?>> entityTypes = new RegistryTagContainer(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");

    public RegistryTagContainer<Block> blocks() {
        return this.blocks;
    }

    public RegistryTagContainer<Item> items() {
        return this.items;
    }

    public RegistryTagContainer<Fluid> fluids() {
        return this.fluids;
    }

    public RegistryTagContainer<EntityType<?>> entityTypes() {
        return this.entityTypes;
    }

    public void toPacket(PacketByteBuf arg) {
        this.blocks.toPacket(arg);
        this.items.toPacket(arg);
        this.fluids.toPacket(arg);
        this.entityTypes.toPacket(arg);
    }

    public static RegistryTagManager fromPacket(PacketByteBuf arg) {
        RegistryTagManager lv = new RegistryTagManager();
        lv.blocks().fromPacket(arg);
        lv.items().fromPacket(arg);
        lv.fluids().fromPacket(arg);
        lv.entityTypes().fromPacket(arg);
        return lv;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg, ResourceManager arg2, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.blocks.prepareReload(arg2, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture2 = this.items.prepareReload(arg2, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture3 = this.fluids.prepareReload(arg2, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture4 = this.entityTypes.prepareReload(arg2, executor);
        return ((CompletableFuture)CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4).thenCompose(arg::whenPrepared)).thenAcceptAsync(void_ -> {
            this.blocks.applyReload((Map)completableFuture.join());
            this.items.applyReload((Map)completableFuture2.join());
            this.fluids.applyReload((Map)completableFuture3.join());
            this.entityTypes.applyReload((Map)completableFuture4.join());
            TagContainers.method_29219(this.blocks, this.items, this.fluids, this.entityTypes);
            HashMultimap multimap = HashMultimap.create();
            multimap.putAll((Object)"blocks", BlockTags.method_29214(this.blocks));
            multimap.putAll((Object)"items", ItemTags.method_29217(this.items));
            multimap.putAll((Object)"fluids", FluidTags.method_29216(this.fluids));
            multimap.putAll((Object)"entity_types", EntityTypeTags.method_29215(this.entityTypes));
            if (!multimap.isEmpty()) {
                throw new IllegalStateException("Missing required tags: " + multimap.entries().stream().map(entry -> (String)entry.getKey() + ":" + entry.getValue()).sorted().collect(Collectors.joining(",")));
            }
        }, executor2);
    }

    public void method_29226() {
        BlockTags.setContainer(this.blocks);
        ItemTags.setContainer(this.items);
        FluidTags.setContainer(this.fluids);
        EntityTypeTags.setContainer(this.entityTypes);
        Blocks.refreshShapeCache();
    }
}

