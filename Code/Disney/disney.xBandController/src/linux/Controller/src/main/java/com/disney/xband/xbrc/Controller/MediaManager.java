package com.disney.xband.xbrc.Controller;

import com.disney.xband.xbrc.lib.model.ReaderInfo;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Helps with saving and reading of media tar files in a format that
 * is compatible with the readers.
 */
public class MediaManager {
    private static Logger logger = Logger.getLogger(PackageManager.class);
    private ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private String sMediaPath;
    private String sMediaPackageHash = null;
    private final String sMediaPackageName = "media.tar.gz";
    private final String sPackageDir = "package";
    private final String sExpandedDir = "expanded";
    private final String sSequenceDir = "ledscripts";

    /**
     * Finds existing media packages on the system and initializes
     * instance variables.
     */
    public void refreshPackages()
    {
        rwl.readLock().lock();
        try
        {
        	// Calculate MD5 hash on the existing package.
        	calculateHash();
        }
        finally
        {
        	rwl.readLock().unlock();
        }
    }

    /**
     * Given a stream, saves the media package to the appropriate
     * locations. It also verifies that the tar file can be
     * decompressed and has some basic file structure to it.
     * @param inputStream Stream containing the media file input
     *                    bytes.
     * @return True if saved successfully. False if not.
     */
    public boolean savePackage(InputStream inputStream)
    {
        boolean fValid = false;
        logger.trace("Saving media package.");

        rwl.writeLock().lock();

        OutputStream output = null;
        FileOutputStream fos = null;

        try
        {
            // Create the directory structure.
            createMediaDirectories();

            String tempPackageFilePath = getPackageFilePath() + ".tmp";
            fos = new FileOutputStream(tempPackageFilePath);
            output = new BufferedOutputStream(fos);

            int BUF_SIZE = 8000;
            byte[] buffer = new byte[BUF_SIZE];
            int readBytes;
            do
            {
                readBytes = inputStream.read(buffer, 0, BUF_SIZE);
                if ( readBytes > 0 )
                    output.write(buffer, 0, readBytes);
            } while (readBytes != -1);

            output.flush();

            // Expand the package for directory purposes.
            String tempManifestPath = getExpandedPackagePath() + ".tmp";
            new File(tempManifestPath).mkdirs();
            if ( expandMediaPackage(tempPackageFilePath, tempManifestPath) )
            {
                if ( verifyExpandedPackage(tempManifestPath))
                {
                    fValid = true;
                }
            }

            // Do file cleanup.
            if ( fValid )
            {
                // Remove old original files.
                deleteMediaPackage();

                // Rename temp files and directory.
                new File(tempManifestPath).renameTo(new File(getExpandedPackagePath()));
                new File(tempPackageFilePath).renameTo(new File(getPackageFilePath()));

                // Update the MD5 hash on the new file.
                calculateHash();
            }
            else
            {
                // Remove old original files.
                delete(new File(tempManifestPath));
                delete(new File(tempPackageFilePath));
            }
        }
        catch ( Exception e )
        {
            logger.error("Unable to save media package: " + e.getLocalizedMessage());
        }
        finally
        {
        	rwl.writeLock().unlock();

            if(fos != null) {
                try {
                    fos.close();
                }
                catch(Exception ignore) {
                }
            }

            if(output != null) {
                try {
                    output.close();
                }
                catch(Exception ignore) {
                }
            }
        }

        logger.trace("Finished saving media package.");

        return fValid;
    }

    /**
     * Verifies that there is at least one sequence file contained
     * within the manifest.
     * @param expandedPackagePath Path to where all the media directories
     *                     and files live.
     * @return True if the manifest appears valid. False if not.
     */
    private boolean verifyExpandedPackage(String expandedPackagePath) {
        boolean fValid = false;

        File sequencePath = new File(expandedPackagePath, sSequenceDir);
        String[] sequenceFiles = findSequenceFiles(sequencePath.toString());
        if ( sequenceFiles.length > 0 )
        {
            fValid = true;
        }

        return fValid;
    }

