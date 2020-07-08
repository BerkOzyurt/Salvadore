package com.example.salvadore.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.salvadore.models.ScreenStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class MyHelper {

    public static void setScreenStackData(Context context, ArrayList<ScreenStack> hastable){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String hashtableString = "";

        try{
            hashtableString = ObjectToString(hastable);
        }catch (IOException e){
            e.printStackTrace();
        }
        editor.putString(Constants.KEY_SCREEN_STACK,hashtableString);
        editor.commit();
   }

   public static ArrayList<ScreenStack> getScreenStackData(Context context){
        if(context == null){
            return null;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, 0);
        String hashtableString = sharedPreferences.getString(Constants.KEY_SCREEN_STACK, null);

        if(hashtableString == null){
            return new ArrayList<>();
        }
        ArrayList<ScreenStack> arrayList = new ArrayList<>();

        try {
            arrayList = (ArrayList<ScreenStack>) ObjectFromString(hashtableString);
        }catch (ClassNotFoundException | IOException e){
            e.printStackTrace();
        }
        return arrayList;
   }

    public static Object ObjectFromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64Coder.decode(s);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o = ois.readObject();
        return o;
    }

    public static String ObjectToString(Serializable o) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(o);
        objectOutputStream.close();
        return new String(Base64Coder.encode(byteArrayOutputStream.toByteArray()));
    }
}
