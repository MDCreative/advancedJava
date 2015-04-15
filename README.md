# Technologies
 - Java Server
 - MYSQL DB
 - HTML, CSS, JS Front End

# Data
``` javascript
User{
	user_id: int,
	type: int,
	username: string,
	pass: string
}
/*
User Types{
	student, take tests and view test history
	instructor, adds/delete/modify words
	administrator, adds/delete/modify users
}
*/

TestHistory{
	user_id: int,
	score: int,
	date: timestamp
}

Word{
	word: string,
	category: string,
	translation: string,
	gender: int,  [0 = none, 1 = masculine, 2 = feminine, 3 = neutral, 4 = plural]
	total_tests: int,
	alternate_sex: int,
	correct_ans: int,
	incorrect_ans: int
}
```
# Additional Functionality
 - Multiple Choice Option
 - Breakdown of topic "Mastery" (graphics)
 - Weighted dificulty
