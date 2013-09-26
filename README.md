This plugin mimic the same behavior like Grails plugin. 

New random messages can be send browsing route /greenmail/sendEmail

Sent messages can be listed, raw vizualized or deleted using routes below:

/greenmail/list
/greenmail/show/:id
/greenmail/clear

Plugin configuration - in conf/application.conf file add next two optional properties:

greenmail.disabled=false
play.mail.port=3025

