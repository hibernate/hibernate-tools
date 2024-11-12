<#if ejb3>
  <#if pojo.hasIdentifierProperty()>
    <#if field.equals(clazz.identifierProperty)>
${pojo.generateAnnIdGenerator()}<#--has space prepended-->
    </#if>
  </#if>
  <#if c2h.isOneToOne(field)>
    ${pojo.generateOneToOneAnnotation(field, md)}
  <#elseif c2h.isManyToOne(field)>
    ${pojo.generateManyToOneAnnotation(field)}
  <#--TODO support optional and targetEntity-->
${pojo.generateJoinColumnsAnnotation(field, md)}
  <#elseif c2h.isCollection(field)>
    ${pojo.generateCollectionAnnotation(field, md)}
  <#else>
    <#if pojo.generateBasicAnnotation(field)?has_content >
      <#if pojo.generateBasicAnnotation(field)?trim?length gt 0 >
${(pojo.generateBasicAnnotation(field))}<#--has space prepended-->
      </#if>
    </#if>
${pojo.generateAnnColumnAnnotation(field)}<#--has space prepended-->
  </#if>
</#if>
