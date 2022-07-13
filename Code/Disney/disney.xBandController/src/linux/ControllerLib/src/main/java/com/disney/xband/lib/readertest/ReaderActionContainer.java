package com.disney.xband.lib.readertest;

import javax.xml.bind.annotation.XmlTransient;

public class ReaderActionContainer {
	private GenericReaderAction genericAction;
	private SpeechReaderAction speechAction;
	private ScriptReaderAction scriptAction;
	private SequenceReaderAction sequenceAction;

	public GenericReaderAction getGenericAction() {
		return genericAction;
	}

	public void setGenericAction(GenericReaderAction genericAction) {
		this.genericAction = genericAction;
	}

	public SpeechReaderAction getSpeechAction() {
		return speechAction;
	}

	public void setSpeechAction(SpeechReaderAction speechAction) {
		this.speechAction = speechAction;
	}
	
	public ScriptReaderAction getScriptAction() {
		return scriptAction;
	}

	public void setScriptAction(ScriptReaderAction scriptAction) {
		this.scriptAction = scriptAction;
	}
	
	public SequenceReaderAction getSequenceAction() {
		return sequenceAction;
	}

	public void setSequenceAction(SequenceReaderAction sequenceAction) {
		this.sequenceAction = sequenceAction;
	}

	@XmlTransient
	public ReaderAction getAction() {
		if (genericAction != null)
			return genericAction;
		
		if (speechAction != null)
			return speechAction;
		
		if (scriptAction != null)
			return scriptAction;
		
		if (sequenceAction != null)
			return sequenceAction;
		
		return null;
	}
}
