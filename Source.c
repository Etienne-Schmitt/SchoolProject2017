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

struct sockaddr_rc rem_addr = {0};
pthread_mutex_t mutex;
sem_t sem;
char buffer[64] = {0};
int sock;

void *sendOutput();

void main()
{

    pthread_t Reception, Transmission;
    struct sockaddr_rc loc_addr = {0};
    char receive_addr[8] = {0};
    int client;
    socklen_t opt = sizeof(rem_addr);

    printf("Starting Bluetooth Server\n");
    sem_init(&sem, 0, 0);
    pthread_mutex_init(&mutex, NULL);

    pthread_create(&Reception, NULL, readInput, NULL);

    loc_addr.rc_family = AF_BLUETOOTH;
    loc_addr.rc_bdaddr = *BDADDR_ANY;
    loc_addr.rc_channel = (uint8_t)1;

    sock = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);

    bind(sock, (struct sockaddr *)&loc_addr, sizeof(loc_addr));

    listen(sock, 10);
    printf("Je suis dansla boucle\n");
    while (1)
    {
        client = accept(sock, (struct sockaddr *)&rem_addr, &opt);

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
    pthread_mutex_lock(&mutex);

    printf("Je suis dans le Thread2\n");
    connect(sock, (struct sockaddr *)&rem_addr, sizeof(rem_addr));

    if (send(sock, buffer, (size_t)strlen(buffer) + 1, 0) < 0)
    {
        printf("Le client n'est pas connecter !\n");
    }

    //printf("sendOutput() : buffer = %s\n", buffer);

    pthread_mutex_unlock(&mutex);

    pthread_exit(NULL);
}
