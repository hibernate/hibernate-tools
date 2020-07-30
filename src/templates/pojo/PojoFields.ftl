<#-- // Fields -->

<#foreach field in pojo.getAllPropertiesIterator()><#if pojo.getMetaAttribAsBool(field, "gen-property", true)><#if annotateFields ><#if pojo.hasFieldJavaDoc(field, javaDocFromDbComments)>    /**
     ${pojo.getFieldJavaDoc(field, 0, javaDocFromDbComments)}
     */
</#if><#assign property = field > <#-- Variable property is used in GetPropertyAnnotation.ftl-->  <#include "GetPropertyAnnotation.ftl"/></#if>${pojo.getFieldModifiers(field)} ${pojo.getJavaTypeName(field, jdk5)} ${c2j.keyWordCheck(field.name)}<#if pojo.hasFieldInitializor(field, jdk5)> = ${pojo.getFieldInitialization(field, jdk5)}</#if>;
</#if>
</#foreach>
