use amster;

SET foreign_key_checks = 0;

drop table table_log_file;
create table table_log_file(
	_id int NOT NULL AUTO_INCREMENT,
	short_name varchar(1000),
	real_file_name varchar(1000),
    output_file_name varchar(1000),
	description text,
	create_date date,
	PRIMARY KEY (_id)
)engine=innodb;

drop table table_file_info;
create table table_file_info(
	_id int NOT NULL AUTO_INCREMENT,
	remove_all_stacks boolean,
	exception_ignore_list varchar(5000),
	file_info2log_file int,
	PRIMARY KEY (_id),
	FOREIGN KEY( file_info2log_file) REFERENCES table_log_file(_id) ON DELETE CASCADE
)engine=innodb;

drop table table_thread_file;
create table table_thread_file(
	_id int NOT NULL AUTO_INCREMENT,
	thread_id varchar(500),
	thread_file varchar(1000),
	thread_info2log_file int NOT NULL,
	PRIMARY KEY (_id),
	FOREIGN KEY( thread_info2log_file) REFERENCES table_log_file(_id) ON DELETE CASCADE
)engine=innodb;


drop table table_time_log;
create table table_time_log(
	_id int NOT NULL AUTO_INCREMENT,
	heading varchar(5000),
    description text,
	log_time datetime,
    time_string varchar(200),
    heading_colour varchar(100),
    icon_colour varchar(100),
	time_log2log_file int,
	PRIMARY KEY (_id),
	FOREIGN KEY( time_log2log_file) REFERENCES table_log_file(_id) ON DELETE CASCADE
)engine=innodb;

drop table table_top_queries;
create table table_top_queries(
	_id int NOT NULL AUTO_INCREMENT,
	query_sql text,
    elapsed_time float,
	query_time varchar(100),
    query_order int,
	query2log_file int,
	PRIMARY KEY (_id),
	FOREIGN KEY( query2log_file) REFERENCES table_log_file(_id) ON DELETE CASCADE
)engine=innodb;

drop table table_exception;
create table table_exception(
	_id int NOT NULL AUTO_INCREMENT,
	exception varchar(5000),
    stack_trace text,
    exception_count int,
	exception2log_file int,
	PRIMARY KEY (_id),
	FOREIGN KEY( exception2log_file) REFERENCES table_log_file(_id) ON DELETE CASCADE
)engine=innodb;

drop table table_exception_times;
create table table_exception_times(
	_id int NOT NULL AUTO_INCREMENT,
	exception_time varchar(100),
	exception_time2exception int,
	PRIMARY KEY (_id),
	FOREIGN KEY( exception_time2exception) REFERENCES table_exception(_id) ON DELETE CASCADE
)engine=innodb;

drop table table_diag_info;
create table table_diag_info(
	_id int NOT NULL AUTO_INCREMENT,
	diag_name varchar(500),
    high_water_mark int,
    high_water_time varchar(100),
	diag2log_file int,
	PRIMARY KEY (_id),
	FOREIGN KEY( diag2log_file) REFERENCES table_log_file(_id) ON DELETE CASCADE
)engine=innodb;

drop table table_diag;
create table table_diag(
	_id int NOT NULL AUTO_INCREMENT,
    diag_value int,
    diag_time varchar(100),
	diag2diag_info int,
	PRIMARY KEY (_id),
	FOREIGN KEY( diag2diag_info) REFERENCES table_diag_info(_id) ON DELETE CASCADE
)engine=innodb;


create or replace view view_log_file as 
select * from table_log_file
order by create_date;

create or replace view view_time_log as 
select * from table_time_log
order by log_time;

create or replace view view_thread_file as 
select * from table_thread_file;

create or replace view view_top_queries as 
select * from table_top_queries
order by query_order;

SET foreign_key_checks = 1;
