#include <stdio.h>
#include <inttypes.h>

void printint(int x) {
setbuf(stdout, NULL);
fprintf(stdout,"%d\n",x);
setbuf(stdout, 0);
fflush(stdout);
}
int readint(){
int n;
scanf("%d", &n);
return n;
}