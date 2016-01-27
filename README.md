# Radix
The Radix class can be used as a ColdFusion Java cfx taq or as a Java POJO
  - Supports conversion from one Radix base to another using BASE2 thru BASE32, BASE62, and BASE72
  - Supports BASEALPHA A-Za-z
  - Arbitrary base string of unique characters

### Version
1.0
### Installation

- Copy the Radix.jar to
your cfusion lib directory (i.e. C:\ColdFusion11\cfusion\lib)
- In your ColdFusion Administrator->Extensions->CFX Tags: add cfx_radix with the Class Name "com.intersuite.Radix"

### Usage

CFX Tag
```html
<cfx_radix value="A8E2A5E989E54EBE354F93D6A7194815" sourcebase="BASE16" targetbase="BASE75" variable="foo">
<cfoutput>#foo#</cfoutput>
Alternately... leave off the variable and it will be output by the tag.
<cfx_radix value="A8E2A5E989E54EBE354F93D6A7194815" sourcebase="BASE16" targetbase="BASE75">
```
JAVA in CF (Example using the loader cfc)
```html
<cfset paths = ArrayNew(1)>
<cfset ArrayAppend(paths,"C:\ColdFusion11\cfusion\lib\Radix.jar")>			
<cfset loader = createObject("component", "javaloader.JavaLoader").init(loadPaths=paths, loadColdFusionClassPath=true)>
<cfset radix = loader.create("com.intersuite.Radix")>
<cfdump var="#radix#">

<cfset result = radix.convert("FFFF","BASE16","BASE10")>

<cfdump var="#result#">


```
### Attributes
| Attributes| Required| Default|Description|
| --- |:---:|:---|:---|
|VALUE|yes||Character string of base you want to convert from.|
|SOURCEBASE|yes||BASE2 thru BASE32,BASE62,BASE75,BASEALPHA, or arbitrary base string of unique characters that does not start with 'BASE'|
|TARGETBASE|yes||BASE2 thru BASE32,BASE62,BASE75,BASEALPHA, or arbitrary base string of unique characters that does not start with 'BASE'|
|VARIABLE|no|With no variable, tag will output result|Outputs result to variable|
License
----
Copyright (c) 2016, Christopher Wigginton
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of Intersuite.com nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
