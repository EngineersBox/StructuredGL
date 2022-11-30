package com.engineersbox.structuredgl.gpu.shader;

import com.engineersbox.structuredgl.gpu.GPUResource;
import com.engineersbox.structuredgl.gpu.ValidationState;
import com.engineersbox.structuredgl.utils.EnumSetUtils;
import com.engineersbox.structuredgl.utils.FileUtils;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram extends GPUResource {

    private final String name;
    private final Uniforms uniforms;

    public ShaderProgram(final String name,
                         final ShaderModuleData... shaderModuleData) {
        this(name, List.of(shaderModuleData));
    }

    public ShaderProgram(final String name,
                         final List<ShaderModuleData> shaderModuleData) {
        this.name = name;
        this.bound = false;
        validateUniqueShaderTypes(shaderModuleData);
        super.id = glCreateProgram();
        if (super.id == 0) {
            throw new RuntimeException("Unable to create a new shader program");
        }
        final List<Integer> moduleIds = shaderModuleData.stream()
                .map((final ShaderModuleData data) -> createShader(
                        data.dataType() == DataType.FILE_PATH
                            ? FileUtils.readFile(data.data())
                            : data.data() ,
                        data.shaderType()
                )).toList();
        link(moduleIds);
        this.uniforms = new Uniforms(super.id);
    }

    private void validateUniqueShaderTypes(final List<ShaderModuleData> shaderModuleData) {
        final Map<ShaderType, Long> counts = shaderModuleData.stream()
                .map(ShaderModuleData::shaderType)
                .collect(EnumSetUtils.counting(ShaderType.class));
        final Optional<Map.Entry<ShaderType, Long>> possibleMultipleTypeBinding = counts.entrySet()
                .stream()
                .filter((final Map.Entry<ShaderType, Long> entry) -> entry.getValue() > 1)
                .findFirst();
        if (possibleMultipleTypeBinding.isEmpty()) {
            return;
        }
        final Map.Entry<ShaderType, Long> multipleTypeBinding = possibleMultipleTypeBinding.get();
        throw new IllegalStateException(String.format(
                "Program was bound with %d instances of %s, only 1 is supported",
                multipleTypeBinding.getValue(),
                multipleTypeBinding.getKey().name()
        ));
    }

    protected int createShader(final String code,
                               final ShaderType type) {
        final int shaderId = glCreateShader(type.getType());
        if (shaderId == 0) {
            throw new RuntimeException(String.format(
                    "[SHADER PROGRAM] Error while creating shader of shaderType %d",
                    type
            ));
        }
        glShaderSource(shaderId, code);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(String.format(
                    "[SHADER PROGRAM] Error while compiling shader: %s",
                    glGetShaderInfoLog(shaderId, 1024)
            ));
        }
        glAttachShader(super.id, shaderId);
        return shaderId;
    }

    private void link(final List<Integer> moduleIds) {
        glLinkProgram(super.id);
        if (glGetProgrami(super.id, GL_LINK_STATUS) == 0) {
            throw new RuntimeException(String.format(
                    "[SHADER PROGRAM] Error while linking shader: %s",
                    glGetProgramInfoLog(super.id, 1024)
            ));
        }
        moduleIds.forEach((final Integer id) -> glDetachShader(super.id, id));
        moduleIds.forEach(GL30::glDeleteShader);
    }

    @Override
    public void bind() {
        super.bind();
        glUseProgram(super.id);
    }

    @Override
    public void unbind() {
        super.unbind();
        glUseProgram(0);
    }

    @Override
    public ValidationState validate() {
        glValidateProgram(super.id);
        if (glGetProgrami(super.id, GL_VALIDATE_STATUS) == 0) {
            return new ValidationState(
                    false,
                    glGetProgramInfoLog(super.id, 1024)
            );
        }
        return new ValidationState(true, null);
    }

    public String getName() {
        return this.name;
    }

    public Uniforms getUniforms() {
        return this.uniforms;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (super.id != 0) {
            glDeleteProgram(super.id);
        }
    }

}
