# LearnLab 

LearnLab is an online learning platform where students can take timed assessments, learn and collaborate as a group. Instructors can monitor student/group performance in real-time and provide feedback.

####Application Link - http://learnlab.herokuapp.com/

##Installation

###Prerequisites
* Redis 2.8.17		- http://redis.io/
* Play Framework 2.3 	- https://www.playframework.com/
* MySQL/PostgreSQL (Optional)

###Steps to run locally
* Clone the repository 
```sh
$ git clone https://github.com/dennyac/LearnLab.git
$ cd LearnLab
```
* Make necessary changes in conf/application.conf for the database connection and Redis url. Enable the in-memory database connection, if you do not wish to choose MySQL/PostgreSQL.
* Ensure Redis server is running
* Issue the command
```sh
$ activator run
```
* Open the application in a browser - http://localhost:9000

##Deployment Instructions

###Prerequisites
* Heroku account
* Heroku Toolbelt

###Steps to Deploy on Heroku
Navigate to the directory that contains the git repository, and run the following commands
* To create the application
```sh
$ heroku create
```
* To add Redis addon for Publish/Subscribe messaging which is required for chat
```sh
$ heroku addons:add rediscloud
```
* To deploy the application
```sh
$ git push heroku master
```
* To open the application in a browser
```sh
$ heroku open
```

###Optional: 
* To add more dynos for scalability
```sh
$ heroku scale web=2
```
* To rename the application
```sh
$ heroku apps:rename newname
```

##Special Thanks
We have used the following reference applications as a starting point.
* PlayStartApp - https://github.com/yesnault/PlayStartApp
* Scalable chat application with Play and WebSockets - https://github.com/heroku-examples/play-websockets-chat-sample

##Known Bugs/Issues
* To view the entire live feeds of an event the instructor will have to log in and click the live feeds page before the event starts. If the instructor joins midway all pinned posts from event start time till the time that the instructor has viewed the page will not be rendered.
* During the event, after the phase duration elapses, it will automatically move on to the next phase. Presently we have bypassed this validation, allowing users to move to the next phase before the duration elapses so that prospective users can get a sense of the application without having to wait for the entire duration.
* All statistics have been computed with the assumption that all users have participated in the event.
* The design is not responsive, so the UI may not be consistent across different browsers/devices. During development, we have used Chrome browser as reference. 
* Data inconsistency may cause errors in the application. For example, If a user quits an event half way through, some information related to later phases may not be captured, and this might cause issues when calculating and rendering stats. These issues have been handled in most cases. 
* For the graphs, there are cases where the scale might vary with the data, so this might cause issues when graphs are rendered.
