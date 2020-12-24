package com.webcheckers.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import spark.TemplateEngine;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

/**
 * Completes coverage for the UI tier with this application test
 *
 * @author Austin Miller 'akm8654'
 */
@Tag("UI-tier")
public class WebServerTest
{
  //Component Under Test
  WebServer CuT;

  //mocked objects
  TemplateEngine engine;

  @BeforeEach
  public void setup()
  {
    engine = mock(TemplateEngine.class);
    CuT = new WebServer(engine);
  }

  @Test
  public void bad_constructor()
  {
    try {
      CuT = new WebServer(null);
      fail("Constructed with null engine");
    } catch (NullPointerException e) {
      //success
    }
  }

  @Test
  public void initialize()
  {
    CuT.initialize();
  }
}
