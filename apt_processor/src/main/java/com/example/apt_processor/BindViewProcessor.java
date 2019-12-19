package com.example.apt_processor;

import com.example.apt_annotation.BindView;
import com.example.apt_annotation.onClick;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Elements mElementUtils;
    private Filer mFiler; //文件相关的辅助类

    //    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();
    private Map<String, AnnotatedClass> mAnnotatedClassMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mElementUtils = processingEnv.getElementUtils();
        mAnnotatedClassMap = new TreeMap<>();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        supportTypes.add(onClick.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.print("=====process========");
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        mAnnotatedClassMap.clear();

        try {
            //增加方法，处理点击注解
            processBindView(roundEnvironment);
            processClickBindMethod(roundEnvironment);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            error(e.getMessage());
        }

        for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
            try {
                //输出中间文件
                annotatedClass.generateFile().writeTo(mFiler);
            } catch (IOException e) {
                error("Generate file failed, reason: %s", e.getMessage());
            }
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE, "process finish ...");
        return true;
    }

    /**处理点击事件绑定*/
    private void processClickBindMethod(RoundEnvironment roundEnv) {
        for(Element element : roundEnv.getElementsAnnotatedWith(onClick.class))
        {
            //获取对应的生成类
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            //生成我们的目标注解模型，方便后期文件输出
            ClickViewFIled clickFile = new ClickViewFIled(element);
            annotatedClass.addClickField(clickFile);
        }

    }

    private void processBindView(RoundEnvironment roundEnv) throws IllegalArgumentException {
        //其实这里annotations中是包含我们的注解类型的，但是你可以看看
        //下面的支持类型函数，BindView,所以这里也没有遍历的必要了
        for (Element element : roundEnv.getElementsAnnotatedWith(BindView.class)) {
            AnnotatedClass annotatedClass = getAnnotatedClass(element);
            BindViewField bindViewField = new BindViewField(element);
            annotatedClass.addField(bindViewField);
        }
    }
    /**获取注解所在文件对应的生成类*/
    private AnnotatedClass getAnnotatedClass(Element element) {
        //typeElement表示类或者接口元素
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String fullName = typeElement.getQualifiedName().toString();
        //这里其实就是变相获得了注解的类名（完全限定名称，这里是这么说的）
        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(fullName);
        // Map<String, AnnotatedClass>
        if (annotatedClass == null) {
            annotatedClass = new AnnotatedClass(typeElement, mElementUtils);
            mAnnotatedClassMap.put(fullName, annotatedClass);
        }
        return annotatedClass;
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

}
