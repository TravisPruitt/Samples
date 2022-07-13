package com.disney.xband.xfpe.simulate;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.disney.xband.xfpe.XfpeProperties;

public class SimulatorManager {
	
	private static Logger logger = Logger.getLogger(SimulatorManager.class);
			
	public static String importCsvFile(Long suiteId, String suiteName,
			String csvfilepath) throws Exception {

		Connection conn = XfpeProperties.getInstance().getConn();
		int count = 0;

        FileInputStream fstream = null;
        DataInputStream in = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

		try {
			fstream = new FileInputStream(csvfilepath);
			in = new DataInputStream(fstream);
            isr = new InputStreamReader(in);
			br = new BufferedReader(isr);
			String strLine;

			conn.setAutoCommit(false);

			PEGuestTestSuite suite = new PEGuestTestSuite(null, suiteName);
			DB.insertTestSuite(suite);

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				int sepPos = strLine.indexOf(',');
				if (sepPos <= 0) {
					logger.error("No comma separator found on line " + count
							+ 1 + ". Ignoring line.");
					continue;
				}
				String bandId = strLine.substring(0, sepPos);
				String template = strLine.length() == sepPos ? "" : strLine
						.substring(sepPos + 1);

				bandId.trim();
				template.trim();

				if (bandId.isEmpty()) {
					logger.error("No bandId found on line " + count + 1
							+ ". Ignoring line.");
					continue;
				}

				PEGuestTest test = new PEGuestTest(null, // id
						suite.getId(), // suiteId,
						1, // seq,
						"RFID?" + bandId, // name,
						bandId, template.isEmpty(), // child
						true, // validBand,
						"", // reason,
						template, // bioTemplate,
						1, // omniLevel,
						PEGuestTestResult.ENTERED, "Test " + count + 1
								+ " for bandId " + bandId); // desc
				DB.insertTest(test);
				
				// Add the TAP action
				PEGuestAction action1 = new PEGuestAction(
						null, 				// id
						test.getId(), 		// guestId
						1, 					// seq
						"Guest taps",		// desc
						PEGuestActionType.TAP,	// type
						1, 					// delay sec
						false, 				// fireAfterEvent,
						"");				// data
				DB.insertGuestAction(action1);
				
				if (!template.isEmpty()) {
					// Add scan if template is present
					PEGuestAction action2 = new PEGuestAction(
							null, 				// id
							test.getId(), 		// guestId
							2, 					// seq
							"Guest scans",		// desc
							PEGuestActionType.SCAN,	// type
							0, 					// delay sec
							true, 				// fireAfterEvent,
							template);
					DB.insertGuestAction(action2);
				}
				
				// In park entry model there will be a blue or green light.
				if (DB.isParkentryModel()) {
					// Wait for the result of TAP/SCAN
					PEGuestAction action2 = new PEGuestAction(
							null, 				// id
							test.getId(), 		// guestId
							3, 					// seq
							"Guest waits",		// desc
							PEGuestActionType.WAIT,	// type
							0, 					// delay sec
							true, 				// fireAfterEvent,
							"");
					DB.insertGuestAction(action2);
				}
				
				count++;
			}
			conn.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			conn.setAutoCommit(true);

            if(fstream != null) {
                try {
                    fstream.close();
                }
                catch(Exception ignore) {
                }
            }

            if(in != null) {
                try {
                    in.close();
                }
                catch(Exception ignore) {
                }
            }

            if(isr != null) {
                try {
                    isr.close();
                }
                catch(Exception ignore) {
                }
            }

            if(br != null) {
                try {
                    br.close();
                }
                catch(Exception ignore) {
                }
            }
		}

		return "Imported " + count + " tests";
	}
	
	
	public static String deleteSuite(Long suiteId) throws Exception {
		DB.deleteTestSuite(suiteId);
		return "The test suite has been deleted";
	}
}
