/*
 * Generated by XDoclet - Do not edit!
 */
package com.endlessloopsoftware.egonet.interfaces;

/**
 * Local interface for QuestionLinkEJB.
 */
public interface QuestionLinkEJBLocal
   extends javax.ejb.EJBLocalObject
{

   public void setQuestionLinkDataValue( com.endlessloopsoftware.egonet.data.QuestionLinkDataValue data ) ;

   public com.endlessloopsoftware.egonet.data.QuestionLinkDataValue getQuestionLinkDataValue(  ) ;

   public com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal getQuestion(  ) ;

   public void setQuestion( com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal question ) ;

}