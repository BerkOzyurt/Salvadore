// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.activities;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    this.target = target;

    target.mainBottomNeedListIcon = Utils.findRequiredViewAsType(source, R.id.mainBottomNeedListIcon, "field 'mainBottomNeedListIcon'", ImageView.class);
    target.mainBottomContactsIcon = Utils.findRequiredViewAsType(source, R.id.mainBottomContactsIcon, "field 'mainBottomContactsIcon'", ImageView.class);
    target.mainBottomMapIcon = Utils.findRequiredViewAsType(source, R.id.mainBottomMapIcon, "field 'mainBottomMapIcon'", ImageView.class);
    target.mainBottomSettingsIcon = Utils.findRequiredViewAsType(source, R.id.mainBottomSettingsIcon, "field 'mainBottomSettingsIcon'", ImageView.class);
    target.mainBottomNeedList = Utils.findRequiredViewAsType(source, R.id.mainBottomNeedList, "field 'mainBottomNeedList'", LinearLayout.class);
    target.mainBottomContacts = Utils.findRequiredViewAsType(source, R.id.mainBottomContacts, "field 'mainBottomContacts'", LinearLayout.class);
    target.mainBottomMap = Utils.findRequiredViewAsType(source, R.id.mainBottomMap, "field 'mainBottomMap'", LinearLayout.class);
    target.mainBottomSettings = Utils.findRequiredViewAsType(source, R.id.mainBottomSettings, "field 'mainBottomSettings'", LinearLayout.class);
    target.mainBottomCenterBtn = Utils.findRequiredViewAsType(source, R.id.mainBottomCenterBtn, "field 'mainBottomCenterBtn'", LinearLayout.class);
    target.btn_back = Utils.findRequiredViewAsType(source, R.id.btn_back, "field 'btn_back'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mainBottomNeedListIcon = null;
    target.mainBottomContactsIcon = null;
    target.mainBottomMapIcon = null;
    target.mainBottomSettingsIcon = null;
    target.mainBottomNeedList = null;
    target.mainBottomContacts = null;
    target.mainBottomMap = null;
    target.mainBottomSettings = null;
    target.mainBottomCenterBtn = null;
    target.btn_back = null;
  }
}
