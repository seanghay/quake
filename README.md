# Quake (WIP)

A tool for quick bug reporting on Android by shaking.

## How does it work?

This library allow testers to shake the device to show the QR code which coresponding to the current screen they are in.

The QR code contains enough information for developer to troubleshoot the issue such as:

- [x] Application version name & version code
- [x] Android Version
- [x] Current Activity/Fragment with (e.g. com.example.app.ui.MainActivity)
- [x] Device Model (Google Pixel 4 XL)
- [ ] Screen size/density (WIP)

> It uses `gzip` to compress the content to keep the QR code content smaller.


## Installation


1. Add jitpack repository in your root project

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

2. Add dependency to your app module

```groovy
dependencies {
  // it should only be on debug build only.
  debugImplementation 'com.github.seanghay:quake:0.0.6-alpha03' 
}
```
