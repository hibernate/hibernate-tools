<#foreach item in ctx?keys>	
<#-- ${templates.create("generictemplates/pojo/generic-content", item + ".txt")}  -->
<#assign captured><#include "generic-content.ftl"/></#assign>
${templates.createFile(captured, "${item}.txt")}
</#foreach>
