// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class PremiumFragment_ViewBinding implements Unbinder {
  private PremiumFragment target;

  @UiThread
  public PremiumFragment_ViewBinding(PremiumFragment target, View source) {
    this.target = target;

    target.btn_buy = Utils.findRequiredViewAsType(source, R.id.btn_buy, "field 'btn_buy'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    PremiumFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btn_buy = null;
  }
}
