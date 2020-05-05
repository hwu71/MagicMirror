# DIY Mirror


## Description:

We forked the open-source MagicMirror2 project and extended it to enable multiple users to customize their module configuration, and use voice command as well as recognition to adjust the display content corresponding to the users. Therefore, we call our project DIY Mirror.


## Implementation:

Our extension mainly focuses on the configuration file update, face recognition, and Google Assistant support. Their implementation are stored in "config", "modules/third_party/FaceRecognition", and "modules/third_party/GoogleAssistant" respectively. Currently, we support module customization of YouTube, COVID19, and Stock modules.


## Execution:

- Execute server-side code of MagicMirror2:
```sh
bash my_server_run.sh
```

- Execute client-side code of MagicMirror2:
```sh
bash my_client_run.sh
```

- Compile and run the configuration reloading request server:
```sh
cd config
make
java ReloadConfigRequestServer
```

- Compile and run the photo collection and webpage refresh request server:
```sh
cd modules/third_party/FaceRecognition
make
. my_import.sh
java PhotoCollectionAndRefreshRequestServer
```

- Run the Google Assistant support:
```sh
cd modules/third_party/GoogleAssistant/google-assistant-sdk/googlesamples/assistant/grpc
. my_import.sh
python pushtotalk.py
```


## Submodules:

In this project, we added several GitHub repositories as submodules in "modules/third_party". "MMM-Stock", "MMM-EmbedYoutube", and "MMM-COVID19" are third-party modules developed by the MagicMirror developer community. "GoogleAssistant" is a forked GitHub repository with our extension of face recognition voice trigger.


## References:

Concerning the usage of OpenCV-based face recognition library in "build_face_dataset.py", "encode_faces.py", and "pi_face_recognition.py" under path "modules/third_party/FaceRecognition", we referred to the tutorial on the following websites:

- Install OpenCV on the Raspberry Pi: https://www.pyimagesearch.com/2018/09/26/install-opencv-4-on-your-raspberry-pi/ 
- Build the face recognition dataset: https://www.pyimagesearch.com/2018/06/11/how-to-build-a-custom-face-recognition-dataset/
- Raspberry Pi Face Recognition: https://www.pyimagesearch.com/2018/06/25/raspberry-pi-face-recognition/


## App Control:

To serve the DIY Mirror, we developed an Android App for users' registration and module update. Its GitHub link is provided below: https://github.com/joezie/SmartMirrorModuleCustomizationApp
