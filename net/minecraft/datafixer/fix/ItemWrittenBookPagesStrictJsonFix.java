/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParseException
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.OpticFinder
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.datafixers.types.Type
 *  com.mojang.serialization.Dynamic
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.datafixer.fix;

import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;

public class ItemWrittenBookPagesStrictJsonFix
extends DataFix {
    public ItemWrittenBookPagesStrictJsonFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    public Dynamic<?> fixBookPages(Dynamic<?> dynamic) {
        return dynamic.update("pages", dynamic2 -> (Dynamic)DataFixUtils.orElse((Optional)dynamic2.asStreamOpt().map(stream -> stream.map(dynamic -> {
            if (!dynamic.asString().result().isPresent()) {
                return dynamic;
            }
            String string = dynamic.asString("");
            Text lv = null;
            if ("null".equals(string) || StringUtils.isEmpty((CharSequence)string)) {
                lv = LiteralText.EMPTY;
            } else if (string.charAt(0) == '\"' && string.charAt(string.length() - 1) == '\"' || string.charAt(0) == '{' && string.charAt(string.length() - 1) == '}') {
                try {
                    lv = JsonHelper.deserialize(BlockEntitySignTextStrictJsonFix.GSON, string, Text.class, true);
                    if (lv == null) {
                        lv = LiteralText.EMPTY;
                    }
                }
                catch (JsonParseException jsonParseException) {
                    // empty catch block
                }
                if (lv == null) {
                    try {
                        lv = Text.Serializer.fromJson(string);
                    }
                    catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                }
                if (lv == null) {
                    try {
                        lv = Text.Serializer.fromLenientJson(string);
                    }
                    catch (JsonParseException jsonParseException) {
                        // empty catch block
                    }
                }
                if (lv == null) {
                    lv = new LiteralText(string);
                }
            } else {
                lv = new LiteralText(string);
            }
            return dynamic.createString(Text.Serializer.toJson(lv));
        })).map(((Dynamic)dynamic)::createList).result(), (Object)dynamic.emptyList()));
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getInputSchema().getType(TypeReferences.ITEM_STACK);
        OpticFinder opticFinder = type.findField("tag");
        return this.fixTypeEverywhereTyped("ItemWrittenBookPagesStrictJsonFix", type, typed2 -> typed2.updateTyped(opticFinder, typed -> typed.update(DSL.remainderFinder(), this::fixBookPages)));
    }
}

