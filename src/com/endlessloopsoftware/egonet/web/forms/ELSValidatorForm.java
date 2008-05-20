package com.endlessloopsoftware.egonet.web.forms;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: ELSValidatorForm.java,v 1.3 2004/02/15 14:59:02 admin Exp $
 */

import org.apache.struts.validator.ValidatorActionForm;
import org.jboss.logging.Logger;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class ELSValidatorForm extends ValidatorActionForm
{
	Logger logger = Logger.getLogger(this.getClass().getName());
	
}

/**
 * $Log: ELSValidatorForm.java,v $
 * Revision 1.3  2004/02/15 14:59:02  admin
 * Fixing Header Tags
 *
 * Revision 1.2  2004/02/10 16:31:24  admin
 * Alter Prompt Completed
 *
 * Revision 1.1  2004/02/07 04:33:26  admin
 * First egoquestions page
 *
 */
