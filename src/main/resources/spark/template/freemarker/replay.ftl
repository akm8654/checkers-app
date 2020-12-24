<!DOCTYPE html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
  <meta http-equiv="refresh" content="10">
  <title>Web Checkers | ${title}</title>
  <link rel="stylesheet" type="text/css" href="/css/style.css">
</head>

<body onload="initTheme()">
<div class="page">
  <h1>Web Checkers | ${title}</h1>
    <#include "nav-bar.ftl" />
  <div class="body">
    <h2>You can rewatch a valid game below!</h2>
    <h3>Replay Games: (Listed by highest average win percentage between players)
    </h3>
      <#list replayGames as games>
        <p>${games.getGameID()}. ${games.getRedPlayer().getUsername()} (${games
            .getRedPlayer()
            .getWinPercentage()
            }) v. ${games.getWhitePlayer().getUsername()} (${games
            .getWhitePlayer()
            .getWinPercentage()}):
        <form action="./replay/game" method="GET">
          <button type="submit" name="replayRequest" value=${games
          .getGameID()}>Watch
            Game
          </button>
        </form></p>
      <#else>
          There have not been any games played yet!
      </#list>
  </div>
</div>
</body>
<script data-main="/js" src="/js/initTheme.js"></script>
</html>

