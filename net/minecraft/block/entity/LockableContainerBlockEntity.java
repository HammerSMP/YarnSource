/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Nameable;

public abstract class LockableContainerBlockEntity
extends BlockEntity
implements Inventory,
NamedScreenHandlerFactory,
Nameable {
    private ContainerLock lock = ContainerLock.EMPTY;
    private Text customName;

    protected LockableContainerBlockEntity(BlockEntityType<?> arg) {
        super(arg);
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.lock = ContainerLock.fromTag(arg2);
        if (arg2.contains("CustomName", 8)) {
            this.customName = Text.Serializer.fromJson(arg2.getString("CustomName"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        this.lock.toTag(arg);
        if (this.customName != null) {
            arg.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        return arg;
    }

    public void setCustomName(Text arg) {
        this.customName = arg;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return this.getContainerName();
    }

    @Override
    public Text getDisplayName() {
        return this.getName();
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }

    protected abstract Text getContainerName();

    public boolean checkUnlocked(PlayerEntity arg) {
        return LockableContainerBlockEntity.checkUnlocked(arg, this.lock, this.getDisplayName());
    }

    public static boolean checkUnlocked(PlayerEntity arg, ContainerLock arg2, Text arg3) {
        if (arg.isSpectator() || arg2.canOpen(arg.getMainHandStack())) {
            return true;
        }
        arg.sendMessage(new TranslatableText("container.isLocked", arg3), true);
        arg.playSound(SoundEvents.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return false;
    }

    @Override
    @Nullable
    public ScreenHandler createMenu(int i, PlayerInventory arg, PlayerEntity arg2) {
        if (this.checkUnlocked(arg2)) {
            return this.createContainer(i, arg);
        }
        return null;
    }

    protected abstract ScreenHandler createContainer(int var1, PlayerInventory var2);
}

