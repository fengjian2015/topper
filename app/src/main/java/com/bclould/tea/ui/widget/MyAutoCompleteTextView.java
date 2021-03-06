package com.bclould.tea.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class MyAutoCompleteTextView extends AutoCompleteTextView {
	public MyAutoCompleteTextView(Context context) {
	    super(context);
	  }

	  public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	  }

	  public MyAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	  }

	  @Override
	  public boolean enoughToFilter() {
	    return true;
	  }
       
	  @Override
	  protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
	    super.onFocusChanged(focused, direction, previouslyFocusedRect);
	  }

}
