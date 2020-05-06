/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.vehicle;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class HopperMinecartEntity
extends StorageMinecartEntity
implements Hopper {
    private boolean enabled = true;
    private int transferCooldown = -1;
    private final BlockPos currentBlockPos = BlockPos.ORIGIN;

    public HopperMinecartEntity(EntityType<? extends HopperMinecartEntity> arg, World arg2) {
        super(arg, arg2);
    }

    public HopperMinecartEntity(World arg, double d, double e, double f) {
        super(EntityType.HOPPER_MINECART, d, e, f, arg);
    }

    @Override
    public AbstractMinecartEntity.Type getMinecartType() {
        return AbstractMinecartEntity.Type.HOPPER;
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.HOPPER.getDefaultState();
    }

    @Override
    public int getDefaultBlockOffset() {
        return 1;
    }

    @Override
    public int size() {
        return 5;
    }

    @Override
    public void onActivatorRail(int i, int j, int k, boolean bl) {
        boolean bl2;
        boolean bl3 = bl2 = !bl;
        if (bl2 != this.isEnabled()) {
            this.setEnabled(bl2);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean bl) {
        this.enabled = bl;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public double getHopperX() {
        return this.getX();
    }

    @Override
    public double getHopperY() {
        return this.getY() + 0.5;
    }

    @Override
    public double getHopperZ() {
        return this.getZ();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClient && this.isAlive() && this.isEnabled()) {
            BlockPos lv = this.getBlockPos();
            if (lv.equals(this.currentBlockPos)) {
                --this.transferCooldown;
            } else {
                this.setTransferCooldown(0);
            }
            if (!this.isCoolingDown()) {
                this.setTransferCooldown(0);
                if (this.canOperate()) {
                    this.setTransferCooldown(4);
                    this.markDirty();
                }
            }
        }
    }

    public boolean canOperate() {
        if (HopperBlockEntity.extract(this)) {
            return true;
        }
        List<Entity> list = this.world.getEntities(ItemEntity.class, this.getBoundingBox().expand(0.25, 0.0, 0.25), EntityPredicates.VALID_ENTITY);
        if (!list.isEmpty()) {
            HopperBlockEntity.extract(this, (ItemEntity)list.get(0));
        }
        return false;
    }

    @Override
    public void dropItems(DamageSource arg) {
        super.dropItems(arg);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            this.dropItem(Blocks.HOPPER);
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        arg.putInt("TransferCooldown", this.transferCooldown);
        arg.putBoolean("Enabled", this.enabled);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        this.transferCooldown = arg.getInt("TransferCooldown");
        this.enabled = arg.contains("Enabled") ? arg.getBoolean("Enabled") : true;
    }

    public void setTransferCooldown(int i) {
        this.transferCooldown = i;
    }

    public boolean isCoolingDown() {
        return this.transferCooldown > 0;
    }

    @Override
    public ScreenHandler getScreenHandler(int i, PlayerInventory arg) {
        return new HopperScreenHandler(i, arg, this);
    }
}

