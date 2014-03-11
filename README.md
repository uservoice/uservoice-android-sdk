## UserVoice Android SDK

The UserVoice Android SDK allows you to integrate a native UserVoice experience directly in your Android apps.

You can try out UserVoice for Android by installing the [UserVoice Help Center app](http://sdk-downloads.uservoice.com/android/HelpCenter.apk) on your device.

You will need a UserVoice account (free) for it to connect to. Go to [uservoice.com](https://www.uservoice.com/plans/) to sign up.

### Installation for Eclipse

* File -> Import... -> General -> Existing Projects into Workspace
  * Select the UserVoiceSDK folder
  * If you wish, also select the UVDemo project (this is a demo of how to connect to the SDK).
* Select the project you wish to add UserVoice to
  * Open project properties -> Android -> Library -> Add...
  * Select the UserVoiceSDK project as a library
* Add the following code to initialize the UserVoice SDK
  * Do this either in Application.onCreate or your root Activity.onCreate
  * We strongly recommend you do this on app launch so that UserVoice can provide accurate analytics.
  * You can call UserVoice.init again later if you need to change something about the config.

```
Config config = new Config("yoursite.uservoice.com");
UserVoice.init(config, this);
```

* Make sure you include an Internet permission in your AndroidManifest.xml

```
<uses-permission android:name="android.permission.INTERNET"/>
```

* Add the following activities to the <application> element in your AndroidManifest.xml

```
<activity android:name="com.uservoice.uservoicesdk.activity.PortalActivity" />
<activity android:name="com.uservoice.uservoicesdk.activity.ForumActivity" /> 
<activity android:name="com.uservoice.uservoicesdk.activity.ArticleActivity" />
<activity android:name="com.uservoice.uservoicesdk.activity.TopicActivity" />
<activity android:name="com.uservoice.uservoicesdk.activity.ContactActivity" android:configChanges="orientation|keyboardHidden|screenSize" />
<activity android:name="com.uservoice.uservoicesdk.activity.PostIdeaActivity" android:configChanges="orientation|keyboardHidden|screenSize" />
```

* Finally, invoke the UserVoice SDK from your application using one of the following methods.

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

### Private sites

The SDK relies on being able to obtain a client key to communicate with the UserVoice API. If you have a public UserVoice site (the default) then it can obtain this key automatically, so you only need to pass your site URL. However, if you turn on site privacy, this key is also private, so you will need to pass it in. You can obtain an API key pair from the mobile settings section of the UserVoice admin console.

```
Config config = new Config("yoursite.uservoice.com", "API_CLIENT_KEY", "API_CLIENT_SECRET");
UserVoice.init(config);
```

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
