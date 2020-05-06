/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Splitter
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render.model.json;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

@Environment(value=EnvType.CLIENT)
public class SimpleMultipartModelSelector
implements MultipartModelSelector {
    private static final Splitter VALUE_SPLITTER = Splitter.on((char)'|').omitEmptyStrings();
    private final String key;
    private final String valueString;

    public SimpleMultipartModelSelector(String string, String string2) {
        this.key = string;
        this.valueString = string2;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateManager<Block, BlockState> arg2) {
        Predicate<BlockState> predicate2;
        List list;
        boolean bl;
        Property<?> lv = arg2.getProperty(this.key);
        if (lv == null) {
            throw new RuntimeException(String.format("Unknown property '%s' on '%s'", this.key, arg2.getOwner().toString()));
        }
        String string2 = this.valueString;
        boolean bl2 = bl = !string2.isEmpty() && string2.charAt(0) == '!';
        if (bl) {
            string2 = string2.substring(1);
        }
        if ((list = VALUE_SPLITTER.splitToList((CharSequence)string2)).isEmpty()) {
            throw new RuntimeException(String.format("Empty value '%s' for property '%s' on '%s'", this.valueString, this.key, arg2.getOwner().toString()));
        }
        if (list.size() == 1) {
            Predicate<BlockState> predicate = this.createPredicate(arg2, lv, string2);
        } else {
            List list2 = list.stream().map(string -> this.createPredicate(arg2, lv, (String)string)).collect(Collectors.toList());
            predicate2 = arg -> list2.stream().anyMatch(predicate -> predicate.test(arg));
        }
        return bl ? predicate2.negate() : predicate2;
    }

    private Predicate<BlockState> createPredicate(StateManager<Block, BlockState> arg, Property<?> arg22, String string) {
        Optional<?> optional = arg22.parse(string);
        if (!optional.isPresent()) {
            throw new RuntimeException(String.format("Unknown value '%s' for property '%s' on '%s' in '%s'", string, this.key, arg.getOwner().toString(), this.valueString));
        }
        return arg2 -> arg2.get(arg22).equals(optional.get());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("key", (Object)this.key).add("value", (Object)this.valueString).toString();
    }
}

