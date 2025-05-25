		    </div>
		</div>
		<div id="push"></div>
    </div>
    
    <div id="footer">
      <div class="container">
          <b>@brand | <a rel="À propos" href="<#if (content.rootpath)??>${content.rootpath}<#else></#if>about.html">À propos</a> | &copy; 2025 | Licence Creative Commons BY-NC-SA
              <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">
                <img alt="Creative Commons License"
                        style="border-width:0"
                        src="http://i.creativecommons.org/l/by-nc-sa/4.0/88x31.png"/>
          </b>
          <br/>
      </div>
    </div>
    
    <!-- Le javascript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/prettify.js"></script>
    <script src="<#if (content.rootpath)??>${content.rootpath}<#else></#if>js/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
