/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.schemas.Schema
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import net.minecraft.datafixer.fix.EntityRenameFix;

public class EntityTippedArrowFix
extends EntityRenameFix {
    public EntityTippedArrowFix(Schema outputSchema, boolean changesType) {
        super("EntityTippedArrowFix", outputSchema, changesType);
    }

    @Override
    protected String rename(String oldName) {
        return Objects.equals(oldName, "TippedArrow") ? "Arrow" : oldName;
    }
}

