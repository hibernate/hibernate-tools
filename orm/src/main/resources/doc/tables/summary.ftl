<#--
~ Copyright 2010 - 2025 Red Hat, Inc.
~
~ Licensed under the Apache License, Version 2.0 (the "License");
~ you may not use this file except in compliance with the License.
~ You may obtain a copy of the License at
~
~     http://www.apache.org/licenses/LICENSE-2.0
~
~ Unless required by applicable law or agreed to in writing, software
~ distributed under the License is distributed on an "AS IS" basis,
~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
~ See the License for the specific language governing permissions and
~ limitations under the License.
-->
<#import "/doc/common.ftl" as common>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
	<head>
		<title>Hibernate Mappings - Table Summary</title>
		<link rel="stylesheet" type="text/css" href="${docFileManager.getRef(docFile, docFileManager.getCssStylesDocFile())}" title="Style"/>
	</head>
	<body>

		<@common.header selected="tables"/>

		<h1>Hibernate Mapping Documentation</h1>

		<#if graphsGenerated>
			<p>
				<img src="tablegraph.png" alt="Table Graph" usemap="#tablegraph"/>
				<map name="tablegraph">
					${tablegrapharea}
				</map>
			</p>
		</#if>

		<table>
			<thead>
				<tr>
					<th class="MainTableHeading">
						Schemas
					</th>
				</tr>
			</thead>
			<tbody>
				<#list dochelper.tablesBySchema.keySet() as schema>
					<tr>
						<td>
							<a href="${docFileManager.getRef(docFile, docFileManager.getSchemaSummaryDocFile(schema))}" target="generalFrame">
								${schema}
							</a>
						</td>
					</tr>
				</#list>
			</tbody>
		</table>
		
	</body>
</html>
