/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(value=EnvType.CLIENT)
public class ShaderParseException
extends IOException {
    private final List<JsonStackTrace> traces = Lists.newArrayList();
    private final String message;

    public ShaderParseException(String message) {
        this.traces.add(new JsonStackTrace());
        this.message = message;
    }

    public ShaderParseException(String message, Throwable cause) {
        super(cause);
        this.traces.add(new JsonStackTrace());
        this.message = message;
    }

    public void addFaultyElement(String jsonKey) {
        this.traces.get(0).add(jsonKey);
    }

    public void addFaultyFile(String path) {
        this.traces.get(0).fileName = path;
        this.traces.add(0, new JsonStackTrace());
    }

    @Override
    public String getMessage() {
        return "Invalid " + this.traces.get(this.traces.size() - 1) + ": " + this.message;
    }

    public static ShaderParseException wrap(Exception cause) {
        if (cause instanceof ShaderParseException) {
            return (ShaderParseException)cause;
        }
        String string = cause.getMessage();
        if (cause instanceof FileNotFoundException) {
            string = "File not found";
        }
        return new ShaderParseException(string, cause);
    }

    @Environment(value=EnvType.CLIENT)
    public static class JsonStackTrace {
        @Nullable
        private String fileName;
        private final List<String> faultyElements = Lists.newArrayList();

        private JsonStackTrace() {
        }

        private void add(String element) {
            this.faultyElements.add(0, element);
        }

        public String joinStackTrace() {
            return StringUtils.join(this.faultyElements, (String)"->");
        }

        public String toString() {
            if (this.fileName != null) {
                if (this.faultyElements.isEmpty()) {
                    return this.fileName;
                }
                return this.fileName + " " + this.joinStackTrace();
            }
            if (this.faultyElements.isEmpty()) {
                return "(Unknown file)";
            }
            return "(Unknown file) " + this.joinStackTrace();
        }
    }
}

