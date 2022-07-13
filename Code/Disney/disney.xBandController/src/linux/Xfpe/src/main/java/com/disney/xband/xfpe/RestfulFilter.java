package com.disney.xband.xfpe;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RestfulFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub.
	}

	/*
	 * The point of this filter is to grab the HTML body and store it in the string so that it can be
	 * accessed from the actions processing restful requests.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {	
		
		if (request instanceof HttpServletRequest) {
			HttpServletRequest hrequest = (HttpServletRequest)request;
			if (hrequest.getRequestURI().contains("restful")) {
				RestfulRequest rrequest = new RestfulRequest((HttpServletRequest)request);
        		chain.doFilter(rrequest, response);
        		return;
			}
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}
}
