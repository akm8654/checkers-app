<div class="navigation">
    <#if currentUser??>
      <a href="/">my home</a> |
      <form id="signout" action="/signout" method="post">
        <a href="#" onclick="event.preventDefault(); signout.submit();">sign out
          [${currentUser}]</a>
      </form> | <form id="changeTheme" action="/changeTheme" method="post">
      <a href="#" onclick="ChangeTheme(); changeTheme.submit();">switch
        theme</a>
    </form>
    <#else>
      <form id="signin" action="/signin" method="post">
        <a href="/signin">sign in</a>
      </form>
        <script>
            console.log("Reset Theme");
            localStorage.removeItem("theme");
            localStorage.clear();
        </script>
    </#if>
  <script data-main="/js" src="/js/ChangeTheme.js"></script>
</div>
