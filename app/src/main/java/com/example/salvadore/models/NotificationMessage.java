package com.example.salvadore.models;

import com.google.gson.annotations.SerializedName;

/**
 *
 * Message Notificaton Example
 *
 * {
 *      "validate_only": false,
 *      "message":
 *      {
 *          "notification":
 *          {
 *              "title": "Big News",
 *              "body": "This is a Big News!"
 *          },
 *          "android":
 *          {
 *              "notification":
 *              {
 *                  "title": "Noti in Noti title",
 *                  "body": "Noti in Noti body",
 *                  "click_action":
 *                  {
 *                      "type": 3
 *                  }
 *              }
 *          },
 *          "token":   ["xxxxxxxxxxxx"]
 *      }
 * }
 *
 */


public class NotificationMessage {

    @SerializedName("validate_only")
    private boolean validateOnly;
    @SerializedName("message")
    private Message message;

    public NotificationMessage(boolean validateOnly, Message message) {
        this.validateOnly = validateOnly;
        this.message = message;
    }

    private NotificationMessage(Builder builder){

    }

    public static class Builder{

        private String title;
        private String body;
        private String[] pushToken;

        public Builder(String title, String body, String pushToken){
            this.title = title;
            this.body = body;
            this.pushToken = new String[1];
            this.pushToken[0] = pushToken;
        }



        public NotificationMessage build(){

            ClickAction clickAction = new ClickAction(3);
            AndroidNotification androidNotification = new AndroidNotification(title, body,
                    clickAction);
            AndroidConfig androidConfig = new AndroidConfig(androidNotification);
            Notification notification = new Notification(title, body);
            Message message = new Message(notification, androidConfig, pushToken);
            NotificationMessage notificationMessage =
                    new NotificationMessage(false, message);
            return notificationMessage;
        }
    }


    public boolean isValidateOnly() {
        return validateOnly;
    }

    public void setValidateOnly(boolean validateOnly) {
        this.validateOnly = validateOnly;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public static class Message{

        @SerializedName("notification")
        private Notification notification;
        @SerializedName("android")
        private AndroidConfig android;
        @SerializedName("token")
        private String[] token;

        public Message(Notification notification, AndroidConfig android, String[] token) {
            this.notification = notification;
            this.android = android;
            this.token = token;
        }

        public Notification getNotification() {
            return notification;
        }

        public void setNotification(Notification notification) {
            this.notification = notification;
        }

        public AndroidConfig getAndroid() {
            return android;
        }

        public void setAndroid(AndroidConfig android) {
            this.android = android;
        }

        public String[] getToken() {
            return token;
        }

        public void setToken(String[] token) {
            this.token = token;
        }
    }

    public static class Notification{
        @SerializedName("title")
        private String title;
        @SerializedName("body")
        private String body;

        public Notification(String title, String body) {
            this.title = title;
            this.body = body;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    public static class AndroidConfig{
        @SerializedName("notification")
        private AndroidNotification notification;

        public AndroidConfig(AndroidNotification notification) {
            this.notification = notification;
        }

        public AndroidNotification getNotification() {
            return notification;
        }

        public void setNotification(AndroidNotification notification) {
            this.notification = notification;
        }
    }

    public static class AndroidNotification{
        @SerializedName("title")
        private String title;
        @SerializedName("body")
        private String body;
        @SerializedName("click_action")
        private ClickAction clickAction;

        public AndroidNotification(String title, String body, ClickAction clickAction) {
            this.title = title;
            this.body = body;
            this.clickAction = clickAction;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public ClickAction getClickAction() {
            return clickAction;
        }

        public void setClickAction(ClickAction clickAction) {
            this.clickAction = clickAction;
        }
    }

    public static class ClickAction{
        @SerializedName("type")
        private int type;
        @SerializedName("intent")
        private String intent;

        public ClickAction(int type) {
            this.type = type;
        }

        public ClickAction(int type, String intent) {
            this.type = type;
            this.intent = intent;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }
    }
}
