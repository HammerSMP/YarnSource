/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.templates.TypeTemplate
 */
package net.minecraft.datafixer.schema;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.datafixer.TypeReferences;

public class Schema100
extends Schema {
    public Schema100(int versionKey, Schema parent) {
        super(versionKey, parent);
    }

    protected static TypeTemplate targetItems(Schema schema) {
        return DSL.optionalFields((String)"ArmorItems", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"HandItems", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)));
    }

    protected static void targetEntity(Schema schema, Map<String, Supplier<TypeTemplate>> map, String string) {
        schema.register(map, string, () -> Schema100.targetItems(schema));
    }

    public Map<String, Supplier<TypeTemplate>> registerEntities(Schema schema) {
        Map map = super.registerEntities(schema);
        Schema100.targetEntity(schema, map, "ArmorStand");
        Schema100.targetEntity(schema, map, "Creeper");
        Schema100.targetEntity(schema, map, "Skeleton");
        Schema100.targetEntity(schema, map, "Spider");
        Schema100.targetEntity(schema, map, "Giant");
        Schema100.targetEntity(schema, map, "Zombie");
        Schema100.targetEntity(schema, map, "Slime");
        Schema100.targetEntity(schema, map, "Ghast");
        Schema100.targetEntity(schema, map, "PigZombie");
        schema.register(map, "Enderman", string -> DSL.optionalFields((String)"carried", (TypeTemplate)TypeReferences.BLOCK_NAME.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        Schema100.targetEntity(schema, map, "CaveSpider");
        Schema100.targetEntity(schema, map, "Silverfish");
        Schema100.targetEntity(schema, map, "Blaze");
        Schema100.targetEntity(schema, map, "LavaSlime");
        Schema100.targetEntity(schema, map, "EnderDragon");
        Schema100.targetEntity(schema, map, "WitherBoss");
        Schema100.targetEntity(schema, map, "Bat");
        Schema100.targetEntity(schema, map, "Witch");
        Schema100.targetEntity(schema, map, "Endermite");
        Schema100.targetEntity(schema, map, "Guardian");
        Schema100.targetEntity(schema, map, "Pig");
        Schema100.targetEntity(schema, map, "Sheep");
        Schema100.targetEntity(schema, map, "Cow");
        Schema100.targetEntity(schema, map, "Chicken");
        Schema100.targetEntity(schema, map, "Squid");
        Schema100.targetEntity(schema, map, "Wolf");
        Schema100.targetEntity(schema, map, "MushroomCow");
        Schema100.targetEntity(schema, map, "SnowMan");
        Schema100.targetEntity(schema, map, "Ozelot");
        Schema100.targetEntity(schema, map, "VillagerGolem");
        schema.register(map, "EntityHorse", string -> DSL.optionalFields((String)"Items", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"ArmorItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"SaddleItem", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (TypeTemplate)Schema100.targetItems(schema)));
        Schema100.targetEntity(schema, map, "Rabbit");
        schema.register(map, "Villager", string -> DSL.optionalFields((String)"Inventory", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.ITEM_STACK.in(schema)), (String)"Offers", (TypeTemplate)DSL.optionalFields((String)"Recipes", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"buy", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"buyB", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema), (String)"sell", (TypeTemplate)TypeReferences.ITEM_STACK.in(schema)))), (TypeTemplate)Schema100.targetItems(schema)));
        Schema100.targetEntity(schema, map, "Shulker");
        schema.registerSimple(map, "AreaEffectCloud");
        schema.registerSimple(map, "ShulkerBullet");
        return map;
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> entityTypes, Map<String, Supplier<TypeTemplate>> blockEntityTypes) {
        super.registerTypes(schema, entityTypes, blockEntityTypes);
        schema.registerType(false, TypeReferences.STRUCTURE, () -> DSL.optionalFields((String)"entities", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)TypeReferences.ENTITY_TREE.in(schema))), (String)"blocks", (TypeTemplate)DSL.list((TypeTemplate)DSL.optionalFields((String)"nbt", (TypeTemplate)TypeReferences.BLOCK_ENTITY.in(schema))), (String)"palette", (TypeTemplate)DSL.list((TypeTemplate)TypeReferences.BLOCK_STATE.in(schema))));
        schema.registerType(false, TypeReferences.BLOCK_STATE, DSL::remainder);
    }
}