    /**
     * Loads all the file names of sequence files found in
     * the given path.
     * @param path Path to search for sequence files.
     * @return List of file names.
     */
    private String[] findSequenceFiles(String path)
    {
        // Check for a sequence directory and at-least one sequence.
        // Not extremely effective, but all we have at the moment.
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.toLowerCase().endsWith(".csv");
            }
        };

        File dir = new File(path);
        return dir.list(filter);
    }

    /**
     * Gets a list of sequence names (files names without the .csv extension)
     * from the ledscripts directory contained within a media tar.
     * @return
     * List of sequence names without the extension.
     */
    public String[] getSequenceNames()
    {
        List<String> sequenceNames = new ArrayList<String>();

        File sequencePath = new File(getExpandedPackagePath(), sSequenceDir);
        String[] sequenceFiles = findSequenceFiles(sequencePath.toString());
        if ( sequenceFiles != null )
        {
            for ( String sequenceFile : sequenceFiles)
            {
                // Filter out sequence files with invalid names.
                // We try encoding the file. Any invalid characters will
                // cause an expansion of the string, changing their lenghts.
                // We throw those out.
                String encodedFilename = null;
                try
                {
                    encodedFilename = URLEncoder.encode(sequenceFile, "UTF-8");
                    if ( sequenceFile.compareTo(encodedFilename) == 0 )
                    {
                        // Ensure we have an extension and that the full length
                        // of the sequence filename is not too large.
                        int periodIndex = sequenceFile.indexOf(".");
                        if ( periodIndex >= 0 )
                        {
                            String friendlyName = sequenceFile.substring(0, periodIndex);
                            if ( friendlyName.length() < 255 )
                            {
                                sequenceNames.add(friendlyName);
                            }
                        }
                    }
                }
                catch (UnsupportedEncodingException e)
                {
                    logger.error("Invalid encoding used for checking sequence files.");
                }
            }
        }

        return sequenceNames.toArray(new String[sequenceNames.size()]);
    }

    /**
     * Untars a media file to the given path.
     * @param inputFilePath Full pathname to the media file.
     * @param outputPath The output directory to expand the files to.
     * @return True if successfully expanded. False if not.
     */
    private boolean expandMediaPackage(String inputFilePath, String outputPath )
    {
        boolean success = false;
        String cmd = "tar -C ";

        cmd += outputPath;
        cmd += " -zxvf ";
        cmd += inputFilePath;

        BufferedReader stream = null;
        InputStreamReader isr = null;

        try
        {
            Runtime run = Runtime.getRuntime();
            Process pr = run.exec(cmd);
            pr.waitFor();

            // Get the exit code from the unzip process.
            isr = new InputStreamReader( pr.getErrorStream());
            stream = new BufferedReader(isr);
            String error = stream.readLine();
            if ( error != null )
                throw new Exception(error);

            success = true;
        }
        catch (Exception e)
        {
            logger.error("Error untaring media package: " + e.getLocalizedMessage());
        }
        finally {
            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }

            if(stream != null) {
                try {
                    stream.close();
                }
                catch(Exception ignore) {
                }
            }
        }

        return success;
    }

    /**
     * Calculates the hash of the current media file.
     */
    private void calculateHash()
    {
        try
        {
            logger.trace("Calculating MD5 hash of media package.");
            sMediaPackageHash = getMD5Checksum( getPackageFilePath());
        }
        catch (Exception e)
        {
            logger.error("Unable to create MD5 hash of media package: " + e.getLocalizedMessage());
        }
    }

    /**
     * Generates an MD5 checksum of a given file.
     * @param filename Full path of the file to read.
     * @return Hash of the file as bytes.
     * @throws Exception
     */
    private static byte[] createChecksum(String filename) throws Exception {
        InputStream fis = null;

        try {
            fis =  new FileInputStream(filename);
            byte[] buffer = new byte[1024];
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int numRead;

            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    complete.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            return complete.digest();
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }
        }
    }

    /**
     * Calculates an MD5 checksum of the given file and returns it
     * as a hex string.
     * @param filename Filename to read.
     * @return Hexified string of the file's checksum.
     * @throws Exception
     */
    private static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (byte aB : b) {
            result += Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    /**
     * Performs a recursive delete and deletes all subcontents
     * of a directory if it is a directory.
     * @param f Path or file to delete.
     * @throws IOException
     */
    private void delete(File f) throws IOException
    {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    /**
     * Creates all of the sub-directories used by the
     * media manager underneath its specified root directory.
     * @throws Exception
     */
    private void createMediaDirectories() throws Exception {
        File manifestFile = new File(getExpandedPackagePath());
        File packageFile = new File(getPackagePath());

        manifestFile.mkdirs();
        packageFile.mkdirs();
    }

    /**
     * Sets the root directory that the media manager uses.
     * @param sMediaPath Full pathname of the root directory
     *                   to use.
     */
    public void setPath(String sMediaPath) 
    {
    	if (sMediaPath.isEmpty())
    	{
    		logger.error("Invalid argument to setPath");
    		return;
    	}
        logger.debug("Setting media manager directory to " + sMediaPath);
        this.sMediaPath = sMediaPath;
    }

    /**
     * Determines if the current media package is out of sync with the
     * given reader's media package.
     * @param r The reader.
     * @return True if the files are identical. False if not.
     */
    public boolean checkReaderVersion(ReaderInfo r)
    {
    	if (r==null)
    	{
    		logger.error("Invalid argument to checkReaderVersion");
    		return false;
    	}
    	
        boolean fVersionsMatch = true;

        rwl.readLock().lock();
        try
        {
        	// TODO: If we currently don't have a hash, then we probably need to delete
        	// the media package from the reader.
        	if ( sMediaPackageHash == null )
        	{

        	}
        	else
        	{
        		// If the reader doesn't have a valid hash or it's empty, we should update.
        		if ( r.getMediaPackageHash() == null || r.getMediaPackageHash().isEmpty())
            	{
                	fVersionsMatch = false;
            	}

            	if ( r.getMediaPackageHash() != null && !r.getMediaPackageHash().equals(sMediaPackageHash))
            	{
                	fVersionsMatch = false;
            	}
        	}
        }
        finally
        {
        	rwl.readLock().unlock();
        }

        return fVersionsMatch;
    }

    /**
     * Gets a stream that can be used to read the contents
     * of the current media package.
     * @return Input stream that can be read from.
     * @throws FileNotFoundException
     */
    public InputStream readMediaPackage()
            throws FileNotFoundException
    {
        return new FileInputStream(getPackageFilePath());
    }

    /**
     * Deletes an existing media package that is managed
     * by the manager.
     */
    public void deleteMediaPackage()
    {
        try
        {
            delete(new File(getExpandedPackagePath()));
            delete(new File(getPackageFilePath()));
            sMediaPackageHash = null;
        }
        catch (IOException e)
        {
            logger.error("Unable to delete existing media package. " + e.getLocalizedMessage());
        }
    }

    /**
     * Gets a fully formed package File name (path+name) where the fully
     * compressed file exists.
     * @return Fully qualified file name.
     */
    private String getPackageFilePath()
    {
        String sPackageFilePath;
        sPackageFilePath = new File(getPackagePath(), sMediaPackageName).toString();
        return sPackageFilePath;
    }

    /**
     * Gets the fully qualified path to where the decompressed
     * contents of the media package lives.
     * @return Fully qualified path.
     */
    private String getExpandedPackagePath()
    {
        String sExpandedPath;
        sExpandedPath = new File(sMediaPath, sExpandedDir).toString();
        return sExpandedPath;
    }

    /**
     * Gets the fully qualified path to where the compressed
     * media package lives.
     * @return fully qualified path.
     */
    private String getPackagePath()
    {
        String sPackagePath;
        sPackagePath = new File(sMediaPath, sPackageDir).toString();
        return sPackagePath;
    }

    /**
     * Gets the hash of the current media package.
     * @return
     */
    public String getHash()
    {
        return sMediaPackageHash;
    }
}
