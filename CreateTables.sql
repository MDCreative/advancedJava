CREATE TABLE IF NOT EXISTS `user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) ,
  `password` VARCHAR(20),
  `type` INT NOT NULL,
  PRIMARY KEY `user_id` (`user_Id`)
);


CREATE TABLE IF NOT EXISTS `test_history` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `score` INT ,
  `date` TIMESTAMP,
  PRIMARY KEY `user_id` (`user_Id`)
 );


CREATE TABLE IF NOT EXISTS `word`(
    `id` INT NOT NULL AUTO_INCREMENT,
    `word` VARCHAR(30) NOT NULL,
    `category` VARCHAR(20) NOT NULL,
    `translation` VARCHAR(50) NOT NULL,
    `gender`INT NOT NULL,
    `total_tests` INT NOT NULL,
    `correct_ans` INT NOT NULL,
    `alternate_sex` INT NOT NULL,
    `incorrect_ans` INT NOT NULL,
    PRIMARY KEY `id` (`id`)
);

CREATE TABLE IF NOT EXISTS `category`(
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(30) NOT NULL,
    `topic_area` INT NOT NULL
    PRIMARY KEY `id` (`id`)
);

CREATE TABLE IF NOT EXISTS `topic_area`(
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(30) NOT NULL
    PRIMARY KEY `id` (`id`)
);

ALTER TABLE `test_history`
	ADD CONSTRAINT `fk_test_history_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON UPDATE CASCADE
ALTER TABLE `category`
	ADD CONSTRAINT `fk_category_topic_area` FOREIGN KEY (`topic_area`) REFERENCES `topic_area`(`id`) ON UPDATE CASCADE
-- Create foreign key, user_id from user table 
