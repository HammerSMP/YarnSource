/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.tag;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.block.Block;
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
    public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer arg2, ResourceManager arg22, Profiler arg3, Profiler arg4, Executor executor, Executor executor2) {
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.blocks.prepareReload(arg22, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture2 = this.items.prepareReload(arg22, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture3 = this.fluids.prepareReload(arg22, executor);
        CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture4 = this.entityTypes.prepareReload(arg22, executor);
        return ((CompletableFuture)CompletableFuture.allOf(completableFuture, completableFuture2, completableFuture3, completableFuture4).thenCompose(arg2::whenPrepared)).thenAcceptAsync(arg -> {
            this.blocks.applyReload((Map)completableFuture.join());
            this.items.applyReload((Map)completableFuture2.join());
            this.fluids.applyReload((Map)completableFuture3.join());
            this.entityTypes.applyReload((Map)completableFuture4.join());
            BlockTags.setContainer(this.blocks);
            ItemTags.setContainer(this.items);
            FluidTags.setContainer(this.fluids);
            EntityTypeTags.setContainer(this.entityTypes);
        }, executor2);
    }
}
