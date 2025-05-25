<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-light bg-light fixed-top">
    <div class="container">
        <a class="navbar-brand" href="/"><i class="fa-solid fa-house"></i></a>
        <button class="navbar-toggler"
                type="button"
                data-bs-toggle="collapse"
                data-bs-target="#navbarNavDropdown"
                aria-controls="navbarNavDropdown"
                aria-expanded="false"
                aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNavDropdown">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">

            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="mailto:cheroliv.contact@gmail.com" target="_blank"><i class="fa-regular fa-envelope fa-lg"></i></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="https://github.com/cheroliv" target="_blank"><i class="fa-brands fa-github fa-lg"></i></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>${config.feed_file}"><i class="fa fa-rss fa-lg"></i></a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<div class="container pt-5">
<div class="container">