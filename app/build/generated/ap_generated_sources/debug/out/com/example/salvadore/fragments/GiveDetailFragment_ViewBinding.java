// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GiveDetailFragment_ViewBinding implements Unbinder {
  private GiveDetailFragment target;

  @UiThread
  public GiveDetailFragment_ViewBinding(GiveDetailFragment target, View source) {
    this.target = target;

    target.btn_submit = Utils.findRequiredViewAsType(source, R.id.btn_submit, "field 'btn_submit'", Button.class);
    target.multiAutoCompleteTextView = Utils.findRequiredViewAsType(source, R.id.multiAutoCompleteTextView, "field 'multiAutoCompleteTextView'", MultiAutoCompleteTextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    GiveDetailFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.btn_submit = null;
    target.multiAutoCompleteTextView = null;
  }
}
