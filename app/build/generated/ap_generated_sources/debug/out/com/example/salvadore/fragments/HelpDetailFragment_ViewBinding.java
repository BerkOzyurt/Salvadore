// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class HelpDetailFragment_ViewBinding implements Unbinder {
  private HelpDetailFragment target;

  @UiThread
  public HelpDetailFragment_ViewBinding(HelpDetailFragment target, View source) {
    this.target = target;

    target.btn_showMap = Utils.findRequiredViewAsType(source, R.id.btn_showMap, "field 'btn_showMap'", Button.class);
    target.btn_helpDetailCloseHelp = Utils.findRequiredViewAsType(source, R.id.btn_helpDetailCloseHelp, "field 'btn_helpDetailCloseHelp'", Button.class);
    target.btn_helpDetailUpdateLocation = Utils.findRequiredViewAsType(source, R.id.btn_helpDetailUpdateLocation, "field 'btn_helpDetailUpdateLocation'", Button.class);
    target.textView_helpDetailName = Utils.findRequiredViewAsType(source, R.id.textView_helpDetailName, "field 'textView_helpDetailName'", TextView.class);
    target.textView_helpDetailState = Utils.findRequiredViewAsType(source, R.id.textView_helpDetailState, "field 'textView_helpDetailState'", TextView.class);
    target.textView_helpDetailDate = Utils.findRequiredViewAsType(source, R.id.textView_helpDetailDate, "field 'textView_helpDetailDate'", TextView.class);
    target.textView_helpDetailDetails = Utils.findRequiredViewAsType(source, R.id.textView_helpDetailDetails, "field 'textView_helpDetailDetails'", TextView.class);
    target.textView_helpDetailAddress = Utils.findRequiredViewAsType(source, R.id.textView_helpDetailAddress, "field 'textView_helpDetailAddress'", TextView.class);
    target.linearLayout_AcceptRequest = Utils.findRequiredViewAsType(source, R.id.linearLayout_AcceptRequest, "field 'linearLayout_AcceptRequest'", LinearLayout.class);
    target.btn_helpDetailAcceptHelp = Utils.findRequiredViewAsType(source, R.id.btn_helpDetailAcceptHelp, "field 'btn_helpDetailAcceptHelp'", Button.class);
    target.textView_helpAccepted = Utils.findRequiredViewAsType(source, R.id.textView_helpAccepted, "field 'textView_helpAccepted'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    HelpDetailFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btn_showMap = null;
    target.btn_helpDetailCloseHelp = null;
    target.btn_helpDetailUpdateLocation = null;
    target.textView_helpDetailName = null;
    target.textView_helpDetailState = null;
    target.textView_helpDetailDate = null;
    target.textView_helpDetailDetails = null;
    target.textView_helpDetailAddress = null;
    target.linearLayout_AcceptRequest = null;
    target.btn_helpDetailAcceptHelp = null;
    target.textView_helpAccepted = null;
  }
}
