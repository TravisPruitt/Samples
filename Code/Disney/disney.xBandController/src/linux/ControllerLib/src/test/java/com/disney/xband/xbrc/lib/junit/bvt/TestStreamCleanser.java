package com.disney.xband.xbrc.lib.junit.bvt;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.disney.xband.xbrc.lib.utils.StreamCleanser;

public class TestStreamCleanser {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRun() {
		try {
			String osName = System.getProperty("os.name");
			
			String[] cmd = new String[3];
			
			// Runtime.exec() method does not spun its own shell
			cmd[0] = "/bin/sh";
			cmd[1] = "-c";
			cmd[2] = "echo $PPID";
			
			if (!osName.startsWith("Windows")){
				
				Process proc = Runtime.getRuntime().exec(cmd);
				
				// error and input buffers will hang when full, so they have to be cleaned up
				StreamCleanser errorSC = new StreamCleanser(proc.getErrorStream(), "ERROR");
				StreamCleanser outputSC = new StreamCleanser(proc.getInputStream(), "OUTPUT");
				
				errorSC.run();
				outputSC.run();
				
				int exitVal = proc.waitFor();
				
				System.out.println("ExitValue: " + exitVal);
			}
				
		} catch (Throwable t) {
			fail(t.getLocalizedMessage());
		}
	}

}
