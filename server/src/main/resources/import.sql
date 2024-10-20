insert into f_exam (e_pin,
                    e_state,
                    e_actual_end,
                    e_actual_start,
                    e_id,
                    e_planned_end,
                    e_planned_start,
                    e_screencapture_interval,
                    e_title)
values (123,
        1,
        null,
        '2024-10-17 10:01.120',
        nextval('f_exam_seq'),
        '2024-10-17 12:30.100',
        '2024-10-17 10:00.000',
        5,
        'test');
insert into f_examinee (e_id,
                        e_firstname,
                        e_lastname)
values (nextval('f_examinee_seq'),
        'Max',
        'Mustermann');
insert into f_participation (p_exam,
                             p_examinee,
                             p_id)
values (currval('f_exam_seq'),
        currval('f_examinee_seq'),
        gen_random_uuid()); -- gen_random_uuid() => postgres only extension

insert into f_connection_state (cs_is_connected,
                                cs_id,
                                cs_ping_timestamp,
                                cs_participation_id)
values (false,
        nextval('f_connection_state_seq'),
        '2024-10-17 10:10.120',
        (select p_id from f_participation where p_exam = currval('f_exam_seq'))
);

insert into f_connection_state (cs_is_connected,
                                cs_id,
                                cs_ping_timestamp,
                                cs_participation_id)
values (true,
        nextval('f_connection_state_seq'),
        '2024-10-17 10:20.120',
        (select p_id from f_participation where p_exam = currval('f_exam_seq'))
       );

SELECT setval('f_examinee_seq', 100);
SELECT setval('f_exam_seq', 100);
select setval('f_connection_state_seq', 100);
