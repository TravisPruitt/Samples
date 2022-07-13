/*
 *  Copyright 2011 - Giovanni Tosto
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.softwareforge.struts2.breadcrumb.tag;

import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.IteratorComponent;
import org.apache.struts2.views.jsp.ComponentTagSupport;
import org.softwareforge.struts2.breadcrumb.BreadCrumbInterceptor;
import org.softwareforge.struts2.breadcrumb.BreadCrumbTrail;
import org.softwareforge.struts2.breadcrumb.Crumb;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @author Giovanni Tosto
 * @version $Id: BreadCrumbTag.java 293 2011-06-14 22:10:50Z giovanni.tosto $
 */
public class BreadCrumbTag extends ComponentTagSupport {

	private static final long serialVersionUID = 1L;

	// var attribute
	private String var;

	public void setVar(String var) {
		this.var = var;
	}

	public void setId(String id) {
		setVar(id);
	}

	// Status attribute
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		IteratorComponent ic = new IteratorComponent(stack);
		return ic;
	}

	@Override
	protected void populateParams() {
		super.populateParams();

		IteratorComponent c = (IteratorComponent) getComponent();
		c.setId(var);
		c.setValue("#session['" + BreadCrumbInterceptor.CRUMB_KEY + "'].crumbs");
		c.setStatus(status);
		
		// Add the home breadcrumb if present in the jstl tag.
		if (BreadCrumbInterceptor.getHome() != null) {
			BreadCrumbTrail bcTrail = (BreadCrumbTrail)pageContext.getSession().getAttribute(BreadCrumbInterceptor.CRUMB_KEY);
			if (bcTrail == null)
				return;
			
			synchronized(bcTrail.getCrumbs()) {
				Stack<Crumb> crumbs = bcTrail.getCrumbs();
				if (crumbs.isEmpty())
					return;
				
				// Add home if it is not there
				if (!crumbs.firstElement().getName().equalsIgnoreCase(BreadCrumbInterceptor.getHome())) {
					Crumb crumb = new Crumb();
					crumb.setName(BreadCrumbInterceptor.getHome());
					crumb.setAction(BreadCrumbInterceptor.getHomeAction());
					crumb.setNamespace(BreadCrumbInterceptor.getHomeNamespace());				
					crumbs.insertElementAt(crumb, 0);
					BreadCrumbInterceptor.setClassNames(crumbs);
				}					
			}
		}
	}

	public int doEndTag() throws JspException {
		component = null;
		return EVAL_PAGE;
	}

	public int doAfterBody() throws JspException {
		boolean again = false;
		BreadCrumbTrail bcTrail = (BreadCrumbTrail)pageContext.getSession().getAttribute(BreadCrumbInterceptor.CRUMB_KEY);
		if (bcTrail != null) {
			synchronized(bcTrail) {
				again = component.end(pageContext.getOut(), getBody());
			}
		}
		else {
			again = component.end(pageContext.getOut(), getBody());
		}

		if (again) {
			return EVAL_BODY_AGAIN;
		} else {
			if (bodyContent != null) {
				try {
					bodyContent.writeOut(bodyContent.getEnclosingWriter());
				} catch (Exception e) {
					throw new JspException(e.getMessage());
				}
			}
			return SKIP_BODY;
		}
	}

}
