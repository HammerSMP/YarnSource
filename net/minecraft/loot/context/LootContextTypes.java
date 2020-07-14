/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  javax.annotation.Nullable
 */
package net.minecraft.loot.context;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.util.Identifier;

public class LootContextTypes {
    private static final BiMap<Identifier, LootContextType> MAP = HashBiMap.create();
    public static final LootContextType EMPTY = LootContextTypes.register("empty", builder -> {});
    public static final LootContextType CHEST = LootContextTypes.register("chest", builder -> builder.require(LootContextParameters.POSITION).allow(LootContextParameters.THIS_ENTITY));
    public static final LootContextType COMMAND = LootContextTypes.register("command", arg -> arg.require(LootContextParameters.POSITION).allow(LootContextParameters.THIS_ENTITY));
    public static final LootContextType SELECTOR = LootContextTypes.register("selector", arg -> arg.require(LootContextParameters.POSITION).require(LootContextParameters.THIS_ENTITY));
    public static final LootContextType FISHING = LootContextTypes.register("fishing", builder -> builder.require(LootContextParameters.POSITION).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY));
    public static final LootContextType ENTITY = LootContextTypes.register("entity", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.POSITION).require(LootContextParameters.DAMAGE_SOURCE).allow(LootContextParameters.KILLER_ENTITY).allow(LootContextParameters.DIRECT_KILLER_ENTITY).allow(LootContextParameters.LAST_DAMAGE_PLAYER));
    public static final LootContextType GIFT = LootContextTypes.register("gift", builder -> builder.require(LootContextParameters.POSITION).require(LootContextParameters.THIS_ENTITY));
    public static final LootContextType BARTER = LootContextTypes.register("barter", arg -> arg.require(LootContextParameters.THIS_ENTITY));
    public static final LootContextType ADVANCEMENT_REWARD = LootContextTypes.register("advancement_reward", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.POSITION));
    public static final LootContextType ADVANCEMENT_ENTITY = LootContextTypes.register("advancement_entity", arg -> arg.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.ORIGIN).require(LootContextParameters.POSITION));
    public static final LootContextType GENERIC = LootContextTypes.register("generic", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(LootContextParameters.LAST_DAMAGE_PLAYER).require(LootContextParameters.DAMAGE_SOURCE).require(LootContextParameters.KILLER_ENTITY).require(LootContextParameters.DIRECT_KILLER_ENTITY).require(LootContextParameters.POSITION).require(LootContextParameters.BLOCK_STATE).require(LootContextParameters.BLOCK_ENTITY).require(LootContextParameters.TOOL).require(LootContextParameters.EXPLOSION_RADIUS));
    public static final LootContextType BLOCK = LootContextTypes.register("block", builder -> builder.require(LootContextParameters.BLOCK_STATE).require(LootContextParameters.POSITION).require(LootContextParameters.TOOL).allow(LootContextParameters.THIS_ENTITY).allow(LootContextParameters.BLOCK_ENTITY).allow(LootContextParameters.EXPLOSION_RADIUS));

    private static LootContextType register(String name, Consumer<LootContextType.Builder> type) {
        LootContextType.Builder lv = new LootContextType.Builder();
        type.accept(lv);
        LootContextType lv2 = lv.build();
        Identifier lv3 = new Identifier(name);
        LootContextType lv4 = (LootContextType)MAP.put((Object)lv3, (Object)lv2);
        if (lv4 != null) {
            throw new IllegalStateException("Loot table parameter set " + lv3 + " is already registered");
        }
        return lv2;
    }

    @Nullable
    public static LootContextType get(Identifier id) {
        return (LootContextType)MAP.get((Object)id);
    }

    @Nullable
    public static Identifier getId(LootContextType type) {
        return (Identifier)MAP.inverse().get((Object)type);
    }
}

