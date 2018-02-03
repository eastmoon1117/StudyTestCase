#include <stdlib.h>
#include <stdio.h>
#include <signal.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <netinet/in.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include <arpa/inet.h>
#include "log.h"

#define LOG_TAG         "Daemon"
#define MAXLINE 1024
#define PATH "com.jared.jnidaemon.localsocket"

/* signal term handler */
static void sigterm_handler(int signo) {
    LOGD(LOG_TAG, "handle signal: %d ", signo);
}

void *connectThread(void *arg) {
    int ret;
    int socketID = *(int *) arg;
    if (socketID < 0) {
        LOGE(LOG_TAG, "socketID is %d", socketID);
        return NULL;
    }
    char buf2[512] = {0};
    ret = read(socketID, buf2, sizeof(buf2));
    if (ret < 0) {
        LOGE(LOG_TAG, "recived failed");
        return NULL;
    }
    LOGI(LOG_TAG, "c server recived: %s", buf2);
    char buffer[] = {"this message from c server "};
    ret = write(socketID, buffer, strlen(buffer));
    if (ret < 0) {
        LOGE(LOG_TAG, "write failed");
        return NULL;
    }
    close(socketID);
    return NULL;

}

//int create_socket() {
//    int ret;
//    int serverID = socket_local_server(PATH, ANDROID_SOCKET_NAMESPACE_ABSTRACT, SOCK_STREAM);
//    if (serverID < 0) {
//        LOGE(LOG_TAG, "socket_local_server failed :%d\n", serverID);
//        return serverID;
//    }
//    int socketID;
//    pthread_t tid;
//    while ((socketID = accept(serverID, NULL, NULL)) >= 0) {
//        ret = pthread_create(&tid, NULL, connectThread, (void *) &socketID);
//        if (ret != 0) {
//            LOGE(LOG_TAG, "error create thread:%s\n", strerror(ret));
//            exit(1);
//        }
//    }
//    return ret;
//}

int main(int argc, char *argv[]) {

    LOGI(LOG_TAG, "Copyright (c) 2018, eastmoon<chenjianneng1117@gmail.com>");

    LOGI(LOG_TAG, "=========== daemon start =======");

    /* add signal */
    signal(SIGTERM, sigterm_handler);

    //create_socket();

//    while (1) {
//        LOGI(LOG_TAG, "=========== daemon running ======");
//        sleep(3);
//    }
}
