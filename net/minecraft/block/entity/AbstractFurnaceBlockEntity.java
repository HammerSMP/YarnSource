/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractFurnaceBlockEntity
extends LockableContainerBlockEntity
implements SidedInventory,
RecipeUnlocker,
RecipeInputProvider,
Tickable {
    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{2, 1};
    private static final int[] SIDE_SLOTS = new int[]{1};
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
    private int burnTime;
    private int fuelTime;
    private int cookTime;
    private int cookTimeTotal;
    protected final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            switch (index) {
                case 0: {
                    return AbstractFurnaceBlockEntity.this.burnTime;
                }
                case 1: {
                    return AbstractFurnaceBlockEntity.this.fuelTime;
                }
                case 2: {
                    return AbstractFurnaceBlockEntity.this.cookTime;
                }
                case 3: {
                    return AbstractFurnaceBlockEntity.this.cookTimeTotal;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: {
                    AbstractFurnaceBlockEntity.this.burnTime = value;
                    break;
                }
                case 1: {
                    AbstractFurnaceBlockEntity.this.fuelTime = value;
                    break;
                }
                case 2: {
                    AbstractFurnaceBlockEntity.this.cookTime = value;
                    break;
                }
                case 3: {
                    AbstractFurnaceBlockEntity.this.cookTimeTotal = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap();
    protected final RecipeType<? extends AbstractCookingRecipe> recipeType;

    protected AbstractFurnaceBlockEntity(BlockEntityType<?> blockEntityType, RecipeType<? extends AbstractCookingRecipe> recipeType) {
        super(blockEntityType);
        this.recipeType = recipeType;
    }

    public static Map<Item, Integer> createFuelTimeMap() {
        LinkedHashMap map = Maps.newLinkedHashMap();
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.LAVA_BUCKET, 20000);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.COAL_BLOCK, 16000);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.BLAZE_ROD, 2400);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.COAL, 1600);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.CHARCOAL, 1600);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.LOGS, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.PLANKS, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.WOODEN_STAIRS, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.WOODEN_SLABS, 150);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.WOODEN_TRAPDOORS, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.OAK_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.BIRCH_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.SPRUCE_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.JUNGLE_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.DARK_OAK_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.ACACIA_FENCE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.OAK_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.BIRCH_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.SPRUCE_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.JUNGLE_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.DARK_OAK_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.ACACIA_FENCE_GATE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.NOTE_BLOCK, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.BOOKSHELF, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.LECTERN, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.JUKEBOX, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.CHEST, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.TRAPPED_CHEST, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.CRAFTING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.DAYLIGHT_DETECTOR, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.BANNERS, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.BOW, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.FISHING_ROD, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.LADDER, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.SIGNS, 200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.WOODEN_SHOVEL, 200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.WOODEN_SWORD, 200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.WOODEN_HOE, 200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.WOODEN_AXE, 200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.WOODEN_PICKAXE, 200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.WOODEN_DOORS, 200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.BOATS, 1200);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.WOOL, 100);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.WOODEN_BUTTONS, 100);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.STICK, 100);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.SAPLINGS, 100);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.BOWL, 100);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, ItemTags.CARPETS, 67);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.DRIED_KELP_BLOCK, 4001);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Items.CROSSBOW, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.BAMBOO, 50);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.DEAD_BUSH, 100);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.SCAFFOLDING, 400);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.LOOM, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.BARREL, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.CARTOGRAPHY_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.FLETCHING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.SMITHING_TABLE, 300);
        AbstractFurnaceBlockEntity.addFuel((Map<Item, Integer>)map, Blocks.COMPOSTER, 300);
        return map;
    }

    private static boolean isFlammableWood(Item item) {
        return ItemTags.NON_FLAMMABLE_WOOD.contains(item);
    }

    private static void addFuel(Map<Item, Integer> fuelTimes, Tag<Item> tag, int fuelTime) {
        for (Item lv : tag.values()) {
            if (AbstractFurnaceBlockEntity.isFlammableWood(lv)) continue;
            fuelTimes.put(lv, fuelTime);
        }
    }

    private static void addFuel(Map<Item, Integer> map, ItemConvertible item, int fuelTime) {
        Item lv = item.asItem();
        if (AbstractFurnaceBlockEntity.isFlammableWood(lv)) {
            if (SharedConstants.isDevelopment) {
                throw Util.throwOrPause(new IllegalStateException("A developer tried to explicitly make fire resistant item " + lv.getName(null).getString() + " a furnace fuel. That will not work!"));
            }
            return;
        }
        map.put(lv, fuelTime);
    }

    private boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.fromTag(tag, this.inventory);
        this.burnTime = tag.getShort("BurnTime");
        this.cookTime = tag.getShort("CookTime");
        this.cookTimeTotal = tag.getShort("CookTimeTotal");
        this.fuelTime = this.getFuelTime(this.inventory.get(1));
        CompoundTag lv = tag.getCompound("RecipesUsed");
        for (String string : lv.getKeys()) {
            this.recipesUsed.put((Object)new Identifier(string), lv.getInt(string));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putShort("BurnTime", (short)this.burnTime);
        tag.putShort("CookTime", (short)this.cookTime);
        tag.putShort("CookTimeTotal", (short)this.cookTimeTotal);
        Inventories.toTag(tag, this.inventory);
        CompoundTag lv = new CompoundTag();
        this.recipesUsed.forEach((arg2, integer) -> lv.putInt(arg2.toString(), (int)integer));
        tag.put("RecipesUsed", lv);
        return tag;
    }

    @Override
    public void tick() {
        boolean bl = this.isBurning();
        boolean bl2 = false;
        if (this.isBurning()) {
            --this.burnTime;
        }
        if (!this.world.isClient) {
            ItemStack lv = this.inventory.get(1);
            if (this.isBurning() || !lv.isEmpty() && !this.inventory.get(0).isEmpty()) {
                Recipe lv2 = this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).orElse(null);
                if (!this.isBurning() && this.canAcceptRecipeOutput(lv2)) {
                    this.fuelTime = this.burnTime = this.getFuelTime(lv);
                    if (this.isBurning()) {
                        bl2 = true;
                        if (!lv.isEmpty()) {
                            Item lv3 = lv.getItem();
                            lv.decrement(1);
                            if (lv.isEmpty()) {
                                Item lv4 = lv3.getRecipeRemainder();
                                this.inventory.set(1, lv4 == null ? ItemStack.EMPTY : new ItemStack(lv4));
                            }
                        }
                    }
                }
                if (this.isBurning() && this.canAcceptRecipeOutput(lv2)) {
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.craftRecipe(lv2);
                        bl2 = true;
                    }
                } else {
                    this.cookTime = 0;
                }
            } else if (!this.isBurning() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }
            if (bl != this.isBurning()) {
                bl2 = true;
                this.world.setBlockState(this.pos, (BlockState)this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
            }
        }
        if (bl2) {
            this.markDirty();
        }
    }

    protected boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe) {
        if (this.inventory.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack lv = recipe.getOutput();
        if (lv.isEmpty()) {
            return false;
        }
        ItemStack lv2 = this.inventory.get(2);
        if (lv2.isEmpty()) {
            return true;
        }
        if (!lv2.isItemEqualIgnoreDamage(lv)) {
            return false;
        }
        if (lv2.getCount() < this.getMaxCountPerStack() && lv2.getCount() < lv2.getMaxCount()) {
            return true;
        }
        return lv2.getCount() < lv.getMaxCount();
    }

    private void craftRecipe(@Nullable Recipe<?> recipe) {
        if (recipe == null || !this.canAcceptRecipeOutput(recipe)) {
            return;
        }
        ItemStack lv = this.inventory.get(0);
        ItemStack lv2 = recipe.getOutput();
        ItemStack lv3 = this.inventory.get(2);
        if (lv3.isEmpty()) {
            this.inventory.set(2, lv2.copy());
        } else if (lv3.getItem() == lv2.getItem()) {
            lv3.increment(1);
        }
        if (!this.world.isClient) {
            this.setLastRecipe(recipe);
        }
        if (lv.getItem() == Blocks.WET_SPONGE.asItem() && !this.inventory.get(1).isEmpty() && this.inventory.get(1).getItem() == Items.BUCKET) {
            this.inventory.set(1, new ItemStack(Items.WATER_BUCKET));
        }
        lv.decrement(1);
    }

    protected int getFuelTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        Item lv = fuel.getItem();
        return AbstractFurnaceBlockEntity.createFuelTimeMap().getOrDefault(lv, 0);
    }

    protected int getCookTime() {
        return this.world.getRecipeManager().getFirstMatch(this.recipeType, this, this.world).map(AbstractCookingRecipe::getCookTime).orElse(200);
    }

    public static boolean canUseAsFuel(ItemStack stack) {
        return AbstractFurnaceBlockEntity.createFuelTimeMap().containsKey(stack.getItem());
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.DOWN) {
            return BOTTOM_SLOTS;
        }
        if (side == Direction.UP) {
            return TOP_SLOTS;
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.isValid(slot, stack);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        Item lv;
        return dir != Direction.DOWN || slot != 1 || (lv = stack.getItem()) == Items.WATER_BUCKET || lv == Items.BUCKET;
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack lv : this.inventory) {
            if (lv.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(this.inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStack lv = this.inventory.get(slot);
        boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(lv) && ItemStack.areTagsEqual(stack, lv);
        this.inventory.set(slot, stack);
        if (stack.getCount() > this.getMaxCountPerStack()) {
            stack.setCount(this.getMaxCountPerStack());
        }
        if (slot == 0 && !bl) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime = 0;
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getBlockEntity(this.pos) != this) {
            return false;
        }
        return player.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) <= 64.0;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        if (slot == 2) {
            return false;
        }
        if (slot == 1) {
            ItemStack lv = this.inventory.get(1);
            return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || stack.getItem() == Items.BUCKET && lv.getItem() != Items.BUCKET;
        }
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public void setLastRecipe(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            Identifier lv = recipe.getId();
            this.recipesUsed.addTo((Object)lv, 1);
        }
    }

    @Override
    @Nullable
    public Recipe<?> getLastRecipe() {
        return null;
    }

    @Override
    public void unlockLastRecipe(PlayerEntity player) {
    }

    public void dropExperience(PlayerEntity player) {
        List<Recipe<?>> list = this.method_27354(player.world, player.getPos());
        player.unlockRecipes(list);
        this.recipesUsed.clear();
    }

    public List<Recipe<?>> method_27354(World arg, Vec3d arg2) {
        ArrayList list = Lists.newArrayList();
        for (Object2IntMap.Entry entry : this.recipesUsed.object2IntEntrySet()) {
            arg.getRecipeManager().get((Identifier)entry.getKey()).ifPresent(arg3 -> {
                list.add(arg3);
                AbstractFurnaceBlockEntity.dropExperience(arg, arg2, entry.getIntValue(), ((AbstractCookingRecipe)arg3).getExperience());
            });
        }
        return list;
    }

    private static void dropExperience(World arg, Vec3d arg2, int i, float f) {
        int j = MathHelper.floor((float)i * f);
        float g = MathHelper.fractionalPart((float)i * f);
        if (g != 0.0f && Math.random() < (double)g) {
            ++j;
        }
        while (j > 0) {
            int k = ExperienceOrbEntity.roundToOrbSize(j);
            j -= k;
            arg.spawnEntity(new ExperienceOrbEntity(arg, arg2.x, arg2.y, arg2.z, k));
        }
    }

    @Override
    public void provideRecipeInputs(RecipeFinder finder) {
        for (ItemStack lv : this.inventory) {
            finder.addItem(lv);
        }
    }
}

