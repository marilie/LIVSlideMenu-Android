package com.limitlessvirtual.livslidemenu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.limitlessvirtual.livslidingmenu.LiVSlidingMenu;


public class MainActivity extends ActionBarActivity {

    private LiVSlidingMenu liVSlidingMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create menu and set background and foreground views
        liVSlidingMenu = new LiVSlidingMenu();
        liVSlidingMenu.setForegroundView(R.layout.view_top);
        liVSlidingMenu.setBackgroundView(R.layout.view_bottom);

        //The menu can be modified by settings
        liVSlidingMenu.showSlideMenuButton(true);
        liVSlidingMenu.setDecelerateInterpolator(0.5f);
        liVSlidingMenu.setAnimationDuration(300);
        liVSlidingMenu.enableFlipSlideMenuButtonImage(true);

        //Add the menu to the menu container
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.slide_menu_container, liVSlidingMenu);
        fragmentTransaction.commit();

        // Change the view members of the foreground and background views after they have been
        // instantiated
        liVSlidingMenu.setViewInstantiatedListener(new LiVSlidingMenu.ViewsInstantiatedListener() {
            @Override
            public void onInstantiated() {
                LinearLayout linearLayout = (LinearLayout)liVSlidingMenu.getForegroundView();
                ((TextView)linearLayout.findViewById(R.id.text)).setText("Front Drawer");
            }
        });


    }

}
