<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
	<link type="text/css" rel="stylesheet" href="/static/main.css" />
</head>
<body>
<div id="wrapper">

<strong>
	${question.question}
</strong>
<form>
	<c:forEach var="ans" items='${question.wrongAnswers}'>
		<input type="radio" name="answer" value="" /><c:out value="${ans.answer}"></c:out><br/> 
	</c:forEach>
	<input type="submit" />
</form>


</div>
</body>
</html>
