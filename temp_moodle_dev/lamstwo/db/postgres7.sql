# This file contains a complete database schema for all the 
# tables used by this module, written in SQL

# It may also contain INSERT statements for particular data 
# that may be used, especially new entries in the table log_display

CREATE TABLE prefix_lamstwo (
  id SERIAL PRIMARY KEY,
  course integer NOT NULL default '0',
  name varchar(255) NOT NULL default '',
  introduction text NOT NULL default '',
  sequence_id integer NOT NULL default '0',
  lesson_id integer NOT NULL default '0',
  timemodified integer NOT NULL default '0'
);  

CREATE INDEX prefix_lamstwo_course_idx ON prefix_lamstwo (course);

