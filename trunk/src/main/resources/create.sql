create table RESULT (id integer identity primary key, symbol varchar(100) not null, name varchar(200) not null, execution_date timestamp not null,quote_date timestamp not null, change double not null, price_change double not null, window_size integer not null, quote_count integer not null)
create table POSITION (id integer identity primary key, result_id integer not null references RESULT(id) on delete cascade, open_date timestamp not null, close_date timestamp not null, change double not null)