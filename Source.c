#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>
#include "RS232.h"

pthread_t Reception, Transmission;
pthread_mutex_t mutex;
sem_t sem;

int socketServer, socketClient;
socklen_t lengthClient;
struct sockaddr_rc serverAddr, clientAddr;
char buffer[64], addrDevice[8];

void *sendOutput();

int main(int argc , char *argv[])
{
	sem_init(&sem, 0, 0);
	pthread_mutex_init(&mutex, NULL);

	printf(GRN "Démarrage Server Bluetooth\n" RESET);

	pthread_create(&Reception, NULL, readInput, NULL);

	socketServer = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

	serverAddr.rc_family = AF_BLUETOOTH;
	serverAddr.rc_bdaddr = *BDADDR_ANY;
	serverAddr.rc_channel = 1;

	bind(socketServer, (struct sockaddr *)&serverAddr, sizeof(serverAddr));

	listen(socketServer, 1);

	lengthClient = sizeof(clientAddr);
	printf(YEL "Attente de client...\n" RESET);


	do {

		socketClient = accept(socketServer, (struct sockaddr *)&clientAddr, &lengthClient);

		pthread_create(&Transmission, NULL, sendOutput, NULL);

	} while (socketClient);
	printf(RED "Connexion refusée, arret en cours...\n" RESET);
	close(socketClient);
	close(socketServer);
	return -1;
}

void *sendOutput()
{
	ba2str(&clientAddr.rc_bdaddr, addrDevice);
	printf(GRN "Connexion recu de : %s\n" RESET, addrDevice);

	printf(GRN "Thread envoie crée !\n" RESET);
	while (1)
	{
		pthread_mutex_lock(&mutex);
		sem_wait (&sem);

		if (send(socketClient, buffer, sizeof(buffer), 0) < 0 )
		{
			printf(RED "Le client s'est déconnecter ! Thread envoie Detruit\n" RESET);
			pthread_mutex_unlock(&mutex);
			pthread_exit(NULL);
		}
		sleep(1);
		pthread_mutex_unlock(&mutex);
	}
	pthread_exit(NULL);
}
