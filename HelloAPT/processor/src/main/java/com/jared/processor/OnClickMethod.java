package com.jared.processor;

import com.jared.annotation.OnClick;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;

/**
 * Created by jared on 2017/12/12.
 */

public class OnClickMethod {
    private ExecutableElement methodElement;
    private Name mMethodName;
    public int[] ids;

    public OnClickMethod(Element element) throws IllegalArgumentException {
        if (element.getKind() != ElementKind.METHOD) {
            throw new IllegalArgumentException(
                    String.format("Only methods can be annotated with @%s",
                            OnClick.class.getSimpleName()));
        }
        this.methodElement = (ExecutableElement) element;
        this.ids = methodElement.getAnnotation(OnClick.class).value();

        if (null == ids) {
            throw new IllegalArgumentException(String.format("Must set valid ids for @%s",
                    OnClick.class.getSimpleName()));
        } else {
            for (int id: ids) {
                if (id < 0) {
                    throw new IllegalArgumentException(String.format("Must set valid id for @%s",
                            OnClick.class.getSimpleName()));
                }
            }
        }
        this.mMethodName = methodElement.getSimpleName();
//        List<? extends VariableElement> params = methodElement.getParameters();
//        if (params.size() > 0) {
//            throw new IllegalArgumentException(
//                    String.format("The method annotated with @%s must have no parameters",
//                            OnClick.class.getSimpleName()));
//        }
    }

    public Name getmMethodName() {
        return mMethodName;
    }
}
