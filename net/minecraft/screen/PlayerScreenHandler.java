/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.screen;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5421;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeFinder;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class PlayerScreenHandler
extends AbstractRecipeScreenHandler<CraftingInventory> {
    public static final Identifier BLOCK_ATLAS_TEXTURE = new Identifier("textures/atlas/blocks.png");
    public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_helmet");
    public static final Identifier EMPTY_CHESTPLATE_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_chestplate");
    public static final Identifier EMPTY_LEGGINGS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_leggings");
    public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = new Identifier("item/empty_armor_slot_boots");
    public static final Identifier EMPTY_OFFHAND_ARMOR_SLOT = new Identifier("item/empty_armor_slot_shield");
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private final CraftingInventory craftingInput = new CraftingInventory(this, 2, 2);
    private final CraftingResultInventory craftingResult = new CraftingResultInventory();
    public final boolean onServer;
    private final PlayerEntity owner;

    public PlayerScreenHandler(PlayerInventory arg, boolean bl, PlayerEntity arg2) {
        super(null, 0);
        this.onServer = bl;
        this.owner = arg2;
        this.addSlot(new CraftingResultSlot(arg.player, this.craftingInput, this.craftingResult, 0, 154, 28));
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 2; ++j) {
                this.addSlot(new Slot(this.craftingInput, j + i * 2, 98 + j * 18, 18 + i * 18));
            }
        }
        for (int k = 0; k < 4; ++k) {
            final EquipmentSlot lv = EQUIPMENT_SLOT_ORDER[k];
            this.addSlot(new Slot(arg, 39 - k, 8, 8 + k * 18){

                @Override
                public int getMaxStackAmount() {
                    return 1;
                }

                @Override
                public boolean canInsert(ItemStack arg) {
                    return lv == MobEntity.getPreferredEquipmentSlot(arg);
                }

                @Override
                public boolean canTakeItems(PlayerEntity arg) {
                    ItemStack lv2 = this.getStack();
                    if (!lv2.isEmpty() && !arg.isCreative() && EnchantmentHelper.hasBindingCurse(lv2)) {
                        return false;
                    }
                    return super.canTakeItems(arg);
                }

                @Override
                @Environment(value=EnvType.CLIENT)
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of((Object)BLOCK_ATLAS_TEXTURE, (Object)EMPTY_ARMOR_SLOT_TEXTURES[lv.getEntitySlotId()]);
                }
            });
        }
        for (int l = 0; l < 3; ++l) {
            for (int m = 0; m < 9; ++m) {
                this.addSlot(new Slot(arg, m + (l + 1) * 9, 8 + m * 18, 84 + l * 18));
            }
        }
        for (int n = 0; n < 9; ++n) {
            this.addSlot(new Slot(arg, n, 8 + n * 18, 142));
        }
        this.addSlot(new Slot(arg, 40, 77, 62){

            @Override
            @Environment(value=EnvType.CLIENT)
            public Pair<Identifier, Identifier> getBackgroundSprite() {
                return Pair.of((Object)BLOCK_ATLAS_TEXTURE, (Object)EMPTY_OFFHAND_ARMOR_SLOT);
            }
        });
    }

    @Override
    public void populateRecipeFinder(RecipeFinder arg) {
        this.craftingInput.provideRecipeInputs(arg);
    }

    @Override
    public void clearCraftingSlots() {
        this.craftingResult.clear();
        this.craftingInput.clear();
    }

    @Override
    public boolean matches(Recipe<? super CraftingInventory> arg) {
        return arg.matches(this.craftingInput, this.owner.world);
    }

    @Override
    public void onContentChanged(Inventory arg) {
        CraftingScreenHandler.updateResult(this.syncId, this.owner.world, this.owner, this.craftingInput, this.craftingResult);
    }

    @Override
    public void close(PlayerEntity arg) {
        super.close(arg);
        this.craftingResult.clear();
        if (arg.world.isClient) {
            return;
        }
        this.dropInventory(arg, arg.world, this.craftingInput);
    }

    @Override
    public boolean canUse(PlayerEntity arg) {
        return true;
    }

    @Override
    public ItemStack transferSlot(PlayerEntity arg, int i) {
        ItemStack lv = ItemStack.EMPTY;
        Slot lv2 = (Slot)this.slots.get(i);
        if (lv2 != null && lv2.hasStack()) {
            int j;
            ItemStack lv3 = lv2.getStack();
            lv = lv3.copy();
            EquipmentSlot lv4 = MobEntity.getPreferredEquipmentSlot(lv);
            if (i == 0) {
                if (!this.insertItem(lv3, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
                lv2.onStackChanged(lv3, lv);
            } else if (i >= 1 && i < 5 ? !this.insertItem(lv3, 9, 45, false) : (i >= 5 && i < 9 ? !this.insertItem(lv3, 9, 45, false) : (lv4.getType() == EquipmentSlot.Type.ARMOR && !((Slot)this.slots.get(8 - lv4.getEntitySlotId())).hasStack() ? !this.insertItem(lv3, j = 8 - lv4.getEntitySlotId(), j + 1, false) : (lv4 == EquipmentSlot.OFFHAND && !((Slot)this.slots.get(45)).hasStack() ? !this.insertItem(lv3, 45, 46, false) : (i >= 9 && i < 36 ? !this.insertItem(lv3, 36, 45, false) : (i >= 36 && i < 45 ? !this.insertItem(lv3, 9, 36, false) : !this.insertItem(lv3, 9, 45, false))))))) {
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
            ItemStack lv5 = lv2.onTakeItem(arg, lv3);
            if (i == 0) {
                arg.dropItem(lv5, false);
            }
        }
        return lv;
    }

    @Override
    public boolean canInsertIntoSlot(ItemStack arg, Slot arg2) {
        return arg2.inventory != this.craftingResult && super.canInsertIntoSlot(arg, arg2);
    }

    @Override
    public int getCraftingResultSlotIndex() {
        return 0;
    }

    @Override
    public int getCraftingWidth() {
        return this.craftingInput.getWidth();
    }

    @Override
    public int getCraftingHeight() {
        return this.craftingInput.getHeight();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getCraftingSlotCount() {
        return 5;
    }

    public CraftingInventory method_29281() {
        return this.craftingInput;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public class_5421 method_30264() {
        return class_5421.CRAFTING;
    }
}

