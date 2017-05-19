#include "RS232.h"
#include <time.h>
#include <stdlib.h>

void *readInput()
{
	printf(GRN "Thread reception crée !\n" RESET);	while (1) //Thread 1
	{
		pthread_mutex_lock(&mutex);

		memset(buffer, 0, sizeof(buffer));
		// Générateur de string aléatoire
		srand(time(NULL));
		for (int i = 0; i < 8; ++i)
		{
			buffer[i] = '0' + rand() % 72;
		}
		strcat(buffer, "\0");
		// Fin générateur de string aléatoire

		printf(YEL "Entrée= %s\n" RESET, buffer);

		sem_post(&sem);
		pthread_mutex_unlock(&mutex);

		sleep(5);
	}
	pthread_exit(NULL);
}
