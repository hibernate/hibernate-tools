<#-- // InnerClasses -->

<#foreach inner in pojo.getInnerClasses()>
     ${exporter.exportInner(pojo, inner)}
</#foreach>
