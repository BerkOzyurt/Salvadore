// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class HowToUseFragment_ViewBinding implements Unbinder {
  private HowToUseFragment target;

  @UiThread
  public HowToUseFragment_ViewBinding(HowToUseFragment target, View source) {
    this.target = target;

    target.frame1 = Utils.findRequiredViewAsType(source, R.id.frame1, "field 'frame1'", FrameLayout.class);
    target.frame2 = Utils.findRequiredViewAsType(source, R.id.frame2, "field 'frame2'", FrameLayout.class);
    target.frame3 = Utils.findRequiredViewAsType(source, R.id.frame3, "field 'frame3'", FrameLayout.class);
    target.frame4 = Utils.findRequiredViewAsType(source, R.id.frame4, "field 'frame4'", FrameLayout.class);
    target.frame5 = Utils.findRequiredViewAsType(source, R.id.frame5, "field 'frame5'", FrameLayout.class);
    target.frame6 = Utils.findRequiredViewAsType(source, R.id.frame6, "field 'frame6'", FrameLayout.class);
    target.frame7 = Utils.findRequiredViewAsType(source, R.id.frame7, "field 'frame7'", FrameLayout.class);
    target.frame8 = Utils.findRequiredViewAsType(source, R.id.frame8, "field 'frame8'", FrameLayout.class);
    target.textView1 = Utils.findRequiredViewAsType(source, R.id.textView1, "field 'textView1'", TextView.class);
    target.textView2 = Utils.findRequiredViewAsType(source, R.id.textView2, "field 'textView2'", TextView.class);
    target.textView3 = Utils.findRequiredViewAsType(source, R.id.textView3, "field 'textView3'", TextView.class);
    target.textView4 = Utils.findRequiredViewAsType(source, R.id.textView4, "field 'textView4'", TextView.class);
    target.textView5 = Utils.findRequiredViewAsType(source, R.id.textView5, "field 'textView5'", TextView.class);
    target.textView6 = Utils.findRequiredViewAsType(source, R.id.textView6, "field 'textView6'", TextView.class);
    target.textView7 = Utils.findRequiredViewAsType(source, R.id.textView7, "field 'textView7'", TextView.class);
    target.textView8 = Utils.findRequiredViewAsType(source, R.id.textView8, "field 'textView8'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    HowToUseFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.frame1 = null;
    target.frame2 = null;
    target.frame3 = null;
    target.frame4 = null;
    target.frame5 = null;
    target.frame6 = null;
    target.frame7 = null;
    target.frame8 = null;
    target.textView1 = null;
    target.textView2 = null;
    target.textView3 = null;
    target.textView4 = null;
    target.textView5 = null;
    target.textView6 = null;
    target.textView7 = null;
    target.textView8 = null;
  }
}
