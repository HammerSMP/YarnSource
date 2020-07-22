/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.client.model;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModelIds {
    @Deprecated
    public static Identifier getMinecraftNamespacedBlock(String name) {
        return new Identifier("minecraft", "block/" + name);
    }

    public static Identifier getMinecraftNamespacedItem(String name) {
        return new Identifier("minecraft", "item/" + name);
    }

    public static Identifier getBlockSubModelId(Block block, String suffix) {
        Identifier lv = Registry.BLOCK.getId(block);
        return new Identifier(lv.getNamespace(), "block/" + lv.getPath() + suffix);
    }

    public static Identifier getBlockModelId(Block block) {
        Identifier lv = Registry.BLOCK.getId(block);
        return new Identifier(lv.getNamespace(), "block/" + lv.getPath());
    }

    public static Identifier getItemModelId(Item item) {
        Identifier lv = Registry.ITEM.getId(item);
        return new Identifier(lv.getNamespace(), "item/" + lv.getPath());
    }

    public static Identifier getItemSubModelId(Item item, String suffix) {
        Identifier lv = Registry.ITEM.getId(item);
        return new Identifier(lv.getNamespace(), "item/" + lv.getPath() + suffix);
    }
}

