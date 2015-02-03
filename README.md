## UserVoice Android SDK

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

### Other Config options

Before calling `UserVoice.init` you can further customize your configuration.

* Select the forum to display (defaults to your default forum)

```
config.setForumId(58438);
```

* Select the topic to display (defaults to displaying all topics)

```
config.setTopicId(495584);
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

* Set ticket custom field values

```
Map<String, String> customFields = new HashMap<String, String>();
customFields.put("My Field", "My Value");
config.setCustomFields(customFields);
```

### Advanced

* Wire up external user ids for admin console Gadgets

```
UserVoice.setExternalId("myapp", "1234");
```

* Track custom events

```
UserVoice.track("myevent");
UserVoice.track("myevent", propertyMap);
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
