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
import net.minecraft.tag.RegistryTagManager;
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

    public ItemStack(ItemConvertible arg) {
        this(arg, 1);
    }

    private ItemStack(ItemConvertible arg, int i, Optional<CompoundTag> optional) {
        this(arg, i);
        optional.ifPresent(this::setTag);
    }

    public ItemStack(ItemConvertible arg, int i) {
        this.item = arg == null ? null : arg.asItem();
        this.count = i;
        if (this.item != null && this.item.isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    private void updateEmptyState() {
        this.empty = false;
        this.empty = this.isEmpty();
    }

    private ItemStack(CompoundTag arg) {
        this.item = Registry.ITEM.get(new Identifier(arg.getString("id")));
        this.count = arg.getByte("Count");
        if (arg.contains("tag", 10)) {
            this.tag = arg.getCompound("tag");
            this.getItem().postProcessTag(arg);
        }
        if (this.getItem().isDamageable()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyState();
    }

    public static ItemStack fromTag(CompoundTag arg) {
        try {
            return new ItemStack(arg);
        }
        catch (RuntimeException runtimeException) {
            LOGGER.debug("Tried to load invalid item: {}", (Object)arg, (Object)runtimeException);
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

    public ItemStack split(int i) {
        int j = Math.min(i, this.count);
        ItemStack lv = this.copy();
        lv.setCount(j);
        this.decrement(j);
        return lv;
    }

    public Item getItem() {
        return this.empty ? Items.AIR : this.item;
    }

    public ActionResult useOnBlock(ItemUsageContext arg) {
        PlayerEntity lv = arg.getPlayer();
        BlockPos lv2 = arg.getBlockPos();
        CachedBlockPosition lv3 = new CachedBlockPosition(arg.getWorld(), lv2, false);
        if (lv != null && !lv.abilities.allowModifyWorld && !this.canPlaceOn(arg.getWorld().getTagManager(), lv3)) {
            return ActionResult.PASS;
        }
        Item lv4 = this.getItem();
        ActionResult lv5 = lv4.useOnBlock(arg);
        if (lv != null && lv5.isAccepted()) {
            lv.incrementStat(Stats.USED.getOrCreateStat(lv4));
        }
        return lv5;
    }

    public float getMiningSpeedMultiplier(BlockState arg) {
        return this.getItem().getMiningSpeedMultiplier(this, arg);
    }

    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        return this.getItem().use(arg, arg2, arg3);
    }

    public ItemStack finishUsing(World arg, LivingEntity arg2) {
        return this.getItem().finishUsing(this, arg, arg2);
    }

    public CompoundTag toTag(CompoundTag arg) {
        Identifier lv = Registry.ITEM.getId(this.getItem());
        arg.putString("id", lv == null ? "minecraft:air" : lv.toString());
        arg.putByte("Count", (byte)this.count);
        if (this.tag != null) {
            arg.put("tag", this.tag.copy());
        }
        return arg;
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

    public void setDamage(int i) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, i));
    }

    public int getMaxDamage() {
        return this.getItem().getMaxDamage();
    }

    public boolean damage(int i, Random random, @Nullable ServerPlayerEntity arg) {
        if (!this.isDamageable()) {
            return false;
        }
        if (i > 0) {
            int j = EnchantmentHelper.getLevel(Enchantments.UNBREAKING, this);
            int k = 0;
            for (int l = 0; j > 0 && l < i; ++l) {
                if (!UnbreakingEnchantment.shouldPreventDamage(this, j, random)) continue;
                ++k;
            }
            if ((i -= k) <= 0) {
                return false;
            }
        }
        if (arg != null && i != 0) {
            Criteria.ITEM_DURABILITY_CHANGED.trigger(arg, this, this.getDamage() + i);
        }
        int m = this.getDamage() + i;
        this.setDamage(m);
        return m >= this.getMaxDamage();
    }

    public <T extends LivingEntity> void damage(int i, T arg, Consumer<T> consumer) {
        if (arg.world.isClient || arg instanceof PlayerEntity && ((PlayerEntity)arg).abilities.creativeMode) {
            return;
        }
        if (!this.isDamageable()) {
            return;
        }
        if (this.damage(i, arg.getRandom(), arg instanceof ServerPlayerEntity ? (ServerPlayerEntity)arg : null)) {
            consumer.accept(arg);
            Item lv = this.getItem();
            this.decrement(1);
            if (arg instanceof PlayerEntity) {
                ((PlayerEntity)arg).incrementStat(Stats.BROKEN.getOrCreateStat(lv));
            }
            this.setDamage(0);
        }
    }

    public void postHit(LivingEntity arg, PlayerEntity arg2) {
        Item lv = this.getItem();
        if (lv.postHit(this, arg, arg2)) {
            arg2.incrementStat(Stats.USED.getOrCreateStat(lv));
        }
    }

    public void postMine(World arg, BlockState arg2, BlockPos arg3, PlayerEntity arg4) {
        Item lv = this.getItem();
        if (lv.postMine(this, arg, arg2, arg3, arg4)) {
            arg4.incrementStat(Stats.USED.getOrCreateStat(lv));
        }
    }

    public boolean isEffectiveOn(BlockState arg) {
        return this.getItem().isEffectiveOn(arg);
    }

    public ActionResult useOnEntity(PlayerEntity arg, LivingEntity arg2, Hand arg3) {
        return this.getItem().useOnEntity(this, arg, arg2, arg3);
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

    public static boolean areTagsEqual(ItemStack arg, ItemStack arg2) {
        if (arg.isEmpty() && arg2.isEmpty()) {
            return true;
        }
        if (arg.isEmpty() || arg2.isEmpty()) {
            return false;
        }
        if (arg.tag == null && arg2.tag != null) {
            return false;
        }
        return arg.tag == null || arg.tag.equals(arg2.tag);
    }

    public static boolean areEqual(ItemStack arg, ItemStack arg2) {
        if (arg.isEmpty() && arg2.isEmpty()) {
            return true;
        }
        if (arg.isEmpty() || arg2.isEmpty()) {
            return false;
        }
        return arg.isEqual(arg2);
    }

    private boolean isEqual(ItemStack arg) {
        if (this.count != arg.count) {
            return false;
        }
        if (this.getItem() != arg.getItem()) {
            return false;
        }
        if (this.tag == null && arg.tag != null) {
            return false;
        }
        return this.tag == null || this.tag.equals(arg.tag);
    }

    public static boolean areItemsEqualIgnoreDamage(ItemStack arg, ItemStack arg2) {
        if (arg == arg2) {
            return true;
        }
        if (!arg.isEmpty() && !arg2.isEmpty()) {
            return arg.isItemEqualIgnoreDamage(arg2);
        }
        return false;
    }

    public static boolean areItemsEqual(ItemStack arg, ItemStack arg2) {
        if (arg == arg2) {
            return true;
        }
        if (!arg.isEmpty() && !arg2.isEmpty()) {
            return arg.isItemEqual(arg2);
        }
        return false;
    }

    public boolean isItemEqualIgnoreDamage(ItemStack arg) {
        return !arg.isEmpty() && this.getItem() == arg.getItem();
    }

    public boolean isItemEqual(ItemStack arg) {
        if (this.isDamageable()) {
            return !arg.isEmpty() && this.getItem() == arg.getItem();
        }
        return this.isItemEqualIgnoreDamage(arg);
    }

    public String getTranslationKey() {
        return this.getItem().getTranslationKey(this);
    }

    public String toString() {
        return this.count + " " + this.getItem();
    }

    public void inventoryTick(World arg, Entity arg2, int i, boolean bl) {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        if (this.getItem() != null) {
            this.getItem().inventoryTick(this, arg, arg2, i, bl);
        }
    }

    public void onCraft(World arg, PlayerEntity arg2, int i) {
        arg2.increaseStat(Stats.CRAFTED.getOrCreateStat(this.getItem()), i);
        this.getItem().onCraft(this, arg, arg2);
    }

    public int getMaxUseTime() {
        return this.getItem().getMaxUseTime(this);
    }

    public UseAction getUseAction() {
        return this.getItem().getUseAction(this);
    }

    public void onStoppedUsing(World arg, LivingEntity arg2, int i) {
        this.getItem().onStoppedUsing(this, arg, arg2, i);
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

    public CompoundTag getOrCreateSubTag(String string) {
        if (this.tag == null || !this.tag.contains(string, 10)) {
            CompoundTag lv = new CompoundTag();
            this.putSubTag(string, lv);
            return lv;
        }
        return this.tag.getCompound(string);
    }

    @Nullable
    public CompoundTag getSubTag(String string) {
        if (this.tag == null || !this.tag.contains(string, 10)) {
            return null;
        }
        return this.tag.getCompound(string);
    }

    public void removeSubTag(String string) {
        if (this.tag != null && this.tag.contains(string)) {
            this.tag.remove(string);
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

    public void setTag(@Nullable CompoundTag arg) {
        this.tag = arg;
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

    public ItemStack setCustomName(@Nullable Text arg) {
        CompoundTag lv = this.getOrCreateSubTag("display");
        if (arg != null) {
            lv.putString("Name", Text.Serializer.toJson(arg));
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
    public List<Text> getTooltip(@Nullable PlayerEntity arg, TooltipContext arg2) {
        ListTag lv8;
        ListTag lv7;
        ArrayList list = Lists.newArrayList();
        MutableText lv = new LiteralText("").append(this.getName()).formatted(this.getRarity().formatting);
        if (this.hasCustomName()) {
            lv.formatted(Formatting.ITALIC);
        }
        list.add(lv);
        if (!arg2.isAdvanced() && !this.hasCustomName() && this.getItem() == Items.FILLED_MAP) {
            list.add(new LiteralText("#" + FilledMapItem.getMapId(this)).formatted(Formatting.GRAY));
        }
        int i = 0;
        if (this.hasTag() && this.tag.contains("HideFlags", 99)) {
            i = this.tag.getInt("HideFlags");
        }
        if ((i & 0x20) == 0) {
            this.getItem().appendTooltip(this, arg == null ? null : arg.world, list, arg2);
        }
        if (this.hasTag()) {
            if ((i & 1) == 0) {
                ItemStack.appendEnchantments(list, this.getEnchantments());
            }
            if (this.tag.contains("display", 10)) {
                CompoundTag lv2 = this.tag.getCompound("display");
                if (lv2.contains("color", 3)) {
                    if (arg2.isAdvanced()) {
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
        for (EquipmentSlot lv5 : EquipmentSlot.values()) {
            Multimap<EntityAttribute, EntityAttributeModifier> multimap = this.getAttributeModifiers(lv5);
            if (multimap.isEmpty() || (i & 2) != 0) continue;
            list.add(LiteralText.EMPTY);
            list.add(new TranslatableText("item.modifiers." + lv5.getName()).formatted(Formatting.GRAY));
            for (Map.Entry entry : multimap.entries()) {
                double g;
                EntityAttributeModifier lv6 = (EntityAttributeModifier)entry.getValue();
                double d = lv6.getValue();
                boolean bl = false;
                if (arg != null) {
                    if (lv6.getId() == Item.ATTACK_DAMAGE_MODIFIER_ID) {
                        d += arg.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                        d += (double)EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
                        bl = true;
                    } else if (lv6.getId() == Item.ATTACK_SPEED_MODIFIER_ID) {
                        d += arg.getAttributeBaseValue(EntityAttributes.GENERIC_ATTACK_SPEED);
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
        if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (i & 4) == 0) {
            list.add(new TranslatableText("item.unbreakable").formatted(Formatting.BLUE));
        }
        if (this.hasTag() && this.tag.contains("CanDestroy", 9) && (i & 8) == 0 && !(lv7 = this.tag.getList("CanDestroy", 8)).isEmpty()) {
            list.add(LiteralText.EMPTY);
            list.add(new TranslatableText("item.canBreak").formatted(Formatting.GRAY));
            for (int k = 0; k < lv7.size(); ++k) {
                list.addAll(ItemStack.parseBlockTag(lv7.getString(k)));
            }
        }
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9) && (i & 0x10) == 0 && !(lv8 = this.tag.getList("CanPlaceOn", 8)).isEmpty()) {
            list.add(LiteralText.EMPTY);
            list.add(new TranslatableText("item.canPlace").formatted(Formatting.GRAY));
            for (int l = 0; l < lv8.size(); ++l) {
                list.addAll(ItemStack.parseBlockTag(lv8.getString(l)));
            }
        }
        if (arg2.isAdvanced()) {
            if (this.isDamaged()) {
                list.add(new TranslatableText("item.durability", this.getMaxDamage() - this.getDamage(), this.getMaxDamage()));
            }
            list.add(new LiteralText(Registry.ITEM.getId(this.getItem()).toString()).formatted(Formatting.DARK_GRAY));
            if (this.hasTag()) {
                list.add(new TranslatableText("item.nbt_tags", this.getTag().getKeys().size()).formatted(Formatting.DARK_GRAY));
            }
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public static void appendEnchantments(List<Text> list, ListTag arg) {
        for (int i = 0; i < arg.size(); ++i) {
            CompoundTag lv = arg.getCompound(i);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(lv.getString("id"))).ifPresent(arg2 -> list.add(arg2.getName(lv.getInt("lvl"))));
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static Collection<Text> parseBlockTag(String string) {
        try {
            boolean bl2;
            BlockArgumentParser lv = new BlockArgumentParser(new StringReader(string), true).parse(true);
            BlockState lv2 = lv.getBlockState();
            Identifier lv3 = lv.getTagId();
            boolean bl = lv2 != null;
            boolean bl3 = bl2 = lv3 != null;
            if (bl || bl2) {
                List<Block> collection;
                if (bl) {
                    return Lists.newArrayList((Object[])new Text[]{lv2.getBlock().getName().formatted(Formatting.DARK_GRAY)});
                }
                net.minecraft.tag.Tag<Block> lv4 = BlockTags.getContainer().get(lv3);
                if (lv4 != null && !(collection = lv4.values()).isEmpty()) {
                    return collection.stream().map(Block::getName).map(arg -> arg.formatted(Formatting.DARK_GRAY)).collect(Collectors.toList());
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

    public void addEnchantment(Enchantment arg, int i) {
        this.getOrCreateTag();
        if (!this.tag.contains("Enchantments", 9)) {
            this.tag.put("Enchantments", new ListTag());
        }
        ListTag lv = this.tag.getList("Enchantments", 10);
        CompoundTag lv2 = new CompoundTag();
        lv2.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(arg)));
        lv2.putShort("lvl", (byte)i);
        lv.add(lv2);
    }

    public boolean hasEnchantments() {
        if (this.tag != null && this.tag.contains("Enchantments", 9)) {
            return !this.tag.getList("Enchantments", 10).isEmpty();
        }
        return false;
    }

    public void putSubTag(String string, Tag arg) {
        this.getOrCreateTag().put(string, arg);
    }

    public boolean isInFrame() {
        return this.holder instanceof ItemFrameEntity;
    }

    public void setHolder(@Nullable Entity arg) {
        this.holder = arg;
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

    public void setRepairCost(int i) {
        this.getOrCreateTag().putInt("RepairCost", i);
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

    public void addAttributeModifier(EntityAttribute arg, EntityAttributeModifier arg2, @Nullable EquipmentSlot arg3) {
        this.getOrCreateTag();
        if (!this.tag.contains("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new ListTag());
        }
        ListTag lv = this.tag.getList("AttributeModifiers", 10);
        CompoundTag lv2 = arg2.toTag();
        lv2.putString("AttributeName", Registry.ATTRIBUTE.getId(arg).toString());
        if (arg3 != null) {
            lv2.putString("Slot", arg3.getName());
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
            lv2.formatted(this.getRarity().formatting).styled(arg -> arg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(this))));
        }
        return lv2;
    }

    private static boolean areBlocksEqual(CachedBlockPosition arg, @Nullable CachedBlockPosition arg2) {
        if (arg2 == null || arg.getBlockState() != arg2.getBlockState()) {
            return false;
        }
        if (arg.getBlockEntity() == null && arg2.getBlockEntity() == null) {
            return true;
        }
        if (arg.getBlockEntity() == null || arg2.getBlockEntity() == null) {
            return false;
        }
        return Objects.equals(arg.getBlockEntity().toTag(new CompoundTag()), arg2.getBlockEntity().toTag(new CompoundTag()));
    }

    public boolean canDestroy(RegistryTagManager arg, CachedBlockPosition arg2) {
        if (ItemStack.areBlocksEqual(arg2, this.lastDestroyPos)) {
            return this.lastDestroyResult;
        }
        this.lastDestroyPos = arg2;
        if (this.hasTag() && this.tag.contains("CanDestroy", 9)) {
            ListTag lv = this.tag.getList("CanDestroy", 8);
            for (int i = 0; i < lv.size(); ++i) {
                String string = lv.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(arg);
                    if (predicate.test(arg2)) {
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

    public boolean canPlaceOn(RegistryTagManager arg, CachedBlockPosition arg2) {
        if (ItemStack.areBlocksEqual(arg2, this.lastPlaceOnPos)) {
            return this.lastPlaceOnResult;
        }
        this.lastPlaceOnPos = arg2;
        if (this.hasTag() && this.tag.contains("CanPlaceOn", 9)) {
            ListTag lv = this.tag.getList("CanPlaceOn", 8);
            for (int i = 0; i < lv.size(); ++i) {
                String string = lv.getString(i);
                try {
                    Predicate<CachedBlockPosition> predicate = BlockPredicateArgumentType.blockPredicate().parse(new StringReader(string)).create(arg);
                    if (predicate.test(arg2)) {
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

    public void setCooldown(int i) {
        this.cooldown = i;
    }

    public int getCount() {
        return this.empty ? 0 : this.count;
    }

    public void setCount(int i) {
        this.count = i;
        this.updateEmptyState();
    }

    public void increment(int i) {
        this.setCount(this.count + i);
    }

    public void decrement(int i) {
        this.increment(-i);
    }

    public void usageTick(World arg, LivingEntity arg2, int i) {
        this.getItem().usageTick(arg, arg2, this, i);
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
}

