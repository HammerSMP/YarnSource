/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.TagContainer;

public class class_5323 {
    private static volatile class_5323 field_25149 = new class_5323(BlockTags.getContainer(), ItemTags.getContainer(), FluidTags.getContainer(), EntityTypeTags.getContainer());
    private final TagContainer<Block> field_25150;
    private final TagContainer<Item> field_25151;
    private final TagContainer<Fluid> field_25152;
    private final TagContainer<EntityType<?>> field_25153;

    private class_5323(TagContainer<Block> arg, TagContainer<Item> arg2, TagContainer<Fluid> arg3, TagContainer<EntityType<?>> arg4) {
        this.field_25150 = arg;
        this.field_25151 = arg2;
        this.field_25152 = arg3;
        this.field_25153 = arg4;
    }

    public TagContainer<Block> method_29218() {
        return this.field_25150;
    }

    public TagContainer<Item> method_29220() {
        return this.field_25151;
    }

    public TagContainer<Fluid> method_29221() {
        return this.field_25152;
    }

    public TagContainer<EntityType<?>> method_29222() {
        return this.field_25153;
    }

    public static class_5323 method_29223() {
        return field_25149;
    }

    public static void method_29219(TagContainer<Block> arg, TagContainer<Item> arg2, TagContainer<Fluid> arg3, TagContainer<EntityType<?>> arg4) {
        field_25149 = new class_5323(arg, arg2, arg3, arg4);
    }
}

