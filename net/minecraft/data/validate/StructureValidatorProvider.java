/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;

public class StructureValidatorProvider
implements SnbtProvider.Tweaker {
    @Override
    public CompoundTag write(String string, CompoundTag arg) {
        if (string.startsWith("data/minecraft/structures/")) {
            return StructureValidatorProvider.update(StructureValidatorProvider.addDataVersion(arg));
        }
        return arg;
    }

    private static CompoundTag addDataVersion(CompoundTag arg) {
        if (!arg.contains("DataVersion", 99)) {
            arg.putInt("DataVersion", 500);
        }
        return arg;
    }

    private static CompoundTag update(CompoundTag arg) {
        Structure lv = new Structure();
        lv.fromTag(NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, arg, arg.getInt("DataVersion")));
        return lv.toTag(new CompoundTag());
    }
}

