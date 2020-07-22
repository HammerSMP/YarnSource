/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 */
package net.minecraft.command.argument.serialize;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.argument.BrigadierArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;

public class DoubleArgumentSerializer
implements ArgumentSerializer<DoubleArgumentType> {
    @Override
    public void toPacket(DoubleArgumentType doubleArgumentType, PacketByteBuf arg) {
        boolean bl = doubleArgumentType.getMinimum() != -1.7976931348623157E308;
        boolean bl2 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
        arg.writeByte(BrigadierArgumentTypes.createFlag(bl, bl2));
        if (bl) {
            arg.writeDouble(doubleArgumentType.getMinimum());
        }
        if (bl2) {
            arg.writeDouble(doubleArgumentType.getMaximum());
        }
    }

    @Override
    public DoubleArgumentType fromPacket(PacketByteBuf arg) {
        byte b = arg.readByte();
        double d = BrigadierArgumentTypes.hasMin(b) ? arg.readDouble() : -1.7976931348623157E308;
        double e = BrigadierArgumentTypes.hasMax(b) ? arg.readDouble() : Double.MAX_VALUE;
        return DoubleArgumentType.doubleArg((double)d, (double)e);
    }

    @Override
    public void toJson(DoubleArgumentType doubleArgumentType, JsonObject jsonObject) {
        if (doubleArgumentType.getMinimum() != -1.7976931348623157E308) {
            jsonObject.addProperty("min", (Number)doubleArgumentType.getMinimum());
        }
        if (doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
            jsonObject.addProperty("max", (Number)doubleArgumentType.getMaximum());
        }
    }

    @Override
    public /* synthetic */ ArgumentType fromPacket(PacketByteBuf arg) {
        return this.fromPacket(arg);
    }
}

