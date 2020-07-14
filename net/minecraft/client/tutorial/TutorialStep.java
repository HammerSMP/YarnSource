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

    private <T extends TutorialStepHandler> TutorialStep(String name, Function<TutorialManager, T> factory) {
        this.name = name;
        this.handlerFactory = factory;
    }

    public TutorialStepHandler createHandler(TutorialManager manager) {
        return this.handlerFactory.apply(manager);
    }

    public String getName() {
        return this.name;
    }

    public static TutorialStep byName(String name) {
        for (TutorialStep lv : TutorialStep.values()) {
            if (!lv.name.equals(name)) continue;
            return lv;
        }
        return NONE;
    }
}

