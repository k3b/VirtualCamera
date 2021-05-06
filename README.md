# ![](https://github.com/k3b/VirtualCamera/raw/master/app/src/main/res/drawable/virtual_camera.png) Virtual Camera: Select from an existing jpg file instead of taking a photo from camera chooser.

When "Virtual Camera" is installed many Android-Apps that can select a camera to take a photo can also 
choose to open an exisiting jpg file. 
 
Note that "Virtual Camera" has no userinterface and no starticon of its own.

The app is super tiny: 0,16 MB

## Example usecase:

Inside your "app with camera" open an existing photo jpg.

![](https://github.com/k3b/VirtualCamera/raw/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1-workflow.png)

* (0) open "app with camera"
* (1) Click on "Take a photo"
* The Android-System-Camera-Chooser opens.
* (2) Choose "Select picture"
* The Android-System-Photo-Chooser opens.
* (3) Choose a photo
* (4) You are back in open "app with camera" with the chosen photo.    

## Requirements:

* Android-4.4 (api 19) or later.
* Required Permissions:  
  * CAMERA needed to simulate a camera
  * WRITE_EXTERNAL_STORAGE to save the photo to a file

## Technical details

* "Virtual Camera" plugs into the Android-System-Camera-Chooser that is used by many Android apps.
* It Translates from MediaStore.ACTION_IMAGE_CAPTURE to ACTION_GET_CONTENT.
 
## Privacy

No adds, no usertracking, no internet connection, free open source, available on f-droid

-----
