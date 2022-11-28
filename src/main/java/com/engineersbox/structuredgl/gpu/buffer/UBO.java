package com.engineersbox.structuredgl.gpu.buffer;

import com.engineersbox.structuredgl.gpu.GPUResource;

import static org.lwjgl.opengl.GL15.glGenBuffers;

public final class UBO extends DataBuffer {

    public UBO() {
        super(DataBufferType.UNB);
    }

}
