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

#define CHANNEL 10

struct sockaddr_rc rem_addr = {0};
pthread_mutex_t mutex;
sem_t sem;
char buffer[64] = {0};
int sock, client;

void *sendOutput();

void main()
{

    pthread_t Reception, Transmission;
    struct sockaddr_rc loc_addr = {0};
    char receive_addr[8] = {0};
    socklen_t length_rem_addr = sizeof(rem_addr);
    socklen_t length_loc_addr = sizeof(loc_addr);

    printf("Starting Bluetooth Server\n");
    sem_init(&sem, 0, 0);
    pthread_mutex_init(&mutex, NULL);

    pthread_create(&Reception, NULL, readInput, NULL);

    loc_addr.rc_family = AF_BLUETOOTH;
    loc_addr.rc_bdaddr = *BDADDR_ANY;
    loc_addr.rc_channel = (uint8_t)CHANNEL;

    sock = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

    bind(sock, (struct sockaddr *)&loc_addr, length_loc_addr);

    while (1)
    {
        listen(sock, CHANNEL);

        client = accept(sock, (struct sockaddr *)&rem_addr, &length_rem_addr);

        // Conversion de l'adresse BT en string (char array) dans receive_addr
        ba2str(&rem_addr.rc_bdaddr, receive_addr);
        printf("Connexion recu de : %s\n", receive_addr);

        pthread_create(&Transmission, NULL, sendOutput, NULL);
    }

    close(client);
    close(sock);
}

void *sendOutput()
{
    while (client)
    {
        pthread_mutex_lock(&mutex);
        sem_wait(&sem);

        if (send(client, buffer, (size_t)strlen(buffer) + 1, 0) < 0)
            printf("Le client n'est pas connecter !\n");

        pthread_mutex_unlock(&mutex);
    }
    pthread_exit(NULL);
}
