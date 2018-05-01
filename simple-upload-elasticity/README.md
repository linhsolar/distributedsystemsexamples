# Introduction
This is just a simple example of using queues and tasks and docker to illustrate
some concepts of work load balancing and elasticity.
It is a kind of templates/mockup sample for students to learn and
develop real assignment.

This part of examples for various distributed systems/technologies courses
lectured by Hong-Linh Truong (http://www.infosys.tuwien.ac.at/staff/truong)

## Setup AMQP and Python

You need to have RabbitMQ and python Pika installed. The access to AMQP will be
given through a URL. For testing you can use local RabbitMQ or CloudAMQP.

## Export the AMQP URL
All code require AMQP URL.
For programs to send and receive information to/from RabbitMQ you can set up

$export AMQPURL="amqp://...

## Run the upload worker of a user (identified by app_id)
The Upload worker will listen the upload request queue and perform the upload.
We dont do the real upload but simulate the upload. You can add real upload, e.g.
using google storage (with Google Storage APIs)

To run the upload you can do:

$export AMQPURL=amqp://...
$python direct_subscriber_worker.py  --app_id e123456  --upload_queuename e123456_upload --report_queuename DSTREPORT

you can also build a docker container and then run the upload worker inside the
container

## Run the report monitor
This report monitor reads the response time of the uploading and decides if
new uploading worker should be run. Note that, we just illustrate the launch
of new workers but we do not implement the stop of existing workers.

$python simple_congestion_monitor.py --report_queuename DSTREPORT

One report monitor is enough for many client apps. Note that this report monitor
is just doing simple check and call docker run. You can change this report monitor
into two programs:
- a program analyzes response time, e.g., using complex event processing and
other techniques and then publish requests for creating new uploading workers
or removing existing uploading workers.
- a program listens the worker requests and performs adding or removing workers.

## Sending upload requests

You can modify the request list (in the json file) and run a client sending requests

$python direct_publisher.py --app_id e123456 --upload_queuename e123456_upload --input_data sample_upload_request.json

this just emulates a client sending requests

## Using docker

You can build a container with the worker. Remember to use the tag in the right way so that
the monitor program can call the docker
e.g

$docker build -t app_id_worker_container/dst2018:0.1 .
