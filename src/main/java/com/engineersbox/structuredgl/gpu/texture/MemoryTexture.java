package com.engineersbox.structuredgl.gpu.texture;

import com.engineersbox.structuredgl.gpu.GPUResource;
import org.joml.Vector3i;

import javax.annotation.Nullable;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glTexParameterf;

public abstract class MemoryTexture extends GPUResource {

    protected final TextureType type;

    private final Vector3i dimensions;

    protected MemoryTexture(final TextureType type) {
        super.id = glGenTextures();
        this.type = type;
        this.dimensions = new Vector3i();
    }

    public abstract void createTexImage(final int level,
                                        final int internalformat,
                                        final int[] dimensions,
                                        final int border,
                                        final int format,
                                        final int type,
                                        @Nullable final ByteBuffer pixels);

    protected void setDimensions(final int[] dimensions) {
        this.dimensions.set(
                dimensions.length >= 1 ? dimensions[0] : this.dimensions.x(),
                dimensions.length >= 2 ? dimensions[1] : this.dimensions.y(),
                dimensions.length >= 3 ? dimensions[2] : this.dimensions.z()
        );
    }

    public void setTexParameterf(final int paramName,
                                 final float value) {
        glTexParameterf(
                this.type.glType(),
                paramName,
                value
        );
    }

    public void setTexParameteri(final int paramName,
                                 final int value) {
        glTexParameteri(
                this.type.glType(),
                paramName,
                value
        );
    }

    public TextureType getType() {
        return this.type;
    }

    public Vector3i getDimensions() {
        return this.dimensions;
    }

    @Override
    public void bind() {
        super.bind();
        glBindTexture(this.type.glType(), super.id);
    }

    @Override
    public void unbind() {
        glBindTexture(this.type.glType(), 0);
    }

    @Override
    public void destroy() {
        super.destroy();
        glDeleteTextures(super.id);
    }

}
