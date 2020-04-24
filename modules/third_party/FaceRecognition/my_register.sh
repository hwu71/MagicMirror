#!/bin/bash
#Usage: my_register.sh user_name

echo "Register.."
if [ $# != 1 ]; then
  echo "Error arguments number: $#"
  echo "Usage: bash $0 user_name"
  exit 1
fi
user_name=$1
#cd "/home/pi/pi-face-recognition"
path="dataset/"
if [ ! -d "${path}" ]; then
  echo -e "Directory ${path} DOES NOT exist, create a ${path} directory...\n"
  mkdir ${path}
fi

if [ ! -d "${path}${user_name}" ]; then
  echo -e "Directory ${user_name} DOES NOT exist, create a ${user_name} directory...\n"
else 
  echo -e "Directory ${user_name} ALREADY exists, rewrite the ${user_name} directory...\n"
  rm -rf ${path}${user_name} 
fi
mkdir ${path}${user_name}
echo -e "Executing:  ${user_name} "
python build_face_dataset.py --cascade haarcascade_frontalface_default.xml --output ${path}${user_name}
