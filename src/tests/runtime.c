#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>


void printint(int x) {
printf("answer is %d\n", x);
return;
}

int readint(){
/*
if i'm going to have multiple types of input (bool or int),
the way to solve the problem is have it get the input as a string.
if the string contains all numbers, return atoi of the string
else, if the string doesn't contain all numbers, return 1 if the string is "t"
and 0 if the string if "f"
*/
int n;
printf("enter ur input: \n");
fflush(stdout);
scanf("%d", &n);
return n;
}
