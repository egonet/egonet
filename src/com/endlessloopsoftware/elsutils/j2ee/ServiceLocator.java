/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN
 * OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
 * FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF
 * LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of
 * any nuclear facility.
 */
package com.endlessloopsoftware.elsutils.j2ee;

import javax.naming.InitialContext;
import javax.naming.NamingException;


import java.net.URL;
import java.util.*;

import javax.ejb.*;
import javax.jms.QueueConnectionFactory;
import javax.jms.Queue;
import javax.jms.TopicConnectionFactory;
import javax.jms.Topic;
import javax.rmi.PortableRemoteObject;
import javax.sql.DataSource;

/**
 *  This class is an implementation of the Service Locator pattern. It is
 *  used to looukup resources such as EJBHomes, JMS Destinations, etc.
 */
public class ServiceLocator
{
   private InitialContext ic;

   public ServiceLocator()
         throws EJBException
   {
      try
      {
         ic = new InitialContext();
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
   }

   public ServiceLocator(Properties connectionProperties)
         throws EJBException
   {
      try
      {
         ic = new InitialContext(connectionProperties);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
   }


   /**
    * will get the ejb Local home factory.
    * clients need to cast to the type of EJBHome they desire
    *
    * @return the Local EJB Home corresponding to the homeName
    */
   public EJBLocalHome getLocalHome(String jndiHomeName)
         throws EJBException
   {
      EJBLocalHome home;
      try
      {
         home = (EJBLocalHome) ic.lookup(jndiHomeName);
      }
      catch (Exception ne)
      {
         throw new EJBException(ne);
      }


      return home;
   }

   /**
    * will get the ejb Local home factory.
    * Clients need to cast to the type of EJBHome they desire.
    * Requires that that the homeclass contain a field called JNDI_NAME
    * containing the, wait for it..., JNDI name.
    *
    * @return the Local EJB Home corresponding to the homeName
    */
   public EJBLocalHome getLocalHome(Class homeClass)
         throws EJBException
   {
      EJBLocalHome home;
      try
      {
         String name = (String) homeClass.getDeclaredField("JNDI_NAME").get(null);
         home = (EJBLocalHome) ic.lookup(name);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return home;
   }

   /**
    * will get the ejb Remote home factory.
    * clients need to cast to the type of EJBHome they desire
    *
    * @return the EJB Home corresponding to the homeName
    */
   public EJBHome getRemoteHome(String jndiHomeName, Class className)
         throws EJBException
   {
      EJBHome home;
      try
      {
         Object objref = ic.lookup(jndiHomeName);
         Object obj = PortableRemoteObject.narrow(objref, className);
         home = (EJBHome) obj;
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return home;
   }

   /**
    * will get the ejb Remote home factory.
    * clients need to cast to the type of EJBHome they desire.
    * Requires that that the homeclass contain a field called JNDI_NAME
    * containing the, wait for it..., JNDI name.
    *
    * @return the EJB Home corresponding to the homeName
    */
   public EJBHome getRemoteHome(Class homeClass)
         throws EJBException
   {
      EJBHome home;
      try
      {
         String jndiHomeName = null;
         try
         {
            jndiHomeName = (String) homeClass.getDeclaredField("JNDI_NAME").get(null);
         }
         catch (NoSuchFieldException e)
         {
            throw new EJBException("The Home Interface Class must contain a field named JNDI_NAME");
         }

         Object objref = ic.lookup(jndiHomeName);
         Object obj = PortableRemoteObject.narrow(objref, homeClass);
         home = (EJBHome) obj;
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return home;
   }

   /**
    * @return the factory for the factory to get queue connections from
    */
   public QueueConnectionFactory getQueueConnectionFactory(String qConnFactoryName)
         throws EJBException
   {
      QueueConnectionFactory factory;
      try
      {
         factory = (QueueConnectionFactory) ic.lookup(qConnFactoryName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return factory;
   }


   /**
    * @return the Queue Destination to send messages to
    */
   public Queue getQueue(String queueName)
         throws EJBException
   {
      Queue queue;
      try
      {
         queue = (Queue) ic.lookup(queueName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return queue;
   }

   /**
    * This method helps in obtaining the topic factory
    * @return the factory for the factory to get topic connections from
    */
   public TopicConnectionFactory getTopicConnectionFactory(String topicConnFactoryName)
         throws EJBException
   {
      TopicConnectionFactory factory;
      try
      {
         factory = (TopicConnectionFactory) ic.lookup(topicConnFactoryName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return factory;
   }

   /**
    * This method obtains the topc itself for a caller
    * @return the Topic Destination to send messages to
    */
   public Topic getTopic(String topicName)
         throws EJBException
   {
      Topic topic;
      try
      {
         topic = (Topic) ic.lookup(topicName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return topic;
   }

   /**
    * This method obtains the datasource itself for a caller
    * @return the DataSource corresponding to the name parameter
    */
   public DataSource getDataSource(String dataSourceName)
         throws EJBException
   {
      DataSource dataSource;
      try
      {
         dataSource = (DataSource) ic.lookup(dataSourceName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return dataSource;
   }

   /**
    * @return the URL value corresponding
    * to the env entry name.
    */
   public URL getUrl(String envName)
         throws EJBException
   {
      URL url;
      try
      {
         url = (URL) ic.lookup(envName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }

      return url;
   }

   /**
    * @return the boolean value corresponding
    * to the env entry such as SEND_CONFIRMATION_MAIL property.
    */
   public boolean getBoolean(String envName)
         throws EJBException
   {
      Boolean bool;
      try
      {
         bool = (Boolean) ic.lookup(envName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return bool.booleanValue();
   }

   /**
    * @return the String value corresponding
    * to the env entry name.
    */
   public String getString(String envName)
         throws EJBException
   {
      String envEntry;
      try
      {
         envEntry = (String) ic.lookup(envName);
      }
      catch (NamingException ne)
      {
         throw new EJBException(ne);
      }
      catch (Exception e)
      {
         throw new EJBException(e);
      }
      return envEntry;
   }

}