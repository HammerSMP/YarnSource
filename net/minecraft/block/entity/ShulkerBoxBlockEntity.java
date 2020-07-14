/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.block.entity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

public class ShulkerBoxBlockEntity
extends LootableContainerBlockEntity
implements SidedInventory,
Tickable {
    private static final int[] AVAILABLE_SLOTS = IntStream.range(0, 27).toArray();
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private int viewerCount;
    private AnimationStage animationStage = AnimationStage.CLOSED;
    private float animationProgress;
    private float prevAnimationProgress;
    @Nullable
    private DyeColor cachedColor;
    private boolean cachedColorUpdateNeeded;

    public ShulkerBoxBlockEntity(@Nullable DyeColor color) {
        super(BlockEntityType.SHULKER_BOX);
        this.cachedColor = color;
    }

    public ShulkerBoxBlockEntity() {
        this((DyeColor)null);
        this.cachedColorUpdateNeeded = true;
    }

    @Override
    public void tick() {
        this.updateAnimation();
        if (this.animationStage == AnimationStage.OPENING || this.animationStage == AnimationStage.CLOSING) {
            this.pushEntities();
        }
    }

    protected void updateAnimation() {
        this.prevAnimationProgress = this.animationProgress;
        switch (this.animationStage) {
            case CLOSED: {
                this.animationProgress = 0.0f;
                break;
            }
            case OPENING: {
                this.animationProgress += 0.1f;
                if (!(this.animationProgress >= 1.0f)) break;
                this.pushEntities();
                this.animationStage = AnimationStage.OPENED;
                this.animationProgress = 1.0f;
                this.updateNeighborStates();
                break;
            }
            case CLOSING: {
                this.animationProgress -= 0.1f;
                if (!(this.animationProgress <= 0.0f)) break;
                this.animationStage = AnimationStage.CLOSED;
                this.animationProgress = 0.0f;
                this.updateNeighborStates();
                break;
            }
            case OPENED: {
                this.animationProgress = 1.0f;
            }
        }
    }

    public AnimationStage getAnimationStage() {
        return this.animationStage;
    }

    public Box getBoundingBox(BlockState state) {
        return this.getBoundingBox(state.get(ShulkerBoxBlock.FACING));
    }

    public Box getBoundingBox(Direction openDirection) {
        float f = this.getAnimationProgress(1.0f);
        return VoxelShapes.fullCube().getBoundingBox().stretch(0.5f * f * (float)openDirection.getOffsetX(), 0.5f * f * (float)openDirection.getOffsetY(), 0.5f * f * (float)openDirection.getOffsetZ());
    }

    private Box getCollisionBox(Direction facing) {
        Direction lv = facing.getOpposite();
        return this.getBoundingBox(facing).shrink(lv.getOffsetX(), lv.getOffsetY(), lv.getOffsetZ());
    }

    private void pushEntities() {
        BlockState lv = this.world.getBlockState(this.getPos());
        if (!(lv.getBlock() instanceof ShulkerBoxBlock)) {
            return;
        }
        Direction lv2 = lv.get(ShulkerBoxBlock.FACING);
        Box lv3 = this.getCollisionBox(lv2).offset(this.pos);
        List<Entity> list = this.world.getEntities(null, lv3);
        if (list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); ++i) {
            Entity lv4 = list.get(i);
            if (lv4.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            double d = 0.0;
            double e = 0.0;
            double f = 0.0;
            Box lv5 = lv4.getBoundingBox();
            switch (lv2.getAxis()) {
                case X: {
                    d = lv2.getDirection() == Direction.AxisDirection.POSITIVE ? lv3.maxX - lv5.minX : lv5.maxX - lv3.minX;
                    d += 0.01;
                    break;
                }
                case Y: {
                    e = lv2.getDirection() == Direction.AxisDirection.POSITIVE ? lv3.maxY - lv5.minY : lv5.maxY - lv3.minY;
                    e += 0.01;
                    break;
                }
                case Z: {
                    f = lv2.getDirection() == Direction.AxisDirection.POSITIVE ? lv3.maxZ - lv5.minZ : lv5.maxZ - lv3.minZ;
                    f += 0.01;
                }
            }
            lv4.move(MovementType.SHULKER_BOX, new Vec3d(d * (double)lv2.getOffsetX(), e * (double)lv2.getOffsetY(), f * (double)lv2.getOffsetZ()));
        }
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean onSyncedBlockEvent(int type, int data) {
        if (type == 1) {
            this.viewerCount = data;
            if (data == 0) {
                this.animationStage = AnimationStage.CLOSING;
                this.updateNeighborStates();
            }
            if (data == 1) {
                this.animationStage = AnimationStage.OPENING;
                this.updateNeighborStates();
            }
            return true;
        }
        return super.onSyncedBlockEvent(type, data);
    }

    private void updateNeighborStates() {
        this.getCachedState().method_30101(this.getWorld(), this.getPos(), 3);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount == 1) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
            this.world.addSyncedBlockEvent(this.pos, this.getCachedState().getBlock(), 1, this.viewerCount);
            if (this.viewerCount <= 0) {
                this.world.playSound(null, this.pos, SoundEvents.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.shulkerBox");
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.deserializeInventory(tag);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        return this.serializeInventory(tag);
    }

    public void deserializeInventory(CompoundTag tag) {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag) && tag.contains("Items", 9)) {
            Inventories.fromTag(tag, this.inventory);
        }
    }

    public CompoundTag serializeInventory(CompoundTag tag) {
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory, false);
        }
        return tag;
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.inventory = list;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return !(Block.getBlockFromItem(stack.getItem()) instanceof ShulkerBoxBlock);
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }

    public float getAnimationProgress(float f) {
        return MathHelper.lerp(f, this.prevAnimationProgress, this.animationProgress);
    }

    @Nullable
    @Environment(value=EnvType.CLIENT)
    public DyeColor getColor() {
        if (this.cachedColorUpdateNeeded) {
            this.cachedColor = ShulkerBoxBlock.getColor(this.getCachedState().getBlock());
            this.cachedColorUpdateNeeded = false;
        }
        return this.cachedColor;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new ShulkerBoxScreenHandler(syncId, playerInventory, this);
    }

    public boolean suffocates() {
        return this.animationStage == AnimationStage.CLOSED;
    }

    public static enum AnimationStage {
        CLOSED,
        OPENING,
        OPENED,
        CLOSING;

    }
}

