package com.engineersbox.structuredgl.gpu.shader;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL20.*;

public class Uniforms {

    private static final String NEWLINE_CHARACTER = System.getProperty("line.separator");
    private static final Pattern GLSL_UNIFORM_PATTERN = Pattern.compile("uniform\\s+\\w+\\s(\\w+)(\\[.+\\])?");

    private final int programId;
    public final Map<String, Integer> uniformFields;

    public Uniforms(final int programId) {
        this.programId = programId;
        this.uniformFields = new HashMap<>();
    }

    public void createUniform(final String name) {
        final int location = glGetUniformLocation(this.programId, name);
        if (location < 0) {
            throw new RuntimeException(String.format(
                    "Cannot find uniform \"%s\" in shader %d",
                    name,
                    this.programId
            ));
        }
        this.uniformFields.put(name, location);
    }

    public void saturateFromSource() {
        final StringTokenizer tokenizer = new StringTokenizer(
                glGetShaderSource(this.programId),
                NEWLINE_CHARACTER
        );
        while (tokenizer.hasMoreTokens()) {
            final String line = tokenizer.nextToken().trim();
            final Matcher matcher = Uniforms.GLSL_UNIFORM_PATTERN.matcher(line);
            if (!matcher.find()) {
                continue;
            }
            createUniform(matcher.group(1));
        }
    }

    private int getUniformLocation(final String name) {
        final Integer location = this.uniformFields.get(name);
        if (location == null) {
            throw new RuntimeException(String.format(
                    "Cannot find uniform \"%s\" in shader %d",
                    name,
                    this.programId
            ));
        }
        return location;
    }

    public void setUniform(final String name,
                           final Matrix4f value) {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(
                    getUniformLocation(name),
                    false,
                    value.get(stack.mallocFloat(16))
            );
        }
    }

    public void setUniform(final String name,
                           final int value) {
        glUniform1i(
                getUniformLocation(name),
                value
        );
    }

    public void setUniform(final String name,
                           final boolean value) {
        glUniform1i(
                getUniformLocation(name),
                value ? 1 : 0
        );
    }

    public void setUniform(final String name,
                           final Vector4f value) {
        glUniform4f(
                getUniformLocation(name),
                value.x,
                value.y,
                value.z,
                value.w
        );
    }

    public void setUniform(final String name,
                           final Vector2f value) {
        glUniform2f(
                getUniformLocation(name),
                value.x,
                value.y
        );
    }

    public void setUniform(final String name,
                           final Vector3f value) {
        glUniform3f(
                getUniformLocation(name),
                value.x,
                value.y,
                value.z
        );
    }

    public void setUniform(final String name,
                           final float value) {
        glUniform1f(
                getUniformLocation(name),
                value
        );
    }

    public void setUniform(final String name,
                           final Matrix4f[] matrices) {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final int length = matrices != null ? matrices.length : 0;
            final FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                matrices[i].get(16 * i, fb);
            }
            glUniformMatrix4fv(
                    getUniformLocation(name),
                    false,
                    fb
            );
        }
    }

}
