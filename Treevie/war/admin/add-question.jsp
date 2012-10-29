<!DOCTYPE html>
<html>

<head>
	<link type="text/css" rel="stylesheet" href="/static/main.css" />
</head>

<body>
<div id="wrapper">

<form method="post" action="/add-question">
<p>Question:</p>
<textarea name="question">${editque.question}</textarea>
<p>Wrong Answers:</p>
<input type="text" name="wrong-1" value="${editque.wrongAnswers[0].answer}" />
<input type="text" name="wrong-2" value="${editque.wrongAnswers[1].answer}" />
<input type="text" name="wrong-3" value="${editque.wrongAnswers[2].answer}" />
<p>Right Answer:</p>
<input type="text" name="right" value="${editque.rightAnswer.answer}" />
<p>Which Series:</p>
<input type="text" name="series" value="${editque.series}" />
<p>Level:</p>
<select name="level">
	<option value="1" ${editque.level == '1' ? 'selected' : ''}>Beginner</option>
	<option value="2" ${editque.level == '2' ? 'selected' : ''}>Medium</option>
	<option value="3" ${editque.level == '3' ? 'selected' : ''}>Advanced</option>
</select>
<p>
<input type="hidden" value="${param.key}" name="key" />
<input type="submit" />
</p>
</form>

</div>
</body>	
</html>