<%@ page import="com.endlessloopsoftware.egonet.Shared" %>

<tr>
	<td>
		<logic:present name="answer">
			<bean:define id="answerType" name="answer" property="question.answerType"/>
			<% /* logger.debug(answerType); logger.debug(answer); */ %>
			
			<logic:equal name='answerType' value='<%= Integer.toString(Shared.TEXT) %>'>
				<layout:text 
					key='<%= answer.getQuestionText() %>'
					property='<%= "answers[" + index + "].answerString" %>' 
					size="32" 
					maxlength="32" 
					isRequired="true" 
					styleClass="LABEL" />
			</logic:equal>
			
			<logic:equal name='answerType' value='<%= Integer.toString(Shared.NUMERICAL) %>'>
				<layout:text 
					key='<%= answer.getQuestionText() %>'
					property='<%= "answers[" + index + "].answerString" %>' 
					size="10" 
					maxlength="16" 
					isRequired="true" 
					styleClass="LABEL" />
			</logic:equal>
			
			<logic:equal name='answerType' value='<%= Integer.toString(Shared.CATEGORICAL) %>'>
				<layout:select 
					key='<%= answer.getQuestionText() %>'
					property='<%= "answers[" + index + "].answerString" %>' 
					isRequired="true" 
					styleClass="LABEL" >
					
					<%= answer.writeSelections() %>

				</layout:select>
			</logic:equal>
			
		</logic:present>
		<logic:notPresent name="answer">
			Missing Answer Bean
		</logic:notPresent>
	</td>
</tr>
