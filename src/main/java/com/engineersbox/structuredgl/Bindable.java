package com.engineersbox.structuredgl;

public interface Bindable {

    void bind();

    void unbind();

    default void validate() {}

    void destroy();

}
