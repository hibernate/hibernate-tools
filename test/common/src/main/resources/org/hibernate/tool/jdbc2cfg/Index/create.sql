CREATE TABLE WITH_INDEX (ONE INT, TWO INT, THREE INT)
CREATE INDEX MY_INDEX ON WITH_INDEX(ONE,THREE)
CREATE UNIQUE INDEX OTHER_IDX on WITH_INDEX(THREE)
