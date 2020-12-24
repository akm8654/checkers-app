package com.webcheckers.ui;

import com.webcheckers.application.PlayerLobby;
import com.webcheckers.model.Player;
import com.webcheckers.ui.GetSignInRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.*;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * The {@code POST /SignIn} route handler.
 *
 * @author Mikayla Wishart 'mcw7246'
 */
@Tag("UI-tier")
public class PostSignInRouteTest
{
  public static final String INVALID_USERNAME_SHORT = "name1";
  public static final String INVALID_USERNAME_LONG = "abcdefghijklmnopqrstuvwxyz123";
  public static final String TAKEN_USERNAME = "username2";
  public static final String SIGN_IN_ERROR = "%s is already taken. Please try " +
    "another username.";

  /*
   *The component-under-test (CuT)
   */
  private PostSignInRoute CuT;

  /*
   *friendly objects
   */
  private Player player;
  private PlayerLobby playerLobby;

  /*
   *mock objects
   */
  private Request request;
  private Session session;
  private TemplateEngine engine;
  private Response response;

  /**
   * Setup mock calls and associations needed for each test.
   */
  @BeforeEach
  public void setup()
  {
    playerLobby = new PlayerLobby();
    player = new Player(playerLobby);
    response = mock(Response.class);
    request = mock(Request.class);
    session = mock(Session.class);
    when(request.session()).thenReturn(session);
    engine = mock(TemplateEngine.class);



    CuT = new PostSignInRoute(engine, playerLobby);
  }

  /**
   * Ensure that a username which is too short is rejected.
   */
  @Test
  public void testInvalid_username_short(){
    when(request.queryParams(eq(PostSignInRoute.USERNAME_PARAM))).thenReturn(INVALID_USERNAME_SHORT);

    final TemplateEngineTest testHelper = new TemplateEngineTest();
    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    testHelper.assertViewModelIsaMap();

    testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, GetHomeRoute.TITLE);
    assertNotNull(PostSignInRoute.MESSAGE_ATTR);

    testHelper.assertViewName(PostSignInRoute.VIEW_NAME);
  }

  /**
   * Ensure that a username which is too long is rejected.
   */
  @Test
  public void testInvalid_username_long(){
    when(request.queryParams(eq(PostSignInRoute.USERNAME_PARAM))).thenReturn(INVALID_USERNAME_LONG);

    final TemplateEngineTest testHelper = new TemplateEngineTest();
    when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

    CuT.handle(request, response);

    testHelper.assertViewModelExists();
    testHelper.assertViewModelIsaMap();

    assertNotNull(PostSignInRoute.MESSAGE_ATTR);
    testHelper.assertViewModelAttribute(GetHomeRoute.TITLE_ATTR, GetHomeRoute.TITLE);

  }
  /**
   * Ensure that a username which is valid in all regards is accepted.
   */
  @Test
  public void testAvailable_username(){
    when(request.queryParams(eq(PostSignInRoute.USERNAME_PARAM))).thenReturn("testName1");

    final TemplateEngineTest testHelper = new TemplateEngineTest();
  }

  /**
   * Ensure that if the player object is not null, that the user is redirected
   * to the sign in page.
   */
  @Test
  public void test_redirect_halt()
  {
    when(session.attribute(GetHomeRoute.PLAYER_KEY)).thenReturn(player);

    try
    {
      when(request.queryParams(eq(PostSignInRoute.USERNAME_PARAM))).thenReturn(null);
      final TemplateEngineTest testHelper = new TemplateEngineTest();
      when(engine.render(any(ModelAndView.class))).thenAnswer(testHelper.makeAnswer());

      CuT.handle(request, response);
      fail("The player object was null!");

    } catch (HaltException e)
    {
      //Test passed
    }
  }
}
