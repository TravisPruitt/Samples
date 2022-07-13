/******************************<%BEGIN LICENSE%>******************************/
// (c) Copyright 2011 Lumidigm, Inc. (Unpublished Copyright) ALL RIGHTS RESERVED.
//
// For a list of applicable patents and patents pending, visit www.lumidigm.com/patents/
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

#ifndef DOXYGEN_SHOULD_SKIP_THIS	// Skip this for auto-docs.

#pragma once

#ifdef __cplusplus
	#define _C_ "C"
#else
	#define _C_
#endif

#ifdef	VCOMDIAG_EXPORTS
  #ifndef VCOM_CORE_EXPORT
	#define VCOM_CORE_EXPORT extern _C_ __declspec(dllexport)
	#define _STDCALL
  #endif
#else
	#define VCOM_CORE_EXPORT extern _C_   // for static lib
	#define _STDCALL
#endif
#endif
#include "V100_internal_types.h"
#include "V100_shared_types.h"
#include "V100_disney_types.h"

typedef _V100_GENERAL_ERROR_CODES V100_ERROR_CODE;

/*********************** V100_Decode_Blob ***********************/
///////////////////////////////////////////////////////////////////////////////
///  global public  V100_Decode_Blob
///  Returns the number of devices attached to the system
///
///  @param [in, out]  
///
///  @return V100_ERROR_CODE Refer to Error code documentation for detailed description of
///							 possible return values. GEN_OK indicates operation was successful
///
///  @remarks
///
///  @see
///
///  @author www.lumidigm.com @date 6/12/2007
///////////////////////////////////////////////////////////////////////////////

VCOM_CORE_EXPORT V100_ERROR_CODE _STDCALL V100_Decode_Blob(uchar* pBlob, 
														    _V100_INTERFACE_CONFIGURATION_TYPE*	config, 
														   _V100_INTERFACE_COMMAND_TYPE*		cmd,		
														   _V100_INTERFACE_STATUS_TYPE*			status,
														   _V100_SYSTEM_DIAGNOSTICS*			sysDiag,
														   _V500_DIAGNOSTIC_PACKET*				v500_diag,
														   _V500_TRANSACTION_DATA*				v500_trans,
														   uchar* pImageBlob);

