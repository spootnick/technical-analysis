create table RESULT (id integer identity primary key, symbol varchar(100) not null, create_time timestamp not null, first_quote timestamp not null,change double not null, price_change double not null, window_size integer not null, quote_count integer not null);