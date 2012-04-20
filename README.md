# Captivate

v1.0.0

Captivate is an Android app that does one very simple thing: whenever you connect to a wifi hotspot, it tries to determine if you are behind a [captive portal](http://en.wikipedia.org/wiki/Captive_portal). If you are, it shows a notification to let you quickly open the portal login page.

There's no configuration, and no real UI. It just automates the process of opening the browser, trying to visit a webpage, getting redirected to the portal's login, then having to re-type the URL, or worse, go back to the app that sent the intent and re-send it.
