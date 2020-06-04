/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.tag;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagContainer;

public class TagContainers {
    private static volatile TagContainers instance = new TagContainers(BlockTags.getContainer(), ItemTags.getContainer(), FluidTags.getContainer(), EntityTypeTags.getContainer());
    private final TagContainer<Block> blocks;
    private final TagContainer<Item> items;
    private final TagContainer<Fluid> fluids;
    private final TagContainer<EntityType<?>> entityTypes;

    private TagContainers(TagContainer<Block> arg, TagContainer<Item> arg2, TagContainer<Fluid> arg3, TagContainer<EntityType<?>> arg4) {
        this.blocks = arg;
        this.items = arg2;
        this.fluids = arg3;
        this.entityTypes = arg4;
    }

    public TagContainer<Block> blocks() {
        return this.blocks;
    }

    public TagContainer<Item> items() {
        return this.items;
    }

    public TagContainer<Fluid> fluids() {
        return this.fluids;
    }

    public TagContainer<EntityType<?>> entityTypes() {
        return this.entityTypes;
    }

    public static TagContainers instance() {
        return instance;
    }

    public static void method_29219(TagContainer<Block> arg, TagContainer<Item> arg2, TagContainer<Fluid> arg3, TagContainer<EntityType<?>> arg4) {
        instance = new TagContainers(arg, arg2, arg3, arg4);
    }
}

