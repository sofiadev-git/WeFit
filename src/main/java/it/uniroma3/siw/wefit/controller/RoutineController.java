package it.uniroma3.siw.wefit.controller;

import it.uniroma3.siw.wefit.service.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RoutineController {

    @Autowired
    RoutineService routineService;
}
