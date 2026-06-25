import urllib.request, urllib.parse, urllib.error
import json
import http.cookiejar

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# 1. We don't know the exact login credentials, but we can bypass or just try.
# Wait, can we just look at the server logs? Where does Spring Boot log to?
# Usually console. If running in IDE, it's not written to a file unless configured in application.properties.
