package com.disney.xband.common.lib.junit.bvt;

import com.disney.xband.common.lib.Sequence;
import com.disney.xband.common.lib.Sequences;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TestSequences {

    @Test
    public void testParsing() {
        Sequences sequences = Sequences.parseSequenceNames("entry_success");
        assertTrue(sequences.size() == 1);
        assertEquals("entry_success", sequences.get(0).getName());
        assertNull(sequences.get(0).getTimeout());
        assertNull(sequences.get(0).getRatio());

        sequences = Sequences.parseSequenceNames("entry_success:");
        assertTrue(sequences.size() == 1);

        sequences = Sequences.parseSequenceNames("entry_success,2000,50");
        assertTrue(sequences.size() == 1);
        assertEquals("entry_success", sequences.get(0).getName());
        assertEquals(2000, (long)sequences.get(0).getTimeout());
        assertEquals(50, (long)sequences.get(0).getRatio());

        sequences = Sequences.parseSequenceNames("entry_success,2000,50;entry_celebrate");
        assertTrue(sequences.size() == 2);
        assertEquals(2000, (long)sequences.get(0).getTimeout());
        assertEquals(50, (long)sequences.get(0).getRatio());

        sequences = Sequences.parseSequenceNames("entry_success,1000,50;entry_celebrate,2000,50");
        assertTrue(sequences.size() == 2);
        assertEquals("entry_success", sequences.get(0).getName());
        assertEquals(1000, (long)sequences.get(0).getTimeout());
        assertEquals(50, (long)sequences.get(0).getRatio());
        assertEquals("entry_celebrate", sequences.get(1).getName());
        assertEquals(2000, (long)sequences.get(1).getTimeout());
        assertEquals(50, (long)sequences.get(1).getRatio());

        sequences = Sequences.parseSequenceNames("entry_success,5000;entry_celebrate,afk");
        assertEquals(1, sequences.size());

        sequences = Sequences.parseSequenceNames("entry_success,afk;entry_celebrate,afk");
        assertEquals(0, sequences.size());

        sequences = Sequences.parseSequenceNames("entry_success,1000,25;entry_celebrate,2000,50;entry_mickey,1000,25");
        assertTrue(sequences.size() == 3);
        assertEquals("entry_success", sequences.get(0).getName());
        assertEquals(1000, (long)sequences.get(0).getTimeout());
        assertEquals(25, (long)sequences.get(0).getRatio());
        assertEquals("entry_celebrate", sequences.get(1).getName());
        assertEquals(2000, (long)sequences.get(1).getTimeout());
        assertEquals(50, (long)sequences.get(1).getRatio());
        assertEquals("entry_mickey", sequences.get(2).getName());
        assertEquals(1000, (long)sequences.get(2).getTimeout());
        assertEquals(25, (long)sequences.get(2).getRatio());

        sequences = Sequences.parseSequenceNames("entry_success,1000,25;entry_celebrate,5000,50;entry_mickey,1000,25;");
        assertTrue(sequences.size() == 3);
        assertEquals("entry_success", sequences.get(0).getName());
        assertEquals(1000, (long)sequences.get(0).getTimeout());
        assertEquals(25, (long)sequences.get(0).getRatio());
        assertEquals("entry_celebrate", sequences.get(1).getName());
        assertEquals(5000, (long)sequences.get(1).getTimeout());
        assertEquals(50, (long)sequences.get(1).getRatio());
        assertEquals("entry_mickey", sequences.get(2).getName());
        assertEquals(1000, (long)sequences.get(2).getTimeout());
        assertEquals(25, (long)sequences.get(2).getRatio());
    }

    @Test
    public void testRandomSelection() {
        Sequences sequences = Sequences.parseSequenceNames("entry_success");
        Sequence selectedSequence = sequences.getRandom();
        assertEquals("entry_success", selectedSequence.getName());

        sequences = Sequences.parseSequenceNames("entry_success,1000,50");
        selectedSequence = sequences.getRandom();
        assertEquals("entry_success", selectedSequence.getName());

        sequences = Sequences.parseSequenceNames("entry_success,1000,0;entry_celebrate,1000,100");
        selectedSequence = sequences.getRandom();
        assertEquals("entry_celebrate", selectedSequence.getName());

        sequences = Sequences.parseSequenceNames("entry_success,1000,0;entry_celebrate,1000,100;entry_mickey,1000,0");
        selectedSequence = sequences.getRandom();
        assertEquals("entry_celebrate", selectedSequence.getName());

        sequences = Sequences.parseSequenceNames("entry_success,1000,0;entry_celebrate,1000,0;entry_mickey,1000,100");
        selectedSequence = sequences.getRandom();
        assertEquals("entry_mickey", selectedSequence.getName());
    }
}
