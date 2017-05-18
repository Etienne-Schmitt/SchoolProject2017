#include "RS232.h"
#include <time.h>
#include <stdlib.h>

void *readInput(char *buffer)
{
	char data = *(char*)buffer;
    printf("Thread reception crée !\n");
    while (1) //Thread 1
    {
        memset(data, 0, sizeof(data));
        // Générateur de string aléatoire
        srand(time(NULL));
        for (int i = 0; i < 8; ++i)
        {
            data[i] = '0' + rand() % 72;
        }
        strcat(data, "\0");
        // Fin générateur de string aléatoire

        //printf("readInput() : buffer = %s\n", buffer);
        printf("Entrée =%s\n", data);

        //sem_post(&sem);
        sleep(10);
    }
    pthread_exit(NULL);
}
