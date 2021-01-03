# A suggestion for implementing a simple control for learning purpose
We assume that each type of requests will take an upload_time
in range(UPLOAD_TIME_MIN, UPLOAD_TIME_MAX)

for example,
- UPLOAD_TIME_MIN for video = 2 seconds
- UPLOAD_TIME_MAX for video = 20 seconds

we can consider that

- a finished request is SLOW if its
         upload_time >= UPLOAD_TIME_MAX - 20 % (UPLOAD_TIME_MAX-UPLOAD_TIME_MIN)

- a finished request is FAST if its
        upload_time <= UPLOAD_TIME_MIN +20% * (UPLOAD_TIME_MAX-UPLOAD_TIME_MIN)

- otherwise, a finished request is normal.

Through the monitoring we will receive

- number requests waiting in the queue: nr_requests
- the list of finished requests and their upload_time

Let us say, if we have K parallel uploaders/workers, then the maximum
end-to-end response time for a request to be finished  
will be

max_request_response_time = (queuing time + upload_time)
= (nr_requests/K  - 1)* UPLOAD_TIME_MAX + UPLOAD_TIME_MAX 
= (nr_requests/K) * UPLOAD_TIME_MAX


(assume we have zero latency and worst case scenario).

We can make assumption that the max_request_response_time can be fairly
estimated based on the average of upload_time of normal finished requests

max_request_response_time =avg(upload_time) * nr_requests/K
for  normal finished requests.
Thus, we can develop a simple control that makes sure:

- max_request_response_time is in the normal range by controlling K
- you adjust the K for every sliding window of  M finished requests.
- M can be configured, e.g., 5
