package com.example.apt_processor;

import com.example.apt_annotation.onClick;

import java.lang.reflect.Executable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

public class ClickViewFIled {
    //方法元素
    private ExecutableElement executableElement;
    //控件id
    private int resId;
    //绑定方法名
    private String methodName;

    public ClickViewFIled(Element element){
        //只支持方法注解
        if (element.getKind()!=ElementKind.METHOD){
            throw new IllegalArgumentException(String.format("Only method can be annotated with @%s",
                    onClick.class.getSimpleName()));
        }
        //转变为方法元素
        executableElement = (ExecutableElement) element;
        //获得注解对象整体
        onClick click = executableElement.getAnnotation(onClick.class);
        //获取id
        resId = click.value();
        if (resId<0){
            throw new IllegalArgumentException(
                    String.format("value() in %s for field %s is not valid !", onClick.class.getSimpleName(),
                            executableElement.getSimpleName()));
        }
        methodName = executableElement.getSimpleName().toString();
    }

    public ExecutableElement getExecutableElement(){
        return executableElement;
    }

    public int getResId(){
        return resId;
    }

    public String getMethodName(){
        return methodName;
    }
}
