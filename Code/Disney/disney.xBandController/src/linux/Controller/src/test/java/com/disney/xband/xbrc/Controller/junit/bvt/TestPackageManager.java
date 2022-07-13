package com.disney.xband.xbrc.Controller.junit.bvt;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.disney.xband.xbrc.Controller.PackageManager;
import com.disney.xband.xbrc.Controller.PackageManager.ReaderVersion;
import com.disney.xband.xbrc.lib.entity.ReaderType;

public class TestPackageManager
{		
	@Test
	public void testRepos() throws IOException
	{
		PackageManager pm = new PackageManager();
		
		if (new File("tests/repos").isDirectory())	
			pm.refereshPackages("tests/repos");
		else if (new File("Controller/tests/repos").isDirectory())
			pm.refereshPackages("Controller/tests/repos");
		else {
			System.out.println("Skipping PackageManger::testRepos because tests/repos directory was not in the path...");
			return;			
		}
		
		PackageManager.ReaderVersion[] lrrexpected = 
			{ 
				new ReaderVersion("xbr", "0.0.8-0", "tests/repos/manifests/xBR-0.0.8-0.manifest", null)
			};
		
		PackageManager.ReaderVersion[] xfpexpected = 
			{ 
				new ReaderVersion("xtp", "0.0.1-0", "tests/repos/manifests/xTP-0.0.1-0.manifest", null)
			};
		
		Collection<ReaderVersion> lrrpackages = pm.getAvailablePackages("xbr");				
		int i = 0;
		for (ReaderVersion actual : lrrpackages)
		{
			ReaderVersion expected =  lrrexpected[i++];
			assertEquals(actual.getVersion(), expected.getVersion());
			assertTrue(actual.getPath().contains(expected.getPath()));
		}
		assertEquals(lrrexpected.length,i);
		
		Collection<ReaderVersion> xfppackages = pm.getAvailablePackages("xtp");
		i = 0;
		for (ReaderVersion actual : xfppackages)
		{
			ReaderVersion expected =  xfpexpected[i++];
			assertEquals(actual.getVersion(), expected.getVersion());
			assertTrue(actual.getPath().contains(expected.getPath()));
		}
		assertEquals(xfpexpected.length,i);
		
		//
		// Now see if we get the expected package for each reader type.
		//
		
		ReaderVersion lrrRv = pm.getUpgradeToVersion("xbr");
		ReaderVersion xfpRv = pm.getUpgradeToVersion("xtp");
		
		assertEquals("0.0.8-0", lrrRv.getVersion());
		assertEquals("0.0.1-0", xfpRv.getVersion());	
	}
	
	@Test
	public void testVersionComparator() 
	{
		PackageManager.VersionComparator vc = new PackageManager.VersionComparator();
		
		assertTrue(vc.compare("0.0.0.0", "0.0.0.0") == 0);
		assertTrue(vc.compare("0.0.0-0", "0.0.0-0") == 0);
		
		assertTrue(vc.compare("1.0.31.99", "1.0.31.99") == 0);
		assertTrue(vc.compare("1.0.31-99", "1.0.31-99") == 0);
		
		assertTrue(vc.compare("0.0.0.0", "1.0.0.0") < 0);
		assertTrue(vc.compare("0.0.0-0", "1.0.0-0") < 0);
		
		assertTrue(vc.compare("1.0.0.2", "1.0.0.3") < 0);
		assertTrue(vc.compare("1.0.0-2", "1.0.0-3") < 0);
		
		assertTrue(vc.compare("1.0.0.2", "0.1.2.30") > 0);
		assertTrue(vc.compare("1.0.0-2", "0.1.2-30") > 0);
		
		assertTrue(vc.compare("1.0.4-1958", "1.0.4-1959") < 0);
		assertTrue(vc.compare("1.0.4-1959", "1.0.4-1958") > 0);
	}
}
