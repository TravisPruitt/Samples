#include "VCOMDiag.h"
#include "string.h"

V100_ERROR_CODE V100_Decode_Blob(uchar* pBlob, 
							   _V100_INTERFACE_CONFIGURATION_TYPE*	config, 
							   _V100_INTERFACE_COMMAND_TYPE*		cmd,		
							   _V100_INTERFACE_STATUS_TYPE*			status,
							   _V100_SYSTEM_DIAGNOSTICS*			sysDiag,
							   _V500_DIAGNOSTIC_PACKET*				v500_diag,
							   _V500_TRANSACTION_DATA*				v500_trans,
							   uchar* pImageBlob)
{
	DiagHeader* pHdr = (DiagHeader*)(pBlob);
	//
	memcpy(pImageBlob, pBlob, pHdr->nImageBlobSize);
	pBlob+=pHdr->nImageBlobSize;
	memcpy(config, pBlob, sizeof(_V100_INTERFACE_CONFIGURATION_TYPE));
	pBlob+= sizeof(_V100_INTERFACE_CONFIGURATION_TYPE);
	memcpy(cmd, pBlob, sizeof(_V100_INTERFACE_COMMAND_TYPE)); 
	pBlob+=sizeof(_V100_INTERFACE_COMMAND_TYPE);
	memcpy(status, pBlob, sizeof(_V100_INTERFACE_STATUS_TYPE));
	pBlob+=sizeof(_V100_INTERFACE_STATUS_TYPE);
	memcpy(sysDiag, pBlob, sizeof(_V100_SYSTEM_DIAGNOSTICS)); 
	pBlob+=sizeof(_V100_SYSTEM_DIAGNOSTICS);
	memcpy(v500_diag, pBlob, sizeof(_V500_DIAGNOSTIC_PACKET));
	pBlob+=sizeof(_V500_DIAGNOSTIC_PACKET);
	memcpy(v500_trans, pBlob, sizeof(_V500_TRANSACTION_DATA));

	return GEN_OK;
}