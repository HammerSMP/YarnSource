/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.BeeNestDestroyedCriterion;
import net.minecraft.advancement.criterion.BlockUsedCriterion;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.advancement.criterion.FilledBucketCriterion;
import net.minecraft.advancement.criterion.FishingRodHookedCriterion;
import net.minecraft.advancement.criterion.ItemDurabilityChangedCriterion;
import net.minecraft.advancement.criterion.PlacedBlockCriterion;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HusbandryTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    private static final EntityType<?>[] BREEDABLE_ANIMALS = new EntityType[]{EntityType.HORSE, EntityType.SHEEP, EntityType.COW, EntityType.MOOSHROOM, EntityType.PIG, EntityType.CHICKEN, EntityType.WOLF, EntityType.OCELOT, EntityType.RABBIT, EntityType.LLAMA, EntityType.TURTLE, EntityType.CAT, EntityType.PANDA, EntityType.FOX, EntityType.BEE, EntityType.HOGLIN};
    private static final Item[] FISH_ITEMS = new Item[]{Items.COD, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.SALMON};
    private static final Item[] FISH_BUCKET_ITEMS = new Item[]{Items.COD_BUCKET, Items.TROPICAL_FISH_BUCKET, Items.PUFFERFISH_BUCKET, Items.SALMON_BUCKET};
    private static final Item[] FOOD_ITEMS = new Item[]{Items.APPLE, Items.MUSHROOM_STEW, Items.BREAD, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE, Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH, Items.COOKED_COD, Items.COOKED_SALMON, Items.COOKIE, Items.MELON_SLICE, Items.BEEF, Items.COOKED_BEEF, Items.CHICKEN, Items.COOKED_CHICKEN, Items.ROTTEN_FLESH, Items.SPIDER_EYE, Items.CARROT, Items.POTATO, Items.BAKED_POTATO, Items.POISONOUS_POTATO, Items.GOLDEN_CARROT, Items.PUMPKIN_PIE, Items.RABBIT, Items.COOKED_RABBIT, Items.RABBIT_STEW, Items.MUTTON, Items.COOKED_MUTTON, Items.CHORUS_FRUIT, Items.BEETROOT, Items.BEETROOT_SOUP, Items.DRIED_KELP, Items.SUSPICIOUS_STEW, Items.SWEET_BERRIES, Items.HONEY_BOTTLE};

    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement lv = Advancement.Task.create().display(Blocks.HAY_BLOCK, (Text)new TranslatableText("advancements.husbandry.root.title"), (Text)new TranslatableText("advancements.husbandry.root.description"), new Identifier("textures/gui/advancements/backgrounds/husbandry.png"), AdvancementFrame.TASK, false, false, false).criterion("consumed_item", ConsumeItemCriterion.Conditions.any()).build(consumer, "husbandry/root");
        Advancement lv2 = Advancement.Task.create().parent(lv).display(Items.WHEAT, (Text)new TranslatableText("advancements.husbandry.plant_seed.title"), (Text)new TranslatableText("advancements.husbandry.plant_seed.description"), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("wheat", PlacedBlockCriterion.Conditions.block(Blocks.WHEAT)).criterion("pumpkin_stem", PlacedBlockCriterion.Conditions.block(Blocks.PUMPKIN_STEM)).criterion("melon_stem", PlacedBlockCriterion.Conditions.block(Blocks.MELON_STEM)).criterion("beetroots", PlacedBlockCriterion.Conditions.block(Blocks.BEETROOTS)).criterion("nether_wart", PlacedBlockCriterion.Conditions.block(Blocks.NETHER_WART)).build(consumer, "husbandry/plant_seed");
        Advancement lv3 = Advancement.Task.create().parent(lv).display(Items.WHEAT, (Text)new TranslatableText("advancements.husbandry.breed_an_animal.title"), (Text)new TranslatableText("advancements.husbandry.breed_an_animal.description"), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("bred", BredAnimalsCriterion.Conditions.any()).build(consumer, "husbandry/breed_an_animal");
        Advancement lv4 = this.requireFoodItemsEaten(Advancement.Task.create()).parent(lv2).display(Items.APPLE, (Text)new TranslatableText("advancements.husbandry.balanced_diet.title"), (Text)new TranslatableText("advancements.husbandry.balanced_diet.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "husbandry/balanced_diet");
        Advancement lv5 = Advancement.Task.create().parent(lv2).display(Items.DIAMOND_HOE, (Text)new TranslatableText("advancements.husbandry.break_diamond_hoe.title"), (Text)new TranslatableText("advancements.husbandry.break_diamond_hoe.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).criterion("broke_hoe", ItemDurabilityChangedCriterion.Conditions.create(ItemPredicate.Builder.create().item(Items.DIAMOND_HOE).build(), NumberRange.IntRange.exactly(0))).build(consumer, "husbandry/break_diamond_hoe");
        Advancement lv6 = Advancement.Task.create().parent(lv).display(Items.LEAD, (Text)new TranslatableText("advancements.husbandry.tame_an_animal.title"), (Text)new TranslatableText("advancements.husbandry.tame_an_animal.description"), null, AdvancementFrame.TASK, true, true, false).criterion("tamed_animal", TameAnimalCriterion.Conditions.any()).build(consumer, "husbandry/tame_an_animal");
        Advancement lv7 = this.requireListedAnimalsBred(Advancement.Task.create()).parent(lv3).display(Items.GOLDEN_CARROT, (Text)new TranslatableText("advancements.husbandry.breed_all_animals.title"), (Text)new TranslatableText("advancements.husbandry.breed_all_animals.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(100)).build(consumer, "husbandry/bred_all_animals");
        Advancement lv8 = this.requireListedFishCaught(Advancement.Task.create()).parent(lv).criteriaMerger(CriteriaMerger.OR).display(Items.FISHING_ROD, (Text)new TranslatableText("advancements.husbandry.fishy_business.title"), (Text)new TranslatableText("advancements.husbandry.fishy_business.description"), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/fishy_business");
        Advancement lv9 = this.requireListedFishBucketsFilled(Advancement.Task.create()).parent(lv8).criteriaMerger(CriteriaMerger.OR).display(Items.PUFFERFISH_BUCKET, (Text)new TranslatableText("advancements.husbandry.tactical_fishing.title"), (Text)new TranslatableText("advancements.husbandry.tactical_fishing.description"), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/tactical_fishing");
        Advancement lv10 = this.requireAllCatsTamed(Advancement.Task.create()).parent(lv6).display(Items.COD, (Text)new TranslatableText("advancements.husbandry.complete_catalogue.title"), (Text)new TranslatableText("advancements.husbandry.complete_catalogue.description"), null, AdvancementFrame.CHALLENGE, true, true, false).rewards(AdvancementRewards.Builder.experience(50)).build(consumer, "husbandry/complete_catalogue");
        Advancement lv11 = Advancement.Task.create().parent(lv).criterion("safely_harvest_honey", BlockUsedCriterion.Conditions.create(BlockPredicate.Builder.create().tag(BlockTags.BEEHIVES), ItemPredicate.Builder.create().item(Items.GLASS_BOTTLE))).display(Items.HONEY_BOTTLE, (Text)new TranslatableText("advancements.husbandry.safely_harvest_honey.title"), (Text)new TranslatableText("advancements.husbandry.safely_harvest_honey.description"), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/safely_harvest_honey");
        Advancement lv12 = Advancement.Task.create().parent(lv).criterion("silk_touch_nest", BeeNestDestroyedCriterion.Conditions.create(Blocks.BEE_NEST, ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1))), NumberRange.IntRange.exactly(3))).display(Blocks.BEE_NEST, (Text)new TranslatableText("advancements.husbandry.silk_touch_nest.title"), (Text)new TranslatableText("advancements.husbandry.silk_touch_nest.description"), null, AdvancementFrame.TASK, true, true, false).build(consumer, "husbandry/silk_touch_nest");
    }

    private Advancement.Task requireFoodItemsEaten(Advancement.Task arg) {
        for (Item lv : FOOD_ITEMS) {
            arg.criterion(Registry.ITEM.getId(lv).getPath(), ConsumeItemCriterion.Conditions.item(lv));
        }
        return arg;
    }

    private Advancement.Task requireListedAnimalsBred(Advancement.Task arg) {
        for (EntityType<?> lv : BREEDABLE_ANIMALS) {
            arg.criterion(EntityType.getId(lv).toString(), BredAnimalsCriterion.Conditions.create(EntityPredicate.Builder.create().type(lv)));
        }
        return arg;
    }

    private Advancement.Task requireListedFishBucketsFilled(Advancement.Task arg) {
        for (Item lv : FISH_BUCKET_ITEMS) {
            arg.criterion(Registry.ITEM.getId(lv).getPath(), FilledBucketCriterion.Conditions.create(ItemPredicate.Builder.create().item(lv).build()));
        }
        return arg;
    }

    private Advancement.Task requireListedFishCaught(Advancement.Task arg) {
        for (Item lv : FISH_ITEMS) {
            arg.criterion(Registry.ITEM.getId(lv).getPath(), FishingRodHookedCriterion.Conditions.create(ItemPredicate.ANY, EntityPredicate.ANY, ItemPredicate.Builder.create().item(lv).build()));
        }
        return arg;
    }

    private Advancement.Task requireAllCatsTamed(Advancement.Task arg) {
        CatEntity.TEXTURES.forEach((integer, arg2) -> arg.criterion(arg2.getPath(), TameAnimalCriterion.Conditions.create(EntityPredicate.Builder.create().type((Identifier)arg2).build())));
        return arg;
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}

