# Simple Dockerfile for building a container with python and pika
# library for AMQP. Then you can put your python code for processing
# messages from AMQP
# You can do a similar thing if you want to use Java and AMQP
# Build it: e.g., $docker build -t [app_id]_worker_container/dst2018:0.1 .
# where app_id is something unique, e.g., the student id
#Using python 2.7
FROM python:2.7-slim

# add all python code and sample from the current directory
ADD . /tmp/dst2018
# Install pika for amqp
RUN pip install pika
# make sure working directory
WORKDIR  /tmp/dst2018
# this environment variable can be changed when running docker with --env
ENV AMQPURL "amqp://guest:guest@pisamba"
#You can add other python commands to run. We dont as we will call the python using docker run
