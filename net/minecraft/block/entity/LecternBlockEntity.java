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
        public ItemStack getStack(int i) {
            return i == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int i, int j) {
            if (i == 0) {
                ItemStack lv = LecternBlockEntity.this.book.split(j);
                if (LecternBlockEntity.this.book.isEmpty()) {
                    LecternBlockEntity.this.onBookRemoved();
                }
                return lv;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeStack(int i) {
            if (i == 0) {
                ItemStack lv = LecternBlockEntity.this.book;
                LecternBlockEntity.this.book = ItemStack.EMPTY;
                LecternBlockEntity.this.onBookRemoved();
                return lv;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setStack(int i, ItemStack arg) {
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
        public boolean canPlayerUse(PlayerEntity arg) {
            if (LecternBlockEntity.this.world.getBlockEntity(LecternBlockEntity.this.pos) != LecternBlockEntity.this) {
                return false;
            }
            if (arg.squaredDistanceTo((double)LecternBlockEntity.this.pos.getX() + 0.5, (double)LecternBlockEntity.this.pos.getY() + 0.5, (double)LecternBlockEntity.this.pos.getZ() + 0.5) > 64.0) {
                return false;
            }
            return LecternBlockEntity.this.hasBook();
        }

        @Override
        public boolean isValid(int i, ItemStack arg) {
            return false;
        }

        @Override
        public void clear() {
        }
    };
    private final PropertyDelegate propertyDelegate = new PropertyDelegate(){

        @Override
        public int get(int i) {
            return i == 0 ? LecternBlockEntity.this.currentPage : 0;
        }

        @Override
        public void set(int i, int j) {
            if (i == 0) {
                LecternBlockEntity.this.setCurrentPage(j);
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

    public void setBook(ItemStack arg) {
        this.setBook(arg, null);
    }

    private void onBookRemoved() {
        this.currentPage = 0;
        this.pageCount = 0;
        LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getCachedState(), false);
    }

    public void setBook(ItemStack arg, @Nullable PlayerEntity arg2) {
        this.book = this.resolveBook(arg, arg2);
        this.currentPage = 0;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.markDirty();
    }

    private void setCurrentPage(int i) {
        int j = MathHelper.clamp(i, 0, this.pageCount - 1);
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

    private ItemStack resolveBook(ItemStack arg, @Nullable PlayerEntity arg2) {
        if (this.world instanceof ServerWorld && arg.getItem() == Items.WRITTEN_BOOK) {
            WrittenBookItem.resolve(arg, this.getCommandSource(arg2), arg2);
        }
        return arg;
    }

    private ServerCommandSource getCommandSource(@Nullable PlayerEntity arg) {
        Text lv2;
        String string2;
        if (arg == null) {
            String string = "Lectern";
            LiteralText lv = new LiteralText("Lectern");
        } else {
            string2 = arg.getName().getString();
            lv2 = arg.getDisplayName();
        }
        Vec3d lv3 = Vec3d.method_24953(this.pos);
        return new ServerCommandSource(CommandOutput.DUMMY, lv3, Vec2f.ZERO, (ServerWorld)this.world, 2, string2, lv2, this.world.getServer(), arg);
    }

    @Override
    public boolean copyItemDataRequiresOperator() {
        return true;
    }

    @Override
    public void fromTag(BlockState arg, CompoundTag arg2) {
        super.fromTag(arg, arg2);
        this.book = arg2.contains("Book", 10) ? this.resolveBook(ItemStack.fromTag(arg2.getCompound("Book")), null) : ItemStack.EMPTY;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.currentPage = MathHelper.clamp(arg2.getInt("Page"), 0, this.pageCount - 1);
    }

    @Override
    public CompoundTag toTag(CompoundTag arg) {
        super.toTag(arg);
        if (!this.getBook().isEmpty()) {
            arg.put("Book", this.getBook().toTag(new CompoundTag()));
            arg.putInt("Page", this.currentPage);
        }
        return arg;
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

