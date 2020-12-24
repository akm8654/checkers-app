package com.webcheckers.ui;

import com.webcheckers.util.Message;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * <p>
 * code from GetHomeRoute has been adapted for this, GetSignInRoute
 * @author Mikayla Wishart 'mcw7246'
 */
public class GetSignInRoute implements Route
{
  private static final Logger LOG = Logger.getLogger(GetHomeRoute.class.getName());

  private static final Message SIGNIN_MSG = Message.info("Please sign in before beginning a game.");
  private final TemplateEngine templateEngine;

  /**
   * Create the Spark Route (UI controller) to handle all {@code GET /} HTTP requests.
   *
   * @param templateEngine the HTML template rendering engine
   */
  public GetSignInRoute(final TemplateEngine templateEngine)
  {
    Objects.requireNonNull(templateEngine, "templateEngine is required");
    this.templateEngine = templateEngine;
    LOG.config("GetSignInRoute is initialized.");
  }

  /**
   * Render the WebCheckers Home page.
   *
   * @param request  the HTTP request
   * @param response the HTTP response
   * @return the rendered HTML for the Home page
   */
  @Override
  public Object handle(Request request, Response response)
  {
    LOG.finer("GetSignInRoute is invoked.");
    Map<String, Object> vm = new HashMap<>();

    vm.put("title", "Sign-in");
    // display a user message in the Home page
    vm.put("message", SIGNIN_MSG);

    // render the View
    return templateEngine.render(new ModelAndView(vm, "signin.ftl"));
  }
}
