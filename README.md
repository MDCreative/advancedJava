# Technologies
 - Java Server
 - MYSQL DB
 - HTML, CSS, JS Front End

# Data
``` javascript
user{
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

test_history{
	user_id: int,
	score: int,
	date: timestamp
}

word{
	id: int,
	word: string,
	category: int,
	translation: string,
	gender: int,  [0 = none, 1 = masculine, 2 = feminine, 3 = neutral, 4 = m/f, 5 = plural]
	total_tests: int,
	alternate_sex: int,
	correct_ans: int,
	incorrect_ans: int
}

category{
	id: int,
	name: string,
	topic_area: int
}

topic_area{
	id: int,
	name: string
}
```
# Additional Functionality
 - Multiple Choice Option
 - Breakdown of topic "Mastery" (graphics)
 - Weighted dificulty
