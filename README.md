## UserVoice Android SDK

In this article, we will walk you through your options for setting up the Android SDK.
If you need features not outlined in this article, please share them as feature requests on our feedback forum [here](https://feedback.uservoice.com/forums/64513-developer-api-sdks/category/162129-mobile-sdks-any).
That is the best channel for requesting additional functionality.

The UserVoice Android SDK allows you to integrate a native UserVoice experience directly in your Android apps.

You can try out UserVoice for Android by installing the [UserVoice Help Center app](http://sdk-downloads.uservoice.com/android/HelpCenter.apk) on your device.

You will need a UserVoice account (free) for it to connect to. Go to [uservoice.com](https://www.uservoice.com/plans/) to sign up.

### Installation

The best way to install UserVoice for Android is to use gradle and jcenter. First add jcenter to your list of Maven repositories, if needed.

```
allprojects {
    repositories {
        jcenter()
    }
}
```

Then, add the UserVoice SDK as a project dependency.

```
dependencies {
    compile 'com.uservoice:uservoice-android-sdk:+'
}
```

Add the following code to initialize the UserVoice SDK. This code should run whenever the user launches your app (for metrics purposes), but you also need to make sure that it has been run before trying to launch the UserVoice interface. (If a user navigates directly to one of your apps activities and then launches UserVoice, you want it to be initialized first.)

You can also call UserVoice.init() again with a different config if you need different settings in different contexts.

```
Config config = new Config("yoursite.uservoice.com");
UserVoice.init(config, this);
```

Finally, invoke the UserVoice SDK from your application using one of the following methods.

```
UserVoice.launchUserVoice(this);    // Show the UserVoice portal
UserVoice.launchForum(this);        // Show the feedback forum
UserVoice.launchContactUs(this);    // Show the contact form
UserVoice.launchPostIdea(this);     // Show the idea form
```

### Using with proguard

You need to exclude some dependencies to avoid problems with `org.apache.http`. A ``-dontwarn` statement in the proguard file is not enough (see issue [#147](https://github.com/uservoice/uservoice-android-sdk/issues/147)), you will have to modify your `build.gradle` as follows:

```
compile ('com.uservoice:uservoice-android-sdk:1.2.+') {
    exclude module: 'commons-logging'
    exclude module: 'httpcore'
    exclude module: 'httpclient'
}
```

Also, to avoid crashes on some devices, add the following to your proguard file:

```
-keep class android.support.v7.widget.SearchView { *; }
```

### Other Config options

Before calling `UserVoice.init` you can further customize your configuration.

* Select the forum to display (defaults to your default forum)

```
config.setForumId(58438);
```

* Identify the user with guid, name, and email

```
config.identifyUser("123", "Test User", "test@example.com");
```

* Track user and account traits

```
config.putUserTrait("type", "Account Owner");
config.putAccountTrait("id", "1234");
config.putAccountTrait("name", "SomeCompany");
config.putAccountTrait("ltv", 212.50);
```

* Turn off different parts of the SDK

```
config.setShowForum(false);
config.setShowContactUs(false);
config.setShowPostIdea(false);
config.setShowKnowledgeBase(false);

```
* Select the topic to display (defaults to displaying all topics) 
**NOTE: you must have ticketing features enabled to do this. Doing so without ticketing will crash the application**

```
config.setTopicId(495584);

```

* Set ticket field values 
**NOTE: you must have ticketing features enabled to do this. Doing so without ticketing will crash the application**

```
Map<String, String> ticketFields = new HashMap<String, String>();
ticketFields.put("My Field", "My Value");
config.setCustomFields(ticketFields);
```

### Advanced

* Wire up external user ids for admin console Gadgets

```
UserVoice.setExternalId("myapp", "1234");
```

### Theming

Starting in version 1.2.0 the UserVoice SDK uses its own theme rather than inheriting the theme of the host app, so that the host app's theme won't conflict with it (e.g. if the host app does not use an AppCompat theme, or if it doesn't have an action bar). To customize the appearance of the UserVoice SDK, you need to override its default theme by creating a theme called `UserVoiceTheme` in your own `styles.xml` files. This theme will need to derive from `Theme.AppCompat` from the v7 compatibility library, and include an action bar.

```
<style name="UserVoiceTheme" parent="Theme.AppCompat.Light">
    <!-- theme customizations -->
</style>
```

### Private sites

Note: UserVoice for Android does **not** support private **forums**. This section is only relevant to those using sitewide privacy.

The SDK relies on being able to obtain a client key to communicate with the UserVoice API. If you have a public UserVoice site (the default) then it can obtain this key automatically, so you only need to pass your site URL. However, if you turn on site privacy, this key is also private, so you will need to pass it in. You can obtain an API key pair from the mobile settings section of the UserVoice admin console.

```
Config config = new Config("yoursite.uservoice.com", "API_CLIENT_KEY", "API_CLIENT_SECRET");
UserVoice.init(config);
```

### Kids Apps

The UserVoice Platform, including iOS & Android SDKs, is not COPPA compliant and should not be used in apps marketed at children.

Translations
------------

UserVoice for Android now has support for the following locales: cs, en, fr, nl, pt, ru, zh-rTW.

If you have done an additional translation, we would love to pull it in so that
everyone can benefit. Just fork the project and submit a pull request.

Some strings that show up in the SDK may come directly from the UserVoice API.
If a translation is missing for a string that does not appear in the SDK
codebase, you will need to contribute to the main [UserVoice translation
site](http://translate.uservoice.com/).

License
-------

Copyright 2013 UserVoice Inc. 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

### Reporting Issues

If you are finding an issue, first make sure you are using the latest version of the SDK:

Latest Android SDK version here: https://github.com/uservoice/uservoice-android-sdk/releases

If you are on the latest version, you need to file an issue on our [github issues page](https://github.com/uservoice/uservoice-android-sdk/issues) with the following information:

* What behavior are you currently seeing? (ie. response, error, screen)

* What behavior did you expect to see?

Our engineers have requested example code from your team, which they can download example, run, and plainly see the resulting problem.  

To do this, they will need:

* The minimum amount of code required to demonstrate the problem using a [secret gist](gist.github.com).  
* Isolate the problem to the few lines that call the SDK, plus the minimum boilerplate necessary to compile and run the example.
* If we will need example secrets or a test account, make that clear in the code or accompanying explanation.

Once filed, you can also reach out to our support team at questions@uservoice.com


