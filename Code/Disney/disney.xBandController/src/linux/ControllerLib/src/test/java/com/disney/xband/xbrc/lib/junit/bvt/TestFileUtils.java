package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.*;

import org.junit.Test;

import com.disney.xband.xbrc.lib.utils.FileUtils;

public class TestFileUtils
{
	@Test
	public void testHideLeadingChars() 
	{
		String src = "";
		String res = "";
		
		res = FileUtils.hideLeadingChars(src, 4);
		assertEquals(res,"");
		
		src = "1";
		res = FileUtils.hideLeadingChars(src, 4);
		assertEquals(res,"1");
		
		src = "1";
		res = FileUtils.hideLeadingChars(src, 1);
		assertEquals(res,"1");
		
		src = "123";
		res = FileUtils.hideLeadingChars(src, 1);
		assertEquals(res,"**3");
		
		src = "12345678";
		res = FileUtils.hideLeadingChars(src, 4);
		assertEquals(res,"****5678");
		
		src = null;
		res = FileUtils.hideLeadingChars(src, 4);
		assertEquals(res,"");
	}
}
