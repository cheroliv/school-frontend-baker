<#include "header.ftl">
<#include "menu.ftl">

<div class="container my-4">
    <!-- <a href="${content.rootpath}${config.archive_file}">Archives</a> -->
    <p>Les anciens post sont disponibles ici : <a href="${content.rootpath}${config.archive_file}">archive</a>.</p>

    <div class="page-header mb-4">
        <h2>Articles récents</h2>
    </div>

    <div class="row g-4">
        <#list posts as post>
            <#if (post.status == "published")>
                <div class="col-md-7 mb-4">
                    <div class="card border">
                        <div class="card-body">
                            <a href="${post.uri}" class="text-decoration-none">
                                <h3 class="card-title"><#escape x as x?xml>${post.title}</#escape></h3>
                            </a>
                            <p class="text-muted">${post.date?string("dd MMMM yyyy")}</p>
                            <#if post.summary??>
                                <div class="bg-light p-3 rounded">${post.summary}</div>
                            <#else></#if>
                        </div>
                    </div>
                </div>
            </#if>
        </#list>
    </div>

    <hr class="my-5"/>
</div>

<#include "footer.ftl">