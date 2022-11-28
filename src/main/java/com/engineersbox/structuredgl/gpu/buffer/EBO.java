package com.engineersbox.structuredgl.gpu.buffer;

import com.engineersbox.structuredgl.gpu.GPUResource;

import static org.lwjgl.opengl.GL15.glGenBuffers;

public final class EBO extends DataBuffer {

    public EBO() {
        super(DataBufferType.EAB);
    }

}
