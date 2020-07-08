// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class HelpListFragment_ViewBinding implements Unbinder {
  private HelpListFragment target;

  @UiThread
  public HelpListFragment_ViewBinding(HelpListFragment target, View source) {
    this.target = target;

    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recyclerView, "field 'recyclerView'", RecyclerView.class);
    target.radioGroup = Utils.findRequiredViewAsType(source, R.id.radioGroup, "field 'radioGroup'", RadioGroup.class);
    target.radioButton_allHelps = Utils.findRequiredViewAsType(source, R.id.radioButton_allHelps, "field 'radioButton_allHelps'", RadioButton.class);
    target.radioButton_myHelps = Utils.findRequiredViewAsType(source, R.id.radioButton_myHelps, "field 'radioButton_myHelps'", RadioButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    HelpListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerView = null;
    target.radioGroup = null;
    target.radioButton_allHelps = null;
    target.radioButton_myHelps = null;
  }
}
