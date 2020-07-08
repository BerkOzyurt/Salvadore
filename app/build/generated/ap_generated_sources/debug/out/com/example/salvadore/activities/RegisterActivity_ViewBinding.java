// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.activities;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RegisterActivity_ViewBinding implements Unbinder {
  private RegisterActivity target;

  @UiThread
  public RegisterActivity_ViewBinding(RegisterActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public RegisterActivity_ViewBinding(RegisterActivity target, View source) {
    this.target = target;

    target.textView_login = Utils.findRequiredViewAsType(source, R.id.textView_login, "field 'textView_login'", TextView.class);
    target.layout_registerWithHuawei = Utils.findRequiredViewAsType(source, R.id.layout_registerWithHuawei, "field 'layout_registerWithHuawei'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RegisterActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.textView_login = null;
    target.layout_registerWithHuawei = null;
  }
}
