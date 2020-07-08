// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ProfileFragment_ViewBinding implements Unbinder {
  private ProfileFragment target;

  @UiThread
  public ProfileFragment_ViewBinding(ProfileFragment target, View source) {
    this.target = target;

    target.btn_editProfile = Utils.findRequiredViewAsType(source, R.id.btn_editProfile, "field 'btn_editProfile'", Button.class);
    target.btn_saveChanges = Utils.findRequiredViewAsType(source, R.id.btn_saveChanges, "field 'btn_saveChanges'", Button.class);
    target.textView_centerName = Utils.findRequiredViewAsType(source, R.id.textView_centerName, "field 'textView_centerName'", TextView.class);
    target.textView_mail = Utils.findRequiredViewAsType(source, R.id.textView_mail, "field 'textView_mail'", TextView.class);
    target.textView_phone = Utils.findRequiredViewAsType(source, R.id.textView_phone, "field 'textView_phone'", TextView.class);
    target.textView_age = Utils.findRequiredViewAsType(source, R.id.textView_age, "field 'textView_age'", TextView.class);
    target.textView_gender = Utils.findRequiredViewAsType(source, R.id.textView_gender, "field 'textView_gender'", TextView.class);
    target.textView_country = Utils.findRequiredViewAsType(source, R.id.textView_country, "field 'textView_country'", TextView.class);
    target.textView_city = Utils.findRequiredViewAsType(source, R.id.textView_city, "field 'textView_city'", TextView.class);
    target.relativeLayout_profileGenderArea = Utils.findRequiredViewAsType(source, R.id.relativeLayout_profileGenderArea, "field 'relativeLayout_profileGenderArea'", RelativeLayout.class);
    target.spinner_profileGender = Utils.findRequiredViewAsType(source, R.id.spinner_profileGender, "field 'spinner_profileGender'", Spinner.class);
    target.editText_profileMail = Utils.findRequiredViewAsType(source, R.id.editText_profileMail, "field 'editText_profileMail'", TextView.class);
    target.editText_profilePhone = Utils.findRequiredViewAsType(source, R.id.editText_profilePhone, "field 'editText_profilePhone'", TextView.class);
    target.editText_profileAge = Utils.findRequiredViewAsType(source, R.id.editText_profileAge, "field 'editText_profileAge'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ProfileFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btn_editProfile = null;
    target.btn_saveChanges = null;
    target.textView_centerName = null;
    target.textView_mail = null;
    target.textView_phone = null;
    target.textView_age = null;
    target.textView_gender = null;
    target.textView_country = null;
    target.textView_city = null;
    target.relativeLayout_profileGenderArea = null;
    target.spinner_profileGender = null;
    target.editText_profileMail = null;
    target.editText_profilePhone = null;
    target.editText_profileAge = null;
  }
}
