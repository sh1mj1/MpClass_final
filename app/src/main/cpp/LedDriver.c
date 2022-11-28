//
// Created by 심지훈 on 2022/11/24.
//
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <jni.h>

int ledFd = 0;

JNIEXPORT jint JNICALL
Java_com_example_minigame_MemoryActivity_openLedDriver(JNIEnv *env, jclass clazz, jstring path) {
    jboolean iscopy;
    const char *path_utf = (*env)->GetStringUTFChars(env, path, &iscopy);
    ledFd = open(path_utf, O_WRONLY);
    (*env)->ReleaseStringUTFChars(env, path, path_utf);

    if(ledFd<0) {return -1;}
    else {return 1;}


}

JNIEXPORT jint JNICALL
Java_com_example_minigame_MemoryActivity_closeLedDirver(JNIEnv *env, jclass clazz) {
    if (ledFd>0) {close(ledFd);}
}

JNIEXPORT void JNICALL
Java_com_example_minigame_MemoryActivity_writeLedDriver(JNIEnv *env, jclass clazz, jbyteArray data,
                                                        jint length) {
    jbyte *chars = (*env)->GetByteArrayElements(env, data, 0);

    if (ledFd > 0){
        write(ledFd, (unsigned char *) chars, length);
    }

    (*env)->ReleaseByteArrayElements(env, data, chars, 0);
}



