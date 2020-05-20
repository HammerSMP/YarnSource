/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

public class Item
implements ItemConvertible {
    public static final Map<Block, Item> BLOCK_ITEMS = Maps.newHashMap();
    protected static final UUID ATTACK_DAMAGE_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID ATTACK_SPEED_MODIFIER_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    protected static final Random RANDOM = new Random();
    protected final ItemGroup group;
    private final Rarity rarity;
    private final int maxCount;
    private final int maxDamage;
    private final boolean fireproof;
    private final Item recipeRemainder;
    @Nullable
    private String translationKey;
    @Nullable
    private final FoodComponent foodComponent;

    public static int getRawId(Item arg) {
        return arg == null ? 0 : Registry.ITEM.getRawId(arg);
    }

    public static Item byRawId(int i) {
        return Registry.ITEM.get(i);
    }

    @Deprecated
    public static Item fromBlock(Block arg) {
        return BLOCK_ITEMS.getOrDefault(arg, Items.AIR);
    }

    public Item(Settings arg) {
        this.group = arg.group;
        this.rarity = arg.rarity;
        this.recipeRemainder = arg.recipeRemainder;
        this.maxDamage = arg.maxDamage;
        this.maxCount = arg.maxCount;
        this.foodComponent = arg.foodComponent;
        this.fireproof = arg.fireproof;
    }

    public void usageTick(World arg, LivingEntity arg2, ItemStack arg3, int i) {
    }

    public boolean postProcessTag(CompoundTag arg) {
        return false;
    }

    public boolean canMine(BlockState arg, World arg2, BlockPos arg3, PlayerEntity arg4) {
        return true;
    }

    @Override
    public Item asItem() {
        return this;
    }

    public ActionResult useOnBlock(ItemUsageContext arg) {
        return ActionResult.PASS;
    }

    public float getMiningSpeedMultiplier(ItemStack arg, BlockState arg2) {
        return 1.0f;
    }

    public TypedActionResult<ItemStack> use(World arg, PlayerEntity arg2, Hand arg3) {
        if (this.isFood()) {
            ItemStack lv = arg2.getStackInHand(arg3);
            if (arg2.canConsume(this.getFoodComponent().isAlwaysEdible())) {
                arg2.setCurrentHand(arg3);
                return TypedActionResult.consume(lv);
            }
            return TypedActionResult.fail(lv);
        }
        return TypedActionResult.pass(arg2.getStackInHand(arg3));
    }

    public ItemStack finishUsing(ItemStack arg, World arg2, LivingEntity arg3) {
        if (this.isFood()) {
            return arg3.eatFood(arg2, arg);
        }
        return arg;
    }

    public final int getMaxCount() {
        return this.maxCount;
    }

    public final int getMaxDamage() {
        return this.maxDamage;
    }

    public boolean isDamageable() {
        return this.maxDamage > 0;
    }

    public boolean postHit(ItemStack arg, LivingEntity arg2, LivingEntity arg3) {
        return false;
    }

    public boolean postMine(ItemStack arg, World arg2, BlockState arg3, BlockPos arg4, LivingEntity arg5) {
        return false;
    }

    public boolean isEffectiveOn(BlockState arg) {
        return false;
    }

    public boolean useOnEntity(ItemStack arg, PlayerEntity arg2, LivingEntity arg3, Hand arg4) {
        return false;
    }

    @Environment(value=EnvType.CLIENT)
    public Text getName() {
        return new TranslatableText(this.getTranslationKey());
    }

    public String toString() {
        return Registry.ITEM.getId(this).getPath();
    }

    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.createTranslationKey("item", Registry.ITEM.getId(this));
        }
        return this.translationKey;
    }

    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }

    public String getTranslationKey(ItemStack arg) {
        return this.getTranslationKey();
    }

    public boolean shouldSyncTagToClient() {
        return true;
    }

    @Nullable
    public final Item getRecipeRemainder() {
        return this.recipeRemainder;
    }

    public boolean hasRecipeRemainder() {
        return this.recipeRemainder != null;
    }

    public void inventoryTick(ItemStack arg, World arg2, Entity arg3, int i, boolean bl) {
    }

    public void onCraft(ItemStack arg, World arg2, PlayerEntity arg3) {
    }

    public boolean isNetworkSynced() {
        return false;
    }

    public UseAction getUseAction(ItemStack arg) {
        return arg.getItem().isFood() ? UseAction.EAT : UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack arg) {
        if (arg.getItem().isFood()) {
            return this.getFoodComponent().isSnack() ? 16 : 32;
        }
        return 0;
    }

    public void onStoppedUsing(ItemStack arg, World arg2, LivingEntity arg3, int i) {
    }

    @Environment(value=EnvType.CLIENT)
    public void appendTooltip(ItemStack arg, @Nullable World arg2, List<Text> list, TooltipContext arg3) {
    }

    public Text getName(ItemStack arg) {
        return new TranslatableText(this.getTranslationKey(arg));
    }

    public boolean hasEnchantmentGlint(ItemStack arg) {
        return arg.hasEnchantments();
    }

    public Rarity getRarity(ItemStack arg) {
        if (!arg.hasEnchantments()) {
            return this.rarity;
        }
        switch (this.rarity) {
            case COMMON: 
            case UNCOMMON: {
                return Rarity.RARE;
            }
            case RARE: {
                return Rarity.EPIC;
            }
        }
        return this.rarity;
    }

    public boolean isEnchantable(ItemStack arg) {
        return this.getMaxCount() == 1 && this.isDamageable();
    }

    protected static HitResult rayTrace(World arg, PlayerEntity arg2, RayTraceContext.FluidHandling arg3) {
        float f = arg2.pitch;
        float g = arg2.yaw;
        Vec3d lv = arg2.getCameraPosVec(1.0f);
        float h = MathHelper.cos(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float i = MathHelper.sin(-g * ((float)Math.PI / 180) - (float)Math.PI);
        float j = -MathHelper.cos(-f * ((float)Math.PI / 180));
        float k = MathHelper.sin(-f * ((float)Math.PI / 180));
        float l = i * j;
        float m = k;
        float n = h * j;
        double d = 5.0;
        Vec3d lv2 = lv.add((double)l * 5.0, (double)m * 5.0, (double)n * 5.0);
        return arg.rayTrace(new RayTraceContext(lv, lv2, RayTraceContext.ShapeType.OUTLINE, arg3, arg2));
    }

    public int getEnchantability() {
        return 0;
    }

    public void appendStacks(ItemGroup arg, DefaultedList<ItemStack> arg2) {
        if (this.isIn(arg)) {
            arg2.add(new ItemStack(this));
        }
    }

    protected boolean isIn(ItemGroup arg) {
        ItemGroup lv = this.getGroup();
        return lv != null && (arg == ItemGroup.SEARCH || arg == lv);
    }

    @Nullable
    public final ItemGroup getGroup() {
        return this.group;
    }

    public boolean canRepair(ItemStack arg, ItemStack arg2) {
        return false;
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot arg) {
        return ImmutableMultimap.of();
    }

    public boolean isUsedOnRelease(ItemStack arg) {
        return arg.getItem() == Items.CROSSBOW;
    }

    @Environment(value=EnvType.CLIENT)
    public ItemStack getStackForRender() {
        return new ItemStack(this);
    }

    public boolean isIn(Tag<Item> arg) {
        return arg.contains(this);
    }

    public boolean isFood() {
        return this.foodComponent != null;
    }

    @Nullable
    public FoodComponent getFoodComponent() {
        return this.foodComponent;
    }

    public SoundEvent getDrinkSound() {
        return SoundEvents.ENTITY_GENERIC_DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ENTITY_GENERIC_EAT;
    }

    public boolean isFireproof() {
        return this.fireproof;
    }

    public boolean damage(DamageSource arg) {
        return !this.fireproof || !arg.isFire();
    }

    public static class Settings {
        private int maxCount = 64;
        private int maxDamage;
        private Item recipeRemainder;
        private ItemGroup group;
        private Rarity rarity = Rarity.COMMON;
        private FoodComponent foodComponent;
        private boolean fireproof;

        public Settings food(FoodComponent arg) {
            this.foodComponent = arg;
            return this;
        }

        public Settings maxCount(int i) {
            if (this.maxDamage > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            }
            this.maxCount = i;
            return this;
        }

        public Settings maxDamageIfAbsent(int i) {
            return this.maxDamage == 0 ? this.maxDamage(i) : this;
        }

        public Settings maxDamage(int i) {
            this.maxDamage = i;
            this.maxCount = 1;
            return this;
        }

        public Settings recipeRemainder(Item arg) {
            this.recipeRemainder = arg;
            return this;
        }

        public Settings group(ItemGroup arg) {
            this.group = arg;
            return this;
        }

        public Settings rarity(Rarity arg) {
            this.rarity = arg;
            return this;
        }

        public Settings fireproof() {
            this.fireproof = true;
            return this;
        }
    }
}

