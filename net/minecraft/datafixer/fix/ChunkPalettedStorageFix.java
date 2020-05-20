/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.class_5298;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.util.collection.Int2ObjectBiMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkPalettedStorageFix
extends DataFix {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final BitSet blocksNeedingSideUpdate = new BitSet(256);
    private static final BitSet blocksNeedingInPlaceUpdate = new BitSet(256);
    private static final Dynamic<?> pumpkin = BlockStateFlattening.parseState("{Name:'minecraft:pumpkin'}");
    private static final Dynamic<?> podzol = BlockStateFlattening.parseState("{Name:'minecraft:podzol',Properties:{snowy:'true'}}");
    private static final Dynamic<?> snowyGrass = BlockStateFlattening.parseState("{Name:'minecraft:grass_block',Properties:{snowy:'true'}}");
    private static final Dynamic<?> snowyMycelium = BlockStateFlattening.parseState("{Name:'minecraft:mycelium',Properties:{snowy:'true'}}");
    private static final Dynamic<?> sunflowerUpper = BlockStateFlattening.parseState("{Name:'minecraft:sunflower',Properties:{half:'upper'}}");
    private static final Dynamic<?> lilacUpper = BlockStateFlattening.parseState("{Name:'minecraft:lilac',Properties:{half:'upper'}}");
    private static final Dynamic<?> grassUpper = BlockStateFlattening.parseState("{Name:'minecraft:tall_grass',Properties:{half:'upper'}}");
    private static final Dynamic<?> fernUpper = BlockStateFlattening.parseState("{Name:'minecraft:large_fern',Properties:{half:'upper'}}");
    private static final Dynamic<?> roseUpper = BlockStateFlattening.parseState("{Name:'minecraft:rose_bush',Properties:{half:'upper'}}");
    private static final Dynamic<?> peonyUpper = BlockStateFlattening.parseState("{Name:'minecraft:peony',Properties:{half:'upper'}}");
    private static final Map<String, Dynamic<?>> flowerPot = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        hashMap.put("minecraft:air0", BlockStateFlattening.parseState("{Name:'minecraft:flower_pot'}"));
        hashMap.put("minecraft:red_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_poppy'}"));
        hashMap.put("minecraft:red_flower1", BlockStateFlattening.parseState("{Name:'minecraft:potted_blue_orchid'}"));
        hashMap.put("minecraft:red_flower2", BlockStateFlattening.parseState("{Name:'minecraft:potted_allium'}"));
        hashMap.put("minecraft:red_flower3", BlockStateFlattening.parseState("{Name:'minecraft:potted_azure_bluet'}"));
        hashMap.put("minecraft:red_flower4", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_tulip'}"));
        hashMap.put("minecraft:red_flower5", BlockStateFlattening.parseState("{Name:'minecraft:potted_orange_tulip'}"));
        hashMap.put("minecraft:red_flower6", BlockStateFlattening.parseState("{Name:'minecraft:potted_white_tulip'}"));
        hashMap.put("minecraft:red_flower7", BlockStateFlattening.parseState("{Name:'minecraft:potted_pink_tulip'}"));
        hashMap.put("minecraft:red_flower8", BlockStateFlattening.parseState("{Name:'minecraft:potted_oxeye_daisy'}"));
        hashMap.put("minecraft:yellow_flower0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dandelion'}"));
        hashMap.put("minecraft:sapling0", BlockStateFlattening.parseState("{Name:'minecraft:potted_oak_sapling'}"));
        hashMap.put("minecraft:sapling1", BlockStateFlattening.parseState("{Name:'minecraft:potted_spruce_sapling'}"));
        hashMap.put("minecraft:sapling2", BlockStateFlattening.parseState("{Name:'minecraft:potted_birch_sapling'}"));
        hashMap.put("minecraft:sapling3", BlockStateFlattening.parseState("{Name:'minecraft:potted_jungle_sapling'}"));
        hashMap.put("minecraft:sapling4", BlockStateFlattening.parseState("{Name:'minecraft:potted_acacia_sapling'}"));
        hashMap.put("minecraft:sapling5", BlockStateFlattening.parseState("{Name:'minecraft:potted_dark_oak_sapling'}"));
        hashMap.put("minecraft:red_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_red_mushroom'}"));
        hashMap.put("minecraft:brown_mushroom0", BlockStateFlattening.parseState("{Name:'minecraft:potted_brown_mushroom'}"));
        hashMap.put("minecraft:deadbush0", BlockStateFlattening.parseState("{Name:'minecraft:potted_dead_bush'}"));
        hashMap.put("minecraft:tallgrass2", BlockStateFlattening.parseState("{Name:'minecraft:potted_fern'}"));
        hashMap.put("minecraft:cactus0", BlockStateFlattening.lookupState(2240));
    });
    private static final Map<String, Dynamic<?>> skull = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.buildSkull(hashMap, 0, "skeleton", "skull");
        ChunkPalettedStorageFix.buildSkull(hashMap, 1, "wither_skeleton", "skull");
        ChunkPalettedStorageFix.buildSkull(hashMap, 2, "zombie", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 3, "player", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 4, "creeper", "head");
        ChunkPalettedStorageFix.buildSkull(hashMap, 5, "dragon", "head");
    });
    private static final Map<String, Dynamic<?>> door = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        ChunkPalettedStorageFix.buildDoor(hashMap, "oak_door", 1024);
        ChunkPalettedStorageFix.buildDoor(hashMap, "iron_door", 1136);
        ChunkPalettedStorageFix.buildDoor(hashMap, "spruce_door", 3088);
        ChunkPalettedStorageFix.buildDoor(hashMap, "birch_door", 3104);
        ChunkPalettedStorageFix.buildDoor(hashMap, "jungle_door", 3120);
        ChunkPalettedStorageFix.buildDoor(hashMap, "acacia_door", 3136);
        ChunkPalettedStorageFix.buildDoor(hashMap, "dark_oak_door", 3152);
    });
    private static final Map<String, Dynamic<?>> noteblock = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        for (int i = 0; i < 26; ++i) {
            hashMap.put("true" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'true',note:'" + i + "'}}"));
            hashMap.put("false" + i, BlockStateFlattening.parseState("{Name:'minecraft:note_block',Properties:{powered:'false',note:'" + i + "'}}"));
        }
    });
    private static final Int2ObjectMap<String> colors = (Int2ObjectMap)DataFixUtils.make((Object)new Int2ObjectOpenHashMap(), int2ObjectOpenHashMap -> {
        int2ObjectOpenHashMap.put(0, (Object)"white");
        int2ObjectOpenHashMap.put(1, (Object)"orange");
        int2ObjectOpenHashMap.put(2, (Object)"magenta");
        int2ObjectOpenHashMap.put(3, (Object)"light_blue");
        int2ObjectOpenHashMap.put(4, (Object)"yellow");
        int2ObjectOpenHashMap.put(5, (Object)"lime");
        int2ObjectOpenHashMap.put(6, (Object)"pink");
        int2ObjectOpenHashMap.put(7, (Object)"gray");
        int2ObjectOpenHashMap.put(8, (Object)"light_gray");
        int2ObjectOpenHashMap.put(9, (Object)"cyan");
        int2ObjectOpenHashMap.put(10, (Object)"purple");
        int2ObjectOpenHashMap.put(11, (Object)"blue");
        int2ObjectOpenHashMap.put(12, (Object)"brown");
        int2ObjectOpenHashMap.put(13, (Object)"green");
        int2ObjectOpenHashMap.put(14, (Object)"red");
        int2ObjectOpenHashMap.put(15, (Object)"black");
    });
    private static final Map<String, Dynamic<?>> bed = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : colors.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "red")) continue;
            ChunkPalettedStorageFix.buildBed(hashMap, entry.getIntKey(), (String)entry.getValue());
        }
    });
    private static final Map<String, Dynamic<?>> banner = (Map)DataFixUtils.make((Object)Maps.newHashMap(), hashMap -> {
        for (Int2ObjectMap.Entry entry : colors.int2ObjectEntrySet()) {
            if (Objects.equals(entry.getValue(), "white")) continue;
            ChunkPalettedStorageFix.buildBanner(hashMap, 15 - entry.getIntKey(), (String)entry.getValue());
        }
    });
    private static final Dynamic<?> air;

    public ChunkPalettedStorageFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private static void buildSkull(Map<String, Dynamic<?>> map, int i, String string, String string2) {
        map.put(i + "north", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'north'}}"));
        map.put(i + "east", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'east'}}"));
        map.put(i + "south", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'south'}}"));
        map.put(i + "west", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_" + string2 + "',Properties:{facing:'west'}}"));
        for (int j = 0; j < 16; ++j) {
            map.put(i + "" + j, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_" + string2 + "',Properties:{rotation:'" + j + "'}}"));
        }
    }

    private static void buildDoor(Map<String, Dynamic<?>> map, String string, int i) {
        map.put("minecraft:" + string + "eastlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "eastlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "eastlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "eastlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "eastlowerrightfalsefalse", BlockStateFlattening.lookupState(i));
        map.put("minecraft:" + string + "eastlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "eastlowerrighttruefalse", BlockStateFlattening.lookupState(i + 4));
        map.put("minecraft:" + string + "eastlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "eastupperleftfalsefalse", BlockStateFlattening.lookupState(i + 8));
        map.put("minecraft:" + string + "eastupperleftfalsetrue", BlockStateFlattening.lookupState(i + 10));
        map.put("minecraft:" + string + "eastupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "eastupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "eastupperrightfalsefalse", BlockStateFlattening.lookupState(i + 9));
        map.put("minecraft:" + string + "eastupperrightfalsetrue", BlockStateFlattening.lookupState(i + 11));
        map.put("minecraft:" + string + "eastupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "eastupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'east',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "northlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "northlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 3));
        map.put("minecraft:" + string + "northlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northlowerrighttruefalse", BlockStateFlattening.lookupState(i + 7));
        map.put("minecraft:" + string + "northlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "northupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "northupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'north',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "southlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "southlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 1));
        map.put("minecraft:" + string + "southlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southlowerrighttruefalse", BlockStateFlattening.lookupState(i + 5));
        map.put("minecraft:" + string + "southlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "southupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "southupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'south',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "westlowerleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "westlowerlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerrightfalsefalse", BlockStateFlattening.lookupState(i + 2));
        map.put("minecraft:" + string + "westlowerrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westlowerrighttruefalse", BlockStateFlattening.lookupState(i + 6));
        map.put("minecraft:" + string + "westlowerrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'lower',hinge:'right',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperleftfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperleftfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperlefttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperlefttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'left',open:'true',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperrightfalsefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperrightfalsetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'false',powered:'true'}}"));
        map.put("minecraft:" + string + "westupperrighttruefalse", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'false'}}"));
        map.put("minecraft:" + string + "westupperrighttruetrue", BlockStateFlattening.parseState("{Name:'minecraft:" + string + "',Properties:{facing:'west',half:'upper',hinge:'right',open:'true',powered:'true'}}"));
    }

    private static void buildBed(Map<String, Dynamic<?>> map, int i, String string) {
        map.put("southfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'foot'}}"));
        map.put("westfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'foot'}}"));
        map.put("northfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'foot'}}"));
        map.put("eastfalsefoot" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'foot'}}"));
        map.put("southfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'false',part:'head'}}"));
        map.put("westfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'false',part:'head'}}"));
        map.put("northfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'false',part:'head'}}"));
        map.put("eastfalsehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'false',part:'head'}}"));
        map.put("southtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'south',occupied:'true',part:'head'}}"));
        map.put("westtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'west',occupied:'true',part:'head'}}"));
        map.put("northtruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'north',occupied:'true',part:'head'}}"));
        map.put("easttruehead" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_bed',Properties:{facing:'east',occupied:'true',part:'head'}}"));
    }

    private static void buildBanner(Map<String, Dynamic<?>> map, int i, String string) {
        for (int j = 0; j < 16; ++j) {
            map.put("" + j + "_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_banner',Properties:{rotation:'" + j + "'}}"));
        }
        map.put("north_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'north'}}"));
        map.put("south_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'south'}}"));
        map.put("west_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'west'}}"));
        map.put("east_" + i, BlockStateFlattening.parseState("{Name:'minecraft:" + string + "_wall_banner',Properties:{facing:'east'}}"));
    }

    public static String getName(Dynamic<?> dynamic) {
        return dynamic.get("Name").asString("");
    }

    public static String getProperty(Dynamic<?> dynamic, String string) {
        return dynamic.get("Properties").get(string).asString("");
    }

    public static int addTo(Int2ObjectBiMap<Dynamic<?>> arg, Dynamic<?> dynamic) {
        int i = arg.getId(dynamic);
        if (i == -1) {
            i = arg.add(dynamic);
        }
        return i;
    }

    private Dynamic<?> fixChunk(Dynamic<?> dynamic) {
        Optional optional = dynamic.get("Level").result();
        if (optional.isPresent() && ((Dynamic)optional.get()).get("Sections").asStreamOpt().result().isPresent()) {
            return dynamic.set("Level", new Level((Dynamic)optional.get()).transform());
        }
        return dynamic;
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.CHUNK);
        Type type2 = this.getOutputSchema().getType(TypeReferences.CHUNK);
        return this.writeFixAndRead("ChunkPalettedStorageFix", type, type2, this::fixChunk);
    }

    public static int getSideToUpgradeFlag(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        int i = 0;
        if (bl3) {
            i = bl2 ? (i |= 2) : (bl ? (i |= 0x80) : (i |= 1));
        } else if (bl4) {
            i = bl ? (i |= 0x20) : (bl2 ? (i |= 8) : (i |= 0x10));
        } else if (bl2) {
            i |= 4;
        } else if (bl) {
            i |= 0x40;
        }
        return i;
    }

    static {
        blocksNeedingInPlaceUpdate.set(2);
        blocksNeedingInPlaceUpdate.set(3);
        blocksNeedingInPlaceUpdate.set(110);
        blocksNeedingInPlaceUpdate.set(140);
        blocksNeedingInPlaceUpdate.set(144);
        blocksNeedingInPlaceUpdate.set(25);
        blocksNeedingInPlaceUpdate.set(86);
        blocksNeedingInPlaceUpdate.set(26);
        blocksNeedingInPlaceUpdate.set(176);
        blocksNeedingInPlaceUpdate.set(177);
        blocksNeedingInPlaceUpdate.set(175);
        blocksNeedingInPlaceUpdate.set(64);
        blocksNeedingInPlaceUpdate.set(71);
        blocksNeedingInPlaceUpdate.set(193);
        blocksNeedingInPlaceUpdate.set(194);
        blocksNeedingInPlaceUpdate.set(195);
        blocksNeedingInPlaceUpdate.set(196);
        blocksNeedingInPlaceUpdate.set(197);
        blocksNeedingSideUpdate.set(54);
        blocksNeedingSideUpdate.set(146);
        blocksNeedingSideUpdate.set(25);
        blocksNeedingSideUpdate.set(26);
        blocksNeedingSideUpdate.set(51);
        blocksNeedingSideUpdate.set(53);
        blocksNeedingSideUpdate.set(67);
        blocksNeedingSideUpdate.set(108);
        blocksNeedingSideUpdate.set(109);
        blocksNeedingSideUpdate.set(114);
        blocksNeedingSideUpdate.set(128);
        blocksNeedingSideUpdate.set(134);
        blocksNeedingSideUpdate.set(135);
        blocksNeedingSideUpdate.set(136);
        blocksNeedingSideUpdate.set(156);
        blocksNeedingSideUpdate.set(163);
        blocksNeedingSideUpdate.set(164);
        blocksNeedingSideUpdate.set(180);
        blocksNeedingSideUpdate.set(203);
        blocksNeedingSideUpdate.set(55);
        blocksNeedingSideUpdate.set(85);
        blocksNeedingSideUpdate.set(113);
        blocksNeedingSideUpdate.set(188);
        blocksNeedingSideUpdate.set(189);
        blocksNeedingSideUpdate.set(190);
        blocksNeedingSideUpdate.set(191);
        blocksNeedingSideUpdate.set(192);
        blocksNeedingSideUpdate.set(93);
        blocksNeedingSideUpdate.set(94);
        blocksNeedingSideUpdate.set(101);
        blocksNeedingSideUpdate.set(102);
        blocksNeedingSideUpdate.set(160);
        blocksNeedingSideUpdate.set(106);
        blocksNeedingSideUpdate.set(107);
        blocksNeedingSideUpdate.set(183);
        blocksNeedingSideUpdate.set(184);
        blocksNeedingSideUpdate.set(185);
        blocksNeedingSideUpdate.set(186);
        blocksNeedingSideUpdate.set(187);
        blocksNeedingSideUpdate.set(132);
        blocksNeedingSideUpdate.set(139);
        blocksNeedingSideUpdate.set(199);
        air = BlockStateFlattening.lookupState(0);
    }

    public static enum Facing {
        DOWN(Direction.NEGATIVE, Axis.Y),
        UP(Direction.POSITIVE, Axis.Y),
        NORTH(Direction.NEGATIVE, Axis.Z),
        SOUTH(Direction.POSITIVE, Axis.Z),
        WEST(Direction.NEGATIVE, Axis.X),
        EAST(Direction.POSITIVE, Axis.X);

        private final Axis axis;
        private final Direction direction;

        private Facing(Direction arg, Axis arg2) {
            this.axis = arg2;
            this.direction = arg;
        }

        public Direction getDirection() {
            return this.direction;
        }

        public Axis getAxis() {
            return this.axis;
        }

        public static enum Direction {
            POSITIVE(1),
            NEGATIVE(-1);

            private final int offset;

            private Direction(int j) {
                this.offset = j;
            }

            public int getOffset() {
                return this.offset;
            }
        }

        public static enum Axis {
            X,
            Y,
            Z;

        }
    }

    static class ChunkNibbleArray {
        private final byte[] contents;

        public ChunkNibbleArray() {
            this.contents = new byte[2048];
        }

        public ChunkNibbleArray(byte[] bs) {
            this.contents = bs;
            if (bs.length != 2048) {
                throw new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + bs.length);
            }
        }

        public int get(int i, int j, int k) {
            int l = this.getRawIndex(j << 8 | k << 4 | i);
            if (this.usesLowNibble(j << 8 | k << 4 | i)) {
                return this.contents[l] & 0xF;
            }
            return this.contents[l] >> 4 & 0xF;
        }

        private boolean usesLowNibble(int i) {
            return (i & 1) == 0;
        }

        private int getRawIndex(int i) {
            return i >> 1;
        }
    }

    static final class Level {
        private int sidesToUpgrade;
        private final Section[] sections = new Section[16];
        private final Dynamic<?> level;
        private final int xPos;
        private final int yPos;
        private final Int2ObjectMap<Dynamic<?>> blockEntities = new Int2ObjectLinkedOpenHashMap(16);

        public Level(Dynamic<?> dynamic) {
            this.level = dynamic;
            this.xPos = dynamic.get("xPos").asInt(0) << 4;
            this.yPos = dynamic.get("zPos").asInt(0) << 4;
            dynamic.get("TileEntities").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamic -> {
                int k;
                int i = dynamic.get("x").asInt(0) - this.xPos & 0xF;
                int j = dynamic.get("y").asInt(0);
                int l = j << 8 | (k = dynamic.get("z").asInt(0) - this.yPos & 0xF) << 4 | i;
                if (this.blockEntities.put(l, dynamic) != null) {
                    LOGGER.warn("In chunk: {}x{} found a duplicate block entity at position: [{}, {}, {}]", (Object)this.xPos, (Object)this.yPos, (Object)i, (Object)j, (Object)k);
                }
            }));
            boolean bl = dynamic.get("convertedFromAlphaFormat").asBoolean(false);
            dynamic.get("Sections").asStreamOpt().result().ifPresent(stream -> stream.forEach(dynamic -> {
                Section lv = new Section((Dynamic<?>)dynamic);
                this.sidesToUpgrade = lv.visit(this.sidesToUpgrade);
                this.sections[lv.y] = lv;
            }));
            for (Section lv : this.sections) {
                if (lv == null) continue;
                block14: for (Map.Entry entry : lv.inPlaceUpdates.entrySet()) {
                    int i = lv.y << 12;
                    switch ((Integer)entry.getKey()) {
                        case 2: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String string;
                                int j = (Integer)intListIterator.next();
                                Dynamic<?> dynamic2 = this.getBlock(j |= i);
                                if (!"minecraft:grass_block".equals(ChunkPalettedStorageFix.getName(dynamic2)) || !"minecraft:snow".equals(string = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(j, Facing.UP)))) && !"minecraft:snow_layer".equals(string)) continue;
                                this.setBlock(j, snowyGrass);
                            }
                            continue block14;
                        }
                        case 3: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String string2;
                                int k = (Integer)intListIterator.next();
                                Dynamic<?> dynamic3 = this.getBlock(k |= i);
                                if (!"minecraft:podzol".equals(ChunkPalettedStorageFix.getName(dynamic3)) || !"minecraft:snow".equals(string2 = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(k, Facing.UP)))) && !"minecraft:snow_layer".equals(string2)) continue;
                                this.setBlock(k, podzol);
                            }
                            continue block14;
                        }
                        case 110: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String string3;
                                int l = (Integer)intListIterator.next();
                                Dynamic<?> dynamic4 = this.getBlock(l |= i);
                                if (!"minecraft:mycelium".equals(ChunkPalettedStorageFix.getName(dynamic4)) || !"minecraft:snow".equals(string3 = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(l, Facing.UP)))) && !"minecraft:snow_layer".equals(string3)) continue;
                                this.setBlock(l, snowyMycelium);
                            }
                            continue block14;
                        }
                        case 25: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int m = (Integer)intListIterator.next();
                                Dynamic<?> dynamic5 = this.removeBlockEntity(m |= i);
                                if (dynamic5 == null) continue;
                                String string4 = Boolean.toString(dynamic5.get("powered").asBoolean(false)) + (byte)Math.min(Math.max(dynamic5.get("note").asInt(0), 0), 24);
                                this.setBlock(m, (Dynamic)noteblock.getOrDefault(string4, noteblock.get("false0")));
                            }
                            continue block14;
                        }
                        case 26: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int o;
                                int n = (Integer)intListIterator.next();
                                Dynamic<?> dynamic6 = this.getBlockEntity(n |= i);
                                Dynamic<?> dynamic7 = this.getBlock(n);
                                if (dynamic6 == null || (o = dynamic6.get("color").asInt(0)) == 14 || o < 0 || o >= 16) continue;
                                String string5 = ChunkPalettedStorageFix.getProperty(dynamic7, "facing") + ChunkPalettedStorageFix.getProperty(dynamic7, "occupied") + ChunkPalettedStorageFix.getProperty(dynamic7, "part") + o;
                                if (!bed.containsKey(string5)) continue;
                                this.setBlock(n, (Dynamic)bed.get(string5));
                            }
                            continue block14;
                        }
                        case 176: 
                        case 177: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int q;
                                int p = (Integer)intListIterator.next();
                                Dynamic<?> dynamic8 = this.getBlockEntity(p |= i);
                                Dynamic<?> dynamic9 = this.getBlock(p);
                                if (dynamic8 == null || (q = dynamic8.get("Base").asInt(0)) == 15 || q < 0 || q >= 16) continue;
                                String string6 = ChunkPalettedStorageFix.getProperty(dynamic9, (Integer)entry.getKey() == 176 ? "rotation" : "facing") + "_" + q;
                                if (!banner.containsKey(string6)) continue;
                                this.setBlock(p, (Dynamic)banner.get(string6));
                            }
                            continue block14;
                        }
                        case 86: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String string7;
                                int r = (Integer)intListIterator.next();
                                Dynamic<?> dynamic10 = this.getBlock(r |= i);
                                if (!"minecraft:carved_pumpkin".equals(ChunkPalettedStorageFix.getName(dynamic10)) || !"minecraft:grass_block".equals(string7 = ChunkPalettedStorageFix.getName(this.getBlock(Level.adjacentTo(r, Facing.DOWN)))) && !"minecraft:dirt".equals(string7)) continue;
                                this.setBlock(r, pumpkin);
                            }
                            continue block14;
                        }
                        case 140: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int s = (Integer)intListIterator.next();
                                Dynamic<?> dynamic11 = this.removeBlockEntity(s |= i);
                                if (dynamic11 == null) continue;
                                String string8 = dynamic11.get("Item").asString("") + dynamic11.get("Data").asInt(0);
                                this.setBlock(s, (Dynamic)flowerPot.getOrDefault(string8, flowerPot.get("minecraft:air0")));
                            }
                            continue block14;
                        }
                        case 144: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                String string12;
                                int t = (Integer)intListIterator.next();
                                Dynamic<?> dynamic12 = this.getBlockEntity(t |= i);
                                if (dynamic12 == null) continue;
                                String string9 = String.valueOf(dynamic12.get("SkullType").asInt(0));
                                String string10 = ChunkPalettedStorageFix.getProperty(this.getBlock(t), "facing");
                                if ("up".equals(string10) || "down".equals(string10)) {
                                    String string11 = string9 + String.valueOf(dynamic12.get("Rot").asInt(0));
                                } else {
                                    string12 = string9 + string10;
                                }
                                dynamic12.remove("SkullType");
                                dynamic12.remove("facing");
                                dynamic12.remove("Rot");
                                this.setBlock(t, (Dynamic)skull.getOrDefault(string12, skull.get("0north")));
                            }
                            continue block14;
                        }
                        case 64: 
                        case 71: 
                        case 193: 
                        case 194: 
                        case 195: 
                        case 196: 
                        case 197: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                Dynamic<?> dynamic14;
                                int u = (Integer)intListIterator.next();
                                Dynamic<?> dynamic13 = this.getBlock(u |= i);
                                if (!ChunkPalettedStorageFix.getName(dynamic13).endsWith("_door") || !"lower".equals(ChunkPalettedStorageFix.getProperty(dynamic14 = this.getBlock(u), "half"))) continue;
                                int v = Level.adjacentTo(u, Facing.UP);
                                Dynamic<?> dynamic15 = this.getBlock(v);
                                String string13 = ChunkPalettedStorageFix.getName(dynamic14);
                                if (!string13.equals(ChunkPalettedStorageFix.getName(dynamic15))) continue;
                                String string14 = ChunkPalettedStorageFix.getProperty(dynamic14, "facing");
                                String string15 = ChunkPalettedStorageFix.getProperty(dynamic14, "open");
                                String string16 = bl ? "left" : ChunkPalettedStorageFix.getProperty(dynamic15, "hinge");
                                String string17 = bl ? "false" : ChunkPalettedStorageFix.getProperty(dynamic15, "powered");
                                this.setBlock(u, (Dynamic)door.get(string13 + string14 + "lower" + string16 + string15 + string17));
                                this.setBlock(v, (Dynamic)door.get(string13 + string14 + "upper" + string16 + string15 + string17));
                            }
                            continue block14;
                        }
                        case 175: {
                            IntListIterator intListIterator = ((IntList)entry.getValue()).iterator();
                            while (intListIterator.hasNext()) {
                                int w = (Integer)intListIterator.next();
                                Dynamic<?> dynamic16 = this.getBlock(w |= i);
                                if (!"upper".equals(ChunkPalettedStorageFix.getProperty(dynamic16, "half"))) continue;
                                Dynamic<?> dynamic17 = this.getBlock(Level.adjacentTo(w, Facing.DOWN));
                                String string18 = ChunkPalettedStorageFix.getName(dynamic17);
                                if ("minecraft:sunflower".equals(string18)) {
                                    this.setBlock(w, sunflowerUpper);
                                    continue;
                                }
                                if ("minecraft:lilac".equals(string18)) {
                                    this.setBlock(w, lilacUpper);
                                    continue;
                                }
                                if ("minecraft:tall_grass".equals(string18)) {
                                    this.setBlock(w, grassUpper);
                                    continue;
                                }
                                if ("minecraft:large_fern".equals(string18)) {
                                    this.setBlock(w, fernUpper);
                                    continue;
                                }
                                if ("minecraft:rose_bush".equals(string18)) {
                                    this.setBlock(w, roseUpper);
                                    continue;
                                }
                                if (!"minecraft:peony".equals(string18)) continue;
                                this.setBlock(w, peonyUpper);
                            }
                            break;
                        }
                    }
                }
            }
        }

        @Nullable
        private Dynamic<?> getBlockEntity(int i) {
            return (Dynamic)this.blockEntities.get(i);
        }

        @Nullable
        private Dynamic<?> removeBlockEntity(int i) {
            return (Dynamic)this.blockEntities.remove(i);
        }

        public static int adjacentTo(int i, Facing arg) {
            switch (arg.getAxis()) {
                case X: {
                    int j = (i & 0xF) + arg.getDirection().getOffset();
                    return j < 0 || j > 15 ? -1 : i & 0xFFFFFFF0 | j;
                }
                case Y: {
                    int k = (i >> 8) + arg.getDirection().getOffset();
                    return k < 0 || k > 255 ? -1 : i & 0xFF | k << 8;
                }
                case Z: {
                    int l = (i >> 4 & 0xF) + arg.getDirection().getOffset();
                    return l < 0 || l > 15 ? -1 : i & 0xFFFFFF0F | l << 4;
                }
            }
            return -1;
        }

        private void setBlock(int i, Dynamic<?> dynamic) {
            if (i < 0 || i > 65535) {
                return;
            }
            Section lv = this.getSection(i);
            if (lv == null) {
                return;
            }
            lv.setBlock(i & 0xFFF, dynamic);
        }

        @Nullable
        private Section getSection(int i) {
            int j = i >> 12;
            return j < this.sections.length ? this.sections[j] : null;
        }

        public Dynamic<?> getBlock(int i) {
            if (i < 0 || i > 65535) {
                return air;
            }
            Section lv = this.getSection(i);
            if (lv == null) {
                return air;
            }
            return lv.getBlock(i & 0xFFF);
        }

        public Dynamic<?> transform() {
            Dynamic dynamic = this.level;
            dynamic = this.blockEntities.isEmpty() ? dynamic.remove("TileEntities") : dynamic.set("TileEntities", dynamic.createList(this.blockEntities.values().stream()));
            Dynamic dynamic2 = dynamic.emptyMap();
            ArrayList list = Lists.newArrayList();
            for (Section lv : this.sections) {
                if (lv == null) continue;
                list.add(lv.transform());
                dynamic2 = dynamic2.set(String.valueOf(lv.y), dynamic2.createIntList(Arrays.stream(lv.innerPositions.toIntArray())));
            }
            Dynamic dynamic3 = dynamic.emptyMap();
            dynamic3 = dynamic3.set("Sides", dynamic3.createByte((byte)this.sidesToUpgrade));
            dynamic3 = dynamic3.set("Indices", dynamic2);
            return dynamic.set("UpgradeData", dynamic3).set("Sections", dynamic3.createList(list.stream()));
        }
    }

    static class Section {
        private final Int2ObjectBiMap<Dynamic<?>> paletteMap = new Int2ObjectBiMap(32);
        private final List<Dynamic<?>> paletteData;
        private final Dynamic<?> section;
        private final boolean hasBlocks;
        private final Int2ObjectMap<IntList> inPlaceUpdates = new Int2ObjectLinkedOpenHashMap();
        private final IntList innerPositions = new IntArrayList();
        public final int y;
        private final Set<Dynamic<?>> seenStates = Sets.newIdentityHashSet();
        private final int[] states = new int[4096];

        public Section(Dynamic<?> dynamic) {
            this.paletteData = Lists.newArrayList();
            this.section = dynamic;
            this.y = dynamic.get("Y").asInt(0);
            this.hasBlocks = dynamic.get("Blocks").result().isPresent();
        }

        public Dynamic<?> getBlock(int i) {
            if (i < 0 || i > 4095) {
                return air;
            }
            Dynamic<?> dynamic = this.paletteMap.get(this.states[i]);
            return dynamic == null ? air : dynamic;
        }

        public void setBlock(int i, Dynamic<?> dynamic) {
            if (this.seenStates.add(dynamic)) {
                this.paletteData.add("%%FILTER_ME%%".equals(ChunkPalettedStorageFix.getName(dynamic)) ? air : dynamic);
            }
            this.states[i] = ChunkPalettedStorageFix.addTo(this.paletteMap, dynamic);
        }

        public int visit(int i) {
            if (!this.hasBlocks) {
                return i;
            }
            ByteBuffer byteBuffer2 = (ByteBuffer)this.section.get("Blocks").asByteBufferOpt().result().get();
            ChunkNibbleArray lv = this.section.get("Data").asByteBufferOpt().map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray((ByteBuffer)byteBuffer))).result().orElseGet(ChunkNibbleArray::new);
            ChunkNibbleArray lv2 = this.section.get("Add").asByteBufferOpt().map(byteBuffer -> new ChunkNibbleArray(DataFixUtils.toArray((ByteBuffer)byteBuffer))).result().orElseGet(ChunkNibbleArray::new);
            this.seenStates.add(air);
            ChunkPalettedStorageFix.addTo(this.paletteMap, air);
            this.paletteData.add(air);
            for (int j = 0; j < 4096; ++j) {
                int k = j & 0xF;
                int l = j >> 8 & 0xF;
                int m = j >> 4 & 0xF;
                int n = lv2.get(k, l, m) << 12 | (byteBuffer2.get(j) & 0xFF) << 4 | lv.get(k, l, m);
                if (blocksNeedingInPlaceUpdate.get(n >> 4)) {
                    this.addInPlaceUpdate(n >> 4, j);
                }
                if (blocksNeedingSideUpdate.get(n >> 4)) {
                    int o = ChunkPalettedStorageFix.getSideToUpgradeFlag(k == 0, k == 15, m == 0, m == 15);
                    if (o == 0) {
                        this.innerPositions.add(j);
                    } else {
                        i |= o;
                    }
                }
                this.setBlock(j, BlockStateFlattening.lookupState(n));
            }
            return i;
        }

        private void addInPlaceUpdate(int i, int j) {
            IntList intList = (IntList)this.inPlaceUpdates.get(i);
            if (intList == null) {
                intList = new IntArrayList();
                this.inPlaceUpdates.put(i, (Object)intList);
            }
            intList.add(j);
        }

        public Dynamic<?> transform() {
            Dynamic dynamic = this.section;
            if (!this.hasBlocks) {
                return dynamic;
            }
            dynamic = dynamic.set("Palette", dynamic.createList(this.paletteData.stream()));
            int i = Math.max(4, DataFixUtils.ceillog2((int)this.seenStates.size()));
            class_5298 lv = new class_5298(i, 4096);
            for (int j = 0; j < this.states.length; ++j) {
                lv.method_28153(j, this.states[j]);
            }
            dynamic = dynamic.set("BlockStates", dynamic.createLongList(Arrays.stream(lv.method_28151())));
            dynamic = dynamic.remove("Blocks");
            dynamic = dynamic.remove("Data");
            dynamic = dynamic.remove("Add");
            return dynamic;
        }
    }
}

