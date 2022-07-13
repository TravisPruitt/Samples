package com.disney.xband.xbrc.parkentrymodel.scheduler;

import java.io.File;

import org.quartz.DisallowConcurrentExecution;

import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.ShellScriptJob;
import com.disney.xband.common.scheduler.XconnectSchedulerJobContext;
import com.disney.xband.xbrc.lib.model.XBRCController;

@DisallowConcurrentExecution
public class DatabaseDataExport extends ShellScriptJob {
	
	public void run(XconnectSchedulerJobContext context) throws Exception {
		
		// For security reasons, all scripts must live in /usr/share/xbrc/scripts directory
		// We only use the script name from the SCRIPT_NAME parameter ignoring the path if any.
		
		SchedulerItemParameter cmd = context.getParameters().get(SCRIPT_NAME);
		if (cmd == null || cmd.getValue() == null || cmd.getValue().isEmpty())
			throw new Exception("The required job parameter " + SCRIPT_NAME + " ws not provided.");
		
		File file = new File(cmd.getValue());
		file = new File(XBRCController.getInstance().getScriptsDirectory() +"/" + file.getName());
		
		if (!file.exists())
			throw new Exception("The script to execute " + file.getAbsolutePath() + " does not exist.");
		
		cmd.setValue(file.getAbsolutePath());
		
		super.run(context);
	}
}
