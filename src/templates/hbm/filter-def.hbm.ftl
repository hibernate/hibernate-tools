<#foreach filterKey in cfg.filterDefinitions.keySet()>
<#assign filterDef = cfg.filterDefinitions.get(filterKey)>
    <filter-def name="filterKey">
<#foreach filterParaName in filterDef.parameterNames>
	<filter-param name="${filterParaName}" type="${filterDef.getParameterType(filterParaName).name}" />
</#foreach>
    </filter-def>    
</#foreach>