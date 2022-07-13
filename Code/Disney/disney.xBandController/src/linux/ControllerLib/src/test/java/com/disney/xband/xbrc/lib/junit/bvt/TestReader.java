package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.*;

import org.junit.Test;

import com.disney.xband.xbrc.lib.entity.Reader;

public class TestReader
{

	@Test
	public void testSanitizeReaderId()
	{
		/*
		 * Illegal charachters:
		 * \s|\r\n|\\||<|>|u003E|u003C|\"|u0022|'|u0027|`|u0060|*|u0028|=|u003D
		 */
		Reader reader = new Reader();
		
		reader.setReaderId("Name with* \n char");
		reader.sanitizeReaderId();
		assertEquals(reader.getReaderId(),"Namewithchar");
		
		reader.setReaderId("Name wi'`th \n\n char");
		reader.sanitizeReaderId();
		assertEquals(reader.getReaderId(),"Namewithchar");
		
		reader.setReaderId("Name \"with\" \r char");
		reader.sanitizeReaderId();
		assertEquals(reader.getReaderId(),"Namewithchar");
		
		reader.setReaderId("Name with \r\n char");
		reader.sanitizeReaderId();
		assertEquals(reader.getReaderId(),"Namewithchar");
		
		reader.setReaderId("<hi>Namewith=twochars</hi>");
		reader.sanitizeReaderId();
		assertEquals(reader.getReaderId(),"hiNamewithtwochars/hi");
		
		reader.setReaderId("\r\n rea||||der1\u0020");
		reader.sanitizeReaderId();
		assertEquals(reader.getReaderId(),"reader1");
	}

}
