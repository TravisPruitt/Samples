package com.disney.xband.lib.readertest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReaderScript {
	private static Pattern lineMatch = Pattern.compile("^([0-9]+\\.[0-9]+),.*");
	private byte[] data;
	private long durationMs = 0l;
	
	public void setData(byte[] data) throws IOException {
		this.data = data;
		calculateDuration();
	}
	
	private void calculateDuration() throws IOException {
		double totDuration = 0.0;
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data)));
		String line = br.readLine();		
		while (line != null) {						
			Matcher m = lineMatch.matcher(line);
			if (m.matches() && m.groupCount() == 1) {
				String lineDuration = line.substring(m.start(1), m.end(1));
				Double dblLineDuration = Double.parseDouble(lineDuration);
				totDuration += dblLineDuration;
			}
			line = br.readLine();
		}
		
		durationMs = (long)(totDuration * 1000.0);
	}

	public long getDurationMs() {
		return durationMs;
	}
}
