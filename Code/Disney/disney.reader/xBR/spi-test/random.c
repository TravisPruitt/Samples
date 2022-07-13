
#include "random.h"

#include <stdlib.h>



Signed_32 random_inRangeOf (Signed_32 min, Signed_32 max)
{
    #define RANDOM_MAX  0x7fffffff
    double randomReal;

    randomReal = (double) random () / RANDOM_MAX * (max - min) + min ;

    return (randomReal > 0) ?
                (Signed_32) (randomReal + 0.5) :
                (Signed_32) (randomReal - 0.5) ;
}

