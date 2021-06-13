# Speak Out ISM
Developed by the students of ISM, the app highlights the problems faced by ISMites accurately. 
‘Speak Out’  lets the students to express their concerns and grievances in an efficient way, and allows the competent authority to redress them quickly.


# Sections:
1) Grievance Redressal System
2) Mess Management System
3) Notice Board
4) Campus Map
5) Other Resources Access: MIS, Parent Portal, Mailer Daemon



# Grievance Redressal System:
## Features 
Both students and admins will be able to access the app. 

Complaints are divided on the basis of status(Pending, In-Progress & Resolved) & various categories (&sub- categories) like Registration, Academics, DSW, MIS/ParentPortal, Health Centre etc.

Can view all the public grievances, can upvote, downvote and comment on the complaints.

Admin and the owner of the complaint will be able to converse incase of any issues in 'Conversations' section.

Trending Section shows the Top 10 Upvotes Complaints and a pie graph representing category wise distribution of complaints.

Admin also has the authority to change the status of the app, delete the entire complaint & to block/unblock the user.

Admin will be able to view the profile of all registered users.

Can sort the complaints by status and/or by categories.


## Push Notifications
Using FCM, notifications can be pushed using Device-to-Device method. The student will be notified if:
- They been blocked/unblocked by the user
- Admin has replied to their complaint
- If status of the complaint has been changed
- Some other student has commented to their complaint
- If their complaint gets deleted by the Admin
  
## Links for sharing
A particular complaint can be shared to various other social platforms with the help of a link. On clicking that link, that particular complaint will be opened all with other corresponding details about the upvotes, downvotes, comments and the conversation (between admin and the owner of the complaint).


# Mess Management System:
## Features
A new hierarchy level for the Mess Admin will be introduced for the mess managers. They will be allowed for this section and also to view and reply to complaints regarding the mess only.

Includes: QR Code, Inventory, Attendance Records & Mess Schedule.

## QR Code
-With the help of QR Code, a consolidated list of records can be given about the number of people coming to mess
-Various problems that is caused due to absence of attendance system could be prevented. 
-While entering the mess, there will be a QR SCANNER which will scan the unique QR Code of every individual student coming to the mess and recording their attendance.
-An unique QR Code will be generated and displayed for students. And Mess Admin will have the feature to scan any QR Code to get an user's details.

## Inventory
Mess Manager can look up and keep adding or editing or deleting the inventory fields. 

A proper list could be published with all the necessary items,after selecting the respective hostel mess.


## Attendance Records
A student can track his/her mess attendance, the frequency of how many times in a week or month do they go to the mess.

Mess Manager will also be able to view the details of a student's records after scanning the respective student’s QR Code.

A detailed report of the whole week and month will be displayed on the screen, if they have taken their breakfast, lunch and dinner from the mess or not.

Mess Manager can get the complete data of how many students had entered the mess per meal in a day or week or month.


## Mess Schedule
After choosing a particular hostel mess and the day of the week, students can view the mess plan accordingly.

Admin can edit, add or delete the schedule if there is any change in the mess plan.


# Notice Board:
Students will be let aware of all the important notices and where abouts happening in the campus and also about club news.

Admin will be allowed to create a new notice for the audience to view.

# Campus Map:
Students will be able to navigate to and locate various places inside the campus without any hassle. They can get the directions to any particular place after selecting it from the list. 

# Resources Access:
Students will be able to access all the Institute's website from a single app. MIS, Parent Portal and Mailer Daemon can be opened directly from this app, which is very easy to use.


# UI OF 'SPEAK OUT'
1) Recycler View has been used
2) Shimmer Layout
3) Pie Chart using MPAndroidChart
4) Toshie Loader
5) Used custom toolbar and navigation header to have a cool UI
6) Added chips instead of spinner for selecting categories and sub-categories.


# Technolgies Used:
**IDE:** Android Studio(JAVA & XML)
**Version Control:** Github for collaboration
**Server:** MongoDB

**Google Firebase:**
-Authentication   
-Realtime Database
-Firebase Cloud Messaging
-Cloud Storage

**Libraries Used:**
-Glide			      
-MPAndroidChart
-Shimmer
-Butterknife
-Circular ImageView
-Dots Loader Animation


# Data Security:

1)Data theft can happen due to database having public rules. So, we changed the rules of the sensitive data, such that only an authorised person will be able to access them.

2)We have made our own server in MongoDB and API call is done through that to protect the secret key from being exposed of FCM. So this will go and get the credentials     and will call the FCM to send notifications.

3)There may be many fraudulent activities that may happen, when users are allowed to log-in from multiple devices. So it will be better to restrict to just one device.      Added auto sign out feature using FCM Data message, which runs in background services. 
