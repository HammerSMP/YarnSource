/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.screen;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilScreenHandler
extends ForgingScreenHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private int repairItemUsage;
    private String newItemName;
    private final Property levelCost = Property.create();

    public AnvilScreenHandler(int i, PlayerInventory arg) {
        this(i, arg, ScreenHandlerContext.EMPTY);
    }

    public AnvilScreenHandler(int i, PlayerInventory arg, ScreenHandlerContext arg2) {
        super(ScreenHandlerType.ANVIL, i, arg, arg2);
        this.addProperty(this.levelCost);
    }

    @Override
    protected boolean canUse(BlockState arg) {
        return arg.isIn(BlockTags.ANVIL);
    }

    @Override
    protected boolean canTakeOutput(PlayerEntity arg, boolean bl) {
        return (arg.abilities.creativeMode || arg.experienceLevel >= this.levelCost.get()) && this.levelCost.get() > 0;
    }

    @Override
    protected ItemStack onTakeOutput(PlayerEntity arg, ItemStack arg22) {
        if (!arg.abilities.creativeMode) {
            arg.addExperienceLevels(-this.levelCost.get());
        }
        this.input.setStack(0, ItemStack.EMPTY);
        if (this.repairItemUsage > 0) {
            ItemStack lv = this.input.getStack(1);
            if (!lv.isEmpty() && lv.getCount() > this.repairItemUsage) {
                lv.decrement(this.repairItemUsage);
                this.input.setStack(1, lv);
            } else {
                this.input.setStack(1, ItemStack.EMPTY);
            }
        } else {
            this.input.setStack(1, ItemStack.EMPTY);
        }
        this.levelCost.set(0);
        this.context.run((arg2, arg3) -> {
            BlockState lv = arg2.getBlockState((BlockPos)arg3);
            if (!arg.abilities.creativeMode && lv.isIn(BlockTags.ANVIL) && arg.getRandom().nextFloat() < 0.12f) {
                BlockState lv2 = AnvilBlock.getLandingState(lv);
                if (lv2 == null) {
                    arg2.removeBlock((BlockPos)arg3, false);
                    arg2.syncWorldEvent(1029, (BlockPos)arg3, 0);
                } else {
                    arg2.setBlockState((BlockPos)arg3, lv2, 2);
                    arg2.syncWorldEvent(1030, (BlockPos)arg3, 0);
                }
            } else {
                arg2.syncWorldEvent(1030, (BlockPos)arg3, 0);
            }
        });
        return arg22;
    }

    @Override
    public void updateResult() {
        ItemStack lv = this.input.getStack(0);
        this.levelCost.set(1);
        int i = 0;
        int j = 0;
        int k = 0;
        if (lv.isEmpty()) {
            this.output.setStack(0, ItemStack.EMPTY);
            this.levelCost.set(0);
            return;
        }
        ItemStack lv2 = lv.copy();
        ItemStack lv3 = this.input.getStack(1);
        Map<Enchantment, Integer> map = EnchantmentHelper.get(lv2);
        j += lv.getRepairCost() + (lv3.isEmpty() ? 0 : lv3.getRepairCost());
        this.repairItemUsage = 0;
        if (!lv3.isEmpty()) {
            boolean bl;
            boolean bl2 = bl = lv3.getItem() == Items.ENCHANTED_BOOK && !EnchantedBookItem.getEnchantmentTag(lv3).isEmpty();
            if (lv2.isDamageable() && lv2.getItem().canRepair(lv, lv3)) {
                int m;
                int l = Math.min(lv2.getDamage(), lv2.getMaxDamage() / 4);
                if (l <= 0) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                for (m = 0; l > 0 && m < lv3.getCount(); ++m) {
                    int n = lv2.getDamage() - l;
                    lv2.setDamage(n);
                    ++i;
                    l = Math.min(lv2.getDamage(), lv2.getMaxDamage() / 4);
                }
                this.repairItemUsage = m;
            } else {
                if (!(bl || lv2.getItem() == lv3.getItem() && lv2.isDamageable())) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
                if (lv2.isDamageable() && !bl) {
                    int o = lv.getMaxDamage() - lv.getDamage();
                    int p = lv3.getMaxDamage() - lv3.getDamage();
                    int q = p + lv2.getMaxDamage() * 12 / 100;
                    int r = o + q;
                    int s = lv2.getMaxDamage() - r;
                    if (s < 0) {
                        s = 0;
                    }
                    if (s < lv2.getDamage()) {
                        lv2.setDamage(s);
                        i += 2;
                    }
                }
                Map<Enchantment, Integer> map2 = EnchantmentHelper.get(lv3);
                boolean bl22 = false;
                boolean bl3 = false;
                for (Enchantment lv4 : map2.keySet()) {
                    if (lv4 == null) continue;
                    int t = map.containsKey(lv4) ? map.get(lv4) : 0;
                    int u = map2.get(lv4);
                    u = t == u ? u + 1 : Math.max(u, t);
                    boolean bl4 = lv4.isAcceptableItem(lv);
                    if (this.player.abilities.creativeMode || lv.getItem() == Items.ENCHANTED_BOOK) {
                        bl4 = true;
                    }
                    for (Enchantment lv5 : map.keySet()) {
                        if (lv5 == lv4 || lv4.canCombine(lv5)) continue;
                        bl4 = false;
                        ++i;
                    }
                    if (!bl4) {
                        bl3 = true;
                        continue;
                    }
                    bl22 = true;
                    if (u > lv4.getMaximumLevel()) {
                        u = lv4.getMaximumLevel();
                    }
                    map.put(lv4, u);
                    int v = 0;
                    switch (lv4.getRarity()) {
                        case COMMON: {
                            v = 1;
                            break;
                        }
                        case UNCOMMON: {
                            v = 2;
                            break;
                        }
                        case RARE: {
                            v = 4;
                            break;
                        }
                        case VERY_RARE: {
                            v = 8;
                        }
                    }
                    if (bl) {
                        v = Math.max(1, v / 2);
                    }
                    i += v * u;
                    if (lv.getCount() <= 1) continue;
                    i = 40;
                }
                if (bl3 && !bl22) {
                    this.output.setStack(0, ItemStack.EMPTY);
                    this.levelCost.set(0);
                    return;
                }
            }
        }
        if (StringUtils.isBlank((CharSequence)this.newItemName)) {
            if (lv.hasCustomName()) {
                k = 1;
                i += k;
                lv2.removeCustomName();
            }
        } else if (!this.newItemName.equals(lv.getName().getString())) {
            k = 1;
            i += k;
            lv2.setCustomName(new LiteralText(this.newItemName));
        }
        this.levelCost.set(j + i);
        if (i <= 0) {
            lv2 = ItemStack.EMPTY;
        }
        if (k == i && k > 0 && this.levelCost.get() >= 40) {
            this.levelCost.set(39);
        }
        if (this.levelCost.get() >= 40 && !this.player.abilities.creativeMode) {
            lv2 = ItemStack.EMPTY;
        }
        if (!lv2.isEmpty()) {
            int w = lv2.getRepairCost();
            if (!lv3.isEmpty() && w < lv3.getRepairCost()) {
                w = lv3.getRepairCost();
            }
            if (k != i || k == 0) {
                w = AnvilScreenHandler.getNextCost(w);
            }
            lv2.setRepairCost(w);
            EnchantmentHelper.set(map, lv2);
        }
        this.output.setStack(0, lv2);
        this.sendContentUpdates();
    }

    public static int getNextCost(int i) {
        return i * 2 + 1;
    }

    public void setNewItemName(String string) {
        this.newItemName = string;
        if (this.getSlot(2).hasStack()) {
            ItemStack lv = this.getSlot(2).getStack();
            if (StringUtils.isBlank((CharSequence)string)) {
                lv.removeCustomName();
            } else {
                lv.setCustomName(new LiteralText(this.newItemName));
            }
        }
        this.updateResult();
    }

    @Environment(value=EnvType.CLIENT)
    public int getLevelCost() {
        return this.levelCost.get();
    }
}

