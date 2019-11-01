package eu.wauz.wazera.controller;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Scope("view")
public class DashboardController implements Serializable {

	private static final long serialVersionUID = -4314266036201867674L;
	
	@PostConstruct
	public void init() {
		
	}
	
	public String ping() {
		return "Connected to Dashboard Controller!";
	}

}
