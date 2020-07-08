// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WifiStatusFragment_ViewBinding implements Unbinder {
  private WifiStatusFragment target;

  @UiThread
  public WifiStatusFragment_ViewBinding(WifiStatusFragment target, View source) {
    this.target = target;

    target.btn_getWifiResult = Utils.findRequiredViewAsType(source, R.id.btn_getWifiResult, "field 'btn_getWifiResult'", Button.class);
    target.textView_wifiResult = Utils.findRequiredViewAsType(source, R.id.textView_wifiResult, "field 'textView_wifiResult'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WifiStatusFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btn_getWifiResult = null;
    target.textView_wifiResult = null;
  }
}
