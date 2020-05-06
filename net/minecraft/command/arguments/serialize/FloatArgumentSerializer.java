/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.FloatArgumentType
 */
package net.minecraft.command.arguments.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.BrigadierArgumentTypes;
import net.minecraft.command.arguments.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class FloatArgumentSerializer
implements ArgumentSerializer<FloatArgumentType> {
    @Override
    public void toPacket(FloatArgumentType floatArgumentType, PacketByteBuf arg) {
        boolean bl = floatArgumentType.getMinimum() != -3.4028235E38f;
        boolean bl2 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
        arg.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
        if (bl) {
            arg.writeFloat(floatArgumentType.getMinimum());
        }
        if (bl2) {
            arg.writeFloat(floatArgumentType.getMaximum());
        }
    }

    @Override
    public FloatArgumentType fromPacket(PacketByteBuf arg) {
        byte b = arg.readByte();
        float f = BrigadierArgumentTypes.hasMin(b) ? arg.readFloat() : -3.4028235E38f;
        float g = BrigadierArgumentTypes.hasMax(b) ? arg.readFloat() : Float.MAX_VALUE;
        return FloatArgumentType.floatArg((float)f, (float)g);
    }

    @Override
    public void toJson(FloatArgumentType floatArgumentType, JsonObject jsonObject) {
        if (floatArgumentType.getMinimum() != -3.4028235E38f) {
            jsonObject.addProperty("min", (Number)Float.valueOf(floatArgumentType.getMinimum()));
        }
        if (floatArgumentType.getMaximum() != Float.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)Float.valueOf(floatArgumentType.getMaximum()));
        }
    }

    @Override
    public /* synthetic */ ArgumentType fromPacket(PacketByteBuf arg) {
        return this.fromPacket(arg);
    }
}

