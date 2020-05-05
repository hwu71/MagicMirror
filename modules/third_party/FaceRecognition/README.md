# Compile
```sh
make
```
# Execute
```sh
. my_import.sh
java PhotoCollectionAndRefreshRequestServer
```
* Please use Ctrl+C to interrupt the program, so that Server could shut down properly (otherwise, the port that the server socket binded to might not be freed)

# Description 
* "PhotoCollectionAndRefreshRequestServer.java" implements a photo collection and webpage refresh request server that listen to port 2540
* "PhotoCollectionAndRefreshRequestHandler.java" implements a thread that serves a client
* "build_face_dataset.py" captures images from video stream and stores as face dataset of the given user
* "encode_faces.py" trains the OpenCV face recognition model with the face dataset and generates a encoding pickle
* "pi_face_recognition.py" performs OpenCV face recognition and returns the recognized user
* "my_register.sh" runs "build_face_dataset.py"
* "my_encode.sh" runs "encode_faces.py"
* "my_recog_run.sh" runs "pi_face_recognition.py"
* "my_refresh.sh" sends a Ctrl-R signal to DIY Mirror to refresh the webpage
* "my_import.sh" activates the virtual environment and prepares for OpenCV operations

## References:
Concerning the usage of OpenCV-based face recognition library in "build_face_dataset.py", "encode_faces.py", and "pi_face_recognition.py", we referred to the tutorial on the following websites:

- Install OpenCV on the Raspberry Pi: https://www.pyimagesearch.com/2018/09/26/install-opencv-4-on-your-raspberry-pi/ 
- Build the face recognition dataset: https://www.pyimagesearch.com/2018/06/11/how-to-build-a-custom-face-recognition-dataset/
- Raspberry Pi Face Recognition: https://www.pyimagesearch.com/2018/06/25/raspberry-pi-face-recognition/
