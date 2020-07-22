/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.Timer;
import net.minecraft.world.timer.TimerCallback;

public class FunctionTimerCallback
implements TimerCallback<MinecraftServer> {
    private final Identifier name;

    public FunctionTimerCallback(Identifier arg) {
        this.name = arg;
    }

    @Override
    public void call(MinecraftServer minecraftServer, Timer<MinecraftServer> arg, long l) {
        CommandFunctionManager lv = minecraftServer.getCommandFunctionManager();
        lv.getFunction(this.name).ifPresent(arg2 -> lv.execute((CommandFunction)arg2, lv.getTaggedFunctionSource()));
    }

    public static class Serializer
    extends TimerCallback.Serializer<MinecraftServer, FunctionTimerCallback> {
        public Serializer() {
            super(new Identifier("function"), FunctionTimerCallback.class);
        }

        @Override
        public void serialize(CompoundTag arg, FunctionTimerCallback arg2) {
            arg.putString("Name", arg2.name.toString());
        }

        @Override
        public FunctionTimerCallback deserialize(CompoundTag arg) {
            Identifier lv = new Identifier(arg.getString("Name"));
            return new FunctionTimerCallback(lv);
        }

        @Override
        public /* synthetic */ TimerCallback deserialize(CompoundTag tag) {
            return this.deserialize(tag);
        }
    }
}

