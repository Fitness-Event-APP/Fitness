# Fitness Event APP

## Product Mission

This App is majorly specific for the users who is wiling to exercise. It help users search for the nearby fitness events by locating users position. Provide different events about fitness to meet the various need of the public. Also, for those who are interested in the same field, they can share their information and organize a new event by themselves.

## User Story

* I, as an athlete, I would like to go to a place where hold a seminar about nutrition intake for the professional.

* I, as an athlete, I would like to participate in an event where I can meet with other professionals in the same fields. We can work together on training and exchange our experience. 

* I, as a soccer player, I would like to go to watch the soccer game/training match and share experience with those soccer enthusiasts.

* I, as a person who goes to gymnasium twice a week, I would like to know interesting surrounding fitness events, and take part in some activities I am interested in to spice my normal gym day up.

* I, as a person who usually goes to gymnasium alone, I would like to participate in some fitness events to learn new things and meet new friends.

* I, as a person who just starts working out at the gym, I would like to take part in various fitness events to find out what I really interested in.

* I, as a naive fitness keeper, want to find a lot of classes to find a sport which I can stick on.

* I, as a member of family who want to keep fit, want to find more and more free courses hold by fitness centers or clubs via this app.

* I, as an outdoor fitness enthusiast, want to join outdoor sport events like marathon, cycling.

* I, as a bodybuilder, want to join bootcamps to communicate and exchange experiences on building bodies.

## Architecture
![Architecture](https://github.com/Fitness-Event-APP/Fitness/raw/master/img/Architecture.png)


## Target Users

* Athelete

* Person who pursue losing weights

* Person who want to keep fit

## MVP

* Background survey, Access to "join/create" an event.

* Feedback(provide number of potential customers based on their activities to the event creator) and Notification.

* Estimate the calorie consumption for each event.

* User Interface

* Utilize Google Map API

* Social module for user-defined groups

## Product Survey
#### [Boston Calendar](https://www.thebostoncalendar.com/)
Majorly focus on finding under-publicized events around the city and surrounding areas for Bostonians (and non-locals).
* <strong> Pros </strong>
  * Provide mostly-free events around Boston for reddit user, young professional, and student.
  * Redditors can add events, and events can be add to Reddit, iCal, and Google Calendar on the website.
* <strong> Cons </strong>
  * The webpage design is quite simple.
  * The webpage don't have a specific category for fitness.

#### [EventBrite](https://www.eventbrite.com/d/ma--boston/events/)
Majorly focus on providing many kinds of events in a lot of places and creating events to the public.

* <strong> Limits </strong>
  * For fitness domain, it cannot provide exhaustive classcifications.
  * For creating an event, it cannot push to the specific group.

#### [FitEvent](https://fitevents.com/): 

Majorly focus on providing professtional training classes and formal events to users. 

* <strong> Limits </strong>
  * Most they presents are global events, holding in a location which is far away from most people. For most people, they prefer choosing a event around their houses. 

## System Design

#### API

* Google Map API, eventbrite API

#### Database

#### Why Choose Firebase not SQL? 

* Pros: The core of Firebase is the real-time database. It doesn't have the restriction format, for example, SQL using tables to store data. User can use Firebase to store what they want without that kind of restriction. The Firebase also very friendy to Android (both of them are from Google). Besides, to build a connection with our database and Google map API, if we choose SQL, we also need to hava a intermediate knowledge about SQL, PHP and XML. But if we use Firebase, then just need to learn about the Firebase platform. 

#### App

* Android Studio(eg.Viewmodel:automatically retained during configuration changes)

## User Interface
* The homepage of the app

![HomePage](https://github.com/Fitness-Event-APP/Fitness/raw/master/img/HomePage.png)

* After click the event in the homepage. Jump to the event activity.

![theFirstEvent](https://github.com/Fitness-Event-APP/Fitness/raw/master/img/theFirstEvent.png)

* Tab Bars

![taBBar_to_dashboard](https://github.com/Fitness-Event-APP/Fitness/raw/master/img/taBBar_to_dashboard.png)

* User create event

![creatEvent](https://github.com/Fitness-Event-APP/Fitness/raw/master/img/creatEvent.png)
