#Build Server Project

You can configure project to run inside IDE, in this case just start embedded tomcat or other web server and check that end point is available
You can also run mvn clean install in root project directory and copy war file from target directory of project to your web server

#Build Client project

Check the Rest end point URI, if different from OrderClient.REST_URI update it with correct end point. Start main method see output.

#Client flow
Client flow is described in  OrderClient in comments

#POST-PUT redirect issues    

it seems that java doesn't support PUT redirect GET pattern properly, issues is with 
```
sun.net.www.protocol.http.HttpURLConnection
``` 
It basically has a check for POST method only, and replaces POST with GET only in this case, all other methods will use original http method during redirection.

Line 1942: 
```
 if (this.method.equals("POST") && !Boolean.getBoolean("http.strictPostRedirect") && var2 != 307) {
                    this.requests = new MessageHeader();
                    this.setRequests = false;
                    super.setRequestMethod("GET");
                    this.poster = null;
                    if (!this.checkReuseConnection()) {
                        this.connect();
                    }
                }
 ```
so in my case I send original request to PUT 
```
**http://localhost:8080/rest/orders/pending/81039426-f374-4803-ae3a-95d81801b3b0**
``` 
and return location 
```
**http://localhost:8080/rest/orders/81039426-f374-4803-ae3a-95d81801b3b0**
``` 
and because of PUT request it tries to redirect to 
```
**http://localhost:8080/rest/orders/81039426-f374-4803-ae3a-95d81801b3b0**
 ```
using PUT method, so it cannot find one and gives me an error, 
in case of POST all works as expected. To resolve issue I hade to turn off redirect option in client
```
   property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE)
```
When using POSTMAN, it might be an issue, as POSTMAN always follow redirect, to make it work in POSTMAN interceptor should be configured
```
   https://www.getpostman.com/docs/postman/sending_api_requests/capturing_http_requests
```
    