package com.disney.xband.xbrc.Controller;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.lib.xbrapi.ReaderManifest;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class PackageManager
{
	private static Logger logger = Logger.getLogger(PackageManager.class);
	private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private Map<String, TreeMap<String, ReaderVersion>> packages = new HashMap<String, TreeMap<String, ReaderVersion>>();

	private Pattern manifestPattern = Pattern
			.compile("([A-Za-z0-9]+)-([0-9]+\\.[0-9]+\\.[0-9]+[\\.-][0-9]+).*");
	private Map<String, ReaderVersion> upgradeToVersions;	// keyed by hardware type (lower cased)
	
	public PackageManager()
	{
		upgradeToVersions = new HashMap<String, PackageManager.ReaderVersion>();
	}

	public static class ReaderVersion 
	{
		private String hwid;
		private ReaderManifest manifest;
		private String version;
		private String path;
		
		public ReaderVersion(String hwid,  String version, String path, ReaderManifest manifest)
		{
			super();
			this.path = path;
			this.hwid = hwid;
			this.version = version;
			this.manifest = manifest;
		}

		public String getVersion()
		{
			return version;
		}
		public void setVersion(String version)
		{
			this.version = version;
		}

		public ReaderManifest getManifest()
		{
			return manifest;
		}

		public void setManifest(ReaderManifest manifest)
		{
			this.manifest = manifest;
		}

		public String getHwid()
		{
			return hwid;
		}

		public void setHwid(String hwid)
		{
			this.hwid = hwid;
		}

		public String getPath()
		{
			return path;
		}

		public void setPath(String path)
		{
			this.path = path;
		}
	}
	
	/*
	 * Scan the directory for all installer packages and build a map keyed on
	 * reader types.
	 */
	public void refereshPackages(String packagesDirectory)
	{
		rwl.writeLock().lock();
		try
		{
			packages.clear();
			upgradeToVersions.clear();
			
			try
			{
				File dir = new File(InputValidator.validateDirectoryName(packagesDirectory));
				if (dir.exists() && dir.isDirectory())
				{							
					// See if we are using the manifests or just ipk packages
					File manifests = new File(dir.getAbsolutePath() + "/manifests");
					if (manifests.exists() && manifests.isDirectory())
						processReposDirectory(dir);
					else
					{
						logger.warn(dir.getAbsolutePath() + "/manifests is missing. " 
								+ " Not looking for packages.");
					}
				}
				else
				{
					logger.info(dir.getCanonicalPath()
							+ " is not a valid directory. Not looking for reader packages.");
				}
			}
			catch(IOException e)
			{
				logger.fatal("Caught IOException while looking for reader packages in " + packagesDirectory);
			}
		}
		finally
		{
			rwl.writeLock().unlock();
		}
	}

	public boolean hasUpgradeToVersion(String hwid)
	{
		ReaderVersion rv = getUpgradeToVersion(hwid);
		return rv != null;
	}
	
	public ReaderVersion getUpgradeToVersion(String hwid)
	{
		rwl.readLock().lock();
		try
		{
			if (hwid == null)
				return null;
			
			return upgradeToVersions.get(hwid.toLowerCase());
		}
		finally
		{
			rwl.readLock().unlock();
		}
	}
	
	/*
	 * Returns true if the reader version is fine. Returns false if the reader version needs to be upgraded.
	 */
	public boolean checkReaderVersion(ReaderInfo reader)
	{
		VersionComparator comparator = new VersionComparator();
		ReaderVersion upgradeToVersion = getUpgradeToVersion(reader.getHardwareType());
		int cmp = comparator.compare(reader.getNormalizedVersion(), upgradeToVersion.getVersion());
		
		return cmp >= 0;
	}
	
	public Collection<ReaderVersion> getAvailablePackages(String hwid)
	{
		TreeMap<String, ReaderVersion> map = packages.get(hwid.toLowerCase());
		if (map == null)
			return null;
		return map.values();
	}

	/*
	 * Comparator to compare four digit version like 1.3.23.2
	 */
	public static class VersionComparator implements Comparator<String>
	{
		public int compare(String k1, String k2)
		{

			// Check for nulls to prevent null pointer exceptions..
			if (k1 == null && k2 == null)
				return 0; // match
			if (k1 == null || k2 == null)
				return 1; // no match

			String parts1[] = k1.split("[\\.-]");
			String parts2[] = k2.split("[\\.-]");

			int len = Math.min(parts1.length, parts2.length);

			for (int i = 0; i < len; i++)
			{
				int res = parts1[i].compareTo(parts2[i]);
				if (res != 0)
					return res;
			}
			return 0;
		}
	}
	
	private void processReposDirectory(File dir) throws IOException
	{
		File manifests = new File(dir.getAbsoluteFile() + "/manifests");
		
		for (File file : manifests.listFiles())
		{
			if (file.isDirectory())
				continue;
	
			// Process file...
			Matcher m = manifestPattern.matcher(file.getName());
			if (!m.matches())
			{
				logger.warn(file.getName()
						+ " does not look like a valid installer manifest file. Ignoring...");
				continue;
			}

			if (m.groupCount() < 2)
			{
				logger.warn(file.getName()
						+ " does not look like a valid installer manifest file. Ignoring...");
				continue;
			}

			//String sType = m.group(1).toLowerCase();
			String sVersion = m.group(2);
			
			// Reader the JSON manifest file into an object
			ReaderManifest rm = null;
			try
			{
				ObjectMapper om = new ObjectMapper();
				rm = (ReaderManifest) om.readValue(file, ReaderManifest.class);
			}
			catch (Exception e)
			{
				logger.error("Failed to parse the reader manifest file " + file.getName() + ". Ignoring...");
				continue;
			}
			
			if (rm.getHwids() == null || rm.getHwids().size() == 0)
			{
				logger.error("Reader manifest file " + file.getName() + " is missing the hwids field. Ignoring...");
				continue;
			}
			
			for (String hwid : rm.getHwids())
			{
				hwid = hwid.toLowerCase();
				
				TreeMap<String, ReaderVersion> versions = packages.get(hwid);
				if (versions == null)
				{
					versions = new TreeMap<String, ReaderVersion>(
							new VersionComparator());
					packages.put(hwid, versions);
				}
				
				versions.put(sVersion, new ReaderVersion(hwid, sVersion, file.getPath(), rm));
			}
		}
		
		// Pick the latest version for each hardware type
		for (String hwid : packages.keySet())
		{
			TreeMap<String, ReaderVersion> versions = packages.get(hwid);
			if (versions == null || versions.isEmpty())
				continue;
			ReaderVersion latestVersion = versions.lastEntry().getValue();
			
			logger.info("Using latest manifest file " + latestVersion.getPath() + " for readers of hardware type " + hwid);
			upgradeToVersions.put(hwid, latestVersion);
		}
	}
}
