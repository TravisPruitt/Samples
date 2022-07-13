package com.disney.xband.xbrc.lib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamCleanser extends Thread {
	InputStream is;
	String type;
	
	public StreamCleanser(InputStream is, String type){
		this.is = is;
		this.type = type;
	}
	
	public void run(){
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			String line = null;
			while( (line = br.readLine()) != null)
				System.out.println(type + ">" + line);
			
		} catch (IOException ioe){
			ioe.printStackTrace();
		}
	}
}
