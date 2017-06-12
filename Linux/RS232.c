#include "RS232.h"

void *readInput()
{
	printf(GRN "Thread reception crée !\n" RESET);	
	
	while (1) //Thread 1
	{
		pthread_mutex_lock(&mutex);

		memset(buffer, 0, sizeof(buffer));
		printf("Entrée la trame à envoyer : \n");
		fgets(buffer, sizeof(buffer), stdin);		

		printf(YEL "Entrée=%s" RESET, buffer);

		pthread_mutex_unlock(&mutex);
		sem_post(&semEnvoie);

		sleep(1);
	}
	pthread_exit(NULL);
}
