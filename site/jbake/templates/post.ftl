<#include "header.ftl">
<#include "menu.ftl">

<#if (content.title)??>
    <div class="mb-4 pb-2 border-bottom">
        <h1 class="display-4"><#escape x as x?xml>${content.title}</#escape></h1>
    </div>
</#if>

<p><em class="text-muted">${content.date?string("dd MMMM yyyy")}</em></p>

<p>${content.body}</p>

<section id="disqus_thread" class="mt-5"></section>

<script type="text/javascript">
    var disqus_identifier = '${content.uri}';

    (({disqus_shortname, document}) => {
        injectScript('//' + disqus_shortname + '.disqus.com/embed.js');
        injectScript('//' + disqus_shortname + '.disqus.com/count.js');

        function injectScript(url) {
            const s = document.createElement('script');
            s.async = true;
            s.src = url;
            (document.head || document.body).appendChild(s);
        }
        //TODO: Create Configuration object with Disqus object nested in it, Disqus object have a disqus_shortname property
    })({'disqus_shortname': `${config.disqus_shortname}`, 'document': document});
</script>

<noscript>
    <div class="alert alert-warning">
        Veuillez activer JavaScript pour voir les
        <a href="http://disqus.com/?ref_noscript" class="alert-link">commentaires propulsés par Disqus</a>.
    </div>
</noscript>

<hr class="my-5" />

<#include "footer.ftl">
