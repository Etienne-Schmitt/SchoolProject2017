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

void *sendOutput(void *socketServer, char *buffer);

int main(int argc , char *argv[])
{
    pthread_t Reception, Transmission;
    pthread_mutex_t mutex;
    sem_t sem;


    int socketServer, socketClient, lengthClient, *socketThread;
    struct sockaddr_rc serverAddr, clientAddr;
    char buffer[64], addresseDevice[8];

    sem_init(&sem, 0, 0);
    pthread_mutex_init(&mutex, NULL);

    printf("Démarrage Server Bluetooth\n");

    pthread_create(&Reception, NULL, readInput, NULL);

    socketServer = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

    serverAddr.rc_family = AF_BLUETOOTH;
    serverAddr.rc_bdaddr = *BDADDR_ANY;
    serverAddr.rc_channel = 1;

    bind(socketServer, (struct sockaddr *)&serverAddr, sizeof(serverAddr));

    listen(socketServer, 1);

    lengthClient = sizeof(clientAddr);
    printf("Attente de client\n");
    while (socketClient = accept(socketServer, (struct sockaddr *)&clientAddr, &lengthClient) )
    {
        ba2str(&rem_addr.rc_bdaddr, addr_device);
        printf("Connexion recu de : %s\n", addr_device);

        socketThread = malloc(1);
        *socketThread = socketClient;
        pthread_create(&Transmission, NULL, sendOutput, NULL);
    }
    printf("Connexion refusée, arret en cours...\n");
    close(socketClient);
    close(socketServer);
    return -1;
}

void *sendOutput(void *socketServer, char *buffer)
{
    int sClient = *(int*)socketServer;

    printf("Thread envoie crée !\n");
    while (sClient > 0)
    {
        //pthread_mutex_lock(&mutex);
        //sem_wait(&sem);

        if (send(sClient, buffer, sizeof(buffer), 0) < 0)
            printf("Le client c'est déconnecter !\n");

        //pthread_mutex_unlock(&mutex);
    }
    pthread_exit(NULL);
}
