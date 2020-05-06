/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.fix.PointOfInterestRenameFix;

public class BeehiveRenameFix
extends PointOfInterestRenameFix {
    public BeehiveRenameFix(Schema schema) {
        super(schema, false);
    }

    @Override
    protected String rename(String string) {
        return string.equals("minecraft:bee_hive") ? "minecraft:beehive" : string;
    }
}

