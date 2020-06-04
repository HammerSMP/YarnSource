/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package net.minecraft;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_5324;

public class class_5310 {
    public static final Codec<class_5310> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("target").forGetter(class_5310::method_28594), (App)class_5324.method_29229(0, 256).fieldOf("size").forGetter(class_5310::method_28596), (App)Codec.INT.fieldOf("offset").forGetter(class_5310::method_28597)).apply((Applicative)instance, class_5310::new));
    private final int field_24818;
    private final int field_24819;
    private final int field_24820;

    public class_5310(int i, int j, int k) {
        this.field_24818 = i;
        this.field_24819 = j;
        this.field_24820 = k;
    }

    public int method_28594() {
        return this.field_24818;
    }

    public int method_28596() {
        return this.field_24819;
    }

    public int method_28597() {
        return this.field_24820;
    }
}

