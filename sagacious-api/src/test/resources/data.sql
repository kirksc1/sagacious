insert into saga (identifier, failed, completed) values ('zero-participants', false, false);

insert into saga (identifier, failed, completed) values ('one-participant', false, false);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('one-participant-1', 'one-participant', false, 1, '{}');

insert into saga (identifier, failed, completed) values ('two-participants', false, false);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('two-participants-1', 'two-participants', false, 1, '{"uri":"1"}');
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('two-participants-2', 'two-participants', false, 2, '{"uri":"2"}');

insert into saga (identifier, failed, completed) values ('completed', false, true);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('completed-1', 'completed', false, 1, '{}');

insert into saga (identifier, failed, completed) values ('failed', true, false);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('failed-1', 'failed', false, 1, '{}');