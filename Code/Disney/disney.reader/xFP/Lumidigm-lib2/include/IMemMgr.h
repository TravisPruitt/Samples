/******************************<%BEGIN LICENSE%>******************************/
// (c) Copyright 2011 Lumidigm, Inc. (Unpublished Copyright) ALL RIGHTS RESERVED.
//
//
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS 
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER 
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
//
/******************************<%END LICENSE%>******************************/
#pragma once

#ifndef __OVERRIDE_NEW__
#define __OVERRIDE_NEW__
#endif


#define MALLOC malloc
#define FREE   free
#define REALLOC realloc
#include "stdlib.h"

#ifdef _GCC
#include <stddef.h>
#endif

class MemoryBase
{
};

