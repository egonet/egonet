/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: DispatchForward.java,v 1.4 2004/02/15 14:59:01 admin Exp $
 */
package com.endlessloopsoftware.egonet.web.actions;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
/**
 * Generic dispatcher to ActionForwards.
 * 
 * @author Ted Husted
 * @version $Revision: 1.4 $ $Date: 2004/02/15 14:59:01 $
 */
public final class DispatchForward extends ELSAction
{
	// --------------------------------------------------------- Public Methods
	/**
	 * Forward request to "cancel", {forward}, or "error" mapping, where
	 * {forward} is an action path given in the parameter mapping or in the
	 * request as "forward=actionPath".
	 * 
	 * @param mapping
	 *                The ActionMapping used to select this instance
	 * @param actionForm
	 *                The optional ActionForm bean for this request (if any)
	 * @param request
	 *                The HTTP request we are processing
	 * @param response
	 *                The HTTP response we are creating
	 * 
	 * @exception IOException
	 *                     if an input/output error occurs
	 * @exception ServletException
	 *                     if a servlet exception occurs
	 */
	public ActionForward perform(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException
	{
		// -- isCancelled?
		if (isCancelled(request))
		{
			form.reset(mapping, request);
			return (mapping.findForward("cancel"));
		}
		
		// -- Locals
		ActionForward thisForward	= null;
		String wantForward 				= null;
		
		// -- Check internal parameter for forward
		wantForward = mapping.getParameter();
		
		// -- If not found, check request for forward
		if (wantForward == null) wantForward = request.getParameter("forward");
		
		// -- If found, consult mappings
		if (wantForward != null)
			thisForward = mapping.findForward(wantForward);
		
		// -- If anything not found, dispatch error
		if (thisForward == null)
		{
			thisForward = mapping.findForward("error");
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("action.missing.parameter"));
			saveErrors(request, errors);
		}
		
		return thisForward;
	} // end perform
} // end Action
