# RestTemplateExecutor
The RestTemplateExecutor communicates compensating actions via HTTP(S). The default
behavior autowires in an existing RestTemplate and uses it for web connections.

## Default Behavior
- Executes only on actions with a URI scheme of "http" or "https".
- Uses the URI as the destination for the outbound web request.
- Uses the method as the HTTP method.
- All headers defined will be added as HTTP headers on the outbound request.