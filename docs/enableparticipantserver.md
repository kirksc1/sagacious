# @EnableParticipantServer
The Participant Server, when enabled, autowires in service endpoints necessary for supporting
remote participant interactions.  At this time, the following endpoints are exposed:
- Add Participant (POST "${sagacious.server.endpoints.web.base-path}/sagas/[sagaId]/participants")
    - Adds a participant to the saga and along with its compensating action definition

## Configuration Properties
Property | Description | Default | Sample
--- | --- | --- | ---
sagacious.server.endpoints.web.base-path | The base path for all server endpoints | "" | "/test"
sagacious.server.endpoints.add-participant.enabled | Control whether the Add Participant endpoint is enabled | true | false