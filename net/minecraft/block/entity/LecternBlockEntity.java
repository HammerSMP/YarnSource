/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.LecternBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Clearable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LecternBlockEntity
extends BlockEntity
implements Clearable,
NamedScreenHandlerFactory {
    private final Inventory inventory = new Inventory(){

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return LecternBlockEntity.this.book.isEmpty();
        }

        @Override
        public ItemStack getStack(int slot) {
            return slot == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            if (slot == 0) {
                ItemStack lv = LecternBlockEntity.this.book.split(amount);
                if (LecternBlockEntity.this.book.isEmpty()) {
                    LecternBlockEntity.this.onBookRemoved();
                }
                return lv;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int slot) {
            if (slot == 0) {
                ItemStack lv = LecternBlockEntity.this.book;
                LecternBlockEntity.this.book = ItemStack.EMPTY;
                LecternBlockEntity.this.onBookRemoved();
                return lv;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {
        }

        @Override
        public int getMaxCountPerStack() {
            return 1;
        }

        @Override
        public void markDirty() {
            LecternBlockEntity.this.markDirty();
        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            if (LecternBlockEntity.this.world.getBlockEntity(LecternBlockEntity.this.pos) != LecternBlockEntity.this) {
                return false;
            }
            if (player.squaredDistanceTo((double)LecternBlockEntity.this.pos.getX() + 0.5, (double)LecternBlockEntity.this.pos.getY() + 0.5, (double)LecternBlockEntity.this.pos.getZ() + 0.5) > 64.0) {
                return false;
            }
            return LecternBlockEntity.this.hasBook();
        }

        @Override
        public boolean isValid(int slot, ItemStack stack) {
            return false;
        }

        @Override
        public void clear() {
        }
    };
    private final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int index) {
            return index == 0 ? LecternBlockEntity.this.currentPage : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                LecternBlockEntity.this.setCurrentPage(value);
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };
    private ItemStack book = ItemStack.EMPTY;
    private int currentPage;
    private int pageCount;

    public LecternBlockEntity() {
        super(BlockEntityType.LECTERN);
    }

    public ItemStack getBook() {
        return this.book;
    }

    public boolean hasBook() {
        Item lv = this.book.getItem();
        return lv == Items.WRITABLE_BOOK || lv == Items.WRITTEN_BOOK;
    }

    public void setBook(ItemStack book) {
        this.setBook(book, null);
    }

    private void onBookRemoved() {
        this.currentPage = 0;
        this.pageCount = 0;
        LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getCachedState(), false);
    }

    public void setBook(ItemStack book, @Nullable PlayerEntity player) {
        this.book = this.resolveBook(book, player);
        this.currentPage = 0;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.markDirty();
    }

    private void setCurrentPage(int currentPage) {
        int j = MathHelper.clamp(currentPage, 0, this.pageCount - 1);
        if (j != this.currentPage) {
            this.currentPage = j;
            this.markDirty();
            LecternBlock.setPowered(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getComparatorOutput() {
        float f = this.pageCount > 1 ? (float)this.getCurrentPage() / ((float)this.pageCount - 1.0f) : 1.0f;
        return MathHelper.floor(f * 14.0f) + (this.hasBook() ? 1 : 0);
    }

    private ItemStack resolveBook(ItemStack book, @Nullable PlayerEntity player) {
        if (this.world instanceof ServerWorld && book.getItem() == Items.WRITTEN_BOOK) {
            WrittenBookItem.resolve(book, this.getCommandSource(player), player);
        }
        return book;
    }

    private ServerCommandSource getCommandSource(@Nullable PlayerEntity player) {
        Text lv2;
        String string2;
        if (player == null) {
            String string = "Lectern";
            LiteralText lv = new LiteralText("Lectern");
        } else {
            string2 = player.getName().getString();
            lv2 = player.getDisplayName();
        }
        Vec3d lv3 = Vec3d.ofCenter(this.pos);
        return new ServerCommandSource(CommandOutput.DUMMY, lv3, Vec2f.ZERO, (ServerWorld)this.world, 2, string2, lv2, this.world.getServer(), player);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.book = tag.contains("Book", 10) ? this.resolveBook(ItemStack.fromTag(tag.getCompound("Book")), null) : ItemStack.EMPTY;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.currentPage = MathHelper.clamp(tag.getInt("Page"), 0, this.pageCount - 1);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!this.getBook().isEmpty()) {
            tag.put("Book", this.getBook().toTag(new CompoundTag()));
            tag.putInt("Page", this.currentPage);
        }
        return tag;
    }

    @Override
    public void clear() {
        this.setBook(ItemStack.EMPTY);
    }

    @Override
    public ScreenHandler createMenu(int i, PlayerInventory arg, PlayerEntity arg2) {
        return new LecternScreenHandler(i, this.inventory, this.propertyDelegate);
    }

    @Override
    public Text getDisplayName() {
        return new TranslatableText("container.lectern");
    }
}

