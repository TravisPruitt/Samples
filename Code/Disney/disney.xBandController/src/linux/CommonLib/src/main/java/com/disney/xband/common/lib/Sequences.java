package com.disney.xband.common.lib;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

/**
 * Handles parsing sequence names from a configuration string and helping to select
 * a random sequence from the string based on its configured rotation %.
 *
 * The sequence configuration string should look like:
 *      entry_success,60:entry_celebration,30:entry_mickeycelebration,10
 *
 * This also handles the case where the configuration string just contains a
 * single sequence name like:
 *      entry_success
 */
public class Sequences extends ArrayList<Sequence> {
    private static Logger logger = Logger.getLogger(Sequences.class);

    /**
     * Used to hide so that the factory method must be used.
     */
    private Sequences() {
    }

    /**
     * Parses a sequence configuration string into all of the configured sequences and their
     * ratio percentage.
     *
     * A configuration string looks like "entry_success,60:entry_celebration,30:entry_mickeycelebration,10".
     * @param allSequenceStrings Configuration string containing 1 or multiple sequences.
     * @return List of sequence strings and their ratio percentage.
     */
    static public Sequences parseSequenceNames(String allSequenceStrings) {
        Sequences foundSequences = new Sequences();

        if ( allSequenceStrings == null || allSequenceStrings.isEmpty() )
            return foundSequences;

        // Parse out the sequence parts.
        String[] sequences = allSequenceStrings.split(";");
        if ( sequences != null )
        {
            for (String sequence : sequences)
            {
                // Split the sequence part into the sequence name and ratio.
                String[] sequenceParts = sequence.split(",");

                // We need both the name and ratio in order to add this.
                if ( sequenceParts != null )
                {
                    Integer ratio = null;
                    Long timeout = null;

                    if ( sequenceParts.length == 2 )
                    {
                        try
                        {
                            timeout = Long.parseLong(sequenceParts[1]);
                            foundSequences.add(new Sequence(sequenceParts[0], timeout, ratio));
                        }
                        catch ( NumberFormatException e )
                        {
                            logger.error(ExceptionFormatter.format("Sequence configuration contains an invalid ratio pair",
                                    e));
                        }
                    }
                    else if ( sequenceParts.length == 3 )
                    {
                        try
                        {
                            timeout = Long.parseLong(sequenceParts[1]);
                            ratio = Integer.parseInt(sequenceParts[2]);
                            foundSequences.add(new Sequence(sequenceParts[0], timeout, ratio));
                        }
                        catch ( NumberFormatException e )
                        {
                            logger.error(ExceptionFormatter.format("Sequence configuration contains an invalid ratio pair",
                                    e));
                        }
                    }
                    else
                    {
                        foundSequences.add(new Sequence(sequenceParts[0], timeout, ratio));
                    }
                }
            }
        }

        return foundSequences;
    }

    /**
     * Gets a contained sequence based on the rotation ratio and some level of randomness.
     * @return The selected sequence.
     */
    public Sequence getRandom()
    {
        Sequence selectedSequence = null;

        // If there's more than one sequence name, randomly select from them.
        if ( size() > 1 )
        {
            Random random = new Random();
            int randomValue = random.nextInt(100);
            int currentMax = 0;

            // Generate a random number from 1-100
            for (Sequence sequence : this)
            {
                currentMax += sequence.getRatio();
                if ( randomValue < currentMax )
                {
                    selectedSequence = sequence;
                    break;
                }
            }

            if ( selectedSequence == null )
            {
                logger.warn("No random sequence found. Ensure all sequence ratios equal 100 percent.");
            }
        }
        else if ( size() == 1 )
        {
            selectedSequence = get(0);
        }

        return selectedSequence;
    }
}
