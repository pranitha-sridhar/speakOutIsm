# Speak Out ISM
Developed by the students of ISM, the app highlights the problems faced by ISMites accurately. 
‘Speak Out’  lets the students to express their concerns and grievances in an efficient way, and allows the competent authority to redress them quickly. 



# Features in App:

Both students and admins will be able to access the app. 

Complaints divided on the basis of status(Pending, In-Progress & Resolved) & various categories (&sub- categories) like Registration, Academics, DSW, MIS/ParentPortal, Health Centre etc.

Can view all the public grievances, can upvote, downvote and comment on the complaints.

Admin and the owner of the complaint will be able to converse incase of any issues in 'Conversations' section.

Trending Section shows the Top 10 Upvotes Complaints and a pie graph representing category wise distribution of complaints.

Admin also has the authority to change the status of the app, delete the entire complaint & to block/unblock the user.

Admin will be able to view the profile of all registered users.

Can sort the complaints by status and/or by categories.



Using FCM, notifications can be pushed using Device-to-Device method. The student will be notified if:
  they been blocked/unblocked by the user,
  Admin has replied to their complaint,
  If status of the complaint has been changed,
  Some other student has commented to their complaint,
  If their complaint gets deleted by the admin,
  

A particular complaint can be shared to various other social platforms with the help of a link. On clicking that link, that particular complaint will be opened all with other corresponding details.


# UI OF 'SPEAK OUT'

Recycler View has been used

Shimmer Layout

Pie Chart using MPAndroidChart

Toshie Loader

Used custom toolbar and navigation header to have a cool UI

Added chips instead of spinner for selecting categories and sub-categories. 
    
 
# Data Security:

1)Data theft can happen due to database having public rules. So, we changed the rules of the sensitive data, such that only an authorised person will be able to access them.

2)We have made our own server in MongoDB and API call is done through that to protect the secret key from being exposed of FCM. So this will go and get the credentials     and will call the FCM to send notifications.

3)There may be many fraudulent activities that may happen, when users are allowed to log-in from multiple devices. So it will be better to restrict to just one device.      Added auto sign out feature using FCM Data message, which runs in background services.

    
