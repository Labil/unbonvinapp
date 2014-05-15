package sunshake.apps.unbonvinapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class WineTitleTextView extends TextView {

    public WineTitleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public WineTitleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WineTitleTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                      "fonts/StartlingFont.ttf");
        setTypeface(tf);
    }

}