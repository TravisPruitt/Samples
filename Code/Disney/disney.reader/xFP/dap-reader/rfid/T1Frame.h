/**
    @file   T1Frame.h
    @author Roy Sprowl
    @date   Jan 2012

    Copyright (c) 2012, synapse.com
*/

#ifndef T1_FRAME_H
#define T1_FRAME_H

#include "BitMask.h"
#include "ByteArray.h"
#include <vector>

using namespace Reader;

namespace RFID
{


/// T=1 Protocol Block Types
namespace BlockType { enum Enum
{
    /// Information block for use by application layer
    IBlock = 0x00,

    /// Response block for conveying ACK or NACK messages
    RBlock = 0x80,

    /// Status block for controlling state and protocol characteristics
    SBlock = 0xC0
};}

/// T=1 Sequence number - sets opposing parity
/// on sequential frames to track chained series
/// of frames or indicate when a frame is missing
namespace FrameSequenceNumber { enum Enum 
{
    Even = 0x00,
    Odd = 0x40,
};}

/// Smart Card T=1 Node Address Byte
struct NAD
{
private:
    uint8_t _value;

public:
    uint8_t Source()
    {
        return (uint8_t)(_value & 0x07); 
    }

    uint8_t Destination()
    {
        return (uint8_t)((_value & 0x70) >> 4); 
    }

    //static implicit operator byte(NAD nad)
    //{
    //    return nad._value;
    //}

    //static explicit operator NAD(uint8_t b)
    //{
    //    return new NAD { _value = b };
    //}
};

/// Smart Card T=1 Protocol Control Byte
struct PCB
{
private:
    const static uint8_t MASK_CHAINED = 0x20;
    const static uint8_t MASK_ISEQ = 0x40;
    const static uint8_t MASK_RSEQ = 0x10;

    uint8_t _value;

public:
    PCB(uint8_t value) { _value = value; }
    PCB() { _value = 0; }

    BlockType::Enum BlockType();

    void BlockType(BlockType::Enum value)
    {
        _value = (uint8_t)value; 
    }

    FrameSequenceNumber::Enum SequenceNumber();
    void SequenceNumber(FrameSequenceNumber::Enum value);

    bool IsChained() const
    {
        return Bitmask::IsSet(MASK_CHAINED, _value);
    }

    void IsChained(bool value)
    {
        Bitmask::SetBit(value, MASK_CHAINED, _value); 
    }

    operator unsigned char()
    {
        return _value;
    }

    //public override string ToString()
    //{
    //    return string.Format(@"{0:X2}", _value);
    //}

    //public static implicit operator byte(PCB pcb)
    //{
    //    return pcb._value;
    //}

    //public static explicit operator PCB(uint8_t b)
    //{
    //    return new PCB { _value = b };
    //}
};

namespace ErrorDetectionType { enum Enum
{
    LRC,
    CRC,
    None
};}

/// Smart Card T=1 protocol frame as defined by ISO 7816-3
class T1Frame
{
private:
    const static int OFFSET_NAD = 0;
    const static int OFFSET_PCB = 1;
    const static int OFFSET_LEN = 2;
    const static int OFFSET_INF = 3;

    std::vector<uint8_t> _bytes;

    ErrorDetectionType::Enum _edctype;

    unsigned int Initialize(uint8_t nad, uint8_t pcb, const uint8_t* inf, unsigned int infLen, ErrorDetectionType::Enum edctype);

public:
    ~T1Frame();

    T1Frame(uint8_t nad, uint8_t pcb, const uint8_t* inf, unsigned int infLen);
    T1Frame(uint8_t nad, uint8_t pcb, const uint8_t* inf, unsigned int infLen, ErrorDetectionType::Enum edctype);

    // Only called within a response
    T1Frame(const uint8_t* bytes, unsigned int length);

    T1Frame(const T1Frame& rhs)
    {
        _bytes = rhs._bytes;
    }

    T1Frame()
    {
    }

    T1Frame& operator= (const T1Frame& rhs)
    {
        _bytes = rhs._bytes;
        return (*this);
    }

    uint8_t* getData() { return &_bytes[0]; }
    unsigned getLength() { return _bytes.size(); }

    // Write self to the supplied queue
    void Enqueue(ByteArray& data) const
    {
        data.append(&_bytes[0], _bytes.size());
    }

    // Lenght of INF
    uint8_t LEN()
    {
        return _bytes.size() > (unsigned)OFFSET_LEN ? _bytes[OFFSET_LEN] : 0;
    }

    const uint8_t* INF() const
    {
        return &_bytes[OFFSET_INF];
    }

    PCB getPCB() const
    {
        return _bytes.size() >= (unsigned)OFFSET_PCB ? RFID::PCB(_bytes[OFFSET_PCB]) : (RFID::PCB)0;
    }

    bool IsChained() const
    {
        return this->getPCB().IsChained();
    }

    static T1Frame CreateACK(FrameSequenceNumber::Enum sequence)
    {
        RFID::PCB pcb;
        pcb.BlockType(BlockType::RBlock);
        pcb.SequenceNumber(sequence);

        T1Frame t1(0x00, pcb, 0, 0);
        return t1;
    }

private:
    uint8_t ComputeLRC();
    bool ValidateLRC();
    uint16_t ComputeCRC();
    bool ValidateCRC();
    bool ValidateEDC();
    void SetEDC();
    static ErrorDetectionType::Enum DetectEDCType(const uint8_t* bytes, unsigned int length);
};


}

#endif // T1_FRAME_H

