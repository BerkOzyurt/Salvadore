// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SettingsFragment_ViewBinding implements Unbinder {
  private SettingsFragment target;

  @UiThread
  public SettingsFragment_ViewBinding(SettingsFragment target, View source) {
    this.target = target;

    target.linearLayout_myAccount = Utils.findRequiredViewAsType(source, R.id.linearLayout_myAccount, "field 'linearLayout_myAccount'", LinearLayout.class);
    target.linearLayout_aboutApp = Utils.findRequiredViewAsType(source, R.id.linearLayout_aboutApp, "field 'linearLayout_aboutApp'", LinearLayout.class);
    target.linearLayout_privacyPolicy = Utils.findRequiredViewAsType(source, R.id.linearLayout_privacyPolicy, "field 'linearLayout_privacyPolicy'", LinearLayout.class);
    target.linearLayout_howToUse = Utils.findRequiredViewAsType(source, R.id.linearLayout_howToUse, "field 'linearLayout_howToUse'", LinearLayout.class);
    target.linearLayout_checkWifi = Utils.findRequiredViewAsType(source, R.id.linearLayout_checkWifi, "field 'linearLayout_checkWifi'", LinearLayout.class);
    target.linearLayout_logOut = Utils.findRequiredViewAsType(source, R.id.linearLayout_logOut, "field 'linearLayout_logOut'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SettingsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.linearLayout_myAccount = null;
    target.linearLayout_aboutApp = null;
    target.linearLayout_privacyPolicy = null;
    target.linearLayout_howToUse = null;
    target.linearLayout_checkWifi = null;
    target.linearLayout_logOut = null;
  }
}
