/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.server.dedicated;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class ServerCommandOutput
implements CommandOutput {
    private final StringBuffer buffer = new StringBuffer();
    private final MinecraftServer server;

    public ServerCommandOutput(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
    }

    public void clear() {
        this.buffer.setLength(0);
    }

    public String asString() {
        return this.buffer.toString();
    }

    public ServerCommandSource createReconCommandSource() {
        ServerWorld lv = this.server.getWorld(DimensionType.OVERWORLD);
        return new ServerCommandSource(this, Vec3d.of(lv.method_27911()), Vec2f.ZERO, lv, 4, "Recon", new LiteralText("Rcon"), this.server, null);
    }

    @Override
    public void sendSystemMessage(Text arg) {
        this.buffer.append(arg.getString());
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return true;
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.server.shouldBroadcastRconToOps();
    }
}
