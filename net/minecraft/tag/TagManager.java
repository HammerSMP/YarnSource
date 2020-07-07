/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.tag;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.RequiredTagListRegistry;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.registry.Registry;

public interface TagManager {
    public static final TagManager EMPTY = TagManager.create(TagGroup.createEmpty(), TagGroup.createEmpty(), TagGroup.createEmpty(), TagGroup.createEmpty());

    public TagGroup<Block> getBlocks();

    public TagGroup<Item> getItems();

    public TagGroup<Fluid> getFluids();

    public TagGroup<EntityType<?>> getEntityTypes();

    default public void apply() {
        RequiredTagListRegistry.updateTagManager(this);
        Blocks.refreshShapeCache();
    }

    default public void toPacket(PacketByteBuf arg) {
        this.getBlocks().toPacket(arg, Registry.BLOCK);
        this.getItems().toPacket(arg, Registry.ITEM);
        this.getFluids().toPacket(arg, Registry.FLUID);
        this.getEntityTypes().toPacket(arg, Registry.ENTITY_TYPE);
    }

    public static TagManager fromPacket(PacketByteBuf arg) {
        TagGroup<Block> lv = TagGroup.fromPacket(arg, Registry.BLOCK);
        TagGroup<Item> lv2 = TagGroup.fromPacket(arg, Registry.ITEM);
        TagGroup<Fluid> lv3 = TagGroup.fromPacket(arg, Registry.FLUID);
        TagGroup<EntityType<?>> lv4 = TagGroup.fromPacket(arg, Registry.ENTITY_TYPE);
        return TagManager.create(lv, lv2, lv3, lv4);
    }

    public static TagManager create(final TagGroup<Block> arg, final TagGroup<Item> arg2, final TagGroup<Fluid> arg3, final TagGroup<EntityType<?>> arg4) {
        return new TagManager(){

            @Override
            public TagGroup<Block> getBlocks() {
                return arg;
            }

            @Override
            public TagGroup<Item> getItems() {
                return arg2;
            }

            @Override
            public TagGroup<Fluid> getFluids() {
                return arg3;
            }

            @Override
            public TagGroup<EntityType<?>> getEntityTypes() {
                return arg4;
            }
        };
    }
}

