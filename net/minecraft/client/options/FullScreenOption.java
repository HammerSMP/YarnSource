/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.options;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(value=EnvType.CLIENT)
public class FullScreenOption
extends DoubleOption {
    public FullScreenOption(Window arg) {
        this(arg, arg.getMonitor());
    }

    private FullScreenOption(Window arg, @Nullable Monitor arg22) {
        super("options.fullscreen.resolution", -1.0, arg22 != null ? (double)(arg22.getVideoModeCount() - 1) : -1.0, 1.0f, arg3 -> {
            if (arg22 == null) {
                return -1.0;
            }
            Optional<VideoMode> optional = arg.getVideoMode();
            return optional.map(arg2 -> arg22.findClosestVideoModeIndex((VideoMode)arg2)).orElse(-1.0);
        }, (arg3, double_) -> {
            if (arg22 == null) {
                return;
            }
            if (double_ == -1.0) {
                arg.setVideoMode(Optional.empty());
            } else {
                arg.setVideoMode(Optional.of(arg22.getVideoMode(double_.intValue())));
            }
        }, (arg2, arg3) -> {
            if (arg22 == null) {
                return new TranslatableText("options.fullscreen.unavailable");
            }
            double d = arg3.get((GameOptions)arg2);
            if (d == -1.0) {
                return arg3.method_30501(new TranslatableText("options.fullscreen.current"));
            }
            return arg3.method_30501(new LiteralText(arg22.getVideoMode((int)d).toString()));
        });
    }
}

