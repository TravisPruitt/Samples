package com.disney.xband.lib.readertest;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.LightMsg;
import com.disney.xband.lib.xbrapi.ReaderApi;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.ReaderSequence;
import com.disney.xband.xbrc.lib.utils.FileUtils;

public class SequenceReaderAction extends BaseReaderAction implements
		ReaderAction {
	
	private static Logger logger = Logger.getLogger(SequenceReaderAction.class);

	private Long duration;
	private String sequenceName;
	private Long timeout;
	private ReaderSequence readerSequence;
	private Boolean dontWait;
	
	@Override
	public void initialize(Properties prop, ReaderInfo ri) throws Exception {
		
		super.initialize(prop, ri);
		
		// The off sequence takes no time.
		
		if (sequenceName.equals("off")) {
			duration = 0l;
			readerSequence = new ReaderSequence(sequenceName, timeout);
			return;
		}
		
		if (prop == null)
			return;		
						
		// Now resolve the sequence name
		if (sequenceName.startsWith("${") && sequenceName.endsWith("}")) {
			String key = sequenceName.substring(2, sequenceName.length()-1);
			readerSequence = (ReaderSequence)prop.get(key);
			if (readerSequence == null)
				logger.error("The " + key + " key was not found in the reader test properties.");
		}
		
		if (readerSequence == null)
			readerSequence = new ReaderSequence(sequenceName, timeout);
		
		// Try to calculate sequence duration from the *.csv script with the same name
		// as the sequence name.
				
		String ledscript = (String)prop.get(PROPKEY_LEDSCRIPTS_DIR) + "/" + readerSequence.getName() + ".csv";
		if (!new File(ledscript).canRead()) {
			logger.warn("Script file " + ledscript + " does not exist of cannot be read. Using default duration of " + duration + " milliseconds");
		} else {		
			byte[] data = FileUtils.fileToArray(new File(ledscript));
			ReaderScript script = new ReaderScript();
			script.setData(data);
			duration = script.getDurationMs();
			logger.trace("Calculated script duration for " + ledscript + " is " + duration + " milliseconds");
			// add some processing time
			duration = duration + 100l;
		}
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public byte[] getData() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}
	
	public Long getWaitMs() {
		if (dontWait != null && dontWait == true)
			return 0l;
		
		Long tmpDuration = duration;
		
		// The script will not play longer than the timeout if timeout is set
		if (tmpDuration != null && timeout != null && timeout > 0l && tmpDuration > timeout)
			tmpDuration = timeout;
		
		if (tmpDuration == null)
			tmpDuration = timeout;
		
		if (tmpDuration == null)
			return waitMs;
		
		if (waitMs != null)
			return tmpDuration + waitMs;
		
		return tmpDuration;
	}

	public String getSequenceName() {
		return sequenceName;
	}

	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
	public Boolean getDontWait() {
		return dontWait;
	}

	public void setDontWait(Boolean dontWait) {
		this.dontWait = dontWait;
	}

	public void performAction() throws Exception {
		int retCode = 200;
		
		if (LightMsg.isColorValue(readerSequence.getName())) {
			retCode = ReaderApi.setReaderColor(ri, readerSequence.getName(),
					readerSequence.getTimeout() == null ? 0l : readerSequence.getTimeout());
		} else {
			retCode = ReaderApi.setReaderSequence(ri,
					readerSequence.getName(), readerSequence.getTimeout() == null ? 0l : readerSequence.getTimeout());
		}
		
		if (retCode != 200)
			throw new Exception("Received return code " + retCode + " from reader " + ri.getName());
	}
}
