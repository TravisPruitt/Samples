package com.disney.xband.lib.readertest;
import java.io.File;
import java.util.Properties;

import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.utils.FileUtils;

public class ScriptReaderAction extends BaseReaderAction implements
		ReaderAction {

	private String scriptFilePath;
	private String scriptResourcePath;
	private byte[] data;
	private ReaderScript script;
	private Boolean dontWait;
	
	@Override
	public void initialize(Properties prop, ReaderInfo ri) throws Exception {
		super.initialize(prop, ri);
		
		if (scriptFilePath == null && scriptResourcePath == null)
			throw new Exception("Neither scriptFilePath nor scriptResourcePath are specified.");
		
		if (scriptResourcePath != null) {
			data = FileUtils.resourceToArray(scriptResourcePath);
		} else if (scriptFilePath != null) {
			data = FileUtils.fileToArray(new File(scriptFilePath));
		}
		
		script = new ReaderScript();
		script.setData(data);
	}	
	
	@Override
	public String getPath() {
		return "media/ledscript";
	}

	@Override
	public byte[] getData() {		
		return data;
	}

	@Override
	public String getContentType() {
		return "text/plain";
	}
	
	public Long getWaitMs() {
		if (dontWait != null && dontWait == true)
			return 0l;
		
		if (script == null)
			return waitMs;		
		if (waitMs != null)
			return script.getDurationMs() + waitMs;
		return script.getDurationMs();
	}

	public String getScriptFilePath() {
		return scriptFilePath;
	}

	public String getScriptResourcePath() {
		return scriptResourcePath;
	}

	public void setScriptFilePath(String scriptFilePath) {
		this.scriptFilePath = scriptFilePath;
	}

	public void setScriptResourcePath(String scriptResourcePath) {
		this.scriptResourcePath = scriptResourcePath;
	}

	public Boolean getDontWait() {
		return dontWait;
	}

	public void setDontWait(Boolean dontWait) {
		this.dontWait = dontWait;
	}
}
