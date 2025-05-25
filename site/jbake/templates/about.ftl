<#include "header.ftl">

<#include "menu.ftl">

<#if (content.title)??>
    <div class="page-header">
        <h1><#escape x as x?xml>${content.title}</#escape></h1>
    </div>
<#else></#if>

    <div class="container my-5">
        <h4>A propos de moi</h4>
    <div class="row">
        <div class="col-md-6">
        <p>https://github.com/cheroliv/school-frontend-baker</p>
        </div>
    </div>
        <hr class="my-4"/>
    </div>
    <p>${content.body}</p>


<#include "footer.ftl">