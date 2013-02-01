# Captivate

Captivate is an Android app that does one very simple thing: whenever you connect to a wifi hotspot, it tries to determine if you are behind a [captive portal](http://en.wikipedia.org/wiki/Captive_portal). If you are, it shows a notification to let you quickly open the portal login page. If your session times out, Captivate will re-show the notification when you next turn on your screen.

It automates the process of opening a browser, trying to visit a webpage, getting redirected to the portal's login, then having to re-type the URL, or worse, go back to the app that opened the browser and re-share.

*For developers:* Captivate sends a broadcast intent whenever the portal state changes. See more [below](#developers).

## What's New

### 1.3.1

*   Hopefully fixed the crash when signin activity finished

### 1.3.0

*   Better tablet experience (sign-in appears as dialog on screens 7" and up)
*   UI changes (dark ActionBar)
*   Updated ActionBarSherlock to 4.2.0
*   Fixed recent false portal notifications
*   Removed French and Turkish translations (too few installs to warrant maintaining)

### 1.2.1

*   Portal notification shows the favicon from the sign-in page

### 1.2.0

*   Tapping on the portal notification now launches a custom browser window which automatically closes when the portal is signed in.
*   Added support for the ICS prettiness!

### 1.1.1

*   Fixed some portal detection bugs

### 1.1.0

*   Captivate is now more intelligent -- it automatically detects when you're logged into a portal, and if your session times out.
*   Portal detection can be disabled in the settings.
*   Spanish, Turkish, and French translations (thanks to Google Translate, so I can't vouch for their quality).

### 1.0.0

- Initial release.

## Download

<a href="http://play.google.com/store/apps/details?id=com.zachklipp.captivate">
  <img alt="Get it on Google Play"
       src="http://www.android.com/images/brand/get_it_on_play_logo_large.png" />
</a>
<a href="https://github.com/zach-klippenstein/captivate/downloads">
    <img alt="Get it on GitHub" height="60px"
         src="https://assets.github.com/images/modules/header/logo.png" />
</a>

<div style="text-align: center;">
    <img src="https://chart.googleapis.com/chart?cht=qr&chs=300x300&chl=https://github.com/zach-klippenstein/captivate%23download" />
</div>

## Developers

Whenever Captivate detects a portal, or a portal signin, or timeout, or anything, it broadcasts the intent `com.zachklipp.captivate.intent.ACTION_PORTAL_STATE_CHANGED`. It includes two extras containing the current portal state, and information about the portal. The intent and extra names as of v1.1.0 are defined around [here](https://github.com/zach-klippenstein/captivate/blob/fec8245d90de1e23788ce8924577d24597db3ff2/src/src/com/zachklipp/captivate/service/PortalDetectorService.java#L34).

*   `com.zachklipp.captivate.intent.EXTRA_PORTAL_STATE`
    One of the following (self-explanatory) strings (defined, as of v1.1.0, [here](https://github.com/zach-klippenstein/captivate/blob/fec8245d90de1e23788ce8924577d24597db3ff2/src/src/com/zachklipp/captivate/state_machine/PortalStateMachine.java#L23)):
    *   `unknown`
    *   `no_portal`
    *   `signin_required`
    *   `signing_in` (currently not used)
    *   `signed_in`

*   `com.zachklipp.captivate.intent.EXTRA_PORTAL_URL`
    A string URL that will take the user to the login page. As of v1.1.0, it only contains the URL used to check for redirects, so it's not the actual URL of the portal sign-in page. This is planned for the future (see issue [#9](https://github.com/zach-klippenstein/captivate/issues/9)).

To receive the intent, you must request permission `com.zachklipp.captivate.permission.ACCESS_PORTAL_STATE`.