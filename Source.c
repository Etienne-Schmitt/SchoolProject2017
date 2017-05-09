#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <sys/socket.h>
#include <bluetooth/bluetooth.h>
#include <bluetooth/rfcomm.h>

#include "RS232.h"

pthread_t Reception, Transmission;
pthread_mutex_t mutex;
sem_t sem;

struct sockaddr_rc loc_addr = {0}, rem_addr = {0};
char buffer[32] = {0}, receive_addr[8] = {0};
int sock, client, status;
socklen_t opt = sizeof(rem_addr);

int *sendOutput();

void main()
{
    printf("Starting Bluetooth Server\n");
    sem_init(&sem, 0, 0);

    if (pthread_create(&Reception, NULL, readInput, NULL) < 0)
    {
        perror("Thread:Reception error");
        exit(1);
    }

    loc_addr.rc_family = AF_BLUETOOTH;
    loc_addr.rc_bdaddr = *BDADDR_ANY;
    loc_addr.rc_channel = (uint8_t)1;

    sock = socket(AF_BLUETOOTH, SOCK_STREAM, BTPROTO_RFCOMM);
    if (sock < 0)
    {
        perror("error socket");
        exit(1);
    }

    if (bind(sock, (struct sockaddr *)&loc_addr, sizeof(loc_addr)) < 0)
    {
        perror("bind error");
        exit(1);
    }

    if (listen(sock, 10) < 0)
    {
        perror("listen error");
        exit(1);
    }

    while (1)
    {
        client = accept(sock, (struct sockaddr *)&rem_addr, &opt);
        if (client < 0)
        {
            perror("accept error");
            exit(1);
        }
        
        fprintf(stderr, "Connexion recu de : %s\n", receive_addr);


        if (pthread_create(&Transmission, NULL, sendOutput, NULL) < 0)
        {
            perror("Thread:Transmission error");
            exit(1);
        }

        sleep(1);
    }

    close(client);
    close(sock);
}

int *sendOutput()
{
    while (1) //Thread 2
    {
        sem_wait(&sem);
	printf("sendOutput(Sans connexion) : buffer = %s\n", buffer);

	ba2str( &rem_addr.rc_bdaddr, receive_addr );

        if (send(client, buffer, (size_t) sizeof(buffer), 0) < 0)
        {
            perror("send error");
            exit(1);
        }

	printf("sendOutput(Avec connexion) : buffer = %s\n", buffer);
    }

    pthread_exit(NULL);
    return 0;
}
