/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  javax.annotation.Nullable
 *  org.apache.logging.log4j.LogManager
 */
package net.minecraft.network;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginKeyC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.c2s.play.AdvancementTabC2SPacket;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickWindowC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientSettingsC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.ConfirmGuiActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.GuiCloseC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.JigsawGeneratingC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.RecipeBookDataC2SPacket;
import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.c2s.play.ResourcePackStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.SelectVillagerTradeC2SPacket;
import net.minecraft.network.packet.c2s.play.SpectatorTeleportC2SPacket;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateBeaconC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockMinecartC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateDifficultyLockC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateJigsawC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateStructureBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryPingC2SPacket;
import net.minecraft.network.packet.c2s.query.QueryRequestC2SPacket;
import net.minecraft.network.packet.s2c.login.LoginCompressionS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginDisconnectS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginHelloS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginQueryRequestS2CPacket;
import net.minecraft.network.packet.s2c.login.LoginSuccessS2CPacket;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockBreakingProgressS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkRenderDistanceCenterS2CPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.CombatEventS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.network.packet.s2c.play.CommandTreeS2CPacket;
import net.minecraft.network.packet.s2c.play.ConfirmGuiActionS2CPacket;
import net.minecraft.network.packet.s2c.play.CooldownUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.CraftFailedResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.network.packet.s2c.play.DifficultyS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttachS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAttributesS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnGlobalS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.HealthUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.HeldItemChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.KeepAliveS2CPacket;
import net.minecraft.network.packet.s2c.play.LightUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.MobSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;
import net.minecraft.network.packet.s2c.play.PaintingSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerAbilitiesS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerActionResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.ResourcePackSendS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerPropertyUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SelectAdvancementTabS2CPacket;
import net.minecraft.network.packet.s2c.play.SetCameraEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.network.packet.s2c.play.SignEditorOpenS2CPacket;
import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.s2c.play.StopSoundS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.SynchronizeTagsS2CPacket;
import net.minecraft.network.packet.s2c.play.TagQueryResponseS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.network.packet.s2c.play.UnloadChunkS2CPacket;
import net.minecraft.network.packet.s2c.play.UnlockRecipesS2CPacket;
import net.minecraft.network.packet.s2c.play.VehicleMoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldBorderS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryPongS2CPacket;
import net.minecraft.network.packet.s2c.query.QueryResponseS2CPacket;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;

