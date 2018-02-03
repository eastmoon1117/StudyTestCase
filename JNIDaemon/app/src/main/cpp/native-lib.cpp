#include <jni.h>
#include <string>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
//#include <cutils/sockets.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include "hello.h"
#include "log.h"

#define PATH "com.jared.jnidaemon.localsocket"

JavaVM *g_jvm;
pthread_t pt;
static jobject sCallbacksObj = NULL;
static JNIEnv *sCallbackEnv = NULL;

static jmethodID onCallbackID = NULL;
jboolean EXIT_THREAD = 0;

static inline void CheckExceptions(JNIEnv *env, const char *methodName) {
    if (!env->ExceptionCheck()) {
        return;
    }

    LOG_E("An exception was thrown by '%s'.", methodName);
    env->ExceptionClear();
}

static bool IsValidCallbackThreadEnvOnly() {
    JNIEnv *env;
    if (JNI_OK != g_jvm->GetEnv(reinterpret_cast<void **> (&env), JNI_VERSION_1_4)) {
        LOG_W("JNI_OnLoad could not get JNI env");
        return JNI_ERR;
    }

    if (sCallbackEnv == NULL || sCallbackEnv != env) {
        LOG_E("CallbackThread check fail: env=%p, expected=%p", env, sCallbackEnv);
        return false;
    }

    return true;
}

static bool IsValidCallbackThread() {
    if (sCallbacksObj == NULL) {
        LOG_E("Attempt to use blocked, because it hasn't been initialized.");
        return false;
    }

    return IsValidCallbackThreadEnvOnly();
}

extern "C"
JNIEXPORT jstring JNICALL stringFromJNI(JNIEnv *env) {
    std::string hello = "Hello from C++";

    Hello *hello1 = new Hello();
    hello = hello1->getHello();

    return env->NewStringUTF(hello.c_str());
}

static void callback(int32_t type) {
    if (!IsValidCallbackThread()) {
        return;
    }

    sCallbackEnv->CallVoidMethod(
            sCallbacksObj,
            onCallbackID,
            type
    );

    CheckExceptions(sCallbackEnv, __FUNCTION__);
}

jint sumFromJNI(jint a, jint b) {

    Hello *sum = new Hello();
    return sum->sum(a, b);
}

void init(JNIEnv *env, jobject instance) {
    if (sCallbacksObj == NULL) {
        sCallbacksObj = env->NewGlobalRef(instance);
    }
}

void classInit(JNIEnv *env) {

    jclass clazz = env->GetObjectClass(sCallbacksObj);

    // get references to the Java provider methods
    onCallbackID = env->GetMethodID(
            clazz,
            "onCallback",
            "(I)V"
    );
}

void cleanup(JNIEnv *env) {
    EXIT_THREAD = 1;
    if (sCallbacksObj != NULL) {
        env->DeleteGlobalRef(sCallbacksObj);
        sCallbacksObj = NULL;
    }
}

//int socket() {
//    int socketID;
//
//    int ret;
//    int i = 0;
//    int len = 0;
//    char buffer[20];
//
//    strcpy(buffer, "HELLO Socket");
//
//    socketID = socket_local_client(PATH, ANDROID_SOCKET_NAMESPACE_ABSTRACT, SOCK_STREAM);
//    if (socketID < 0) {
//        return socketID;
//    }
//    ret = write(socketID, buffer, strlen(buffer));
//    if(ret < 0){
//        LOG_E("send failed");
//        return ret;
//    }
//
////    char buf2[512] = {0};
////    ret = read(socketID,buf2,sizeof(buf2));
////    if(ret < 0){
////        LOG_E("recived failed");
////        return ret;
////    }else{
////        LOG_E("c client recived from server: %s",buf2);
////    }
//
//    ret = close(socketID);
//    if (ret < 0) {
//        return ret;
//    }
//
//    return 0;
//}

void *runMethod(void *args) {

    JavaVMAttachArgs jvmArgs = {
            JNI_VERSION_1_6,
            /* group */ NULL
    };

    jint attachResult = g_jvm->AttachCurrentThread(&sCallbackEnv, &jvmArgs);
    if (attachResult != 0) {
        LOG_E("Callback thread attachment error: %d", attachResult);
        return NULL;
    }

    while (true) {
        if (EXIT_THREAD != 0) break;
        sleep(3);
        callback(6);
        //socket();
    }

    //Detach主线程
    if (g_jvm->DetachCurrentThread() != JNI_OK) {
        LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
        return NULL;
    }
    //退出进程
    pthread_exit(0);
}

void startThread() {
    //创建子线程
    pthread_create(&pt, NULL, runMethod, NULL);
}

//参数映射表
static JNINativeMethod methods[] = {
        {"nativeInit",      "()V",                  reinterpret_cast<void *>(init)},
        {"nativeClassInit", "()V",                  reinterpret_cast<void *>(classInit)},
        {"nativeCleanup",   "()V",                  reinterpret_cast<void *>(cleanup)},

        {"stringFromJNI",   "()Ljava/lang/String;", reinterpret_cast<void *>(stringFromJNI)},
        {"sumFromJNI",      "(II)I",                reinterpret_cast<void *>(sumFromJNI)},
        {"startThread",     "()V",                  reinterpret_cast<void *>(startThread)}
};

//为某一个类注册本地方法，调运JNI注册方法
extern "C"
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (JNI_OK != vm->GetEnv(reinterpret_cast<void **> (&env), JNI_VERSION_1_4)) {
        LOG_W("JNI_OnLoad could not get JNI env");
        return JNI_ERR;
    }

    g_jvm = vm; //用于后面获取JNIEnv
    jclass clazz = env->FindClass("com/jared/jnidaemon/NdkJniUtils");  //获取Java

    //注册Native方法
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof((methods)[0])) < 0) {
        LOG_W("RegisterNatives error");
        return JNI_ERR;
    }

    return JNI_VERSION_1_4;
}