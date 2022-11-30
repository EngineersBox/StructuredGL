package com.engineersbox.structuredgl;

import com.engineersbox.structuredgl.gpu.ValidationState;

public interface Bindable {

    void bind();

    void unbind();

    default ValidationState validate() {
        return new ValidationState(true, null);
    }

    void destroy();

}
