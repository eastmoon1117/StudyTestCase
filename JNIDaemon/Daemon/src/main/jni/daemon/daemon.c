#include <stdlib.h>
#include <stdio.h>
#include <signal.h>

#include "log.h"

#define LOG_TAG         "Daemon"

/* signal term handler */
static void sigterm_handler(int signo) {
    LOGD(LOG_TAG, "handle signal: %d ", signo);
}

int main(int argc, char *argv[]) {

    LOGI(LOG_TAG, "Copyright (c) 2018, eastmoon<chenjianneng1117@gmail.com>");

    LOGI(LOG_TAG, "=========== daemon start =======");

    /* add signal */
    signal(SIGTERM, sigterm_handler);

    while (1) {
        LOGI(LOG_TAG, "=========== daemon running ======");
        sleep(3);
    }
}
