/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.server.network;

import io.netty.buffer.Unpooled;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DebugInfoSender {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void addGameTestMarker(ServerWorld arg, BlockPos arg2, String string, int i, int j) {
        PacketByteBuf lv = new PacketByteBuf(Unpooled.buffer());
        lv.writeBlockPos(arg2);
        lv.writeInt(i);
        lv.writeString(string);
        lv.writeInt(j);
        DebugInfoSender.sendToAll(arg, lv, CustomPayloadS2CPacket.DEBUG_GAME_TEST_ADD_MARKER);
    }

    public static void clearGameTestMarkers(ServerWorld arg) {
        PacketByteBuf lv = new PacketByteBuf(Unpooled.buffer());
        DebugInfoSender.sendToAll(arg, lv, CustomPayloadS2CPacket.DEBUG_GAME_TEST_CLEAR);
    }

    public static void sendChunkWatchingChange(ServerWorld arg, ChunkPos arg2) {
    }

    public static void sendPoiAddition(ServerWorld arg, BlockPos arg2) {
        DebugInfoSender.method_24819(arg, arg2);
    }

    public static void sendPoiRemoval(ServerWorld arg, BlockPos arg2) {
        DebugInfoSender.method_24819(arg, arg2);
    }

    public static void sendPointOfInterest(ServerWorld arg, BlockPos arg2) {
        DebugInfoSender.method_24819(arg, arg2);
    }

    private static void method_24819(ServerWorld arg, BlockPos arg2) {
    }

    public static void sendPathfindingData(World arg, MobEntity arg2, @Nullable Path arg3, float f) {
    }

    public static void sendNeighborUpdate(World arg, BlockPos arg2) {
    }

    public static void sendStructureStart(IWorld arg, StructureStart arg2) {
    }

    public static void sendGoalSelector(World arg, MobEntity arg2, GoalSelector arg3) {
    }

    public static void sendRaids(ServerWorld arg, Collection<Raid> collection) {
    }

    public static void sendBrainDebugData(LivingEntity arg) {
    }

    public static void sendBeeDebugData(BeeEntity arg) {
    }

    public static void sendBeehiveDebugData(BeehiveBlockEntity arg) {
    }

    private static void sendToAll(ServerWorld arg, PacketByteBuf arg2, Identifier arg3) {
        CustomPayloadS2CPacket lv = new CustomPayloadS2CPacket(arg3, arg2);
        for (PlayerEntity playerEntity : arg.getWorld().getPlayers()) {
            ((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(lv);
        }
    }
}

