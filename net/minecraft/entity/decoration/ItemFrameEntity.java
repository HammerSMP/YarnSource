/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.Validate
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.entity.decoration;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemFrameEntity
extends AbstractDecorationEntity {
    private static final Logger ITEM_FRAME_LOGGER = LogManager.getLogger();
    private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> ROTATION = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private float itemDropChance = 1.0f;
    private boolean fixed;

    public ItemFrameEntity(EntityType<? extends ItemFrameEntity> arg, World arg2) {
        super((EntityType<? extends AbstractDecorationEntity>)arg, arg2);
    }

    public ItemFrameEntity(World arg, BlockPos arg2, Direction arg3) {
        super(EntityType.ITEM_FRAME, arg, arg2);
        this.setFacing(arg3);
    }

    @Override
    protected float getEyeHeight(EntityPose arg, EntityDimensions arg2) {
        return 0.0f;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
        this.getDataTracker().startTracking(ROTATION, 0);
    }

    @Override
    protected void setFacing(Direction arg) {
        Validate.notNull((Object)arg);
        this.facing = arg;
        if (arg.getAxis().isHorizontal()) {
            this.pitch = 0.0f;
            this.yaw = this.facing.getHorizontal() * 90;
        } else {
            this.pitch = -90 * arg.getDirection().offset();
            this.yaw = 0.0f;
        }
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.updateAttachmentPosition();
    }

    @Override
    protected void updateAttachmentPosition() {
        if (this.facing == null) {
            return;
        }
        double d = 0.46875;
        double e = (double)this.attachmentPos.getX() + 0.5 - (double)this.facing.getOffsetX() * 0.46875;
        double f = (double)this.attachmentPos.getY() + 0.5 - (double)this.facing.getOffsetY() * 0.46875;
        double g = (double)this.attachmentPos.getZ() + 0.5 - (double)this.facing.getOffsetZ() * 0.46875;
        this.setPos(e, f, g);
        double h = this.getWidthPixels();
        double i = this.getHeightPixels();
        double j = this.getWidthPixels();
        Direction.Axis lv = this.facing.getAxis();
        switch (lv) {
            case X: {
                h = 1.0;
                break;
            }
            case Y: {
                i = 1.0;
                break;
            }
            case Z: {
                j = 1.0;
            }
        }
        this.setBoundingBox(new Box(e - (h /= 32.0), f - (i /= 32.0), g - (j /= 32.0), e + h, f + i, g + j));
    }

    @Override
    public boolean canStayAttached() {
        if (this.fixed) {
            return true;
        }
        if (!this.world.doesNotCollide(this)) {
            return false;
        }
        BlockState lv = this.world.getBlockState(this.attachmentPos.offset(this.facing.getOpposite()));
        if (!(lv.getMaterial().isSolid() || this.facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(lv))) {
            return false;
        }
        return this.world.getEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
    }

    @Override
    public void move(MovementType arg, Vec3d arg2) {
        if (!this.fixed) {
            super.move(arg, arg2);
        }
    }

    @Override
    public void addVelocity(double d, double e, double f) {
        if (!this.fixed) {
            super.addVelocity(d, e, f);
        }
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    public void kill() {
        this.removeFromFrame(this.getHeldItemStack());
        super.kill();
    }

    @Override
    public boolean damage(DamageSource arg, float f) {
        if (this.fixed) {
            if (arg == DamageSource.OUT_OF_WORLD || arg.isSourceCreativePlayer()) {
                return super.damage(arg, f);
            }
            return false;
        }
        if (this.isInvulnerableTo(arg)) {
            return false;
        }
        if (!arg.isExplosive() && !this.getHeldItemStack().isEmpty()) {
            if (!this.world.isClient) {
                this.dropHeldStack(arg.getAttacker(), false);
                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.0f);
            }
            return true;
        }
        return super.damage(arg, f);
    }

    @Override
    public int getWidthPixels() {
        return 12;
    }

    @Override
    public int getHeightPixels() {
        return 12;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double d) {
        double e = 16.0;
        return d < (e *= 64.0 * ItemFrameEntity.getRenderDistanceMultiplier()) * e;
    }

    @Override
    public void onBreak(@Nullable Entity arg) {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0f, 1.0f);
        this.dropHeldStack(arg, true);
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0f, 1.0f);
    }

    private void dropHeldStack(@Nullable Entity arg, boolean bl) {
        if (this.fixed) {
            return;
        }
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (arg == null) {
                this.removeFromFrame(this.getHeldItemStack());
            }
            return;
        }
        ItemStack lv = this.getHeldItemStack();
        this.setHeldItemStack(ItemStack.EMPTY);
        if (arg instanceof PlayerEntity) {
            PlayerEntity lv2 = (PlayerEntity)arg;
            if (lv2.abilities.creativeMode) {
                this.removeFromFrame(lv);
                return;
            }
        }
        if (bl) {
            this.dropItem(Items.ITEM_FRAME);
        }
        if (!lv.isEmpty()) {
            lv = lv.copy();
            this.removeFromFrame(lv);
            if (this.random.nextFloat() < this.itemDropChance) {
                this.dropStack(lv);
            }
        }
    }

    private void removeFromFrame(ItemStack arg) {
        if (arg.getItem() == Items.FILLED_MAP) {
            MapState lv = FilledMapItem.getOrCreateMapState(arg, this.world);
            lv.removeFrame(this.attachmentPos, this.getEntityId());
            lv.setDirty(true);
        }
        arg.setHolder(null);
    }

    public ItemStack getHeldItemStack() {
        return this.getDataTracker().get(ITEM_STACK);
    }

    public void setHeldItemStack(ItemStack arg) {
        this.setHeldItemStack(arg, true);
    }

    public void setHeldItemStack(ItemStack arg, boolean bl) {
        if (!arg.isEmpty()) {
            arg = arg.copy();
            arg.setCount(1);
            arg.setHolder(this);
        }
        this.getDataTracker().set(ITEM_STACK, arg);
        if (!arg.isEmpty()) {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0f, 1.0f);
        }
        if (bl && this.attachmentPos != null) {
            this.world.updateComparators(this.attachmentPos, Blocks.AIR);
        }
    }

    @Override
    public boolean equip(int i, ItemStack arg) {
        if (i == 0) {
            this.setHeldItemStack(arg);
            return true;
        }
        return false;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> arg) {
        ItemStack lv;
        if (arg.equals(ITEM_STACK) && !(lv = this.getHeldItemStack()).isEmpty() && lv.getFrame() != this) {
            lv.setHolder(this);
        }
    }

    public int getRotation() {
        return this.getDataTracker().get(ROTATION);
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean bl) {
        this.getDataTracker().set(ROTATION, i % 8);
        if (bl && this.attachmentPos != null) {
            this.world.updateComparators(this.attachmentPos, Blocks.AIR);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag arg) {
        super.writeCustomDataToTag(arg);
        if (!this.getHeldItemStack().isEmpty()) {
            arg.put("Item", this.getHeldItemStack().toTag(new CompoundTag()));
            arg.putByte("ItemRotation", (byte)this.getRotation());
            arg.putFloat("ItemDropChance", this.itemDropChance);
        }
        arg.putByte("Facing", (byte)this.facing.getId());
        arg.putBoolean("Invisible", this.isInvisible());
        arg.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag arg) {
        super.readCustomDataFromTag(arg);
        CompoundTag lv = arg.getCompound("Item");
        if (lv != null && !lv.isEmpty()) {
            ItemStack lv3;
            ItemStack lv2 = ItemStack.fromTag(lv);
            if (lv2.isEmpty()) {
                ITEM_FRAME_LOGGER.warn("Unable to load item from: {}", (Object)lv);
            }
            if (!(lv3 = this.getHeldItemStack()).isEmpty() && !ItemStack.areEqual(lv2, lv3)) {
                this.removeFromFrame(lv3);
            }
            this.setHeldItemStack(lv2, false);
            this.setRotation(arg.getByte("ItemRotation"), false);
            if (arg.contains("ItemDropChance", 99)) {
                this.itemDropChance = arg.getFloat("ItemDropChance");
            }
        }
        this.setFacing(Direction.byId(arg.getByte("Facing")));
        this.setInvisible(arg.getBoolean("Invisible"));
        this.fixed = arg.getBoolean("Fixed");
    }

    @Override
    public boolean interact(PlayerEntity arg, Hand arg2) {
        boolean bl2;
        ItemStack lv = arg.getStackInHand(arg2);
        boolean bl = !this.getHeldItemStack().isEmpty();
        boolean bl3 = bl2 = !lv.isEmpty();
        if (this.world.isClient) {
            return bl || bl2;
        }
        if (!this.fixed && !bl) {
            if (bl2 && !this.removed) {
                this.setHeldItemStack(lv);
                if (!arg.abilities.creativeMode) {
                    lv.decrement(1);
                }
            }
        } else {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0f, 1.0f);
            this.setRotation(this.getRotation() + 1);
        }
        return true;
    }

    public int getComparatorPower() {
        if (this.getHeldItemStack().isEmpty()) {
            return 0;
        }
        return this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), this.facing.getId(), this.getDecorationBlockPos());
    }
}

