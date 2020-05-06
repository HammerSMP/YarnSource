/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class ObjectiveRenderTypeFix
extends DataFix {
    public ObjectiveRenderTypeFix(Schema schema, boolean bl) {
        super(schema, bl);
    }

    private static ScoreboardCriterion.RenderType parseLegacyRenderType(String string) {
        return string.equals("health") ? ScoreboardCriterion.RenderType.HEARTS : ScoreboardCriterion.RenderType.INTEGER;
    }

    protected TypeRewriteRule makeRule() {
        Type type = DSL.named((String)TypeReferences.OBJECTIVE.typeName(), (Type)DSL.remainderType());
        if (!Objects.equals((Object)type, (Object)this.getInputSchema().getType(TypeReferences.OBJECTIVE))) {
            throw new IllegalStateException("Objective type is not what was expected.");
        }
        return this.fixTypeEverywhere("ObjectiveRenderTypeFix", type, dynamicOps -> pair -> pair.mapSecond(dynamic -> {
            Optional optional = dynamic.get("RenderType").asString();
            if (!optional.isPresent()) {
                String string = dynamic.get("CriteriaName").asString("");
                ScoreboardCriterion.RenderType lv = ObjectiveRenderTypeFix.parseLegacyRenderType(string);
                return dynamic.set("RenderType", dynamic.createString(lv.getName()));
            }
            return dynamic;
        }));
    }
}

