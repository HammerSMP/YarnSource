/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.ConstructBeaconCriterion;
import net.minecraft.advancement.criterion.EffectsChangedCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.advancement.criterion.NetherTravelCriterion;
import net.minecraft.advancement.criterion.OnKilledCriterion;
import net.minecraft.advancement.criterion.SummonedEntityCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.DistancePredicate;
import net.minecraft.predicate.entity.EntityEffectPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;

public class NetherTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement lv = Advancement.Task.create().display(Blocks.RED_NETHER_BRICKS, (Text)new TranslatableText("advancements.nether.root.title"), (Text)new TranslatableText("advancements.nether.root.description"), new Identifier("textures/gui/advancements/backgrounds/nether.png"), AdvancementFrame.TASK, false, false, false).criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.THE_NETHER)).build(consumer, "nether/root");
        Advancement lv2 = Advancement.Task.create().parent(lv).display(Items.FIRE_CHARGE, (Text)new TranslatableText("advancements.nether.return_to_sender.title"), (Text)new TranslatableText("advancements.nether.return_to_sender.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).criterion("killed_ghast", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.GHAST), DamageSourcePredicate.Builder.create().projectile(true).directEntity(EntityPredicate.Builder.create().type(EntityType.FIREBALL)))).build(consumer, "nether/return_to_sender");
        Advancement lv3 = Advancement.Task.create().parent(lv).display(Blocks.NETHER_BRICKS, (Text)new TranslatableText("advancements.nether.find_fortress.title"), (Text)new TranslatableText("advancements.nether.find_fortress.description"), null, AdvancementFrame.TASK, true, true, false).criterion("fortress", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(Feature.NETHER_BRIDGE))).build(consumer, "nether/find_fortress");
        Advancement lv4 = Advancement.Task.create().parent(lv).display(Items.MAP, (Text)new TranslatableText("advancements.nether.fast_travel.title"), (Text)new TranslatableText("advancements.nether.fast_travel.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("travelled", NetherTravelCriterion.Conditions.distance(DistancePredicate.horizontal(NumberRange.FloatRange.atLeast(7000.0f)))).build(consumer, "nether/fast_travel");
        Advancement lv5 = Advancement.Task.create().parent(lv2).display(Items.GHAST_TEAR, (Text)new TranslatableText("advancements.nether.uneasy_alliance.title"), (Text)new TranslatableText("advancements.nether.uneasy_alliance.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("killed_ghast", OnKilledCriterion.Conditions.createPlayerKilledEntity(EntityPredicate.Builder.create().type(EntityType.GHAST).location(LocationPredicate.dimension(DimensionType.OVERWORLD)))).build(consumer, "nether/uneasy_alliance");
        Advancement lv6 = Advancement.Task.create().parent(lv3).display(Blocks.WITHER_SKELETON_SKULL, (Text)new TranslatableText("advancements.nether.get_wither_skull.title"), (Text)new TranslatableText("advancements.nether.get_wither_skull.description"), null, AdvancementFrame.TASK, true, true, false).criterion("wither_skull", InventoryChangedCriterion.Conditions.items(Blocks.WITHER_SKELETON_SKULL)).build(consumer, "nether/get_wither_skull");
        Advancement lv7 = Advancement.Task.create().parent(lv6).display(Items.NETHER_STAR, (Text)new TranslatableText("advancements.nether.summon_wither.title"), (Text)new TranslatableText("advancements.nether.summon_wither.description"), null, AdvancementFrame.TASK, true, true, false).criterion("summoned", SummonedEntityCriterion.Conditions.create(EntityPredicate.Builder.create().type(EntityType.WITHER))).build(consumer, "nether/summon_wither");
        Advancement lv8 = Advancement.Task.create().parent(lv3).display(Items.BLAZE_ROD, (Text)new TranslatableText("advancements.nether.obtain_blaze_rod.title"), (Text)new TranslatableText("advancements.nether.obtain_blaze_rod.description"), null, AdvancementFrame.TASK, true, true, false).criterion("blaze_rod", InventoryChangedCriterion.Conditions.items(Items.BLAZE_ROD)).build(consumer, "nether/obtain_blaze_rod");
        Advancement lv9 = Advancement.Task.create().parent(lv7).display(Blocks.BEACON, (Text)new TranslatableText("advancements.nether.create_beacon.title"), (Text)new TranslatableText("advancements.nether.create_beacon.description"), null, AdvancementFrame.TASK, true, true, false).criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.atLeast(1))).build(consumer, "nether/create_beacon");
        Advancement lv10 = Advancement.Task.create().parent(lv9).display(Blocks.BEACON, (Text)new TranslatableText("advancements.nether.create_full_beacon.title"), (Text)new TranslatableText("advancements.nether.create_full_beacon.description"), null, AdvancementFrame.GOAL, true, true, false).criterion("beacon", ConstructBeaconCriterion.Conditions.level(NumberRange.IntRange.exactly(4))).build(consumer, "nether/create_full_beacon");
        Advancement lv11 = Advancement.Task.create().parent(lv8).display(Items.POTION, (Text)new TranslatableText("advancements.nether.brew_potion.title"), (Text)new TranslatableText("advancements.nether.brew_potion.description"), null, AdvancementFrame.TASK, true, true, false).criterion("potion", BrewedPotionCriterion.Conditions.any()).build(consumer, "nether/brew_potion");
        Advancement lv12 = Advancement.Task.create().parent(lv11).display(Items.MILK_BUCKET, (Text)new TranslatableText("advancements.nether.all_potions.title"), (Text)new TranslatableText("advancements.nether.all_potions.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("all_effects", EffectsChangedCriterion.Conditions.create(EntityEffectPredicate.create().withEffect(StatusEffects.SPEED).withEffect(StatusEffects.SLOWNESS).withEffect(StatusEffects.STRENGTH).withEffect(StatusEffects.JUMP_BOOST).withEffect(StatusEffects.REGENERATION).withEffect(StatusEffects.FIRE_RESISTANCE).withEffect(StatusEffects.WATER_BREATHING).withEffect(StatusEffects.INVISIBILITY).withEffect(StatusEffects.NIGHT_VISION).withEffect(StatusEffects.WEAKNESS).withEffect(StatusEffects.POISON).withEffect(StatusEffects.SLOW_FALLING).withEffect(StatusEffects.RESISTANCE))).build(consumer, "nether/all_potions");
        Advancement lv13 = Advancement.Task.create().parent(lv12).display(Items.BUCKET, (Text)new TranslatableText("advancements.nether.all_effects.title"), (Text)new TranslatableText("advancements.nether.all_effects.description"), null, AdvancementFrame.CHALLENGE, true, true, true).rewards(AdvancementRewards.Builder.experience(1000)).criterion("all_effects", EffectsChangedCriterion.Conditions.create(EntityEffectPredicate.create().withEffect(StatusEffects.SPEED).withEffect(StatusEffects.SLOWNESS).withEffect(StatusEffects.STRENGTH).withEffect(StatusEffects.JUMP_BOOST).withEffect(StatusEffects.REGENERATION).withEffect(StatusEffects.FIRE_RESISTANCE).withEffect(StatusEffects.WATER_BREATHING).withEffect(StatusEffects.INVISIBILITY).withEffect(StatusEffects.NIGHT_VISION).withEffect(StatusEffects.WEAKNESS).withEffect(StatusEffects.POISON).withEffect(StatusEffects.WITHER).withEffect(StatusEffects.HASTE).withEffect(StatusEffects.MINING_FATIGUE).withEffect(StatusEffects.LEVITATION).withEffect(StatusEffects.GLOWING).withEffect(StatusEffects.ABSORPTION).withEffect(StatusEffects.HUNGER).withEffect(StatusEffects.NAUSEA).withEffect(StatusEffects.RESISTANCE).withEffect(StatusEffects.SLOW_FALLING).withEffect(StatusEffects.CONDUIT_POWER).withEffect(StatusEffects.DOLPHINS_GRACE).withEffect(StatusEffects.BLINDNESS).withEffect(StatusEffects.BAD_OMEN).withEffect(StatusEffects.HERO_OF_THE_VILLAGE))).build(consumer, "nether/all_effects");
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}

