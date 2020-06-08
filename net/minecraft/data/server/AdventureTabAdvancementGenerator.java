/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriterionMerger;
import net.minecraft.advancement.criterion.ChanneledLightningCriterion;
import net.minecraft.advancement.criterion.KilledByCrossbowCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.PlayerHurtEntityCriterion;
import net.minecraft.advancement.criterion.ShotCrossbowCriterion;
import net.minecraft.advancement.criterion.SlideDownBlockCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.advancement.criterion.TargetHitCriterion;
import net.minecraft.advancement.criterion.UsedTotemCriterion;
import net.minecraft.advancement.criterion.VillagerTradeCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.raid.Raid;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEquipmentPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class AdventureTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    private static final Biome[] BIOMES = new Biome[]{Biomes.BIRCH_FOREST_HILLS, Biomes.RIVER, Biomes.SWAMP, Biomes.DESERT, Biomes.WOODED_HILLS, Biomes.GIANT_TREE_TAIGA_HILLS, Biomes.SNOWY_TAIGA, Biomes.BADLANDS, Biomes.FOREST, Biomes.STONE_SHORE, Biomes.SNOWY_TUNDRA, Biomes.TAIGA_HILLS, Biomes.SNOWY_MOUNTAINS, Biomes.WOODED_BADLANDS_PLATEAU, Biomes.SAVANNA, Biomes.PLAINS, Biomes.FROZEN_RIVER, Biomes.GIANT_TREE_TAIGA, Biomes.SNOWY_BEACH, Biomes.JUNGLE_HILLS, Biomes.JUNGLE_EDGE, Biomes.MUSHROOM_FIELD_SHORE, Biomes.MOUNTAINS, Biomes.DESERT_HILLS, Biomes.JUNGLE, Biomes.BEACH, Biomes.SAVANNA_PLATEAU, Biomes.SNOWY_TAIGA_HILLS, Biomes.BADLANDS_PLATEAU, Biomes.DARK_FOREST, Biomes.TAIGA, Biomes.BIRCH_FOREST, Biomes.MUSHROOM_FIELDS, Biomes.WOODED_MOUNTAINS, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.BAMBOO_JUNGLE, Biomes.BAMBOO_JUNGLE_HILLS};
    private static final EntityType<?>[] MONSTERS = new EntityType[]{EntityType.BLAZE, EntityType.CAVE_SPIDER, EntityType.CREEPER, EntityType.DROWNED, EntityType.ELDER_GUARDIAN, EntityType.ENDER_DRAGON, EntityType.ENDERMAN, EntityType.ENDERMITE, EntityType.EVOKER, EntityType.GHAST, EntityType.GUARDIAN, EntityType.HOGLIN, EntityType.HUSK, EntityType.MAGMA_CUBE, EntityType.PHANTOM, EntityType.PIGLIN, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.SHULKER, EntityType.SILVERFISH, EntityType.SKELETON, EntityType.SLIME, EntityType.SPIDER, EntityType.STRAY, EntityType.VEX, EntityType.VINDICATOR, EntityType.WITCH, EntityType.WITHER_SKELETON, EntityType.WITHER, EntityType.ZOGLIN, EntityType.ZOMBIE_VILLAGER, EntityType.ZOMBIE, EntityType.ZOMBIFIED_PIGLIN};

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement lv = Advancement.Task.create().display(Items.MAP, (Text)new TranslatableText("advancements.adventure.root.title"), (Text)new TranslatableText("advancements.adventure.root.description"), new Identifier("textures/gui/advancements/backgrounds/adventure.png"), AdvancementFrame.TASK, false, false, false).criteriaMerger(CriterionMerger.OR).criterion("killed_something", OnKilledCriterion.Conditions.createPlayerKilledEntity()).criterion("killed_by_something", OnKilledCriterion.Conditions.createEntityKilledPlayer()).build(consumer, "adventure/root");
        Advancement lv2 = Advancement.Task.create().parent(lv).display(Blocks.RED_BED, (Text)new TranslatableText("advancements.adventure.sleep_in_bed.title"), (Text)new TranslatableText("advancements.adventure.sleep_in_bed.description"), null, AdvancementFrame.TASK, true, true, false).criterion("slept_in_bed", LocationArrivalCriterion.Conditions.createSleptInBed()).build(consumer, "adventure/sleep_in_bed");
        AdventureTabAdvancementGenerator.requireListedBiomesVisited(Advancement.Task.create(), BIOMES).parent(lv2).display(Items.DIAMOND_BOOTS, (Text)new TranslatableText("advancements.adventure.adventuring_time.title"), (Text)new TranslatableText("advancements.adventure.adventuring_time.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(500)).build(consumer, "adventure/adventuring_time");
        Advancement lv3 = Advancement.Task.create().parent(lv).display(Items.EMERALD, (Text)new TranslatableText("advancements.adventure.trade.title"), (Text)new TranslatableText("advancements.adventure.trade.description"), null, AdvancementFrame.TASK, true, true, false).criterion("traded", VillagerTradeCriterion.Conditions.any()).build(consumer, "adventure/trade");
        Advancement lv4 = this.requireListedMobsKilled(Advancement.Task.create()).parent(lv).display(Items.IRON_SWORD, (Text)new TranslatableText("advancements.adventure.kill_a_mob.title"), (Text)new TranslatableText("advancements.adventure.kill_a_mob.description"), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriterionMerger.OR).build(consumer, "adventure/kill_a_mob");
        this.requireListedMobsKilled(Advancement.Task.create()).parent(lv4).display(Items.DIAMOND_SWORD, (Text)new TranslatableText("advancements.adventure.kill_all_mobs.title"), (Text)new TranslatableText("advancements.adventure.kill_all_mobs.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "adventure/kill_all_mobs");
        Advancement lv5 = Advancement.Task.create().parent(lv4).display(Items.BOW, (Text)new TranslatableText("advancements.adventure.shoot_arrow.title"), (Text)new TranslatableText("advancements.adventure.shoot_arrow.description"), null, AdvancementFrame.TASK, true, true, false).criterion("shot_arrow", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityTypeTags.ARROWS))))).build(consumer, "adventure/shoot_arrow");
        Advancement lv6 = Advancement.Task.create().parent(lv4).display(Items.TRIDENT, (Text)new TranslatableText("advancements.adventure.throw_trident.title"), (Text)new TranslatableText("advancements.adventure.throw_trident.description"), null, AdvancementFrame.TASK, true, true, false).criterion("shot_trident", PlayerHurtEntityCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.TRIDENT))))).build(consumer, "adventure/throw_trident");
        Advancement.Task.create().parent(lv6).display(Items.TRIDENT, (Text)new TranslatableText("advancements.adventure.very_very_frightening.title"), (Text)new TranslatableText("advancements.adventure.very_very_frightening.description"), null, AdvancementFrame.TASK, true, true, false).criterion("struck_villager", ChanneledLightningCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.VILLAGER).build())).build(consumer, "adventure/very_very_frightening");
        Advancement.Task.create().parent(lv3).display(Blocks.CARVED_PUMPKIN, (Text)new TranslatableText("advancements.adventure.summon_iron_golem.title"), (Text)new TranslatableText("advancements.adventure.summon_iron_golem.description"), null, AdvancementFrame.GOAL, true, true, false).criterion("summoned_golem", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.IRON_GOLEM))).build(consumer, "adventure/summon_iron_golem");
        Advancement.Task.create().parent(lv5).display(Items.ARROW, (Text)new TranslatableText("advancements.adventure.sniper_duel.title"), (Text)new TranslatableText("advancements.adventure.sniper_duel.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("killed_skeleton", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.SKELETON).distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(50.0f))), DamageSourcePredicate.Builder.create().projectile(true))).build(consumer, "adventure/sniper_duel");
        Advancement.Task.create().parent(lv4).display(Items.TOTEM_OF_UNDYING, (Text)new TranslatableText("advancements.adventure.totem_of_undying.title"), (Text)new TranslatableText("advancements.adventure.totem_of_undying.description"), null, AdvancementFrame.GOAL, true, true, false).criterion("used_totem", UsedTotemCriterion.Conditions.create(Items.TOTEM_OF_UNDYING)).build(consumer, "adventure/totem_of_undying");
        Advancement lv7 = Advancement.Task.create().parent(lv).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.ol_betsy.title"), (Text)new TranslatableText("advancements.adventure.ol_betsy.description"), null, AdvancementFrame.TASK, true, true, false).criterion("shot_crossbow", ShotCrossbowCriterion.Conditions.create(Items.CROSSBOW)).build(consumer, "adventure/ol_betsy");
        Advancement.Task.create().parent(lv7).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.whos_the_pillager_now.title"), (Text)new TranslatableText("advancements.adventure.whos_the_pillager_now.description"), null, AdvancementFrame.TASK, true, true, false).criterion("kill_pillager", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PILLAGER))).build(consumer, "adventure/whos_the_pillager_now");
        Advancement.Task.create().parent(lv7).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.two_birds_one_arrow.title"), (Text)new TranslatableText("advancements.adventure.two_birds_one_arrow.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(65)).criterion("two_birds", KilledByCrossbowCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.PHANTOM), EntityPredicate.Builder.create().type(EntityType.PHANTOM))).build(consumer, "adventure/two_birds_one_arrow");
        Advancement.Task.create().parent(lv7).display(Items.CROSSBOW, (Text)new TranslatableText("advancements.adventure.arbalistic.title"), (Text)new TranslatableText("advancements.adventure.arbalistic.description"), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(85)).criterion("arbalistic", KilledByCrossbowCriterion.Conditions.create(NumberRange.IntRange.exactly(5))).build(consumer, "adventure/arbalistic");
        Advancement lv8 = Advancement.Task.create().parent(lv).display(Raid.getOminousBanner(), (Text)new TranslatableText("advancements.adventure.voluntary_exile.title"), (Text)new TranslatableText("advancements.adventure.voluntary_exile.description"), null, AdvancementFrame.TASK, true, true, true).criterion("voluntary_exile", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityTypeTags.RAIDERS).equipment(EntityEquipmentPredicate.OMINOUS_BANNER_ON_HEAD))).build(consumer, "adventure/voluntary_exile");
        Advancement.Task.create().parent(lv8).display(Raid.getOminousBanner(), (Text)new TranslatableText("advancements.adventure.hero_of_the_village.title"), (Text)new TranslatableText("advancements.adventure.hero_of_the_village.description"), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(100)).criterion("hero_of_the_village", LocationArrivalCriterion.Conditions.createHeroOfTheVillage()).build(consumer, "adventure/hero_of_the_village");
        Advancement.Task.create().parent(lv).display(Blocks.HONEY_BLOCK.asItem(), (Text)new TranslatableText("advancements.adventure.honey_block_slide.title"), (Text)new TranslatableText("advancements.adventure.honey_block_slide.description"), null, AdvancementFrame.TASK, true, true, false).criterion("honey_block_slide", SlideDownBlockCriterion.Conditions.create(Blocks.HONEY_BLOCK)).build(consumer, "adventure/honey_block_slide");
        Advancement.Task.create().parent(lv5).display(Blocks.TARGET.asItem(), (Text)new TranslatableText("advancements.adventure.bullseye.title"), (Text)new TranslatableText("advancements.adventure.bullseye.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("bullseye", TargetHitCriterion.Conditions.create(NumberRange.IntRange.exactly(15), EntityPredicate.Extended.ofLegacy(EntityPredicate.Builder.create().distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(30.0f))).build()))).build(consumer, "adventure/bullseye");
    }

    private Advancement.Task requireListedMobsKilled(Advancement.Task arg) {
        for (EntityType<?> lv : MONSTERS) {
            arg.criterion(Registry.ENTITY_TYPE.getId(lv).toString(), OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(lv)));
        }
        return arg;
    }

    protected static Advancement.Task requireListedBiomesVisited(Advancement.Task arg, Biome[] args) {
        for (Biome lv : args) {
            arg.criterion(Registry.BIOME.getId(lv).toString(), LocationArrivalCriterion.Conditions.create(LocationPredicate.biome(lv)));
        }
        return arg;
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}

