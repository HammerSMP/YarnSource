/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

public class Team
extends AbstractTeam {
    private final Scoreboard scoreboard;
    private final String name;
    private final Set<String> playerList = Sets.newHashSet();
    private Text displayName;
    private Text prefix = LiteralText.EMPTY;
    private Text suffix = LiteralText.EMPTY;
    private boolean friendlyFire = true;
    private boolean showFriendlyInvisibles = true;
    private AbstractTeam.VisibilityRule nameTagVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
    private AbstractTeam.VisibilityRule deathMessageVisibilityRule = AbstractTeam.VisibilityRule.ALWAYS;
    private Formatting color = Formatting.RESET;
    private AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.ALWAYS;
    private final Style field_24195;

    public Team(Scoreboard arg, String string) {
        this.scoreboard = arg;
        this.name = string;
        this.displayName = new LiteralText(string);
        this.field_24195 = Style.EMPTY.withInsertion(string).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(string)));
    }

    @Override
    public String getName() {
        return this.name;
    }

    public Text getDisplayName() {
        return this.displayName;
    }

    public MutableText getFormattedName() {
        MutableText lv = Texts.bracketed(this.displayName.shallowCopy().fillStyle(this.field_24195));
        Formatting lv2 = this.getColor();
        if (lv2 != Formatting.RESET) {
            lv.formatted(lv2);
        }
        return lv;
    }

    public void setDisplayName(Text arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.displayName = arg;
        this.scoreboard.updateScoreboardTeam(this);
    }

    public void setPrefix(@Nullable Text arg) {
        this.prefix = arg == null ? LiteralText.EMPTY : arg;
        this.scoreboard.updateScoreboardTeam(this);
    }

    public Text getPrefix() {
        return this.prefix;
    }

    public void setSuffix(@Nullable Text arg) {
        this.suffix = arg == null ? LiteralText.EMPTY : arg;
        this.scoreboard.updateScoreboardTeam(this);
    }

    public Text getSuffix() {
        return this.suffix;
    }

    @Override
    public Collection<String> getPlayerList() {
        return this.playerList;
    }

    @Override
    public MutableText modifyText(Text arg) {
        MutableText lv = new LiteralText("").append(this.prefix).append(arg).append(this.suffix);
        Formatting lv2 = this.getColor();
        if (lv2 != Formatting.RESET) {
            lv.formatted(lv2);
        }
        return lv;
    }

    public static MutableText modifyText(@Nullable AbstractTeam arg, Text arg2) {
        if (arg == null) {
            return arg2.shallowCopy();
        }
        return arg.modifyText(arg2);
    }

    @Override
    public boolean isFriendlyFireAllowed() {
        return this.friendlyFire;
    }

    public void setFriendlyFireAllowed(boolean bl) {
        this.friendlyFire = bl;
        this.scoreboard.updateScoreboardTeam(this);
    }

    @Override
    public boolean shouldShowFriendlyInvisibles() {
        return this.showFriendlyInvisibles;
    }

    public void setShowFriendlyInvisibles(boolean bl) {
        this.showFriendlyInvisibles = bl;
        this.scoreboard.updateScoreboardTeam(this);
    }

    @Override
    public AbstractTeam.VisibilityRule getNameTagVisibilityRule() {
        return this.nameTagVisibilityRule;
    }

    @Override
    public AbstractTeam.VisibilityRule getDeathMessageVisibilityRule() {
        return this.deathMessageVisibilityRule;
    }

    public void setNameTagVisibilityRule(AbstractTeam.VisibilityRule arg) {
        this.nameTagVisibilityRule = arg;
        this.scoreboard.updateScoreboardTeam(this);
    }

    public void setDeathMessageVisibilityRule(AbstractTeam.VisibilityRule arg) {
        this.deathMessageVisibilityRule = arg;
        this.scoreboard.updateScoreboardTeam(this);
    }

    @Override
    public AbstractTeam.CollisionRule getCollisionRule() {
        return this.collisionRule;
    }

    public void setCollisionRule(AbstractTeam.CollisionRule arg) {
        this.collisionRule = arg;
        this.scoreboard.updateScoreboardTeam(this);
    }

    public int getFriendlyFlagsBitwise() {
        int i = 0;
        if (this.isFriendlyFireAllowed()) {
            i |= 1;
        }
        if (this.shouldShowFriendlyInvisibles()) {
            i |= 2;
        }
        return i;
    }

    @Environment(value=EnvType.CLIENT)
    public void setFriendlyFlagsBitwise(int i) {
        this.setFriendlyFireAllowed((i & 1) > 0);
        this.setShowFriendlyInvisibles((i & 2) > 0);
    }

    public void setColor(Formatting arg) {
        this.color = arg;
        this.scoreboard.updateScoreboardTeam(this);
    }

    @Override
    public Formatting getColor() {
        return this.color;
    }
}

