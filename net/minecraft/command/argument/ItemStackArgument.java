/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.registry.Registry;

public class ItemStackArgument
implements Predicate<ItemStack> {
    private static final Dynamic2CommandExceptionType OVERSTACKED_EXCEPTION = new Dynamic2CommandExceptionType((object, object2) -> new TranslatableText("arguments.item.overstacked", object, object2));
    private final Item item;
    @Nullable
    private final CompoundTag tag;

    public ItemStackArgument(Item arg, @Nullable CompoundTag tag) {
        this.item = arg;
        this.tag = tag;
    }

    public Item getItem() {
        return this.item;
    }

    @Override
    public boolean test(ItemStack arg) {
        return arg.getItem() == this.item && NbtHelper.matches(this.tag, arg.getTag(), true);
    }

    public ItemStack createStack(int amount, boolean checkOverstack) throws CommandSyntaxException {
        ItemStack lv = new ItemStack(this.item, amount);
        if (this.tag != null) {
            lv.setTag(this.tag);
        }
        if (checkOverstack && amount > lv.getMaxCount()) {
            throw OVERSTACKED_EXCEPTION.create((Object)Registry.ITEM.getId(this.item), (Object)lv.getMaxCount());
        }
        return lv;
    }

    public String asString() {
        StringBuilder stringBuilder = new StringBuilder(Registry.ITEM.getRawId(this.item));
        if (this.tag != null) {
            stringBuilder.append(this.tag);
        }
        return stringBuilder.toString();
    }

    @Override
    public /* synthetic */ boolean test(Object object) {
        return this.test((ItemStack)object);
    }
}

