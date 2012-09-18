<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
	<link type="text/css" rel="stylesheet" href="/static/main.css" />
</head>
<body>
<div id="wrapper">


<c:forEach var="ques" items='${questions}'>
	<p>
		<strong>Question:</strong> 
		<c:out value="${ques.question}"></c:out>
	</p>
	<p>
		Right Answer:
		<c:out value="${ques.rightAnswer.answer}"></c:out>
	</p>
    <p>
    	Wrong Answers:
		<c:forEach var="ans" items='${ques.wrongAnswers}'>
			<br/> <c:out value="${ans.answer}"></c:out> 
		</c:forEach>
	</p>
</c:forEach>

</div>
</body>
</html>
