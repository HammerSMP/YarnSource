/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.server;

import java.util.function.Consumer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.CriteriaMerger;
import net.minecraft.advancement.criterion.ChangedDimensionCriterion;
import net.minecraft.advancement.criterion.CuredZombieVillagerCriterion;
import net.minecraft.advancement.criterion.EnchantedItemCriterion;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.LocationArrivalCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.ItemTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.StructureFeature;

public class StoryTabAdvancementGenerator
implements Consumer<Consumer<Advancement>> {
    @Override
    public void accept(Consumer<Advancement> consumer) {
        Advancement lv = Advancement.Task.create().display(Blocks.GRASS_BLOCK, (Text)new TranslatableText("advancements.story.root.title"), (Text)new TranslatableText("advancements.story.root.description"), new Identifier("textures/gui/advancements/backgrounds/stone.png"), AdvancementFrame.TASK, false, false, false).criterion("crafting_table", InventoryChangedCriterion.Conditions.items(Blocks.CRAFTING_TABLE)).build(consumer, "story/root");
        Advancement lv2 = Advancement.Task.create().parent(lv).display(Items.WOODEN_PICKAXE, (Text)new TranslatableText("advancements.story.mine_stone.title"), (Text)new TranslatableText("advancements.story.mine_stone.description"), null, AdvancementFrame.TASK, true, true, false).criterion("get_stone", InventoryChangedCriterion.Conditions.items(ItemPredicate.Builder.create().tag(ItemTags.STONE_TOOL_MATERIALS).build())).build(consumer, "story/mine_stone");
        Advancement lv3 = Advancement.Task.create().parent(lv2).display(Items.STONE_PICKAXE, (Text)new TranslatableText("advancements.story.upgrade_tools.title"), (Text)new TranslatableText("advancements.story.upgrade_tools.description"), null, AdvancementFrame.TASK, true, true, false).criterion("stone_pickaxe", InventoryChangedCriterion.Conditions.items(Items.STONE_PICKAXE)).build(consumer, "story/upgrade_tools");
        Advancement lv4 = Advancement.Task.create().parent(lv3).display(Items.IRON_INGOT, (Text)new TranslatableText("advancements.story.smelt_iron.title"), (Text)new TranslatableText("advancements.story.smelt_iron.description"), null, AdvancementFrame.TASK, true, true, false).criterion("iron", InventoryChangedCriterion.Conditions.items(Items.IRON_INGOT)).build(consumer, "story/smelt_iron");
        Advancement lv5 = Advancement.Task.create().parent(lv4).display(Items.IRON_PICKAXE, (Text)new TranslatableText("advancements.story.iron_tools.title"), (Text)new TranslatableText("advancements.story.iron_tools.description"), null, AdvancementFrame.TASK, true, true, false).criterion("iron_pickaxe", InventoryChangedCriterion.Conditions.items(Items.IRON_PICKAXE)).build(consumer, "story/iron_tools");
        Advancement lv6 = Advancement.Task.create().parent(lv5).display(Items.DIAMOND, (Text)new TranslatableText("advancements.story.mine_diamond.title"), (Text)new TranslatableText("advancements.story.mine_diamond.description"), null, AdvancementFrame.TASK, true, true, false).criterion("diamond", InventoryChangedCriterion.Conditions.items(Items.DIAMOND)).build(consumer, "story/mine_diamond");
        Advancement lv7 = Advancement.Task.create().parent(lv4).display(Items.LAVA_BUCKET, (Text)new TranslatableText("advancements.story.lava_bucket.title"), (Text)new TranslatableText("advancements.story.lava_bucket.description"), null, AdvancementFrame.TASK, true, true, false).criterion("lava_bucket", InventoryChangedCriterion.Conditions.items(Items.LAVA_BUCKET)).build(consumer, "story/lava_bucket");
        Advancement lv8 = Advancement.Task.create().parent(lv4).display(Items.IRON_CHESTPLATE, (Text)new TranslatableText("advancements.story.obtain_armor.title"), (Text)new TranslatableText("advancements.story.obtain_armor.description"), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("iron_helmet", InventoryChangedCriterion.Conditions.items(Items.IRON_HELMET)).criterion("iron_chestplate", InventoryChangedCriterion.Conditions.items(Items.IRON_CHESTPLATE)).criterion("iron_leggings", InventoryChangedCriterion.Conditions.items(Items.IRON_LEGGINGS)).criterion("iron_boots", InventoryChangedCriterion.Conditions.items(Items.IRON_BOOTS)).build(consumer, "story/obtain_armor");
        Advancement.Task.create().parent(lv6).display(Items.ENCHANTED_BOOK, (Text)new TranslatableText("advancements.story.enchant_item.title"), (Text)new TranslatableText("advancements.story.enchant_item.description"), null, AdvancementFrame.TASK, true, true, false).criterion("enchanted_item", EnchantedItemCriterion.Conditions.any()).build(consumer, "story/enchant_item");
        Advancement lv9 = Advancement.Task.create().parent(lv7).display(Blocks.OBSIDIAN, (Text)new TranslatableText("advancements.story.form_obsidian.title"), (Text)new TranslatableText("advancements.story.form_obsidian.description"), null, AdvancementFrame.TASK, true, true, false).criterion("obsidian", InventoryChangedCriterion.Conditions.items(Blocks.OBSIDIAN)).build(consumer, "story/form_obsidian");
        Advancement.Task.create().parent(lv8).display(Items.SHIELD, (Text)new TranslatableText("advancements.story.deflect_arrow.title"), (Text)new TranslatableText("advancements.story.deflect_arrow.description"), null, AdvancementFrame.TASK, true, true, false).criterion("deflected_projectile", EntityHurtPlayerCriterion.Conditions.create(DamagePredicate.Builder.create().type(DamageSourcePredicate.Builder.create().projectile(true)).blocked(true))).build(consumer, "story/deflect_arrow");
        Advancement.Task.create().parent(lv6).display(Items.DIAMOND_CHESTPLATE, (Text)new TranslatableText("advancements.story.shiny_gear.title"), (Text)new TranslatableText("advancements.story.shiny_gear.description"), null, AdvancementFrame.TASK, true, true, false).criteriaMerger(CriteriaMerger.OR).criterion("diamond_helmet", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_HELMET)).criterion("diamond_chestplate", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_CHESTPLATE)).criterion("diamond_leggings", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_LEGGINGS)).criterion("diamond_boots", InventoryChangedCriterion.Conditions.items(Items.DIAMOND_BOOTS)).build(consumer, "story/shiny_gear");
        Advancement lv10 = Advancement.Task.create().parent(lv9).display(Items.FLINT_AND_STEEL, (Text)new TranslatableText("advancements.story.enter_the_nether.title"), (Text)new TranslatableText("advancements.story.enter_the_nether.description"), null, AdvancementFrame.TASK, true, true, false).criterion("entered_nether", ChangedDimensionCriterion.Conditions.to(DimensionType.field_24754)).build(consumer, "story/enter_the_nether");
        Advancement.Task.create().parent(lv10).display(Items.GOLDEN_APPLE, (Text)new TranslatableText("advancements.story.cure_zombie_villager.title"), (Text)new TranslatableText("advancements.story.cure_zombie_villager.description"), null, AdvancementFrame.GOAL, true, true, false).criterion("cured_zombie", CuredZombieVillagerCriterion.Conditions.any()).build(consumer, "story/cure_zombie_villager");
        Advancement lv11 = Advancement.Task.create().parent(lv10).display(Items.ENDER_EYE, (Text)new TranslatableText("advancements.story.follow_ender_eye.title"), (Text)new TranslatableText("advancements.story.follow_ender_eye.description"), null, AdvancementFrame.TASK, true, true, false).criterion("in_stronghold", LocationArrivalCriterion.Conditions.create(LocationPredicate.feature(StructureFeature.STRONGHOLD))).build(consumer, "story/follow_ender_eye");
        Advancement.Task.create().parent(lv11).display(Blocks.END_STONE, (Text)new TranslatableText("advancements.story.enter_the_end.title"), (Text)new TranslatableText("advancements.story.enter_the_end.description"), null, AdvancementFrame.TASK, true, true, false).criterion("entered_end", ChangedDimensionCriterion.Conditions.to(DimensionType.field_24755)).build(consumer, "story/enter_the_end");
    }

    @Override
    public /* synthetic */ void accept(Object object) {
        this.accept((Consumer)object);
    }
}

