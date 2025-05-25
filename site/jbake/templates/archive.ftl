<#include "header.ftl">
<#include "menu.ftl">

<div class="container my-4">
    <div class="page-header">
        <h2 class="mb-4">Archives du Blog</h2>
    </div>

    <div class="row">
        <div class="col-md-8">
            <#list published_posts as post>
                <#if (last_month)??>
                    <#if post.date?string("MMMM yyyy") != last_month>
                        </ul>
                        <h4 class="mt-4 mb-3">${post.date?string("MMMM yyyy")}</h4>
                        <ul class="list-group list-group-flush">
                    </#if>
                <#else>
                    <h4 class="mb-3">${post.date?string("MMMM yyyy")}</h4>
                    <ul class="list-group list-group-flush">
                </#if>
                    <li class="list-group-item border-0 ps-0">
                        <span class="badge bg-light text-dark me-2">${post.date?string("dd")}</span>
                        <a href="${content.rootpath}${post.uri}" class="text-decoration-none">
                            <#escape x as x?xml>${post.title}</#escape>
                        </a>
                    </li>
                <#assign last_month = post.date?string("MMMM yyyy")>
            </#list>
            </ul>
        </div>
    </div>
</div>

<#include "footer.ftl">