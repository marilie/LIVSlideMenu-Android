package com.limitlessvirtual.livslidingmenu;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * LiVSlidingMenu
 * <p/>
 * <P>This fragment is responsible for containing, handling and animating the
 * foreground and background views
 *
 * @author Limitless Virtual
 * @version 1.0
 */
public class LiVSlidingMenu extends Fragment {

    //View Members
    private FrameLayout foregroundFrameContainer;
    private FrameLayout foregroundFrame;
    private FrameLayout backgroundFrame;
    private View fragment;
    private int backgroundLayoutResource;
    private int foregroundLayoutResource;
    private ViewGroup foregroundView;
    private ViewGroup backgroundView;
    private LinearLayout slideMenuButton;
    private ImageView slideMenuButtonImage;

    //Listeners
    private OnAnimateListener animateListener;
    private ViewsInstantiatedListener viewsInstantiatedListener;

    //Settings
    private int animationDuration = 500;
    private float decelerateInterpolator = 1.0f;
    private boolean flipEnabled = true;
    private float margin = -1;
    private Boolean showButton = true;


    //URI's
    private static final String SHOW_BUTTON = "SHOW_BUTTON";
    private static final String ANIMATION_DURATION = "ANIMATION_DURATION";
    private static final String DECELERATE_INTERPOLATOR = "DECELERATE_INTERPOLATOR";
    private static final String FOREGROUND_LAYOUT_RESOURCE = "FOREGROUND_LAYOUT_RESOURCE";
    private static final String BACKGROUND_LAYOUT_RESOURCE = "BACKGROUND_LAYOUT_RESOURCE";
    private static final String FLIP_ENABLED = "FLIP_ENABLED";

