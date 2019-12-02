package eu.wauz.wazera.controller.auth;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.service.DocsTool;

@Controller
@Scope("session")
public class SessionController implements Serializable {
	
	private static final long serialVersionUID = 6410079263923624297L;
	
	private DocsTool docsTool;
	
	public SessionController() {
		docsTool = new DocsTool();
	}
	
	public boolean isChrome() {
		return StringUtils.contains(docsTool.getBrowser(), "Chrome");
	}

}
