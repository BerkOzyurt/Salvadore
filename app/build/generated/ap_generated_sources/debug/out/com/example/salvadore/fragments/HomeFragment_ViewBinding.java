// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class HomeFragment_ViewBinding implements Unbinder {
  private HomeFragment target;

  @UiThread
  public HomeFragment_ViewBinding(HomeFragment target, View source) {
    this.target = target;

    target.linearLayout_quickHelp = Utils.findRequiredViewAsType(source, R.id.linearLayout_quickHelp, "field 'linearLayout_quickHelp'", LinearLayout.class);
    target.btn_giveDetail = Utils.findRequiredViewAsType(source, R.id.btn_giveDetail, "field 'btn_giveDetail'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    HomeFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.linearLayout_quickHelp = null;
    target.btn_giveDetail = null;
  }
}
