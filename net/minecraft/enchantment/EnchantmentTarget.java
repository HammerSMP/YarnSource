/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.Item;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.Vanishable;
import net.minecraft.item.Wearable;

public enum EnchantmentTarget {
    ARMOR{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof ArmorItem;
        }
    }
    ,
    ARMOR_FEET{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof ArmorItem && ((ArmorItem)arg).getSlotType() == EquipmentSlot.FEET;
        }
    }
    ,
    ARMOR_LEGS{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof ArmorItem && ((ArmorItem)arg).getSlotType() == EquipmentSlot.LEGS;
        }
    }
    ,
    ARMOR_CHEST{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof ArmorItem && ((ArmorItem)arg).getSlotType() == EquipmentSlot.CHEST;
        }
    }
    ,
    ARMOR_HEAD{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof ArmorItem && ((ArmorItem)arg).getSlotType() == EquipmentSlot.HEAD;
        }
    }
    ,
    WEAPON{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof SwordItem;
        }
    }
    ,
    DIGGER{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof MiningToolItem;
        }
    }
    ,
    FISHING_ROD{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof FishingRodItem;
        }
    }
    ,
    TRIDENT{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof TridentItem;
        }
    }
    ,
    BREAKABLE{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg.isDamageable();
        }
    }
    ,
    BOW{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof BowItem;
        }
    }
    ,
    WEARABLE{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof Wearable || Block.getBlockFromItem(arg) instanceof Wearable;
        }
    }
    ,
    CROSSBOW{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof CrossbowItem;
        }
    }
    ,
    VANISHABLE{

        @Override
        public boolean isAcceptableItem(Item arg) {
            return arg instanceof Vanishable || Block.getBlockFromItem(arg) instanceof Vanishable || BREAKABLE.isAcceptableItem(arg);
        }
    };


    public abstract boolean isAcceptableItem(Item var1);
}

