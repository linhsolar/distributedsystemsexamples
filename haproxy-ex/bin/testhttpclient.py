from urlparse import urlparse
from threading import Thread
import httplib, sys

def readServer():
    url=urlparse("http://localhost:8000")
    client=httplib.HTTPConnection(url.netloc)
    client.request("GET",url.path)
    print client.getresponse().read()
    
for i in range(10):
    thread=Thread(target=readServer)
    thread.start()
