# SimpleSagaManager
The SimpleSagaManager implementation provides the following behavior.

Completed | Failed | addParticipant | completeSaga | failSaga
----------|--------|----------------|--------------|---------
false | false | Allowed | Allowed | Allowed
true | false | Allowed | Allowed (NoOp) | Not Allowed (No Changes)
false | true | Allowed | Not Allowed (No Changes) | Allowed (Compensating Actions)
true | true | Allowed (May result in Completed=false) | Not Allowed (No Changes) | Allowed (NoOp)