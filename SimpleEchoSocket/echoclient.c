/* 
 * echoclient.c - A simple connection-based client
 * usage: echoclient <host> <port>
 * Original from http://www.cs.cmu.edu/afs/cs/academic/class/15213-f00/www/class24code/
 * Modified by Linh Truong
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h> 

#define BUFSIZE 1024

/* 
 * error - wrapper for perror
 */
void error(char *msg) {
    perror(msg);
    exit(0);
}

int main(int argc, char **argv) {
    int sockfd, portno, n;
    struct sockaddr_in serveraddr;
    struct hostent *server;
    char *hostname;
    char buf[BUFSIZE];
    char temp[BUFSIZE];

    /* check command line arguments */
    if (argc != 3) {
       fprintf(stderr,"usage: %s <hostname> <port>\n", argv[0]);
       exit(0);
    }
    hostname = argv[1];
    portno = atoi(argv[2]);

    /* socket: create the socket */
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");

    /* gethostbyname: get the server's DNS entry */
    server = gethostbyname(hostname);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host as %s\n", hostname);
        exit(0);
    }

    /* build the server's Internet address */
    bzero((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
	  (char *)&serveraddr.sin_addr.s_addr, server->h_length);
    serveraddr.sin_port = htons(portno);
    printf("CLIENT - Pls. enter to start connect():");
    fgets(temp,BUFSIZE,stdin);
    if (connect(sockfd, &serveraddr, sizeof(serveraddr)) < 0) 
      error("ERROR connecting");

    /* get message line from the user */
    printf("CLIENT - Please enter msg: ");
    bzero(buf, BUFSIZE);
    fgets(buf,BUFSIZE,stdin);  
    printf("CLIENT - Pls enter to start write():");
    fgets(temp,BUFSIZE,stdin);
    n = write(sockfd, buf, strlen(buf));
    if (n < 0) 
      error("ERROR writing to socket");
    printf("CLIENT - Pls enter to start read():");
    fgets(temp,BUFSIZE,stdin);
    /* read: print the server's reply */
    bzero(buf, BUFSIZE);
    
    
    n = read(sockfd, buf, BUFSIZE);
    if (n < 0) 
      error("ERROR reading from socket");
    printf("Echo from server: %s", buf);
    close(sockfd);
    return 0;
}
