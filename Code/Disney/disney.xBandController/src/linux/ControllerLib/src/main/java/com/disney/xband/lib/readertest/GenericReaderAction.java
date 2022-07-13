package com.disney.xband.lib.readertest;

public class GenericReaderAction extends BaseReaderAction implements ReaderAction {
	private String path;		
	private String data;
	private String contentType;
	
	@Override
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public byte[] getData() {
		if (data != null)
			return data.getBytes();
		return null;
	}
	
	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String getContentType() {
		return contentType;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}	
}
