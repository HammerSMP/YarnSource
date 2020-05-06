/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.ArgumentCommandNode
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.network.packet.s2c.play;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.server.command.CommandSource;

public class CommandTreeS2CPacket
implements Packet<ClientPlayPacketListener> {
    private RootCommandNode<CommandSource> commandTree;

    public CommandTreeS2CPacket() {
    }

    public CommandTreeS2CPacket(RootCommandNode<CommandSource> rootCommandNode) {
        this.commandTree = rootCommandNode;
    }

    @Override
    public void read(PacketByteBuf arg) throws IOException {
        CommandNodeData[] lvs = new CommandNodeData[arg.readVarInt()];
        ArrayDeque<CommandNodeData> deque = new ArrayDeque<CommandNodeData>(lvs.length);
        for (int i = 0; i < lvs.length; ++i) {
            lvs[i] = this.readCommandNode(arg);
            deque.add(lvs[i]);
        }
        while (!deque.isEmpty()) {
            boolean bl = false;
            Iterator iterator = deque.iterator();
            while (iterator.hasNext()) {
                CommandNodeData lv = (CommandNodeData)iterator.next();
                if (!lv.build(lvs)) continue;
                iterator.remove();
                bl = true;
            }
            if (bl) continue;
            throw new IllegalStateException("Server sent an impossible command tree");
        }
        this.commandTree = (RootCommandNode)lvs[arg.readVarInt()].node;
    }

    @Override
    public void write(PacketByteBuf arg) throws IOException {
        HashMap map = Maps.newHashMap();
        ArrayDeque<Object> deque = new ArrayDeque<Object>();
        deque.add(this.commandTree);
        while (!deque.isEmpty()) {
            CommandNode commandNode = (CommandNode)deque.pollFirst();
            if (map.containsKey((Object)commandNode)) continue;
            int i = map.size();
            map.put(commandNode, i);
            deque.addAll(commandNode.getChildren());
            if (commandNode.getRedirect() == null) continue;
            deque.add((Object)commandNode.getRedirect());
        }
        CommandNode[] commandNodes = new CommandNode[map.size()];
        for (Map.Entry entry : map.entrySet()) {
            commandNodes[((Integer)entry.getValue()).intValue()] = (CommandNode)entry.getKey();
        }
        arg.writeVarInt(commandNodes.length);
        for (CommandNode commandNode2 : commandNodes) {
            this.writeNode(arg, (CommandNode<CommandSource>)commandNode2, map);
        }
        arg.writeVarInt((Integer)map.get(this.commandTree));
    }

    private CommandNodeData readCommandNode(PacketByteBuf arg) {
        byte b = arg.readByte();
        int[] is = arg.readIntArray();
        int i = (b & 8) != 0 ? arg.readVarInt() : 0;
        ArgumentBuilder<CommandSource, ?> argumentBuilder = this.readArgumentBuilder(arg, b);
        return new CommandNodeData(argumentBuilder, b, i, is);
    }

    @Nullable
    private ArgumentBuilder<CommandSource, ?> readArgumentBuilder(PacketByteBuf arg, byte b) {
        int i = b & 3;
        if (i == 2) {
            String string = arg.readString(32767);
            ArgumentType<?> argumentType = ArgumentTypes.fromPacket(arg);
            if (argumentType == null) {
                return null;
            }
            RequiredArgumentBuilder requiredArgumentBuilder = RequiredArgumentBuilder.argument((String)string, argumentType);
            if ((b & 0x10) != 0) {
                requiredArgumentBuilder.suggests(SuggestionProviders.byId(arg.readIdentifier()));
            }
            return requiredArgumentBuilder;
        }
        if (i == 1) {
            return LiteralArgumentBuilder.literal((String)arg.readString(32767));
        }
        return null;
    }

    private void writeNode(PacketByteBuf arg, CommandNode<CommandSource> commandNode, Map<CommandNode<CommandSource>, Integer> map) {
        int b = 0;
        if (commandNode.getRedirect() != null) {
            b = (byte)(b | 8);
        }
        if (commandNode.getCommand() != null) {
            b = (byte)(b | 4);
        }
        if (commandNode instanceof RootCommandNode) {
            b = (byte)(b | 0);
        } else if (commandNode instanceof ArgumentCommandNode) {
            b = (byte)(b | 2);
            if (((ArgumentCommandNode)commandNode).getCustomSuggestions() != null) {
                b = (byte)(b | 0x10);
            }
        } else if (commandNode instanceof LiteralCommandNode) {
            b = (byte)(b | 1);
        } else {
            throw new UnsupportedOperationException("Unknown node type " + commandNode);
        }
        arg.writeByte(b);
        arg.writeVarInt(commandNode.getChildren().size());
        for (CommandNode commandNode2 : commandNode.getChildren()) {
            arg.writeVarInt(map.get((Object)commandNode2));
        }
        if (commandNode.getRedirect() != null) {
            arg.writeVarInt(map.get((Object)commandNode.getRedirect()));
        }
        if (commandNode instanceof ArgumentCommandNode) {
            ArgumentCommandNode argumentCommandNode = (ArgumentCommandNode)commandNode;
            arg.writeString(argumentCommandNode.getName());
            ArgumentTypes.toPacket(arg, argumentCommandNode.getType());
            if (argumentCommandNode.getCustomSuggestions() != null) {
                arg.writeIdentifier(SuggestionProviders.computeName((SuggestionProvider<CommandSource>)argumentCommandNode.getCustomSuggestions()));
            }
        } else if (commandNode instanceof LiteralCommandNode) {
            arg.writeString(((LiteralCommandNode)commandNode).getLiteral());
        }
    }

    @Override
    public void apply(ClientPlayPacketListener arg) {
        arg.onCommandTree(this);
    }

    @Environment(value=EnvType.CLIENT)
    public RootCommandNode<CommandSource> getCommandTree() {
        return this.commandTree;
    }

    static class CommandNodeData {
        @Nullable
        private final ArgumentBuilder<CommandSource, ?> argumentBuilder;
        private final byte flags;
        private final int redirectNodeIndex;
        private final int[] childNodeIndices;
        private CommandNode<CommandSource> node;

        private CommandNodeData(@Nullable ArgumentBuilder<CommandSource, ?> argumentBuilder, byte b, int i, int[] is) {
            this.argumentBuilder = argumentBuilder;
            this.flags = b;
            this.redirectNodeIndex = i;
            this.childNodeIndices = is;
        }

        public boolean build(CommandNodeData[] args) {
            if (this.node == null) {
                if (this.argumentBuilder == null) {
                    this.node = new RootCommandNode();
                } else {
                    if ((this.flags & 8) != 0) {
                        if (args[this.redirectNodeIndex].node == null) {
                            return false;
                        }
                        this.argumentBuilder.redirect(args[this.redirectNodeIndex].node);
                    }
                    if ((this.flags & 4) != 0) {
                        this.argumentBuilder.executes(commandContext -> 0);
                    }
                    this.node = this.argumentBuilder.build();
                }
            }
            for (int i : this.childNodeIndices) {
                if (args[i].node != null) continue;
                return false;
            }
            for (int j : this.childNodeIndices) {
                CommandNode<CommandSource> commandNode = args[j].node;
                if (commandNode instanceof RootCommandNode) continue;
                this.node.addChild(commandNode);
            }
            return true;
        }
    }
}

