# RestTemplateExecutor
The RestTemplateExecutor communicates compensating actions via HTTP(S). The default
behavior autowires in an existing RestTemplate and uses it for web connections.

## Default Behavior
- Executes only on actions with a URI scheme of "http" or "https".
- Uses the URI as the destination for the outbound web request.
- Chooses the HttpMethod according to the following:
    - If an attribute with the name "http.method" is provided, then resolve it to an HttpMethod
    - If a body is provided, then HttpMethod.POST
    - If a body is not provided, then HttpMethod.DELETE
- All headers defined will be added as HTTP headers on the outbound request.