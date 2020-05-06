/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.api.EnvironmentInterface
 *  net.fabricmc.api.EnvironmentInterfaces
 */
package net.minecraft.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterfaces(value={@EnvironmentInterface(value=EnvType.CLIENT, itf=ChestAnimationProgress.class)})
public class ChestBlockEntity
extends LootableContainerBlockEntity
implements ChestAnimationProgress,
Tickable {
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
    protected float animationAngle;
    protected float lastAnimationAngle;
    protected int viewerCount;
    private int ticksOpen;

    protected ChestBlockEntity(BlockEntityType<?> arg) {
        super(arg);
    }

    public ChestBlockEntity() {
        this(BlockEntityType.CHEST);
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.chest");
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if (!this.deserializeLootTable(arg2)) {
            Inventories.fromTag(arg2, this.inventory);
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        if (!this.serializeLootTable(arg)) {
            Inventories.toTag(arg, this.inventory);
        }
        return arg;
    }

    @Override
    public void tick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        ++this.ticksOpen;
        this.viewerCount = ChestBlockEntity.tickViewerCount(this.world, this, this.ticksOpen, i, j, k, this.viewerCount);
        this.lastAnimationAngle = this.animationAngle;
        float f = 0.1f;
        if (this.viewerCount > 0 && this.animationAngle == 0.0f) {
            this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
        }
        if (this.viewerCount == 0 && this.animationAngle > 0.0f || this.viewerCount > 0 && this.animationAngle < 1.0f) {
            float g = this.animationAngle;
            this.animationAngle = this.viewerCount > 0 ? (this.animationAngle += 0.1f) : (this.animationAngle -= 0.1f);
            if (this.animationAngle > 1.0f) {
                this.animationAngle = 1.0f;
            }
            float h = 0.5f;
            if (this.animationAngle < 0.5f && g >= 0.5f) {
                this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
            }
            if (this.animationAngle < 0.0f) {
                this.animationAngle = 0.0f;
            }
        }
    }

    public static int tickViewerCount(World arg, LockableContainerBlockEntity arg2, int i, int j, int k, int l, int m) {
        if (!arg.isClient && m != 0 && (i + j + k + l) % 200 == 0) {
            m = ChestBlockEntity.countViewers(arg, arg2, j, k, l);
        }
        return m;
    }

    public static int countViewers(World arg, LockableContainerBlockEntity arg2, int i, int j, int k) {
        int l = 0;
        float f = 5.0f;
        List<PlayerEntity> list = arg.getNonSpectatingEntities(PlayerEntity.class, new Box((float)i - 5.0f, (float)j - 5.0f, (float)k - 5.0f, (float)(i + 1) + 5.0f, (float)(j + 1) + 5.0f, (float)(k + 1) + 5.0f));
        for (PlayerEntity lv : list) {
            Inventory lv2;
            if (!(lv.currentScreenHandler instanceof GenericContainerScreenHandler) || (lv2 = ((GenericContainerScreenHandler)lv.currentScreenHandler).getInventory()) != arg2 && (!(lv2 instanceof DoubleInventory) || !((DoubleInventory)lv2).isPart(arg2))) continue;
            ++l;
        }
        return l;
    }

    private void playSound(SoundEvent arg) {
        ChestType lv = this.getCachedState().get(ChestBlock.CHEST_TYPE);
        if (lv == ChestType.LEFT) {
            return;
        }
        double d = (double)this.pos.getX() + 0.5;
        double e = (double)this.pos.getY() + 0.5;
        double f = (double)this.pos.getZ() + 0.5;
        if (lv == ChestType.RIGHT) {
            Direction lv2 = ChestBlock.getFacing(this.getCachedState());
            d += (double)lv2.getOffsetX() * 0.5;
            f += (double)lv2.getOffsetZ() * 0.5;
        }
        this.world.playSound(null, d, e, f, arg, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }

    @Override
    public boolean onSyncedBlockEvent(int i, int j) {
        if (i == 1) {
            this.viewerCount = j;
            return true;
        }
        return super.onSyncedBlockEvent(i, j);
    }

    @Override
    public void onOpen(PlayerEntity arg) {
        if (!arg.isSpectator()) {
            if (this.viewerCount < 0) {
                this.viewerCount = 0;
            }
            ++this.viewerCount;
            this.onInvOpenOrClose();
        }
    }

    @Override
    public void onClose(PlayerEntity arg) {
        if (!arg.isSpectator()) {
            --this.viewerCount;
            this.onInvOpenOrClose();
        }
    }

    protected void onInvOpenOrClose() {
        Block lv = this.getCachedState().getBlock();
        if (lv instanceof ChestBlock) {
            this.world.addSyncedBlockEvent(this.pos, lv, 1, this.viewerCount);
            this.world.updateNeighborsAlways(this.pos, lv);
        }
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return this.inventory;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> arg) {
        this.inventory = arg;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public float getAnimationProgress(float f) {
        return MathHelper.lerp(f, this.lastAnimationAngle, this.animationAngle);
    }

    public static int getPlayersLookingInChestCount(BlockView arg, BlockPos arg2) {
        BlockEntity lv2;
        BlockState lv = arg.getBlockState(arg2);
        if (lv.getBlock().hasBlockEntity() && (lv2 = arg.getBlockEntity(arg2)) instanceof ChestBlockEntity) {
            return ((ChestBlockEntity)lv2).viewerCount;
        }
        return 0;
    }

    public static void copyInventory(ChestBlockEntity arg, ChestBlockEntity arg2) {
        DefaultedList<ItemStack> lv = arg.getInvStackList();
        arg.setInvStackList(arg2.getInvStackList());
        arg2.setInvStackList(lv);
    }

    @Override
    protected ScreenHandler createContainer(int i, PlayerInventory arg) {
        return GenericContainerScreenHandler.createGeneric9x3(i, arg, this);
    }
}

