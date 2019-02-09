<#-- // Property accessors -->
<#foreach property in pojo.getAllPropertiesIterator()>
<#if pojo.getMetaAttribAsBool(property, "gen-property", true)>
<#if !annotateFields ><#if pojo.hasFieldJavaDoc(property, javaDocFromDbComments) >
    /**       
      ${pojo.getFieldJavaDoc(property, 4, javaDocFromDbComments)}
     */
</#if>
    <#include "GetPropertyAnnotation.ftl"/></#if>
    ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5)} ${pojo.getGetterSignature(property)}() {
        return this.${c2j.keyWordCheck(property.name)};
    }
    
    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5)} ${c2j.keyWordCheck(property.name)}) {
        this.${c2j.keyWordCheck(property.name)} = ${c2j.keyWordCheck(property.name)};
    }
</#if>
</#foreach>
