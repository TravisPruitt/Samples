#include "strings.h"
#include "character.h"

Boolean strings_matchCaseInsensitive (const char * string1, const char * string2)
{
    // case-insensitive string comparison

    Boolean match;

    while (TRUE)
    {
        if ((* string1 == 0) && (* string2 == 0))
        {
            match = TRUE;
            break;
        }

        if (character_toLower (* string1 ++) != character_toLower (* string2 ++))
        {
            match = FALSE;
            break;
        }
    }

    return match;
}