public enum NetworkState {
    HANDSHAKING(-1, NetworkState.createPacketHandlerInitializer().setup(NetworkSide.SERVERBOUND, new PacketHandler<T>().register(HandshakeC2SPacket.class, HandshakeC2SPacket::new))),
    PLAY(0, NetworkState.createPacketHandlerInitializer().setup(NetworkSide.CLIENTBOUND, new PacketHandler<T>().register(EntitySpawnS2CPacket.class, EntitySpawnS2CPacket::new).register(ExperienceOrbSpawnS2CPacket.class, ExperienceOrbSpawnS2CPacket::new).register(EntitySpawnGlobalS2CPacket.class, EntitySpawnGlobalS2CPacket::new).register(MobSpawnS2CPacket.class, MobSpawnS2CPacket::new).register(PaintingSpawnS2CPacket.class, PaintingSpawnS2CPacket::new).register(PlayerSpawnS2CPacket.class, PlayerSpawnS2CPacket::new).register(EntityAnimationS2CPacket.class, EntityAnimationS2CPacket::new).register(StatisticsS2CPacket.class, StatisticsS2CPacket::new).register(PlayerActionResponseS2CPacket.class, PlayerActionResponseS2CPacket::new).register(BlockBreakingProgressS2CPacket.class, BlockBreakingProgressS2CPacket::new).register(BlockEntityUpdateS2CPacket.class, BlockEntityUpdateS2CPacket::new).register(BlockEventS2CPacket.class, BlockEventS2CPacket::new).register(BlockUpdateS2CPacket.class, BlockUpdateS2CPacket::new).register(BossBarS2CPacket.class, BossBarS2CPacket::new).register(DifficultyS2CPacket.class, DifficultyS2CPacket::new).register(GameMessageS2CPacket.class, GameMessageS2CPacket::new).register(ChunkDeltaUpdateS2CPacket.class, ChunkDeltaUpdateS2CPacket::new).register(CommandSuggestionsS2CPacket.class, CommandSuggestionsS2CPacket::new).register(CommandTreeS2CPacket.class, CommandTreeS2CPacket::new).register(ConfirmGuiActionS2CPacket.class, ConfirmGuiActionS2CPacket::new).register(CloseScreenS2CPacket.class, CloseScreenS2CPacket::new).register(InventoryS2CPacket.class, InventoryS2CPacket::new).register(ScreenHandlerPropertyUpdateS2CPacket.class, ScreenHandlerPropertyUpdateS2CPacket::new).register(ScreenHandlerSlotUpdateS2CPacket.class, ScreenHandlerSlotUpdateS2CPacket::new).register(CooldownUpdateS2CPacket.class, CooldownUpdateS2CPacket::new).register(CustomPayloadS2CPacket.class, CustomPayloadS2CPacket::new).register(PlaySoundIdS2CPacket.class, PlaySoundIdS2CPacket::new).register(DisconnectS2CPacket.class, DisconnectS2CPacket::new).register(EntityStatusS2CPacket.class, EntityStatusS2CPacket::new).register(ExplosionS2CPacket.class, ExplosionS2CPacket::new).register(UnloadChunkS2CPacket.class, UnloadChunkS2CPacket::new).register(GameStateChangeS2CPacket.class, GameStateChangeS2CPacket::new).register(OpenHorseScreenS2CPacket.class, OpenHorseScreenS2CPacket::new).register(KeepAliveS2CPacket.class, KeepAliveS2CPacket::new).register(ChunkDataS2CPacket.class, ChunkDataS2CPacket::new).register(WorldEventS2CPacket.class, WorldEventS2CPacket::new).register(ParticleS2CPacket.class, ParticleS2CPacket::new).register(LightUpdateS2CPacket.class, LightUpdateS2CPacket::new).register(GameJoinS2CPacket.class, GameJoinS2CPacket::new).register(MapUpdateS2CPacket.class, MapUpdateS2CPacket::new).register(SetTradeOffersS2CPacket.class, SetTradeOffersS2CPacket::new).register(EntityS2CPacket.MoveRelative.class, EntityS2CPacket.MoveRelative::new).register(EntityS2CPacket.RotateAndMoveRelative.class, EntityS2CPacket.RotateAndMoveRelative::new).register(EntityS2CPacket.Rotate.class, EntityS2CPacket.Rotate::new).register(EntityS2CPacket.class, EntityS2CPacket::new).register(VehicleMoveS2CPacket.class, VehicleMoveS2CPacket::new).register(OpenWrittenBookS2CPacket.class, OpenWrittenBookS2CPacket::new).register(OpenScreenS2CPacket.class, OpenScreenS2CPacket::new).register(SignEditorOpenS2CPacket.class, SignEditorOpenS2CPacket::new).register(CraftFailedResponseS2CPacket.class, CraftFailedResponseS2CPacket::new).register(PlayerAbilitiesS2CPacket.class, PlayerAbilitiesS2CPacket::new).register(CombatEventS2CPacket.class, CombatEventS2CPacket::new).register(PlayerListS2CPacket.class, PlayerListS2CPacket::new).register(LookAtS2CPacket.class, LookAtS2CPacket::new).register(PlayerPositionLookS2CPacket.class, PlayerPositionLookS2CPacket::new).register(UnlockRecipesS2CPacket.class, UnlockRecipesS2CPacket::new).register(EntitiesDestroyS2CPacket.class, EntitiesDestroyS2CPacket::new).register(RemoveEntityStatusEffectS2CPacket.class, RemoveEntityStatusEffectS2CPacket::new).register(ResourcePackSendS2CPacket.class, ResourcePackSendS2CPacket::new).register(PlayerRespawnS2CPacket.class, PlayerRespawnS2CPacket::new).register(EntitySetHeadYawS2CPacket.class, EntitySetHeadYawS2CPacket::new).register(SelectAdvancementTabS2CPacket.class, SelectAdvancementTabS2CPacket::new).register(WorldBorderS2CPacket.class, WorldBorderS2CPacket::new).register(SetCameraEntityS2CPacket.class, SetCameraEntityS2CPacket::new).register(HeldItemChangeS2CPacket.class, HeldItemChangeS2CPacket::new).register(ChunkRenderDistanceCenterS2CPacket.class, ChunkRenderDistanceCenterS2CPacket::new).register(ChunkLoadDistanceS2CPacket.class, ChunkLoadDistanceS2CPacket::new).register(PlayerSpawnPositionS2CPacket.class, PlayerSpawnPositionS2CPacket::new).register(ScoreboardDisplayS2CPacket.class, ScoreboardDisplayS2CPacket::new).register(EntityTrackerUpdateS2CPacket.class, EntityTrackerUpdateS2CPacket::new).register(EntityAttachS2CPacket.class, EntityAttachS2CPacket::new).register(EntityVelocityUpdateS2CPacket.class, EntityVelocityUpdateS2CPacket::new).register(EntityEquipmentUpdateS2CPacket.class, EntityEquipmentUpdateS2CPacket::new).register(ExperienceBarUpdateS2CPacket.class, ExperienceBarUpdateS2CPacket::new).register(HealthUpdateS2CPacket.class, HealthUpdateS2CPacket::new).register(ScoreboardObjectiveUpdateS2CPacket.class, ScoreboardObjectiveUpdateS2CPacket::new).register(EntityPassengersSetS2CPacket.class, EntityPassengersSetS2CPacket::new).register(TeamS2CPacket.class, TeamS2CPacket::new).register(ScoreboardPlayerUpdateS2CPacket.class, ScoreboardPlayerUpdateS2CPacket::new).register(WorldTimeUpdateS2CPacket.class, WorldTimeUpdateS2CPacket::new).register(TitleS2CPacket.class, TitleS2CPacket::new).register(PlaySoundFromEntityS2CPacket.class, PlaySoundFromEntityS2CPacket::new).register(PlaySoundS2CPacket.class, PlaySoundS2CPacket::new).register(StopSoundS2CPacket.class, StopSoundS2CPacket::new).register(PlayerListHeaderS2CPacket.class, PlayerListHeaderS2CPacket::new).register(TagQueryResponseS2CPacket.class, TagQueryResponseS2CPacket::new).register(ItemPickupAnimationS2CPacket.class, ItemPickupAnimationS2CPacket::new).register(EntityPositionS2CPacket.class, EntityPositionS2CPacket::new).register(AdvancementUpdateS2CPacket.class, AdvancementUpdateS2CPacket::new).register(EntityAttributesS2CPacket.class, EntityAttributesS2CPacket::new).register(EntityStatusEffectS2CPacket.class, EntityStatusEffectS2CPacket::new).register(SynchronizeRecipesS2CPacket.class, SynchronizeRecipesS2CPacket::new).register(SynchronizeTagsS2CPacket.class, SynchronizeTagsS2CPacket::new)).setup(NetworkSide.SERVERBOUND, new PacketHandler<T>().register(TeleportConfirmC2SPacket.class, TeleportConfirmC2SPacket::new).register(QueryBlockNbtC2SPacket.class, QueryBlockNbtC2SPacket::new).register(UpdateDifficultyC2SPacket.class, UpdateDifficultyC2SPacket::new).register(ChatMessageC2SPacket.class, ChatMessageC2SPacket::new).register(ClientStatusC2SPacket.class, ClientStatusC2SPacket::new).register(ClientSettingsC2SPacket.class, ClientSettingsC2SPacket::new).register(RequestCommandCompletionsC2SPacket.class, RequestCommandCompletionsC2SPacket::new).register(ConfirmGuiActionC2SPacket.class, ConfirmGuiActionC2SPacket::new).register(ButtonClickC2SPacket.class, ButtonClickC2SPacket::new).register(ClickWindowC2SPacket.class, ClickWindowC2SPacket::new).register(GuiCloseC2SPacket.class, GuiCloseC2SPacket::new).register(CustomPayloadC2SPacket.class, CustomPayloadC2SPacket::new).register(BookUpdateC2SPacket.class, BookUpdateC2SPacket::new).register(QueryEntityNbtC2SPacket.class, QueryEntityNbtC2SPacket::new).register(PlayerInteractEntityC2SPacket.class, PlayerInteractEntityC2SPacket::new).register(JigsawGeneratingC2SPacket.class, JigsawGeneratingC2SPacket::new).register(KeepAliveC2SPacket.class, KeepAliveC2SPacket::new).register(UpdateDifficultyLockC2SPacket.class, UpdateDifficultyLockC2SPacket::new).register(PlayerMoveC2SPacket.PositionOnly.class, PlayerMoveC2SPacket.PositionOnly::new).register(PlayerMoveC2SPacket.Both.class, PlayerMoveC2SPacket.Both::new).register(PlayerMoveC2SPacket.LookOnly.class, PlayerMoveC2SPacket.LookOnly::new).register(PlayerMoveC2SPacket.class, PlayerMoveC2SPacket::new).register(VehicleMoveC2SPacket.class, VehicleMoveC2SPacket::new).register(BoatPaddleStateC2SPacket.class, BoatPaddleStateC2SPacket::new).register(PickFromInventoryC2SPacket.class, PickFromInventoryC2SPacket::new).register(CraftRequestC2SPacket.class, CraftRequestC2SPacket::new).register(UpdatePlayerAbilitiesC2SPacket.class, UpdatePlayerAbilitiesC2SPacket::new).register(PlayerActionC2SPacket.class, PlayerActionC2SPacket::new).register(ClientCommandC2SPacket.class, ClientCommandC2SPacket::new).register(PlayerInputC2SPacket.class, PlayerInputC2SPacket::new).register(RecipeBookDataC2SPacket.class, RecipeBookDataC2SPacket::new).register(RenameItemC2SPacket.class, RenameItemC2SPacket::new).register(ResourcePackStatusC2SPacket.class, ResourcePackStatusC2SPacket::new).register(AdvancementTabC2SPacket.class, AdvancementTabC2SPacket::new).register(SelectVillagerTradeC2SPacket.class, SelectVillagerTradeC2SPacket::new).register(UpdateBeaconC2SPacket.class, UpdateBeaconC2SPacket::new).register(UpdateSelectedSlotC2SPacket.class, UpdateSelectedSlotC2SPacket::new).register(UpdateCommandBlockC2SPacket.class, UpdateCommandBlockC2SPacket::new).register(UpdateCommandBlockMinecartC2SPacket.class, UpdateCommandBlockMinecartC2SPacket::new).register(CreativeInventoryActionC2SPacket.class, CreativeInventoryActionC2SPacket::new).register(UpdateJigsawC2SPacket.class, UpdateJigsawC2SPacket::new).register(UpdateStructureBlockC2SPacket.class, UpdateStructureBlockC2SPacket::new).register(UpdateSignC2SPacket.class, UpdateSignC2SPacket::new).register(HandSwingC2SPacket.class, HandSwingC2SPacket::new).register(SpectatorTeleportC2SPacket.class, SpectatorTeleportC2SPacket::new).register(PlayerInteractBlockC2SPacket.class, PlayerInteractBlockC2SPacket::new).register(PlayerInteractItemC2SPacket.class, PlayerInteractItemC2SPacket::new))),
    STATUS(1, NetworkState.createPacketHandlerInitializer().setup(NetworkSide.SERVERBOUND, new PacketHandler<T>().register(QueryRequestC2SPacket.class, QueryRequestC2SPacket::new).register(QueryPingC2SPacket.class, QueryPingC2SPacket::new)).setup(NetworkSide.CLIENTBOUND, new PacketHandler<T>().register(QueryResponseS2CPacket.class, QueryResponseS2CPacket::new).register(QueryPongS2CPacket.class, QueryPongS2CPacket::new))),
    LOGIN(2, NetworkState.createPacketHandlerInitializer().setup(NetworkSide.CLIENTBOUND, new PacketHandler<T>().register(LoginDisconnectS2CPacket.class, LoginDisconnectS2CPacket::new).register(LoginHelloS2CPacket.class, LoginHelloS2CPacket::new).register(LoginSuccessS2CPacket.class, LoginSuccessS2CPacket::new).register(LoginCompressionS2CPacket.class, LoginCompressionS2CPacket::new).register(LoginQueryRequestS2CPacket.class, LoginQueryRequestS2CPacket::new)).setup(NetworkSide.SERVERBOUND, new PacketHandler<T>().register(LoginHelloC2SPacket.class, LoginHelloC2SPacket::new).register(LoginKeyC2SPacket.class, LoginKeyC2SPacket::new).register(LoginQueryResponseC2SPacket.class, LoginQueryResponseC2SPacket::new)));

