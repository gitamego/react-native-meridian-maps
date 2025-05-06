Meridian SDK for Android
========================

This SDK package will assist you in embedding Meridian's maps and directions features into your own Android apps. You create your location-based data and maps at https://edit.meridianapps.com then use this SDK to display them in your app. The SDK provides all the necessary functionality of downloading the relevant location data and presenting the UI for maps & wayfinding.

By using the Meridian SDK, you hereby agree to the terms of the [Meridian SDK License](https://edit.meridianapps.com/users/sdk_tac)

What's In The Box?
==================

- README.md: this file!
- meridian-x.y.z.aar: The Android AAR library you'll be referencing in your app.
- MeridianSamples: An Android project demonstrating a simple app that uses the SDK.


Using the SDK in an existing app
================================

Adding a 3rd-party library to an Android project can be tricky. We've tried to make it as simple as possible by bundling the SDK code into an AAR file which is a binary distribution of an "Android Library Project".

In short, here are the steps you'll need to take:

1. Move `meridian-x.y.z.aar` where you'd like it

2. Edit your app's `build.gradle` file

3. Add the relative path to where you placed the AAR file to the root-level `repositories` element like this:

```
repositories {
    mavenCentral()
    google() // optional, probably exists in your project already

    flatDir {
        dirs '../../'
    }
}
```

4. Compile the AAR into your app in the `dependencies` section (make sure to replace the `x.y.z` with the correct version as seen in MeridianSamples):

```
dependencies {
    implementation 'com.arubanetworks.meridian:meridian:x.y.z@aar'

    // 3rd party dependencies used by meridian - please check samples app for latest versions
    implementation 'com.android.volley:volley:x.x.x'
    implementation 'com.squareup:otto:x.x.x'
    implementation 'com.google.android.gms:play-services-location:x.x.x'
    implementation 'org.conscrypt:conscrypt-android:x.x.x'
    implementation 'com.google.android.material:material:x.x.x'
    implementation 'com.lemmingapex.trilateration:trilateration:1.0.2'
    
    // The dependencies below are required for MapSheetFragment
    implementation 'com.github.bumptech.glide:glide:x.x.x'
    annotationProcessor 'com.github.bumptech.glide:compiler:x.x.x'
    implementation 'org.greenrobot:eventbus:x.x.x'
}
```

5. In your relevant source files, you can now import the `com.arubanetworks.meridian` packages to access the Meridian classes.

You can examine the MeridianSamples project to see what the finished `build.gradle` should look like.


Questions, Help, etc.
=====================

More documentation and code snippets can be found on the our [site](https://docs.meridianapps.com/hc/en-us/sections/360006512033).

Please email any support requests, comments, and concerns to developers@meridianapps.com.

Thanks so much for using Meridian!
