/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 */
package net.minecraft.command.argument;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.argument.CoordinateArgument;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

public class AngleArgumentType
implements ArgumentType<Angle> {
    private static final Collection<String> EXAMPLES = Arrays.asList("0", "~", "~-5");
    public static final SimpleCommandExceptionType INCOMPLETE_ANGLE_EXCEPTION = new SimpleCommandExceptionType((Message)new TranslatableText("argument.angle.incomplete"));

    public static AngleArgumentType angle() {
        return new AngleArgumentType();
    }

    public static float getAngle(CommandContext<ServerCommandSource> context, String name) {
        return ((Angle)context.getArgument(name, Angle.class)).getAngle((ServerCommandSource)context.getSource());
    }

    public Angle parse(StringReader stringReader) throws CommandSyntaxException {
        if (!stringReader.canRead()) {
            throw INCOMPLETE_ANGLE_EXCEPTION.createWithContext((ImmutableStringReader)stringReader);
        }
        boolean bl = CoordinateArgument.isRelative(stringReader);
        float f = stringReader.canRead() && stringReader.peek() != ' ' ? stringReader.readFloat() : 0.0f;
        return new Angle(f, bl);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public /* synthetic */ Object parse(StringReader stringReader) throws CommandSyntaxException {
        return this.parse(stringReader);
    }

    public static final class Angle {
        private final float angle;
        private final boolean relative;

        private Angle(float angle, boolean relative) {
            this.angle = angle;
            this.relative = relative;
        }

        public float getAngle(ServerCommandSource source) {
            return MathHelper.wrapDegrees(this.relative ? this.angle + source.getRotation().y : this.angle);
        }
    }
}

