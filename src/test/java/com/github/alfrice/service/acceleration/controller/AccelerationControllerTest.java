package com.github.alfrice.service.acceleration.controller;

import org.junit.*;

/**
 * Created by alice.martin
 * Developer: alice.martin
 * Date: 11/13/18
 * Time: 2:07 PM
 * Description: com.github.alfrice.service.acceleration.controller
 */
public class AccelerationControllerTest {
    AccelerationController accelerationController = new AccelerationController();

    @Test
    public void testIndex() throws Exception {
        String result = accelerationController.index();

    }
}

