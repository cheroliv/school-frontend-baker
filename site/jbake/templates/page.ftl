<#include "header.ftl">
<#include "menu.ftl">

<div class="container my-4">
    <#if (content.title)??>
        <div class="row">
            <div class="col-12">
                <div class="border-bottom pb-2 mb-4">
                    <h1><#escape x as x?xml>${content.title}</#escape></h1>
                </div>
            </div>
        </div>
    <#else></#if>

    <#-- <p><em>${content.date?string("dd MMMM yyyy")}</em></p> -->

    <div class="row">
        <div class="col-lg-10">
            <p>${content.body}</p>
        </div>
    </div>

    <hr class="my-5"/>
</div>

<#include "footer.ftl">