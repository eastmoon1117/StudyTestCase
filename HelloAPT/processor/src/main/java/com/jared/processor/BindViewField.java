package com.jared.processor;

import com.jared.annotation.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Created by jared on 2017/12/12.
 */

public class BindViewField {
    private VariableElement mVariableElement;
    private int mResId;

    public BindViewField(Element element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.FIELD) {
            throw new IllegalArgumentException(
                    String.format("Only fields can be annotated with @%s",
                            BindView.class.getSimpleName()));
        }
        mVariableElement = (VariableElement) element;
        BindView bindView = mVariableElement.getAnnotation(BindView.class);
        mResId = bindView.value();
        if (mResId < 0) {
            throw new IllegalArgumentException(
                    String.format("value() in %s for field %s is not valid !",
                            BindView.class.getSimpleName(), mVariableElement.getSimpleName()));
        }
    }

    public Name getFieldName() {
        return mVariableElement.getSimpleName();
    }

    public int getmResId() {
        return mResId;
    }

    public TypeMirror getFieldType() {
        return mVariableElement.asType();
    }
}
