/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 */
package net.minecraft.data.client.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.TextureKey;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Texture {
    private final Map<TextureKey, Identifier> entries = Maps.newHashMap();
    private final Set<TextureKey> inherited = Sets.newHashSet();

    public Texture put(TextureKey arg, Identifier arg2) {
        this.entries.put(arg, arg2);
        return this;
    }

    public Stream<TextureKey> getInherited() {
        return this.inherited.stream();
    }

    public Texture inherit(TextureKey arg, TextureKey arg2) {
        this.entries.put(arg2, this.entries.get(arg));
        this.inherited.add(arg2);
        return this;
    }

    public Identifier getTexture(TextureKey arg) {
        for (TextureKey lv = arg; lv != null; lv = lv.getParent()) {
            Identifier lv2 = this.entries.get(lv);
            if (lv2 == null) continue;
            return lv2;
        }
        throw new IllegalStateException("Can't find texture for slot " + arg);
    }

    public Texture copyAndAdd(TextureKey arg, Identifier arg2) {
        Texture lv = new Texture();
        lv.entries.putAll(this.entries);
        lv.inherited.addAll(this.inherited);
        lv.put(arg, arg2);
        return lv;
    }

    public static Texture all(Block arg) {
        Identifier lv = Texture.getId(arg);
        return Texture.all(lv);
    }

    public static Texture texture(Block arg) {
        Identifier lv = Texture.getId(arg);
        return Texture.texture(lv);
    }

    public static Texture texture(Identifier arg) {
        return new Texture().put(TextureKey.TEXTURE, arg);
    }

    public static Texture all(Identifier arg) {
        return new Texture().put(TextureKey.ALL, arg);
    }

    public static Texture cross(Block arg) {
        return Texture.of(TextureKey.CROSS, Texture.getId(arg));
    }

    public static Texture cross(Identifier arg) {
        return Texture.of(TextureKey.CROSS, arg);
    }

    public static Texture plant(Block arg) {
        return Texture.of(TextureKey.PLANT, Texture.getId(arg));
    }

    public static Texture plant(Identifier arg) {
        return Texture.of(TextureKey.PLANT, arg);
    }

    public static Texture rail(Block arg) {
        return Texture.of(TextureKey.RAIL, Texture.getId(arg));
    }

    public static Texture rail(Identifier arg) {
        return Texture.of(TextureKey.RAIL, arg);
    }

    public static Texture wool(Block arg) {
        return Texture.of(TextureKey.WOOL, Texture.getId(arg));
    }

    public static Texture stem(Block arg) {
        return Texture.of(TextureKey.STEM, Texture.getId(arg));
    }

    public static Texture stemAndUpper(Block arg, Block arg2) {
        return new Texture().put(TextureKey.STEM, Texture.getId(arg)).put(TextureKey.UPPERSTEM, Texture.getId(arg2));
    }

    public static Texture pattern(Block arg) {
        return Texture.of(TextureKey.PATTERN, Texture.getId(arg));
    }

    public static Texture fan(Block arg) {
        return Texture.of(TextureKey.FAN, Texture.getId(arg));
    }

    public static Texture crop(Identifier arg) {
        return Texture.of(TextureKey.CROP, arg);
    }

    public static Texture paneAndTopForEdge(Block arg, Block arg2) {
        return new Texture().put(TextureKey.PANE, Texture.getId(arg)).put(TextureKey.EDGE, Texture.getSubId(arg2, "_top"));
    }

    public static Texture of(TextureKey arg, Identifier arg2) {
        return new Texture().put(arg, arg2);
    }

    public static Texture sideEnd(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getSubId(arg, "_side")).put(TextureKey.END, Texture.getSubId(arg, "_top"));
    }

    public static Texture sideAndTop(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getSubId(arg, "_side")).put(TextureKey.TOP, Texture.getSubId(arg, "_top"));
    }

    public static Texture sideAndEndForTop(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getId(arg)).put(TextureKey.END, Texture.getSubId(arg, "_top"));
    }

    public static Texture sideEnd(Identifier arg, Identifier arg2) {
        return new Texture().put(TextureKey.SIDE, arg).put(TextureKey.END, arg2);
    }

    public static Texture sideTopBottom(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getSubId(arg, "_side")).put(TextureKey.TOP, Texture.getSubId(arg, "_top")).put(TextureKey.BOTTOM, Texture.getSubId(arg, "_bottom"));
    }

    public static Texture wallSideTopBottom(Block arg) {
        Identifier lv = Texture.getId(arg);
        return new Texture().put(TextureKey.WALL, lv).put(TextureKey.SIDE, lv).put(TextureKey.TOP, Texture.getSubId(arg, "_top")).put(TextureKey.BOTTOM, Texture.getSubId(arg, "_bottom"));
    }

    public static Texture method_27168(Block arg) {
        Identifier lv = Texture.getId(arg);
        return new Texture().put(TextureKey.WALL, lv).put(TextureKey.SIDE, lv).put(TextureKey.END, Texture.getSubId(arg, "_top"));
    }

    public static Texture topBottom(Block arg) {
        return new Texture().put(TextureKey.TOP, Texture.getSubId(arg, "_top")).put(TextureKey.BOTTOM, Texture.getSubId(arg, "_bottom"));
    }

    public static Texture particle(Block arg) {
        return new Texture().put(TextureKey.PARTICLE, Texture.getId(arg));
    }

    public static Texture particle(Identifier arg) {
        return new Texture().put(TextureKey.PARTICLE, arg);
    }

    public static Texture fire0(Block arg) {
        return new Texture().put(TextureKey.FIRE, Texture.getSubId(arg, "_0"));
    }

    public static Texture fire1(Block arg) {
        return new Texture().put(TextureKey.FIRE, Texture.getSubId(arg, "_1"));
    }

    public static Texture lantern(Block arg) {
        return new Texture().put(TextureKey.LANTERN, Texture.getId(arg));
    }

    public static Texture torch(Block arg) {
        return new Texture().put(TextureKey.TORCH, Texture.getId(arg));
    }

    public static Texture torch(Identifier arg) {
        return new Texture().put(TextureKey.TORCH, arg);
    }

    public static Texture particle(Item arg) {
        return new Texture().put(TextureKey.PARTICLE, Texture.getId(arg));
    }

    public static Texture sideFrontBack(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getSubId(arg, "_side")).put(TextureKey.FRONT, Texture.getSubId(arg, "_front")).put(TextureKey.BACK, Texture.getSubId(arg, "_back"));
    }

    public static Texture sideFrontTopBottom(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getSubId(arg, "_side")).put(TextureKey.FRONT, Texture.getSubId(arg, "_front")).put(TextureKey.TOP, Texture.getSubId(arg, "_top")).put(TextureKey.BOTTOM, Texture.getSubId(arg, "_bottom"));
    }

    public static Texture sideFrontTop(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getSubId(arg, "_side")).put(TextureKey.FRONT, Texture.getSubId(arg, "_front")).put(TextureKey.TOP, Texture.getSubId(arg, "_top"));
    }

    public static Texture sideFrontEnd(Block arg) {
        return new Texture().put(TextureKey.SIDE, Texture.getSubId(arg, "_side")).put(TextureKey.FRONT, Texture.getSubId(arg, "_front")).put(TextureKey.END, Texture.getSubId(arg, "_end"));
    }

    public static Texture top(Block arg) {
        return new Texture().put(TextureKey.TOP, Texture.getSubId(arg, "_top"));
    }

    public static Texture frontSideWithCustomBottom(Block arg, Block arg2) {
        return new Texture().put(TextureKey.PARTICLE, Texture.getSubId(arg, "_front")).put(TextureKey.DOWN, Texture.getId(arg2)).put(TextureKey.UP, Texture.getSubId(arg, "_top")).put(TextureKey.NORTH, Texture.getSubId(arg, "_front")).put(TextureKey.EAST, Texture.getSubId(arg, "_side")).put(TextureKey.SOUTH, Texture.getSubId(arg, "_side")).put(TextureKey.WEST, Texture.getSubId(arg, "_front"));
    }

    public static Texture frontTopSide(Block arg, Block arg2) {
        return new Texture().put(TextureKey.PARTICLE, Texture.getSubId(arg, "_front")).put(TextureKey.DOWN, Texture.getId(arg2)).put(TextureKey.UP, Texture.getSubId(arg, "_top")).put(TextureKey.NORTH, Texture.getSubId(arg, "_front")).put(TextureKey.SOUTH, Texture.getSubId(arg, "_front")).put(TextureKey.EAST, Texture.getSubId(arg, "_side")).put(TextureKey.WEST, Texture.getSubId(arg, "_side"));
    }

    public static Texture method_27167(Block arg) {
        return new Texture().put(TextureKey.LIT_LOG, Texture.getSubId(arg, "_log_lit")).put(TextureKey.FIRE, Texture.getSubId(arg, "_fire"));
    }

    public static Texture layer0(Item arg) {
        return new Texture().put(TextureKey.LAYER0, Texture.getId(arg));
    }

    public static Texture layer0(Block arg) {
        return new Texture().put(TextureKey.LAYER0, Texture.getId(arg));
    }

    public static Texture layer0(Identifier arg) {
        return new Texture().put(TextureKey.LAYER0, arg);
    }

    public static Identifier getId(Block arg) {
        Identifier lv = Registry.BLOCK.getId(arg);
        return new Identifier(lv.getNamespace(), "block/" + lv.getPath());
    }

    public static Identifier getSubId(Block arg, String string) {
        Identifier lv = Registry.BLOCK.getId(arg);
        return new Identifier(lv.getNamespace(), "block/" + lv.getPath() + string);
    }

    public static Identifier getId(Item arg) {
        Identifier lv = Registry.ITEM.getId(arg);
        return new Identifier(lv.getNamespace(), "item/" + lv.getPath());
    }

    public static Identifier getSubId(Item arg, String string) {
        Identifier lv = Registry.ITEM.getId(arg);
        return new Identifier(lv.getNamespace(), "item/" + lv.getPath() + string);
    }
}

