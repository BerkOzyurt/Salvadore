package com.example.salvadore.models;

import java.util.ArrayList;

public class NotificationBody {

    private boolean validate_only;
    Message MessageObject;

    public boolean getValidate_only() {
        return validate_only;
    }

    public void setValidate_only( boolean validate_only ) {
        this.validate_only = validate_only;
    }

    public Message getMessage() {
        return MessageObject;
    }

    public void setMessage( Message messageObject ) {
        this.MessageObject = messageObject;
    }


    public class Message {
        Notification NotificationObject;
        Android AndroidObject;
        ArrayList<String> token = new ArrayList<String>();

        public Notification getNotification() {
            return NotificationObject;
        }

        public void setNotification( Notification notificationObject ) {
            this.NotificationObject = notificationObject;
        }

        public Android getAndroid() {
            return AndroidObject;
        }

        public void setAndroid( Android androidObject ) {
            this.AndroidObject = androidObject;
        }

        public ArrayList<String> getToken() {
            return token;
        }

        public void setToken(ArrayList<String> token) {
            this.token = token;
        }

    }

    public class Notification {
        private String title;
        private String body;
        Click_action Click_actionObject;

        public String getTitle() {
            return title;
        }

        public String getBody() {
            return body;
        }
        public Click_action getClick_action() {
            return Click_actionObject;
        }

        public void setTitle( String title ) {
            this.title = title;
        }

        public void setBody( String body ) {
            this.body = body;
        }

        public void setClick_action( Click_action click_actionObject ) {
            this.Click_actionObject = click_actionObject;
        }
    }

    public class Click_action {
        private float type;
        private String intent;


        // Getter Methods

        public float getType() {
            return type;
        }

        public String getIntent() {
            return intent;
        }

        // Setter Methods

        public void setType( float type ) {
            this.type = type;
        }

        public void setIntent( String intent ) {
            this.intent = intent;
        }
    }

    public class Android {
        Notification NotificationObject;

        public Notification getNotification() {
            return NotificationObject;
        }

        public void setNotification( Notification notificationObject ) {
            this.NotificationObject = notificationObject;
        }
    }


}
