package com.disney.xband.common.scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/*
 * The ShellScriptJob executes a shell script. 
 * Required job parameters: 
 * 	"script-name" - the name of the script or command to run
 * Optional parameters:
 *  All other parameters are passed to the shell script as command line arguments.
 *  Only the value of the parameter is passed, the keys are ignored.
 */
public class ShellScriptJob extends XconnectSchedulerJob {

	public static final String SCRIPT_NAME = "script.name";
	
	private final static int RET_CODE_COMMAND_NOT_FOUND = 127;
	private final static int RET_CODE_COMMAND_NOT_EXECUTABLE = 126;
	
	private String scriptName = "";
	
	@Override
	public void run(XconnectSchedulerJobContext context) throws Exception {
		
		StringBuffer command = new StringBuffer();
		
		SchedulerItemParameter cmd = context.getParameters().get(SCRIPT_NAME);
		if (cmd == null || cmd.getValue() == null || cmd.getValue().isEmpty())
			throw new Exception("The required job parameter " + SCRIPT_NAME + " ws not provided.");
		
		scriptName = cmd.getValue();
		command.append(scriptName);
		
		for (SchedulerItemParameter param : context.getParameters().values()) {
			if (param.getName().equals(SCRIPT_NAME))
				continue;
			command.append(" ");
			escapeArgument(command, param.getValue());
		}
		
		StringBuffer output = new StringBuffer();
		StringBuffer error = new StringBuffer();
		int ret = execShellCmd(command.toString(), output, error, 900);
		
		if (error.length() > 0) {
			output.append("Error: ");
			output.append(error);
		}
		
		switch(ret)
		{
		case 0:
			context.getLog().setSuccess(true);
			context.getLog().setStatusReport("Job finished with 0 return code." + (output.length() > 0 ? " (" + output + ")" : ""));
			break;
		case RET_CODE_COMMAND_NOT_FOUND:
			context.getLog().setSuccess(false);
			context.getLog().setStatusReport("Failed to run script. Command not found: " + cmd.getValue());
			break;
		case RET_CODE_COMMAND_NOT_EXECUTABLE:
			context.getLog().setSuccess(false);
			context.getLog().setStatusReport("Failed to run script. Command " + cmd.getValue() + " cannot be executed." + 
											(output.length() > 0 ? " (" + output + ")" : ""));
			break;		
		default:
			context.getLog().setSuccess(false);
			context.getLog().setStatusReport("Job finished with non zero exit code: " + ret + 
											(output.length() > 0 ? " (" + output + ")" : ""));
		}
	}
	
	private int execShellCmd(String cmd, StringBuffer output, StringBuffer error, int maxOutputLength) throws IOException, InterruptedException 
    {    
        Runtime runtime = Runtime.getRuntime();  
        Process process = runtime.exec(new String[] { "/bin/sh", "-c", cmd });  
        int exitValue = process.waitFor();
        BufferedReader out = null;
        BufferedReader err = null;
        try
        {
        	out = new BufferedReader(new InputStreamReader(process.getInputStream())); 
        	err = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        	
	        String line = "";
	        String errLine = "";
	        while ((line = out.readLine()) != null || (errLine = err.readLine()) != null) {
	        	
	        	if (line != null) {
		        	if (output.length() + 2 + line.length() <= maxOutputLength) {
		        		output.append(line);
		        		output.append("\n");
		        	}
	        	}
	            
	        	if (errLine != null) {
		            if (error.length() + 2 + errLine.length() <= maxOutputLength) {
		            	error.append(errLine);
		            	error.append("\n");
		            }
	        	}
	        }
        }
        finally {
        	if (out != null)
        		out.close();
        	if (err != null)
        		err.close();
        }
        return exitValue;
    }  
	
	/*
	 * We escape all characters passed to the shell because some may be 
	 * interpreted by the shell as a special character such as $ or !.
	 */
	private StringBuffer escapeArgument(StringBuffer sb, String arg) {
		for (int i = 0; i < arg.length(); i++)
		{
			sb.append('\\');
			sb.append(arg.charAt(i));
		}
		return sb;
	}

	@Override
	public void abort(XconnectSchedulerJobContext context) throws Exception {
		throw new Exception("Shell script " + scriptName + " cannot be aborted once started.");
	}
}
