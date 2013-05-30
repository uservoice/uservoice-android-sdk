## UserVoice for Android

UserVoice for Android allows you to embed UserVoice directly in your Android apps.

You will need a UserVoice account (free) for it to connect to. Go to [uservoice.com](https://www.uservoice.com/plans/) to sign up.

### Installation for Eclipse

* File -> Import... -> General -> Existing Projects into Workspace
  * Select the UserVoiceSDK folder
  * If you wish, also select the UVDemo project (this is a demo of how to connect to the SDK).
* Select the project you wish to add UserVoice to
  * Open project properties -> Android -> Library -> Add...
  * Select the UserVoiceSDK project as a library
* Obtain an API key from your UserVoice admin console
  * Admin console -> Settings -> Channels -> API
  * A regular API key pair is fine. You don't need a special Android key.
  * Set it as untrusted.
* Add the following code to initialize the UserVoice SDK
  * Do this either in Application.onCreate or your root Activity.onCreate

```
    Config config = new Config("yoursite.uservoice.com", "YOUR_API_KEY", "YOUR_API_SECRET");
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
    <activity android:name="com.uservoice.uservoicesdk.activity.SuggestionActivity" />
    <activity android:name="com.uservoice.uservoicesdk.activity.ArticleActivity" android:hardwareAccelerated="true" />
    <activity android:name="com.uservoice.uservoicesdk.activity.CommentActivity" />
    <activity android:name="com.uservoice.uservoicesdk.activity.TopicActivity" />
    <activity android:name="com.uservoice.uservoicesdk.activity.ContactActivity" android:hardwareAccelerated="true" />
    <activity android:name="com.uservoice.uservoicesdk.activity.PostIdeaActivity" android:hardwareAccelerated="true" />
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
