/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 */
package net.minecraft.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.Vanishable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MiningToolItem
extends ToolItem
implements Vanishable {
    private final Set<Block> effectiveBlocks;
    protected final float miningSpeed;
    private final float attackDamage;
    private final Multimap<EntityAttribute, EntityAttributeModifier> field_23742;

    protected MiningToolItem(float f, float g, ToolMaterial arg, Set<Block> set, Item.Settings arg2) {
        super(arg, arg2);
        this.effectiveBlocks = set;
        this.miningSpeed = arg.getMiningSpeedMultiplier();
        this.attackDamage = f + arg.getAttackDamage();
        ImmutableMultimap.Builder builder = ImmutableMultimap.builder();
        builder.put((Object)EntityAttributes.GENERIC_ATTACK_DAMAGE, (Object)new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_UUID, "Tool modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
        builder.put((Object)EntityAttributes.GENERIC_ATTACK_SPEED, (Object)new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_UUID, "Tool modifier", (double)g, EntityAttributeModifier.Operation.ADDITION));
        this.field_23742 = builder.build();
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack arg, BlockState arg2) {
        return this.effectiveBlocks.contains(arg2.getBlock()) ? this.miningSpeed : 1.0f;
    }

    @Override
    public boolean postHit(ItemStack arg2, LivingEntity arg22, LivingEntity arg3) {
        arg2.damage(2, arg3, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack arg2, World arg22, BlockState arg3, BlockPos arg4, LivingEntity arg5) {
        if (!arg22.isClient && arg3.getHardness(arg22, arg4) != 0.0f) {
            arg2.damage(1, arg5, arg -> arg.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(EquipmentSlot arg) {
        if (arg == EquipmentSlot.MAINHAND) {
            return this.field_23742;
        }
        return super.getModifiers(arg);
    }

    public float method_26366() {
        return this.attackDamage;
    }
}

