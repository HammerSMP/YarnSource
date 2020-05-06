/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.scoreboard;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class ScoreboardObjective {
    private final Scoreboard scoreboard;
    private final String name;
    private final ScoreboardCriterion criterion;
    private Text displayName;
    private Text field_24194;
    private ScoreboardCriterion.RenderType renderType;

    public ScoreboardObjective(Scoreboard arg, String string, ScoreboardCriterion arg2, Text arg3, ScoreboardCriterion.RenderType arg4) {
        this.scoreboard = arg;
        this.name = string;
        this.criterion = arg2;
        this.displayName = arg3;
        this.field_24194 = this.method_27441();
        this.renderType = arg4;
    }

    @Environment(value=EnvType.CLIENT)
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public String getName() {
        return this.name;
    }

    public ScoreboardCriterion getCriterion() {
        return this.criterion;
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    private Text method_27441() {
        return Texts.bracketed(this.displayName.shallowCopy().styled(arg -> arg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(this.name)))));
    }

    public Text toHoverableText() {
        return this.field_24194;
    }

    public void setDisplayName(Text arg) {
        this.displayName = arg;
        this.field_24194 = this.method_27441();
        this.scoreboard.updateExistingObjective(this);
    }

    public ScoreboardCriterion.RenderType getRenderType() {
        return this.renderType;
    }

    public void setRenderType(ScoreboardCriterion.RenderType arg) {
        this.renderType = arg;
        this.scoreboard.updateExistingObjective(this);
    }
}

