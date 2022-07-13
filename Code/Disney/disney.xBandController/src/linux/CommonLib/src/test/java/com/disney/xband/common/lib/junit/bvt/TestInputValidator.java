package com.disney.xband.common.lib.junit.bvt;

import com.disney.xband.common.lib.security.InputValidator;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/1/12
 * Time: 4:04 PM
 */
public class TestInputValidator {
    @Test
    public void testFileName0() throws IOException
    {
        assertTrue(InputValidator.validateFileName("foo.xml") != null);
    }

    @Test
    public void testFileName1() throws IOException
    {
        assertTrue(InputValidator.validateFileName("aZ09!@#$%^&{}[]()_+-=,.~'`") != null);
    }

    @Test
    public void testFileName2() throws IOException
    {
        try {
            InputValidator.validateFileName("/etc/foo.xml");
            fail();
        }
        catch(Exception ok) {
        }
    }

    @Test
    public void testFileName3() throws IOException
    {
        try {
            InputValidator.validateFileName("../foo.xml");
            fail();
        }
        catch(Exception ok) {
        }
    }

    @Test
    public void testDirName0() throws IOException
    {
        assertTrue(InputValidator.validateDirectoryName("foo") != null);
    }

    @Test
    public void testDirName1() throws IOException
    {
        assertTrue(InputValidator.validateDirectoryName("/etc/foo") != null);
    }

    @Test
    public void testDirName2() throws IOException
    {
        assertTrue(InputValidator.validateDirectoryName("/etc/foo/") != null);
    }

    @Test
    public void testDirName3() throws IOException
    {
        assertTrue(InputValidator.validateDirectoryName("aZ09:/\\!@#$%^&{}[]()_+-=,.~'` ") != null);
    }

    @Test
    public void testGetFileName0() throws IOException
    {
        assertTrue(InputValidator.getFileName("/etc/foo.xml").equals("foo.xml"));
    }

    @Test
    public void testGetFileName1() throws IOException
    {
        assertTrue(InputValidator.getFileName("").equals(""));
    }

    @Test
    public void testGetDirName0() throws IOException
    {
        assertTrue(InputValidator.getDirName("/etc/foo.xml").equals("/etc"));
    }

    @Test
    public void testGetDirName1() throws IOException
    {
        assertTrue(InputValidator.getDirName("").equals(""));
    }

    @Test
    public void testValidateFilePath() throws IOException
    {
        String path = "/etc/nge/config/.keystore";
        assertTrue(InputValidator.validateFilePath(path).equals(path));

        path = "/etc/nge/config/keystore.txt";
        assertTrue(InputValidator.validateFilePath(path).equals(path));

        path = "./keystore.txt";
        assertTrue(InputValidator.validateFilePath(path).equals(path));

        path = ".";
        assertTrue(InputValidator.validateFilePath(path).equals(path));
    }
}
