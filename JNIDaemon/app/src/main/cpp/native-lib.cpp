#include <jni.h>
#include <string>
#include "hello.h"
#include "log.h"

extern "C"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_jared_jnidaemon_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    Hello *hello1 = new Hello();
    hello = hello1->getHello();

    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_jared_jnidaemon_MainActivity_sum(JNIEnv *env, jobject instance, jint a, jint b) {

    Hello *sum = new Hello();
    return sum->sum(a, b);
}
