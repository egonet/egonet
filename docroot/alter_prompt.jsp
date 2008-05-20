<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"  "http://www.w3.org/TR/html4/loose.dtd">

<%--
 * <p>Title: Egocentric Networks Web Client</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 *  $Id: alter_prompt.jsp,v 1.9 2004/04/14 13:45:18 admin Exp $
 --%>

<%@ page import="java.util.*" %>
<%@ page import="org.jboss.logging.Logger" %>
<%@ page import="com.endlessloopsoftware.egonet.web.WebShared" %>
<%@ page import="com.endlessloopsoftware.egonet.Shared" %>

<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-layout.tld" prefix="layout" %>

<%
		Logger logger = Logger.getLogger(this.getClass().getName());
		logger.debug("enter");
%>

<layout:html locale="true" styleClass="FORM" key='global.title'>

	<html:errors property="org.apache.struts.action.GLOBAL_ERROR"/>

<%--
	<html:img src="/EgoWeb/drawnetwork" alt="Network Graph Placeholder" />
--%>
	
	<layout:form reqCode="submit" action="/alternameSubmit" styleClass="FORM" key="alterprompt.title" focus='nameOne'>
	
		<bean:define id="answer" 			name="alterNameForm" property="answer" 				type="com.endlessloopsoftware.egonet.util.AnswerDataValue"/>
		<bean:define id="position" 			name="alterNameForm" property="position"			type="java.lang.Integer"/>
		<bean:define id="interviewId"		name="alterNameForm" property="interviewId" 		type="java.lang.Long"/>
		<bean:define id="promptType"		name="alterNameForm" property="promptType" 		type="com.endlessloopsoftware.egonet.Shared.AlterPromptType"/>

		<tr>
			<td colspan="2" align="center">

				<%! String mode = WebShared.VIEW_MODE; %>

			</td>
		</tr>
		<tr>
			<td colspan="2">
				<p>&nbsp;</p>
			</td>
		</tr>

	
		<logic:equal name="position" value="-1">
			<tr>
				<td colspan="2">
					<p class="LABEL">
						The following procedure will build your social network as you enter names.<br/>
						The first part of the process is designed to get a general idea of what your network looks like.<br/>
						For this first part please try not to list your very closest family and friends,<br/>
						although we want you to list people you know reasonably well. 
					</p>
				</td>
			</tr>
		</logic:equal>

		<tr>
			<td colspan="2">
				<p class="LABEL"><%= answer.getQuestionText() %></p>
			</td>
		</tr>

	 	<tr><td colspan="2">&nbsp</td></tr>
		
		<layout:text
			key='alterprompt.name.displayname'
			property='nameOne' 
			size="48" 
			maxlength="16" 
			isRequired="<%= Boolean.toString(promptType.equals(Shared.LINK_TO_NONE)) %>"
			styleClass="LABEL" />
		
		<%-- Link Pair --%>
		<logic:equal name="promptType" value="<%= Shared.LINK_PAIR.toString() %>">
			<layout:text
				key='alterprompt.name.secondname'
				property='nameTwo' 
				size="48" 
				maxlength="16" 
				isRequired="false" 
				styleClass="LABEL" />
	 
	 	 	<tr>&nbsp</tr>

			<layout:row>
				<layout:formActions>
					<layout:submit reqCode="submit">
						<layout:message key="alterprompt.button.pairsubmit"/>
					</layout:submit>
					<layout:submit reqCode="complete">
						<layout:message key="alterprompt.button.pair.complete"/>
					</layout:submit>
				</layout:formActions>
			</layout:row>
		</logic:equal>

		<%-- Link Next --%>
		<logic:equal name="promptType" value="<%= Shared.LINK_TO_NEXT.toString() %>">
			<layout:row>
				<layout:formActions>
					<layout:submit reqCode="submit">
						<layout:message key="alterprompt.button.submit"/>
					</layout:submit>
					<layout:submit reqCode="complete">
						<layout:message key="alterprompt.button.next.complete"/>
					</layout:submit>
				</layout:formActions>
			</layout:row>
		</logic:equal>

		<%-- Link Prior --%>
		<logic:equal name="promptType" value="<%= Shared.LINK_TO_PRIOR.toString() %>">
			<layout:row>
				<layout:formActions>
					<layout:submit reqCode="submit">
						<layout:message key="alterprompt.button.submit"/>
					</layout:submit>
					<layout:submit reqCode="complete">
						<layout:message key="alterprompt.button.prior.complete"/>
					</layout:submit>
				</layout:formActions>
			</layout:row>
		</logic:equal>

		<logic:equal name="promptType" value="<%= Shared.LINK_TO_NONE.toString() %>">
			<layout:row>
				<layout:formActions>
					<layout:submit reqCode="submit">
						<layout:message key="alterprompt.button.submit"/>
					</layout:submit>
				</layout:formActions>
			</layout:row>
		</logic:equal>		 
		 
	</layout:form>
	
</layout:html>