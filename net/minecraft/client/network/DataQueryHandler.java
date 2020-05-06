/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.network;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class DataQueryHandler {
    private final ClientPlayNetworkHandler networkHandler;
    private int expectedTransactionId = -1;
    @Nullable
    private Consumer<CompoundTag> callback;

    public DataQueryHandler(ClientPlayNetworkHandler arg) {
        this.networkHandler = arg;
    }

    public boolean handleQueryResponse(int i, @Nullable CompoundTag arg) {
        if (this.expectedTransactionId == i && this.callback != null) {
            this.callback.accept(arg);
            this.callback = null;
            return true;
        }
        return false;
    }

    private int nextQuery(Consumer<CompoundTag> consumer) {
        this.callback = consumer;
        return ++this.expectedTransactionId;
    }

    public void queryEntityNbt(int i, Consumer<CompoundTag> consumer) {
        int j = this.nextQuery(consumer);
        this.networkHandler.sendPacket(new QueryEntityNbtC2SPacket(j, i));
    }

    public void queryBlockNbt(BlockPos arg, Consumer<CompoundTag> consumer) {
        int i = this.nextQuery(consumer);
        this.networkHandler.sendPacket(new QueryBlockNbtC2SPacket(i, arg));
    }
}

