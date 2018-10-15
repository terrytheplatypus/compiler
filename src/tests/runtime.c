#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>


void printint(int x) {
printf("answer is %d\n", x);
return;
}

int readint(){
int n;
printf("enter ur input: \n");
fflush(stdout);
scanf("%d", &n);
return n;
}
