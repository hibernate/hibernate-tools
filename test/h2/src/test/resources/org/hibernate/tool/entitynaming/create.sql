CREATE TABLE "orders" ( "ordId" INT generated by default as identity (start with 1) NOT NULL, "ordDesc" VARCHAR(10), "ordVersion" TINYINT DEFAULT 0 NOT NULL, PRIMARY KEY ("ordId"))
CREATE TABLE "orderItems" ( "oiId" INT generated by default as identity (start with 1) NOT NULL, "oiOrdId" INT NOT NULL, "oiDesc" VARCHAR(10), "oiVersion" TINYINT DEFAULT 0 NOT NULL, PRIMARY KEY ("oiId"))
ALTER TABLE "orderItems" ADD CONSTRAINT "orderorderitemfk" FOREIGN KEY ("oiOrdId") REFERENCES "orders" ("ordId") ON DELETE CASCADE ON UPDATE CASCADE
CREATE TABLE "dummy" ( "duId" INT generated by default as identity (start with 1) NOT NULL, "duVersion" TINYINT DEFAULT 0 NOT NULL, PRIMARY KEY ("duId"))