# USAGE
# python pi_face_recognition.py --cascade haarcascade_frontalface_default.xml --encodings encodings.pickle

# import the necessary packages
from imutils.video import VideoStream
from imutils.video import FPS
from collections import Counter
import face_recognition
import argparse
import imutils
import pickle
import time
import cv2
import socket
import os

# construct the argument parser and parse the arguments
ap = argparse.ArgumentParser()
ap.add_argument("-c", "--cascade", required=True,
	help = "path to where the face cascade resides")
ap.add_argument("-e", "--encodings", required=True,
	help="path to serialized db of facial encodings")
args = vars(ap.parse_args())

# load the known faces and embeddings along with OpenCV's Haar
# cascade for face detection
print("[INFO] loading encodings + face detector...")
#my_path='/home/pi/MagicMirror/modules/third_party/FaceRecognition/encodings.pickle'
#my_file = open(my_path, "rb")
data = pickle.loads(open(args["encodings"], "rb").read())
#data = pickle.loads(my_file.read())
detector = cv2.CascadeClassifier(args["cascade"])

# initialize the video stream and allow the camera sensor to warm up
print("[INFO] starting video stream...")
vs = VideoStream(src=0).start()
# vs = VideoStream(usePiCamera=True).start()
#time.sleep(1.0)

# start the FPS counter
#fps = FPS().start()

flag = 0
names = []
# loop over frames from the video file stream
while True:
	# grab the frame from the threaded video stream and resize it
	# to 500px (to speedup processing)
	frame = vs.read()
	frame = imutils.resize(frame, width=500)

	# convert the input frame from (1) BGR to grayscale (for face
	# detection) and (2) from BGR to RGB (for face recognition)
	gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
	rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)

	# detect faces in the grayscale frame
	rects = detector.detectMultiScale(gray, scaleFactor=1.1,
		minNeighbors=5, minSize=(30, 30),
		flags=cv2.CASCADE_SCALE_IMAGE)

	# OpenCV returns bounding box coordinates in (x, y, w, h) order
	# but we need them in (top, right, bottom, left) order, so we
	# need to do a bit of reordering
	boxes = [(y, x + w, y + h, x) for (x, y, w, h) in rects]

	# compute the facial embeddings for each face bounding box
	encodings = face_recognition.face_encodings(rgb, boxes)


	# loop over the facial embeddings
	for encoding in encodings:
		# attempt to match each face in the input image to our known
		# encodings
		matches = face_recognition.compare_faces(data["encodings"],
			encoding)
		name = "Unknown"

		# check to see if we have found a match
		if True in matches:
			# find the indexes of all matched faces then initialize a
			# dictionary to count the total number of times each face
			# was matched
			matchedIdxs = [i for (i, b) in enumerate(matches) if b]
			counts = {}

			# loop over the matched indexes and maintain a count for
			# each recognized face face
			for i in matchedIdxs:
				name = data["names"][i]
				counts[name] = counts.get(name, 0) + 1

			# determine the recognized face with the largest number
			# of votes (note: in the event of an unlikely tie Python
			# will select first entry in the dictionary)
			name = max(counts, key=counts.get)
			#print(name)
			flag += 1
			#names.append(name)


		# update the list of names
		#print(name)
		names.append(name)
		#print(names)

	# loop over the recognized faces
	#for ((top, right, bottom, left), name) in zip(boxes, names):
		# draw the predicted face name on the image
	#	cv2.rectangle(frame, (left, top), (right, bottom),
	#		(0, 255, 0), 2)
	#	y = top - 15 if top - 15 > 15 else top + 15
	#	cv2.putText(frame, name, (left, y), cv2.FONT_HERSHEY_SIMPLEX,
	#		0.75, (0, 255, 0), 2)

	# display the image to our screen
	#cv2.imshow("Frame", frame)
	#key = cv2.waitKey(1) & 0xFF

	# if the `q` key was pressed, break from the loop
	#if key == ord("q"):
	#	break
	#print("while loop again")
	if flag >= 10:
		break
	# update the FPS counter
	#fps.update()

# stop the timer and display FPS information
#fps.stop()
#print("[INFO] elasped time: {:.2f}".format(fps.elapsed()))
#print("[INFO] approx. FPS: {:.2f}".format(fps.fps()))
#print(names)
#print(Counter(names).most_common(1)[0][0])

appear_times = {}
for label in names:
	if label in appear_times:
		appear_times[label] += 1
	else:
		appear_times[label] = 1

most_common = max(appear_times, key=lambda x: appear_times[x])
print(appear_times)
print(most_common)

# connect to cloud server (34.69.18.117:2540)
connection_to_server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
connection_to_server.connect(("34.69.18.117", 2540))

# send a messsage with LOGIN header + username's length + username to the server
loginHeaderAndUsernameLength = (str(2) + '\n' + str(len(most_common)) + '\n').encode('utf-8')
connection_to_server.send(loginHeaderAndUsernameLength)
totalsent = 0
while totalsent < len(most_common):
	sent = connection_to_server.send(most_common[totalsent:].encode('utf-8'))
	if sent == 0:
		raise RuntimeError("Send reload config request failed")
	totalsent = totalsent + sent
	
# receive a confirmation message from server
success_status = connection_to_server.recv(1)
print("success status:") #####################
print(success_status) ###################
if success_status[0] == b'\x01':
	os.system("bash /home/pi/MagicMirror/modules/third_party/FaceRecognition/my_refresh.sh")
	print("Refreshed smart mirror!")
else:
	print("Failed to reload config on cloud server")


# do a bit of cleanup
cv2.destroyAllWindows()
vs.stop()
