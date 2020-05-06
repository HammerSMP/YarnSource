/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package com.mojang.blaze3d.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class FramebufferInfo {
    public static int FRAME_BUFFER;
    public static int RENDER_BUFFER;
    public static int COLOR_ATTACHMENT;
    public static int DEPTH_ATTACHMENT;
    public static int FRAME_BUFFER_COMPLETE;
    public static int FRAME_BUFFER_INCOMPLETE_ATTACHMENT;
    public static int FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT;
    public static int FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER;
    public static int FRAME_BUFFER_INCOMPLETE_READ_BUFFER;
}

