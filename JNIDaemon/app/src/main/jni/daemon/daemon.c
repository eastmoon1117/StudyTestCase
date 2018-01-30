#include <unistd.h>
#include "log.h"

#define LOG_TAG         "Daemon"

int main(int argc, char *argv[])
{
	LOGI(LOG_TAG, "Copyright (c) 2018, eastmoon<chenjianneng1117@gmail.com>");

	LOGI(LOG_TAG, "=========== daemon start =======");

    while(1) {
        LOGI(LOG_TAG, "=========== daemon running ======");
        sleep(3);
    }
}
