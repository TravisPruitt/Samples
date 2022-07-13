package com.disney.xband.xbrc.lib.junit.bvt;

import java.util.LinkedList;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import com.disney.xband.common.lib.XmlUtil;
import com.disney.xband.lib.readertest.GenericReaderAction;
import com.disney.xband.lib.readertest.HttpMethod;
import com.disney.xband.lib.readertest.ReaderAction;
import com.disney.xband.lib.readertest.ReaderActionContainer;
import com.disney.xband.lib.readertest.ReaderTest;
import com.disney.xband.lib.readertest.ReaderTestRunner;
import com.disney.xband.lib.readertest.ReaderUnitTest;
import com.disney.xband.lib.readertest.ScriptReaderAction;
import com.disney.xband.lib.readertest.SequenceReaderAction;
import com.disney.xband.lib.readertest.SpeechReaderAction;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.ReaderSequence;

public class TestReaderTestSerialization {
	@Test
	public void testSerialization() throws JAXBException {		
		
		ReaderTest test = new ReaderTest();
		
		test.setName("First Reader Test");
		test.setWaitForAckMs(3000l);
		test.setTests(new LinkedList<ReaderUnitTest>());
		
		ReaderUnitTest testOne = new ReaderUnitTest();
		testOne.setName("First unit test");
		testOne.setActions(new LinkedList<ReaderActionContainer>());
		
		test.getTests().add(testOne);
		
		GenericReaderAction ga = new GenericReaderAction();
		ga.setName("Show color");
		ga.setContentType("text/plain");
		ga.setData("some data");
		ga.setMethod(HttpMethod.PUT);
		ga.setPath("media/color?name=100,100,100");
		ga.setWaitMs(400l);
		
		ReaderActionContainer rac = new ReaderActionContainer();
		rac.setGenericAction(ga);
		testOne.getActions().add(rac);
		
		SpeechReaderAction sa = new SpeechReaderAction();
		sa.setName("Say something");
		sa.setMethod(HttpMethod.PUT);
		sa.setText("I am running a unit test called Test Reader Test Serialization.");
		
		rac = new ReaderActionContainer();
		rac.setSpeechAction(sa);
		testOne.getActions().add(rac);
		
		ScriptReaderAction sra = new ScriptReaderAction();
		sra.setName("Play script");
		sra.setMethod(HttpMethod.PUT);
		sra.setScriptResourcePath("All_Colors_Test_Final.csv");
		
		rac = new ReaderActionContainer();
		rac.setScriptAction(sra);
		testOne.getActions().add(rac);
		
		SequenceReaderAction seq = new SequenceReaderAction();
		seq.setName("Play media sequence");
		seq.setMethod(HttpMethod.PUT);
		seq.setDuration(3000l);
		seq.setSequenceName("GXP_success");
		
		rac = new ReaderActionContainer();
		rac.setSequenceAction(seq);
		testOne.getActions().add(rac);
						
		@SuppressWarnings("unused")
		String xml = XmlUtil.convertToXml(test, ReaderTest.class);
	}
	
	@Test
	public void testDeserialization() throws JAXBException {
		@SuppressWarnings("unused")
		ReaderTest readerTest = ReaderTest.readFromResource("readertest.xml");	
	}
	
	@Test 
	public void testRunningTest() throws JAXBException, InterruptedException {
		ReaderTest readerTest = ReaderTest.readFromResource("readertest.xml");
		
		Properties prop = new Properties();
		prop.put(ReaderAction.PROPKEY_READER, "entry-left");
		prop.put(ReaderAction.PROPKEY_READER_LOCATION, "Entry");
		prop.put(ReaderAction.PROPKEY_VENUE, "Pirates of the Caribbean");
		prop.put(ReaderAction.PROPKEY_LEDSCRIPTS_DIR, "/usr/share/xbrc/www/media/expanded/ledscripts");
		
		prop.put(ReaderAction.PROPKEY_SUCCESS_SEQUENCE, new ReaderSequence("success", 0l));
		prop.put(ReaderAction.PROPKEY_ERROR_SEQUENCE, new ReaderSequence("exception", 0l));
		prop.put(ReaderAction.PROPKEY_FAILURE_SEQUENCE, new ReaderSequence("exception", 0l));
		
		ReaderInfo ri = new ReaderInfo();
		ri.setIpAddress("10.110.1.7");
		ri.setPort(8080);
		ri.setName("entry-left");
		
		ReaderTestRunner runner = new ReaderTestRunner(
				readerTest,
				ri,
				prop,
				new Runnable() { public void run() {System.out.println("Reader test finished successfully");} },
				new Runnable() { public void run() {System.out.println("Reader test finished unsuccessfully");} }				
				);
		
		//runner.start();
		
		//runner.waitForFinish(300000);
	}
}
