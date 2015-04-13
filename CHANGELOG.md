## master

## 1.2.4

* Always check that emails are valid

## 1.2.3

* Add localizations

## 1.2.2

* Fix a regression that broke article browsing

## 1.2.1

* Fix Session to be a real singleton. Thanks to everyone who reported this. It was resulting in some random crashes.
* Fill in missing translations.
* Limit idea name to 140 chars, which is what the API will accept.
* Delete saved access token and retry without it if it is invalid.
* Fix reporting of instant answers metrics.
* Store the config in local storage so that if we get evicted from memory, we still have a config.

## 1.2.0

* Pull in appcompat-v7 to provide an ActionBar on older OS versions
* Fix loading indicator dimensions on older OS versions
* Unescape html entities in topic and forum names
* Hide post idea item on forum view if post idea is disabled
* Use a theme separate from the host app
* Add Spanish translation
* Bump compileSdkVersion to 21 (Android 5)

## 1.1.2

* Add programmatic support for ticket attachments
* Fix issue with stretched images in knowledgebase articles

## 1.1.1

* Translation updates
* Fix a bug where the sdk could not be used if an admin email address was passed to identifyUser()
* Fix a few crash sources related to Activity state
* Add support for displaying suggestion rank

## 1.1.0

* Fix a bug that sent way too much traffic to the UserVoice api

## 1.0.1

* Add portuguese translation
* fix a bug where UserVoice.init() would show an error message to the user if the server was unreachable
* Prevent duplicated ideas or tickets if the user taps submit multiple times
* Add email validation
* Force soft keyboard to hide when tapping Next in form views

## 1.0.0

* First versioned release

