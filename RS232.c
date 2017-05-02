#include "RS232.h"
#include <time.h>
#include <stdlib.h>

int *readInput()
{
    while (1) //Thread 1
    {
        memset(buffer, 0, sizeof(buffer));
        // Code de Steve ici :




        // Générateur de string aléatoire 
        // (temporaire pour test sans le code de steve)

        srand(time(NULL));
        for (int i = 0; i < 8; ++i)
        {
            buffer[i] = '0' + rand() % 72;
        }
        // Fin générateur de string aléatoire

        printf("readInput() : buffer = %s\n", buffer);

        sem_post(&sem);
        sleep(10);
    }
    pthread_exit(NULL);
    return 0;
}
