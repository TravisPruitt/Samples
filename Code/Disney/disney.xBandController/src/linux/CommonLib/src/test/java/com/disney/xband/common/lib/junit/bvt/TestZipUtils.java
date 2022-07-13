package com.disney.xband.common.lib.junit.bvt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

import com.disney.xband.common.lib.ZipUtils;

public class TestZipUtils
{	
	@Test 
	public void testGzipFile() throws IOException
	{
		File file = File.createTempFile("test", ".txt");
		BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
		    for (int i = 0; i < 10000; i++)
			    writer.write("Line number " + i + "\n");
        }
        finally {
            if(writer != null) {
		        writer.close();
            }
        }
		
		try
		{
			String newFilePath = ZipUtils.gzip(file.getAbsolutePath());			
			new File(newFilePath).delete();
		}
		finally
		{
			file.delete();
		}
	}
}
