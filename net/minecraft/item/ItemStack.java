/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Multimap
 *  com.google.gson.JsonParseException
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.Stats;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagManager;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {
    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Registry.ITEM.fieldOf("id").forGetter(arg -> arg.item), (App)Codec.INT.fieldOf("Count").forGetter(arg -> arg.count), (App)CompoundTag.field_25128.optionalFieldOf("tag").forGetter(arg -> Optional.ofNullable(arg.tag))).apply((Applicative)instance, ItemStack::new));
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ItemStack EMPTY = new ItemStack((ItemConvertible)null);
    public static final DecimalFormat MODIFIER_FORMAT = Util.make(new DecimalFormat("#.##"), decimalFormat -> decimalFormat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT)));
    private static final Style field_24092 = Style.EMPTY.withColor(Formatting.DARK_PURPLE).withItalic(true);
    private int count;
    private int cooldown;
    @Deprecated
    private final Item item;
    private CompoundTag tag;
    private boolean empty;
    private Entity holder;
    private CachedBlockPosition lastDestroyPos;
    private boolean lastDestroyResult;
    private CachedBlockPosition lastPlaceOnPos;
    private boolean lastPlaceOnResult;

    public ItemStack(ItemConvertible item) {
        this(item, 1);
    }

    private ItemStack(ItemConvertible arg, int i, Optional<CompoundTag> optional) {
        this(arg, i);
        optional.ifPresent(this::setTag);
    }

    public ItemStack(ItemConvertible item, int count) {
        this.item = item == null ? null : item.asItem();
        this.count = count;
        if (this.item != null && this.item.isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    private void updateEmptyState() {
        this.empty = false;
        this.empty = this.isEmpty();
    }

    private ItemStack(CompoundTag tag) {
        this.item = Registry.ITEM.get(new Identifier(tag.getString("id")));
        this.count = tag.getByte("Count");
        if (tag.contains("tag", 10)) {
            this.tag = tag.getCompound("tag");
            this.getItem().postProcessTag(tag);
        }
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    public static ItemStack fromTag(CompoundTag tag) {
        try {
            return new ItemStack(tag);
        }
        catch (RuntimeException runtimeException) {
            LOGGER.debug("Tried to load invalid item: {}", (Object)tag, (Object)runtimeException);
            return EMPTY;
        }
    }

    public boolean isEmpty() {
        if (this == EMPTY) {
            return true;
        }
        if (this.getItem() == null || this.getItem() == Items.AIR) {
            return true;
        }
        return this.count <= 0;
    }

    public ItemStack split(int amount) {
        int j = Math.min(amount, this.count);
        ItemStack lv = this.copy();
        lv.setCount(j);
        this.decrement(j);
        return lv;
    }

    public Item getItem() {
        return this.empty ? Items.AIR : this.item;
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity lv = context.getPlayer();
        BlockPos lv2 = context.getBlockPos();
        CachedBlockPosition lv3 = new CachedBlockPosition(context.getWorld(), lv2, false);
        if (lv != null && !lv.abilities.allowModifyWorld && !this.canPlaceOn(context.getWorld().getTagManager(), lv3)) {
            return ActionResult.PASS;
        }
        Item lv4 = this.getItem();
        ActionResult lv5 = lv4.useOnBlock(context);
        if (lv != null && lv5.isAccepted()) {
            lv.incrementStat(Stats.USED.getOrCreateStat(lv4));
        }
        return lv5;
    }

    public float getMiningSpeedMultiplier(BlockState state) {
        return this.getItem().getMiningSpeedMultiplier(this, state);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.getItem().use(world, user, hand);
    }

    public ItemStack finishUsing(World world, LivingEntity user) {
        return this.getItem().finishUsing(this, world, user);
    }

    public CompoundTag toTag(CompoundTag tag) {
        Identifier lv = Registry.ITEM.getId(this.getItem());
        tag.putString("id", lv == null ? "minecraft:air" : lv.toString());
        tag.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            tag.put("tag", this.tag.copy());
        }
        return tag;
    }

    public int getMaxCount() {
        return this.getItem().getMaxCount();
    }

    public boolean isStackable() {
        return this.getMaxCount() > 1 && (!this.isDamageable() || !this.isDamaged());
    }

    public boolean isDamageable() {
        if (this.empty || this.getItem().getMaxDamage() <= 0) {
            return false;
        }
        CompoundTag lv = this.getTag();
        return lv == null || !lv.getBoolean("Unbreakable");
    }

    public boolean isDamaged() {
        return this.isDamageable() && this.getDamage() > 0;
    }

    public int getDamage() {
        return this.tag == null ? 0 : this.tag.getInt("Damage");
    }

    public void setDamage(int damage) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, damage));
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public boolean damage(int amount, Random random, @Nullable ServerPlayerEntity player) {
        if (!this.isDamageable()) {
            return false;
        }
        if (amount > 0) {
            int j = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
            int k = 0;
            for (int l = 0; j > 0 && l < amount; ++l) {
                if (!UnbreakingEnchantment.shouldPreventDamage(this, j, random)) continue;
                ++k;
            }
            if ((amount -= k) <= 0) {
                return false;
            }
        }
        if (player != null && amount != 0) {
            Criteria.ITEM_DURABILITY_CHANGED.trigger(player, this, this.getDamage() + amount);
        }
        int m = this.getDamage() + amount;
        this.setDamage(m);
        return m >= this.getMaxDamage();
    }

    public <T extends LivingEntity> void damage(int amount, T entity, Consumer<T> breakCallback) {
        if (entity.world.isClient || entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode) {
            return;
        }
        if (!this.isDamageable()) {
            return;
        }
        if (this.damage(amount, entity.getRandom(), entity instanceof ServerPlayerEntity ? (ServerPlayerEntity)entity : null)) {
            breakCallback.accept(entity);
            Item lv = this.getItem();
            this.decrement(1);
            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).incrementStat(Stats.BROKEN.getOrCreateStat(lv));
            }
            this.setDamage(0);
        }
    }

    public void postHit(LivingEntity target, PlayerEntity attacker) {
        Item lv = this.getItem();
        if (lv.postHit(this, target, attacker)) {
            attacker.incrementStat(Stats.USED.getOrCreateStat(lv));
        }
    }

    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {
        Item lv = this.getItem();
        if (lv.postMine(this, world, state, pos, miner)) {
            miner.incrementStat(Stats.USED.getOrCreateStat(lv));
        }
    }

    public boolean isEffectiveOn(BlockState state) {
        return this.getItem().isEffectiveOn(state);
    }

    public ActionResult useOnEntity(PlayerEntity user, LivingEntity entity, Hand hand) {
        return this.getItem().useOnEntity(this, user, entity, hand);
    }

    public ItemStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        }
        ItemStack lv = new ItemStack(this.getItem(), this.count);
        lv.setCooldown(this.getCooldown());
        if (this.tag != null) {
            lv.tag = this.tag.copy();
        }
        return lv;
    }

    public static boolean areTagsEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (left.tag == null && right.tag != null) {
            return false;
        }
        return left.tag == null || left.tag.equals(right.tag);
    }

    public static boolean areEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        return left.isEqual(right);
    }

    private boolean isEqual(ItemStack stack) {
        if (this.count != stack.count) {
            return false;
        }
        if (this.getItem() != stack.getItem()) {
            return false;
        }
        if (this.tag == null && stack.tag != null) {
            return false;
        }
        return this.tag == null || this.tag.equals(stack.tag);
    }

    public static boolean areItemsEqualIgnoreDamage(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        }
        if (!left.isEmpty() && !right.isEmpty()) {
            return left.isItemEqualIgnoreDamage(right);
        }
        return false;
    }

    public static boolean areItemsEqual(ItemStack left, ItemStack right) {
        if (left == right) {
            return true;
        }
        if (!left.isEmpty() && !right.isEmpty()) {
            return left.isItemEqual(right);
        }
        return false;
    }

    public boolean isItemEqualIgnoreDamage(ItemStack stack) {
        return !stack.isEmpty() && this.getItem() == stack.getItem();
    }

    public boolean isItemEqual(ItemStack stack) {
        if (this.isDamageable()) {
            return !stack.isEmpty() && this.getItem() == stack.getItem();
        }
        return this.isItemEqualIgnoreDamage(stack);
    }

    public String getTranslationKey() {
        return this.getItem().getTranslationKey(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(World world, Entity entity, int slot, boolean selected) {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, world, entity, slot, selected);
        }
    }

    public void onCraft(World world, PlayerEntity player, int amount) {
        player.increaseStat(Stats.CRAFTED.getOrCreateStat(this.getItem()), amount);
        this.getItem().onCraft(this, world, player);
    }

    public int getMaxUseTime() {
        return this.getItem().getMaxUseTime(this);
    }

    public UseAction getUseAction() {
        return this.getItem().getUseAction(this);
    }

    public void onStoppedUsing(World world, LivingEntity user, int remainingUseTicks) {
        this.getItem().onStoppedUsing(this, world, user, remainingUseTicks);
    }

    public boolean isUsedOnRelease() {
        return this.getItem().isUsedOnRelease(this);
    }

    public boolean hasTag() {
        return !this.empty && this.tag != null && !this.tag.isEmpty();
    }

    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }

    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
        }
        return this.tag;
    }

    public CompoundTag getOrCreateSubTag(String key) {
        if (this.tag == null || !this.tag.contains(key, 10)) {
            CompoundTag lv = new CompoundTag();
            this.putSubTag(key, lv);
            return lv;
        }
        return this.tag.getCompound(key);
    }

    @Nullable
    public CompoundTag getSubTag(String key) {
        if (this.tag == null || !this.tag.contains(key, 10)) {
            return null;
        }
        return this.tag.getCompound(key);
    }

    public void removeSubTag(String key) {
        if (this.tag != null && this.tag.contains(key)) {
            this.tag.remove(key);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }
    }

    public ListTag getEnchantments() {
        if (this.tag != null) {
            return this.tag.getList("Enchantments", 10);
        }
        return new ListTag();
    }

    public void setTag(@Nullable CompoundTag tag) {
        this.tag = tag;
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
    }

    public Text getName() {
        CompoundTag lv = this.getSubTag("display");
        if (lv != null && lv.contains("Name", 8)) {
            try {
                MutableText lv2 = Text.Serializer.fromJson(lv.getString("Name"));
                if (lv2 != null) {
                    return lv2;
                }
                lv.remove("Name");
            }
            catch (JsonParseException jsonParseException) {
                lv.remove("Name");
            }
        }
        return this.getItem().getName(this);
    }

    public ItemStack setCustomName(@Nullable Text name) {
        CompoundTag lv = this.getOrCreateSubTag("display");
        if (name != null) {
            lv.putString("Name", Text.Serializer.toJson(name));
        } else {
            lv.remove("Name");
        }
        return this;
    }

    public void removeCustomName() {
        CompoundTag lv = this.getSubTag("display");
        if (lv != null) {
            lv.remove("Name");
            if (lv.isEmpty()) {
                this.removeSubTag("display");
            }
        }
        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }
    }

    public boolean hasCustomName() {
        CompoundTag lv = this.getSubTag("display");
        return lv != null && lv.contains("Name", 8);
    }

    @Environment(value=EnvType.CLIENT)
    public List<Text> getTooltip(@Nullable PlayerEntity player, TooltipContext context) {
        int i;
        ArrayList list = Lists.newArrayList();
        MutableText lv = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            lv.formatted(Formatting.ITALIC);
        }
        list.add(lv);
        if (!context.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
            list.add(new LiteralText("#" + FilledMapItem.getMapId(this)).formatted(Formatting.GRAY));
        }
        if (ItemStack.method_30267(i = this.method_30266(), class_5422.field_25773)) {
            this.getItem().appendTooltip(this, player == null ? null : player.world, list, context);
        }
        if (this.hasTag()) {
            if (ItemStack.method_30267(i, class_5422.field_25768)) {
                ItemStack.appendEnchantments(list, this.getEnchantments());
            }
            if (this.tag.contains("display", 10)) {
                CompoundTag lv2 = this.tag.getCompound("display");
                if (ItemStack.method_30267(i, class_5422.field_25774) && lv2.contains("color", 99)) {
                    if (context.isAdvanced()) {
                        list.add(new TranslatableText("item.color", String.format("#%06X", lv2.getInt("color"))).formatted(Formatting.GRAY));
                    } else {
                        list.add(new TranslatableText("item.dyed").formatted(Formatting.GRAY, Formatting.ITALIC));
                    }
                }
                if (lv2.getType("Lore") == 9) {
                    ListTag lv3 = lv2.getList("Lore", 8);
                    for (int j = 0; j < lv3.size(); ++j) {
                        String string = lv3.getString(j);
                        try {
                            MutableText lv4 = Text.Serializer.fromJson(string);
                            if (lv4 == null) continue;
                            list.add(Texts.setStyleIfAbsent(lv4, field_24092));
                            continue;
                        }
                        catch (JsonParseException jsonParseException) {
                            lv2.remove("Lore");
                        }
                    }
                }
            }
        }
        if (ItemStack.method_30267(i, class_5422.field_25769)) {
            for (EquipmentSlot lv5 : EquipmentSlot.values()) {
                Multimap<EntityAttribute, EntityAttributeModifier> multimap = this.getAttributeModifiers(lv5);
                if (multimap.isEmpty()) continue;
                list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.modifiers." + lv5.getName()).formatted(Formatting.GRAY));
                for (Map.Entry entry : multimap.entries()) {
                    double g;
                    EntityAttributeModifier lv6 = (EntityAttributeModifier)entry.getValue();
                    double d = lv6.getValue();
                    boolean bl = false;
                    if (player != null) {
                        if (lv6.getId() == Item.ATTACK_DAMAGE_MODIFIER_ID) {
                            d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                            d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
                            bl = true;
                        } else if (lv6.getId() == Item.ATTACK_SPEED_MODIFIER_ID) {
                            d += player.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
                            bl = true;
                        }
                    }
                    if (lv6.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_BASE || lv6.getOperation() == EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                        double e = d * 100.0;
                    } else if (((EntityAttribute)entry.getKey()).equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
                        double f = d * 10.0;
                    } else {
                        g = d;
                    }
                    if (bl) {
                        list.add(new LiteralText(" ").append(new TranslatableText("attribute.modifier.equals." + lv6.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey()))).formatted(Formatting.DARK_GREEN));
                        continue;
                    }
                    if (d > 0.0) {
                        list.add(new TranslatableText("attribute.modifier.plus." + lv6.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())).formatted(Formatting.BLUE));
                        continue;
                    }
                    if (!(d < 0.0)) continue;
                    list.add(new TranslatableText("attribute.modifier.take." + lv6.getOperation().getId(), MODIFIER_FORMAT.format(g *= -1.0), new TranslatableText(((EntityAttribute)entry.getKey()).getTranslationKey())).formatted(Formatting.RED));
                }
            }
        }
        if (this.hasTag()) {
            ListTag lv8;
            ListTag lv7;
            if (ItemStack.method_30267(i, class_5422.field_25770) && this.tag.getBoolean("Unbreakable")) {
                list.add(new TranslatableText("item.unbreakable").formatted(Formatting.BLUE));
            }
            if (ItemStack.method_30267(i, class_5422.field_25771) && this.tag.contains("CanDestroy", 9) && !(lv7 = this.tag.getList("CanDestroy", 8)).isEmpty()) {
                list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.canBreak").formatted(Formatting.GRAY));
                for (int k = 0; k < lv7.size(); ++k) {
                    list.addAll(ItemStack.parseBlockTag(lv7.getString(k)));
                }
            }
            if (ItemStack.method_30267(i, class_5422.field_25772) && this.tag.contains("CanPlaceOn", 9) && !(lv8 = this.tag.getList("CanPlaceOn", 8)).isEmpty()) {
                list.add(LiteralText.EMPTY);
                list.add(new TranslatableText("item.canPlace").formatted(Formatting.GRAY));
                for (int l = 0; l < lv8.size(); ++l) {
                    list.addAll(ItemStack.parseBlockTag(lv8.getString(l)));
                }
            }
        }
        if (context.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslatableText("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
            }
            list.add(new LiteralText(Registry.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
            if (this.hasTag()) {
                list.add(new TranslatableText("item.nbt_tags", this.tag.getKeys().size()).formatted(Formatting.DARK_GRAY));
            }
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    private static boolean method_30267(int i, class_5422 arg) {
        return (i & arg.method_30269()) == 0;
    }

    @Environment(value=EnvType.CLIENT)
    private int method_30266() {
        if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
            return this.tag.getInt("HideFlags");
        }
        return 0;
    }

    public void method_30268(class_5422 arg) {
        CompoundTag lv = this.getOrCreateTag();
        lv.putInt("HideFlags", lv.getInt("HideFlags") | arg.method_30269());
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendEnchantments(List<Text> tooltip, ListTag enchantments) {
        for (int i = 0; i < enchantments.size(); ++i) {
            CompoundTag lv = enchantments.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(lv.getString("id"))).ifPresent(e -> tooltip.add(e.getName(lv.getInt("lvl"))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Collection<Text> parseBlockTag(String tag) {
        try {
            boolean bl2;
            BlockArgumentParser lv = new BlockArgumentParser(new StringReader(tag), true).parse(true);
            BlockState lv2 = lv.getBlockState();
            Identifier lv3 = lv.getTagId();
            boolean bl = lv2 != null;
            boolean bl3 = bl2 = lv3 != null;
            if (bl || bl2) {
                List<Block> collection;
                if (bl) {
                    return Lists.newArrayList((Object[])new Text[]{lv2.getBlock().getName().formatted(Formatting.DARK_GRAY)});
                }
                net.minecraft.tag.Tag<Block> lv4 = BlockTags.getTagGroup().getTag(lv3);
                if (lv4 != null && !(collection = lv4.values()).isEmpty()) {
                    return collection.stream().map(Block::getName).map(text -> text.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList());
                }
            }
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return Lists.newArrayList((Object[])new Text[]{new LiteralText("missingno").formatted(Formatting.DARK_GRAY)});
    }

    public boolean hasGlint() {
        return this.getItem().hasGlint(this);
    }

    public Rarity getRarity() {
        return this.getItem().getRarity(this);
    }

    public boolean isEnchantable() {
        if (!this.getItem().isEnchantable(this)) {
            return false;
        }
        return !this.hasEnchantments();
    }

    public void addEnchantment(Enchantment enchantment, int level) {
        this.getOrCreateTag();
        if (!this.tag.contains("Enchantments", 9)) {
            this.tag.put("Enchantments", new ListTag());
        }
        ListTag lv = this.tag.getList("Enchantments", 10);
        CompoundTag lv2 = new CompoundTag();
        lv2.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
        lv2.putShort("lvl", (byte)level);
        lv.add(lv2);
    }

    public boolean hasEnchantments() {
        if (this.tag != null && this.tag.contains("Enchantments", 9)) {
            return !this.tag.getList("Enchantments", 10).isEmpty();
        }
        return false;
    }

    public void putSubTag(String key, Tag tag) {
        this.getOrCreateTag().put(key, tag);
    }

    public boolean isInFrame() {
        return this.holder instanceof ItemFrameEntity;
    }

    public void setHolder(@Nullable Entity holder) {
        this.holder = holder;
    }

    @Nullable
    public ItemFrameEntity getFrame() {
        return this.holder instanceof ItemFrameEntity ? (ItemFrameEntity)this.getHolder() : null;
    }

    @Nullable
    public Entity getHolder() {
        return !this.empty ? this.holder : null;
    }

    public int getRepairCost() {
        if (this.hasTag() && this.tag.contains("RepairCost", 3)) {
            return this.tag.getInt("RepairCost");
        }
        return 0;
    }

    public void setRepairCost(int repairCost) {
        this.getOrCreateTag().putInt("RepairCost", repairCost);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot arg) {
        Multimap<EntityAttribute, EntityAttributeModifier> multimap2;
        if (this.hasTag() && this.tag.contains("AttributeModifiers", 9)) {
            HashMultimap multimap = HashMultimap.create();
            ListTag lv = this.tag.getList("AttributeModifiers", 10);
            for (int i = 0; i < lv.size(); ++i) {
                EntityAttributeModifier lv3;
                Optional<EntityAttribute> optional;
                CompoundTag lv2 = lv.getCompound(i);
                if (lv2.contains("Slot", 8) && !lv2.getString("Slot").equals(arg.getName()) || !(optional = Registry.ATTRIBUTE.getOrEmpty(Identifier.tryParse(lv2.getString("AttributeName")))).isPresent() || (lv3 = EntityAttributeModifier.fromTag(lv2)) == null || lv3.getId().getLeastSignificantBits() == 0L || lv3.getId().getMostSignificantBits() == 0L) continue;
                multimap.put((Object)optional.get(), (Object)lv3);
            }
        } else {
            multimap2 = this.getItem().getAttributeModifiers(arg);
        }
        return multimap2;
    }

    public void addAttributeModifier(EntityAttribute arg, EntityAttributeModifier modifier, @Nullable EquipmentSlot slot) {
        this.getOrCreateTag();
        if (!this.tag.contains("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new ListTag());
        }
        ListTag lv = this.tag.getList("AttributeModifiers", 10);
        CompoundTag lv2 = modifier.toTag();
        lv2.putString("AttributeName", Registry.ATTRIBUTE.getId(arg).toString());
        if (slot != null) {
            lv2.putString("Slot", slot.getName());
        }
        lv.add(lv2);
    }

    public Text toHoverableText() {
        MutableText lv = new LiteralText("").append(this.getName());
        if (this.hasCustomName()) {
            lv.formatted(Formatting.ITALIC);
        }
        MutableText lv2 = Texts.bracketed(lv);
        if (!this.empty) {
            lv2.formatted(this.getRarity().formatting).styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(this))));
        }
        return lv2;
    }

    private static boolean areBlocksEqual(CachedBlockPosition first, @Nullable CachedBlockPosition second) {
        if (second == null || first.getBlockState() != second.getBlockState()) {
            return false;
        }
        if (first.getBlockEntity() == null && second.getBlockEntity() == null) {
            return true;
        }
        if (first.getBlockEntity() == null || second.getBlockEntity() == null) {
            return false;
        }
        return Objects.equals(first.getBlockEntity().toTag(new CompoundTag()), second.getBlockEntity().toTag(new CompoundTag()));
    }

    public boolean canDestroy(TagManager arg, CachedBlockPosition pos) {
        if (ItemStack.areBlocksEqual(pos, this.lastDestroyPos)) {
            return this.lastDestroyResult;
        }
        this.lastDestroyPos = pos;
        if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListTag lv = this.tag.getList("CanDestroy", 8);
            for (int i = 0; i < lv.size(); ++i) {
                String string = lv.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(arg);
                    if (predicate.test(pos)) {
                        this.lastDestroyResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastDestroyResult = false;
        return false;
    }

    public boolean canPlaceOn(TagManager arg, CachedBlockPosition pos) {
        if (ItemStack.areBlocksEqual(pos, this.lastPlaceOnPos)) {
            return this.lastPlaceOnResult;
        }
        this.lastPlaceOnPos = pos;
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListTag lv = this.tag.getList("CanPlaceOn", 8);
            for (int i = 0; i < lv.size(); ++i) {
                String string = lv.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(arg);
                    if (predicate.test(pos)) {
                        this.lastPlaceOnResult = true;
                        return true;
                    }
                    continue;
                }
                catch (CommandSyntaxException commandSyntaxException) {
                    // empty catch block
                }
            }
        }
        this.lastPlaceOnResult = false;
        return false;
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCount() {
        return this.empty ? 0 : this.count;
    }

    public void setCount(int count) {
        this.count = count;
        this.updateEmptyState();
    }

    public void increment(int amount) {
        this.setCount(this.count + amount);
    }

    public void decrement(int amount) {
        this.increment(-amount);
    }

    public void usageTick(World world, LivingEntity user, int remainingUseTicks) {
        this.getItem().usageTick(world, user, this, remainingUseTicks);
    }

    public boolean isFood() {
        return this.getItem().isFood();
    }

    public SoundEvent getDrinkSound() {
        return this.getItem().getDrinkSound();
    }

    public SoundEvent getEatSound() {
        return this.getItem().getEatSound();
    }

    public static enum class_5422 {
        field_25768,
        field_25769,
        field_25770,
        field_25771,
        field_25772,
        field_25773,
        field_25774;

        private int field_25775 = 1 << this.ordinal();

        public int method_30269() {
            return this.field_25775;
        }
    }
}

