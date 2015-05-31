# LIVSlideMenu-Android

A two layer sliding menu where the top layer can be animated open to reveal the bottom layer.

**Note**: An <a href="https://github.com/limitlessvirtual/LIVSlideMenu-iOS">iOS Version</a> is also available.

##Setup

Download the library and include as module dependancy.

##Initialisation & Usage

1. XML

```xml
...
 <FrameLayout
    android:id="@+id/slide_menu_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
  </FrameLayout>
...
```
2. JAVA

```java
//Create menu and set background and foreground views
    LiVSlidingMenu liVSlidingMenu = new LiVSlidingMenu();
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
```

##Customizable Properties

| Property               | Type     | Description                                                            | Default Value |
|------------------------|----------|------------------------------------------------------------------------|---------------|
| animationDuration      | int      | Duration of the sliding animation.                                     | 500           |
| decelerateInterpolator | float    | Degree to which the animation should be eased.                         | 1.0f           |
| flipEnabled            | boolean  | Flag whether the sliding button image flips on animation.              | true            |
| showButton             | boolean  | Flag whether the sliding button is shown.                              | true           |

##Listeners

###ViewsInstantiatedListener

This listener has a onViewInstantiated() function to implemented and gets called when the top and bottom views are instantiated.
This should be used to make changes or add adapters to views passed through as layouts.

###OnAnimateListener

This listener has 2 functions to be implemented:
1. onStartAnimateOpen()
2. onStartAnimateClose()
These functions get called when the drawer starts to animate open or close.

##Upcoming Features

* Keep drawer open when screen rotates.
* Bounce effect on open and close animation.
