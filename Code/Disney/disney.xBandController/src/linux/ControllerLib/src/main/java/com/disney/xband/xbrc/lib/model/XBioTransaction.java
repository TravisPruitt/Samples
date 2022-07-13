package com.disney.xband.xbrc.lib.model;

import java.util.Date;

public class XBioTransaction {
	
	private int xBioTransactionId;
	private String xFpeId;
	private String xBandId;
	private Boolean xBio;
	private int transactionType;
	private Boolean byPass;
	private int transactionResultCode;
	private Date transactionStarted;
	private Date entitlements;
	private Date fingerPlaced;
	private int numberOfRetries;
	private int finalBioResultCode;
	private float matchScore;
	private Date xBioTransactionEnded;
	private Date transactionEnded;
	private String transactionData;

	
	
	public XBioTransaction()
	{
		
	}
	
	public XBioTransaction(String xFpeId, String xBandId
			, Boolean xBio, int transactionType, Boolean byPass, int transactionResultCode
			, Date transactionStarted, Date entitlements, Date fingerPlaced, int numberOfRetries
			, int finalBioResultCode, float matchScore, Date xBioTransactionEnded, Date transactionEnded
			, String transactionData)
	{
		this.setxFpeId(xFpeId);
		this.setxBandId(xBandId);
		this.setxBio(xBio);
		this.setTransactionType(transactionType);
		this.setByPass(byPass);
		this.setTransactionResultCode(transactionResultCode);
		this.setTransactionStarted(transactionStarted);
		this.setEntitlements(entitlements);
		this.setFingerPlaced(fingerPlaced);
		this.setNumberOfRetries(numberOfRetries);
		this.setFinalBioResultCode(finalBioResultCode);
		this.setFinalBioResultCode(finalBioResultCode);
		this.setMatchScore(matchScore);
		this.setxBioTransactionEnded(xBioTransactionEnded);
		this.setTransactionEnded(transactionEnded);
		this.setTransactionData(transactionData);
	
	}

	/**
	 * @return the xBioTransactionId
	 */
	public int getxBioTransactionId() {
		return xBioTransactionId;
	}

	/**
	 * @param xBioTransactionId the xBioTransactionId to set
	 */
	public void setxBioTransactionId(int xBioTransactionId) {
		this.xBioTransactionId = xBioTransactionId;
	}

	/**
	 * @return the xFpeId
	 */
	public String getxFpeId() {
		return xFpeId;
	}

	/**
	 * @param xFpeId the xFpeId to set
	 */
	public void setxFpeId(String xFpeId) {
		this.xFpeId = xFpeId;
	}

	/**
	 * @return the xBandId
	 */
	public String getxBandId() {
		return xBandId;
	}

	/**
	 * @param xBandId the xBandId to set
	 */
	public void setxBandId(String xBandId) {
		this.xBandId = xBandId;
	}

	/**
	 * @return the xBio
	 */
	public Boolean getxBio() {
		return xBio;
	}

	/**
	 * @param xBio the xBio to set
	 */
	public void setxBio(Boolean xBio) {
		this.xBio = xBio;
	}

	/**
	 * @return the transactionType
	 */
	public int getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType;
	}

	/**
	 * @return the byPass
	 */
	public Boolean getByPass() {
		return byPass;
	}

	/**
	 * @param byPass the byPass to set
	 */
	public void setByPass(Boolean byPass) {
		this.byPass = byPass;
	}

	/**
	 * @return the transactionResultCode
	 */
	public int getTransactionResultCode() {
		return transactionResultCode;
	}

	/**
	 * @param transactionResultCode the transactionResultCode to set
	 */
	public void setTransactionResultCode(int transactionResultCode) {
		this.transactionResultCode = transactionResultCode;
	}

	/**
	 * @return the transactionStarted
	 */
	public Date getTransactionStarted() {
		return transactionStarted;
	}

	/**
	 * @param transactionStarted the transactionStarted to set
	 */
	public void setTransactionStarted(Date transactionStarted) {
		this.transactionStarted = transactionStarted;
	}

	/**
	 * @return the entitlements
	 */
	public Date getEntitlements() {
		return entitlements;
	}

	/**
	 * @param entitlements the entitlements to set
	 */
	public void setEntitlements(Date entitlements) {
		this.entitlements = entitlements;
	}

	/**
	 * @return the fingerPlaced
	 */
	public Date getFingerPlaced() {
		return fingerPlaced;
	}

	/**
	 * @param fingerPlaced the fingerPlaced to set
	 */
	public void setFingerPlaced(Date fingerPlaced) {
		this.fingerPlaced = fingerPlaced;
	}

	/**
	 * @return the numberOfRetries
	 */
	public int getNumberOfRetries() {
		return numberOfRetries;
	}

	/**
	 * @param numberOfRetries the numberOfRetries to set
	 */
	public void setNumberOfRetries(int numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
	}

	/**
	 * @return the finalBioResultCode
	 */
	public int getFinalBioResultCode() {
		return finalBioResultCode;
	}

	/**
	 * @param finalBioResultCode the finalBioResultCode to set
	 */
	public void setFinalBioResultCode(int finalBioResultCode) {
		this.finalBioResultCode = finalBioResultCode;
	}

	/**
	 * @return the matchScore
	 */
	public float getMatchScore() {
		return matchScore;
	}

	/**
	 * @param matchScore the matchScore to set
	 */
	public void setMatchScore(float matchScore) {
		this.matchScore = matchScore;
	}

	/**
	 * @return the xBioTransactionEnded
	 */
	public Date getxBioTransactionEnded() {
		return xBioTransactionEnded;
	}

	/**
	 * @param xBioTransactionEnded the xBioTransactionEnded to set
	 */
	public void setxBioTransactionEnded(Date xBioTransactionEnded) {
		this.xBioTransactionEnded = xBioTransactionEnded;
	}

	/**
	 * @return the transactionEnded
	 */
	public Date getTransactionEnded() {
		return transactionEnded;
	}

	/**
	 * @param transactionEnded the transactionEnded to set
	 */
	public void setTransactionEnded(Date transactionEnded) {
		this.transactionEnded = transactionEnded;
	}

	/**
	 * @return the transactionData
	 */
	public String getTransactionData() {
		return transactionData;
	}

	/**
	 * @param transactionData the transactionData to set
	 */
	public void setTransactionData(String transactionData) {
		this.transactionData = transactionData;
	}

	
	
	
	
}
