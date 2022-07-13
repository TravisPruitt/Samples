package com.disney.xband.lib.controllerapi;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="sequences")
public class ReaderSequenceInfo
{
    private Collection<ReaderSequence> sequences = new ArrayList<ReaderSequence>();

    private String sTimestamp;

    public void setReaderSequences(Collection<ReaderSequence> sequences)
    {
        this.sequences = sequences;
    }

    public void setTimestamp(String sTimestamp)
    {
        this.sTimestamp = sTimestamp;
    }

    @XmlAttribute(name="time")
    public String getTimestamp()
    {
        return sTimestamp;
    }

    @XmlElement(name="sequence")
    public Collection<ReaderSequence> getReaderSequences()
    {
        return sequences;
    }
}
