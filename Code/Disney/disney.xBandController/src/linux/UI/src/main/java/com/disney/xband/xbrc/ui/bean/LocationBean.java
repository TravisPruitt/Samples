package com.disney.xband.xbrc.ui.bean;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class LocationBean {

	private Long id;
	private Map<String, ReaderValue> readers;
	private Double avgSignalStrength;
	
	private static Logger logger = Logger.getLogger(LocationBean.class);
	
	public LocationBean(Long id) throws IllegalArgumentException {
		if (id == null)
			throw new IllegalArgumentException("Location ID is required.");
		
		this.id = id;
		readers = new LinkedHashMap<String, ReaderValue>();
		avgSignalStrength = 0.0;
	}
	
	public Long getId(){
		return id;
	}
	
	public Double getAvgSignalStrength() {
		DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
		return Double.valueOf(oneDecimalPlace.format(avgSignalStrength));
	}

	public void updateAvgSignalStrength() {
		
		double signalStrenghtSum = 0.0;
		for (ReaderValue r: readers.values()){
			//calculate average amplified channels' signal strength
			r.updateAmpifiedSignalStrength();
			
			signalStrenghtSum += r.getAvgSignalStrength();
		}
				
		if (readers.values().size() > 0)
			avgSignalStrength = signalStrenghtSum / readers.values().size();
	}
	
	public Double getStrongestReader() {
		double strongestSignal = 0.0;
		for (ReaderValue r : readers.values()) {
			if (strongestSignal < r.getAvgSignalStrength())
				strongestSignal = r.getAvgSignalStrength();
		}
		
		DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
		return Double.valueOf(oneDecimalPlace.format(strongestSignal));
	}
	
	public Double getReadersSSAverage(){
		double avg = 0.0;
		
		double ssSum = 0.0;
		int numReadersIncluded = 0;
		
		logger.debug("Calculating location id " + id + " signal strength average for values:");
		
		for (ReaderValue r : readers.values()){
			
			logger.debug(r.getReaderId() + ": " + r.getAvgSignalStrength());
			
			if (r.getAvgSignalStrength() != 0){
				ssSum += r.getAvgSignalStrength();
				numReadersIncluded++;
			}
		}
		
		if (numReadersIncluded > 0)
			avg = ssSum / numReadersIncluded;
		else
			avg = 0.0;
		
		DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
		
		double formattedAvg = Double.valueOf(oneDecimalPlace.format(avg));
		
		logger.debug("Location's id " + id + " signal strength avg = " + formattedAvg);
		
		return formattedAvg;
	}
	
	public Double getReadersSSMax(){
		Double max = null;
		
		logger.debug("Calculating location id " + id + " signal strength max for values:");
		
		for (ReaderValue r : readers.values()){						
			if (r.getMaxSignalStrength() != 0){
				if (max == null)
					max = r.getMaxSignalStrength();
				else
					max = Math.max(max, r.getMaxSignalStrength()); 								
			}
		}
			
		DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
		
		double formattedMax = Double.valueOf(oneDecimalPlace.format(max));
		
		logger.debug("Location's id " + id + " signal strength max = " + formattedMax);
		
		return formattedMax;
	}
	
	public Double getStrongestFormattedReader() {
		double strongestSignal = 0.0;
		for (ReaderValue r : readers.values()) {
			if (strongestSignal < r.getFormattedMaxSignalStrength())
				strongestSignal = r.getFormattedMaxSignalStrength();
		}
		
		DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
		return Double.valueOf(oneDecimalPlace.format(strongestSignal));
	}
	
	public Double getPositiveReadersSSAverage(){
		double avg = 0.0;
		
		double ssSum = 0.0;
		int numReadersIncluded = 0;
		
		logger.debug("Calculating location id " + id + " signal strength average for values:");
		
		for (ReaderValue r : readers.values()){
			
			logger.debug(r.getReaderId() + ": " + r.getFormattedSignalStrength());
			
			if (r.getFormattedSignalStrength() != 0){
				ssSum += r.getFormattedSignalStrength();
				numReadersIncluded++;
			}
		}
		
		if (numReadersIncluded > 0)
			avg = ssSum / numReadersIncluded;
		else
			avg = 0.0;
		
		DecimalFormat oneDecimalPlace = new DecimalFormat("#.#");
		
		double formattedAvg = Double.valueOf(oneDecimalPlace.format(avg));
		
		logger.debug("Location's id " + id + " signal strength avg = " + formattedAvg);
		
		return formattedAvg;
	}
	
	public void addReader(ReaderValue reader){
		if (reader == null)
			return;
		
		readers.put(reader.getReaderId(), reader);
	}
	
	public ReaderValue getReader(String readerId){
		return readers.get(readerId);
	}
	
	public Collection<ReaderValue> getReaders(){
		return readers.values();
	}
}
