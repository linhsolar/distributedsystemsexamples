#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>
 
/************************************************************
based on 
http://geco.mines.edu/workshop/class2/examples/mpi/c_ex04.c

by adding also Send/Recv

************************************************************/

int main(argc,argv)
int argc;
char *argv[];
{
    int i,myid, numprocs;
    int source,count;
    int buffer[4];
    char output[80];
    MPI_Status status;
    MPI_Request request;
    MPI_Init(&argc,&argv);
    MPI_Comm_size(MPI_COMM_WORLD,&numprocs);
    MPI_Comm_rank(MPI_COMM_WORLD,&myid);
    /* first using send/receive to announce a task
     */
   if(myid == 0)
   {
     printf("I am %d: We have %d processors\n", myid, numprocs);
     sprintf(output, "This is a message sending from %d", i);    
     for(i=1;i<numprocs;i++)
       MPI_Send(output, 80, MPI_CHAR, i, 0, MPI_COMM_WORLD);
   }
   else {
       MPI_Recv(output, 80, MPI_CHAR, i, 0, MPI_COMM_WORLD, &status);
       printf("I am %d and I receive: %s\n", myid, output);
   }
    source=0;
    count=4;
    if(myid == source){
      for(i=0;i<count;i++)
        buffer[i]=i;
    }
    MPI_Bcast(buffer,count,MPI_INT,source,MPI_COMM_WORLD);
    
    for(i=0;i<count;i++) {
      printf("I am %d and I receive: %d \n",myid, buffer[i]);  
    }
    printf("\n");
    MPI_Finalize();
}

