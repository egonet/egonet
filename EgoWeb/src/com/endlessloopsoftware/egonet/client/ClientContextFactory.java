/*****************************************************
 * This returns an intial context with correct properties to be use
 * external to the application server
 *
 *  @author     $Author: admin $
 *  @created    Dec 18, 2003
 *  @version    $Id: ClientContextFactory.java,v 1.2 2004/01/03 15:44:20 admin Exp $
 */
package com.endlessloopsoftware.egonet.client;

import java.util.Properties;

import javax.naming.NamingException;


public class ClientContextFactory
{
	private static Properties       prop    = null;

  /**
	* This routine returns the Correct intial context for a client jndi lookup
	* Lazy loads, then caches props
	* May only be called from outside App Server
	*
	* @return      InitialContext
	* @throws      NamingException
	*/
	public static Properties get() throws NamingException
	{
		if (prop == null)
		{
			prop = new Properties();
			// prop.setProperty("java.naming.factory.url.pkgs",        "org.jboss.naming:org.jnp.interfaces");
			prop.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			prop.setProperty("java.naming.provider.url", "localhost:1099");
		}

		return prop;
	}
}


/**
 * Change Log
 *
 * $Id: ClientContextFactory.java,v 1.2 2004/01/03 15:44:20 admin Exp $
 * $Log: ClientContextFactory.java,v $
 * Revision 1.2  2004/01/03 15:44:20  admin
 * Adding new EJB files.
 * More relationships and use of value objects.
 *
 * Revision 1.1.1.1  2003/12/18 21:17:18  admin
 * Imported sources
 *
 *
 */
