/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.tag;

import net.minecraft.class_5415;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;

public class TagContainers {
    private static volatile class_5415 instance = class_5415.method_30216(BlockTags.getContainer(), ItemTags.getContainer(), FluidTags.getContainer(), EntityTypeTags.getContainer());

    public static class_5415 instance() {
        return instance;
    }

    public static void method_29219(class_5415 arg) {
        instance = arg;
    }
}