    private static final NetworkState[] STATES;
    private static final Map<Class<? extends Packet<?>>, NetworkState> HANDLER_STATE_MAP;
    private final int stateId;
    private final Map<NetworkSide, ? extends PacketHandler<?>> packetHandlers;

    private static PacketHandlerInitializer createPacketHandlerInitializer() {
        return new PacketHandlerInitializer();
    }

    private NetworkState(int j, PacketHandlerInitializer arg) {
        this.stateId = j;
        this.packetHandlers = arg.packetHandlers;
    }

    @Nullable
    public Integer getPacketId(NetworkSide arg, Packet<?> arg2) {
        return this.packetHandlers.get((Object)arg).getId(arg2.getClass());
    }

    @Nullable
    public Packet<?> getPacketHandler(NetworkSide arg, int i) {
        return this.packetHandlers.get((Object)arg).createPacket(i);
    }

    public int getId() {
        return this.stateId;
    }

    @Nullable
    public static NetworkState byId(int i) {
        if (i < -1 || i > 2) {
            return null;
        }
        return STATES[i - -1];
    }

    public static NetworkState getPacketHandlerState(Packet<?> arg) {
        return HANDLER_STATE_MAP.get(arg.getClass());
    }

    static {
        STATES = new NetworkState[4];
        HANDLER_STATE_MAP = Maps.newHashMap();
        for (NetworkState lv : NetworkState.values()) {
            int i = lv.getId();
            if (i < -1 || i > 2) {
                throw new Error("Invalid protocol ID " + Integer.toString(i));
            }
            NetworkState.STATES[i - -1] = lv;
            lv.packetHandlers.forEach((arg22, arg3) -> arg3.getPacketTypes().forEach(arg2 -> {
                if (HANDLER_STATE_MAP.containsKey(arg2) && HANDLER_STATE_MAP.get(arg2) != lv) {
                    throw new IllegalStateException("Packet " + arg2 + " is already assigned to protocol " + (Object)((Object)HANDLER_STATE_MAP.get(arg2)) + " - can't reassign to " + (Object)((Object)lv));
                }
                HANDLER_STATE_MAP.put((Class<Packet<?>>)arg2, lv);
            }));
        }
    }

