<#-- // Property accessors -->
<#list pojo.getAllPropertiesIterator() as property>
  <#if pojo.getMetaAttribAsBool(property, "gen-property", true)>
    <#if pojo.hasFieldJavaDoc(property)>
    /**
     * ${pojo.getFieldJavaDoc(property, 4)}
     */
    </#if>

    <#if !annotateField??><#include "GetPropertyAnnotation.ftl"/></#if><#-- Property annotations. Field annotation configured in PojoFields.ftl -->
    ${pojo.getPropertyGetModifiers(property)} ${pojo.getJavaTypeName(property, jdk5)} ${pojo.getGetterSignature(property)}() {
        return this.${c2j.keyWordCheck(property.name)};
    }

    ${pojo.getPropertySetModifiers(property)} void set${pojo.getPropertyName(property)}(${pojo.getJavaTypeName(property, jdk5)} ${c2j.keyWordCheck(property.name)}) {
        this.${c2j.keyWordCheck(property.name)} = ${c2j.keyWordCheck(property.name)};
    }
  </#if>
</#list>
