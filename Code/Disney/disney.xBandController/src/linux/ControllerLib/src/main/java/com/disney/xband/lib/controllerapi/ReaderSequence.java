package com.disney.xband.lib.controllerapi;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;

@XmlRootElement(name="sequence")
public class ReaderSequence implements Comparable<ReaderSequence> {
    public ReaderSequence()
    {
    }

    public ReaderSequence( String name, boolean isDefault )
    {
        setName(name);
        setIsReaderDefault(isDefault);
    }

    private String Name;

    private boolean IsReaderDefault;

    @Override
    public int compareTo(ReaderSequence sequence) {
        return getName().toLowerCase().compareTo(sequence.getName().toLowerCase());
    }

    @XmlAttribute(name="name")
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @XmlAttribute(name="readerdefault")
    public boolean getIsReaderDefault() {
        return IsReaderDefault;
    }

    public void setIsReaderDefault(boolean isReaderDefault) {
        IsReaderDefault = isReaderDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReaderSequence that = (ReaderSequence) o;

        if (IsReaderDefault != that.IsReaderDefault) return false;
        if (!Name.equals(that.Name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Name.hashCode();
        result = 31 * result + (IsReaderDefault ? 1 : 0);
        return result;
    }
}
