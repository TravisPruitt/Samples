package com.disney.xband.lib.readertest;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xband.common.scheduler.SchedulerMetadataList;

@XmlRootElement(name="ReaderTest")
public class ReaderTest {
	private String name;
	private List<ReaderUnitTest> tests;
	private Long waitForAckMs;
	private ReaderActionContainer passedAction;
	private ReaderActionContainer failedAction;
	private ReaderActionContainer acknowledgeAction;
	
	public String getName() {
		return name;
	}
	
	@XmlElementWrapper(name="tests")
	@XmlElement(name="test")
	public List<ReaderUnitTest> getTests() {
		return tests;
	}
	public Long getWaitForAckMs() {
		return waitForAckMs;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTests(List<ReaderUnitTest> tests) {
		this.tests = tests;
	}
	public void setWaitForAckMs(Long waitForAckMs) {
		this.waitForAckMs = waitForAckMs;
	}
	
	public static ReaderTest readFromResource(String resourcepath) throws JAXBException
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = null;

        try {
           is = loader.getResourceAsStream(resourcepath);
           ReaderTest readerTest = XmlUtil.convertToPojo(is, ReaderTest.class);
           return readerTest;
        }
        finally {
            if(is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
	
	public static ReaderTest readFromXmlFile(String path) throws Exception
	{
		 FileInputStream is = null;

         try {
            is = new FileInputStream(InputValidator.validateFilePath(path));
            ReaderTest readerTest = XmlUtil.convertToPojo(is, ReaderTest.class);
            return readerTest;
         }
         finally {
             if(is != null) {
                 try {
                     is.close();
                 }
                 catch (Exception ignore) {
                 }
             }
         }
     }

	public ReaderActionContainer getPassedAction() {
		return passedAction;
	}

	public ReaderActionContainer getFailedAction() {
		return failedAction;
	}

	public void setPassedAction(ReaderActionContainer passedAction) {
		this.passedAction = passedAction;
	}

	public void setFailedAction(ReaderActionContainer failedAction) {
		this.failedAction = failedAction;
	}

	public ReaderActionContainer getAcknowledgeAction() {
		return acknowledgeAction;
	}

	public void setAcknowledgeAction(ReaderActionContainer acknowledgeAction) {
		this.acknowledgeAction = acknowledgeAction;
	}
}