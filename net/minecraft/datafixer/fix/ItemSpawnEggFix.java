/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.Typed
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Dynamic
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.schema.IdentifierNormalizingSchema;

public class ItemSpawnEggFix
extends DataFix {
    private static final String[] DAMAGE_TO_ENTITY_IDS = (String[])DataFixUtils.make((Object)new String[256], strings -> {
        strings[1] = "Item";
        strings[2] = "XPOrb";
        strings[7] = "ThrownEgg";
        strings[8] = "LeashKnot";
        strings[9] = "Painting";
        strings[10] = "Arrow";
        strings[11] = "Snowball";
        strings[12] = "Fireball";
        strings[13] = "SmallFireball";
        strings[14] = "ThrownEnderpearl";
        strings[15] = "EyeOfEnderSignal";
        strings[16] = "ThrownPotion";
        strings[17] = "ThrownExpBottle";
        strings[18] = "ItemFrame";
        strings[19] = "WitherSkull";
        strings[20] = "PrimedTnt";
        strings[21] = "FallingSand";
        strings[22] = "FireworksRocketEntity";
        strings[23] = "TippedArrow";
        strings[24] = "SpectralArrow";
        strings[25] = "ShulkerBullet";
        strings[26] = "DragonFireball";
        strings[30] = "ArmorStand";
        strings[41] = "Boat";
        strings[42] = "MinecartRideable";
        strings[43] = "MinecartChest";
        strings[44] = "MinecartFurnace";
        strings[45] = "MinecartTNT";
        strings[46] = "MinecartHopper";
        strings[47] = "MinecartSpawner";
        strings[40] = "MinecartCommandBlock";
        strings[48] = "Mob";
        strings[49] = "Monster";
        strings[50] = "Creeper";
        strings[51] = "Skeleton";
        strings[52] = "Spider";
        strings[53] = "Giant";
        strings[54] = "Zombie";
        strings[55] = "Slime";
        strings[56] = "Ghast";
        strings[57] = "PigZombie";
        strings[58] = "Enderman";
        strings[59] = "CaveSpider";
        strings[60] = "Silverfish";
        strings[61] = "Blaze";
        strings[62] = "LavaSlime";
        strings[63] = "EnderDragon";
        strings[64] = "WitherBoss";
        strings[65] = "Bat";
        strings[66] = "Witch";
        strings[67] = "Endermite";
        strings[68] = "Guardian";
        strings[69] = "Shulker";
        strings[90] = "Pig";
        strings[91] = "Sheep";
        strings[92] = "Cow";
        strings[93] = "Chicken";
        strings[94] = "Squid";
        strings[95] = "Wolf";
        strings[96] = "MushroomCow";
        strings[97] = "SnowMan";
        strings[98] = "Ozelot";
        strings[99] = "VillagerGolem";
        strings[100] = "EntityHorse";
        strings[101] = "Rabbit";
        strings[120] = "Villager";
        strings[200] = "EnderCrystal";
    });

    public ItemSpawnEggFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    public TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        Type type = schema.getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = DSL.fieldFinder((String)"id", (Type)DSL.named((String)TypeReferences.ITEM_NAME.typeName(), IdentifierNormalizingSchema.method_28295()));
        OpticFinder opticFinder2 = DSL.fieldFinder((String)"id", (Type)DSL.string());
        OpticFinder opticFinder3 = type.findField("tag");
        OpticFinder opticFinder4 = opticFinder3.type().findField("EntityTag");
        OpticFinder opticFinder5 = DSL.typeFinder((Type)schema.getTypeRaw(TypeReferences.ENTITY));
        Type type2 = this.getOutputSchema().getTypeRaw(TypeReferences.ENTITY);
        return this.fixTypeEverywhereTyped("ItemSpawnEggFix", type, typed2 -> {
            Optional optional = typed2.getOptional(opticFinder);
            if (optional.isPresent() && Objects.equals(((Pair)optional.get()).getSecond(), "minecraft:spawn_egg")) {
                Dynamic dynamic = (Dynamic)typed2.get(DSL.remainderFinder());
                short s = dynamic.get("Damage").asShort((short)0);
                Optional optional2 = typed2.getOptionalTyped(opticFinder3);
                Optional optional3 = optional2.flatMap(typed -> typed.getOptionalTyped(opticFinder4));
                Optional optional4 = optional3.flatMap(typed -> typed.getOptionalTyped(opticFinder5));
                Optional optional5 = optional4.flatMap(typed -> typed.getOptional(opticFinder2));
                Typed typed22 = typed2;
                String string = DAMAGE_TO_ENTITY_IDS[s & 0xFF];
                if (!(string == null || optional5.isPresent() && Objects.equals(optional5.get(), string))) {
                    Typed typed3 = typed2.getOrCreateTyped(opticFinder3);
                    Typed typed4 = typed3.getOrCreateTyped(opticFinder4);
                    Typed typed5 = typed4.getOrCreateTyped(opticFinder5);
                    Dynamic dynamic22 = dynamic;
                    Typed typed6 = (Typed)((Pair)typed5.write().flatMap(dynamic2 -> type2.readTyped(dynamic2.set("id", dynamic22.createString(string)))).result().orElseThrow(() -> new IllegalStateException("Could not parse new entity"))).getFirst();
                    typed22 = typed22.set(opticFinder3, typed3.set(opticFinder4, typed4.set(opticFinder5, typed6)));
                }
                if (s != 0) {
                    dynamic = dynamic.set("Damage", dynamic.createShort((short)0));
                    typed22 = typed22.set(DSL.remainderFinder(), (Object)dynamic);
                }
                return typed22;
            }
            return typed2;
        });
    }
}

