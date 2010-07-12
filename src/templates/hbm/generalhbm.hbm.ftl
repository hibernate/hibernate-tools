<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC 
	"-//Hibernate/Hibernate Mapping DTD 3.0//EN"
	"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">

<hibernate-mapping>	
<!-- 
	Auto-generated mapping file from
	the hibernate.org cfg2hbm engine
	for General Global Setttings
-->

<#if c2h.isImportData(cfg)>
<#include "import.hbm.ftl">
</#if>
<#if c2h.isNamedQueries(cfg)>
<#include "query.hbm.ftl">
</#if>
<#if c2h.isNamedSQLQueries(cfg)>
<#include "sql-query.hbm.ftl">
</#if>
<#if c2h.isFilterDefinitions(cfg)>
<#include "filter-def.hbm.ftl">
</#if>

</hibernate-mapping>
