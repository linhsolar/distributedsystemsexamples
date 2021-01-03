#!/usr/bin/env python
import random
import time
import pika, os, logging, shlex, subprocess
import json
import argparse
# this program just simply listens the report queue and if it finds that
# a response_time is larger than a threshold, it calls a new worker
parser = argparse.ArgumentParser()
#the name of report queue, we can set it by default, e.g., "DST_REPORT"
#we can use only 1 queue for all reports from all app_id
parser.add_argument('--report_queuename', help='the name of report queue')
#we use a direct exchange in rabbitmq so we dont need to declare the name of the exchange
args = parser.parse_args()
#provide the link of AMQP
amqpLink=os.environ.get('AMQPURL', 'amqp://test:test@localhost')
params = pika.URLParameters(amqpLink)
params.socket_timeout = 5
connection = pika.BlockingConnection(params) # Connect to AMQP, We test with CloudAMQP
channel = connection.channel()
channel.queue_declare(queue=args.report_queuename, durable=True)
# create a function which is called for upload request
# this should be done using complex event processing and parallel task
# but I just show the simple logic
def monitor_callback(ch, method, properties, body):
  #the input data is followed the json format, for example
  # [{"response_time":"time","app_id":app_id}]
  #
  #
  print "[report_monitor] received: "+body
  report_message = json.loads(body)

  print " [report_monitor] just call a simple check"
  check_report_time(report_message)

#assume that the threshold is 50 seconds
#instead of checking report message, we can also turn this report_message
#into a request message that asks this program to call a new worker
UPLOAD_TIME_THRESHOLD=30
def   check_report_time(report_message):
    response_time=report_message['response_time']
    app_id =report_message['app_id']
    #of course you need to think a lot about
    #when a new container should be lauched.
    #it does not make sense to run a new container
    #for simply checking one single event.
    if (response_time > UPLOAD_TIME_THRESHOLD):
        call_worker(app_id)
    #else: one can check if the load is low, then
    #call remove_worker(app_id)
def call_worker(app_id):
    print  "[report_monitor] calls the worker of " +app_id
    print  " [report_monitor] assume that the worker container of the " + app_id + " is " +app_id+"_worker_container/dst2018:0.1"
    #this is related to the convention. we need to make sure it work
    container_name=app_id+"_worker_container/dst2018:0.1"
    #we assume the upload queue is app_id_upload
    #we aslo assume that the AMQPURL has been set using ENV
    cmd="docker run --env AMQPURL="+amqpLink+" -it --rm "+container_name + " python direct_subscriber_worker.py  --app_id "+app_id+"  --upload_queuename "+app_id+"_upload --report_queuename "+args.report_queuename
    print "[report_monitor}: calls " + cmd
    #just create new container, no remove. you need to think about
    #management of a list of running containers then remove containers
    docker_params = shlex.split(cmd)
    #you need to change and manage containers. Otherwise, after few requests
    #your machines might be dead with
    newpid = os.fork()
    if newpid == 0:
         proc_container=subprocess.Popen(docker_params)
         proc_container.wait()

def remove_worker(app_id):
    print "[report_monitor] removes workers but no implementation"
# set up subscription on the queue
channel.basic_consume(monitor_callback,
                      queue=args.report_queuename,
    no_ack=True)

channel.start_consuming() # start consuming (blocks)

connection.close()
