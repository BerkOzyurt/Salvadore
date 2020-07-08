// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.activities;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RegisterInfoActivity_ViewBinding implements Unbinder {
  private RegisterInfoActivity target;

  @UiThread
  public RegisterInfoActivity_ViewBinding(RegisterInfoActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public RegisterInfoActivity_ViewBinding(RegisterInfoActivity target, View source) {
    this.target = target;

    target.btn_createAccount = Utils.findRequiredViewAsType(source, R.id.btn_createAccount, "field 'btn_createAccount'", Button.class);
    target.editText_registerNameLastName = Utils.findRequiredViewAsType(source, R.id.editText_registerNameLastName, "field 'editText_registerNameLastName'", EditText.class);
    target.editText_registerMail = Utils.findRequiredViewAsType(source, R.id.editText_registerMail, "field 'editText_registerMail'", EditText.class);
    target.editText_registerPhone = Utils.findRequiredViewAsType(source, R.id.editText_registerPhone, "field 'editText_registerPhone'", EditText.class);
    target.editText_registerAge = Utils.findRequiredViewAsType(source, R.id.editText_registerAge, "field 'editText_registerAge'", EditText.class);
    target.spinner_registerGender = Utils.findRequiredViewAsType(source, R.id.spinner_registerGender, "field 'spinner_registerGender'", Spinner.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RegisterInfoActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btn_createAccount = null;
    target.editText_registerNameLastName = null;
    target.editText_registerMail = null;
    target.editText_registerPhone = null;
    target.editText_registerAge = null;
    target.spinner_registerGender = null;
  }
}
