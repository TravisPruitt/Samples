package com.disney.xband.xfpe.xbrapi.action;

import com.disney.xband.xfpe.controller.XfpeController;

public class OptionsAction extends BaseAction
{
	private String test_loop;
	private String image_capture;
	
	@Override
	public String execute() throws Exception {
		try {
			super.execute();
			XfpeController.getInstance().onCommandSetOptions(readerId, image_capture, test_loop);
			return SUCCESS; 
		}
		catch(Exception e) {
			logger.error("Caught exception in OptionsAction.", e);
		}
		return ERROR;
	}

	public String getTest_loop()
	{
		return test_loop;
	}

	public void setTest_loop(String test_loop)
	{
		this.test_loop = test_loop;
	}

	public String getImage_capture()
	{
		return image_capture;
	}

	public void setImage_capture(String image_capture)
	{
		this.image_capture = image_capture;
	}
}
