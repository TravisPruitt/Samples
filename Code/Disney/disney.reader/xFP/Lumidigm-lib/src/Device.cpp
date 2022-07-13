#include "Device.h"

Device::Device(void)
{
	memset(m_commPipeName, 0, SZ_OF_PIPE_NAME);
	memset(m_deviceName, 0, SZ_OF_PIPE_NAME);
	m_deviceType   = V100_NORMAL_DEVICE;
	m_deviceNumber = 0;
	m_nMemberIndex = 0;
	m_nSensorType  = 0;
	memset(m_commServerName, 0, SZ_OF_PIPE_NAME);
	GetCommServerName(m_commServerName);
}

Device::Device(uint deviceNumber, V100_DEVICE_TYPE deviceType, uchar* strCommPipeName, uchar* strDeviceName, uint nMemberIndex, uint nSensorType)
{
	memset(m_commPipeName, 0, SZ_OF_PIPE_NAME);
	memset(m_deviceName, 0, SZ_OF_PIPE_NAME);
	m_deviceType = deviceType;
	if(strCommPipeName)
	{
		memcpy(m_commPipeName, strCommPipeName, strlen((const char*)strCommPipeName));
	}
	if(strDeviceName)
	{
		memcpy(m_deviceName, strDeviceName, strlen((const char*)strDeviceName));
	}
	m_deviceNumber = deviceNumber;
	
	m_nMemberIndex = nMemberIndex;
	m_nSensorType = nSensorType;
	//
	memset(m_commServerName, 0, SZ_OF_PIPE_NAME);
	GetCommServerName(m_commServerName);
}

Device::~Device()
{
	
}

void Device::Init(uint deviceNumber, V100_DEVICE_TYPE deviceType, uchar* strCommPipeName, uchar* strDeviceName, uint nMemberIndex, uint nSensorType)
{
	memset(m_commPipeName, 0, SZ_OF_PIPE_NAME);
	memset(m_deviceName, 0, SZ_OF_PIPE_NAME);
	
	if(strCommPipeName)
	{
		memcpy(m_commPipeName, strCommPipeName, strlen((const char*)strCommPipeName));
	}

	if(strDeviceName)
	{
		memcpy(m_deviceName, strDeviceName, strlen((const char*)strDeviceName));
	}

	m_deviceType   = deviceType;
	m_deviceNumber = deviceNumber;
	m_nMemberIndex = nMemberIndex;
	m_nSensorType  = nSensorType;
}

V100_DEVICE_TYPE Device::GetDeviceType()
{
	return m_deviceType;
}

void Device::GetCommPipeName(uchar* strCommPipeName)
{
	//<TODO>: Check pointer comming in
	//int x = strlen((const char*)m_commPipeName);
	memcpy(strCommPipeName, m_commPipeName, strlen((const char*)m_commPipeName));
}
void Device::GetCommServerName(uchar* strCommPipeName)
{

	char* pSvrName = NULL;
	pSvrName = getenv("XDClientName");
	if(pSvrName != NULL)
	{
		memcpy(strCommPipeName, pSvrName, strlen((const char*)pSvrName));
	}
	else
	{
		strCommPipeName[0] = '.';
		strCommPipeName[1] = '\0';
		return;
	}	

}
uint Device::GetDeviceNumber()
{
	return m_deviceNumber;
}