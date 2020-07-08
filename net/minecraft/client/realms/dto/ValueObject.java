/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.realms.dto;

import com.google.gson.annotations.SerializedName;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public abstract class ValueObject {
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("{");
        for (Field field : this.getClass().getFields()) {
            if (ValueObject.isStatic(field)) continue;
            try {
                stringBuilder.append(ValueObject.getName(field)).append("=").append(field.get(this)).append(" ");
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    private static String getName(Field field) {
        SerializedName serializedName = field.getAnnotation(SerializedName.class);
        return serializedName != null ? serializedName.value() : field.getName();
    }

    private static boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
}

