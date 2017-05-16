#include "RS232.h"
#include <time.h>
#include <stdlib.h>

int *readInput()
{
    while (1) //Thread 1
    {
        // Générateur de string aléatoire

        srand(time(NULL));
        for (int i = 0; i < 8; ++i)
        {
            buffer[i] = '0' + rand() % 72;
        }
        strcat(buffer, "\0");
        // Fin générateur de string aléatoire

        printf("readInput() : buffer = %s\n", buffer);
        printf("sizeof(buffer) = %d\n", sizeof(buffer));

        sem_post(&sem);
        sleep(10);
    }
    pthread_exit(NULL);
    return 0;
}
