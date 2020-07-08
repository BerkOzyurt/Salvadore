// Generated code from Butter Knife. Do not modify!
package com.example.salvadore.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.salvadore.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ContactsFragment_ViewBinding implements Unbinder {
  private ContactsFragment target;

  @UiThread
  public ContactsFragment_ViewBinding(ContactsFragment target, View source) {
    this.target = target;

    target.linearLayout_editContact1 = Utils.findRequiredViewAsType(source, R.id.linearLayout_editContact1, "field 'linearLayout_editContact1'", LinearLayout.class);
    target.linearLayout_editContact2 = Utils.findRequiredViewAsType(source, R.id.linearLayout_editContact2, "field 'linearLayout_editContact2'", LinearLayout.class);
    target.linearLayout_editContact3 = Utils.findRequiredViewAsType(source, R.id.linearLayout_editContact3, "field 'linearLayout_editContact3'", LinearLayout.class);
    target.linearLayout_deleteContact1 = Utils.findRequiredViewAsType(source, R.id.linearLayout_deleteContact1, "field 'linearLayout_deleteContact1'", LinearLayout.class);
    target.linearLayout_deleteContact2 = Utils.findRequiredViewAsType(source, R.id.linearLayout_deleteContact2, "field 'linearLayout_deleteContact2'", LinearLayout.class);
    target.linearLayout_deleteContact3 = Utils.findRequiredViewAsType(source, R.id.linearLayout_deleteContact3, "field 'linearLayout_deleteContact3'", LinearLayout.class);
    target.textView_contact1Name = Utils.findRequiredViewAsType(source, R.id.textView_contact1Name, "field 'textView_contact1Name'", TextView.class);
    target.textView_contact2Name = Utils.findRequiredViewAsType(source, R.id.textView_contact2Name, "field 'textView_contact2Name'", TextView.class);
    target.textView_contact3Name = Utils.findRequiredViewAsType(source, R.id.textView_contact3Name, "field 'textView_contact3Name'", TextView.class);
    target.textView_contact1Phone = Utils.findRequiredViewAsType(source, R.id.textView_contact1Phone, "field 'textView_contact1Phone'", TextView.class);
    target.textView_contact2Phone = Utils.findRequiredViewAsType(source, R.id.textView_contact2Phone, "field 'textView_contact2Phone'", TextView.class);
    target.textView_contact3Phone = Utils.findRequiredViewAsType(source, R.id.textView_contact3Phone, "field 'textView_contact3Phone'", TextView.class);
    target.textView_contact1Mail = Utils.findRequiredViewAsType(source, R.id.textView_contact1Mail, "field 'textView_contact1Mail'", TextView.class);
    target.textView_contact2Mail = Utils.findRequiredViewAsType(source, R.id.textView_contact2Mail, "field 'textView_contact2Mail'", TextView.class);
    target.textView_contact3Mail = Utils.findRequiredViewAsType(source, R.id.textView_contact3Mail, "field 'textView_contact3Mail'", TextView.class);
    target.linearLayout_editContact = Utils.findRequiredViewAsType(source, R.id.linearLayout_editContact, "field 'linearLayout_editContact'", LinearLayout.class);
    target.editText_contactName = Utils.findRequiredViewAsType(source, R.id.editText_contactName, "field 'editText_contactName'", EditText.class);
    target.editText_contactPhone = Utils.findRequiredViewAsType(source, R.id.editText_contactPhone, "field 'editText_contactPhone'", EditText.class);
    target.editText_contactMail = Utils.findRequiredViewAsType(source, R.id.editText_contactMail, "field 'editText_contactMail'", EditText.class);
    target.btn_saveContact = Utils.findRequiredViewAsType(source, R.id.btn_saveContact, "field 'btn_saveContact'", Button.class);
    target.btn_cancelContact = Utils.findRequiredViewAsType(source, R.id.btn_cancelContact, "field 'btn_cancelContact'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ContactsFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.linearLayout_editContact1 = null;
    target.linearLayout_editContact2 = null;
    target.linearLayout_editContact3 = null;
    target.linearLayout_deleteContact1 = null;
    target.linearLayout_deleteContact2 = null;
    target.linearLayout_deleteContact3 = null;
    target.textView_contact1Name = null;
    target.textView_contact2Name = null;
    target.textView_contact3Name = null;
    target.textView_contact1Phone = null;
    target.textView_contact2Phone = null;
    target.textView_contact3Phone = null;
    target.textView_contact1Mail = null;
    target.textView_contact2Mail = null;
    target.textView_contact3Mail = null;
    target.linearLayout_editContact = null;
    target.editText_contactName = null;
    target.editText_contactPhone = null;
    target.editText_contactMail = null;
    target.btn_saveContact = null;
    target.btn_cancelContact = null;
  }
}
