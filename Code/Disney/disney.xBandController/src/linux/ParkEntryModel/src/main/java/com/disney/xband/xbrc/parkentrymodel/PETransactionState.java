package com.disney.xband.xbrc.parkentrymodel;

import java.util.Date;

import com.disney.xband.xbrc.OmniTicketLib.answer.AnswerCommand;
import com.disney.xband.xbrc.lib.entity.PETransaction;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class PETransactionState {
	private Date startTime;
	private Date omniEntitlementStart;
	private Date omniEntitlementFinish;
	private Long omniEntitlementDuration = 0L;
	private Integer omniEntitlementCount = 0;
	private Date scanStart;
	private Date scanFinish;
	private Long scanDuration = 0L;
	private Integer scanCount = 0;
	private Date omniBioMatchStart;
	private Date omniBioMatchFinish;
	private Long omniBioMatchDuration = 0L;
	private Integer omniBioMatchCount = 0;
	private Date finishTime;
	private Integer scanErrorCount = 0;
	private String scanErrorReasons;
	private Boolean blue = false;
	private Boolean abandoned = false;
	private Long transactionId;
	private ReaderInfo reader;
	private Boolean decremented = false;
	private Boolean newEnrollment = false;
		
	public PETransaction getTransaction(String bandId, AnswerCommand entCmd, AnswerCommand bioCmd ) {
		PETransaction t = new PETransaction();
		t.setBandId(bandId);
		t.setStartTime(startTime);
		t.setOmniEntitlementDuration(omniEntitlementDuration);
		t.setOmniEntitlementCount(omniEntitlementCount);
		t.setScanDuration(scanDuration);
		t.setScanCount(scanCount);
		t.setOmniBioMatchDuration(omniBioMatchDuration);
		t.setOmniBioMatchCount(omniBioMatchCount);
		if (startTime != null && finishTime != null)
			t.setTotalDuration(finishTime.getTime() - startTime.getTime());
		t.setScanErrorCount(scanErrorCount);
		t.setScanErrorReasons(scanErrorReasons);
		t.setBlue(blue);
		t.setAbandoned(abandoned);
		t.setFinishTime(finishTime);
		t.setId(transactionId);
		t.setDecremented(decremented);
		t.setNewEnrollment(newEnrollment);
		
		if (reader != null) {
			t.setReaderName(reader.getName());
			t.setLocationName(reader.getLocation().getName());
		}
		
		if (entCmd != null) {			
			AnswerCommand.Entitlement.MediaInfo.TDSSN tdssn = null;
			
			if (entCmd.getEntitlement() != null &&
				entCmd.getEntitlement().getMediaInfo() != null)
				tdssn = entCmd.getEntitlement().getMediaInfo().getTDSSN();
		
			if (tdssn != null) {
				t.setTdssnDate(tdssn.getDate());
				t.setTdssnSite(tdssn.getSite());
				t.setTdssnStation(tdssn.getStation());
				t.setTdssnTicketId(tdssn.getTicketId());
			}
			
			t.setOmniEntitlementErrorCode(entCmd.getError().getErrorCode().longValue());
			t.setOmniEntitlementErrorDesc(entCmd.getError().getErrorDescription());
		}
		
		if (bioCmd != null) {
			t.setOmniBioMatchErrorCode(bioCmd.getError().getErrorCode().longValue());
			t.setOmniBioMatchErrorDesc(bioCmd.getError().getErrorDescription());
		}
		
		return t;
	}
	
	public Long getTotalScanDuration() {
		if (scanStart != null && scanFinish != null)
			return scanFinish.getTime() - scanStart.getTime();
		return 0L;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getOmniEntitlementStart() {
		return omniEntitlementStart;
	}
	public void setOmniEntitlementStart(Date omniEntitlementStart) {
		this.omniEntitlementStart = omniEntitlementStart;
		this.omniEntitlementCount++;
	}
	public Date getOmniEntitlementFinish() {
		return omniEntitlementFinish;
	}
	public void setOmniEntitlementFinish(Date omniEntitlementFinish) {
		this.omniEntitlementFinish = omniEntitlementFinish;
		if (omniEntitlementStart != null && omniEntitlementFinish != null)
			omniEntitlementDuration += omniEntitlementFinish.getTime() - omniEntitlementStart.getTime();
	}
	public Date getScanStart() {
		return scanStart;
	}
	public void setScanStart(Date scanStart) {
		this.scanStart = scanStart;
		this.scanCount++;
	}
	public Date getScanFinish() {
		return scanFinish;
	}
	public void setScanFinish(Date scanFinish) {
		this.scanFinish = scanFinish;
		if (scanStart != null && scanFinish != null)
			scanDuration += scanFinish.getTime() - scanStart.getTime();
	}
	public Date getOmniBioMatchStart() {
		return omniBioMatchStart;
	}
	public void setOmniBioMatchStart(Date omniBioMatchStart) {
		this.omniBioMatchStart = omniBioMatchStart;
		this.omniBioMatchCount++;
	}
	public Date getOmniBioMatchFinish() {
		return omniBioMatchFinish;
	}
	public void setOmniBioMatchFinish(Date omniBioMatchFinish) {
		this.omniBioMatchFinish = omniBioMatchFinish;
		if (omniBioMatchStart != null && omniBioMatchFinish != null)
			omniBioMatchDuration += omniBioMatchFinish.getTime() - omniBioMatchStart.getTime();
	}
	public Date getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
	}
	public Integer getScanErrorCount() {
		return scanErrorCount;
	}
	public void setScanErrorCount(Integer scanErrorCount) {
		this.scanErrorCount = scanErrorCount;
	}
	public String getScanErrorReasons() {
		return scanErrorReasons;
	}
	public void setScanErrorReasons(String scanErrorReasons) {
		this.scanErrorReasons = scanErrorReasons;
	}
	public Boolean getBlue() {
		return blue;
	}
	public void setBlue(Boolean blue) {
		this.blue = blue;
	}

	public Boolean getAbandoned() {
		return abandoned;
	}

	public void setAbandoned(Boolean abandoned) {
		this.abandoned = abandoned;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Long transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getOmniEntitlementCount() {
		return omniEntitlementCount;
	}

	public Integer getScanCount() {
		return scanCount;
	}

	public Integer getOmniBioMatchCount() {
		return omniBioMatchCount;
	}

	public Long getOmniEntitlementDuration() {
		return omniEntitlementDuration;
	}

	public Long getScanDuration() {
		return scanDuration;
	}

	public Long getOmniBioMatchDuration() {
		return omniBioMatchDuration;
	}

	public ReaderInfo getReader() {
		return reader;
	}

	public void setReader(ReaderInfo reader) {
		this.reader = reader;
	}
	
	public Boolean getDecremented() {
		return decremented;
	}

	public void setDecremented(Boolean decremented) {
		this.decremented = decremented;
	}

	public Boolean getNewEnrollment() {
		return newEnrollment;
	}

	public void setNewEnrollment(Boolean newEnrollment) {
		this.newEnrollment = newEnrollment;
	}
}
