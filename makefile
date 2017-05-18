CC = gcc
CFLAGS = -Wall -std=c99
INCLUDES =
LFLAGS =
LIBS = -lbluetooth -lpthread
SRCS = Source.c RS232.c
OBJS = $(SRCS:.c=.o)
MAIN = rfcomm-server

all:    $(MAIN)


$(MAIN): $(OBJS) 
	$(CC) $(CFLAGS) $(INCLUDES) -o $(MAIN) $(OBJS) $(LFLAGS) $(LIBS)

.c.o:
	$(CC) $(CFLAGS) $(INCLUDES) -c $<  -o $@

clean:
	$(RM) *.o *~ $(MAIN)

depend: $(SRCS)
	makedepend $(INCLUDES) $^
