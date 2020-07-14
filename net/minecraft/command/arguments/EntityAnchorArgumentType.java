/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

public class EntityAnchorArgumentType
implements ArgumentType<EntityAnchor> {
    private static final Collection<String> EXAMPLES = Arrays.asList("eyes", "feet");
    private static final DynamicCommandExceptionType INVALID_ANCHOR_EXCEPTION = new DynamicCommandExceptionType(object -> new TranslatableText("argument.anchor.invalid", object));

    public static EntityAnchor getEntityAnchor(CommandContext<ServerCommandSource> commandContext, String string) {
        return (EntityAnchor)((Object)commandContext.getArgument(string, EntityAnchor.class));
    }

    public static EntityAnchorArgumentType entityAnchor() {
        return new EntityAnchorArgumentType();
    }

    public EntityAnchor parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        String string = stringReader.readUnquotedString();
        EntityAnchor lv = EntityAnchor.fromId(string);
        if (lv == null) {
            stringReader.setCursor(i);
            throw INVALID_ANCHOR_EXCEPTION.createWithContext((ImmutableStringReader)stringReader, (Object)string);
        }
        return lv;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(EntityAnchor.anchors.keySet(), builder);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static enum EntityAnchor {
        FEET("feet", (arg, arg2) -> arg),
        EYES("eyes", (arg, arg2) -> new Vec3d(arg.x, arg.y + (double)arg2.getStandingEyeHeight(), arg.z));

        private static final Map<String, EntityAnchor> anchors;
        private final String id;
        private final BiFunction<Vec3d, Entity, Vec3d> offset;

        private EntityAnchor(String id, BiFunction<Vec3d, Entity, Vec3d> offset) {
            this.id = id;
            this.offset = offset;
        }

        @Nullable
        public static EntityAnchor fromId(String id) {
            return anchors.get(id);
        }

        public Vec3d positionAt(Entity arg) {
            return this.offset.apply(arg.getPos(), arg);
        }

        public Vec3d positionAt(ServerCommandSource arg) {
            Entity lv = arg.getEntity();
            if (lv == null) {
                return arg.getPosition();
            }
            return this.offset.apply(arg.getPosition(), lv);
        }

        static {
            anchors = Util.make(Maps.newHashMap(), hashMap -> {
                for (EntityAnchor lv : EntityAnchor.values()) {
                    hashMap.put(lv.id, lv);
                }
            });
        }
    }
}

