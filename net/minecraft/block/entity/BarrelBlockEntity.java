/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3i;

public class BarrelBlockEntity
extends LootableContainerBlockEntity {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private int viewerCount;

    private BarrelBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    public BarrelBlockEntity() {
        this(BlockEntityType.BARREL);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.serializeLootTable(tag)) {
            Inventories.toTag(tag, this.inventory);
        }
        return tag;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(tag)) {
            Inventories.fromTag(tag, this.inventory);
        }
    }

    @Override
    public int size() {
        return 27;
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
    protected Text getContainerName() {
        return new TranslatableText("container.barrel");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            BlockState lv = this.getCachedState();
            boolean bl = lv.get(BarrelBlock.OPEN);
            if (!bl) {
                this.playSound(lv, SoundEvents.BLOCK_BARREL_OPEN);
                this.setOpen(lv, true);
            }
            this.scheduleUpdate();
        }
    }

    private void scheduleUpdate() {
        this.world.getBlockTickScheduler().schedule(this.getPos(), this.getCachedState().getBlock(), 5);
    }

    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        this.viewerCount = ChestBlockEntity.countViewers(this.world, this, i, j, k);
        if (this.viewerCount > 0) {
            this.scheduleUpdate();
        } else {
            BlockState lv = this.getCachedState();
            if (!lv.isOf(Blocks.BARREL)) {
                this.markRemoved();
                return;
            }
            boolean bl = lv.get(BarrelBlock.OPEN);
            if (bl) {
                this.playSound(lv, SoundEvents.BLOCK_BARREL_CLOSE);
                this.setOpen(lv, false);
            }
        }
    }

    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.viewerCount;
        }
    }

    private void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), (BlockState)state.with(BarrelBlock.OPEN, open), 3);
    }

    private void playSound(BlockState arg, SoundEvent arg2) {
        Vec3i lv = arg.get(BarrelBlock.FACING).getVector();
        double d = (double)this.pos.getX() + 0.5 + (double)lv.getX() / 2.0;
        double e = (double)this.pos.getY() + 0.5 + (double)lv.getY() / 2.0;
        double f = (double)this.pos.getZ() + 0.5 + (double)lv.getZ() / 2.0;
        this.world.playSound(null, d, e, f, arg2, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }
}

