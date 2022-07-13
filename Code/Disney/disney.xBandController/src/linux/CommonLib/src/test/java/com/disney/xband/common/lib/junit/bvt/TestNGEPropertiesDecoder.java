package com.disney.xband.common.lib.junit.bvt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.disney.xband.common.lib.NGEPropertiesDecoder;

public class TestNGEPropertiesDecoder
{
	@Test
	public void testDecoder() throws IOException
	{	
		NGEPropertiesDecoder decoder = new NGEPropertiesDecoder();
		decoder.setJasyptPropertiesPath("classpath:com/disney/xband/common/lib/junit/bvt/jasypt.properties");		
		
		if (new File("tests").isDirectory())	
			decoder.setPropertiesPath("tests/decodertest.properties");
		else if (new File("CommonLib/tests").isDirectory())
			decoder.setPropertiesPath("CommonLib/tests/decodertest.properties");
		else 
			return;
		
		decoder.initialize();
	
		String keyTwoEncrypted = decoder.encrypt("key two");
		String keyTwoEncryptedAgain = decoder.encrypt("key two");
		String keyTwoDecrypted = decoder.decrypt(keyTwoEncrypted);
		String keyTwoDecryptedAgain = decoder.decrypt(keyTwoEncryptedAgain);
		assertEquals(keyTwoDecrypted,"key two");
		assertEquals(keyTwoDecryptedAgain,"key two");
		
		Properties prop = decoder.read();
		assertEquals(prop.get("key.one"), "key one");
		assertEquals(prop.get("key.two"), "key two");
	}
}
