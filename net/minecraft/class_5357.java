/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.World;

public class class_5357
implements Recipe<Inventory> {
    private final Ingredient field_25389;
    private final Ingredient field_25390;
    private final ItemStack field_25391;
    private final Identifier field_25392;

    public class_5357(Identifier arg, Ingredient arg2, Ingredient arg3, ItemStack arg4) {
        this.field_25392 = arg;
        this.field_25389 = arg2;
        this.field_25390 = arg3;
        this.field_25391 = arg4;
    }

    @Override
    public boolean matches(Inventory arg, World arg2) {
        return this.field_25389.test(arg.getStack(0)) && this.field_25390.test(arg.getStack(1));
    }

    @Override
    public ItemStack craft(Inventory arg) {
        ItemStack lv = this.field_25391.copy();
        CompoundTag lv2 = arg.getStack(0).getTag();
        if (lv2 != null) {
            lv.setTag(lv2.copy());
        }
        return lv;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean fits(int i, int j) {
        return i * j >= 2;
    }

    @Override
    public ItemStack getOutput() {
        return this.field_25391;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    @Override
    public Identifier getId() {
        return this.field_25392;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMITHING;
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    public static class class_5358
    implements RecipeSerializer<class_5357> {
        @Override
        public class_5357 read(Identifier arg, JsonObject jsonObject) {
            Ingredient lv = Ingredient.fromJson((JsonElement)JsonHelper.getObject(jsonObject, "base"));
            Ingredient lv2 = Ingredient.fromJson((JsonElement)JsonHelper.getObject(jsonObject, "addition"));
            ItemStack lv3 = ShapedRecipe.getItemStack(JsonHelper.getObject(jsonObject, "result"));
            return new class_5357(arg, lv, lv2, lv3);
        }

        @Override
        public class_5357 read(Identifier arg, PacketByteBuf arg2) {
            Ingredient lv = Ingredient.fromPacket(arg2);
            Ingredient lv2 = Ingredient.fromPacket(arg2);
            ItemStack lv3 = arg2.readItemStack();
            return new class_5357(arg, lv, lv2, lv3);
        }

        @Override
        public void write(PacketByteBuf arg, class_5357 arg2) {
            arg2.field_25389.write(arg);
            arg2.field_25390.write(arg);
            arg.writeItemStack(arg2.field_25391);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier arg, PacketByteBuf arg2) {
            return this.read(arg, arg2);
        }

        @Override
        public /* synthetic */ Recipe read(Identifier arg, JsonObject jsonObject) {
            return this.read(arg, jsonObject);
        }
    }
}

