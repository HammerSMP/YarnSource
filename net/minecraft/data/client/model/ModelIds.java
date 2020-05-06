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
    public static Identifier getMinecraftNamespacedBlock(String string) {
        return new Identifier("minecraft", "block/" + string);
    }

    public static Identifier getMinecraftNamespacedItem(String string) {
        return new Identifier("minecraft", "item/" + string);
    }

    public static Identifier getBlockSubModelId(Block arg, String string) {
        Identifier lv = Registry.BLOCK.getId(arg);
        return new Identifier(lv.getNamespace(), "block/" + lv.getPath() + string);
    }

    public static Identifier getBlockModelId(Block arg) {
        Identifier lv = Registry.BLOCK.getId(arg);
        return new Identifier(lv.getNamespace(), "block/" + lv.getPath());
    }

    public static Identifier getItemModelId(Item arg) {
        Identifier lv = Registry.ITEM.getId(arg);
        return new Identifier(lv.getNamespace(), "item/" + lv.getPath());
    }

    public static Identifier getItemSubModelId(Item arg, String string) {
        Identifier lv = Registry.ITEM.getId(arg);
        return new Identifier(lv.getNamespace(), "item/" + lv.getPath() + string);
    }
}

