CREATE TABLE IF NOT EXISTS `user` (
  `user_id` INT NOT NULL,
  `username` VARCHAR(255) ,
  `password` VARCHAR(255),
  `type` INT NOT NULL,
  PRIMARY KEY `user_id` (`user_Id`)
);# MySQL returned an empty result set (i.e. zero rows).


CREATE TABLE IF NOT EXISTS `test_history` (
  `user_id` INT NOT NULL,
  `score` INT ,
  `date` TIMESTAMP,
  PRIMARY KEY `user_id` (`user_Id`)
 );# MySQL returned an empty result set (i.e. zero rows).


CREATE TABLE IF NOT EXISTS `word`(
    `word` VARCHAR(255) NOT NULL,
    `category` VARCHAR(255) NOT NULL,
    `translation` VARCHAR(255) NOT NULL,
    `gender`INT NOT NULL,
    `total_tests` INT NOT NULL,
    `correct_ans` INT NOT NULL,
    `alternate_sex` VARCHAR(255) NOT NULL,
    `incorrect_ans` INT NOT NULL,
    PRIMARY KEY `word` (`word`)
);# MySQL returned an empty result set (i.e. zero rows).

ALTER TABLE `test_history`
	ADD CONSTRAINT `fk_test_history_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON UPDATE CASCADE
