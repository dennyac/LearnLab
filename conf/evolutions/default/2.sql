# --- Adding Sample Data

# --- !Ups

INSERT INTO users VALUES (1,'dennyac@gmail.com','Denny Abraham Cheriyan',NULL,'$2a$10$H8k5DE2U35YDwPK7ySmxVeDVi9mhagfpeeASc/9yVTkQewDLumoGe',NULL,'t','f',0,0,'Beginner',0,'Hi! Denny this side',NULL);
INSERT INTO users VALUES (3,'anil@gmail.com','anil kuncham',NULL,'$2a$10$4xRcGUh38ttLY7d/xG5E3eu15gdVu4wQET9CUkddLdb392UXQ2X3G',NULL,'t','t',0,0,'Beginner',0,'Hello! My name is Anil. Nice to meet all of you!',NULL);
INSERT INTO users VALUES (4,'supriya@gmail.com','supriya',NULL,'$2a$10$27Yv35U9WfNEo9zJlnypkO74MFXmMGYcGuQfKEe0QBRpx6462VPMa',NULL,'t','f',0,0,'Beginner',0,NULL,NULL);
INSERT INTO users VALUES (5,'dhanya@gmail.com','Dhanyatha',NULL,'$2a$10$Sxbz4ji.tpdZubgUobNJcuQmQVMys/..R5qryyTQsdYZkpgEm.7Lu',NULL,'t','f',0,0,'Beginner',0,NULL,NULL);
INSERT INTO users VALUES (6,'shruthi@gmail.com','Shruthi',NULL,'$2a$10$V2J0vxfeG3JvaWiz4PY43ehLJ7bGnYveuK91wNE.55focLl1bX2JW',NULL,'t','f',0,0,'Beginner',0,NULL,NULL);

INSERT INTO event VALUES (1,3,'DBNormalization','2014-11-11 22:38:53','The event contains 3 stages. Stage 1: Answer the question that is being displayed. Stage 2: Collaborate with you event-mates and brainstorm about the answer options.Use Hashtags to anything that might be relevant. Stage 3: Having discussed, conclude the right answer and submit it. Give a short justification as to why you pick the answer. Also let us know who helped you better to arrive at the answer','This is the Phase 1 script','This is the Phase 2 script','This is the Phase 3 script','t',NULL);
INSERT INTO event VALUES (2,3,'Aggregate Functions','2014-11-11 22:38:53','The event contains 3 stages. Stage 1: Answer the question that is being displayed. Stage 2: Collaborate with you event-mates and brainstorm about the answer options.Use Hashtags to anything that might be relevant. Stage 3: Having discussed, conclude the right answer and submit it. Give a short justification as to why you pick the answer. Also let us know who helped you better to arrive at the answer','This is the Phase 1 script','This is the Phase 2 script','This is the Phase 3 script','t',NULL);
INSERT INTO question VALUES (1,'What keyword will you use to sort the results of a query?','sort by','order by','arrange by','reorder','Option2');
INSERT INTO question VALUES (2,'What keyword will you use to sort the results in descending order?','high_to_low','low_to_high','DESC','ASC','Option3');
INSERT INTO question VALUES (3,'What function will you use to get the average of a specific column?','COUNT()','AVG()','MIN()','MAX()','Option2');
INSERT INTO question VALUES (4,'What function will you use to get the sum of a specific column?','SUM()','BAR()','COUNT()','FOO()','Option1');


INSERT INTO event_question VALUES (1,1);
INSERT INTO event_question VALUES (1,2);

INSERT INTO event_question VALUES (2,3);
INSERT INTO event_question VALUES (2,4);
INSERT INTO event_users VALUES (1,1);
INSERT INTO event_users VALUES (1,4);

INSERT INTO event_users VALUES (2,5);
INSERT INTO event_users VALUES (2,6);



# --- !Downs

DELETE FROM event_users;
DELETE FROM event_question;
DELETE FROM question;
DELETE FROM event;
DELETE FROM users;


