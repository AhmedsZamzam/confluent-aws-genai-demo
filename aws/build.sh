#!/usr/bin/env sh

if [ "$1" == "help" ]; then
  echo "==> Usage: ./build.sh <command>"
  echo "==> Commands:"
  echo "==>   help: Prints this help message"
  echo "==>   push: Pushes the images to ECR without building them"
  echo "==>   no command: Builds and pushes the images to ECR"
  exit 1
fi

if [  -f ./build.log ]; then
  rm -f ./build.log
fi

if [ -f "env.sh" ]; then
    source ./env.sh
fi

pwd=$(aws ecr get-login-password --region us-east-1 --profile csid)
echo $pwd | docker login --username AWS --password-stdin 635910096382.dkr.ecr.us-east-1.amazonaws.com
if [ $? -ne 0 ]; then
  echo "==> Login failed"
  exit 1
fi

mvn clean install

current_version=$(cat ./app.version)
next_version=$(echo $current_version | awk -F. -v OFS=. 'NF==1{print ++$NF}; NF>1{if(length($NF+1)>length($NF))$(NF-1)++; $NF=sprintf("%0*d", length($NF), ($NF+1)%(10^length($NF))); print}')

update_version() {
  echo "==> Successfully pushed version $1"
  echo $1 > ./app.version
  echo "Updated app.version to $1n"

  git add ./app.version
  git commit -m "Updated app.version to $1"
  git push
}

build() {
  image_name="$2"
  folder="$1"
  echo "==> Building image $image_name from folder $folder"
  cd ./$folder/
  docker build --platform linux/amd64 -t $image_name -f Dockerfile .
  if [ $? -ne 0 ]; then
      echo "==> Build failed"
      exit 1
  fi
  cd ..

  echo "==> Successfully built image $image_name"
}

push() {
  image_name="$1"
  echo "==> Pushing image $image_name"

  docker tag $image_name 635910096382.dkr.ecr.us-east-1.amazonaws.com/$image_name
  if [ $? -ne 0 ]; then
    echo "==> Tag failed"
    exit 1
  fi

  docker push 635910096382.dkr.ecr.us-east-1.amazonaws.com/$image_name
  if [ $? -ne 0 ]; then
    echo "==> Push failed"
    exit 1
  fi

  echo "==> Successfully pushed image $image_name"
}

command=$1
if [ "$command" = "push" ]; then
  next_version=$current_version
fi

echo "==> Current version: $current_version"
echo "==> Next version: $next_version"

WEBAPP_IMAGE_NAME="csp-demo-webapp:$next_version"
CHATBOT_IMAGE_NAME="csp-demo-chatbot:$next_version"
SUMMARY_IMAGE_NAME="csp-demo-summary:$next_version"
SUBMIT_IMAGE_NAME="csp-demo-submit:$next_version"

if [[ "$(docker images -q $WEBAPP_IMAGE_NAME 2> /dev/null)" == "" ]]; then
  build app $WEBAPP_IMAGE_NAME
fi
push $WEBAPP_IMAGE_NAME

if [[ "$(docker images -q $CHATBOT_IMAGE_NAME 2> /dev/null)" == "" ]]; then
  build bot $CHATBOT_IMAGE_NAME
fi
push $CHATBOT_IMAGE_NAME

if [[ "$(docker images -q $SUMMARY_IMAGE_NAME 2> /dev/null)" == "" ]]; then
  build summary $SUMMARY_IMAGE_NAME
fi
push $SUMMARY_IMAGE_NAME

if [[ "$(docker images -q $SUBMIT_IMAGE_NAME 2> /dev/null)" == "" ]]; then
  build submit $SUBMIT_IMAGE_NAME
fi
push $SUBMIT_IMAGE_NAME

if [ "$command" != "push" ]; then
  update_version $next_version
fi

echo "==> Done"

