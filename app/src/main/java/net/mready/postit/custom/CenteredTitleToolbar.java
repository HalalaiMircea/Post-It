package net.mready.postit.custom;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import net.mready.postit.R;

//
// CenteredTitleToolbar
//
// Created by Ben De La Haye on 25/05/2016.
// https://gist.github.com/BenDLH/5e95ca556235710d02a414257e616c78
public class CenteredTitleToolbar extends Toolbar {

    private TextView _titleTextView;
    private int _screenWidth;
    private boolean _centerTitle = true;

    public CenteredTitleToolbar(Context context) {
        super(context);
        init();
    }

    public CenteredTitleToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CenteredTitleToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        _screenWidth = getScreenSize().x;

        _titleTextView = new TextView(getContext());
        _titleTextView.setTextAppearance(getContext(), R.style.ToolbarTitleText);
        _titleTextView.setText("Title");
        addView(_titleTextView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (_centerTitle) {
            int[] location = new int[2];
            _titleTextView.getLocationOnScreen(location);
            _titleTextView.setTranslationX(_titleTextView.getTranslationX() + (-location[0] + _screenWidth / 2 - _titleTextView.getWidth() / 2));
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        _titleTextView.setText(title);
        requestLayout();
    }

    @Override
    public void setTitle(int titleRes) {
        _titleTextView.setText(titleRes);
        requestLayout();
    }

    public void setTitleCentered(boolean centered) {
        _centerTitle = centered;
        requestLayout();
    }

    private Point getScreenSize() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);

        return screenSize;
    }
}