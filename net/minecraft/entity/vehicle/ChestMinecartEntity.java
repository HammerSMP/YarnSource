/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class ChestMinecartEntity
extends StorageMinecartEntity {
    public ChestMinecartEntity(EntityType<? extends ChestMinecartEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public ChestMinecartEntity(World arg, double d, double e, double f) {
        super(EntityType.CHEST_MINECART, d, e, f, arg);
    }

    @Override
    public void dropItems(DamageSource arg) {
        super.dropItems(arg);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(Blocks.CHEST);
        }
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.CHEST;
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return (BlockState)Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.NORTH);
    }

    @Override
    public int getDefaultBlockOffset() {
        return 8;
    }

    @Override
    public ScreenHandler getScreenHandler(int i, PlayerInventory arg) {
        return GenericContainerScreenHandler.createGeneric9x3(i, arg, this);
    }
}

