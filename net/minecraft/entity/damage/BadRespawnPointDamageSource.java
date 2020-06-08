/*
 * Decompiled with CFR 0.149.
 */
package net.minecraft.entity.damage;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.text.TranslatableText;

public class BadRespawnPointDamageSource
extends DamageSource {
    protected BadRespawnPointDamageSource() {
        super("badRespawnPoint");
        this.setScaledWithDifficulty();
        this.setExplosive();
    }

    @Override
    public Text getDeathMessage(LivingEntity arg2) {
        MutableText lv = Texts.bracketed(new TranslatableText("death.attack.badRespawnPoint.link")).styled(arg -> arg.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("MCPE-28723"))));
        return new TranslatableText("death.attack.badRespawnPoint.message", arg2.getDisplayName(), lv);
    }
}

