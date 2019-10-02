insert into saga (identifier, failed, completed) values ('zero-participants', false, false);

insert into saga (identifier, failed, completed) values ('one-participant', false, false);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('one-participant-1', 'one-participant', false, 1, '{}');

insert into saga (identifier, failed, completed) values ('two-participants', false, false);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('two-participants-1', 'two-participants', false, 1, '{"uri":"1"}');
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('two-participants-2', 'two-participants', false, 2, '{"uri":"2"}');

insert into saga (identifier, failed, completed) values ('completed', false, true);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('completed-1', 'completed', false, 1, '{}');

insert into saga (identifier, failed, completed) values ('completed-2', false, true);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('completed-2-1', 'completed-2', false, 1, '{}');

insert into saga (identifier, failed, completed) values ('failed-notcompleted', true, false);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('failed-notcompleted-1', 'failed-notcompleted', false, 1, '{}');

insert into saga (identifier, failed, completed) values ('failed-completed', true, true);
insert into participant (identifier, saga_id, fail_completed, order_index, action_definition) values ('failed-completed-1', 'failed-completed', true, 1, '{}');