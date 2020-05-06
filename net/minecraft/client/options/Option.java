/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.options.AoOption;
import net.minecraft.client.options.AttackIndicator;
import net.minecraft.client.options.BooleanOption;
import net.minecraft.client.options.ChatVisibility;
import net.minecraft.client.options.CloudRenderMode;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.LogarithmicOption;
import net.minecraft.client.options.NarratorOption;
import net.minecraft.client.options.ParticlesOption;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.Window;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public abstract class Option {
    public static final DoubleOption BIOME_BLEND_RADIUS = new DoubleOption("options.biomeBlendRadius", 0.0, 7.0, 1.0f, arg -> arg.biomeBlendRadius, (arg, arg2) -> {
        arg.biomeBlendRadius = MathHelper.clamp((int)arg2.doubleValue(), 0, 7);
        MinecraftClient.getInstance().worldRenderer.reload();
    }, (arg, arg2) -> {
        double d = arg2.get((GameOptions)arg);
        MutableText lv = arg2.getDisplayPrefix();
        int i = (int)d * 2 + 1;
        return lv.append(new TranslatableText("options.biomeBlendRadius." + i));
    });
    public static final DoubleOption CHAT_HEIGHT_FOCUSED = new DoubleOption("options.chat.height.focused", 0.0, 1.0, 0.0f, arg -> arg.chatHeightFocused, (arg, arg2) -> {
        arg.chatHeightFocused = arg2;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        return arg2.getDisplayPrefix().append(ChatHud.getHeight(d) + "px");
    });
    public static final DoubleOption SATURATION = new DoubleOption("options.chat.height.unfocused", 0.0, 1.0, 0.0f, arg -> arg.chatHeightUnfocused, (arg, arg2) -> {
        arg.chatHeightUnfocused = arg2;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        return arg2.getDisplayPrefix().append(ChatHud.getHeight(d) + "px");
    });
    public static final DoubleOption CHAT_OPACITY = new DoubleOption("options.chat.opacity", 0.0, 1.0, 0.0f, arg -> arg.chatOpacity, (arg, arg2) -> {
        arg.chatOpacity = arg2;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        return arg2.getDisplayPrefix().append((int)(d * 90.0 + 10.0) + "%");
    });
    public static final DoubleOption CHAT_SCALE = new DoubleOption("options.chat.scale", 0.0, 1.0, 0.0f, arg -> arg.chatScale, (arg, arg2) -> {
        arg.chatScale = arg2;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        MutableText lv = arg2.getDisplayPrefix();
        if (d == 0.0) {
            return lv.append(ScreenTexts.OFF);
        }
        return lv.append((int)(d * 100.0) + "%");
    });
    public static final DoubleOption CHAT_WIDTH = new DoubleOption("options.chat.width", 0.0, 1.0, 0.0f, arg -> arg.chatWidth, (arg, arg2) -> {
        arg.chatWidth = arg2;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        return arg2.getDisplayPrefix().append(ChatHud.getWidth(d) + "px");
    });
    public static final DoubleOption CHAT_LINE_SPACING = new DoubleOption("options.chat.line_spacing", 0.0, 1.0, 0.0f, arg -> arg.chatLineSpacing, (arg, arg2) -> {
        arg.chatLineSpacing = arg2;
    }, (arg, arg2) -> arg2.getDisplayPrefix().append((int)(arg2.getRatio(arg2.get((GameOptions)arg)) * 100.0) + "%"));
    public static final DoubleOption CHAT_DELAY_INSTANT = new DoubleOption("options.chat.delay_instant", 0.0, 6.0, 0.1f, arg -> arg.chatDelay, (arg, arg2) -> {
        arg.chatDelay = arg2;
    }, (arg, arg2) -> {
        double d = arg2.get((GameOptions)arg);
        if (d <= 0.0) {
            return new TranslatableText("options.chat.delay_none");
        }
        return new TranslatableText("options.chat.delay", String.format("%.1f", d));
    });
    public static final DoubleOption FOV = new DoubleOption("options.fov", 30.0, 110.0, 1.0f, arg -> arg.fov, (arg, arg2) -> {
        arg.fov = arg2;
    }, (arg, arg2) -> {
        double d = arg2.get((GameOptions)arg);
        MutableText lv = arg2.getDisplayPrefix();
        if (d == 70.0) {
            return lv.append(new TranslatableText("options.fov.min"));
        }
        if (d == arg2.getMax()) {
            return lv.append(new TranslatableText("options.fov.max"));
        }
        return lv.append(Integer.toString((int)d));
    });
    public static final DoubleOption FRAMERATE_LIMIT = new DoubleOption("options.framerateLimit", 10.0, 260.0, 10.0f, arg -> arg.maxFps, (arg, arg2) -> {
        arg.maxFps = (int)arg2.doubleValue();
        MinecraftClient.getInstance().getWindow().setFramerateLimit(arg.maxFps);
    }, (arg, arg2) -> {
        double d = arg2.get((GameOptions)arg);
        MutableText lv = arg2.getDisplayPrefix();
        if (d == arg2.getMax()) {
            return lv.append(new TranslatableText("options.framerateLimit.max"));
        }
        return lv.append(new TranslatableText("options.framerate", (int)d));
    });
    public static final DoubleOption GAMMA = new DoubleOption("options.gamma", 0.0, 1.0, 0.0f, arg -> arg.gamma, (arg, arg2) -> {
        arg.gamma = arg2;
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        MutableText lv = arg2.getDisplayPrefix();
        if (d == 0.0) {
            return lv.append(new TranslatableText("options.gamma.min"));
        }
        if (d == 1.0) {
            return lv.append(new TranslatableText("options.gamma.max"));
        }
        return lv.append("+" + (int)(d * 100.0) + "%");
    });
    public static final DoubleOption MIPMAP_LEVELS = new DoubleOption("options.mipmapLevels", 0.0, 4.0, 1.0f, arg -> arg.mipmapLevels, (arg, arg2) -> {
        arg.mipmapLevels = (int)arg2.doubleValue();
    }, (arg, arg2) -> {
        double d = arg2.get((GameOptions)arg);
        MutableText lv = arg2.getDisplayPrefix();
        if (d == 0.0) {
            return lv.append(ScreenTexts.OFF);
        }
        return lv.append(Integer.toString((int)d));
    });
    public static final DoubleOption MOUSE_WHEEL_SENSITIVITY = new LogarithmicOption("options.mouseWheelSensitivity", 0.01, 10.0, 0.01f, arg -> arg.mouseWheelSensitivity, (arg, arg2) -> {
        arg.mouseWheelSensitivity = arg2;
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        return arg2.getDisplayPrefix().append(String.format("%.2f", arg2.getValue(d)));
    });
    public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", arg -> arg.rawMouseInput, (arg, arg2) -> {
        arg.rawMouseInput = arg2;
        Window lv = MinecraftClient.getInstance().getWindow();
        if (lv != null) {
            lv.setRawMouseMotion((boolean)arg2);
        }
    });
    public static final DoubleOption RENDER_DISTANCE = new DoubleOption("options.renderDistance", 2.0, 16.0, 1.0f, arg -> arg.viewDistance, (arg, arg2) -> {
        arg.viewDistance = (int)arg2.doubleValue();
        MinecraftClient.getInstance().worldRenderer.scheduleTerrainUpdate();
    }, (arg, arg2) -> {
        double d = arg2.get((GameOptions)arg);
        return arg2.getDisplayPrefix().append(new TranslatableText("options.chunks", (int)d));
    });
    public static final DoubleOption ENTITY_DISTANCE_SCALING = new DoubleOption("options.entityDistanceScaling", 0.5, 5.0, 0.25f, arg -> arg.entityDistanceScaling, (arg, arg2) -> {
        arg.entityDistanceScaling = (float)arg2.doubleValue();
    }, (arg, arg2) -> {
        double d = arg2.get((GameOptions)arg);
        return arg2.getDisplayPrefix().append(new TranslatableText("options.entityDistancePercent", (int)(d * 100.0)));
    });
    public static final DoubleOption SENSITIVITY = new DoubleOption("options.sensitivity", 0.0, 1.0, 0.0f, arg -> arg.mouseSensitivity, (arg, arg2) -> {
        arg.mouseSensitivity = arg2;
    }, (arg, arg2) -> {
        double d = arg2.getRatio(arg2.get((GameOptions)arg));
        MutableText lv = arg2.getDisplayPrefix();
        if (d == 0.0) {
            return lv.append(new TranslatableText("options.sensitivity.min"));
        }
        if (d == 1.0) {
            return lv.append(new TranslatableText("options.sensitivity.max"));
        }
        return lv.append((int)(d * 200.0) + "%");
    });
    public static final DoubleOption TEXT_BACKGROUND_OPACITY = new DoubleOption("options.accessibility.text_background_opacity", 0.0, 1.0, 0.0f, arg -> arg.textBackgroundOpacity, (arg, arg2) -> {
        arg.textBackgroundOpacity = arg2;
        MinecraftClient.getInstance().inGameHud.getChatHud().reset();
    }, (arg, arg2) -> arg2.getDisplayPrefix().append((int)(arg2.getRatio(arg2.get((GameOptions)arg)) * 100.0) + "%"));
    public static final CyclingOption AO = new CyclingOption("options.ao", (arg, integer) -> {
        arg.ao = AoOption.getOption(arg.ao.getValue() + integer);
        MinecraftClient.getInstance().worldRenderer.reload();
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.ao.getTranslationKey())));
    public static final CyclingOption ATTACK_INDICATOR = new CyclingOption("options.attackIndicator", (arg, integer) -> {
        arg.attackIndicator = AttackIndicator.byId(arg.attackIndicator.getId() + integer);
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.attackIndicator.getTranslationKey())));
    public static final CyclingOption VISIBILITY = new CyclingOption("options.chat.visibility", (arg, integer) -> {
        arg.chatVisibility = ChatVisibility.byId((arg.chatVisibility.getId() + integer) % 3);
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.chatVisibility.getTranslationKey())));
    public static final CyclingOption GRAPHICS = new CyclingOption("options.graphics", (arg, integer) -> {
        arg.fancyGraphics = !arg.fancyGraphics;
        MinecraftClient.getInstance().worldRenderer.reload();
    }, (arg, arg2) -> {
        if (arg.fancyGraphics) {
            return arg2.getDisplayPrefix().append(new TranslatableText("options.graphics.fancy"));
        }
        return arg2.getDisplayPrefix().append(new TranslatableText("options.graphics.fast"));
    });
    public static final CyclingOption GUI_SCALE = new CyclingOption("options.guiScale", (arg, integer) -> {
        arg.guiScale = Integer.remainderUnsigned(arg.guiScale + integer, MinecraftClient.getInstance().getWindow().calculateScaleFactor(0, MinecraftClient.getInstance().forcesUnicodeFont()) + 1);
    }, (arg, arg2) -> {
        MutableText lv = arg2.getDisplayPrefix();
        if (arg.guiScale == 0) {
            return lv.append(new TranslatableText("options.guiScale.auto"));
        }
        return lv.append(Integer.toString(arg.guiScale));
    });
    public static final CyclingOption MAIN_HAND = new CyclingOption("options.mainHand", (arg, integer) -> {
        arg.mainArm = arg.mainArm.getOpposite();
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(arg.mainArm.method_27301()));
    public static final CyclingOption NARRATOR = new CyclingOption("options.narrator", (arg, integer) -> {
        arg.narrator = NarratorManager.INSTANCE.isActive() ? NarratorOption.byId(arg.narrator.getId() + integer) : NarratorOption.OFF;
        NarratorManager.INSTANCE.addToast(arg.narrator);
    }, (arg, arg2) -> {
        if (NarratorManager.INSTANCE.isActive()) {
            return arg2.getDisplayPrefix().append(arg.narrator.getTranslationKey());
        }
        return arg2.getDisplayPrefix().append(new TranslatableText("options.narrator.notavailable"));
    });
    public static final CyclingOption PARTICLES = new CyclingOption("options.particles", (arg, integer) -> {
        arg.particles = ParticlesOption.byId(arg.particles.getId() + integer);
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.particles.getTranslationKey())));
    public static final CyclingOption CLOUDS = new CyclingOption("options.renderClouds", (arg, integer) -> {
        arg.cloudRenderMode = CloudRenderMode.getOption(arg.cloudRenderMode.getValue() + integer);
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.cloudRenderMode.getTranslationKey())));
    public static final CyclingOption TEXT_BACKGROUND = new CyclingOption("options.accessibility.text_background", (arg, integer) -> {
        arg.backgroundForChatOnly = !arg.backgroundForChatOnly;
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere")));
    public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", arg -> arg.autoJump, (arg, arg2) -> {
        arg.autoJump = arg2;
    });
    public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", arg -> arg.autoSuggestions, (arg, arg2) -> {
        arg.autoSuggestions = arg2;
    });
    public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", arg -> arg.chatColors, (arg, arg2) -> {
        arg.chatColors = arg2;
    });
    public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", arg -> arg.chatLinks, (arg, arg2) -> {
        arg.chatLinks = arg2;
    });
    public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", arg -> arg.chatLinksPrompt, (arg, arg2) -> {
        arg.chatLinksPrompt = arg2;
    });
    public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", arg -> arg.discreteMouseScroll, (arg, arg2) -> {
        arg.discreteMouseScroll = arg2;
    });
    public static final BooleanOption VSYNC = new BooleanOption("options.vsync", arg -> arg.enableVsync, (arg, arg2) -> {
        arg.enableVsync = arg2;
        if (MinecraftClient.getInstance().getWindow() != null) {
            MinecraftClient.getInstance().getWindow().setVsync(arg.enableVsync);
        }
    });
    public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", arg -> arg.entityShadows, (arg, arg2) -> {
        arg.entityShadows = arg2;
    });
    public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", arg -> arg.forceUnicodeFont, (arg, arg2) -> {
        arg.forceUnicodeFont = arg2;
        MinecraftClient lv = MinecraftClient.getInstance();
        if (lv.getWindow() != null) {
            lv.initFont((boolean)arg2);
        }
    });
    public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", arg -> arg.invertYMouse, (arg, arg2) -> {
        arg.invertYMouse = arg2;
    });
    public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", arg -> arg.realmsNotifications, (arg, arg2) -> {
        arg.realmsNotifications = arg2;
    });
    public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", arg -> arg.reducedDebugInfo, (arg, arg2) -> {
        arg.reducedDebugInfo = arg2;
    });
    public static final BooleanOption SUBTITLES = new BooleanOption("options.showSubtitles", arg -> arg.showSubtitles, (arg, arg2) -> {
        arg.showSubtitles = arg2;
    });
    public static final BooleanOption SNOOPER = new BooleanOption("options.snooper", arg -> {
        if (arg.snooperEnabled) {
            // empty if block
        }
        return false;
    }, (arg, arg2) -> {
        arg.snooperEnabled = arg2;
    });
    public static final CyclingOption SNEAK_TOGGLED = new CyclingOption("key.sneak", (arg, integer) -> {
        arg.sneakToggled = !arg.sneakToggled;
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.sneakToggled ? "options.key.toggle" : "options.key.hold")));
    public static final CyclingOption SPRINT_TOGGLED = new CyclingOption("key.sprint", (arg, integer) -> {
        arg.sprintToggled = !arg.sprintToggled;
    }, (arg, arg2) -> arg2.getDisplayPrefix().append(new TranslatableText(arg.sprintToggled ? "options.key.toggle" : "options.key.hold")));
    public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", arg -> arg.touchscreen, (arg, arg2) -> {
        arg.touchscreen = arg2;
    });
    public static final BooleanOption FULLSCREEN = new BooleanOption("options.fullscreen", arg -> arg.fullscreen, (arg, arg2) -> {
        arg.fullscreen = arg2;
        MinecraftClient lv = MinecraftClient.getInstance();
        if (lv.getWindow() != null && lv.getWindow().isFullscreen() != arg.fullscreen) {
            lv.getWindow().toggleFullscreen();
            arg.fullscreen = lv.getWindow().isFullscreen();
        }
    });
    public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", arg -> arg.bobView, (arg, arg2) -> {
        arg.bobView = arg2;
    });
    private final String key;

    public Option(String string) {
        this.key = string;
    }

    public abstract AbstractButtonWidget createButton(GameOptions var1, int var2, int var3, int var4);

    public MutableText getDisplayPrefix() {
        return new TranslatableText(this.key).append(": ");
    }
}

