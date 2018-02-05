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
//创建的localsocket的绝对路径
#define PATH "/data/data/com.jared.jnidaemon/app_socket/localsocket"
//#define PATH "/data/local/tmp/localsocket"

/* signal term handler */
static void sigterm_handler(int signo) {
    LOGD(LOG_TAG, "handle signal: %d ", signo);
}

int create_socket() {
    /* delete the socket file */
    unlink(PATH);

    /* create a socket */
    int server_sockfd = socket(AF_UNIX, SOCK_STREAM, 0);

    struct sockaddr_un server_addr;
    server_addr.sun_family = AF_UNIX;
    strcpy(server_addr.sun_path, PATH);

    /* bind with the local file */
    if(bind(server_sockfd, (struct sockaddr *) &server_addr, sizeof(server_addr)) != 0) {
        LOGE(LOG_TAG, "binder error!");
        return -1;
    }

    /* listen */
    listen(server_sockfd, 5);

    char ch[1024];
    int client_sockfd;
    struct sockaddr_un client_addr;
    socklen_t len = sizeof(client_addr);
    while (1) {
        LOGD(LOG_TAG, "server waiting:");

        /* accept a connection */
        client_sockfd = accept(server_sockfd, (struct sockaddr *) &client_addr, &len);

        /* exchange data */
        read(client_sockfd, &ch, 1024);
        LOGD(LOG_TAG, "get message from client: %s", ch);
        write(client_sockfd, &ch, strlen(ch));

        /* close the socket */
        close(client_sockfd);
    }
}

int main(int argc, char *argv[]) {

    LOGI(LOG_TAG, "Copyright (c) 2018, eastmoon<chenjianneng1117@gmail.com>");

    LOGI(LOG_TAG, "=========== daemon start =======");

    /* add signal */
    signal(SIGTERM, sigterm_handler);

    create_socket();

//    while (1) {
//        LOGI(LOG_TAG, "=========== daemon running ======");
//        sleep(3);
//    }
}
