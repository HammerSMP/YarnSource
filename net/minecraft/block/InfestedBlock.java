/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package net.minecraft.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class InfestedBlock
extends Block {
    private final Block regularBlock;
    private static final Map<Block, Block> REGULAR_TO_INFESTED = Maps.newIdentityHashMap();

    public InfestedBlock(Block arg, AbstractBlock.Settings arg2) {
        super(arg2);
        this.regularBlock = arg;
        REGULAR_TO_INFESTED.put(arg, this);
    }

    public Block getRegularBlock() {
        return this.regularBlock;
    }

    public static boolean isInfestable(BlockState arg) {
        return REGULAR_TO_INFESTED.containsKey(arg.getBlock());
    }

    private void spawnSilverfish(ServerWorld arg, BlockPos arg2) {
        SilverfishEntity lv = EntityType.SILVERFISH.create(arg);
        lv.refreshPositionAndAngles((double)arg2.getX() + 0.5, arg2.getY(), (double)arg2.getZ() + 0.5, 0.0f, 0.0f);
        arg.spawnEntity(lv);
        lv.playSpawnEffects();
    }

    @Override
    public void onStacksDropped(BlockState arg, ServerWorld arg2, BlockPos arg3, ItemStack arg4) {
        super.onStacksDropped(arg, arg2, arg3, arg4);
        if (arg2.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, arg4) == 0) {
            this.spawnSilverfish(arg2, arg3);
        }
    }

    @Override
    public void onDestroyedByExplosion(World arg, BlockPos arg2, Explosion arg3) {
        if (arg instanceof ServerWorld) {
            this.spawnSilverfish((ServerWorld)arg, arg2);
        }
    }

    public static BlockState fromRegularBlock(Block arg) {
        return REGULAR_TO_INFESTED.get(arg).getDefaultState();
    }
}

