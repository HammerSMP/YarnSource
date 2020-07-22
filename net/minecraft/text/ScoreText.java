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
import net.minecraft.command.argument.EntityArgumentType;
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
    private static EntitySelector parseEntitySelector(String name) {
        try {
            return new EntitySelectorReader(new StringReader(name)).read();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            return null;
        }
    }

    public ScoreText(String name, String objective) {
        this(name, ScoreText.parseEntitySelector(name), objective);
    }

    private ScoreText(String name, @Nullable EntitySelector selector, String objective) {
        this.name = name;
        this.selector = selector;
        this.objective = objective;
    }

    public String getName() {
        return this.name;
    }

    public String getObjective() {
        return this.objective;
    }

    private String getPlayerName(ServerCommandSource source) throws CommandSyntaxException {
        List<? extends Entity> list;
        if (this.selector != null && !(list = this.selector.getEntities(source)).isEmpty()) {
            if (list.size() != 1) {
                throw EntityArgumentType.TOO_MANY_ENTITIES_EXCEPTION.create();
            }
            return list.get(0).getEntityName();
        }
        return this.name;
    }

    private String getScore(String playerName, ServerCommandSource source) {
        ScoreboardObjective lv2;
        ServerScoreboard lv;
        MinecraftServer minecraftServer = source.getMinecraftServer();
        if (minecraftServer != null && (lv = minecraftServer.getScoreboard()).playerHasObjective(playerName, lv2 = lv.getNullableObjective(this.objective))) {
            ScoreboardPlayerScore lv3 = lv.getPlayerScore(playerName, lv2);
            return Integer.toString(lv3.getScore());
        }
        return "";
    }

    @Override
    public ScoreText copy() {
        return new ScoreText(this.name, this.selector, this.objective);
    }

    @Override
    public MutableText parse(@Nullable ServerCommandSource source, @Nullable Entity sender, int depth) throws CommandSyntaxException {
        if (source == null) {
            return new LiteralText("");
        }
        String string = this.getPlayerName(source);
        String string2 = sender != null && string.equals("*") ? sender.getEntityName() : string;
        return new LiteralText(this.getScore(string2, source));
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

