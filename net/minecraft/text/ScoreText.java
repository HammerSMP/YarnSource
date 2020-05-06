/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package net.minecraft.text;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.ParsableText;

public class ScoreText
extends BaseText
implements ParsableText {
    private final String name;
    @Nullable
    private final EntitySelector selector;
    private final String objective;

    @Nullable
    private static EntitySelector parseEntitySelector(String string) {
        try {
            return new EntitySelectorReader(new StringReader(string)).read();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public ScoreText(String string, String string2) {
        this(string, ScoreText.parseEntitySelector(string), string2);
    }

    private ScoreText(String string, @Nullable EntitySelector arg, String string2) {
        this.name = string;
        this.selector = arg;
        this.objective = string2;
    }

    public String getName() {
        return this.name;
    }

    public String getObjective() {
        return this.objective;
    }

    private String getPlayerName(ServerCommandSource arg) throws CommandSyntaxException {
        List<? extends Entity> list;
        if (this.selector != null && !(list = this.selector.getEntities(arg)).isEmpty()) {
            if (list.size() != 1) {
                throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
            }
            return list.get(0).getEntityName();
        }
        return this.name;
    }

    private String getScore(String string, ServerCommandSource arg) {
        ScoreboardObjective lv2;
        ServerScoreboard lv;
        MinecraftServer minecraftServer = arg.getMinecraftServer();
        if (minecraftServer != null && (lv = minecraftServer.getScoreboard()).playerHasObjective(string, lv2 = lv.getNullableObjective(this.objective))) {
            ScoreboardPlayerScore lv3 = lv.getPlayerScore(string, lv2);
            return Integer.toString(lv3.getScore());
        }
        return "";
    }

    @Override
    public ScoreText copy() {
        return new ScoreText(this.name, this.selector, this.objective);
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource arg, @Nullable Entity arg2, int i) throws CommandSyntaxException {
        if (arg == null) {
            return new LiteralText("");
        }
        String string = this.getPlayerName(arg);
        String string2 = arg2 != null && string.equals("*") ? arg2.getEntityName() : string;
        return new LiteralText(this.getScore(string2, arg));
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof ScoreText) {
            ScoreText lv = (ScoreText)object;
            return this.name.equals(lv.name) && this.objective.equals(lv.objective) && super.equals(object);
        }
        return false;
    }

    @Override
    public String toString() {
        return "ScoreComponent{name='" + this.name + '\'' + "objective='" + this.objective + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }

    @Override
    public /* synthetic */ BaseText copy() {
        return this.copy();
    }

    @Override
    public /* synthetic */ MutableText copy() {
        return this.copy();
    }
}

