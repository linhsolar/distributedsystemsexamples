#!/usr/bin/env python
import random
import time
import pika, os, logging
import json
import argparse
# uploading worker
#a simple program reads requests from a queue, pretend to upload
#data and then report the time.
#very simple, no debug, no test
parser = argparse.ArgumentParser()
parser.add_argument('--app_id',help='provide the application id, such as a unique student id')
#the name of upload queue, we can set it by default, e.g. "app_id_UPLOAD"
parser.add_argument('--upload_queuename', help='the name of upload queue')
#the name of report queue, we can set it by default, e.g., "DST_REPORT"
#we can use a queue for all reports from all app_id
parser.add_argument('--report_queuename', help='the name of report queue')
#we use a direct exchange in rabbitmq so we dont need to declare the name of the exchange
args = parser.parse_args()
#provide the link of AMQP
amqpLink=os.environ.get('AMQPURL', 'amqp://test:test@localhost')
#print ("We will connect to", amqpLink)
params = pika.URLParameters(amqpLink)
params.socket_timeout = 5
connection = pika.BlockingConnection(params) # Connect to AMQP, We test with CloudAMQP
channel = connection.channel()
channel.queue_declare(queue=args.upload_queuename,durable=False)
channel.queue_declare(queue=args.report_queuename, durable=True)
print "[uploading_worker] from " + args.app_id
# create a function which is called for upload request
def upload_callback(ch, method, properties, body):
  #the input data is followed the json format, for example
  # [{"type":"video","uri":"http://abc.xyz","app_id":app_id}]
  #
  #
  upload_request = json.loads(body)
  print "[uploader_worker] Received: "+body
  print "[uploader_worker] Just pretend to upload the file"
  upload(upload_request)
#assume that max upload time is 60 seconds
UPLOAD_TIME_MAX=60
UPLOAD_TIME_MIN=30
def   upload(upload_request):
    print(upload_request)
    response_time =random.randint(UPLOAD_TIME_MIN,UPLOAD_TIME_MAX)
    time.sleep(response_time)
    print "[uploading_worker] Upload done. Now reporting the time"
    notify_result(response_time)
def notify_result(response_time):
    print "[uploading_worker] I just send a notification to the report queue"
    report ={"response_time":response_time,"app_id":args.app_id}
    report_message=json.dumps(report)
    channel.basic_publish(exchange='',routing_key=args.report_queuename,
                      body=report_message)

# listen the upload_queue
channel.basic_consume(upload_callback,
                      queue=args.upload_queuename,
    no_ack=True)
# start consuming requests from the upload queue (blocking)
channel.start_consuming()

connection.close()