    static class PacketHandlerInitializer {
        private final Map<NetworkSide, PacketHandler<?>> packetHandlers = Maps.newEnumMap(NetworkSide.class);

        private PacketHandlerInitializer() {
        }

        public <T extends PacketListener> PacketHandlerInitializer setup(NetworkSide arg, PacketHandler<T> arg2) {
            this.packetHandlers.put(arg, arg2);
            return this;
        }
    }

    static class PacketHandler<T extends PacketListener> {
        private final Object2IntMap<Class<? extends Packet<T>>> packetIds = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), object2IntOpenHashMap -> object2IntOpenHashMap.defaultReturnValue(-1));
        private final List<Supplier<? extends Packet<T>>> packetFactories = Lists.newArrayList();

        private PacketHandler() {
        }

        public <P extends Packet<T>> PacketHandler<T> register(Class<P> arg, Supplier<P> supplier) {
            int i = this.packetFactories.size();
            int j = this.packetIds.put(arg, i);
            if (j != -1) {
                String string = "Packet " + arg + " is already registered to ID " + j;
                LogManager.getLogger().fatal(string);
                throw new IllegalArgumentException(string);
            }
            this.packetFactories.add(supplier);
            return this;
        }

        @Nullable
        public Integer getId(Class<?> arg) {
            int i = this.packetIds.getInt(arg);
            return i == -1 ? null : Integer.valueOf(i);
        }

        @Nullable
        public Packet<?> createPacket(int i) {
            Supplier<Packet<T>> supplier = this.packetFactories.get(i);
            return supplier != null ? supplier.get() : null;
        }

        public Iterable<Class<? extends Packet<?>>> getPacketTypes() {
            return Iterables.unmodifiableIterable((Iterable)this.packetIds.keySet());
        }
    }
}