    //Flags
    private boolean isOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_header, null);
        foregroundFrameContainer = (FrameLayout) fragment.findViewById(R.id.header_frame_foreground_container);
        foregroundFrame = (FrameLayout) fragment.findViewById(R.id.header_frame_foreground);
        backgroundFrame = (FrameLayout) fragment.findViewById(R.id.header_frame_background);

        slideMenuButton = (LinearLayout) fragment.findViewById(R.id.slide_menu_button);
        slideMenuButtonImage = (ImageView) fragment.findViewById(R.id.slide_menu_button_image);


        slideMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isOpen = !isOpen;
                animate(isOpen);
            }
        });


        if (viewsInstantiatedListener == null)
            viewsInstantiatedListener = new ViewsInstantiatedListener() {
                @Override
                public void onInstantiated() {

                }
            };

        if (animateListener == null)
            animateListener = new OnAnimateListener() {
                @Override
                public void onStartAnimateOpen() {

                }

                @Override
                public void onStartAnimateClose() {

                }
            };

        instantiateViews(savedInstanceState);

        ViewTreeObserver vto = foregroundFrame.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = foregroundFrame.getWidth();
                enableDrawer(width);
                if (Build.VERSION.SDK_INT < 16) {
                    foregroundFrame.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    foregroundFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });
        backgroundFrame.setVisibility(View.GONE);
        return fragment;
    }

    /**
     * Responsible for specifying how the drawer moves
     *
     * @param width of the container
     */
    private void enableDrawer(final int width) {
        backgroundFrame.setX(-width);
        backgroundFrame.setVisibility(View.VISIBLE);
        fragment.setOnTouchListener(new View.OnTouchListener() {
            private float touchX;
            private float touchY;
            private float startX;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN: {
                        startX = backgroundFrame.getX();
                        touchX = event.getX();
                        touchY = event.getY();
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        float deltaX = event.getX() - touchX;
                        //clamp the movements
                        float movementX = Math.max(Math.min(startX + deltaX, -margin), - width);
                        backgroundFrame.setX(movementX);
                        foregroundFrame.setX(movementX + backgroundFrame.getWidth());
                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        if (!isOpen) {
                            if (backgroundFrame.getX() >= -(backgroundFrame.getWidth() / 4.0f * 3.0f)) {
                                animate(true);
                                isOpen = true;
                            } else {
                                animate(false);
                                isOpen = false;
                            }
                        } else {
                            if (backgroundFrame.getX() >= -(backgroundFrame.getWidth() / 4.0f)) {
                                animate(true);
                                isOpen = true;
                            } else {
                                animate(false);
                                isOpen = false;
                            }
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    /**
     * Specifies the animation of the drawer
     *
     * @param open boolean specifying if the drawer is open
     */
    private void animate(final boolean open) {

        final float startingPosition = backgroundFrame.getX();

        Animation background_animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {


                Matrix matrix = t.getMatrix();
                if (open)
                    matrix.setTranslate(interpolatedTime * (-(startingPosition + margin)), 0.0f);
                else
                    matrix.setTranslate(interpolatedTime * (-backgroundFrame.getWidth() - (startingPosition)), 0.0f);

                backgroundFrame.invalidate();
                foregroundFrame.invalidate();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

            @Override
            public boolean willChangeTransformationMatrix() {
                return true;
            }


        };


        background_animation.setDuration(animationDuration);


        background_animation.setInterpolator(new DecelerateInterpolator(decelerateInterpolator));
        background_animation.setFillEnabled(true);
        Animation.AnimationListener animationListenerA = new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

                fragment.setEnabled(false);
                if (!isOpen)
                    animateListener.onStartAnimateClose();
                else
                    animateListener.onStartAnimateOpen();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (open) {

                    backgroundFrame.setX(-margin);
                } else {
                    backgroundFrame.setX(-backgroundFrame.getWidth());
                }
                fragment.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }


        };
        background_animation.setAnimationListener(animationListenerA);


        final float startingPositionBack = foregroundFrame.getX();

        Animation foregroundAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                Matrix matrix = t.getMatrix();
                if (open)
                    matrix.setTranslate(interpolatedTime * (-(startingPosition + margin)), 0.0f);
                else
                    matrix.setTranslate(interpolatedTime * (-backgroundFrame.getWidth() - (startingPosition)), 0.0f);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }

            @Override
            public boolean willChangeTransformationMatrix() {
                return true;
            }


        };

        foregroundAnimation.setDuration(animationDuration);


        Animation.AnimationListener animationListenerB = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(final Animation animation) {
                slideMenuButton.setEnabled(false);
                if (flipEnabled) {
                    if (isOpen)
                        slideMenuButtonImage.setScaleX(-1f);
                    else
                        slideMenuButtonImage.setScaleX(1f);
                }
                fragment.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (open) {
                    foregroundFrame.setX(backgroundFrame.getWidth() - margin);
                } else {
                    foregroundFrame.setX(0.0f);
                }
                fragment.setEnabled(true);
                slideMenuButton.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

        };
        foregroundAnimation.setInterpolator(new DecelerateInterpolator(decelerateInterpolator));
        foregroundAnimation.setFillEnabled(true);
        foregroundAnimation.setAnimationListener(animationListenerB);

        foregroundFrame.startAnimation(foregroundAnimation);

        backgroundFrame.startAnimation(background_animation);
    }


    /**
     * To close the drawer with animation
     */
    public void closeDrawer() {
        if (isOpen) {
            animate(false);
            isOpen = false;
        }

    }

    /**
     * To open the drawer with animation
     */
    public void openDrawer() {
        if (!isOpen) {
            animate(true);
            isOpen = true;
        }

    }


    /**
     * Setter
     * Sets the fragment underneath
     *
     * @param foregroundView
     */
    public void setForegroundView(int foregroundView) {
        this.foregroundLayoutResource = foregroundView;

    }

    /**
     * Setter
     * Sets the fragment on top
     *
     * @param backgroundView
     */
    public void setBackgroundView(int backgroundView) {
        this.backgroundLayoutResource = backgroundView;

    }

    /**
     * A listener that can be implemented to specify what happens when the drawer opens
     * or closes
     */
    public interface OnAnimateListener {
        /**
         * Will be called when the drawer starts to animate open
         */
        public void onStartAnimateOpen();

        /**
         * Will be called when the drawer starts to animate close
         */
        public void onStartAnimateClose();
    }

    /**
     * Setter
     * Set the OnAnimateListener
     *
     * @param eventListener
     */
    public void setOnAnimateListener(OnAnimateListener eventListener) {
        animateListener = eventListener;
    }

    /**
     * Getter
     *
     * @return a boolean specifying if the drawer is open or not
     */
    public boolean isOpen() {
        return isOpen;
    }


    /**
     * Specifies if the slide menu button should be visible
     * (default = true)
     *
     * @param showButton boolean value to set if the button should
     *                   be shown or not
     */
    public void showSlideMenuButton(boolean showButton) {
        this.showButton = showButton;
    }

    /**
     * Set if image of slide menu button flips as animate
     * (default = true)
     *
     * @param enable
     */
    public void enableFlipSlideMenuButtonImage(boolean enable) {
        if (enable)
            flipEnabled = true;
        else
            flipEnabled = false;

    }

    /**
     * Setter
     * (default = 500)
     *
     * @param duration of the animation in milliseconds
     */
    public void setAnimationDuration(int duration) {
        this.animationDuration = duration;
    }

    /**
     * Setter
     * (default = 1.0f)
     *
     * @param decelerateInterpolator decelerate interpolator factor
     */
    public void setDecelerateInterpolator(float decelerateInterpolator) {
        this.decelerateInterpolator = decelerateInterpolator;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(FOREGROUND_LAYOUT_RESOURCE, foregroundLayoutResource);
        outState.putInt(BACKGROUND_LAYOUT_RESOURCE, backgroundLayoutResource);
        outState.putFloat(DECELERATE_INTERPOLATOR, decelerateInterpolator);
        outState.putInt(ANIMATION_DURATION, animationDuration);
        outState.putBoolean(FLIP_ENABLED, flipEnabled);
        outState.putBoolean(SHOW_BUTTON, showButton);

    }

    /**
     * Instantiates foreground and background views as well as settings
     *
     * @param savedInstanceState
     */
    private void instantiateViews(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            foregroundLayoutResource = savedInstanceState.getInt(FOREGROUND_LAYOUT_RESOURCE);
            backgroundLayoutResource = savedInstanceState.getInt(BACKGROUND_LAYOUT_RESOURCE);
            animationDuration = savedInstanceState.getInt(ANIMATION_DURATION);
            decelerateInterpolator = savedInstanceState.getFloat(DECELERATE_INTERPOLATOR);
            flipEnabled = savedInstanceState.getBoolean(FLIP_ENABLED);
            showButton = savedInstanceState.getBoolean(SHOW_BUTTON);
        }

        margin = getResources().getDimensionPixelOffset(R.dimen.header_slide_margin);

        if (showButton)
            slideMenuButton.setVisibility(View.VISIBLE);
        else
            slideMenuButton.setVisibility(View.GONE);

        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        foregroundView = (LinearLayout) inflater.inflate(foregroundLayoutResource, null);
        backgroundView = (LinearLayout) inflater.inflate(backgroundLayoutResource, null);

        foregroundFrameContainer.addView(foregroundView);
        backgroundFrame.addView(backgroundView);
        viewsInstantiatedListener.onInstantiated();

    }

    /**
     * Listener for when the views are instantiated
     */
    public interface ViewsInstantiatedListener {
        public void onInstantiated();
    }

    /**
     * Setter
     *
     * @param viewsInstantiatedListener
     */
    public void setViewInstantiatedListener(ViewsInstantiatedListener viewsInstantiatedListener) {
        this.viewsInstantiatedListener = viewsInstantiatedListener;
    }

    /**
     * Getter
     *
     * @return View in ForegroundFrame
     */
    public ViewGroup getForegroundView() {
        return foregroundView;
    }

    /**
     * Getter
     *
     * @return View in BackgroundFrame
     */
    public ViewGroup getBackgroundView() {
        return foregroundView;
    }


}
