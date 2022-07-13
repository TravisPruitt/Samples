#include "ITransport.h"
#include "ICmd.h"
#include "V100_shared_types.h"
#include "V100_internal_types.h"

ICmd* ITransport::GetResponse()
{
	_V100_COMMAND_SET cmd;
	memcpy(&cmd,&pResponseBytes[2],sizeof(int));
	ICmd* pCmd = CreateCommand(cmd);
	pCmd->UnpackResponse(pResponseBytes,nSize);
	return pCmd;
}