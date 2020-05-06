/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.minecraft.client.render;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormatElement;

@Environment(value=EnvType.CLIENT)
public class VertexFormats {
    public static final VertexFormatElement POSITION_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.POSITION, 3);
    public static final VertexFormatElement COLOR_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.UBYTE, VertexFormatElement.Type.COLOR, 4);
    public static final VertexFormatElement TEXTURE_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.FLOAT, VertexFormatElement.Type.UV, 2);
    public static final VertexFormatElement OVERLAY_ELEMENT = new VertexFormatElement(1, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2);
    public static final VertexFormatElement LIGHT_ELEMENT = new VertexFormatElement(2, VertexFormatElement.Format.SHORT, VertexFormatElement.Type.UV, 2);
    public static final VertexFormatElement NORMAL_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.NORMAL, 3);
    public static final VertexFormatElement PADDING_ELEMENT = new VertexFormatElement(0, VertexFormatElement.Format.BYTE, VertexFormatElement.Type.PADDING, 1);
    public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT_NORMAL = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)COLOR_ELEMENT).add((Object)TEXTURE_ELEMENT).add((Object)LIGHT_ELEMENT).add((Object)NORMAL_ELEMENT).add((Object)PADDING_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)COLOR_ELEMENT).add((Object)TEXTURE_ELEMENT).add((Object)OVERLAY_ELEMENT).add((Object)LIGHT_ELEMENT).add((Object)NORMAL_ELEMENT).add((Object)PADDING_ELEMENT).build());
    @Deprecated
    public static final VertexFormat POSITION_TEXTURE_COLOR_LIGHT = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)TEXTURE_ELEMENT).add((Object)COLOR_ELEMENT).add((Object)LIGHT_ELEMENT).build());
    public static final VertexFormat POSITION = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)COLOR_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_LIGHT = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)COLOR_ELEMENT).add((Object)LIGHT_ELEMENT).build());
    public static final VertexFormat POSITION_TEXTURE = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)TEXTURE_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_TEXTURE = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)COLOR_ELEMENT).add((Object)TEXTURE_ELEMENT).build());
    @Deprecated
    public static final VertexFormat POSITION_TEXTURE_COLOR = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)TEXTURE_ELEMENT).add((Object)COLOR_ELEMENT).build());
    public static final VertexFormat POSITION_COLOR_TEXTURE_LIGHT = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)COLOR_ELEMENT).add((Object)TEXTURE_ELEMENT).add((Object)LIGHT_ELEMENT).build());
    @Deprecated
    public static final VertexFormat POSITION_TEXTURE_LIGHT_COLOR = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)TEXTURE_ELEMENT).add((Object)LIGHT_ELEMENT).add((Object)COLOR_ELEMENT).build());
    @Deprecated
    public static final VertexFormat POSITION_TEXTURE_COLOR_NORMAL = new VertexFormat((ImmutableList<VertexFormatElement>)ImmutableList.builder().add((Object)POSITION_ELEMENT).add((Object)TEXTURE_ELEMENT).add((Object)COLOR_ELEMENT).add((Object)NORMAL_ELEMENT).add((Object)PADDING_ELEMENT).build());
}

