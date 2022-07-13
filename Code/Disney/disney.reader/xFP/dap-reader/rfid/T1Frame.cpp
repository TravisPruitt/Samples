/**
    @file   T1Frame.cpp
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com

    Handles a T=1 Frame to SAM.
    Documented in section 11 of ISO 7816-3

    A T=1 frame for outgoing transmission looks like this:

    <NAD><PCB><length><payload><crc>

    NAD = Node address byte.  It can be used to indicate the source and destination of the
          tranmission when using logical channels, but we always set it to 0.

    PCB = Protocol Control Byte.  The meanins of the bits differs depending on whether
          it is an outgoing command or a reply.  For what are called I-block transmissions
          (the type we use), the 6th bit (0x40), is a sequence "number" that must toggle.

    crc = Can be a 1 or 2 byte CRC.
*/


#include "standard.h"
#include "RFIDExceptions.h"
//#include "BitMask.h"
#include "T1Frame.h"
#include "Serializer.h"
#include "CRC16.h"
#include "log.h"
#include <string.h>


using namespace RFID;

int GetEDCLength(ErrorDetectionType::Enum edctype)
{
    switch (edctype)
    {
    case ErrorDetectionType::LRC:
        return 1;

    case ErrorDetectionType::CRC:
        return 2;

    default:
        return 0;
    }
};

BlockType::Enum PCB::BlockType()
{
    if (!Bitmask::IsSet(0x80, _value))
        return BlockType::IBlock;
    else if (!Bitmask::IsSet(0x40, _value))
        return BlockType::RBlock;
    else 
        return BlockType::SBlock; 
}

FrameSequenceNumber::Enum PCB::SequenceNumber()
{
    switch (this->BlockType())
    {
        case BlockType::IBlock:
            return Bitmask::IsSet(MASK_ISEQ, _value) ? FrameSequenceNumber::Odd : FrameSequenceNumber::Even;

        case BlockType::RBlock:
            return Bitmask::IsSet(MASK_RSEQ, _value) ? FrameSequenceNumber::Odd : FrameSequenceNumber::Even;

        default:
            LOG_WARN("SAM: SequenceNumber not valid for this block type\n");
            throw SAMException();
    }
}

void PCB::SequenceNumber(FrameSequenceNumber::Enum value)
{
    switch (this->BlockType())
    {
        case BlockType::IBlock:
            Bitmask::SetBit(value == FrameSequenceNumber::Odd, MASK_ISEQ, _value);
            break;

        case BlockType::RBlock:
            Bitmask::SetBit(value == FrameSequenceNumber::Odd, MASK_RSEQ, _value);
            break;

        default:
            LOG_WARN("SequenceNumber not valid for this block type\n");
            throw SAMException();
    }
}

// Only called within a response
T1Frame::T1Frame(const uint8_t* bytes, unsigned int length)
{
    _edctype = DetectEDCType(bytes, length);
    if (_edctype == ErrorDetectionType::None)
    {
        _bytes.resize(length+1);
        memcpy(&_bytes[0], bytes, length);

        _edctype = ErrorDetectionType::LRC;
        SetEDC();
    }
    else
    {
        _bytes.resize(length);
        memcpy(&_bytes[0], bytes, length);
        if (!ValidateEDC()) 
        {
            LOG_WARN("SAM: T=1 frame failed error detection test\n");
            throw SAMException();
        }
    }
}

T1Frame::T1Frame(uint8_t nad, uint8_t pcb, const uint8_t* inf, unsigned int infLen)
{
    Initialize(nad, pcb, inf, infLen, ErrorDetectionType::LRC);
}

T1Frame::T1Frame(uint8_t nad, uint8_t pcb, const uint8_t* inf, unsigned int infLen, ErrorDetectionType::Enum edctype)
{
    Initialize(nad, pcb, inf, infLen, edctype);
}

unsigned int T1Frame::Initialize(uint8_t nad, uint8_t pcb, const uint8_t* inf, unsigned int infLen, ErrorDetectionType::Enum edctype)
{ 
    unsigned int length = 3 + infLen + GetEDCLength(edctype);
    _bytes.resize(length);
    _bytes[OFFSET_NAD] = nad;
    _bytes[OFFSET_PCB] = pcb;
    _bytes[OFFSET_LEN] = infLen;

    //if (inf != null) Buffer.BlockCopy(inf, 0, _bytes, OFFSET_INF, inf.Length);
    memcpy(&_bytes[OFFSET_INF], inf, infLen);

    _edctype = edctype;
    SetEDC();
    return _bytes.size();
}

T1Frame::~T1Frame()
{
}


ErrorDetectionType::Enum T1Frame::DetectEDCType(const uint8_t* bytes, unsigned int length)
{
    if (length < 3) 
    {
        LOG_WARN("SAM: A valid T=1 frame must include at least 3 bytes, not including the EDC\n");
        throw SAMException();
    }

    int minLength = 3 + bytes[OFFSET_LEN];
    switch (length - minLength)
    {
        case 0:
            return /*ErrorDetectionType*/ErrorDetectionType::None;

        case 1:
            return /*ErrorDetectionType*/ErrorDetectionType::LRC;

        case 2:
            return /*ErrorDetectionType*/ErrorDetectionType::CRC;

        default:
            LOG_WARN("SAM: Invalid T=1 frame length\n");
            throw SAMException();
    }    
};

bool T1Frame::ValidateEDC()
{
    switch (_edctype)
    {
    case /*ErrorDetectionType*/ErrorDetectionType::LRC:
            return ValidateLRC();

    case /*ErrorDetectionType*/ErrorDetectionType::CRC:
            return ValidateCRC();

        default:
            return true;
    }
};

void T1Frame::SetEDC()
{
    switch (_edctype)
    {
    case ErrorDetectionType::LRC:
        {
        uint8_t lrc = ComputeLRC();
        _bytes[_bytes.size() - 1] = lrc;
        }
        break;

    case ErrorDetectionType::CRC:
        {
        uint16_t crc = ComputeCRC();
        Serializer::SerializeInt16(&_bytes[0], (short)crc, _bytes.size() - 2);
        }
        break;

    default:
        break;
    }
};

uint16_t T1Frame::ComputeCRC()
{
    return CRC16::ComputeCRC16_CCITT(&_bytes[0], 0, _bytes.size() - 2, false);
}

bool T1Frame::ValidateCRC()
{
    if (_bytes.size() < 2)
        return false;

    int offset = _bytes.size() - 2;
    uint16_t crc = (uint16_t)Serializer::DeserializeInt16(&_bytes[0], offset);
    return crc == ComputeCRC();
}

uint8_t T1Frame::ComputeLRC()
{
    uint8_t lrc = 0;
    for (unsigned int idx = 0; idx < _bytes.size() - 1; idx++)
        lrc ^= _bytes[idx];

    return lrc;
}

bool T1Frame::ValidateLRC()
{
    uint8_t lrc = 0;
    for (unsigned int idx = 0; idx < _bytes.size(); idx++)
        lrc ^= _bytes[idx];

    return lrc == 0x00;
}
