/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.Dynamic
 *  com.mojang.datafixers.OptionalDynamic
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.datafixer.TypeReferences;

public class EntityProjectileOwnerFix
extends DataFix {
    public EntityProjectileOwnerFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        return this.fixTypeEverywhereTyped("EntityProjectileOwner", schema.getType(TypeReferences.ENTITY), this::fixEntities);
    }

    private Typed<?> fixEntities(Typed<?> typed) {
        typed = this.update(typed, "minecraft:egg", this::moveOwnerToArray);
        typed = this.update(typed, "minecraft:ender_pearl", this::moveOwnerToArray);
        typed = this.update(typed, "minecraft:experience_bottle", this::moveOwnerToArray);
        typed = this.update(typed, "minecraft:snowball", this::moveOwnerToArray);
        typed = this.update(typed, "minecraft:potion", this::moveOwnerToArray);
        typed = this.update(typed, "minecraft:potion", this::renamePotionToItem);
        typed = this.update(typed, "minecraft:llama_spit", this::moveNestedOwnerMostLeastToArray);
        typed = this.update(typed, "minecraft:arrow", this::moveFlatOwnerMostLeastToArray);
        typed = this.update(typed, "minecraft:spectral_arrow", this::moveFlatOwnerMostLeastToArray);
        typed = this.update(typed, "minecraft:trident", this::moveFlatOwnerMostLeastToArray);
        return typed;
    }

    private Dynamic<?> moveFlatOwnerMostLeastToArray(Dynamic<?> dynamic) {
        long l = dynamic.get("OwnerUUIDMost").asLong(0L);
        long m = dynamic.get("OwnerUUIDLeast").asLong(0L);
        return this.insertOwnerUuidArray(dynamic, l, m).remove("OwnerUUIDMost").remove("OwnerUUIDLeast");
    }

    private Dynamic<?> moveNestedOwnerMostLeastToArray(Dynamic<?> dynamic) {
        OptionalDynamic optionalDynamic = dynamic.get("Owner");
        long l = optionalDynamic.get("OwnerUUIDMost").asLong(0L);
        long m = optionalDynamic.get("OwnerUUIDLeast").asLong(0L);
        return this.insertOwnerUuidArray(dynamic, l, m).remove("Owner");
    }

    private Dynamic<?> renamePotionToItem(Dynamic<?> dynamic) {
        OptionalDynamic optionalDynamic = dynamic.get("Potion");
        return dynamic.set("Item", optionalDynamic.orElseEmptyMap()).remove("Potion");
    }

    private Dynamic<?> moveOwnerToArray(Dynamic<?> dynamic) {
        String string = "owner";
        OptionalDynamic optionalDynamic = dynamic.get("owner");
        long l = optionalDynamic.get("M").asLong(0L);
        long m = optionalDynamic.get("L").asLong(0L);
        return this.insertOwnerUuidArray(dynamic, l, m).remove("owner");
    }

    private Dynamic<?> insertOwnerUuidArray(Dynamic<?> dynamic, long l, long m) {
        String string = "OwnerUUID";
        if (l != 0L && m != 0L) {
            return dynamic.set("OwnerUUID", dynamic.createIntList(Arrays.stream(EntityProjectileOwnerFix.makeUuidArray(l, m))));
        }
        return dynamic;
    }

    private static int[] makeUuidArray(long l, long m) {
        return new int[]{(int)(l >> 32), (int)l, (int)(m >> 32), (int)m};
    }

    private Typed<?> update(Typed<?> typed2, String string, Function<Dynamic<?>, Dynamic<?>> function) {
        Type type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, string);
        Type type2 = this.getOutputSchema().getChoiceType(TypeReferences.ENTITY, string);
        return typed2.updateTyped(DSL.namedChoice((String)string, (Type)type), type2, typed -> typed.update(DSL.remainderFinder(), function));
    }
}

