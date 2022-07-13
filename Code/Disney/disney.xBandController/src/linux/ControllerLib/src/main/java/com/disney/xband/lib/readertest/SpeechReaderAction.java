package com.disney.xband.lib.readertest;

import java.util.Properties;

import javax.xml.bind.annotation.XmlTransient;

import com.disney.xband.common.lib.JsonUtil;
import com.disney.xband.lib.xbrapi.TalkPayload;
import com.disney.xband.xbrc.lib.model.ReaderInfo;

public class SpeechReaderAction extends BaseReaderAction implements
		ReaderAction {

	private String text;
	private Integer range;
	private Integer rate;
	private Integer gap;
	
	@XmlTransient
	private byte[] data;

	@Override
	public void initialize(Properties prop, ReaderInfo ri) throws Exception {

		super.initialize(prop, ri);
		
		if (text == null)
			throw new Exception("The text field is not set for this action");	
		
		if (data != null)
			return;
			
		String translatedText;
		
		if (prop == null) {
			translatedText = text;
		} else {
		
			/*
			 * Substitute all keys of the form ${KEY} with the values from the prop
			 * map.
			 */
			
			StringBuffer out = new StringBuffer();
			int start = 0;
			for (int i = 0; i < text.length(); i++) {
				char cur = text.charAt(i);
				if (cur == '$' && (i + 1) < text.length()
						&& text.charAt(i + 1) == '{') {
					if (start != 0) {
						out.append(text.substring(start - 2, i));
					}
					start = i + 2;
				}
	
				if (cur == '}' && start > 0) {
					String key = text.substring(start, i);
					String value = prop.getProperty(key);
					if (value == null)
						value = " ";
					start = 0;
	
					out.append(value);
				} else if (start == 0) {
					out.append(cur);
				}
			}
	
			if (start != 0) {
				out.append(text.substring(start - 2, text.length() - 1));
			}
	
			translatedText = out.toString();
		}
		
		String volume = "100";
		if (prop != null && prop.get(PROPKEY_SPEECH_VOLUME) != null)
			volume = (String) prop.get(PROPKEY_SPEECH_VOLUME);

		TalkPayload payload = new TalkPayload();
		payload.setText(translatedText);
		payload.setVolume(Integer.parseInt(volume));
		if (range != null)
			payload.setRange(range);
		if (rate != null)
			payload.setRate(rate);
		if (gap != null)
			payload.setGap(gap);
	
		data = JsonUtil.convertToJson(payload).getBytes();
	}

	@Override
	public String getPath() {
		return "media/talk";
	}

	@Override
	public byte[] getData() {
		return data;		
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Integer getRange() {
		return range;
	}

	public Integer getRate() {
		return rate;
	}

	public Integer getGap() {
		return gap;
	}

	public void setRange(Integer range) {
		this.range = range;
	}

	public void setRate(Integer rate) {
		this.rate = rate;
	}

	public void setGap(Integer gap) {
		this.gap = gap;
	}
}
