DROP TABLE FLUSHTEST1;
DROP TABLE FLUSHTEST2;

CREATE TABLE FLUSHTEST1 (
	ID VARCHAR(3) PRIMARY KEY NOT NULL,
	NAME VARCHAR(5) NULL
);

CREATE TABLE FLUSHTEST2 (
	ID VARCHAR(3) PRIMARY KEY NOT NULL,
	NAME VARCHAR(5) NULL
);

commit;

quit;
