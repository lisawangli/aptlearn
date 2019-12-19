package com.example.apt_processor;

import com.example.apt_annotation.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BindViewField {
    private VariableElement mVariableElement;
    private int resId;

    public BindViewField(Element element){

            if (element.getKind() != ElementKind.FIELD) {
                throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                        BindView.class.getSimpleName()));
            }
            mVariableElement = (VariableElement) element;
            BindView bindView = mVariableElement.getAnnotation(BindView.class);
            resId = bindView.value();
        if (resId < 0) {
            throw new IllegalArgumentException(
                    String.format("value() in %s for field %s is not valid !", BindView.class.getSimpleName(),
                            mVariableElement.getSimpleName()));
        }

    }

    /**
     * 获取变量名称
     * @return
     */
    public Name getFieldName(){
        return mVariableElement.getSimpleName();
    }

    public int resId(){
        return resId;
    }

    /**
     * 获取变量类型
     * @return
     */
    TypeMirror getFieldType(){
        return mVariableElement.asType();
    }
}
