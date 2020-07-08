// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import com.huawei.hms.maps.MapView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MapFragment_ViewBinding implements Unbinder {
  private MapFragment target;

  @UiThread
  public MapFragment_ViewBinding(MapFragment target, View source) {
    this.target = target;

    target.mMapView = Utils.findRequiredViewAsType(source, R.id.mapview_mapviewdemo, "field 'mMapView'", MapView.class);
    target.editText_search = Utils.findRequiredViewAsType(source, R.id.editText_search, "field 'editText_search'", EditText.class);
    target.btn_search = Utils.findRequiredViewAsType(source, R.id.btn_search, "field 'btn_search'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MapFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mMapView = null;
    target.editText_search = null;
    target.btn_search = null;
  }
}
