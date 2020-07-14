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
import net.minecraft.util.ActionResult;
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

    public ItemFrameEntity(World world, BlockPos pos, Direction direction) {
        super(EntityType.ITEM_FRAME, world, pos);
        this.setFacing(direction);
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.0f;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
        this.getDataTracker().startTracking(ROTATION, 0);
    }

    @Override
    protected void setFacing(Direction facing) {
        Validate.notNull((Object)facing);
        this.facing = facing;
        if (facing.getAxis().isHorizontal()) {
            this.pitch = 0.0f;
            this.yaw = this.facing.getHorizontal() * 90;
        } else {
            this.pitch = -90 * facing.getDirection().offset();
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
    public void move(MovementType type, Vec3d movement) {
        if (!this.fixed) {
            super.move(type, movement);
        }
    }

    @Override
    public void addVelocity(double deltaX, double deltaY, double deltaZ) {
        if (!this.fixed) {
            super.addVelocity(deltaX, deltaY, deltaZ);
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
    public boolean damage(DamageSource source, float amount) {
        if (this.fixed) {
            if (source == DamageSource.OUT_OF_WORLD || source.isSourceCreativePlayer()) {
                return super.damage(source, amount);
            }
            return false;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!source.isExplosive() && !this.getHeldItemStack().isEmpty()) {
            if (!this.world.isClient) {
                this.dropHeldStack(source.getAttacker(), false);
                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.0f);
            }
            return true;
        }
        return super.damage(source, amount);
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
    public boolean shouldRender(double distance) {
        double e = 16.0;
        return distance < (e *= 64.0 * ItemFrameEntity.getRenderDistanceMultiplier()) * e;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0f, 1.0f);
        this.dropHeldStack(entity, true);
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0f, 1.0f);
    }

    private void dropHeldStack(@Nullable Entity entity, boolean alwaysDrop) {
        if (this.fixed) {
            return;
        }
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (entity == null) {
                this.removeFromFrame(this.getHeldItemStack());
            }
            return;
        }
        ItemStack lv = this.getHeldItemStack();
        this.setHeldItemStack(ItemStack.EMPTY);
        if (entity instanceof PlayerEntity) {
            PlayerEntity lv2 = (PlayerEntity)entity;
            if (lv2.abilities.creativeMode) {
                this.removeFromFrame(lv);
                return;
            }
        }
        if (alwaysDrop) {
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

    private void removeFromFrame(ItemStack map) {
        if (map.getItem() == Items.FILLED_MAP) {
            MapState lv = FilledMapItem.getOrCreateMapState(map, this.world);
            lv.removeFrame(this.attachmentPos, this.getEntityId());
            lv.setDirty(true);
        }
        map.setHolder(null);
    }

    public ItemStack getHeldItemStack() {
        return this.getDataTracker().get(ITEM_STACK);
    }

    public void setHeldItemStack(ItemStack stack) {
        this.setHeldItemStack(stack, true);
    }

    public void setHeldItemStack(ItemStack value, boolean update) {
        if (!value.isEmpty()) {
            value = value.copy();
            value.setCount(1);
            value.setHolder(this);
        }
        this.getDataTracker().set(ITEM_STACK, value);
        if (!value.isEmpty()) {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0f, 1.0f);
        }
        if (update && this.attachmentPos != null) {
            this.world.updateComparators(this.attachmentPos, Blocks.AIR);
        }
    }

    @Override
    public boolean equip(int slot, ItemStack item) {
        if (slot == 0) {
            this.setHeldItemStack(item);
            return true;
        }
        return false;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        ItemStack lv;
        if (data.equals(ITEM_STACK) && !(lv = this.getHeldItemStack()).isEmpty() && lv.getFrame() != this) {
            lv.setHolder(this);
        }
    }

    public int getRotation() {
        return this.getDataTracker().get(ROTATION);
    }

    public void setRotation(int value) {
        this.setRotation(value, true);
    }

    private void setRotation(int value, boolean bl) {
        this.getDataTracker().set(ROTATION, value % 8);
        if (bl && this.attachmentPos != null) {
            this.world.updateComparators(this.attachmentPos, Blocks.AIR);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        if (!this.getHeldItemStack().isEmpty()) {
            tag.put("Item", this.getHeldItemStack().toTag(new CompoundTag()));
            tag.putByte("ItemRotation", (byte)this.getRotation());
            tag.putFloat("ItemDropChance", this.itemDropChance);
        }
        tag.putByte("Facing", (byte)this.facing.getId());
        tag.putBoolean("Invisible", this.isInvisible());
        tag.putBoolean("Fixed", this.fixed);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        CompoundTag lv = tag.getCompound("Item");
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
            this.setRotation(tag.getByte("ItemRotation"), false);
            if (tag.contains("ItemDropChance", 99)) {
                this.itemDropChance = tag.getFloat("ItemDropChance");
            }
        }
        this.setFacing(Direction.byId(tag.getByte("Facing")));
        this.setInvisible(tag.getBoolean("Invisible"));
        this.fixed = tag.getBoolean("Fixed");
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        boolean bl2;
        ItemStack lv = player.getStackInHand(hand);
        boolean bl = !this.getHeldItemStack().isEmpty();
        boolean bl3 = bl2 = !lv.isEmpty();
        if (this.fixed) {
            return ActionResult.PASS;
        }
        if (this.world.isClient) {
            return bl || bl2 ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        if (!bl) {
            if (bl2 && !this.removed) {
                this.setHeldItemStack(lv);
                if (!player.abilities.creativeMode) {
                    lv.decrement(1);
                }
            }
        } else {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0f, 1.0f);
            this.setRotation(this.getRotation() + 1);
        }
        return ActionResult.CONSUME;
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

