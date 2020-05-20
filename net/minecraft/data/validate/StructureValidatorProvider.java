/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureValidatorProvider
implements SnbtProvider.Tweaker {
    private static final Logger field_24617 = LogManager.getLogger();

    @Override
    public CompoundTag write(String string, CompoundTag arg) {
        if (string.startsWith("data/minecraft/structures/")) {
            return StructureValidatorProvider.update(string, StructureValidatorProvider.addDataVersion(arg));
        }
        return arg;
    }

    private static CompoundTag addDataVersion(CompoundTag arg) {
        if (!arg.contains("DataVersion", 99)) {
            arg.putInt("DataVersion", 500);
        }
        return arg;
    }

    private static CompoundTag update(String string, CompoundTag arg) {
        Structure lv = new Structure();
        int i = arg.getInt("DataVersion");
        int j = 2532;
        if (i < 2532) {
            field_24617.warn("SNBT Too old, do not forget to update: " + i + " < " + 2532 + ": " + string);
        }
        CompoundTag lv2 = NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, arg, i);
        lv.fromTag(lv2);
        return lv.toTag(new CompoundTag());
    }
}

