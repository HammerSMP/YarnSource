/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class EnchantmentScreenHandler
extends ScreenHandler {
    private final Inventory inventory = new BasicInventory(2){

        @Override
        public void markDirty() {
            super.markDirty();
            EnchantmentScreenHandler.this.onContentChanged(this);
        }
    };
    private final ScreenHandlerContext context;
    private final Random random = new Random();
    private final Property seed = Property.create();
    public final int[] enchantmentPower = new int[3];
    public final int[] enchantmentId = new int[]{-1, -1, -1};
    public final int[] enchantmentLevel = new int[]{-1, -1, -1};

    public EnchantmentScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, ScreenHandlerContext.EMPTY);
    }

    public EnchantmentScreenHandler(int i, PlayerInventory arg, ScreenHandlerContext arg2) {
        super(ScreenHandlerType.ENCHANTMENT, i);
        this.context = arg2;
        this.addSlot(new Slot(this.inventory, 0, 15, 47){

            @Override
            public boolean canInsert(ItemStack arg) {
                return true;
            }

            @Override
            public int getMaxStackAmount() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.inventory, 1, 35, 47){

            @Override
            public boolean canInsert(ItemStack arg) {
                return arg.getItem() == Items.LAPIS_LAZULI;
            }
        });
        for (int j = 0; j < 3; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(arg, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }
        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(arg, l, 8 + l * 18, 142));
        }
        this.addProperty(Property.create(this.enchantmentPower, 0));
        this.addProperty(Property.create(this.enchantmentPower, 1));
        this.addProperty(Property.create(this.enchantmentPower, 2));
        this.addProperty(this.seed).set(arg.player.getEnchantmentTableSeed());
        this.addProperty(Property.create(this.enchantmentId, 0));
        this.addProperty(Property.create(this.enchantmentId, 1));
        this.addProperty(Property.create(this.enchantmentId, 2));
        this.addProperty(Property.create(this.enchantmentLevel, 0));
        this.addProperty(Property.create(this.enchantmentLevel, 1));
        this.addProperty(Property.create(this.enchantmentLevel, 2));
    }

    @Override
    public void onContentChanged(Inventory arg) {
        if (arg == this.inventory) {
            ItemStack lv = arg.getStack(0);
            if (lv.isEmpty() || !lv.isEnchantable()) {
                for (int i = 0; i < 3; ++i) {
                    this.enchantmentPower[i] = 0;
                    this.enchantmentId[i] = -1;
                    this.enchantmentLevel[i] = -1;
                }
            } else {
                this.context.run((arg2, arg3) -> {
                    int i = 0;
                    for (int j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if (j == 0 && k == 0 || !arg2.isAir(arg3.add(k, 0, j)) || !arg2.isAir(arg3.add(k, 1, j))) continue;
                            if (arg2.getBlockState(arg3.add(k * 2, 0, j * 2)).isOf(Blocks.BOOKSHELF)) {
                                ++i;
                            }
                            if (arg2.getBlockState(arg3.add(k * 2, 1, j * 2)).isOf(Blocks.BOOKSHELF)) {
                                ++i;
                            }
                            if (k == 0 || j == 0) continue;
                            if (arg2.getBlockState(arg3.add(k * 2, 0, j)).isOf(Blocks.BOOKSHELF)) {
                                ++i;
                            }
                            if (arg2.getBlockState(arg3.add(k * 2, 1, j)).isOf(Blocks.BOOKSHELF)) {
                                ++i;
                            }
                            if (arg2.getBlockState(arg3.add(k, 0, j * 2)).isOf(Blocks.BOOKSHELF)) {
                                ++i;
                            }
                            if (!arg2.getBlockState(arg3.add(k, 1, j * 2)).isOf(Blocks.BOOKSHELF)) continue;
                            ++i;
                        }
                    }
                    this.random.setSeed(this.seed.get());
                    for (int l = 0; l < 3; ++l) {
                        this.enchantmentPower[l] = EnchantmentHelper.calculateRequiredExperienceLevel(this.random, l, i, lv);
                        this.enchantmentId[l] = -1;
                        this.enchantmentLevel[l] = -1;
                        if (this.enchantmentPower[l] >= l + 1) continue;
                        this.enchantmentPower[l] = 0;
                    }
                    for (int m = 0; m < 3; ++m) {
                        List<EnchantmentLevelEntry> list;
                        if (this.enchantmentPower[m] <= 0 || (list = this.generateEnchantments(lv, m, this.enchantmentPower[m])) == null || list.isEmpty()) continue;
                        EnchantmentLevelEntry lv = list.get(this.random.nextInt(list.size()));
                        this.enchantmentId[m] = Registry.ENCHANTMENT.getRawId(lv.enchantment);
                        this.enchantmentLevel[m] = lv.level;
                    }
                    this.sendContentUpdates();
                });
            }
        }
    }

    @Override
    public boolean onButtonClick(PlayerEntity arg, int i) {
        ItemStack lv = this.inventory.getStack(0);
        ItemStack lv2 = this.inventory.getStack(1);
        int j = i + 1;
        if ((lv2.isEmpty() || lv2.getCount() < j) && !arg.abilities.creativeMode) {
            return false;
        }
        if (this.enchantmentPower[i] > 0 && !lv.isEmpty() && (arg.experienceLevel >= j && arg.experienceLevel >= this.enchantmentPower[i] || arg.abilities.creativeMode)) {
            this.context.run((arg4, arg5) -> {
                ItemStack lv = lv;
                List<EnchantmentLevelEntry> list = this.generateEnchantments(lv, i, this.enchantmentPower[i]);
                if (!list.isEmpty()) {
                    boolean bl;
                    arg.applyEnchantmentCosts(lv, j);
                    boolean bl2 = bl = lv.getItem() == Items.BOOK;
                    if (bl) {
                        lv = new ItemStack(Items.ENCHANTED_BOOK);
                        CompoundTag lv2 = lv.getTag();
                        if (lv2 != null) {
                            lv.setTag(lv2.copy());
                        }
                        this.inventory.setStack(0, lv);
                    }
                    for (int k = 0; k < list.size(); ++k) {
                        EnchantmentLevelEntry lv3 = list.get(k);
                        if (bl) {
                            EnchantedBookItem.addEnchantment(lv, lv3);
                            continue;
                        }
                        lv.addEnchantment(lv3.enchantment, lv3.level);
                    }
                    if (!arg2.abilities.creativeMode) {
                        lv2.decrement(j);
                        if (lv2.isEmpty()) {
                            this.inventory.setStack(1, ItemStack.EMPTY);
                        }
                    }
                    arg.incrementStat(Stats.ENCHANT_ITEM);
                    if (arg instanceof ServerPlayerEntity) {
                        Criteria.ENCHANTED_ITEM.trigger((ServerPlayerEntity)arg, lv, j);
                    }
                    this.inventory.markDirty();
                    this.seed.set(arg.getEnchantmentTableSeed());
                    this.onContentChanged(this.inventory);
                    arg4.playSound(null, (BlockPos)arg5, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0f, arg4.random.nextFloat() * 0.1f + 0.9f);
                }
            });
            return true;
        }
        return false;
    }

    private List<EnchantmentLevelEntry> generateEnchantments(ItemStack arg, int i, int j) {
        this.random.setSeed(this.seed.get() + i);
        List<EnchantmentLevelEntry> list = EnchantmentHelper.generateEnchantments(this.random, arg, j, false);
        if (arg.getItem() == Items.BOOK && list.size() > 1) {
            list.remove(this.random.nextInt(list.size()));
        }
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public int getLapisCount() {
        ItemStack lv = this.inventory.getStack(1);
        if (lv.isEmpty()) {
            return 0;
        }
        return lv.getCount();
    }

    @Environment(value=EnvType.CLIENT)
    public int getSeed() {
        return this.seed.get();
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.context.run((arg2, arg3) -> this.dropInventory(arg, arg.world, this.inventory));
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return EnchantmentScreenHandler.canUse(this.context, arg, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            if (i == 0) {
                if (!this.insertItem(lv3, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (i == 1) {
                if (!this.insertItem(lv3, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (lv3.getItem() == Items.LAPIS_LAZULI) {
                if (!this.insertItem(lv3, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!((Slot)this.slots.get(0)).hasStack() && ((Slot)this.slots.get(0)).canInsert(lv3)) {
                ItemStack lv4 = lv3.copy();
                lv4.setCount(1);
                lv3.decrement(1);
                ((Slot)this.slots.get(0)).setStack(lv4);
            } else {
                return ItemStack.EMPTY;
            }
            if (lv3.isEmpty()) {
                lv2.setStack(ItemStack.EMPTY);
            } else {
                lv2.markDirty();
            }
            if (lv3.getCount() == lv.getCount()) {
                return ItemStack.EMPTY;
            }
            lv2.onTakeItem(arg, lv3);
        }
        return lv;
    }
}

