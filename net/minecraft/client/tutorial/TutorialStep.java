/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.tutorial;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.tutorial.CraftPlanksTutorialStepHandler;
import net.minecraft.client.tutorial.FindTreeTutorialStepHandler;
import net.minecraft.client.tutorial.MovementTutorialStepHandler;
import net.minecraft.client.tutorial.NoneTutorialStepHandler;
import net.minecraft.client.tutorial.OpenInventoryTutorialStepHandler;
import net.minecraft.client.tutorial.PunchTreeTutorialStepHandler;
import net.minecraft.client.tutorial.TutorialManager;
import net.minecraft.client.tutorial.TutorialStepHandler;

@Environment(value=EnvType.CLIENT)
public enum TutorialStep {
    MOVEMENT("movement", MovementTutorialStepHandler::new),
    FIND_TREE("find_tree", FindTreeTutorialStepHandler::new),
    PUNCH_TREE("punch_tree", PunchTreeTutorialStepHandler::new),
    OPEN_INVENTORY("open_inventory", OpenInventoryTutorialStepHandler::new),
    CRAFT_PLANKS("craft_planks", CraftPlanksTutorialStepHandler::new),
    NONE("none", NoneTutorialStepHandler::new);

    private final String name;
    private final Function<TutorialManager, ? extends TutorialStepHandler> handlerFactory;

    private <T extends TutorialStepHandler> TutorialStep(String string2, Function<TutorialManager, T> function) {
        this.name = string2;
        this.handlerFactory = function;
    }

    public TutorialStepHandler createHandler(TutorialManager arg) {
        return this.handlerFactory.apply(arg);
    }

    public String getName() {
        return this.name;
    }

    public static TutorialStep byName(String string) {
        for (TutorialStep lv : TutorialStep.values()) {
            if (!lv.name.equals(string)) continue;
            return lv;
        }
        return NONE;
    }
}

