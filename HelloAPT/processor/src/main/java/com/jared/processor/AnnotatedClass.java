package com.jared.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by jared on 2017/12/12.
 */

public class AnnotatedClass {

    public TypeElement mClassElement;
    public List<BindViewField> mFields;
    public List<OnClickMethod> mMethods;
    public Elements mElementUtils;

    public AnnotatedClass(TypeElement mClassElement, Elements mElementUtils) {
        this.mClassElement = mClassElement;
        this.mFields = new ArrayList<>();
        this.mMethods = new ArrayList<>();
        this.mElementUtils = mElementUtils;
    }

    public String getFullClassName() {
        return mClassElement.getQualifiedName().toString();
    }

    public void addField(BindViewField field) {
        mFields.add(field);
    }

    public void addMethod(OnClickMethod method) {
        mMethods.add(method);
    }

    /**
     * $S for Strings
     * $T for Types
     * $N for Names(我们自己生成的方法名或者变量名等等)
     */

    public JavaFile generateFinder() {
        MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder("inject") //inject代表方法名
                .addModifiers(Modifier.PUBLIC) //修饰的关键字，这里是public
                .addAnnotation(Override.class) //override注解
                .addParameter(TypeName.get(mClassElement.asType()), "host", Modifier.FINAL) //名为host的参数
                .addParameter(TypeName.OBJECT, "source")  //名为source的参数
                .addParameter(TypeUtils.PROVIDER, "provider"); //名为provider的参数

        for (BindViewField field : mFields) {
            injectMethodBuilder.addStatement("host.$N = ($T)(provider.findView(source, $L))",
                    field.getFieldName(), ClassName.get(field.getFieldType()), field.getmResId()); //添加代码
        }

        if (mMethods.size() > 0) {
            injectMethodBuilder.addStatement("$T listener", TypeUtils.ANDROID_ON_CLICK_LISTENER); //添加代码
        }

        for (OnClickMethod method : mMethods) {
            TypeSpec listener = TypeSpec.anonymousClassBuilder("")
                    .addSuperinterface(TypeUtils.ANDROID_ON_CLICK_LISTENER)
                    .addMethod(MethodSpec.methodBuilder("onClick") //onClick代表方法名
                            .addAnnotation(Override.class) //override注解
                            .addModifiers(Modifier.PUBLIC) //修饰的关键字，这里是public
                            .addParameter(TypeUtils.ANDROID_VIEW, "view") //名为view的参数
                            .returns(TypeName.VOID) //返回值为void
                            .addStatement("host.$N($N)", method.getmMethodName(), "view").build())//添加代码
                    .build();
            injectMethodBuilder.addStatement("listener = $L", listener);//添加代码
            for (int id: method.ids) {
                injectMethodBuilder.addStatement("provider.findView(source, $L).setOnClickListener(listener)", id);//添加代码
            }
        }

        TypeSpec finderClass = TypeSpec.classBuilder(mClassElement.getSimpleName() + "$$Finder")//xxx$$Finder是类名，其中xxx是当前引入注解的类名,比如:MainActivity$$Finder
                .addModifiers(Modifier.PUBLIC) //修饰的关键字，这里是public
                .addSuperinterface(ParameterizedTypeName.get(TypeUtils.FINDER, TypeName.get(mClassElement.asType())))
                .addMethod(injectMethodBuilder.build()) //在类中添加方法
                .build();

        String packageName = mElementUtils.getPackageOf(mClassElement).getQualifiedName().toString();

        return JavaFile.builder(packageName, finderClass).build();
    }
}