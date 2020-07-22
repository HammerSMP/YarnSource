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

    public InfestedBlock(Block regularBlock, AbstractBlock.Settings settings) {
        super(settings);
        this.regularBlock = regularBlock;
        REGULAR_TO_INFESTED.put(regularBlock, this);
    }

    public Block getRegularBlock() {
        return this.regularBlock;
    }

    public static boolean isInfestable(BlockState block) {
        return REGULAR_TO_INFESTED.containsKey(block.getBlock());
    }

    private void spawnSilverfish(ServerWorld arg, BlockPos pos) {
        SilverfishEntity lv = EntityType.SILVERFISH.create(arg);
        lv.refreshPositionAndAngles((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, 0.0f, 0.0f);
        arg.spawnEntity(lv);
        lv.playSpawnEffects();
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld arg2, BlockPos pos, ItemStack stack) {
        super.onStacksDropped(state, arg2, pos, stack);
        if (arg2.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            this.spawnSilverfish(arg2, pos);
        }
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        if (world instanceof ServerWorld) {
            this.spawnSilverfish((ServerWorld)world, pos);
        }
    }

    public static BlockState fromRegularBlock(Block regularBlock) {
        return REGULAR_TO_INFESTED.get(regularBlock).getDefaultState();
    }
}

