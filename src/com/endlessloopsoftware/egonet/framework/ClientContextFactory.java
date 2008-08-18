/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.endlessloopsoftware.egonet.framework;

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
			
		    prop.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory" ) ;
		    prop.setProperty("java.naming.provider.url", "jnp://127.0.0.1:1099" ) ;
		    prop.setProperty("java.naming.factory.url.pkgs", "org.jboss.naming:org.jnp.interfaces" ) ;
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
